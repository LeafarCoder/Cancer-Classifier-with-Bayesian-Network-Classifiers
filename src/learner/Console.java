package learner;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;

public class Console {

	private String currentPath = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
	private Map<String, Integer> code_map;
	private String[] code_map_inv;
	private ArrayList<String[]> commands;
	private String fileName = "";
	
	public Console(){
		commands = new ArrayList<String[]>();
		code_map = new HashMap<String, Integer>();
		
		commands.add(new String[]{"cd","Select directory"});
		commands.add(new String[]{"cd..", "Go back in directory"});
		commands.add(new String[]{"cd/", "Go to root directory"});
		commands.add(new String[]{"set_path", "Defines path"});
		commands.add(new String[]{"help", "Displays help"});
		commands.add(new String[]{"learn", "Creates the optimal classifier based on the given data"});
		commands.add(new String[]{"save", "Saves the learned classifier"});
		commands.add(new String[]{"view_graph", "Opens a new window with the graph from BN"});
		commands.add(new String[]{"dir", "Displays the content of the current directory"});
		commands.add(new String[]{"upload_sample", "Uploads a sample from a file in the current directory"});
		commands.add(new String[]{"goto_pref_path", "Changes current directory to the preferable path"});
		commands.add(new String[]{"cls", "Clears screen"});
		
		
		// sort alphabetically
		commands.sort(new Comparator<String[]>() {
			@Override
			public int compare(String[] S1, String[] S2){
				return S1[0].compareTo(S2[0]);
			}
		});
		
		code_map_inv = new String[commands.size()];
		
		// create map to error_codes
		for (int i = 0; i < commands.size(); i++) {
			code_map_inv[i] = commands.get(i)[0];
			code_map.put(commands.get(i)[0], i);
		}
		
	}
	
	public Map<String,Integer> getCodeMap(){
		return code_map;
	}
	
	public String[] getCodeMapInv(){
		return code_map_inv;
	}
	
	public String getFileName(){
		return fileName;
	}
	
	public String getCurrentPath(){
		return currentPath;
	}
	
	public void setCurrentPath(String str){
		currentPath = str;
	}
	
	public boolean isValidCommand(String command){
		for (int i = 0; i < commands.size(); i++) {
			if(commands.get(i)[0] == command){
				return true;
			}
		}
		return false;
	}
	
	public void setFileName(String str){
		fileName = str;
	}
	
	public String giveHelp(){
		String ans = "\n   Commands list:\n\n";
		int max = 0;
		
		for(int i = 0; i < commands.size(); i++){
			if(max < commands.get(i)[0].length())max = commands.get(i)[0].length();
		}
		System.out.println("MAX: "+max);
		int shortest_space = (int) Math.ceil(max / 8.) + 1;
		System.out.println("sH "+shortest_space);
		for (int i = 0; i < commands.size(); i++) {
			int tab_times = shortest_space - (commands.get(i)[0].length() + 3) / 8;
			System.out.println(commands.get(i)[0] + ": "+tab_times);
			ans += "   " + commands.get(i)[0] + new String(new char[tab_times]).replace("\0", "\t") + commands.get(i)[1] + "\n";
		}
		
		return ans;
		
	}
	
	public class Triple<name, id_code, help>{
		
		private String name;
		private String help;
		private Integer id_code;
		
		public Triple(String name, Integer id_code, String help){
			this.name = name;
			this.help = help;
			this.id_code = id_code;
		}
		
		public String getName(){return name;}
		public String getHelp(){return help;}
		public int getIdCode(){return id_code;}
	}
	
	public String parseQuery(String query){
		String[] q = query.split(" ");
		
		// process queries
		if(q.length == 1){
			if(code_map.containsKey(q[0])){
				
				switch (q[0]) {
				case "cd..":
					// Go back in directory
					String[] apart = currentPath.split("\\\\");
					apart[apart.length-1] = "";
					currentPath = String.join("\\", apart);
					currentPath = currentPath.substring(0, currentPath.length() - 1);
					break;

				case "dir":
					return code_map.get(q[0]) + ".0";


				}
				
				return "" + code_map.get(q[0]);
			}else{
				return "" + -1;
			}

		}else if(q.length > 1){
			// if one extra parameter:
			switch (q[0]) {
			case "cd":
				String folder = "";
				for(int i = 1; i < q.length; i++){folder += q[i]+" ";}
				folder = folder.substring(0, folder.length() - 1);
				String newPath = currentPath + "\\" + folder;
				if(new File(newPath).isDirectory()){
					currentPath = newPath;
					return code_map.get(q[0]) + ".1";
				}else{
					return code_map.get(q[0]) + ".2";
				}

			case "dir":
				switch (q[1]) {
				case "-a":
					return code_map.get(q[0]) + ".0";
					
				case "-f":
					return code_map.get(q[0]) + ".1";

				case "-d":
					return code_map.get(q[0]) + ".2";

				default:
					break;
				}
			
			case "upload_sample":
				fileName = q[1];
				return code_map.get(q[0]) + "";
				
			case "save":
				fileName = q[1];
				return code_map.get(q[0]) + "";
				
			default:
				break;
			}
			
		}
		return "" + -1;
		
	}
}
