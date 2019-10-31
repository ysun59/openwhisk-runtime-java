from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import subprocess
import numpy as np
import tensorflow as tf
# import keras

import argparse
import os
import os.path
import re
import sys
import urllib
import json
import traceback
import logging
import sys
import time
import PIL.Image as Image
import numpy as np

download = None

#import only on aws lambda
if __name__ != "__main__":
    import boto3
    def downloadFromS3(strBucket,strKey,strFile):
        s3_client = boto3.client('s3')
        s3_client.download_file(strBucket, strKey, strFile)
    download = downloadFromS3


SESSION = None


local_dir = '/tmp/imagenet/'
local_bucket_dir = '/tmp/hpybucket/'

modelFiles = [
    'tensorflow_inception_graph.pb'
    # 'imagenet_synset_to_human_label_map.txt',
    # 'imagenet_2012_challenge_label_map_proto.pbtxt',
    # 'classify_image_graph_def.pb'
]

def getStats():
    with open('/proc/self/stat', 'r') as f:
        data = f.read()
        return data

def cycles(start_time, start_stats, end_time, end_stats):
    start = start_stats.split(' ')
    end = end_stats.split(' ')

    tck = (os.sysconf(os.sysconf_names['SC_CLK_TCK']))

    exec_time = end_time-start_time
    # total_time_start = float(start[13])+float(start[14])+float(start[15])+float(start[16])
    # total_time_end = float(end[13])+float(end[14])+float(end[15])+float(end[16])
    total_time_start = float(start[13])+float(start[15])
    total_time_end = float(end[13])+float(end[15])
    return (total_time_end-total_time_start)*1000./tck

def downloadFromLocal(strBucket,strKey,strFile):
    os.system('cp %s %s'%(local_bucket_dir+strKey,strFile))


def get_memory():
    with open('/proc/meminfo') as f:
        meminfo = f.read()
    matched = re.search(r'MemTotal:\s+(\d+)', meminfo)
    if matched: 
        mem_total_kB = int(matched.groups()[0])
    matched = re.search(r'MemAvailable:\s+(\d+)', meminfo)
    if matched: 
        mem_available_kB = int(matched.groups()[0])
    return (mem_total_kB-mem_available_kB)/1000.


def create_graph():
    #with tf.gfile.FastGFile(os.path.join(local_dir, 'classify_image_graph_def.pb'), 'rb') as f:
    with tf.gfile.FastGFile(os.path.join(local_dir, 'tensorflow_inception_graph.pb'), 'rb') as f:
        graph_def = tf.GraphDef()
        graph_def.ParseFromString(f.read())
        _ = tf.import_graph_def(graph_def, name='')


def run_inference_on_image(image):
    global SESSION
    # if not tf.gfile.Exists(image):
    #     tf.logging.fatal('File does not exist %s', image)
    # image_data = tf.gfile.FastGFile(image, 'rb').read()
    image_data =  Image.open(image).resize((224,224))

    if SESSION is None:
        SESSION = tf.InteractiveSession()
        create_graph()
    softmax_tensor = tf.get_default_graph().get_tensor_by_name('softmax0:0')
    mem2 = get_memory()
    predictions = SESSION.run(softmax_tensor, {'input:0': np.expand_dims(image_data, axis=0)})
    
    return 'lion', mem2

def handler(event, context):
    print(tf.__version__)
    mem1 = get_memory()
    st = int(round(time.time() * 1000))
    start_stats = getStats()

    start_time = time.time()
    if not os.path.exists(local_dir):
        os.makedirs(local_dir)

    strBucket = 'hpyindex'

    for f in modelFiles:
        strFile = local_dir + f
        if not os.path.exists(strFile):
            #strKey = 'tensorflow_model/'+f
            strKey = 'bmk/'+f
            download(strBucket,strKey,strFile)


    fn = 'lion.jpg'
    # if isinstance(event, basestring):
    #     fn = event
    strKey = 'bmk/'+fn
    strFile = local_dir + 'inputimage.jpg'
    download(strBucket,strKey,strFile)

    strResult, mem2 = run_inference_on_image(local_dir + 'inputimage.jpg')
    mem3 = get_memory()
    et = int(round(time.time() * 1000))
    c = cycles(st, start_stats, et, getStats())
    objRet =  {
        'statusCode': 200,
        'ev': event,
        'etime': time.time() - start_time,
        'cycles': c,
        'utilization': c/(et-st),
        #'download_size' : os.path.getsize(local_dir + 'inputimage.jpg'),
        #'upload_size': os.path.getsize('%sout_%s'%(local_dir, key))
        'download_size' : 0,
        'upload_size': 0,
        'memory': str(mem1)+' '+str(mem2)+' '+str(mem3)
    }
    return objRet


if __name__ == "__main__":
    download = downloadFromLocal
    res = handler(sys.argv[1],None)
    print(res)

