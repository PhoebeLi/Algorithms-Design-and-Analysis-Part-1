package week2Assign;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class QuickSortMedianOfThree {
	
	public long quickSort(int [] arr, int left, int right) {
		if (left >= right) return 0;
		int pivotIndex = partition(arr, left, right);
		long leftCount = quickSort(arr, left, pivotIndex-1);
		long rightCount = quickSort(arr, pivotIndex+1, right);
		return leftCount+rightCount+(right-left);
	}
	
	
	public int partition(int [] arr, int left, int right) {
		swap(arr, left, pivot(arr, left, right));
		int pivot = arr[left];
		int i = left+1;
		for (int j = i; j <= right; j++) {
			if (arr[j] < pivot) {
				swap(arr, i, j);
				i ++;
			}
		}
		swap(arr, left, i-1);
		return i-1; // the position of the pivot after partition.
	}
	
	public void swap(int [] arr, int i, int j) {
		int temp = arr[i];
		arr[i] = arr[j];
		arr[j] =temp;
	}
	
	//choose median of three, return the index of the pivot 
	public int pivot(int [] arr, int left, int right) { 
		int median = arr[(left+right)/2];
		int first = arr[left];
		int last = arr[right];
		
		if ((last <= first && first <= median) || (median <= first && first <= last) ) return left;
		if ((first <= last && last <= median) || (median <= last && last <= first)) return right;
		return (left+right)/2;
	}
	
	public static void main(String [] args) throws IOException {
		File input = new File("./src/files/QuickSort.txt");
		 BufferedReader reader = new BufferedReader(new FileReader(input));
		 
		 String line = null;
		 int [] arr = new int[10000];
		 int i = 0;
		 
		 while((line = reader.readLine()) != null) {
			 arr[i] = Integer.parseInt(line);
			 i ++;
		 }

		QuickSortMedianOfThree quickSort = new QuickSortMedianOfThree(); 
		long count = quickSort.quickSort(arr, 0, arr.length-1);

		System.out.println("Total number of comparisons is " + count);
		
		if (reader != null) reader.close();
	}
}
