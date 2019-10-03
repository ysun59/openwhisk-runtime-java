#!/usr/bin/python3

from concurrent.futures import ThreadPoolExecutor, wait, as_completed
from random import randint
from argparse import ArgumentParser
import time
import requests
import json
import urllib3

urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

url='https://129.132.102.71/api/v1/namespaces/_/actions/Sort?blocking=true&result=true'
headers = {
  "Authorization": "Basic MjNiYzQ2YjEtNzFmNi00ZWQ1LThjNTQtODE2YWE0ZjhjNTAyOjEyM3pPM3haQ0xyTU42djJCS0sxZFhZRnBYbFBrY2NPRnFtMTJDZEFzTWdSVTRWck5aOWx5R1ZDR3VNREdJd1A=",
  "Content-Type": "application/json",
  "User-Agent": "OpenWhisk-CLI/1.0 (2019-08-10T00:47:48.313+0000) linux amd64"
}

parser = ArgumentParser()
parser.add_argument("-nt", "--number_of_threads",     type=int, default=15,  help="The number of threads")
parser.add_argument("-ne", "--number_of_experiments", type=int, default=100, help="Total number of requests to server")
args = parser.parse_args()

number_of_threads = args.number_of_threads
number_of_experiments = args.number_of_experiments

result_array = [0]*number_of_experiments

def run_request(index):
    payload = { 'seed' : index }

    print(payload, end=" -> ")
    start = time.time()
    r = requests.post(url, data=json.dumps(payload), headers=headers, verify=False)
    end = time.time()
    print(r.text, end=" -> ")
    print(round((end-start)*1000,3), "ms")

pool = ThreadPoolExecutor(max_workers=number_of_threads)

all_start = time.time()

futures = [pool.submit(run_request,i) for i in range(number_of_experiments)]

results = [r.result() for r in as_completed(futures)]

all_end = time.time()

print("Experiment took", round((all_end-all_start)*1000,3), "ms")
