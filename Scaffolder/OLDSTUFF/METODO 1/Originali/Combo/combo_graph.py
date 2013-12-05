import networkx as nx
import os,sys
##################
args=sys.argv
inp,out=args[1],args[2]
try: args[3] 
except : a = 0.5
else : a =  float(args[3])

try: args[4] 
except : b = 0.5
else : b = float(args[4])
##################
def new_w(hom,synt,a,b):
	return a*hom + b*synt
##################
G=nx.read_gexf(inp)
for edge in G.edges():
	n1,n2=edge[0],edge[1]
	try: G[n1][n2]['homology']
	except: 
		print G[n1][n2].keys()		
		hom=0
	else:
		print 'ajajajaja'
		hom=G[n1][n2]['homology']
	try: G[n1][n2]['synteny']
	except: synt=0
	else: synt=G[n1][n2]['synteny']
##################
	G[n1][n2]['weight']=new_w(hom,synt,a,b)
	print n1,n2, G[n1][n2]['weight']
nx.write_gexf(G,out)
