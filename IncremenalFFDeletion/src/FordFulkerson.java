public class FordFulkerson {
    private final int V;          // number of vertices
    private boolean[] marked;     // marked[v] = true iff s->v path in residual graph
    private FlowEdge[] edgeTo;    // edgeTo[v] = last edge on shortest residual s->v path
    private double value;         // current value of max flow
  
    public FordFulkerson(FlowNetwork G, int s, int t) {
        V = G.V();
        if (s == t)               throw new IllegalArgumentException("Source equals sink"); 
        while (hasAugmentingPath(G, s, t)) {
            // compute bottleneck capacity
            double bottle = Double.POSITIVE_INFINITY;
            for (int v = t; v != s; v = edgeTo[v].other(v)) {
                bottle = Math.min(bottle, edgeTo[v].residualCapacityTo(v));
                //StdOut.println(v+"  : "+ edgeTo[v]);
            }
            
            // augment flow
            for (int v = t; v != s; v = edgeTo[v].other(v)) {
                edgeTo[v].addResidualFlowTo(v, bottle); 
               // StdOut.println(v+"  : "+ edgeTo[v]);
            }

            value += bottle;
        }
    }

    public double value(){
        return value;
    }

    private boolean hasAugmentingPath(FlowNetwork G, int s, int t) {
        edgeTo = new FlowEdge[G.V()];
        marked = new boolean[G.V()];

        // breadth-first search
        Queue<Integer> queue = new Queue<Integer>();
        queue.enqueue(s);
        marked[s] = true;
        while (!queue.isEmpty() && !marked[t]) {
            int v = queue.dequeue();

            for (FlowEdge e : G.adj(v)) {
                int w = e.other(v);

                // if residual capacity from v to w
                if (e.residualCapacityTo(w) > 0) {
                    if (!marked[w]) {
                        edgeTo[w] = e;
                        marked[w] = true;
                        queue.enqueue(w);
                       //StdOut.println(w+":"+ e);
                    }
                }
            }
        }
        // is there an augmenting path?
        return marked[t];  
    }

    //test
    public static void main(String[] args) {
        //create flow network with V vertices and E edges
    	In in = new In("test3.txt");
        FlowNetwork G = new FlowNetwork(in);
        int s = 0, t = G.V()-1;
        //compute maximum flow
        long begin = System.currentTimeMillis(); 
        FordFulkerson maxflow = new FordFulkerson(G, s, t); 
        long end = System.currentTimeMillis() - begin; 
        
        StdOut.println(G);
        StdOut.println("Max flow value = " + maxflow.value() + " (By FordFulkerson)");
		System.out.println("Execution Time：" + end + "ms"); 

    }
    
    
}