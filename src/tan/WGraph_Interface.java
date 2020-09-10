package tan;

public interface WGraph_Interface {
	
	// Methods
	void add_edge(int node_1, int node_2, double weight);
	
	void remove_edge(int node_1, int node_2);
	
	DGraph MST(int node);
}