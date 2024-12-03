import argparse
import json
import logging as log
import signal
import sys
import time
from datetime import timedelta
from time import sleep

from k6 import K6Controller
from utils import parse_duration

log.basicConfig(stream=sys.stderr, level=log.DEBUG)

controller = K6Controller()


def exit_handler(x, y):
    metrics = controller.list_metrics()
    controller.stop()
    data = metrics["data"]

    req_duration = get_metrics(data, "http_req_duration")
    p95 = get_sample(req_duration, "p(95)")
    p90 = get_sample(req_duration, "p(90)")
    avg = get_sample(req_duration, "avg")

    print("request duration metrics")
    print(f"p95: {p95}")
    print(f"p90: {p90}")
    print(f"avg: {avg}")
    sys.exit(0)


def pretty_dict(d):
    return json.dumps(d, indent=4)


class Config:
    high_load_vus: int
    low_load_vus: int
    rest: timedelta
    duration: timedelta
    constant: bool

    def __init__(self, high_load_vus, low_load_vus, rest, duration, constant=False):
        self.high_load_vus = high_load_vus
        self.low_load_vus = low_load_vus
        self.rest = rest
        self.duration = duration
        self.constant = constant

    def __str__(self):
        return f"""
high load vus: {self.high_load_vus}vus
low load vus: {self.low_load_vus}vus
rest: {self.rest}
duration: {self.duration}
will run at constant vus: {self.constant}
"""


def parse_arg(args) -> Config:
    parser = argparse.ArgumentParser(description="Benchmarking tool for k6")

    parser.add_argument(
        "-d", "--duration", type=str, help="Time to run the benchmark", default="60s"
    )
    parser.add_argument(
        "--low", type=int, help="Number of mimimum virtual users", default=0
    )
    parser.add_argument(
        "--high", type=int, help="Number of maxiumum virtual users", default=100
    )
    parser.add_argument(
        "--rest",
        type=str,
        help="Time to rest between scaling operations",
        default="10s",
    )
    parser.add_argument("--feature", action=argparse.BooleanOptionalAction)
    parser.add_argument(
        "--constant",
        help="Number of constant virtual users",
        action=argparse.BooleanOptionalAction,
        default=True,
    )

    ns = parser.parse_args(sys.argv[1:])
    duration = ns.duration
    high_load_vus = ns.high
    low_load_vus = ns.low
    rest = ns.rest
    constant = ns.constant

    try:
        duration = parse_duration(duration)
        rest = parse_duration(rest)
    except ValueError as e:
        log.error(e)
        sys.exit(1)

    try:
        if high_load_vus < 0 or low_load_vus < 0:
            raise ValueError("Number of virtual users must be positive")
        elif high_load_vus < low_load_vus:
            raise ValueError("High load must be greater than low load")
    except ValueError as e:
        log.error(e)
        sys.exit(1)

    return Config(high_load_vus, low_load_vus, rest, duration, constant)


def get_metrics(data, id: str):
    return [item for item in data if item["type"] == "metrics" and item["id"] == id][0]


def get_sample(data, name: str):
    return data["attributes"]["sample"][name]


def run_constant_vus(controller, config):
    log.info(
        f"Running constant vus for {config.duration} at {config.high_load_vus} vus"
    )
    controller.scale(config.high_load_vus)
    sleep(config.duration.total_seconds())
    controller.scale(0)
    exit_handler(None, None)


def run_burst_vus(controller, config):
    log.info(f"Running burst vus for {config.duration}")
    begin_time = time.time()
    while True:
        total_time = timedelta(seconds=time.time() - begin_time)
        if total_time >= config.duration:
            exit_handler(None, None)
        else:
            log.info(f"Time left: {config.duration - total_time}")

        controller.scale(config.high_load_vus)
        log.info(f"Scaling to high load: {config.high_load_vus} vus")
        sleep(config.rest.total_seconds())
        controller.scale(config.low_load_vus)
        log.info(f"Scaling to low load: {config.low_load_vus} vus")
        sleep(config.rest.total_seconds())


def main():
    config = parse_arg(sys.argv[:1])

    signal.signal(signal.SIGINT, exit_handler)

    log.info(f"Starting benchmark with the following config:, {config}")

    if config.constant:
        run_constant_vus(controller, config)
    else:
        run_burst_vus(controller, config)


if __name__ == "__main__":
    main()
