package tan;

import java.io.Serializable;
import java.util.ArrayList;

public class DGraph implements DGraph_Interface, Serializable{

	// **** Attributes ****

	private static final long serialVersionUID = 1L;
	// representation of the graph
	private ArrayList<ArrayList<Integer>> adjList;
	// Dual graph (represents the same graph with the edges directions inverted)
	// Using this variable, despite having to allocate more memory, facilitates the retrieval of the parents of a node (constant time)
	private ArrayList<ArrayList<Integer>> adjListDual;
	// edges keeps all edges
	private ArrayList<Edge> edges;
	
	
	// **** Constructor ****
	
	public DGraph(int n){
		adjList = new ArrayList<ArrayList<Integer>>();
		adjListDual = new ArrayList<ArrayList<Integer>>();
		// for each node initialize its 'children vector'
		for(int i = 0; i < n; i++){
			adjList.add(new ArrayList<Integer>());
			adjListDual.add(new ArrayList<Integer>());
		}
		edges = new ArrayList<Edge>();
	}
	
	
	// **** Methods ****
	
	// adds an edge to the graph from node 'node_1' to node 'node_2'
	public void add_edge(int node_1, int node_2) {
		adjList.get(node_1).add(node_2);
		// for the dual graph invert the edge direction.
		adjListDual.get(node_2).add(node_1);
		// add edge to vetor
		edges.add(new Edge(node_1, node_2, 0));
	}

	// removes an edge from the graph
	public void remove_edge(int node_1, int node_2) {
		// remove link from 1 to 2
		adjList.get(node_1).remove(node_2);
		adjListDual.get(node_2).remove(node_1);
	}

	// returns vector with all of the 'node's parents
	public ArrayList<Integer> parents(int node) {
		// get the parents of 'node' from Dual Graph
		return adjListDual.get(node);
	}
	
	// add a new node (
	public void add_node(){
		adjList.add(new ArrayList<Integer>());
		adjListDual.add(new ArrayList<Integer>());
	}
	
	// return the 'edges'
	public ArrayList<Edge> getEdges(){
		return edges;	
	}

	// return number of nodes
	public int getNumNodes(){
		return adjList.size();
	}
}
