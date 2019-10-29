import os
import sys
import re
import subprocess

docker_image_name = 'tf-object-detection'
#mib_const = int(1.048576*(10**6))
mib_const = 10**6

def execute(command):
    p = subprocess.Popen(command, stdout=subprocess.PIPE, shell=True)
    (output, err) = p.communicate()
    p_status = p.wait()
    return output

#currentPath = os.path.dirname(os.path.realpath(__file__))+'/'

default_file = '0'
if len(sys.argv)>1:
    default_file = sys.argv[1]
cpu = '0.5'
if len(sys.argv)>2:
    cpu = sys.argv[2]
memory = 512
if len(sys.argv)>3:
    memory = int(sys.argv[3])
b_memory = memory * mib_const
b_memory = str(b_memory)

if len(sys.argv)<4:
    #stop all docker
    os.system('docker rm $(docker ps -a -q) > /dev/null 2>&1')

command = 'docker run --cpus="%s" --memory="%s" %s val_%s.JPEG 2>&1'%(cpu, b_memory, docker_image_name, default_file)
#execute
output = execute(command)
#print output
#status
resStatus = 1 if '\'statusCode\': 200' in output else 0

resInnerDur = re.search(r'\'etime\': (\d+\.?\d*)', output).group(1)
# resBilledDuration = re.search(r'Billed Duration: (\d+\.?\d*) ms', output).group(1)
resMemoryUsed = re.search(r'MMM:(\d+)', output).group(1)
resMemoryUsed = int(resMemoryUsed)/1000
# resMemoryAllocated = re.search(r'Memory Size: (\d+\.?\d*) MB', output).group(1)

print 'status:'+str(resStatus)
print 'innerdur:'+str(int(float(resInnerDur)*1000))
# print 'duration:'+str(resDuration)
# print 'billed_duration:'+str(resBilledDuration)
# print 'memory_used:'+str(resMemoryUsed)
# print 'memory_allocated:'+str(resMemoryAllocated)
print 'memory_used:'+str(resMemoryUsed)
print 'memory_allocated:'+str(memory)


