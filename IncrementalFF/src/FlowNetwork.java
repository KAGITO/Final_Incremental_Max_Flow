public class FlowNetwork {
	 private static final String NEWLINE = System.getProperty("line.separator");
	
	 int rows;
	 int cols;
	 
	 int V;
	 int E;
	 Bag<FlowEdge>[] adj;
	 
	 //init a network
	 public FlowNetwork(int V) {
		 if (V < 0) throw new IllegalArgumentException("Number of vertices in a Graph must be nonnegative");
	     this.V = V;
	     this.E = 0;
	     adj = (Bag<FlowEdge>[]) new Bag[V];
	     for (int v = 0; v < V; v++)
	    	 adj[v] = new Bag<FlowEdge>();
	 }
	 
	 public FlowNetwork(int rows, int cols){
		 //int a grid with no egde
		 if (rows < 2) throw new IllegalArgumentException("valid number of rows is >= 2");
	     if (cols < 2) throw new IllegalArgumentException("valid number of columns is >= 2");
	     this.rows = rows;
	     this.cols = cols;
	     this.V = rows*cols;
	     this.E = 0;
	     adj = (Bag<FlowEdge>[]) new Bag[rows*cols];
	     for (int v = 0; v < V; v++)
	    	 adj[v] = new Bag<FlowEdge>();
		 
		 //generate a grid flow
		 int[][] grid = new int[rows][cols];
		 int v;
		 int w;
		 double capacity;
		 
		 for(int i = 0; i < rows; i++){
			 for(int j = 0; j< cols; j++){
				//cap and source node 
				grid[i][j] = i*cols + j;
				v = grid[i][j];
				
				//right node
				if(j + 1 < cols){
					grid[i][j+1] = i*cols + (j+1);
					w = grid[i][j+1];
					capacity = StdRandom.uniform(100);
					addEdge(new FlowEdge(v, w, capacity));
				}
				
				//down node
				if(i+1 < rows){
					grid[i+1][j] = (i+1)*cols + j;
					w = grid[i+1][j];
					capacity = StdRandom.uniform(100);
					addEdge(new FlowEdge(v, w, capacity));
				}
			 } 
		 } 
	 }
	 
	 //read network from file
	 public FlowNetwork(In in) {
		 this(in.readInt());
	     int E = in.readInt();
	     if (E < 0) throw new IllegalArgumentException("number of edges must be nonnegative");
	     for (int i = 0; i < E; i++) {
	    	 int v = in.readInt();
	         int w = in.readInt();
	         double capacity = in.readDouble();
	         addEdge(new FlowEdge(v, w, capacity));
	     }
	 }
	 
	 //constructor of V,E
	 public int V() {
		 return V;
	 }

	 public int E() {
		 return E;
	 }
	 //the method for adding edge
	 public void addEdge(FlowEdge e) {
		 int v = e.from();
	     int w = e.to();
	     adj[v].add(e);
	     adj[w].add(e);
	     E++;
	 }
	 
	 //
	 
	 public Iterable<FlowEdge> adj(int v) {
		 return adj[v];
	 }
	 
	 //return list of all edges
	 public Iterable<FlowEdge> edges() {
		 Bag<FlowEdge> list = new Bag<FlowEdge>();
	     for (int v = 0; v < V; v++)
	    	 for (FlowEdge e : adj(v)) {
	    		 if (e.to() != v)
	    			 list.add(e);
	         }
	     return list;
	 }
	  
	 //print into string
	 public String toString() {
		 StringBuilder s = new StringBuilder();
		 s.append(V + " " + E + NEWLINE);
		 for (int v = 0; v < V; v++) {
			 s.append(v + ":  ");
			 for (FlowEdge e : adj[v]) {
				 if (e.to() != v) s.append(e + "  ");
			 }
			 s.append(NEWLINE);
		 }
		 return s.toString();
	 }

	 //test
	 public static void main(String[] args) {
		 FlowNetwork G = new FlowNetwork(3,3);
		 StdOut.println(G);
	 }
 
}
