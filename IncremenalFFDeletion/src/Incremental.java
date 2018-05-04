import java.util.LinkedList;
import java.util.List;

public class Incremental {
	//
	private boolean[] marked;
	private FlowEdge[] edgeTo1;//used in reverse BFS from t to w
	private FlowEdge[] edgeTo2;//used in reverse BFS from v to s
	private FlowEdge[] edgeTo3;
	private List<Integer> AFFECTED_1 = new LinkedList<Integer>();
	private List<Integer> AFFECTED_2 = new LinkedList<Integer>();
	private double value;  
	
	//
	In in = new In("test3.txt");
    FlowNetwork G = new FlowNetwork(in);
    int s = 0, t = G.V()-1;
    
    /*int rows = 3;
	int cols = 3;
	int V = rows*cols;
	int s = 0;
	int t = V-1;
	FlowNetwork G = new FlowNetwork(rows,cols);*/
	
	FordFulkerson maxflow = new FordFulkerson(G,s,t);
	double initMaxFlow = maxflow.value();
	
	//
	public Incremental (int v, int w, double capacity){	
		StdOut.println("The original max flow is "+initMaxFlow);
		StdOut.println("The original network is : \n"+G);
		
		
		double alpha         = 0.0;
		double delta         = 0.0;
		double decreasedFlow = 0.0;	
		//change capacity for an arc (decrease here)
		for (FlowEdge e: G.adj(v)) {
			if(e.from() == v && e.to() == w){
				e.capacity = capacity;
				alpha = e.flow()-e.capacity();
				decreasedFlow = alpha;
			}	
		}
		StdOut.println("The excess flow needs to be subtracted is " + alpha);
		
		//print something
		AFFECTED_1 = FBFS(w,t);
		AFFECTED_2 = BBFS(v,s);
		StdOut.println("The affected vertices found by BBFS are "+AFFECTED_1);
		StdOut.println("The affected vertices found by FBFS are "+AFFECTED_2);
		//StdOut.println(hasDecreasingPathInSubnet1(G,AFFECTED_1,t,w));
		//StdOut.println(hasDecreasingPathInSubnet2(G,AFFECTED_2,v,s));

		//subtract the flow from the network
		while (hasDecreasingPathInSubnet1(G,AFFECTED_1,t,w)
				&& hasDecreasingPathInSubnet2(G,AFFECTED_2,v,s)
				&& alpha > 0) {
			// compute bottleneck capacity
		    
            double bottle1 = Double.POSITIVE_INFINITY;
	        for (int x = w; x != t; x = edgeTo1[x].other(x)) {
	        	bottle1 = Math.min(bottle1, edgeTo1[x].flow());
	            //StdOut.println(x + " : "+ edgeTo1[x]);
	        }
	        
	        double bottle2 = Double.POSITIVE_INFINITY;
	        for (int x = s; x != v; x = edgeTo2[x].other(x)) {
	        	bottle2 = Math.min(bottle2, edgeTo2[x].flow());
	            //StdOut.println(x + " : "+ edgeTo1[x]);
	        }
	        
            double bottle3 = Double.POSITIVE_INFINITY;
            for(FlowEdge e : G.adj(v)){
            	if (e.from() == v && e.to() == w){
            		bottle3 = Math.min(bottle3, e.flow()-e.capacity());
            		//StdOut.println("edge is " + e );
            	}
            }
           // StdOut.println("bottle3 is " + bottle3);
            
            double bottle = Math.min(bottle3,Math.min(bottle1, bottle2));
	        // decrease flow
	        for (int x = w; x != t; x = edgeTo1[x].other(x)) {
	        	edgeTo1[x].addResidualFlowTo(x, bottle); 
	            //StdOut.println(x + " : "+ edgeTo1[x]);
	        }
	    
	        for (int x = s; x != v; x = edgeTo2[x].other(x)) {
	        	edgeTo2[x].addResidualFlowTo(x, bottle); 
	            //StdOut.println(x + " : "+ edgeTo1[x]);
	        }
	        
	        for(FlowEdge e : G.adj(v)){
            	if (e.from() == v && e.to() == w){
            		e.flow = e.flow()-bottle;
            		alpha = e.flow()-e.capacity();
            	}
            } 
		}
		
		//augment flow
		//StdOut.println(G);
		//StdOut.println(hasAugmentingPath(G, s, t));
		while (hasAugmentingPath(G, s, t)) {
	            // compute bottleneck capacity
			double bottle = Double.POSITIVE_INFINITY;
	        for (int x = t; x != s; x = edgeTo3[x].other(x)) {
	        	bottle = Math.min(bottle, edgeTo3[x].residualCapacityTo(x));
	        }
	            
	            // augment flow
	        for (int x = t; x != s; x = edgeTo3[x].other(x)) {
	            edgeTo3[x].addResidualFlowTo(x, bottle); 
	               // StdOut.println(v+"  : "+ edgeTo[v]);
	        }
	        delta += bottle;
	       StdOut.println("The additional flow is "+bottle);
		}
		
		value = initMaxFlow - decreasedFlow + delta;
		//	
	}
	//constructor of value
	public double value(){
		return value;
	}
	//FBFS
	List<Integer> FBFS (int w, int t){
		marked = new boolean[G.V];
		Queue<Integer> queue = new Queue<Integer>();
		List<Integer> AFFECTED_1  = new LinkedList<Integer>();	
		queue.enqueue(w);
		AFFECTED_1.add(w);
		marked[w] = true;
		while (!queue.isEmpty()){
			int x = queue.dequeue();
		    if(x == t)
		    	break;
		    for(FlowEdge e : G.adj(x)){
		    	int y = e.other(x);
		    	//flow should be greater than 0 that we can subtract
		    	if(y == e.to() && e.flow()>0 && !marked[y]){
		    		queue.enqueue(y);
		    		AFFECTED_1.add(y);
		    		marked[y] = true;
		    	}
		    }
		 }
		 return AFFECTED_1;
	}
	
