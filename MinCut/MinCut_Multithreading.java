package week3Assign;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;

public class MinCut_Multithreading {
		
	private class Graph {
		
		//Every single vertex is unique
		private Map<Integer, MinCut_Multithreading.Vertex> vertices = new HashMap<Integer, Vertex>();
		
		//duplicated edges might exist
		private List<MinCut_Multithreading.Edge> edges = new ArrayList<Edge>();
		
		
		public Graph() {
			
		}
		
		public int numVertices() {
			return vertices.size();
		}
		
		public void addEdge(Edge e) {
			edges.add(e);
		}
		
		public void addVertex(Vertex v) {
			if (vertices.containsKey(v.getId())) {
				throw new IllegalArgumentException("Vertex already exist!");
			}
			vertices.put(v.getId(), v);
		}
		
		public void removeEdgesIncidentWith(Vertex v) { // v as one of the  vertex 
			for(ListIterator<Edge> itr = edges.listIterator(); itr.hasNext();) {
				Edge e = itr.next();
				if (e.EdgeContains(v)) {
					itr.remove();
				}
			}
		}
		
		/*
		 * remove v from the adjacent vertices of all of the other vertices in the graph
		 */
		public void removeFromAllVertices(Vertex v) {
			for(Map.Entry<Integer, Vertex> entry: vertices.entrySet()) {
				entry.getValue().removeAdjVertex(v);
			}
		}
		
		/*
		 * Add v to the adjacent list of vertices adjacent to v
		 */
		public void updateAllVerticesAdjTo(Vertex v) {
			for(Vertex ver: v.getAdjVertices()) {
				if (vertices.get(ver.getId()) != null) {
					vertices.get(ver.getId()).addAdjVertex(v);

				}
			}
		}
		
		public boolean containsEdge(Edge e) {
			return edges.contains(e);
		}
	}
	
	
	
	private class Vertex {
		private Integer Id;
		private List<MinCut_Multithreading.Vertex> adjacentVertices = new ArrayList<Vertex>();
		
		public Vertex(Integer id) {
			this.Id = id;
		}
		
		public int getId() {
			return this.Id;
		}
		
		public void addAdjVertex(Vertex v) {
			adjacentVertices.add(v);
		}
		
		public boolean hasAdjVertex(Vertex v) {
			return adjacentVertices.contains(v);
		}
		
		public List<Vertex> getAdjVertices() {
			return this.adjacentVertices;
		}
		
		/*
		 * remove v from this.adjacentVertices
		 */
		public void removeAdjVertex(Vertex v) {
			for (Iterator<Vertex> itr = adjacentVertices.iterator(); itr.hasNext();) {
				if(itr.next().equals(v)) {
					itr.remove();
				}
			}
		}
		
		@Override
		public boolean equals(Object obj) {
			if (super.equals(obj)) {
				return true;
			}
			
			Vertex v = null;
			if (obj == null) return false;
			if (obj instanceof Vertex) {
				v = (Vertex)obj;
			} else {
				throw new ClassCastException("fail to cast obj to Vertex");
			}
			return Integer.compare(this.Id, v.getId()) == 0;
		}
	}
	
	private class Edge {
		private Vertex first;
		private Vertex scnd;
		
		public Edge(Vertex first, Vertex scnd) {
			this.first = first;
			this.scnd = scnd;
		}
		
		public Vertex getFirstVertex() {
			return first;
		}
		
		public Vertex getScndVertex() {
			return scnd;
		}
		
