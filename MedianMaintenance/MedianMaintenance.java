package week6Assign;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.PriorityQueue;

public class MedianMaintenance {
	PriorityQueue<Integer> h_low; // keep the smallest half of the elements of the input
	PriorityQueue<Integer> h_high; // keep the largest half of the elements of the input
	

	public MedianMaintenance() {
		h_low = new PriorityQueue<Integer>(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return o2.compareTo(o1);
			}
		
		});
		
		h_high = new PriorityQueue<Integer>();
	}

    /*
     * insert integer e into h_low or h_high, and then re-balance the two heaps. 
     */
	public void insert(int e) {
		if (h_low.size() == 0) {
			h_low.offer(e);
		} else if (e <= h_low.peek()) {
			h_low.offer(e);
		} else {
			h_high.offer(e);
		}
		if(!(h_low.size() == h_high.size() || (h_low.size()-h_high.size())==1)) {
			rebalance();
		}
	}
	
	/*
	 * re-balance h_low and h_high until the difference between the size of h_low and the size of h_high is 0 or 1.
	 */
	public void rebalance() {
		while(!(h_low.size() == h_high.size() || (h_low.size()-h_high.size())==1)) {
			if (h_low.size() > h_high.size()) {
				h_high.offer(h_low.poll());
			} else {
				h_low.offer(h_high.poll());
			}
		}
	}
	
	/*
	 * return the median of the input so far
	 */
	public int getMedian() {
		return h_low.peek();
	}

	
 	public static void main(String[] args) throws NumberFormatException, IOException {
 		File input = new File("./src/files/Median.txt");
		BufferedReader reader = new BufferedReader(new FileReader(input));
		String line = "";		
		
		int medianSum = 0;
		int size = 0;
		MedianMaintenance mm = new MedianMaintenance();
		while((line = reader.readLine()) != null) {
			 mm.insert(Integer.parseInt(line));
			 medianSum += mm.getMedian();
			 size ++;
		}
		System.out.println("result is " + medianSum % size);
		if(reader != null) reader.close();
	}

}
