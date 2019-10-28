#!/usr/bin/python3

from concurrent.futures import ThreadPoolExecutor, wait, as_completed
from random import randint
from argparse import ArgumentParser
import invoke
import time
import statistics

parser = ArgumentParser()
parser.add_argument("-nt", "--number_of_threads",     type=int, default=15,  help="The number of threads")
parser.add_argument("-ne", "--number_of_experiments", type=int, default=100, help="Total number of requests to server")
parser.add_argument("-nw", "--number_of_warmups", type=int, default=1000, help="Total number of warmup requests to server")
parser.add_argument("-d", "--debug", type=bool, default=False, help="Total number of warmup requests to server")
args = parser.parse_args()

number_of_threads = args.number_of_threads
number_of_experiments = args.number_of_experiments
number_of_warmups = args.number_of_warmups
debug = args.debug

warmup_result_array      = [0]*(number_of_warmups)
experiments_result_array = [0]*(number_of_experiments)
warmup_start_time = 0
warmup_finish_time = 0
experiments_start_time = 0
experiments_finish_time = 0

def run_request(index):
    #value = invoke.processPayload('{"seed":"%s"}' % index)
    value = invoke.processPayload('{"index":"%s"}' % '0')
    args.host=invoke.dockerHost()
    args.port=8080
    url = invoke.containerRoute(args, 'run')
    start = time.time()
    r = invoke.requests.post(url, json = {"value": value})
    end = time.time()
    latency = round((end-start)*1000,3)
    if debug:
        print(value, " -> ", r.text, " -> ", latency, "ms")
    return (index, latency, end)

all_start = time.time()

with ThreadPoolExecutor(max_workers=number_of_threads) as pool:

    warmup_start_time = time.time()

    futures = [pool.submit(run_request,i) for i in range(number_of_warmups)]
    for future in as_completed(futures):
        result = future.result()
        warmup_result_array[result[0]] = result[1], round((result[2] - warmup_start_time) * 1000, 3)

    warmup_finish_time = experiments_start_time = time.time()

    futures = [pool.submit(run_request,i) for i in range(number_of_experiments)]
    for future in as_completed(futures):
        result = future.result()
        experiments_result_array[result[0]] = result[1]
    experiments_finish_time = time.time()

warmup_time_ms = round((warmup_finish_time - warmup_start_time) * 1000, 3)
experiments_time_ms = round((experiments_finish_time - experiments_start_time) * 1000, 3)
avg_latency = statistics.mean(experiments_result_array)
std_latency = statistics.stdev(experiments_result_array)
ops_sec = number_of_experiments * 1000 / experiments_time_ms

for warmup_result in warmup_result_array:
    print("#", warmup_result[0], warmup_result[1])

print("Warmup took (ms):", warmup_time_ms)
print("Experiments total time (ms): ", experiments_time_ms, "ms")
print("Experiments avg latency (ms):", avg_latency)
print("Experiments std latency:", std_latency)
print("Experiments ops/s", ops_sec)
