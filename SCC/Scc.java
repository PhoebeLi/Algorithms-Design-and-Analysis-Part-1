package week4Assign;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import week4Assign.Accesories.ValueComparator;

public class Scc {

	class Vertex implements Comparable<Vertex>{
		private Integer Id;
		private List<Vertex> neighbors = new ArrayList<Scc.Vertex>();
		private List<Vertex> neighbors_rev = new ArrayList<Scc.Vertex>();
		private Integer finishing_time = 0; // the smallest time is 1 
		private boolean visited = false;
		private Integer scc_size = 0;
		private List<Integer> sccMembers= new ArrayList<Integer>();
		
		
		public Vertex(int Id) {
			this.Id = Id;
		}
		
		public int getId() {
			return this.Id;
		}
		
		public void addNeighbor(Vertex v) {
			this.neighbors.add(v);
		}
		
		public void addNeighbor_rev(Vertex v) {
			this.neighbors_rev.add(v);
		}
		
		public void setFinishTime(int time) {
			this.finishing_time = time;
		}
		
		public boolean Finished() {
			return (this.finishing_time != 0);
		}
		
		public int getFinishTime() {
			return this.finishing_time;
		}
		
		public void setVisited() {
			this.visited =  true;
		}
		
		public boolean isVisited() {
			return this.visited;
		}
		
		public void clearUp() {
			visited = false;
		}
		
		public void setSccSize(int size) {
			this.scc_size = size;
		}
		
		public int getSccSize() {
			return this.scc_size;
		}

		public void addSccMem(int id) {
			this.sccMembers.add(id);
		}
		public void printSccMems() {
			for(int mem: this.sccMembers) {
				System.out.print(mem + " ");
			}
			System.out.println();
		}
		@Override
		public int compareTo(Vertex o) {
			return this.scc_size.compareTo(o.getSccSize());
		}
	}

	class Graph {
		// Store vertices in decreasing order of Id, key is the Id of the corresponding vertex； 
		// for the first pass
		private Map<Integer, Vertex> vertices_id = new TreeMap<Integer, Scc.Vertex>(new Accesories().new KeyComparator());
		
		// Store vertices in decreasing order of finishing time, key is the finishing time of the corresponding vertex
		// for the second pass
		private Map<Integer, Vertex> vertices_ft = new TreeMap<Integer, Scc.Vertex>(new Accesories().new KeyComparator()); 
		
		
		public void addVertexById(Vertex v) {
			this.vertices_id.put(v.getId(), v);
		}
		
		public Vertex getVertexById(int id) {
			return this.vertices_id.get(id);
		}
		
		public void addVertexByFtime(Vertex v) {
			this.vertices_ft.put(v.getFinishTime(), v);
		}
		
		public void clearUpVisitedLog() {
			for(Map.Entry<Integer, Vertex> entry: this.vertices_ft.entrySet()) {
				entry.getValue().clearUp();
			}
		}
	}
	
	/*
	 * compute finishing time for each node, iterative version
	 */
	public void DFS_rev(Graph gr, Vertex s, Accesories.MutableInteger ftime_so_far) {
		if (s == null) return;
		Stack<Vertex> stack = new Stack<Scc.Vertex>();
		stack.push(s);
		while(!stack.isEmpty()) {
			Vertex cur = stack.peek();
			cur.setVisited();
			
			int new_neighbors = 0;
			for(Vertex neighbor_rev: cur.neighbors_rev) {
				if (!neighbor_rev.isVisited()) {
					stack.push(neighbor_rev);
					new_neighbors ++;
				}
			}
				
			// all of the neighbors of cur has been visited, so we 
			// set cur's finish time and pop it from stack
			if (new_neighbors == 0) {
				if (!cur.Finished()) {
					ftime_so_far.incrementBy(1);
					cur.setFinishTime(ftime_so_far.getValue());
					gr.addVertexByFtime(cur);
				}
				stack.pop();
			}
		}		
	}
	
	/*
	 * run DFS_loop on reversed G, compute finishing time for each node
	 */
	public void DFS_loop_reverse(Graph gr) {
		Accesories.MutableInteger finish_time_so_far = new Accesories().new MutableInteger(0);
		for(Map.Entry<Integer, Vertex> entry: gr.vertices_id.entrySet()) {
			if (!entry.getValue().isVisited()) {
				//System.out.println("next vertex to visit in the first loop is " + entry.getKey());
				DFS_rev(gr, entry.getValue(), finish_time_so_far);
			}
		}
	}
	
	
	
