
workloads = ['baseline','sleep','rest','image','filehash','video','thumbnail']
formats = '%.3f %.3f %.3f %.3f %.3f %.3f %.3f\n'
values = [1, 0.026, 0.018, 0.184, 0.083, 0.75,0.076]
current = [1]*len(values)
total_points = 100

with open('data/data.txt', 'w') as f:
    for i in range(1, total_points+1):
        f.write(formats % tuple(current))
        for co, x in enumerate(values):
            current[co] += x
