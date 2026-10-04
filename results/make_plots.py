"""Draw the figures from results.csv into results/plots/.
Run from anywhere: python3 results/make_plots.py   (needs matplotlib)"""
import csv, os
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt

here = os.path.dirname(os.path.abspath(__file__))
rows = list(csv.DictReader(open(os.path.join(here, 'results.csv'))))
os.makedirs(os.path.join(here, 'plots'), exist_ok=True)
ns = [100, 1000, 10000, 100000]
cases = [('W1', '-', 'W1 Random access: 10,000 get(index)'),
         ('W2', '-', 'W2 Search: 1,000 contains(x), half present'),
         ('W3', 'head', 'W3 Insert + remove at index 0: 1,000 + 1,000'),
         ('W3', 'middle', 'W3 Insert + remove at index n/2: 1,000 + 1,000'),
         ('W4', '-', 'W4 Priority processing: n insert + n extractMin')]
col = {'DynamicArray': 'tab:blue', 'MyLinkedList': 'tab:red', 'MinHeap': 'tab:green'}
mk = {'steps': 'o', 'moves': 's', 'comparisons': '^'}
ls = {'DynamicArray': '-', 'MyLinkedList': '--', 'MinHeap': '-'}
for w, v, title in cases:
    structs = sorted({r['structure'] for r in rows if r['workload'] == w and r['variant'] == v})
    fig, (a, b) = plt.subplots(1, 2, figsize=(11, 4.2))
    for s in structs:
        rs = sorted([r for r in rows if r['workload'] == w and r['variant'] == v and r['structure'] == s],
                    key=lambda r: int(r['n']))
        a.plot(ns, [float(r['time_ms']) for r in rs], ls[s], marker='o', color=col[s], label=s)
        for m in ('steps', 'moves', 'comparisons'):
            y = [int(r[m]) for r in rs]
            if max(y) == 0:
                continue
            b.plot(ns, y, ls[s], marker=mk[m], color=col[s], label=f"{s}: {m}", alpha=.85)
    a.set_title('(a) Time vs n (median of 5 runs)')
    a.set_xlabel('n (number of elements)'); a.set_ylabel('time (ms)')
    b.set_title('(b) Operation counts vs n')
    b.set_xlabel('n (number of elements)'); b.set_ylabel('operations (count)')
    for ax in (a, b):
        ax.set_xscale('log'); ax.set_yscale('log'); ax.grid(True, which='both', alpha=.3)
    a.legend(fontsize=8, title='structure'); b.legend(fontsize=7, title='structure: counter')
    fig.suptitle(title + '   [log-log axes; counters that are always 0 are not drawn]', fontsize=10)
    fig.tight_layout()
    name = f"{w}{'_' + v if v != '-' else ''}.png"
    fig.savefig(os.path.join(here, 'plots', name), dpi=110)
    plt.close(fig)
print('plots written to', os.path.join(here, 'plots'))
