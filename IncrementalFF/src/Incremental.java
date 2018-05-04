import java.util.LinkedList;
import java.util.List;

public class Incremental {
	//can not add arc from source to another
	private boolean[] marked;
	private FlowEdge[] edgeTo1;//last edge on shortest residual s->v path
	private FlowEdge[] edgeTo2;//last edge on shortest residual w->t path
	private double value;
	private List<Integer> AFFECTED_1 = new LinkedList<Integer>();
	private List<Integer> AFFECTED_2 = new LinkedList<Integer>();
	
	//create a network without no augmenting path
	In in = new In("test3.txt");
    FlowNetwork G = new FlowNetwork(in);
    int s = 0, t = G.V()-1;
	
	/*int rows = 200;
	int cols = 200;
	int V = rows*cols;
    int s = 0, t = V-1; 
    FlowNetwork G = new FlowNetwork(rows, cols);*/
    
	FordFulkerson maxflow = new FordFulkerson(G,s,t);
	double initMaxValue = maxflow.value();
	
	public Incremental (int v, int w, double capacity){
		StdOut.println("The original max flow is "+initMaxValue);
		StdOut.println("The original network is : \n" + G);
		double delta = 0.0;
		//add arc v->w
		G.addEdge(new FlowEdge(v, w, capacity));
		
		/*
		
		for (FlowEdge e: G.adj(v)) {
			if(e.from() == v && e.to() == w){
				e.capacity = capacity;
			}	
		}
		
	   */
		//test
		AFFECTED_1 = BBFS(v,s);
		AFFECTED_2 = FBFS(w,t);
		StdOut.println("The affected vertices found by BBFS are "+AFFECTED_1);
		StdOut.println("The affected vertices found by FBFS are "+AFFECTED_2);
		//StdOut.println(hasAugmentingPathInSubnet1(G, AFFECTED_1, s, v));
		//StdOut.println(hasAugmentingPathInSubnet2(G, AFFECTED_2, w, t));
		
		while(hasAugmentingPathInSubnet1(G, AFFECTED_1, s, v)
			 && hasAugmentingPathInSubnet2(G, AFFECTED_2, w, t)){
			//subnet1
            double bottle1 = Double.POSITIVE_INFINITY;
            for (int x = v; x != s; x = edgeTo1[x].other(x)) {
                bottle1 = Math.min(bottle1, edgeTo1[x].residualCapacityTo(x));
                //StdOut.println(x+" : "+ edgeTo1[x]);
            }
 
           // StdOut.println("bottle1 is " + bottle1);
            //subnet2
            double bottle2 = Double.POSITIVE_INFINITY;
            for (int x = t; x != w; x = edgeTo2[x].other(x)) {
                bottle2 = Math.min(bottle2, edgeTo2[x].residualCapacityTo(x));
               //StdOut.println(x + edgeTo2[x]);
            }
            //StdOut.println("bottle2 is " + bottle2);
            //changed arc
            double bottle3 = Double.POSITIVE_INFINITY;
            for(FlowEdge e : G.adj(v)){
            	if (e.from() == v && e.to() == w){
            		bottle3 = Math.min(bottle3, e.residualCapacityTo(w));
            		//StdOut.println("edge is " + e );
            	}
            }
           // StdOut.println("bottle3 is " + bottle3);
            
            double bottle = Math.min(bottle3, Math.min(bottle1, bottle2));
           // StdOut.println("bottle is " + bottle);
            
            //augment subnet1
            for (int x = v; x != s; x = edgeTo1[x].other(x)) {
                edgeTo1[x].addResidualFlowTo(x, bottle); 
            }
            //augment subnet2
            for (int x = t; x != w; x = edgeTo2[x].other(x)) {
                edgeTo2[x].addResidualFlowTo(x, bottle); 
            }
             
            //augment changed arc
            for(FlowEdge e : G.adj(v)){
            	if (e.from() == v && e.to() == w){
            		e.addResidualFlowTo(w, bottle);	
            	}
            }
            delta += bottle;
		}
		StdOut.println("The additional flow is " + delta);
		value = delta + initMaxValue;
    }
	
	//value
	double value(){
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
	
	//BFS
	boolean hasAugmentingPathInSubnet1(FlowNetwork G,List<Integer> AFFECTED_1, int s, int v) {
		edgeTo1 = new FlowEdge[G.V()];
	    marked = new boolean[G.V()];    
	    Queue<Integer> queue = new Queue<Integer>();
	    queue.enqueue(s);
	    marked[s] = true;
	    while (!queue.isEmpty() && !marked[v]) {
	    	int x = queue.dequeue();
	        if(AFFECTED_1.contains(x)){
	        	for (FlowEdge e : G.adj(x)) {
	        		int y = e.other(x);
	  	            if (AFFECTED_1.contains(y) && e.residualCapacityTo(y) > 0) {
	  	            	if (!marked[y]) {
	  	            		edgeTo1[y] = e;
	  	                    marked[y] = true;
	  	                    queue.enqueue(y);
	  	                    //StdOut.println(edgeTo1[y]);
	  	                }
	  	            }
	        	}
	        }
	    }
	    // is there an augmenting path?
	    return marked[v];  
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
	
	//BFS
	boolean hasAugmentingPathInSubnet2(FlowNetwork G,List<Integer> AFFECTED_2, int w, int t) {
		edgeTo2 = new FlowEdge[G.V()];
	    marked = new boolean[G.V()];    
	    Queue<Integer> queue = new Queue<Integer>();
	    queue.enqueue(w);
	    marked[w] = true;
	    while (!queue.isEmpty() && !marked[t]) {
	    	int x = queue.dequeue();
	        if(AFFECTED_2.contains(x)){
	        	for (FlowEdge e : G.adj(x)) {
	        		int y = e.other(x);
	  	            if (AFFECTED_2.contains(y) && e.residualCapacityTo(y) > 0) {
	  	            	if (!marked[y]) {
	  	            		edgeTo2[y] = e;
	  	                    marked[y] = true;
	  	                    queue.enqueue(y);
	  	                   //StdOut.println(edgeTo2[y]);
	  	                }
	  	            }
	        	}
	        }
	    }
	    // is there an augmenting path?
	    return marked[t];  
	}	
	
	//test
	public static void main (String[] args){
		long begin = System.currentTimeMillis();
		Incremental incremental  = new Incremental(2,4,10.0);
		long end = System.currentTimeMillis() - begin; 
		StdOut.println("The updated max flow is " + incremental.value()+" by IncrementalFordFulkerson");
		StdOut.println("The updated network is : \n" + incremental.G);
		//System.out.println("Execution Time：" + end + "ms"); 
	}
	
}