	//BFS in reversed subnet1
	boolean hasDecreasingPathInSubnet1(FlowNetwork G,List<Integer> AFFECTED_1, int t, int w) {
		edgeTo1 = new FlowEdge[G.V()];
	    marked = new boolean[G.V()];    
	    Queue<Integer> queue = new Queue<Integer>();
	    queue.enqueue(t);
	    marked[t] = true;
	    while (!queue.isEmpty() && !marked[w]) {
	    	int x = queue.dequeue();
	        if(AFFECTED_1.contains(x)){
	        	for (FlowEdge e : G.adj(x)) {
	        		int y = e.other(x);
	  	            if (AFFECTED_1.contains(y) && e.flow() > 0) {
	  	            	if (!marked[y]) {
	  	            		edgeTo1[y] = e;
	  	                    marked[y] = true;
	  	                    queue.enqueue(y);
	  	                   // StdOut.println(y+" : "+edgeTo1[y]);
	  	                }
	  	            }
	        	}
	        }
	    }
	    //is there an augmenting path?
	    return marked[w];  
	}	
	
	//BBFS
	List<Integer> BBFS (int v, int s){
		marked = new boolean[G.V];
		Queue<Integer> queue = new Queue<Integer>();
		List<Integer> AFFECTED_2 = new LinkedList<Integer>();
		
	    queue.enqueue(v);
	    AFFECTED_2.add(v);
	    marked[v] = true;
	    
	    while (!queue.isEmpty()){
	    	int x = queue.dequeue();
	    	if(x == s)
	    		break;
	    	for(FlowEdge e : G.adj(x)){
	    		int y = e.other(x);
	    		//flow should be greater than 0 that we can subtract
	    		if( y == e.from() && e.flow()>0 && !marked[y]){
	    			queue.enqueue(y);
	    			AFFECTED_2.add(y);
	    			marked[y] = true;
	    		}
	    	}
	    }
	    return AFFECTED_2;
	}
	
	//BFS in reversed subnet2
	boolean hasDecreasingPathInSubnet2(FlowNetwork G,List<Integer> AFFECTED_2, int v, int s) {
		edgeTo2 = new FlowEdge[G.V()];
	    marked = new boolean[G.V()];    
	    Queue<Integer> queue = new Queue<Integer>();
	    queue.enqueue(v);
	    marked[v] = true;
	    while (!queue.isEmpty() && !marked[s]) {
	    	int x = queue.dequeue();
	        if(AFFECTED_2.contains(x)){
	        	for (FlowEdge e : G.adj(x)) {
	        		int y = e.other(x);
	  	            if (AFFECTED_2.contains(y) && e.flow() > 0) {
	  	            	if (!marked[y]) {
	  	            		edgeTo2[y] = e;
	  	                    marked[y] = true;
	  	                    queue.enqueue(y);
	  	                   // StdOut.println(y+" : "+edgeTo2[y]);
	  	                }
	  	            }
	        	}
	        }
	    }
	    return marked[s];  
	}	
	
	//bfs for whole network
	boolean hasAugmentingPath(FlowNetwork G, int s, int t) {
		edgeTo3 = new FlowEdge[G.V()];
	    marked = new boolean[G.V()];
	    Queue<Integer> queue = new Queue<Integer>();
	    queue.enqueue(s);
	    marked[s] = true;
	    while (!queue.isEmpty() && !marked[t]) {
	    	int v = queue.dequeue();
	        for (FlowEdge e : G.adj(v)) {
	        	int w = e.other(v);
	            if (e.residualCapacityTo(w) > 0) {
	            	if (!marked[w]) {
	            		edgeTo3[w] = e;
	                    marked[w] = true;
	                    queue.enqueue(w);
	                }
	            }
	        }
	    }
	    return marked[t];  
	}

	//test
	
	public static void main (String[] args){
		Incremental incremental  = new Incremental(2,4,0);
		StdOut.println("The updated max flow is " + incremental.value());
		StdOut.println("The updated network is : \n" +incremental.G);	
	}
	
}
