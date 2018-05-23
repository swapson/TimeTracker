package com.swapnil.timetracker;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FeedTime {
	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_TIME_FORMATE);
		Calendar startCal = Calendar.getInstance();
		System.out.println("Time Feed: " + sdf.format(startCal.getTime()));

		File outFile = new File(Constants.TIME_TRACKER_IN_FILE_NAME);
		System.out.println(outFile.getAbsolutePath());
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(outFile, true));
			bw.write(sdf.format(startCal.getTime()) + "\n");
			bw.close();
		} catch (IOException e) {
			System.out.println("***** Unable to write file: " + outFile.getAbsolutePath() + " *****");
			e.printStackTrace();
		}
	}
}
