package tan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

public class BN implements BN_Interface, Serializable{

	// **** Attributes ****

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// the direct graph of the Bayes Network
	private DGraph graph;

	// Variables theta that stores the probabilities.
	// |R ^ (n1*n2*...*nx) is isomorphic to |R ^ (prod(n1,n2,...,nx)) so a vector for each node is all we need
	private ArrayList<double[]> theta;
	private Sample sample;
	private double S;
	

	// **** Constructor ****

	// Further to the asked parameters, the constructor also receives a String[] 'h' which has the names of each variable.
	public BN(DGraph dg, Sample sample, double S){
		theta = new ArrayList<double[]>();

		

		graph = dg;
		int num_nodes = graph.getNumNodes();

		// add node C (class node); last node (index = #nodes - 1)
		graph.add_node();

		long start = System.nanoTime();


		// add edge between the class node (C) and every other node (Xi)
		for(int i = 0; i < num_nodes; i++){
			// class node index: #variables
			graph.add_edge(num_nodes, i);
		}
		System.out.println(" Add class: " + (System.nanoTime() - start)/1000000000. + " seconds");

		this.sample = sample;
		this.S = S;

		int[] max = sample.getMaxValues();
		// Allocate space for each vector of theta
		for(int i = 0; i < graph.getNumNodes(); i++){
			ArrayList<Integer> p = graph.parents(i);
			// 'prod' is the production of all dimensions
			int prod = (max[i] + 1);
			// for each parent...
			for (int j = 0; j < p.size(); j++) {
				prod *= (max[p.get(j)] + 1);
			}
			theta.add(new double[prod]);
		}


		// Calculate 'theta' variable values:
		start = System.nanoTime();
		calculateTheta();
		System.out.println(" Add class: " + (System.nanoTime() - start)/1000000000. + " seconds");
		// for (int i = 0; i < theta.size(); i++) {
		//	 System.out.println(i+ ": "+Arrays.toString(theta.get(i)));
		// }
	}


	// **** Methods ****

	// returns the probability of vector 'vec' occuring using the Bayes Network
	public double prob(BN bayes, int[] vec) {

		double prob = 1;
		int[] max = bayes.sample.getMaxValues();
		for(int i = 0; i < bayes.graph.getNumNodes(); i++){
			ArrayList<Integer> p = bayes.graph.parents(i);

			// calculate index to put prob in theta:
			int idx = vec[i];
			int prod = (max[i] + 1);
			for(int j = 0; j < p.size(); j++){
				idx += vec[p.get(j)]*prod;
				prod *= (max[p.get(j)] + 1);
			}


			prob *= bayes.theta.get(i)[idx];
		}
		return prob;
	}

	public DGraph getGraph(){
		return graph;
	}

	public Sample getSample(){
		return sample;
	}

	public ArrayList<double[]> getTheta(){
		return theta;
	}
	
	// Calculates the states for the variable theta
	private void calculateTheta(){

		int[] max = sample.getMaxValues();
		// For each node in the graph:
		for(int i = 0; i < graph.getNumNodes(); i++){
			// get parents of node i:
			ArrayList<Integer> parents = graph.parents(i);

			LinkedList<Integer> vars = new LinkedList<Integer>();
			vars.add(i);
			// for each value of this node
			for(int j = 0; j <= max[i]; j++){
				LinkedList<Integer> vals = new LinkedList<Integer>();
				vals.add(j);
				calculateRecursively(new ArrayList<>(parents), new LinkedList<>(vars), new LinkedList<>(vals));
			}

		}
	}

	private void calculateRecursively(ArrayList<Integer> parents, LinkedList<Integer> vars, LinkedList<Integer> values){
		int[] max = sample.getMaxValues();
		// if there are parents remaining
		if(!parents.isEmpty()){
			int p = parents.get(0);
			parents.remove(0);
			// add node 'p' to 'vars'
			vars.add(p);
			// for each of the parent's values (from 0 to max[p], i.e, the maximum value for node 'p')
			for(int j = 0; j <= max[p]; j++){
				values.add(j);
				// recursively calculate the probabilities:
				calculateRecursively(new ArrayList<>(parents), new LinkedList<>(vars), new LinkedList<>(values));
				// remove last added value to use another one
				values.removeLast();
			}
			// else, if no parents remain
		}else{
			// calculate index to put prob in theta:
			int prod = 1;
			int idx = 0;
			for(int k = 0; k < vars.size(); k++){
				idx += values.get(k)*prod;
				prod *= (max[vars.get(k)] + 1);
			}

			// calculate probability (node and parents)
			int[] vars_vec = new int[vars.size()];
			int[] values_vec = new int[values.size()];
			for(int i = 0; i < vars.size(); i++){
				vars_vec[i] = vars.get(i);
				values_vec[i] = values.get(i);
			}

			double cnt1 = sample.count(vars_vec, values_vec);
			// removes the main node but store it
			int node_idx = vars.get(0);
			vars.remove(0);
			values.remove(0);

			// recalculate (only with the parents now)
			vars_vec = new int[vars.size()];
			values_vec = new int[values.size()];
			for(int i = 0; i < vars.size(); i++){
				vars_vec[i] = vars.get(i);
				values_vec[i] = values.get(i);
			}
			double cnt2 = sample.count(vars_vec, values_vec);
			// theta formula
			double prob = (cnt1 + S) / (cnt2 + S*(max[node_idx] + 1));

			// set prob at idx
			theta.get(node_idx)[idx] = prob;
		}
	}
}
