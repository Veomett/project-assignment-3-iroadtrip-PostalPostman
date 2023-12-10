import java.io.*;
import java.util.Scanner;
import java.util.List;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Collections;
import java.util.PriorityQueue;

/* WARNING WARNING WARNING
 * FIRST PROMPT OF COUNTRY NAME MIGHT NOT APPEAR ON FIRST STARTUP. TYPE THE COUNTRY YOU WANT TO TYPE ANYWAY
 * ONCE YOU HIT ENTER THE PROMPT WILL APPEAR AND YOUR RESPONSE WILL STILL BE RECORDED AND STATED AS RECORDED, AND YOU WILL BE PROMPTED AGAIN AFTER
 * I DON'T KNOW WHY THIS HAPPENS, IT WASN'T HAPPENING EARLIER. PROGRAM STILL FUNCTIONS AS INTENDED OTHER WISE
 * ALSO PROMPT IS CASE SPECIFIC FYI.
 */



public class IRoadTrip {
	
	public class Node implements Comparable<Node>{
		String country;
		int dist;
		
		Node(String c, int d){
			country = c;
			d = dist;
		}
		
		public int compareTo(Node other) {
			return Integer.compare(this.dist, other.dist);
		}
	}
	public class Name{
		private HashMap<String, String> c_Ids;
		public Name(String[] state_name) {
			c_Ids = new HashMap<>();
			parseNames(state_name);
		}
		
