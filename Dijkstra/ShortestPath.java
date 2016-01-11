package week5Assign;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;


public class ShortestPath {

	class Vertex implements Comparable<Vertex>{
		private Integer Id;
		private Integer DijkstraScore = 1000000;
		private List<Vertex> neighbors = new ArrayList<Vertex>();
		
		//key = neighbor Id, value = the length of edge sticking from this vertex to vertex with ID = key
		private Map<Integer, Integer> lens = new HashMap<Integer, Integer>();
		
		
		public Vertex(int Id) {
			this.Id = Id;
		}
		
		public int getId() {
			return this.Id;
		}
		
		public void addNeighbor(Vertex v) {
			this.neighbors.add(v);
		}
		
		public void addneighborLen(Vertex neighbor, int len) {
			lens.put(neighbor.getId(), len);
		}
		
		public void setScore(int score) {
			if (score < this.DijkstraScore){
				this.DijkstraScore = score;
			}	
		}
		
		public int getScore() {
			return this.DijkstraScore;
		}
		
		//update Dijkstra's score for every neighbor of this vertex
		public void updateNeighbors(Heap heap) {
			for(Vertex neighbor: neighbors) {
				if (heap.inHeap(neighbor)) {
					heap.deleteVertex(neighbor);
					int newScore = DijkstraScore+lens.get(neighbor.getId());
					neighbor.setScore(newScore);
					heap.insertVertex(neighbor);
				}
			}
		}
		
		@Override
		public int compareTo(Vertex o) {
			return this.DijkstraScore.compareTo(o.getScore());
		}
		
	}
	

	
	class Heap {
		// Vertices that has not been assigned a shortest path size;
		// Store vertices in a heap structure to improve efficiency.
		private PriorityQueue<Vertex> pq = new PriorityQueue<ShortestPath.Vertex>();
		
		// Vertices to which shortest paths from source node has been identified;
		// key = vertex Id, value = reference to vertex
		private Map<Integer, Vertex> processedVertices = new HashMap<Integer, ShortestPath.Vertex>();
		
		//extract root and update its neighbors
		public void extractVertex() {
			Vertex v = pq.poll();
			v.updateNeighbors(this);
			processedVertices.put(v.getId(), v);
		}
				
		public void insertVertex(Vertex v) {
			pq.offer(v);
		}
				
		public void deleteVertex(Vertex v) {
			pq.remove(v);
		}
		
		public void extractMin() {
			pq.poll();
		}
		
		public boolean inHeap(Vertex v) {
			return !processedVertices.containsKey(v.getId());
		}
		
		public boolean isEmpty() {
			return this.pq.isEmpty();
		}
		
		public void printShortestPath() {
			for(Map.Entry<Integer, Vertex>entry: processedVertices.entrySet()) {
				System.out.println("node " + entry.getKey() + " has shortest path distance: " + entry.getValue().getScore());
			}
		}
	}
	
	
	/*
	 * run Dijkstra algorithm
	 */
	public void calcShortestPath(Heap heap) {
		while(!heap.isEmpty()) {
			heap.extractVertex();
		}
	}
	
	
	public Heap createHeapFromFile(String file) throws IOException {
		Map<Integer, Vertex> vertices = new HashMap<Integer, Vertex>();
		
		File input = new File(file);
		BufferedReader reader = new BufferedReader(new FileReader(input));
		String line = "";		
		
		while((line = reader.readLine()) != null) {
			String [] tokens = line.trim().split("\\s+");
			int Id = Integer.parseInt(tokens[0]);
			Vertex v = vertices.get(Id);
			if (v == null) {
				v = new Vertex(Id);
				vertices.put(Id, v);
			}
			
			for(int i = 1; i < tokens.length; i++) {
				String [] valuePair = tokens[i].trim().split(",");
				int neighborId = Integer.parseInt(valuePair[0]);
				int len = Integer.parseInt(valuePair[1]);
				Vertex neighbor = vertices.get(neighborId);
				if (neighbor == null) {
					neighbor = new Vertex(neighborId);
					vertices.put(neighborId, neighbor);
				}
				
				v.addNeighbor(neighbor);
				v.addneighborLen(neighbor, len);
			}
		}
		
		if (reader != null) reader.close();
		vertices.get(1).setScore(0); // set source node score to 0 
		
		Heap heap = new Heap();
		for(Map.Entry<Integer, Vertex> entry: vertices.entrySet()) {
			heap.insertVertex(entry.getValue());
		}
		
		return heap;
	}
	
	public void printHeap(Heap heap) {
		while(!heap.isEmpty()) {
			heap.extractMin();
		}
	}
	
	public static void main(String[] args) throws IOException {
		ShortestPath shortestPathObj = new ShortestPath();
		Heap heap = shortestPathObj.createHeapFromFile("./src/files/DijkstraData.txt");
		shortestPathObj.calcShortestPath(heap);
		heap.printShortestPath();
	}
}
