import os


def main():
    os.system('gnuplot draw_latency.plt')
    os.system('gnuplot draw_cost.plt')


if __name__ == '__main__':
    main()