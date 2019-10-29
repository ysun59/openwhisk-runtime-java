import os
import sys
import re

def rewrite_config(memory):
    conf_txt = ''
    with open('serverless.yml') as f:
        for line in f:
            conf_txt += line

    replaced_conf = re.sub(r'memorySize: \d+', 'memorySize: '+memory, conf_txt)

    with open('serverless.yml','w') as f:
        f.write(replaced_conf)

if len(sys.argv) == 2:
    rewrite_config(sys.argv[1])

skeleton_path = '/home/voja/Development/work/tf_lambda_skeleton/'



#copy everything to the skeleton
os.system('cp -r . %s'%(skeleton_path,))
#redeploy
os.system('cd %s && serverless deploy'%(skeleton_path,))
