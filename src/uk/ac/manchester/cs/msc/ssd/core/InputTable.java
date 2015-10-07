package uk.ac.manchester.cs.msc.ssd.core;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.apache.commons.csv.*;

/*
 * Represents a table that is read in from a CSV file, and subsequently
 * written to the database.
 */
public class InputTable {

	private List<String> addRowCommandArgs = new ArrayList<String>();

	/**
	 * Reads the table from a CSV file.
	 *
	 * @param csvHandler CSV handler for current process
	 * @param csvFile CSV file to be read from
	 * @throws IOException if error occurs accesing CSV file
	 */
	public void readFromFile(CSVHandler csvHandler, File csvFile) throws IOException {

		Iterator<CSVRecord> records = csvHandler.createParser(csvFile);

		while (records.hasNext()) {

			addRowCommandArgs.add(getAddRowCommandArgs(records.next()));
		}
	}

	/**
	 * Writes the table to the database.
	 *
	 * @param database Database handler for current process
	 * @param tableName Name of table to write to
	 * @throws SQLException if error occurs writing to database
	 */
	public void writeToDatabase(Database database, String tableName) throws SQLException {

		for (String args : addRowCommandArgs) {

			database.addTableRow(tableName, args);
		}
	}

	private String getAddRowCommandArgs(CSVRecord record) {

		StringBuilder args = new StringBuilder();
		Iterator<String> values = record.iterator();

		while (values.hasNext()) {

			args.append(checkWrapAddRowCommandArg(values.next()) + ",");
		}

		return args.substring(0, args.length() - 1);
	}

	private String checkWrapAddRowCommandArg(String arg) {

		try {

			Integer.parseInt(arg);
		}
		catch (NumberFormatException e) {

			arg = ("'" + arg + "'");
		}

		return arg;
	}
}
