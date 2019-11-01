import matplotlib as mpl

## agg backend is used to create plot as a .png file
mpl.use('agg')
mpl.rcParams['font.size'] = 16
mpl.rcParams['font.sans-serif'] = 'Arial'


import matplotlib.pyplot as plt
import os
import numpy as np


def plot_line(concurrency, threads, data, ax):
    x = sorted(data)
    y = np.linspace(0,1,len(x))
    ax.plot(x,y, label=f'concurrency:{concurrency} load:{threads}')

def plot_execution_time():
    fig = plt.figure(1, figsize=(9, 6))
    ax = fig.add_subplot(111)

    data_path = 'data/'
    for fn in sorted(list(os.listdir(data_path))):
        path = data_path+fn
        if not os.path.isfile(path) or '_ss' in path:
            continue
        with open(path) as f:
            data = [float(x) for x in f.read().strip().split(',')]
            sp = fn.split('_')
            concurrency = int(sp[0])
            threads = int(sp[1].split('.')[0])
            plot_line(concurrency, threads, data, ax)
    
    ax.grid(color='#e3e3e3', linestyle='--', linewidth=0.5)
    ax.spines['right'].set_visible(False)
    ax.spines['top'].set_visible(False)
    ax.set_title("Invocation overhead")
    ax.set_xlabel("Overhead (ms)")
    ax.set_ylabel('CDF')
    ## Remove top axes and right axes ticks
    ax.get_xaxis().tick_bottom()
    ax.get_yaxis().tick_left()
    leg = ax.legend(loc='lower right')
    leg.get_frame().set_linewidth(0.0)
    fig.savefig(os.path.join('plots', 'overhead.png'), bbox_inches='tight', dpi=300)
    fig.clf()

def plot_slow_starts():
    fig = plt.figure(1, figsize=(9, 6))
    ax = fig.add_subplot(111)

    data_path = 'data/'
    data_points = []
    labels = []
    for fn in sorted(list(os.listdir(data_path))):
        path = data_path+fn
        if not os.path.isfile(path) or '_ss' not in path:
            continue
        with open(path) as f:
            data = [int(x) for x in f.read().strip().split(',')]
            sp = fn.split('_')
            concurrency = int(sp[0])
            threads = int(sp[1].split('.')[0])

            data_points.append(sum(data))
            labels.append(threads)

    labels,data_points = zip(*sorted(zip(labels,data_points))) 
    labels = [str(x) for x in labels]
    ax.bar(labels, data_points, align='center', alpha=0.5)
    
    ax.grid(color='#e3e3e3', linestyle='--', linewidth=0.5)
    ax.spines['right'].set_visible(False)
    ax.spines['top'].set_visible(False)
    ax.set_title("Slow starts")
    ax.set_ylabel("Slow starts")
    ## Remove top axes and right axes ticks
    ax.get_xaxis().tick_bottom()
    ax.get_yaxis().tick_left()
    leg = ax.legend(loc='lower right')
    leg.get_frame().set_linewidth(0.0)
    fig.savefig(os.path.join('plots', 'slow_start.png'), bbox_inches='tight', dpi=300)
    fig.clf()

def main():   
    plot_execution_time()
    plot_slow_starts()

    
if __name__ == '__main__':
    main()
