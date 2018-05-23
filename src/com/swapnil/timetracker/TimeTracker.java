package com.swapnil.timetracker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TimeTracker {
	public static void main(String[] args) {
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat(Constants.DATE_TIME_FORMATE);
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMATE);

		List<Date> dates = new ArrayList<Date>();
		File inFile = new File(Constants.TIME_TRACKER_IN_FILE_NAME);
		File outHTMLFile = new File(Constants.TIME_TRACKER_OUT_FILE_NAME_HTML);
		File outTXTFile = new File(Constants.TIME_TRACKER_OUT_FILE_NAME_TXT);
		String line = null;
		int lineNumber = 1;

		Map<String, List<Date>> dayTimeEntries = new LinkedHashMap<String, List<Date>>();
		try {
			BufferedReader inBR = new BufferedReader(new FileReader(inFile));

			while ((line = inBR.readLine()) != null) {
				dates.add(dateTimeFormat.parse(line));
			}
			inBR.close();

			for (Date d : dates) {
				String date = dateFormat.format(d);
				
				List<Date> dayTimeEntry = null;
				if(dayTimeEntries.containsKey(date)) {
					dayTimeEntry = dayTimeEntries.get(date);
				} else {
					dayTimeEntry = new ArrayList<Date>();
					dayTimeEntries.put(date,dayTimeEntry);
				}
				
				dayTimeEntry.add(d);
			}
			
			List<Entry<String, List<Date>>> dayTimeEntriesList = new LinkedList<Entry<String, List<Date>>>(dayTimeEntries.entrySet());
			Collections.sort(dayTimeEntriesList,new DayTimeEntriesSort<Entry<String, List<Date>>>());
			
			dayTimeEntries.clear();
			for(Entry<String, List<Date>> entry : dayTimeEntriesList) {
				dayTimeEntries.put(entry.getKey(), entry.getValue());
			}
			
			
			if(dayTimeEntries.size()>0) {
				
				BufferedWriter outHTMLBR = new BufferedWriter(new FileWriter(outHTMLFile));
				BufferedWriter outTXTBR = new BufferedWriter(new FileWriter(outTXTFile));
				
				//write header
				
				line = String.format("%-10s\t%-19s\t%-19s\t%-5s","DATE","START","END","TIME");
				System.out.println(line);
				outTXTBR.write(line+"\n");
				
				outHTMLBR.write("<html><head><style>th,td {padding:5px}</style></head><body><table cellspacing='0' cellpading='0' border='1'>\n");
				outHTMLBR.write("<tr><th>DATE</th><th>START</th><th>END</th><th>TIME</th></tr>\n");
				
				String currentDateRowFormatting = "style='background:yellow'";
				for(Entry<String,List<Date>> dayEntry: dayTimeEntries.entrySet() ) {
					Date startDate = null;
					Date endDate = null;
					if(dayEntry.getValue().size()>1) {
						Collections.sort(dayEntry.getValue());
						startDate = dayEntry.getValue().get(0);
						endDate = dayEntry.getValue().get(dayEntry.getValue().size()-1);
					} else {
						startDate = endDate = dayEntry.getValue().get(0);
					}
					
					long timeMilliSec  = endDate.getTime() - startDate.getTime();
					
					long hours = (timeMilliSec/(1000*60*60));
					long mins = ((timeMilliSec/(1000*60))%60);
					long secs = ((timeMilliSec/(1000))%60);
					
					String timeStr = String.format("%02d:%02d:%02d", hours, mins,secs);
					line = String.format("%10s\t%19s\t%19s\t%5s",dayEntry.getKey(),dateTimeFormat.format(startDate),dateTimeFormat.format(endDate), timeStr);
					System.out.println(line);
					outTXTBR.write(line+"\n");
					
					if(timeMilliSec==0L) {
						currentDateRowFormatting = "style='background:red'";
					}
					
					outHTMLBR.write("<tr "+ currentDateRowFormatting + " >");  
					currentDateRowFormatting="";
					outHTMLBR.write("<td>" + dayEntry.getKey() + "</td>");
					outHTMLBR.write("<td>" + dateTimeFormat.format(startDate) + "</td>");
					outHTMLBR.write("<td>" + dateTimeFormat.format(endDate) + "</td>");
					outHTMLBR.write("<td>" + timeStr + "</td>");
					outHTMLBR.write("</tr>");
				}		
				
				outHTMLBR.write("</table></body></html>\n");
				outTXTBR.close();
				outHTMLBR.close();
				
				System.out.println("\n\nPlease check output at:");
				System.out.println("HTML Format: " + outHTMLFile.getAbsolutePath());
				System.out.println("Text Format: " + outTXTFile.getAbsolutePath());
			}
		} catch (IOException e) {
			System.err.println("***** Unable to read file: "
					+ inFile.getAbsolutePath() + " *****");
			e.printStackTrace();
		} catch (ParseException e) {
			System.err.println("***** Problem in parsing date *****");
			System.err.println("Line Number: " + lineNumber);
			System.err.println("Line: " + line);
			System.err.println("***********************************");
			e.printStackTrace();
		}

	}
}
