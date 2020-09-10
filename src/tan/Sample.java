package tan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/*
 * This class implements a data set holder (data sample)
 */
public class Sample implements Sample_Interface, Serializable{

	// **** Attributes ****

	private static final long serialVersionUID = 1L;
	// the sample itself is made as an ArrayList of integer static vectors
	private ArrayList<int[]> sample;
	// this vector keeps track of the largest value for each variable
	private int[] maxValue;
	// size of each data set (including class)
	private int variableNum;
	// names for Variables
	private String[] headers;
	// names for the Class Categories
	private String[] classNames;
	// domain limits of each variable:
	// 		var_domain[i][0] is the minimum value for variable i
	//		var_domain[i][1] is the maximum value for variable i
	private int[][] var_domain;
	
	
	// **** Constructor ****
	// receives an integer 'n' for the size of each sample set (including Class node)
	public Sample(int n){
		headers = new String[n];
		var_domain = new int[n][2];
		for (int i = 0; i < headers.length; i++) {
			var_domain[i][0] = 0;
			var_domain[i][1] = 0;
		}
		sample = new ArrayList<int[]>();
		maxValue = new int[n];
		variableNum = n;
		
		for (int i = 0; i < headers.length; i++) {
			this.headers[i] = (i == headers.length - 1) ? "Class" : ("Var. "+(i+1));
		}
		
	}
	
	
	// **** Methods ****
	
	// this method adds a data set to the sample (if the size matches)
	// return true if adding was successful; return false otherwise 
	public boolean add(int[] vec){
		if(variableNum != vec.length){
			System.err.println("TAN package :: Sample :: add : Vector size doens't match already existing vectors");
			return false;
		}
		// add vector to sample
		sample.add(vec);
		// update 'maxValue'
		for(int i = 0; i < variableNum; i++){
			if(maxValue[i] < vec[i]){
				maxValue[i] = vec[i];
				var_domain[i][1] = vec[i];
				
				// if in class
				if(i == variableNum - 1){
					classNames = new String[vec[i] + 1];

					for (int j = 0; j <= vec[i]; j++) {
						classNames[j] = "" + j;
					}
				}
			}
		}
		return true;
	}
	
	// returns the number of data sets (size of the sample)
	public int length(){
		return sample.size();
	}
	
	// returns the number of variables in a data set (excluding class)
	public int getNumFactors(){
		return variableNum - 1;
	}
	
	// returns the maxValue vector
	public int[] getMaxValues(){
		return maxValue;
	}
	
	// returns the number of classes (domain size)
	public int numberOfClasses(){
		return maxValue[variableNum-1] + 1;
	}
	
	// returns the data set element required (as index 'pos')
	public int[] element(int pos){
		return sample.get(pos);		
	}
	
	public void setHeaders(String[] h){
		for(int i = 0; i < h.length; i++){
			headers[i] = h[i];
		}
	}

	public void setClassNames(String[] cn){
		classNames = new String[cn.length];
		for(int i = 0; i < cn.length; i++){
			classNames[i] = cn[i];
		}
	}
	
	public String[] getClassNames(){
		return classNames;
	}
	
	public String[] getHeaders(){
		return headers;
	}

	public int[][] getVarDomain(){
		return var_domain;
	}
	
	// returns the count of occurrences of the values 'val' in the variables 'var' in the whole sample
	public int count(int[] vars, int[] values){
		int ans = 0;
		
		for(int i = 0; i < sample.size(); i++){
			boolean match = true;
			for(int j = 0; j < vars.length; j++){
				if(sample.get(i)[vars[j]] != values[j]){
					match = false;
					break;
				}
			}
			if(match)ans++;
		}
		
		return ans;
	}
	
	// returns the text representation of the Sample set with the following template:
	/*
	 *  Sample set (n samples):
	 *	   Sample 1: [x11,x12,x13,...]
	 *	   Sample 2: [x21,x22,x23,...]
	 *	   ...	   
	 *     Sample n: [xn1,xn2,xn3,...] 
	 */
	@Override
	public String toString() {
		// header message
		String ans = "Sample set ("+this.length()+" samples):\n";
		// for each data set
		for(int i = 0; i < this.length(); i++){
			ans += "   Sample "+(i+1)+":	"+Arrays.toString(this.element(i))+"\n";
		}
		return ans;
	}
}
