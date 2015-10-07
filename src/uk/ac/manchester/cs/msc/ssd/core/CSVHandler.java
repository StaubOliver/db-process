package uk.ac.manchester.cs.msc.ssd.core;

import java.io.*;
import java.util.*;
import java.nio.charset.Charset;

import org.apache.commons.csv.*;

/*
 * Handles all reading and writing of CSV files for a particular process. Uses
 * the "Commons CSV" package. See:
 * <p>
 *   https://commons.apache.org/proper/commons-csv/
 * <p>
 * All <code>CSVParser</code>, <code>CSVPrinter</code> and <code>FileWriter</code>
 * objects that are created via the relevant methods, will be registered, and will
 * eventually be closed by invocation of the <code>closeAll</code> method. This
 * method will be invoked after the relevant process has completed its run.
 */
public class CSVHandler {

	static private final Charset UTF_8 = Charset.forName("UTF-8");

	private List<CSVParser> openParsers = new ArrayList<CSVParser>();
	private List<CSVPrinter> openPrinters = new ArrayList<CSVPrinter>();
	private List<FileWriter> openFileWriters = new ArrayList<FileWriter>();

	/**
	 * Creates a parser for a reading from particular CSV file.
	 *
	 * @param csvFile CSV file to be read from
	 * @returns Iterator for reading records from CSV file
	 * @throws IOException if error creating parser
	 */
	public Iterator<CSVRecord> createParser(File csvFile) throws IOException {

		CSVParser parser = CSVParser.parse(csvFile, UTF_8, CSVFormat.DEFAULT);

		openParsers.add(parser);

		return getNonHeaderRecords(parser);
	}

	/**
	 * Creates a printer for writing to a particular CSV file.
	 *
	 * @param csvFile CSV file to be parsed written to
	 * @returns Created printer
	 * @throws IOException if error creating printer
	 */
	public CSVPrinter createPrinter(File csvFile) throws IOException {

		FileWriter writer = new FileWriter(csvFile);
		CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);

		openPrinters.add(printer);
		openFileWriters.add(writer);

		return printer;
	}

	/**
	 * Closes all <code>CSVParser</code>, <code>CSVPrinter</code> and
	 * <code>FileWriter</code> objects that have been created. Will print
	 * warning messages if any errors occur whilst performing any of the
	 * close operations.
	 */
	public void closeAll() {

		closeAllParsers();
		closeAllPrinters();
		closeAllFileWriters();
	}

	private Iterator<CSVRecord> getNonHeaderRecords(CSVParser parser) {

		Iterator<CSVRecord> records = parser.iterator();

		if (records.hasNext()) {

			records.next();
		}

		return records;
	}

	private void closeAllParsers() {

		for (CSVParser parser : openParsers) {

			try {

				parser.close();
			}
			catch (IOException e) {

				System.out.println("WARNING: Could not close CSVParser");
			}
		}
	}

	private void closeAllPrinters() {

		for (CSVPrinter printer : openPrinters) {

			try {

				printer.close();
			}
			catch (IOException e) {

				System.out.println("WARNING: Could not close CSVPrinter");
			}
		}
	}

	private void closeAllFileWriters() {

		for (FileWriter writer : openFileWriters) {

			try {

				writer.close();
			}
			catch (IOException e) {

				System.out.println("WARNING: Could not close FileWriter");
			}
		}
	}
}
