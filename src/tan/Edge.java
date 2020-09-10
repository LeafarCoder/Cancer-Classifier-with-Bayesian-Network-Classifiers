package tan;

import java.io.Serializable;

// This class is used to define an Edge
public class Edge implements Serializable{

	private static final long serialVersionUID = 1L;
	
	// node1; node2; weight
	public final int n1, n2;
	public double w;

	public Edge(int n1, int n2, double w){
		this.n1 = n1;
		this.n2 = n2;
		this.w = w;
	}
}