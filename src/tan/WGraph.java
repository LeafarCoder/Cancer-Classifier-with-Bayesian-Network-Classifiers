package tan;

import java.util.LinkedList;

public class WGraph implements WGraph_Interface{

	
	// **** Attributes ****

	// adjList is a vector of ArrayList's containing Pair<int,double>. The size of the vector will be N
	private double[][] graph;
	// number of nodes
	private int num_nodes;
	// edges keeps all edges sorted by weight (non-increasing order)
	private LinkedList<Edge> edges;
	/* Using LinkedList<Edge> is faster then ArrayList<Edge> in out context.
	 * Insertion is done in O(1)
	 * Search is slower than ArrayList as it has to traverse data structure but this doens't pose
	 * a problem as we only search for the first element (the heaviest node). So in our case Search
	 * is also O(1).
	 * Removal is also O(1).
	 */
	
	// **** Constructor ****
	
	public WGraph(int n){
		// initialize graph
		graph = new double[n][n];
		// fill matrix with -1 to mean empty
		for(int i = 0; i < n; i++)
			for(int j = 0; j < n; j++)
				graph[i][j] = -1.;

		// graph size
		num_nodes = n;
		// list of edges added (sorted by weight)
		edges = new LinkedList<Edge>();
	}
	
	
	// **** Methods ****
	
	// returns the 'edges'
	public LinkedList<Edge> getEdges(){
		return edges;	
	}
	
	// returns the number of nodes in the graph
	public int getNumNodes(){
		return num_nodes;
	}
	
	// returns the graph (matrix of doubles)
	public double[][] getGraph(){
		return graph;
	}
	
	// adds a new edge to the graph
	public void add_edge(int node_1, int node_2, double weight) {
		graph[node_1][node_2] = weight;
		graph[node_2][node_1] = weight;
		
		// insert new link in 'edges' in the proper index (use binary search, O(logN))
		int index = binarySearch(weight);
		// this addition takes constant time, O(1), because 'edges' is a LinkedList (instead of an ArrayList)
		edges.add(index, new Edge(node_1, node_2, weight));
	}

	// removes an edge from the graph
	/*
	 * This operation is added to the implementation because it is requested in the project although it is never used.
	 * That's why a simple O(n) search is implemented. If this operation was frequently used we would have to consider
	 * changing the data structure to LinkedList which offers a constant time remove operation.
	 */
	public void remove_edge(int node_1, int node_2) {
		// delete link 1 -> 2
		graph[node_1][node_2] = -1;
		// delete link 2 -> 1
		graph[node_2][node_1] = -1;
		
		// remove link that contains both node_1 and node_2 (linear search, O(N); can't do binary search here as the 'edges' vector is ordered by weight)
		for(int i = 0; i < edges.size(); i++){
			if((edges.get(i).n1 == node_1 && edges.get(i).n2 == node_2) || (edges.get(i).n1 == node_2 && edges.get(i).n2 == node_1)){
				edges.remove(i);
				break;
			}
		}
	}

	// returns whether or not the graph has an edge linking node 'n1' and node 'n2' 
	public boolean hasEdge(int n1, int n2){
		// checking either 
		return (graph[n1][n2] != -1);
	}
	
	// returns the weight of a node linking n1 to n2
	public double getWeight(int n1, int n2){
		return graph[n1][n2];
	}
	
	/* 
	 * Returns the DGraph (directed graph) that results from applying the Kurskal algorithm (to get the 
	 * Maximal Spanning Tree) to the current WGraph and adding directionality to the edges diverging 
	 * from a single node 'node'.
	 */
	public DGraph MST(int node) {
		// KRUSKAL algorithm
		
		int edges_num = edges.size();
		int nodes_num = num_nodes;
		
		// the edges of the MST will be represented as  WGraph with null weights (= 0);
		WGraph MST_WG = new WGraph(nodes_num);
		/* Note: by construction the 'edges' vector is already sorted in a non-decreasing manner so there is 
		 * no need to perform a O(NlogN) sort. That's the advantage of the 'edges' attribute.
		 */

		// create a new Union-Find-Disjoint set with size 'nodes_num'
		UFDS sets = new UFDS(nodes_num);
		
		/* 'edges_added' counts the number of edges already added to the 'MST_WG' and gives a good 
		 * heuristic to pause the Kruskal algorithm because, when completed, a MST has the general tree property:
		 * #Edges = #Nodes - 1
		 */
		int edges_added = 0;
		for(int i = 0; (i < edges_num) && (edges_added < nodes_num - 1); i++){
			
			// get the representative for each of the nodes (linked by the edge with highest weight)
			Edge edge = edges.get(i);
			// if adding this edge doesn't create cycles then add to the MST_WG (weight = 0, irrelevant):
			if(!sets.sameSet(edge.n1, edge.n2)){
				MST_WG.add_edge(edge.n1, edge.n2, 0);
				// join both nodes into the same set
				sets.unionSet(edge.n1, edge.n2);
				// increment number of edges in the current MST
				edges_added++;
			}
		}
		
		// now that we have the MST in a Weighted Graph form we have to convert it to a DAG with 'node' as the root:
		DGraph MST_DG = new DGraph(nodes_num);

		// BFS for traversing on the tree (could use DFS as well)
		boolean visited[] = new boolean[nodes_num];
        LinkedList<Integer> queue = new LinkedList<Integer>();
 
        visited[node]=true;
        queue.add(node);
 
        // while the "visit" list (queue) is not empty keep visiting nodes:
        while (!queue.isEmpty()){
        	// stores the first element of the queue a removes it from the queue
            int s = queue.pop();
            // for each node linked to 's' (every other node because the graph is complete)
            for(int i = 0; i < MST_WG.getNumNodes(); i++){
            	// if this is the same node then skip
            	if((MST_WG.getWeight(s, i) == -1) || (s == i))continue;
            	// if we haven't visited it yet then add to queue (to visit later):
            	if(!visited[i]){
            		visited[i] = true;
            		queue.add(i);
            		
            		/* As we traverse through the tree we also construct the MST_DG:
            		 * Add oriented edge from 's' to 'i'
            		 */
            		MST_DG.add_edge(s, i);
            	}
            }
        }
		
		return MST_DG;
	}
	
	// returns the index where to insert the new edge with weight 'weight' performing binary search on the 'edges'
	private int binarySearch(double weight){
		int l = 0;
		int r = edges.size() - 1;
		int m = 0;
		
		while(l <= r){
			m =  l + ((r - l) >> 1);		// average between 'l' and 'r' (efficient sum and division)
			if(edges.get(m).w < weight){
				r = m - 1;
			}else{
				l = m + 1;
			}
		}
		
		return l;
	}
	
	// implementation of Union-Find-Disjoint sets for Kruskal algorithm
	class UFDS{
		int[] sets;
		
		UFDS(int n){
			sets = new int[n];
			// each element points to itself (completely disjoint set)
			for(int i = 0; i < n; i++){
				sets[i] = i;
			}
		}
		
		private int findSet(int i){
			if(sets[i] == i){
				return i;
			}else{
				return sets[i] = findSet(sets[i]);
			}
		}
		private void unionSet(int i, int j){
			sets[findSet(i)] = findSet(j);
		}
		
		private boolean sameSet(int i, int j){
			return findSet(i) == findSet(j);
		}
		
	}

}
