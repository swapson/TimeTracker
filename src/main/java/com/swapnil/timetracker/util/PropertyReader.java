package com.swapnil.timetracker.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.TreeMap;

public class PropertyReader {
	public static void main(String[] args) throws FileNotFoundException{
		Map<String, Object> map = getPropertyMap();
		System.out.println(map);

	}

	public static Map<String, Object> getPropertyMap() throws FileNotFoundException{
		Map<String, Object> map = new HashMap<String, Object>();
		Map<Integer, String> minutes = new TreeMap<Integer, String>();
		try {
			ResourceBundle rb = new PropertyResourceBundle(new FileReader("./TimeTracker.properties"));
			Enumeration<String> en = rb.getKeys();

			while (en.hasMoreElements()) {
				String key = en.nextElement();
				if (key.matches("hours-[0-9\\.]+")) {
					minutes.put((int) (Float.parseFloat(key.split("-")[1])* 60), rb.getString(key));
				} else {
					map.put(key, rb.getString(key));
				}
				map.put("minutes", minutes);
			}

		} catch (IOException e) {
			System.out.println("Unable to find TimeTracker.properties file");
			System.out.println("Please create this file with below sample data:");
			System.out.println("comment.color=lightcoral");
			System.out.println("hours.9=green");
			System.out.println("hours.8=aqua");
			System.out.println("hours.7.5=lightgreen");
			System.out.println("under.time.color=red");
			throw new FileNotFoundException("Unable to find TimeTracker.properties file");
		}
		return map;
	}
}
