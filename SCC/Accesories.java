package week4Assign;

import java.util.Comparator;
import java.util.Map;

public class Accesories {

	public Accesories() {
		// TODO Auto-generated constructor stub
	}

	
	/*
	 * Accessories: KeyComparator; MutableInteger
	 */
	
	//comparator passed to treeMap as an argument
	public class KeyComparator implements Comparator<Integer> {
		@Override
		public int compare(Integer o1, Integer o2) {
			return o2.compareTo(o1);
		}	
	}
	
	public class ValueComparator implements Comparator {
		Map map;
		
		
		public ValueComparator(Map map) {
			this.map = map;
		}
		
		@Override
		public int compare(Object o1, Object o2) {
			Comparable value1 = (Comparable)map.get(o1);
			Comparable value2 = (Comparable)map.get(o2);
			return value2.compareTo(value1);
		}
	}
	
	public class MutableInteger {
		int value = 0;
		public MutableInteger(int value) {
			this.value = value;
		}
		public int incrementBy(int i) {
			this.value += i;
			return this.value;
		}
		public int getValue() {
			return this.value;
		}
	}
	
	
}
