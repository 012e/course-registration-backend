import http from "k6/http";
import {check, sleep} from "k6";
import {randomIntBetween} from "https://jslib.k6.io/k6-utils/1.0.0/index.js";

const URL = "http://app:8080";
const INFINITE = "1000000h";

const MIN_COURSES = 1;
const MAX_COURSES = 100;
const TOTAL_REGISTERED_COURSES = 10;

const JSON_PARAMS = {
  headers: {
    "Content-Type": "application/json",
  },
};

export const options = {
  scenarios: {
    testing: {
      executor: "externally-controlled",
      duration: INFINITE,
      maxVus: 1000,
    },
  },
};

/*
  * Generate n distinct random numbers between low and high
  * using the shifted exponential distribution.
  With low = 1, high = 100 and n = 10, the function will generate approximately about 5 numbers under 10
*/
function getRandomNumbersShiftedExponential(low, high, n, lambda = 10) {
  if (n > high - low + 1) {
    throw new Error("Cannot pick more distinct numbers than the range allows.");
  }

  const randomExponential = () => {
    return -Math.log(1 - Math.random()) / lambda; // Generate exponential random variable
  };

  const numbers = new Set();
  while (numbers.size < n) {
    let num = low + Math.floor(randomExponential() * (high - low));
    if (num <= high) {
      numbers.add(num);
    }
  }

  return Array.from(numbers);
}

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

function randomFloatBetween(min, max) {
  return Math.random() * (max - min) + min;
}

function waitRandomly() {
  sleep(randomFloatBetween(0.1, 1));
}

function register(data) {
  let registerResult = http.post(`${URL}/auth/register`, data, JSON_PARAMS);
  check(registerResult, {
    "register response is 200": (r) => r.status === 200,
    "response is success": (r) => JSON.parse(r.body).success === true,
  });
}

function login(loginData) {
  let loginResult = http.post(`${URL}/auth/login`, loginData, JSON_PARAMS);
  check(loginResult, {
    "login response is 200": (r) => r.status === 200,
  });
}

function registerRandomCourses() {
  const subjectIds = getRandomNumbersShiftedExponential(
      MIN_COURSES,
      MAX_COURSES,
      TOTAL_REGISTERED_COURSES,
  );
  const payload = JSON.stringify({courseIds: subjectIds});
  let result = http.post(`${URL}/registration`, payload, JSON_PARAMS);
  check(result, {
    "course registration response is 200": (r) => r.status === 200,
    "course registration response is success": (r) =>
        JSON.parse(r.body).success === true,
  });
}

function getAuthInfo() {
  const username = "user" + randomIntBetween(1, 1000);
  const password = "password";

  return {
    username: username,
    password: password,
  };
}

function checkRegisteredCourses() {
  const result = http.get(`${URL}/registration/registered`, JSON_PARAMS);
  check(result, {
    "registered courses response is 200": (r) => r.status === 200,
  });
}

export default function () {
  const authInfo = getAuthInfo();

  const loginData = JSON.stringify(authInfo);

  login(loginData);
  waitRandomly();

  const totalTries = randomIntBetween(1, 10);
  for (let i = 0; i < totalTries; i++) {
    registerRandomCourses();
    waitRandomly();

    const checkCourses = randomIntBetween(1, 5);
    for (let j = 0; j < checkCourses; j++) {
      checkRegisteredCourses();
      waitRandomly();
    }
  }
}
