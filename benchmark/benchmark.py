import json
import logging as log
import sys
from time import sleep

from k6 import K6Controller

log.basicConfig(stream=sys.stdout, level=log.INFO)


def pretty_dict(d):
    return json.dumps(d, indent=4)


controller = K6Controller()


HIGH_LOAD_VUS = 100
LOW_LOAD_VUS = 0

while True:
    controller.scale(HIGH_LOAD_VUS)
    log.info(f"Scaling to high load: {HIGH_LOAD_VUS} vus")
    sleep(10)
    controller.scale(LOW_LOAD_VUS)
    log.info(f"Scaling to high load: {LOW_LOAD_VUS} vus")
    sleep(10)
