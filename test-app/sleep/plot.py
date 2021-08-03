import matplotlib as mpl

## agg backend is used to create plot as a .png file
mpl.use('agg')
mpl.rcParams['font.size'] = 16
mpl.rcParams['font.sans-serif'] = 'Arial'


import matplotlib.pyplot as plt
import os
import numpy as np


data_path = 'data/'
number_of_experiments = 100

if not os.path.exists(data_path):
    os.makedirs(data_path)

if not os.path.exists('plots/'):
    os.makedirs('plots/')

dirs = 'plots/compare/'
if not os.path.exists(dirs):
    os.makedirs(dirs)


def plot_line(concurrency, threads, data, ax):
    x = sorted(data)
    y = np.linspace(0,1,len(x))
    ax.plot(x,y, label=f'concurrency:{concurrency} load:{threads}')

def plot_line_Overtime(concurrency, threads, data, ax):
#   x = list(range(number_of_experiments))
    x = range(1,number_of_experiments+1)
    y = data
    ax.plot(x,y, label=f'concurrency:{concurrency} load:{threads}')


def plot_execution_time_overtime():
    fig = plt.figure(1, figsize=(9, 6))
    ax = fig.add_subplot(111)

#   data_path = 'data/'
    for fn in sorted(list(os.listdir(data_path))):
        path = data_path+fn
        if not os.path.isfile(path) or '_ss' in path:
            continue
        with open(path) as f:
            data = [float(x) for x in f.read().strip().split(',')]
            sp = fn.split('_')
            concurrency = int(sp[0])
            threads = int(sp[1].split('.')[0])
            plot_line_Overtime(concurrency, threads, data, ax)
    
    ax.grid(color='#e3e3e3', linestyle='--', linewidth=0.5)
    ax.spines['right'].set_visible(False)
    ax.spines['top'].set_visible(False)
    ax.set_title("Execution time Overtime")
    ax.set_xlabel("Experiment num")
    ax.set_ylabel('Time (ms)')
    ## Remove top axes and right axes ticks
    ax.get_xaxis().tick_bottom()
    ax.get_yaxis().tick_left()
    leg = ax.legend(loc='lower right')
    leg.get_frame().set_linewidth(0.0)
    fig.savefig(os.path.join('plots', 'overhead_overtime.png'), bbox_inches='tight', dpi=300)
    fig.clf()
 
def plot_execution_time_bar():
    fig = plt.figure(1, figsize=(9, 6))
    ax = fig.add_subplot(111)

#   data_path = 'data/'
    for fn in sorted(list(os.listdir(data_path))):
        path = data_path+fn
        if not os.path.isfile(path) or '_ss' in path:
            continue
        with open(path) as f:
            data = [float(x) for x in f.read().strip().split(',')]
            sp = fn.split('_')
            concurrency = int(sp[0])
            threads = int(sp[1].split('.')[0])
            plot_line_Overtime(concurrency, threads, data, ax)  #can be delete

            x = list(range(1,number_of_experiments+1))
        #   x = list(range(number_of_experiments))
        #   print("bar x is ", x)
            y = data
        #   print("bar y is ",data)
            ax.bar(x, y, align='center', alpha=0.4, label=f'concurrency:{concurrency} load:{threads}')
    
    ax.grid(color='#e3e3e3', linestyle='--', linewidth=0.5)
    ax.spines['right'].set_visible(False)
    ax.spines['top'].set_visible(False)
    ax.set_title("Execution time Overtime_Bar")
    ax.set_xlabel("Experiment num")
    ax.set_ylabel('Time (ms)')
    ## Remove top axes and right axes ticks
    ax.get_xaxis().tick_bottom()
    ax.get_yaxis().tick_left()
    leg = ax.legend(loc='lower right')
    leg.get_frame().set_linewidth(0.0)
    fig.savefig(os.path.join('plots', 'overhead_overtime_bar.png'), bbox_inches='tight', dpi=300)
    fig.clf()

def plot_compare():

#   data_path = 'data/'
    data_path_openwhisk = 'data-openwhisk/'
    data_path_pho = "data-pho/"
    for fn in sorted(list(os.listdir(data_path_openwhisk))):
