import java.util.Arrays;
import java.util.List;

public class Dinic {
	int V;
	double value;
	
	public  Dinic (FlowNetwork G, int s, int t){
	    this.value = 0.0;
	    int[] level = new int[G.V()];
	    while (dinicBfs(G, s, t, level)) {
	      int[] ptr = new int[G.V()];
	      while (true) {
	    	  double delta = dinicDfs(G, ptr, level, t, s, Integer.MAX_VALUE);
	    	  if (delta == 0)
	    		  break;
	    	  value += delta;
	      }
	    }
	}
	
	public double value(){
		return value;
	}

	boolean dinicBfs(FlowNetwork G, int s, int t, int[] level) {
		Queue<Integer> queue = new Queue<Integer>();
		for(int i=0; i<G.V();i++){
		   level[i]=-1;
	    }
	    level[s]=0;
	    queue.enqueue(s);
	    while(!queue.isEmpty()){
	    	int v = queue.dequeue();
	    	for (FlowEdge e : G.adj[v]) {
	    		if (level[e.to()] < 0 && e.flow() < e.capacity()) {
	    			level[e.to()] = level[v] + 1;
	    			 queue.enqueue(e.to());
	    		}
	    	}
	    }
	    return level[t] >= 0;	  
	}

	  double dinicDfs(FlowNetwork G, int[] ptr, int[] dist, int dest, int u, double f) {
	    if (u == dest)
	      return f;
	    for (; ptr[u] < G.adj[u].size(); ++ptr[u]) {
	      FlowEdge e = G.adj[u].get(ptr[u]);
	     // StdOut.println(u+":"+ptr[u]);
	      if (dist[e.v] == dist[u] + 1 && e.flow() < e.capacity()) {
	        double df = dinicDfs(G, ptr, dist, dest, e.v, Math.min(f, e.capacity - e.flow));
	        if (df > 0) {
	          
	        	e.addResidualFlowTo(e.v, df);
	     
	          return df;
	         
	        }
	      }
	    }
	    return 0;
	  }	
	 
	  public static void main(String[] args){
		 // long begin = System.currentTimeMillis(); 
		    int rows = 100;
			int cols = 100;
			int V = rows*cols;
			int s = 0;
			int t = V-1;
			FlowNetwork G = new FlowNetwork(rows,cols);
			StdOut.println(G);
			long begin = System.currentTimeMillis(); 
			Dinic maxflow = new Dinic(G,s,t);
			StdOut.println(G);
			
			//print
			StdOut.println("Max flow from " + s + " to " + t);
		    	for (int v = 0; v < G.V(); v++) {
		            for (FlowEdge e : G.adj(v)) {
		                if ((v == e.from()) && e.flow() > 0)
		                    StdOut.println("   " + e);
		            }
		     }
			StdOut.println(maxflow.value());	
			long end = System.currentTimeMillis() - begin; 
			System.out.println("Execution Time：" + end + "ms"); 
	  }
}
