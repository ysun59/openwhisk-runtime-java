import os
import sys
import re
import subprocess

skeleton_path = '/home/voja/Development/work/tf_lambda_skeleton/'

#currentPath = os.path.dirname(os.path.realpath(__file__))+'/'

def execute(command):
    p = subprocess.Popen(command, stdout=subprocess.PIPE, shell=True)
    (output, err) = p.communicate()
    p_status = p.wait()
    return output

#execute
output = execute('cd %s && serverless invoke --function main --log -d val_%s.JPEG'%(skeleton_path, sys.argv[1]))
print output
#status
resStatus = 1 if '"statusCode": 200' in output else 0


#REPORT RequestId: b417ec29-dac1-11e8-8c38-61bd2f19044e  Duration: 838.93 ms Billed Duration: 900 ms     Memory Size: 2944 MB    Max Memory Used: 672 MB
resDuration = re.search(r'Duration: (\d+\.?\d*) ms', output).group(1)
resBilledDuration = re.search(r'Billed Duration: (\d+\.?\d*) ms', output).group(1)
resMemoryUsed = re.search(r'Max Memory Used: (\d+\.?\d*) MB', output).group(1)
resMemoryAllocated = re.search(r'Memory Size: (\d+\.?\d*) MB', output).group(1)
resInnerDur = re.search(r'\"etime\": (\d+\.?\d*)', output).group(1)

utilization = re.search(r'\"utilization\": (\d+\.?\d*)', output).group(1)
cycles = re.search(r'\"cycles\": (\d+\.?\d*)', output).group(1)
download_size = re.search(r'\"download_size\": (\d+\.?\d*)', output).group(1)
upload_size = re.search(r'\"upload_size\": (\d+\.?\d*)', output).group(1)

print 'status:'+str(resStatus)
print 'duration:'+str(resDuration)
print 'innerdur:'+str(int(float(resInnerDur)*1000))
print 'billed_duration:'+str(resBilledDuration)
print 'memory_used:'+str(resMemoryUsed)
print 'memory_allocated:'+str(resMemoryAllocated)
print 'utilization:'+str(utilization)
print 'cycles:'+str(cycles)
print 'download_size:'+str(download_size)
print 'upload_size:'+str(upload_size)

