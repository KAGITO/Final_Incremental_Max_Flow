import java.util.ArrayList;
import java.util.List;

public class FlowNetwork {
	private static final String NEWLINE = System.getProperty("line.separator");
	
	int rows;
	int cols;
	int V;
	int E;
	List<FlowEdge>[] adj;

	//init a network
	public FlowNetwork(int V) {
		if (V < 0) throw new IllegalArgumentException("Number of vertices in a Graph must be nonnegative");
	    this.V = V;
	    this.E = 0;
	    adj = new List[V];;
	    for (int v = 0; v < V; v++)
	    	adj[v] = new ArrayList<>();
	}
	
	public FlowNetwork (int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		this.V = rows*cols;
		this.E = 0;
	   //this.adj = createNetwork(V);
	  
		adj = new List[rows*cols];
			for (int i = 0; i < V; i++)
				adj[i] = new ArrayList<>();
		
	    int[][] grid = new int[rows][cols];
		int u;
		int v;
		double capacity;
		 
		for(int i = 0; i < rows; i++){
			for(int j = 0; j< cols; j++){
				//cap and source node 
				grid[i][j] = i*cols + j;
				u = grid[i][j];
				
				//right node
				if(j + 1 < cols){
					grid[i][j+1] = i*cols + (j+1);
					v = grid[i][j+1];
					capacity = StdRandom.uniform(100);
					addEdge(new FlowEdge(u, v, capacity));
				}
				
				//down node
				if(i+1 < rows){
					grid[i+1][j] = (i+1)*cols + j;
					v = grid[i+1][j];
					capacity = StdRandom.uniform(100);
					addEdge(new FlowEdge(u, v, capacity));
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
	 
	public int V (){
		return V;
	}
	
	public int E (){
		return E;
	}

	//the method for adding edge
	public void addEdge(FlowEdge e) {
		int u = e.from();
	    int v = e.to();
	    adj[u].add(e);
	    adj[v].add(e);
	    E++;
	}
	 
	//return list of all edges
	public Iterable<FlowEdge> adj(int v) {
		return adj[v];
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
	public static void main(String[] args){
		FlowNetwork G= new FlowNetwork(3,3);
		StdOut.println(G);
	}	
}
