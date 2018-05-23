package com.swapnil.timetracker;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

public class DayTimeEntriesSort<T> implements Comparator<T> {

	@Override
	public int compare(T o1, T o2) {
		int result = 0;
		if (o1 != null && o2 != null) {
			Entry<String, List<Date>> e1 = (Entry<String, List<Date>>) o1;
			Entry<String, List<Date>> e2 = (Entry<String, List<Date>>) o2;
			result = e1.getKey().compareTo(e2.getKey()) * -1;
		}
		return result;
	}

}
