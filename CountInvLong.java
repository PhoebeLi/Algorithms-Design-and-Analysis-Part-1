package week1Assign;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CountInvLong {

	public long countInv(int [] arr, int start, int end) {
		 if (start >= end) return 0;
		 int mid = (start+end)/2;
		 long left = countInv(arr, start, mid);
		 long right = countInv(arr, mid+1, end);
		 long splitInvs = countSplits(arr, start, mid, end);
		 return left+right+splitInvs;
	 }
	 
	 public long countSplits(int [] arr, int start, int mid, int end) {
		 if (start >= end) return 0;
		 int [] helper = new int[arr.length];
		 for (int i = start; i <= end; i++) {
			helper[i] = arr[i];
		}
		 
		long splits = 0;
		int left = start;
		int right = mid+1;
		int cur = start;
		
		while(left <= mid && right <= end) {
			if (helper[left] <= helper[right]) {
				arr[cur] = helper[left];
				left ++;
				cur ++;
			} else { // counting split inversions
				arr[cur] = helper[right];
				right ++;
				cur ++;
				splits += (mid-left+1); // remaining elements in the left sub-array
			}
		}
		
		int remaining = mid-left+1;
		for (int i = 0; i < remaining; i++) {
			arr[cur] = helper[left];
			left ++;
			cur ++;
		}
		
		return splits;
	 }
	 
	 public static void main(String [] args) throws NumberFormatException, IOException {
		 CountInvLong count = new CountInvLong();
		 
		 File input = new File("./src/files/IntegerArray.txt");
		 BufferedReader reader = new BufferedReader(new FileReader(input));
		 
		 String line = null;
		 int [] arr = new int[100000];
		 int i = 0;
		 
		 while((line = reader.readLine()) != null) {
			 arr[i] = Integer.parseInt(line);
			 i ++;
		 }
		 
		 System.out.println("There are " + count.countInv(arr, 0, 100000-1) + " inversions in total.");
		 if (reader != null) reader.close();
		 
	 }
}
