import os

def read_file(path):
    with open(path, 'r') as file:
        data = file.read().replace('\n', '')
    return data

def draw_slow_starts_count():
    setups = ['aws', 'c1', 'c4']
    for setup in setups:
        path_tmp = 'data/%s'%setup
        data = {}
        for fn in os.listdir(path_tmp):
            if '_ss' not in fn:
                continue
            path = os.path.join(path_tmp, fn)
            ss = read_file(path)
            ss = [int(x) for x in ss.split(',')]

            threads = int(fn.split('_')[1])
            data[threads] = sum(ss)
        with open(os.path.join('tmp',setup + '.txt'), 'w') as f:
            keys = sorted(data.keys())
            for i,key in enumerate(keys):
                f.write(f'{i} {key} {data[key]}\n')

    os.system('gnuplot draw.plt')


def main():
    draw_slow_starts_count()



if __name__ == '__main__':
    main()