		public String getId(String name){
			return c_Ids.get(name);
		}
		/*
		 * parses the names of the states
		 *@param state_name state names read from csv
		 */
		private void parseNames( String[] state_name){
			String temp;
			for(int i = 1; i < state_name.length; i++){
				temp = state_name[i].trim();
				String[] tempArr = temp.split("	");
				if(temp.contains("2020-12-31")){
					c_Ids.put(tempArr[2].replaceAll(" \\(.*?\\)", ""), tempArr[1]);
				}
			}
		}
	}
	public class Graph{
		private HashMap<String, HashMap<String, Integer>> graph;
		public Graph(String[] border, String[] fileName) throws IOException{
			graph = new HashMap<>();
			String[] borders = border;
			parseBorders(borders, fileName);
		}
		public HashMap<String, HashMap<String, Integer>> getGraph() {
			return graph;
		}
		/*
		 * parses the borders
		 *@param lineArr feed from border file
		 *@fileName names of fliles passed through
		 */
		private void parseBorders(String[] lineArr, String[] fileName) throws IOException {
		Scanner capScan = new Scanner(new File(fileName[1])); //initialization
		String capDistArr = "";
		while(capScan.hasNext()){
			capDistArr += (capScan.nextLine() + "\n");
		}
		//System.out.print(capDistArr);
		CapDist capdists = new CapDist(capDistArr.split("\n"));
		Scanner nameScan = new Scanner(new File(fileName[2]));
		String nameArr = "";
		while(nameScan.hasNext()){
			nameArr += (nameScan.nextLine() + "\n");
		}
		Name name = new Name(nameArr.split("\n"));

		for (String line : lineArr) { //parsing
			String[] parts = line.split(" = ");
			String country = parts[0];
			String borders;
			if (parts.length > 1)
				borders = parts[1];
			else
				borders = null;
			List<String> adjs = getBorders(borders);
			if (adjs != null) {
				HashMap<String, Integer> dest = new HashMap<>(); 
				for (int i = 0; i < adjs.size(); i++) {
					HashMap<String, String> theExcepts = theExcepts();
					if (theExcepts.get(country) != null) //tricky handling for 
						country = theExcepts.get(country);
					if (theExcepts.get(adjs.get(i)) != null)
						adjs.set(i, theExcepts.get(adjs.get(i)));
					String c_Id = name.getId(country);
					String adjId = name.getId(adjs.get(i));
					int dist = capdists.getDistance(c_Id, adjId);
					if (dist != -1)
						dest.put(adjs.get(i), capdists.getDistance(c_Id, adjId));
					}
					graph.put(country, dest);
				}
			}
		}
		/*
		 * regexing the borders
		 *@param input
		 *@return cleaned up borders
		 */
	private List<String> getBorders(String input) {
		List<String> borderList;
		if (input == null) {
			borderList = null;
		} else {
			List<String> states = new ArrayList<>();
			borderList = states;
			Pattern pattern = Pattern.compile("[A-Za-z\\s\\(\\)]+(?=\\s\\d+[,.]?\\d*\\s*km)");
			Matcher matcher = pattern.matcher(input);
			while (matcher.find()) {
				String country = matcher.group().trim(); /* trim to remove leading/trailing spaces */
				states.add(country);
			}
		}
		return borderList;
	}
		/*
		 *handles trickier name things
		 *@return the trickier name things
		 */ 
		private HashMap<String, String> theExcepts() {
		HashMap<String, String> theExcepts = new HashMap<>();
		theExcepts.put("Cabo Verde", "Cape Verde");
		theExcepts.put("Cote d'Ivoire", "Ivory Coast");
		theExcepts.put("Gambia, The", "Gambia");
		theExcepts.put("Bahamas, The", "Bahamas");
		theExcepts.put("Czechia", "Czech Republic");
		theExcepts.put("North Macedonia", "Macedonia (Former Yugoslav Republic of)");
		theExcepts.put("Belarus", "Belarus (Byelorussia");
		theExcepts.put("Macedonia", "Macedonia (Former Yugoslav Republic of)");
		theExcepts.put("The Central African Republic", "Central African Republic");
		theExcepts.put("Congo, Democratic Republic of the", "Congo, Democratic Republic of (Zaire)");
		theExcepts.put("Congo, Republic of the", "Congo");
		theExcepts.put("The Republic of the Congo", "Congo");
		theExcepts.put("Democratic Republic of the Congo", "Congo, Democratic Republic of (Zaire)");
		theExcepts.put("The Slovak Republic", "Slovakia");
		theExcepts.put("Zimbabwe", "Zimbabwe (Rhodesia)");
		theExcepts.put("Iran", "Iran (Persia)");
		theExcepts.put("United States", "United States of America");
		theExcepts.put("US", "United States of America");
		theExcepts.put("Canada 1.3 km", "Canada");
		theExcepts.put("Czechia", "Czech Republic");
		theExcepts.put("Turkey (Turkiye)", "Turkey (Ottoman Empire)");
		theExcepts.put("Turkey", "Turkey (Ottoman Empire)");
		theExcepts.put("Korea, South", "Korea, Republic of");
		theExcepts.put("Korea, North", "Korea, People's Republic of");
		theExcepts.put("Cambodia", "Cambodia (Kampuchea)");
		theExcepts.put("Vietnam", "Vietnam, Democratic Republic of");
		theExcepts.put("Russia (Kaliningrad)", "Russia (Soviet Union)");
		theExcepts.put("Russia", "Russia (Soviet Union)");
		theExcepts.put("Timor-Leste", "East Timor");
		theExcepts.put("Botswana 0.15 km", "Botswana");
		theExcepts.put("Zambia 0.15 km", "Zambia");
		theExcepts.put("Denmark (Greenland) 1.3 km", "Denmark");
		theExcepts.put("Gibraltar 1.2 km", "Gibraltar");
		theExcepts.put("Holy See (Vatican City) 3.4 km", "Holy See (Vatican City)");
		theExcepts.put("Denmark (Greenland)", "Denmark");
		theExcepts.put("Yemen (Arab Republic of Yemen", "Yemen");
		theExcepts.put("Tanzania", "Tanzania/Tanganyika");
		theExcepts.put("The Solomon Islands", "Solomon Islands");
		theExcepts.put("UK", "United Kingdom");
		theExcepts.put("Germany", "German Federal Republic");
		theExcepts.put("Spain (Ceuta)", "Spain");
		theExcepts.put("Morocco (Cueta)", "Morocco");
		theExcepts.put("Spain 1.2 km", "Spain");
		theExcepts.put("Italy", "Italy/Sardinia");
		theExcepts.put("Burkina Faso", "Burkina Faso (Upper Volta)");
		return theExcepts;
		}
	}
	public class CapDist{
		public HashMap<List<String>, Integer> capdists;
		/*
		 *constructor does constructing things
		 *@param list array of strings developed from the capdist file
		 */
		public CapDist(String[] list){
			capdists = new HashMap<List<String>, Integer>();
			parseDist(list);
		}
		/*
		 *project write up has anything I would say here so yeah
		 */
		public int getDistance(String country0, String country1) {
			int dist;
			if (capdists.get(Arrays.asList(country0, country1)) == null)
				dist = -1;
			else
				dist = capdists.get(Arrays.asList(country0, country1));
			return dist;
		}
		/*
		 *parses distance from file
		 *@param list passed from constructor
		 */
		private void parseDist(String[] list){
			String temp;
			String[] tempArr;
			for(int i = 1; i < list.length; i++){
				temp = list[i];
				tempArr = temp.split(",");
				capdists.put(Arrays.asList(tempArr[1], tempArr[3]), Integer.parseInt(tempArr[4]));
			}
		}
	}
    Graph graph;
	public IRoadTrip (String [] args) throws Exception {
        // Replace with your code
		/**
		 *read border file & construct Graph class
		 *@param args arguments fed to the program
		 */
		 Scanner scan = new Scanner(new File(args[0]));
		 String temp = "";
		 while(scan.hasNext()){
			 temp += scan.nextLine() + "\n";
		 }
		 graph = new Graph(temp.split("\n"), args);
		 //System.out.println(graph.getGraph().keySet());
    }
	/*
	 * finds path using dijkstra's algo (shorter runtime with min Heap) uses a bunch of hashmaps 
	 *@param country1 starting country
	 *@param country2 destintation contry
	 *@return the path from source to destination
	 */
    public List<String> findPath (String country1, String country2) {
		HashMap<String, Integer> dists = new HashMap<>(); //initialization
		HashMap<String, String> prevNode = new HashMap<>();
		Set<String> visit = new HashSet<>();
		PriorityQueue<Node> minDist = new PriorityQueue<>();
		List<String> path;
		for (String node : graph.getGraph().keySet())
			dists.put(node, Integer.MAX_VALUE);
		dists.put(country1, 0);
		minDist.add(new Node(country1, 0));
		List<String> visitedNodes = new ArrayList<>();
		path = visitedNodes;

		while (!minDist.isEmpty()) { //dijkstra's
			Node curNode = minDist.poll();
			String cur = curNode.country;
			if (!visit.contains(cur))
				visit.add(cur);
			else
				continue; 
			if (dists.get(cur) == 1)
				continue;
			if (graph.getGraph().containsKey(cur)) {
				for (Map.Entry<String, Integer> neighbor : graph.getGraph().get(cur).entrySet()) {
					String adjacentCountry = neighbor.getKey();
					int neighborWeight = neighbor.getValue();
					int newDist = dists.get(cur) + neighborWeight;
					if (newDist < dists.getOrDefault(adjacentCountry, Integer.MAX_VALUE)) {
						dists.put(adjacentCountry, newDist);
						minDist.add(new Node(adjacentCountry, newDist));
						prevNode.put(adjacentCountry, cur);
					}
				}
			}
			if (cur.equals(country2))
				break;
		}

		while (!country2.equals(country1)) { //finish up
			String prevCountry = prevNode.get(country2);
			if (dists.get(country2) == null || dists.get(prevCountry) == null) {
				path = null; 
				break;
			}
			int dist = dists.get(country2) - dists.get(prevCountry);
			visitedNodes.add(prevCountry + " --> " + country2 + " (" + dist + " km.)");
			country2 = prevCountry;
		}
		Collections.reverse(visitedNodes); // so that path doesn't trace from the end to the beginning that would be bad
		return path;
    }
	

