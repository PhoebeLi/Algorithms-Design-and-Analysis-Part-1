package week6Assign;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TargetValue {
	List<Long> list;
	
	public TargetValue(String file) throws IOException {
		list = new ArrayList<Long>();
		
		File input = new File(file);
		BufferedReader reader = new BufferedReader(new FileReader(input));
		String line = "";		
		
		while((line = reader.readLine()) != null) {
			Long value = Long.parseLong(line);
			list.add(value);
		}
		
		if(reader != null) reader.close();
		Collections.sort(list);
	}
	
	/*
	 * Return true if there exists a pair of elements x and y such that x+y = target, or false otherwise.
	 */
	public boolean sumTo(int target) {
		int i = 0;
		int j = list.size()-1;
		while(i < j) {
			if ((list.get(i) + list.get(j)) < target) {
				i ++;
			}
		
			else if ((list.get(i) + list.get(j)) > target) {
				j --;
			}
			
			else {
				if(list.get(i) == list.get(j)) return false;
				return true;
			}
		}
		return false;
	}
	
	/*
	 * Return the number of targets in the interval[-10000, 10000] such that there are distinct x and y
	 * in the input file that satisfy x+y = t.
	 */
	public int numTargets() {
		int num = 0;
		for(int i = -10000; i <= 10000; i++) {
			if(sumTo(i)) num ++;
		}
		return num;
	}

	public static void main(String[] args) throws IOException {
		TargetValue obj = new TargetValue("./src/files/2SumData.txt");
		int num = obj.numTargets();
		System.out.println(num);		
	}

}
