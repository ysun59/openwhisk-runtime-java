#!/usr/bin/python3

# step 1, load argument and get the options
# step 2, load files and metrics
# step 3, prepapre plots

import sys, getopt
import statistics
import matplotlib.pyplot as plt
import collections

# TODO - allow the config file and results dir to be args
configfile = ''
resultsdir = ''
configmap = collections.OrderedDict()
throughput_avg_map = collections.OrderedDict()
throughput_std_map = collections.OrderedDict()
latency_map = collections.OrderedDict()
warmup_x_map = collections.OrderedDict()
warmup_y_map = collections.OrderedDict()

#prices for 1000ms of one vCPU
lambda_pricing = {}
lambda_pricing['0.1'] = 0.00000313
lambda_pricing['0.2'] = 0.00000625
lambda_pricing['0.4'] = 0.00001250
lambda_pricing['0.6'] = 0.00001771
lambda_pricing['0.8'] = 0.00002396
lambda_pricing['1']   = 0.00002917

def parse_args(argv):
  global configfile
  global resultsdir
  try:
    opts, args = getopt.getopt(argv,"c:r:")
  except getopt.GetoptError:
    print ('experiment-process.py -c <experiment config file> -r <experiment results dir>')
    sys.exit(2)

  for opt, arg in opts:
    if opt in ("-c", "--cfile"):
      configfile = arg
    elif opt in ("-r", "--rdir"):
      resultsdir = arg

  if not configfile or not resultsdir:
      print('experiment-process.py -c <experiment config file> -r <experiment results dir>')
      sys.exit()

def load_config():
  global configmap
  with open(configfile) as f:
    for line in f:
      key, value = line.split('=')
      configmap[key] = value.replace('\n','').replace('\"','').split(' ')
  configmap['nwarms'] = int(configmap['nwarms'][0])
  configmap['nexps'] = int(configmap['nexps'][0])
  configmap['app_name'] = configmap['app_name'][0]

def load_log(path):
  warmup_x = []
  warmup_y = []
  throughput = 0
  latency = 0
  with open(path) as f:
    for line in f:
      if '#' in line:
        warmup_y.append(float(line.split(' ')[1]))
        warmup_x.append(float(line.split(' ')[2]))
      elif 'Experiments ops/s' in line:
        throughput = float(line.split(' ')[2])
      elif 'Experiments avg latency' in line:
        latency = float(line.split(' ')[4])
  return warmup_x, warmup_y, throughput, latency

def plot_warmup():
  for label in warmup_x_map.keys():
    plt.plot(warmup_x_map[label], warmup_y_map[label], label=label)
  plt.ylabel('Response Time (ms)')
  plt.xlabel('Time')
  plt.legend(loc='upper right')
  plt.savefig('warmup.png')
  plt.clf()

def plot_throughput():
  x_labels = list(throughput_avg_map.keys())
  x = range(1, len(x_labels) + 1)
  y = list(throughput_avg_map.values())
  y_error = list(throughput_std_map.values())
  plt.figure(figsize=(10, 5))
  plt.bar(x, y, yerr=y_error, width=0.5)
  plt.xticks(x, x_labels, rotation='vertical')
  plt.ylabel('Invocations/s')
  plt.subplots_adjust(bottom=0.25)
  plt.savefig('throughput.png', dpi=1000)
  plt.clf()

def plot_cost():
  cost_map = throughput_avg_map.copy()
  for exp in cost_map:
    if exp.startswith('vcpu-0.1-'):
      # rationale: price for 100M under s specific vCPU configuration
      cost_map[exp] = 100000000.0 / cost_map[exp] * lambda_pricing['0.1']
    elif exp.startswith('vcpu-0.2-'):
      cost_map[exp] = 100000000.0 / cost_map[exp] * lambda_pricing['0.2']
    elif exp.startswith('vcpu-0.4-'):
      cost_map[exp] = 100000000.0 / cost_map[exp] * lambda_pricing['0.4']
    elif exp.startswith('vcpu-0.6-'):
      cost_map[exp] = 100000000.0 / cost_map[exp] * lambda_pricing['0.6']
    elif exp.startswith('vcpu-0.8-'):
      cost_map[exp] = 100000000.0 / cost_map[exp] * lambda_pricing['0.8']
    elif exp.startswith('vcpu-1-'):
      cost_map[exp] = 100000000.0 / cost_map[exp] * lambda_pricing['1']
    else:
      raise ValueError('Unknown vcpu count for', exp)

  x_labels = list(cost_map.keys())
  x = range(1, len(x_labels) + 1)
  y = list(cost_map.values())
  plt.figure(figsize=(10, 5))
  plt.bar(x, y, width=0.5)
  plt.xticks(x, x_labels, rotation='vertical')
  plt.ylabel('Cost (USD) per 100M invocations')
  plt.subplots_adjust(bottom=0.25)
  plt.gca().grid(which='major', axis='y', linestyle='--')
  plt.savefig('cost.png', dpi=1000)
  plt.clf()

def main(argv):

  parse_args(argv)

  load_config()

  for vcpu in configmap['vcpus']:
    for cthread in configmap['cthreads']:
      exp_id = 'vcpu-' + vcpu + '-conc-' + cthread
      exp_throughput = {}
      exp_latency = {}
      for rep in configmap['reps']:
        log_path= resultsdir + '/' + exp_id + '-rep-' + rep + '.log'
        print(log_path)
        warmup_x, warmup_y, throughput, latency = load_log(log_path)
        warmup_x_map[exp_id] = warmup_x
        warmup_y_map[exp_id] = warmup_y
        exp_throughput[rep] = throughput
        exp_latency[rep] = latency
      throughput_avg_map[exp_id] = statistics.mean(exp_throughput.values())
      throughput_std_map[exp_id] = statistics.stdev(exp_throughput.values())
      latency_map[exp_id] = statistics.mean(exp_latency.values())

  plot_warmup()
  plot_throughput()
  plot_cost()

if __name__ == "__main__":
  main(sys.argv[1:])
