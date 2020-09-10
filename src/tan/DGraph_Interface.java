package tan;

import java.util.ArrayList;

public interface DGraph_Interface {
	
	// Methods
	void add_edge(int node_1, int node_2);
	
	void remove_edge(int node_1, int node_2);
	
	ArrayList<Integer> parents(int node);
}
