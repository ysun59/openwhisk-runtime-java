#!/usr/bin/python3
import boto3
from concurrent.futures import ThreadPoolExecutor, wait, as_completed
from random import randint
from argparse import ArgumentParser
import time
import requests
import json
import urllib3
import random
import subprocess
import random
import os


urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)
def execute(command):
    p = subprocess.Popen(command, stdout=subprocess.PIPE, shell=True)
    (output, err) = p.communicate()
    p_status = p.wait()
    return output

parser = ArgumentParser()
parser.add_argument("-nt", "--number_of_threads",     type=int, default=15,  help="The number of threads")
parser.add_argument("-ne", "--number_of_experiments", type=int, default=100, help="Total number of requests to server")
parser.add_argument("-m", "--memory", type=int, default=128, help="Memory")
args = parser.parse_args()


def main():
    execute(f'bash deploy.sh {args.memory}')
    if not os.path.exists('tmp'):
        os.makedirs('tmp')

    number_of_threads = args.number_of_threads
    number_of_experiments = args.number_of_experiments

    result_array = [0]*number_of_experiments

    lambda_client = boto3.client('lambda')

    def run_request(index):
        start = time.time()
        response = lambda_client.invoke(
            FunctionName='sleep-jvm',
            InvocationType='RequestResponse',
        )
        #execute(f'bash invoke.sh tmp/{index}.txt')
        end = time.time()
        et = round((end-start)*1000,3)
        print(f"{et}ms")
        data = response['Payload'].read().decode('utf-8')
        # with open(f'tmp/{index}.txt', 'r') as file:
        #     data = file.read().replace('\n', '')
        isSlowStart = 0 if ' 0\"' in data else 2
        if isSlowStart != 0:
            isSlowStart = 1 if ' 1\"' in data else 2
        return (et, isSlowStart)

    pool = ThreadPoolExecutor(max_workers=number_of_threads)

    all_start = time.time()

    futures = [pool.submit(run_request,i) for i in range(number_of_experiments)]

    results = [r.result() for r in as_completed(futures)]

    all_end = time.time()

    print(results)
    with open(f'data/1_{args.number_of_threads}.txt', 'w') as f:
        f.write(','.join([str(x[0]) for x in results if x[1]!= 2]))
    with open(f'data/1_{args.number_of_threads}_ss.txt', 'w') as f:
        f.write(','.join([str(x[1]) for x in results if x[1]!= 2]))

    total_execution_time = round((all_end-all_start)*1000,3)
    print("Experiment took", total_execution_time, "ms")

if __name__ == '__main__':
    main()