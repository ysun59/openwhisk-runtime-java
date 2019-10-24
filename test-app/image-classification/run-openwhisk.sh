#!/bin/bash

# wsk action --result  -i invoke FileHashing  --param seed 1234

wsk --apihost https://10.1.212.71 --auth 23bc46b1-71f6-4ed5-8c54-816aa4f8c502:123zO3xZCLrMN6v2BKK1dXYFpXlPkccOFqm12CdAsMgRU4VrNZ9lyGVCGuMDGIwP action --result  -i invoke Sleep  --param time 1000
