import matplotlib.pyplot as plt
import os
import numpy as np

def plot_line(concurrency, threads, data):
    x = sorted(data)
    y = np.linspace(0,1,len(x))
    plt.plot(x,y, label=f'concurrency:{concurrency} parallel:{threads}')

def main():
    data_path = 'data/'
    for fn in sorted(list(os.listdir(data_path))):
        with open(data_path+fn) as f:
            data = [float(x) for x in f.read().strip().split(',')]
            sp = fn.split('_')
            concurrency = int(sp[0])
            threads = int(sp[1].split('.')[0])
            plot_line(concurrency, threads, data)
    plt.legend(loc='lower right')
    plt.title("Invocation overhead")
    plt.xlabel("Overhead (ms)")
    plt.ylabel("CDF")
    plt.show()

    
if __name__ == '__main__':
    main()