		public boolean EdgeContains(Vertex v) {
			if ((first.getId() == v.getId()) || (scnd.getId() == v.getId())) return true;
			return false;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (super.equals(obj)) {
				return true;
			}
			
			if (obj == null) return false;
			
			Edge e = null;
			if (obj instanceof Edge) {
				e = (Edge)obj;
			} else {
				throw new ClassCastException("fail to cast obj to Edge");
			}
			
			if (((this.first.getId() == e.first.getId()) && (this.scnd.getId() == e.scnd.getId())) 
					|| ((this.first.getId() == e.scnd.getId()) && (this.scnd.getId() == e.first.getId()))) {
				return true;
			}
			
			return false;
		}
	}
	
	
	/*
	 * remove self loops in a. That is, if there are edges from a to a or from a to b in the edges of a, remove them.
	 */
	public void rmvSelfLoop(Vertex a, Vertex b) {
		if (a != null) {
			a.removeAdjVertex(b);
		}
		
	}
	
	
	/*
	 * replace edges incident with src with edges incident with dest.
	 * Add new edges to graph gr
	 */
	public void redirectEdges(Graph gr, Vertex src, Vertex dest) {
		for (Vertex v: src.getAdjVertices()) {
			dest.addAdjVertex(v);
			Edge e = new MinCut_Multithreading.Edge(dest, v);
			gr.addEdge(e);
		}	
	}
	
	
	/*
	 * Implementation of Random Contraction Algorithm(Karger's Algorithm).
	 * the algorithm is used to find the min cut of a graph
	 */
	public int CalcMinCut(Graph gr) {
		Random randomGenerator = new Random();
		while(gr.vertices.size() > 2) {
			// pick an edge uniformly at random, and remove it
			//If  there are parallel edges, remove at the same time.
			int index = randomGenerator.nextInt(gr.edges.size());
			Edge edge = gr.edges.remove(index);
			while(gr.edges.remove(edge));			
			
			//remove 2 vertices from graph, as well as their related edges
			Vertex u = gr.vertices.remove(edge.getFirstVertex().getId()); // remove u
			Vertex v = gr.vertices.remove(edge.getScndVertex().getId()); // remove v

			//remove self loop between the u and v
			rmvSelfLoop(u, v); 
			rmvSelfLoop(u, u); 
			rmvSelfLoop(v, u);
			rmvSelfLoop(v, v); 
			
			gr.removeFromAllVertices(u);
			gr.removeFromAllVertices(v);
			
			gr.removeEdgesIncidentWith(u);
			gr.removeEdgesIncidentWith(v);
			
			// create a super node uv with the same Id of u
			Vertex superNode = new MinCut_Multithreading.Vertex(u.getId()); 

			// replace edges incident to first and second vertices with edges incident to superNode
			redirectEdges(gr, u, superNode);
			redirectEdges(gr, v, superNode);
			
			gr.updateAllVerticesAdjTo(superNode);
			
			//add superNode to graph
			gr.addVertex(superNode);
		}
		
		return gr.edges.size(); // # of edges represented by the last two vertices
	}
	
	
	/*
	 * read the info of a graph into an array of array, where the first column represents one node
	 * of an edge, the rest of that row each represent  the other node of an edge.
	 */
	public Map<Integer, List<Integer>> readDataFromFile(String file) throws IOException {
		Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
		
		File input = new File(file);
		BufferedReader reader = new BufferedReader(new FileReader(input));
		
		String line = null;	
		int col = 0;
		Integer key = 0; // key is just the index of each entry, different from the id of vertex
		
		while((line = reader.readLine()) != null) {
			String[] tokens = line.trim().split("\\s+");
			
			map.put(key, new ArrayList<Integer>());
			for (String string: tokens) {
					map.get(key).add(Integer.parseInt(string));
			}
			col = Math.max(col, map.get(key).size());
			key ++;
		}
			
		if (reader != null) reader.close();
		return map;
	}
	
	
	/*
	 * Convert the map into a Graph structure
	 */
	public Graph createGraphFromMap(Map<Integer, List<Integer>> map) {
		Graph graph = new MinCut_Multithreading.Graph();
		for(Map.Entry<Integer, List<Integer>> entry: map.entrySet()) {
			Vertex u = new MinCut_Multithreading.Vertex(entry.getValue().get(0));
			for(int i = 1; i < entry.getValue().size(); i ++) {
				Vertex v = new MinCut_Multithreading.Vertex(entry.getValue().get(i));
				u.addAdjVertex(v);
				Edge e = new MinCut_Multithreading.Edge(u, v);
				if (!graph.edges.contains(e)) {
					graph.addEdge(e);
				}
			}
			graph.addVertex(u);
		}
		return graph;
	}
	
	//For Printing
	public void printGraph(Graph gr) {
		for (Map.Entry<Integer, Vertex> entry: gr.vertices.entrySet()) {
			System.out.println("Vertex "+ entry.getKey() + " are incident with the following edges:");
			for(Vertex v: entry.getValue().getAdjVertices()) {
				System.out.println(entry.getKey() + " ---> " + v.getId());
			}
		}
		
		System.out.println("Print edges only:");
		
		for (Edge e: gr.edges) {
			System.out.println(e.getFirstVertex().getId() + " ---> " + e.getScndVertex().getId());
		}
	}
	
	/*
	 * Run Karger's Algorithm (# of vertices)^2 times to increase the probability of success to find
	 * the min cut of a graph.
	 * 
	 * Return the size of min cut
	 * 
	 */
	public static void main(String [] args) throws IOException, InterruptedException {
		// We create n threads, each of which run the Karger's algorithm n times in order to
		// improving performance.
		MinCut_Multithreading minCut = new MinCut_Multithreading();
		String input = "./src/InOutput/kargerMinCut.txt";
		Map<Integer, List<Integer>> rawMap = minCut.readDataFromFile(input);
		
		Thread[] threads = new Thread[200];
		List<Integer> cutSizes = Collections.synchronizedList(new ArrayList<Integer>());
				
		for(int i = 0; i < 200; i++) {
			threads[i] = new Thread(new Runnable() {
				@Override
				public void run() {
					Graph graph = minCut.createGraphFromMap(rawMap);
					
					int minCutSize = Integer.MAX_VALUE;
					int cutSize = 0;
					int runTimes = graph.numVertices();
					
					for (int i = 0; i < runTimes; i++) {
						cutSize = minCut.CalcMinCut(graph);
						minCutSize = Math.min(cutSize, minCutSize);
						graph = minCut.createGraphFromMap(rawMap);
					}		
					cutSizes.add(minCutSize);
				}
			});
		}
		
		for(Thread th: threads) {
			th.start();
		}
		
		for(Thread th: threads) {
			th.join();
		}
		
		int minCutSize = Integer.MAX_VALUE;
		for(int size: cutSizes) {
			minCutSize = Math.min(size, minCutSize);
		}
		System.out.println("The minimal cut size is " + minCutSize);
	}

}
