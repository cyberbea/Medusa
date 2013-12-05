import os,sys
import networkx as nx

######################

args=sys.argv
inp=args[1]
out_=args[2]

G=nx.read_gexf(inp)
diz=nx.betweenness_centrality(G,normalized=False)

out=open(out_,'w')
for k,v in diz.iteritems():
	out.write('%s\t%s\n' %(k,v))