#       print("fn is", fn)
        path = data_path_openwhisk+fn
#       print("path is: ", path)
        if not os.path.isfile(path) or '_ss' in path:
            continue
        with open(path) as f:
            data = [float(x) for x in f.read().strip().split(',')]
            sp = fn.split('_')
            concurrency = int(sp[0])
            threads = int(sp[1].split('.')[0])
            y = data
#           print("bar y is ",data)    
            
            for fn2 in sorted(list(os.listdir(data_path_pho))):
#               print("fn2 is", fn2)
                path2 = data_path_pho+fn2
#               print("path2 is: ", path2)
                if not os.path.isfile(path2) or '_ss' in path2:
                    continue
                with open(path2) as f2:
                    fig = plt.figure(1, figsize=(9, 6))
                    ax = fig.add_subplot(111)

                    data2 = [float(x) for x in f2.read().strip().split(',')]
                    sp2 = fn2.split('_')
                    concurrency2 = int(sp2[0])
                    threads2 = int(sp2[1].split('.')[0])
                    if concurrency == concurrency2 and threads == threads2:
                        x = list(range(1,number_of_experiments+1))
                    #   x = list(range(number_of_experiments))
#                       print("bar x2 is ", x)
                        y2 = data2
#                       print("bar y2 is ",data2)

                        plot_line_Overtime(concurrency, threads, data, ax) #can be delete
                        plot_line_Overtime(concurrency, threads, data2, ax) #can be delete
                        
                        result1 = ax.bar(x, y, align='center', alpha=0.5, label=f'openwhisk')
                        result2 = ax.bar(x, y2, align='center', alpha=0.5, label=f'photon')
                        name = f'concurrency:{concurrency}_load:{threads}.png'
#                       print(name)

                        ax.grid(color='#e3e3e3', linestyle='--', linewidth=0.5)
                        ax.spines['right'].set_visible(False)
                        ax.spines['top'].set_visible(False)
                        ax.set_title("Execution time Overtime_" + f'concurrency:{concurrency}_load:{threads}')
                        ax.set_xlabel("Experiment num")
                        ax.set_ylabel('Time (ms)')
                        ## Remove top axes and right axes ticks
                        ax.get_xaxis().tick_bottom()
                        ax.get_yaxis().tick_left()
                        leg = ax.legend(loc='lower right')
                        leg.get_frame().set_linewidth(0.0)
                        fig.savefig(os.path.join(dirs, name), bbox_inches='tight', dpi=300)
#                       fig.savefig(os.path.join('plots/compare/', 'overhead_overtime_compare.png'), bbox_inches='tight', dpi=300)
                        break
                    fig.clf()

   
def plot_openwhisk():

#   data_path = 'data/'
    data_path_openwhisk = 'data-openwhisk/'
    data_path_pho = "data-pho/"
    for fn in sorted(list(os.listdir(data_path_openwhisk))):
#       print("fn is", fn)
        path = data_path_openwhisk+fn
#       print("path is: ", path)
        if not os.path.isfile(path) or '_ss' in path:
            continue
        with open(path) as f:
            fig = plt.figure(1, figsize=(9, 6))
            ax = fig.add_subplot(111)
            data = [float(x) for x in f.read().strip().split(',')]
            sp = fn.split('_')
            concurrency = int(sp[0])
            threads = int(sp[1].split('.')[0])
            y = data
#           print("bar y is ",data)    
            x = list(range(1,number_of_experiments+1))
            plot_line_Overtime(concurrency, threads, data, ax) #can be delete
            result1 = ax.bar(x, y, align='center', alpha=0.5, label=f'openwhisk')
            name = f'concurrency:{concurrency}_load:{threads}.png'

            ax.grid(color='#e3e3e3', linestyle='--', linewidth=0.5)
            ax.spines['right'].set_visible(False)
            ax.spines['top'].set_visible(False)
            ax.set_title("Execution time Overtime_" + f'concurrency:{concurrency}_load:{threads}')
            ax.set_xlabel("Experiment num")
            ax.set_ylabel('Time (ms)')
            ## Remove top axes and right axes ticks
            ax.get_xaxis().tick_bottom()
            ax.get_yaxis().tick_left()
            leg = ax.legend(loc='lower right')
            leg.get_frame().set_linewidth(0.0)
            fig.savefig(os.path.join(dirs, name), bbox_inches='tight', dpi=300)
            fig.clf()