	/*
	 * Run DFS on vertices of Graph gr, compute size of each found SCC
	 */
	public int DFS(Graph gr, Vertex s) {
		if (s == null) return 0;
		
		Stack<Vertex> stack = new Stack<Scc.Vertex>();
		stack.push(s);	
		int hops_so_far = 0;
		while(!stack.isEmpty()) {
			Vertex cur = stack.pop();
			if (!cur.isVisited()) {
				hops_so_far += 1;
			}
			cur.setVisited();
			s.addSccMem(cur.getId());
			for(Vertex neighbor: cur.neighbors) {			
				if (!neighbor.isVisited()) {
					stack.push(neighbor);
				}
			}
		}
		s.setSccSize(hops_so_far);
		return hops_so_far;
	}
	
	/*
	 * run DFS_loop on original Graph gr, in decreasing order of the finishing time.
	 * return: a list of leaders of each SCC.
	 */
	public List<Vertex> DFS_loop(Graph gr) {
		List<Vertex> leaders = new ArrayList<Scc.Vertex>();
		for(Map.Entry<Integer, Vertex> entry: gr.vertices_ft.entrySet()) {
			if (!entry.getValue().isVisited()) {
				DFS(gr, entry.getValue());
				leaders.add(entry.getValue());
			}
		}
		return leaders;
	}
	
	/*
	 * print leaders and their corresponding size
	 */
	public void printLeaders(List<Vertex> leaders) {
		System.out.println("print leaders :");
		for(Vertex leader: leaders) {
			System.out.println("Size of scc is " + leader.getSccSize() + " with leader Id " + leader.getId());
		}
	}
	
	/*
	 * implement Kosaraju's algorithm(a.k.a. two-pass algorithm)
	 */
	public List<Vertex> calcScc(Graph gr) {		
		DFS_loop_reverse(gr);
		gr.clearUpVisitedLog(); // set all vertices back to be unvisited
		List<Vertex> leaders = DFS_loop(gr);
		return leaders;
	}
	
	
	
	public Graph buildGraphFromFile(String file) throws IOException {
		Graph gr = new Graph();
		File input = new File(file);
		BufferedReader reader = new BufferedReader(new FileReader(input));
		String line = "";		
		
		while((line = reader.readLine()) != null) {
			String [] tokens = line.trim().split("\\s+");
			int firstVId = Integer.parseInt(tokens[0]);
			int secondVId = Integer.parseInt(tokens[1]);
			
			Vertex vertexA = gr.getVertexById(firstVId);
			Vertex vertexB = gr.getVertexById(secondVId);
					
			if (vertexA == null) { // vertex A not existed yet in Graph
				vertexA = new Vertex(firstVId);
				gr.addVertexById(vertexA);
			}
			
			if (vertexB == null) { // vertex B not existed yet in Graph
				vertexB = new Vertex(secondVId);
				gr.addVertexById(vertexB);
			}
			
			vertexA.addNeighbor(vertexB);
			vertexB.addNeighbor_rev(vertexA);
			
		}
		if (reader != null) reader.close();
		return gr;
	}
	
	
	
	//For printing
	public void printGraph(Graph gr) {
		System.out.println("edges of graph: ");
		for(Map.Entry<Integer, Vertex> entry: gr.vertices_id.entrySet()) {
			for(Vertex neighbor: entry.getValue().neighbors) {
				System.out.println(entry.getKey() + " --> " + neighbor.getId());
			}
		}
		
		System.out.println("edges of reversed graph: ");
		for(Map.Entry<Integer, Vertex> entry: gr.vertices_id.entrySet()) {
			for(Vertex neighbor_rev: entry.getValue().neighbors_rev) {
				System.out.println(entry.getKey() + " --> " + neighbor_rev.getId());
			}
		}
	}

	
	/*
	 * run algorithm and returns the sizes of five largest scc's.
	 */
	public static void main(String[] args) throws IOException{
		Scc scc = new Scc();
		String file = "./src/files/SCC.txt";
		Graph gr = scc.buildGraphFromFile(file);
			
		List<Vertex> leaders = scc.calcScc(gr);
		
		//print the size of the largest five sccs
		Collections.sort(leaders, new Comparator<Vertex>() {
			@Override
			public int compare(Vertex o1, Vertex o2) {
				return o2.compareTo(o1);
			}
		});
		int i = 0;
		for(Vertex v: leaders) {
			if (i >= 5) break;
			System.out.println(v.getSccSize() + " with id " + v.getId() + " :");
			i ++;
		}
		
	}

}
