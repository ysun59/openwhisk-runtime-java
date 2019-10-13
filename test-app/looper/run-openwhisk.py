#!/usr/bin/python3

import requests
import json
import urllib3

urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

# This script is inspired by hittps://github.com/wsadminlib/openwhisklib/blob/master/src/openwhisklib.py

# Got this by invoking openwhisk with verbose
url='https://129.132.102.71/api/v1/namespaces/_/actions/Looper?blocking=true&result=true'
headers = {
  "Authorization": "Basic MjNiYzQ2YjEtNzFmNi00ZWQ1LThjNTQtODE2YWE0ZjhjNTAyOjEyM3pPM3haQ0xyTU42djJCS0sxZFhZRnBYbFBrY2NPRnFtMTJDZEFzTWdSVTRWck5aOWx5R1ZDR3VNREdJd1A=",
  "Content-Type": "application/json",
  "User-Agent": "OpenWhisk-CLI/1.0 (2019-08-10T00:47:48.313+0000) linux amd64"
}


payload = { 'seed' : 1234 }
response = requests.post(url, data=json.dumps(payload), headers=headers, verify=False)

print(response.text)
