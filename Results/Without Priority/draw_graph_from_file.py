import re
import sys

import plotly.graph_objs as go
import plotly.offline as py

graph_type = sys.argv[1]

if graph_type == 'rtt':
    regex_raw = r'(echo|ack)_str\s+(?P<size>\d+)\s+average rtt\/request=(?P<val>.*)\b'
    regex = re.compile(regex_raw)
elif graph_type == 'bandwidth':
    regex_raw = r'(echo|ack)_str\s+(?P<size>\d+)\s+(?P<val>.*)\s+kB\/s'
    regex = re.compile(regex_raw)
else:
    raise ValueError('First argument must be the type of the graph (rtt or bandwidth)')

files = sys.argv[2:]
traces = {}

for file in files:
    with open(file) as f:
        file = file.replace('/', '_')
        # each file has 2 traces, ack and echo. Each trace has x and y values
        traces[file] = {'ack':  {'x': [], 'y': []},
                        'echo': {'x': [], 'y': []}}
        for line in f.readlines():
            match = regex.search(line)
            if match:
                # line.split('_')[0] returns either ack or echo
                traces[file][line.split('_')[0]]['x'].append(match.group('size'))
                traces[file][line.split('_')[0]]['y'].append(match.group('val'))

layout = go.Layout(
    xaxis=dict(
        title='MESSAGE SIZE (bytes)',
        type='log',
        autorange=True,
    ),
    yaxis=dict(
        title='RTT (milliseconds)' if graph_type == 'rtt' else 'BANDWIDTH (kB/s)',
        type='log',
        autorange=True
    )
)


def draw_graph(traces):
    for name in traces.keys():
        data = [go.Scatter(x=traces[name]['ack']['x'], y=traces[name]['ack']['y'], name=name + '_ack'),
                go.Scatter(x=traces[name]['echo']['x'], y=traces[name]['echo']['y'], name=name + '_echo')]
        fig = go.Figure(data=data, layout=layout)
        py.plot(fig, filename=name + ('_rtt.html' if graph_type == 'rtt' else '_bw.html'))


def draw_graph_merged(traces):
    data = []
    for name in traces.keys():
        data.append(go.Scatter(x=traces[name]['ack']['x'], y=traces[name]['ack']['y'], name=name + '_ack'))
        data.append(go.Scatter(x=traces[name]['echo']['x'], y=traces[name]['echo']['y'], name=name + '_echo'))
    fig = go.Figure(data=data, layout=layout)
    py.plot(fig, filename='-'.join(traces.keys()) + ('_rtt.html' if graph_type == 'rtt' else '_bw.html'))


#draw_graph(traces)
draw_graph_merged(traces)