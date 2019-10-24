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



def main():

    def run_request(index):
        print(index)
        time.sleep(5.)
        return index

    pool = ThreadPoolExecutor(max_workers=10)

    futures = [pool.submit(run_request,i) for i in range(100)]

    results = [r.result() for r in as_completed(futures)]

    print(results)
    

if __name__ == '__main__':
    main()