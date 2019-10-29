#!/bin/bash

# aws lambda invoke --function-name sleep-jvm \
# --payload '{"input": "from the client"}' response.json

aws lambda invoke --function-name tensorflow-jvm response.json \
--payload '{"input": "from the client"}' --output text --query 'LogResult' --log-type Tail |  base64 -d