import os

def read_file(path):
    with open(path, 'r') as file:
        data = file.read().replace('\n', '')
    return data

def draw_slow_starts_count():
    setups = ['sleep', 'image_classification','file_hashing','video']
    for setup in setups:
        path_tmp = 'data/%s'%setup
        data = {}
        for fn in os.listdir(path_tmp):
            if '_ss' in fn:
                continue
            path = os.path.join(path_tmp, fn)
            ss = read_file(path)
            ss = [float(x) for x in ss.split(',')]

            concurrency = int(fn.split('_')[0])
            data[concurrency] = max(ss)
        with open(os.path.join('tmp',setup + '.txt'), 'w') as f:
            keys = sorted(data.keys())
            for i,key in enumerate(keys):
                f.write(f'{key} {data[key]/data[1]}\n')

    os.system('gnuplot draw.plt')


def main():
    draw_slow_starts_count()



if __name__ == '__main__':
    main()