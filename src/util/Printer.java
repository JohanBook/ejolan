////////////////////////////////////////////////
// Printer.java
// A class responsible for IO.
// Johan Book
// 2015-09-12
////////////////////////////////////////////////

package util;

import java.io.File;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import settings.Settings;

// A class responsible for writing text to file
public class Printer {
	private PrintWriter writer;
	private final Settings settings;

	public Printer(String path, Settings settings) {
		this.settings = settings;
		
		// Make directory if nonexistent
		File file = new File("data");
		if (!file.exists())
			file.mkdir();

		// Initiate printwriter
		path = "data/" + path + ".dat";
		try {
			writer = new PrintWriter(path, "UTF-8");
		} catch (Exception e) {
			System.out.println("ERROR: Unable to write data to file, path: "
					+ path);
		}
		
		// Write date to header of file
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		writer.println("# " + dateFormat.format(new Date()));
	}

	public void write(String a) {
		writer.println(a);
	}

	public void write(double... a) {
		String text = settings.t + "\t";
		double sum = 0;
		if (settings.writeFractionOfPopulation)
			for (double x : a)
				sum += x;

		for (double x : a)
			if (settings.writeFractionOfPopulation)
				text += (x / sum) + "\t";
			else
				text += (int) x + "\t";
		writer.println(text);
	}

	public void close() {
		writer.close();
	}
}
