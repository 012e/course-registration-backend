import http from "k6/http";
import {sleep, check} from "k6";
import {randomIntBetween} from "https://jslib.k6.io/k6-utils/1.0.0/index.js";

const url = "http://app:8080";
const infinite = "1000000h";

export const options = {
    scenarios: {
        testing: {
            executor: "externally-controlled",
            duration: infinite,
            maxVus: 1000,
        },
    },
};

function generateRandomString(minLength, maxLength) {
    // Define the characters to choose from (alphabets and numbers)
    const characters =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    // Generate a random length between minLength and maxLength
    const length =
        Math.floor(Math.random() * (maxLength - minLength + 1)) + minLength;

    // Initialize the result string
    let result = "";

    // Loop to generate a string of the chosen length
    for (let i = 0; i < length; i++) {
        const randomIndex = Math.floor(Math.random() * characters.length);
        result += characters[randomIndex];
    }

    return result;
}

const jsonParam = {
    headers: {
        "Content-Type": "application/json",
    },
};

function randomFloatBetween(min, max) {
    return Math.random() * (max - min) + min;
}

function waitRandomly() {
    sleep(randomFloatBetween(0.1, 1));
}

export default function () {
    const username = generateRandomString(5, 10);
    const password = generateRandomString(10, 20);

    const loginData = JSON.stringify({
        username: username,
        password: password,
    });

    const registerData = JSON.stringify({
        firstName: "a",
        lastName: "b",
        username: username,
        password: password,
    });

    let registerResult = http.post(
        `${url}/auth/register`,
        registerData,
        jsonParam,
    );
    check(registerResult, {
        "register response is 200": (r) => r.status === 200,
    });
    waitRandomly();

    let loginResult = http.post(`${url}/auth/login`, loginData, jsonParam);
    check(loginResult, {
        "login response is 200": (r) => r.status === 200,
    });
    waitRandomly();

    const totalSecretRequests = randomIntBetween(1, 30);
    for (let i = 0; i < totalSecretRequests; i++) {
        let secretResult = http.get(`${url}/secret`);
        check(secretResult, {
            "secret response is 200": (r) => r.status === 200,
            "secret data is correct": (r) =>
                r.body ===
                JSON.stringify({success: true, message: "Secret data", data: null}),
        });
        waitRandomly();
    }
}
