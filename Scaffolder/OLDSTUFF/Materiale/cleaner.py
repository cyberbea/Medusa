import networkx as nx
import os,sys

###################
args=sys.argv
inp,out=args[1],args[2]
try: args[3]
except: thr=100.0
else: thr=float(args[3])
###################

G=nx.read_gexf(inp)
bc=nx.betweenness_centrality(G,normalized=False)
bads=[node for node in bc if bc[node]>thr]
for node in bads: G.remove_node(node)

nx.write_gexf(G,out)