def plot_pho():

#   data_path = 'data/'
    data_path_openwhisk = 'data-openwhisk/'
    data_path_pho = "data-pho/"
    for fn2 in sorted(list(os.listdir(data_path_pho))):
#       print("fn2 is", fn2)
        path2 = data_path_pho+fn2
#       print("path2 is: ", path2)
        if not os.path.isfile(path2) or '_ss' in path2:
            continue
        with open(path2) as f2:
            fig = plt.figure(1, figsize=(9, 6))
            ax = fig.add_subplot(111)

            data2 = [float(x) for x in f2.read().strip().split(',')]
            sp2 = fn2.split('_')
            concurrency2 = int(sp2[0])
            threads2 = int(sp2[1].split('.')[0])
            y2 = data2
#           print("bar y2 is ",data2)    
            x = list(range(1,number_of_experiments+1))
            plot_line_Overtime(concurrency2, threads2, data2, ax) #can be delete
            result1 = ax.bar(x, y2, align='center', alpha=0.5, label=f'photon')
            name = f'concurrency:{concurrency2}_load:{threads2}.png'

            ax.grid(color='#e3e3e3', linestyle='--', linewidth=0.5)
            ax.spines['right'].set_visible(False)
            ax.spines['top'].set_visible(False)
            ax.set_title("Execution time Overtime_" + f'concurrency:{concurrency2}_load:{threads2}')
            ax.set_xlabel("Experiment num")
            ax.set_ylabel('Time (ms)')
            ## Remove top axes and right axes ticks
            ax.get_xaxis().tick_bottom()
            ax.get_yaxis().tick_left()
            leg = ax.legend(loc='lower right')
            leg.get_frame().set_linewidth(0.0)
            fig.savefig(os.path.join(dirs, name), bbox_inches='tight', dpi=300)
            fig.clf()

def plot_execution_time():
    fig = plt.figure(1, figsize=(9, 6))
    ax = fig.add_subplot(111)

#   data_path = 'data/'
    for fn in sorted(list(os.listdir(data_path))):
        path = data_path+fn
        if not os.path.isfile(path) or '_ss' in path:
            continue
        with open(path) as f:
            data = [float(x) for x in f.read().strip().split(',')]
            print(data)
            sp = fn.split('_')
            concurrency = int(sp[0])
            threads = int(sp[1].split('.')[0])
            plot_line(concurrency, threads, data, ax)
    
    ax.grid(color='#e3e3e3', linestyle='--', linewidth=0.5)
    ax.spines['right'].set_visible(False)
    ax.spines['top'].set_visible(False)
    ax.set_title("Execution time")
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

#   data_path = 'data/'
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
            labels.append(str(threads))
            

    ax.bar(labels, data_points, align='center', alpha=0.5)
    print(labels)
    print(data_points)
    
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
   

def avg_execution_time():
#   data_path = 'data/'
    for fn in sorted(list(os.listdir(data_path))):
        path = data_path+fn
        if not os.path.isfile(path) or '_ss' in path:
            continue
        with open(path) as f:
            data = [float(x) for x in f.read().strip().split(',')]
            avg = np.mean(data)
            sum = np.sum(data)
            print(" ")
            print(fn)
#           print("sum is: ", sum)
            print("avg is: ", avg)


            sum2 = 0
            i = 0
            for x in data:
                if x > 50:
                    sum2 +=x
                    i +=1
            avg2 = sum2 / i
            print("qualified experiment num: ", i)
#           print("sum2 is: ", sum2)
            print("avg qualified is: ", avg2)



def main():   
    plot_execution_time()
    plot_slow_starts()
    plot_execution_time_overtime()
    plot_execution_time_bar()
    plot_openwhisk()    #can command out at first, plot only openwhisk res
    plot_pho()          #can command out at first, plot only photon res
    plot_compare()      #can command out at first, used to compare openwhisk and photon result

    avg_execution_time()

    
if __name__ == '__main__':
    main()
