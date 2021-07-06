#!/usr/bin/python3

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



urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)
def execute(command):
    p = subprocess.Popen(command, stdout=subprocess.PIPE, shell=True)
    (output, err) = p.communicate()
    p_status = p.wait()
    return output

ip_address = '172.17.0.1:3233'
#ip_address = '10.1.212.71'
#ip_address = '129.132.102.71'
url='http://%s/api/v1/namespaces/_/actions/%s?blocking=true&result=true'
#url='https://%s/api/v1/namespaces/_/actions/%s?blocking=true&result=true'
headers = {
    "Authorization": "Basic MjNiYzQ2YjEtNzFmNi00ZWQ1LThjNTQtODE2YWE0ZjhjNTAyOjEyM3pPM3haQ0xyTU42djJCS0sxZFhZRnBYbFBrY2NPRnFtMTJDZEFzTWdSVTRWck5aOWx5R1ZDR3VNREdJd1A=",
    "Content-Type": "application/json",
    "User-Agent": "OpenWhisk-CLI/1.0 (2019-08-10T00:47:48.313+0000) linux amd64"
}

parser = ArgumentParser()
parser.add_argument("-nt", "--number_of_threads",     type=int, default=5,  help="The number of threads")
#parser.add_argument("-nt", "--number_of_threads",     type=int, default=15,  help="The number of threads")
parser.add_argument("-ne", "--number_of_experiments", type=int, default=1, help="Total number of requests to server")
#parser.add_argument("-ne", "--number_of_experiments", type=int, default=100, help="Total number of requests to server")
parser.add_argument("-wl", "--workload", type=str, default='Sleep', help="Workload name")
parser.add_argument("-wl2", "--workload2", type=str, default='Sleep2', help="Workload2 name")
parser.add_argument("-c", "--concurrency", type=int, default=4, help="The number of threads")
#parser.add_argument("-c", "--concurrency", type=int, default=1, help="The number of threads")
parser.add_argument("-f", "--frequency", type=int, default=12, help="Frequency of shifting workloads")
parser.add_argument("-m", "--memory", type=int, default=256, help="Memory")
args = parser.parse_args()


def deploy_functions():
    deploy_command = 'wsk --apihost http://%s action update -i %s ./target/sleep.jar --main ch.ethz.systems.Sleep --docker ysun59/java8action -c %s'
#   deploy_command = 'wsk --apihost https://%s action update -i %s ./target/sleep.jar --main ch.ethz.systems.Sleep --docker ysun59/java8action -c %s'
#   deploy_command = 'wsk --apihost https://%s action update -i %s ./target/sleep.jar --main Sleep --docker ysun59/java8action -c %s -m %s -t 300000'
#   deploy_command = 'wsk --apihost https://%s --auth 23bc46b1-71f6-4ed5-8c54-816aa4f8c502:123zO3xZCLrMN6v2BKK1dXYFpXlPkccOFqm12CdAsMgRU4VrNZ9lyGVCGuMDGIwP action update -i %s sleep.jar --main Sleep --docker rfbpb/java8action -c %s -m %s -t 300000'
    memory = int(args.concurrency)*int(args.memory)
    dc = deploy_command%(ip_address, args.workload, str(args.concurrency))
    print(dc)
#   dc = 'wsk --apihost http://172.17.0.1:3233 action update -i Sleep ./target/sleep.jar --main ch.ethz.systems.Sleep --docker ysun59/java8action -c 1'
#   print(dc)
#   dc = deploy_command%(ip_address, args.workload, str(args.concurrency), str(memory))
    execute(dc)

    #deploy constant workload
    #dc = deploy_command%(ip_address, args.workload2, '1')
    dc = deploy_command%(ip_address, args.workload2, str(args.concurrency))
#   dc = deploy_command%(ip_address, args.workload2, str(args.concurrency), str(args.memory))
    print(dc)
    execute(dc)


def main():
    deploy_functions()

    number_of_threads = args.number_of_threads
    number_of_experiments = args.number_of_experiments

    result_array = [0]*number_of_experiments

    def run_request(index):
        time.sleep(1./random.randint(1, 1000))
        payload = { 'time' : 1000 }

        start = time.time()

        workload = ''
        alterante_frequency = args.frequency
        print(start%alterante_frequency)
        print(alterante_frequency/2)
        if (start%alterante_frequency) < (alterante_frequency/2):
            workload = args.workload
        else:
            workload = args.workload2

#       workload = 'Sleep'
        request_url = url%(ip_address, workload)
        print(request_url)
#       request_url = 'http://172.17.0.1:3233/api/v1/namespaces/_/actions/Sleep?blocking=true&result=true'
#       print(request_url)

        r = requests.post(request_url, data=json.dumps(payload), headers=headers, verify=False)
        print(r.json())
        end = time.time()
        et = round((end-start)*1000,3)
        print(f"{payload} -> {r.text} -> {et}ms")

        print('slow_start\":\"0')
        isSlowStart = 0 if 'slow_start\":\"0' in r.text else 1
        # isSlowStart = 0 if 'slow_start\":\"0' in r.text else 2
        # if isSlowStart != 0:
        #     isSlowStart = 1 if 'slow_start\":\"1' in r.text else 2
        return (et, isSlowStart)

    pool = ThreadPoolExecutor(max_workers=number_of_threads)

    all_start = time.time()

    futures = [pool.submit(run_request,i) for i in range(number_of_experiments)]

    results = [r.result() for r in as_completed(futures)]

    all_end = time.time()

    print(results)
    with open(f'data/{args.concurrency}_{args.number_of_threads}.txt', 'w') as f:
        f.write(','.join([str(x[0]) for x in results if x[1]!= 2]))
    with open(f'data/{args.concurrency}_{args.number_of_threads}_ss.txt', 'w') as f:
        f.write(','.join([str(x[1]) for x in results if x[1]!= 2]))

    total_execution_time = round((all_end-all_start)*1000,3)
    #print("Experiment took", , "ms")


if __name__ == '__main__':
    main()
