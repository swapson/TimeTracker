package com.swapnil.timetracker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
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

import com.swapnil.timetracker.util.PropertyReader;

public class TimeTracker {
	public static void main(String[] args) {
		try {
			Map<String, Object> colorCodeMap = PropertyReader.getPropertyMap();
			processTimeEntries(colorCodeMap);
		} catch(FileNotFoundException fnfex) {
			System.out.println("Existing");
			
		}
	}
	
	public static void processTimeEntries(Map<String, Object> colorCodeMap) {
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat(Constants.DATE_TIME_FORMATE);
		SimpleDateFormat dayTimeFormat = new SimpleDateFormat(Constants.DAY_TIME_FORMATE);
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
				CustomeDate cDate = new CustomeDate();
				if(line.contains("#")) {
					String[] fields = line.split(Constants.DATE_ENTRY_FIELD_SEPARATOR);
					cDate.setComment(fields[1]);
					cDate.setTime(dateTimeFormat.parse(fields[0]).getTime());
				} else {
					cDate.setComment("");
					cDate.setTime(dateTimeFormat.parse(line).getTime());
				}
				dates.add(cDate);
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
				
				line = String.format("%-10s\t%-10s\t%-20s\t%-20s\t%-5s\t\t%s","DATE","DAY","START","END","TIME","Comment");
				//System.out.println(line);
				outTXTBR.write(line+"\n");
				
				outHTMLBR.write("<html><head><style>th,td {padding:5px}</style></head><body><table cellspacing='0' cellpading='0' border='1'>\n");
				outHTMLBR.write("<tr><th>DATE</th><th>DAY</th><th>START</th><th>END</th><th>TIME</th><th>Comment</th></tr>\n");
				
				String currentDateRowFormatting = "style='background:yellow'";
				String bkColor="background:yellow";
				Map<Integer,String> minuteMap = ((Map<Integer,String>)colorCodeMap.get("minutes"));
				for(Entry<String,List<Date>> dayEntry: dayTimeEntries.entrySet() ) {
					CustomeDate startDate = null;
					Date endDate = null;
					if(dayEntry.getValue().size()>1) {
						Collections.sort(dayEntry.getValue());
						startDate = (CustomeDate)dayEntry.getValue().get(0);
						endDate = dayEntry.getValue().get(dayEntry.getValue().size()-1);
					} else {
						endDate = dayEntry.getValue().get(0);
						startDate = (CustomeDate) endDate; 
					}
					
					long timeMilliSec  = endDate.getTime() - startDate.getTime();
					int timInMinute = (int)(timeMilliSec/60000L); 
					
					long hours = (timeMilliSec/(1000*60*60));
					long mins = ((timeMilliSec/(1000*60))%60);
					long secs = ((timeMilliSec/(1000))%60);
					
					String timeStr = String.format("%02d:%02d:%02d", hours, mins,secs);
					line = String.format("%10s\t%-10s\t%20s\t%20s\t%5s\t%s",dayEntry.getKey(),dayTimeFormat.format(startDate) ,dateTimeFormat.format(startDate),dateTimeFormat.format(endDate), timeStr,startDate.getComment());
					//System.out.println(line);
					outTXTBR.write(line+"\n");
					
					if (startDate.getComment().length()>0) {
						if(startDate.getComment().trim().equalsIgnoreCase("Weekend")) {
							currentDateRowFormatting = String.format("style='%s'", colorCodeMap.get("weekend.color"));
						} /*else {
							currentDateRowFormatting = String.format("style='%s'", colorCodeMap.get("comment.color"));														
						}*/
					} else {
						currentDateRowFormatting = String.format("style='%s'", colorCodeMap.get("under.time.color"));
						for(Entry<Integer,String> minute: minuteMap.entrySet()) {
							if(timInMinute>=minute.getKey()) {
								currentDateRowFormatting = String.format("style='%s'", minute.getValue());
							}
						}
						
					}
					
					outHTMLBR.write("<tr "+ currentDateRowFormatting + " >");  
					currentDateRowFormatting="";
					outHTMLBR.write("<td>" + dayEntry.getKey() + "</td>");
					outHTMLBR.write("<td>" + dayTimeFormat.format(startDate) + "</td>");
					outHTMLBR.write("<td>" + dateTimeFormat.format(startDate) + "</td>");
					outHTMLBR.write("<td>" + dateTimeFormat.format(endDate) + "</td>");
					outHTMLBR.write("<td>" + timeStr + "</td>");
					outHTMLBR.write("<td>" + startDate.getComment() + "</td>");
					outHTMLBR.write("</tr>\n");
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
