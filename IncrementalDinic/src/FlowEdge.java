public class FlowEdge {
	 int u;
	 int v;
	 int reverse;
	 double capacity;
	 double flow;
	
	public FlowEdge (int u, int v, double capacity){
		this.u = u;
		this.v = v;
		this.capacity =  capacity;
		this.flow = 0.0;
		//this.reverse = reverse;
	}
	
	public FlowEdge(int u, int v, int reverse, double capacity, double flow) {
	    if (u < 0) throw new IllegalArgumentException("vertex index must be a non-negative integer");
	    if (v < 0) throw new IllegalArgumentException("vertex index must be a non-negative integer");
	    if (!(capacity >= 0.0))  throw new IllegalArgumentException("edge capacity must be non-negative");
	    if (!(flow <= capacity)) throw new IllegalArgumentException("flow exceeds capacity");
	    if (!(flow >= 0.0))      throw new IllegalArgumentException("flow must be non-negative");
	    this.u         = u;
	    this.v         = v;  
	    this.capacity  = capacity;
	    this.flow      = flow;
	}

	public FlowEdge(FlowEdge e) {
	    this.u         = e.u;
	    this.v         = e.v;
	    this.reverse   = e.reverse;
	    this.capacity  = e.capacity;
	    this.flow      = e.flow;
	 }

	 public int from() {
	     return u;
	 }  

	 public int to() {
	     return v;
	 }  

	 public int reverse() {
		 return reverse;
	 }
	 
	 public double capacity() {
	     return capacity;
	 }
	    
	 public double flow() {
	     return flow;
	 }
	    
	 public int other(int vertex) {
		 if      (vertex == u) return v;
	     else if (vertex == v) return u;
	     else throw new IllegalArgumentException("invalid endpoint");
	 }
	
	 public void addResidualFlowTo(int vertex, double delta) {
	     if (!(delta >= 0.0)) throw new IllegalArgumentException("Delta must be nonnegative");

	     if      (vertex == u) flow -= delta;           // backward edge
	     else if (vertex == v) flow += delta;           // forward edge
	     else throw new IllegalArgumentException("invalid endpoint");
	        
	     if (!(flow >= 0.0))      throw new IllegalArgumentException("Flow is negative");
	     if (!(flow <= capacity)) throw new IllegalArgumentException("Flow exceeds capacity");
	 }

	 
	 public String toString() {
	     return u + "->" + v + " " + flow + "/" + capacity;
	 }
}