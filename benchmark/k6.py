import logging as log
from typing import TypedDict

import requests


class Attributes(TypedDict):
    status: int
    paused: bool
    vus: int
    vus_max: int
    stopped: bool
    running: bool
    tainted: bool


class Data(TypedDict):
    type: str
    id: str
    attributes: Attributes


class StatusResponse(TypedDict):
    data: Data


JSON_HEADERS = {"Content-Type": "application/json"}

def delete_none(_dict):
    """Delete None values recursively from all of the dictionaries"""
    for key, value in list(_dict.items()):
        if isinstance(value, dict):
            delete_none(value)
        elif value is None:
            del _dict[key]
        elif isinstance(value, list):
            for v_i in value:
                if isinstance(v_i, dict):
                    delete_none(v_i)

    return _dict



class K6Controller:
    address: str

    def __init__(self, address: str = "http://localhost:6565"):
        self.address = address

    def get_status(self) -> StatusResponse:
        response = requests.get(f"{self.address}/v1/status", headers=JSON_HEADERS)

        return response.json()

    def update_status(
        self, vus: int | None = None, stopped: bool | None = None, **kwargs
    ) -> None:
        body = {
            "data": {
                "type": "status",
                "id": "default",
                "attributes": {"vus": vus, "stopped": stopped, **kwargs},
            }
        }
        body = delete_none(body)

        response = requests.patch(
            f"{self.address}/v1/status", headers=JSON_HEADERS, json=body
        )

        if response.status_code != 200:
            raise Exception("Failed to update status")

    def list_metrics(self):
        response = requests.get(f"{self.address}/v1/metrics", headers=JSON_HEADERS)
        return response.json()

    def get_metrics(self, id: str):
        response = requests.get(f"{self.address}/v1/metrics/{id}", headers=JSON_HEADERS)
        return response.json()

    def pause(self) -> None:
        log.debug("pausing")
        self.update_status(paused=True)

    def resume(self) -> None:
        log.debug("resuming")
        self.update_status(paused=False)

    def stop(self) -> None:
        log.debug("stopping")
        self.update_status(stopped=True)

    def scale(self, vus: int) -> None:
        log.debug(f"Scaling to {vus} vus")
        self.update_status(vus)


if __name__ == "__main__":
    con = K6Controller()
    print(con.get_status())
    con.scale(vus=10)
    print(con.get_status())
