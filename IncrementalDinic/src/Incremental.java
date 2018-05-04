import java.util.LinkedList;
import java.util.List;

public class Incremental {
	private boolean[] marked;
	private List<Integer> AFFECTED_1 = new LinkedList<Integer>();
	private List<Integer> AFFECTED_2 = new LinkedList<Integer>();
	private double value;

    In in = new In("test3.txt");
    FlowNetwork G = new FlowNetwork(in);
    int s = 0, t = G.V()-1;
    
    /*int rows = 300;
	int cols = 300;
	int V = rows*cols;
	int s = 0;
	int t = V-1;
	FlowNetwork G = new FlowNetwork(rows,cols);*/
    
	Dinic maxflow = new Dinic(G,s,t);
	double initMaxValue = maxflow.value();

	public Incremental (int v, int w, double capacity){
		StdOut.println("The original max flow is "+initMaxValue);
		StdOut.println("The original network is : \n" + G);
		double delta = 0.0;
		//add arc v->w
		G.addEdge(new FlowEdge(v, w, capacity));
		AFFECTED_1 = BBFS(v,s);
	
		AFFECTED_2 = FBFS(w,t);
		StdOut.println("The affected vertices found by BBFS are "+AFFECTED_1);
		StdOut.println("The affected vertices found by FBFS are "+AFFECTED_2);
		List<Integer> AFFECTED =  AFFECTED_1;
		AFFECTED.addAll(AFFECTED_2);
		//StdOut.println("all affected vertices "+AFFECTED);
		/*
		//a test for constructing level graph
		List<Integer> AFFECTED = new LinkedList<Integer>();
		AFFECTED.add(0);
		AFFECTED.add(2);
		AFFECTED.add(4);
		AFFECTED.add(6);
		StdOut.println(AFFECTED);*/
		
		
		int[] level = new int[G.V()];
		//StdOut.println(constructLevelGraph(G,AFFECTED,s,t,level));
	    while (constructLevelGraph(G,AFFECTED,s,t,level)) {
	    	int[] ptr = new int[G.V()];
		    while (true) {
		    	double bottle = sendFlow(G, ptr, level, t, s, Integer.MAX_VALUE);
		    	if (bottle == 0)
		    		break;
		    	delta += bottle;
		    }
		}
	    StdOut.println("The additional flow is " + delta);
	    value = delta+ initMaxValue;
    }
	
	public double value(){
		return value;
	}
	//BBFS
	List<Integer> BBFS (int v, int s){
		marked = new boolean[G.V];
		Queue<Integer> queue = new Queue<Integer>();
		List<Integer> AFFECTED_1 = new LinkedList<Integer>();
			
		queue.enqueue(v);
		AFFECTED_1.add(v);
		marked[v] = true;
		    
		while (!queue.isEmpty()){
			int x = queue.dequeue();
		    if(x == s)
		    	break;
		    for(FlowEdge e : G.adj(x)){
		    	int y = e.other(x);
		    	if( y == e.from() && e.capacity()>e.flow() && !marked[y]){
		    		queue.enqueue(y);
		    		AFFECTED_1.add(y);
		    		marked[y] = true;
		    	}
		    }
		}
		return AFFECTED_1;
	}
	
	//FBFS
	List<Integer> FBFS (int w, int t){
		marked = new boolean[G.V];
		Queue<Integer> queue = new Queue<Integer>();
		List<Integer> AFFECTED_2  = new LinkedList<Integer>();
		
		queue.enqueue(w);
		AFFECTED_2.add(w);
		marked[w] = true;
		
		while (!queue.isEmpty()){
			int x = queue.dequeue();
		    if(x == t)
		    	break;
		    for(FlowEdge e : G.adj(x)){
		    	int y = e.other(x);
		    	if( y == e.to() && e.capacity()>e.flow() && !marked[y]){
		    		queue.enqueue(y);
		    		AFFECTED_2.add(y);
		    		marked[y] = true;
		    	}
		    }
		 }
		 return AFFECTED_2;
	}
	
	boolean constructLevelGraph(FlowNetwork G, List<Integer> AFFECTED, int s, int t, int[] level) {
		Queue<Integer> queue = new Queue<Integer>();
		for(int i: AFFECTED){
		   level[i]=-1;
		   //StdOut.println(i+":"+level[i]);
	    }
		if(AFFECTED.contains(s)){
			level[s]=0;
		  	queue.enqueue(s);
		  	while(!queue.isEmpty()){
		  		int v = queue.dequeue();
		  		if(AFFECTED.contains(v)){
		  			for (FlowEdge e : G.adj[v]) {
		  				if (AFFECTED.contains(e.to()) && level[e.to()] < 0 && e.flow() < e.capacity()) {
		  					level[e.to()] = level[v] + 1;
		  					queue.enqueue(e.to());
		  				}
		  			}
		  		}
		  		//StdOut.println(v+":"+level[v]);
		  	}
		  	//StdOut.println("t" + " is "+level[t]);
		  	return level[t] > 0;
		} else {
			return false;
		}     
	}

	
	double sendFlow(FlowNetwork G, int[] ptr, int[] dist, int dest, int u, double f) {
		if (u == dest)
			return f;
	    for (; ptr[u] < G.adj[u].size(); ++ptr[u]) {
	      FlowEdge e = G.adj[u].get(ptr[u]);
	      if (dist[e.to()] == dist[u] + 1 && e.flow() < e.capacity()) {
	    	  double df = sendFlow(G, ptr, dist, dest, e.v, Math.min(f, e.capacity - e.flow));
	    	  if (df > 0) {
	        	e.addResidualFlowTo(e.to(), df);
	        	return df;
	    	  }
	      }
	    }
	    return 0;
	}	
	
	//test
	public static void main (String[] args){
		long begin = System.currentTimeMillis(); 
		Incremental incremental  = new Incremental(2,4,15.0);
		long end = System.currentTimeMillis() - begin;
		StdOut.println("The updated max flow is " + incremental.value()+" by IncrementalDinic");
		StdOut.println("The updated network is : \n" + incremental.G);
		//System.out.println("Execution Time：" + end + "ms"); 
	}
	
}
