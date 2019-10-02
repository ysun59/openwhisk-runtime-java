#!/usr/bin/python

import datetime
from concurrent.futures import ThreadPoolExecutor, wait, as_completed
from random import randint
from argparse import ArgumentParser
import invoke

parser = ArgumentParser()
parser.add_argument("-nt", "--number_of_threads",     type=int, default=15,  help="The number of threads")
parser.add_argument("-ne", "--number_of_experiments", type=int, default=100, help="Total number of requests to server")
args = parser.parse_args()

number_of_threads = args.number_of_threads
number_of_experiments = args.number_of_experiments

result_array = [0]*number_of_experiments

def run_request(index):
    #value = invoke.processPayload('{"seed":"%s"}' % (index))
    value = invoke.processPayload('{"seed":"1"}')
    print(value)
    args.host=invoke.dockerHost()
    args.port=8080
    url = invoke.containerRoute(args, 'run')
    print(url)
    r = invoke.requests.post(url, json = {"value": value})
    print(r.text)

pool = ThreadPoolExecutor(max_workers=number_of_threads)

futures = [pool.submit(run_request,i) for i in range(number_of_experiments)]

results = [r.result() for r in as_completed(futures)]