    /*
	 *see project writeup
	 */
	public void acceptUserInput() {
		Scanner scan = new Scanner(System.in);
		for(;;){
			System.out.print("Enter the name of the first country (type EXIT to quit): ");
			String src = scan.nextLine();
			if (src.equals("EXIT"))
				break;
			if (graph.getGraph().get(src) == null) {
				System.out.println("Invalid country name. Please enter a valid country name.");
				continue;
			}
			System.out.print("Enter the name of the second country (type EXIT to quit): ");
			String dest = scan.nextLine();
			if (src.equals("EXIT"))
				break;
			if (graph.getGraph().get(dest) == null) {
				System.out.println("Invalid country name. Please enter a valid country name.");
				continue;
			}
			System.out.println("Route from "+ src +" to " + dest +": ");
			List<String> paths = findPath(src, dest);
			for (int i = 0; i < paths.size(); i++)
				System.out.println("* " + paths.get(i));
		}
    }


    public static void main(String[] args) throws Exception{
        if(args.length != 3 || !args[0].equals("borders.txt") || !args[1].equals("capdist.csv") || !args[2].equals("state_name.tsv")){
			System.out.println("bad args");
			return;
		}
		//String[] arr = {"borders.txt", "capdist.csv", "state_name.tsv"};
		IRoadTrip a3 = new IRoadTrip(args);
        a3.acceptUserInput();
    }

}

