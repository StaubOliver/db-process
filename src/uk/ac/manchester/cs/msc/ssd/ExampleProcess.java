package uk.ac.manchester.cs.msc.ssd;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.apache.commons.csv.*;

import uk.ac.manchester.cs.msc.ssd.core.*;

 //
 // Example process that performs the following sequence of actions:
 //
 // * Reads in "problems.csv" file
 // * Creates database table named "PROBLEMS" and populates it with
 //	  the contents of "problems.csv"
 // * Executes a simple "SELECT" query to obtain entire contents
 //	  of "PROBLEMS" table, storing results as an array of objects of type
 //	  "Problem" (which is an inner-class of this one)
 // * Writes the contents of the "Problem" array to an output CSV file
 //
class ExampleProcess extends DatabaseProcess {

	// Input CSV file to be read in from "files/in" directory.
	// Obtained via the getInputFile method which is provided by the
	// base-class (Note: You don't specify the ".csv" extension)
	static private final File PROBLEMS_IN_FILE = getInputFile("problems");

	// Name of "PROBLEMS" table
	static private final String PROBLEMS_TABLE_NAME = "PROBLEMS";

	// Names of columns for "PROBLEMS" table
	static private final String PROBLEM_ID_NAME = "ID";
	static private final String PROBLEM_OP_NAME = "OPERATOR";
	static private final String PROBLEM_ARG1_NAME = "ARG1";
	static private final String PROBLEM_ARG2_NAME = "ARG2";

	// Arguments for "CREATE TABLE ..." command that will create
	// "PROBLEMS" table
	static private final String PROBLEMS_TABLE_CREATION_ARGS
									= PROBLEM_ID_NAME + " integer NOT NULL, "
									+ PROBLEM_ARG1_NAME + " integer NOT NULL, "
									+ PROBLEM_OP_NAME + " char(1), "
									+ PROBLEM_ARG2_NAME + " integer NOT NULL";

	// Query for selecting everything from "PROBLEMS" table
	static private final String SELECT_ALL_PROBLEMS_QUERY
									= "SELECT * FROM "
									+ PROBLEMS_TABLE_NAME;

	// Object that will handle all database access
	// Note: The Database class pro
	private Database database;

	// Object that will handle all CSV file access
	private CSVHandler csvHandler;

	// Object into which the contents of "problems.csv" file will be read
	private InputTable problemsInputTable = new InputTable();

	// Array into which the results of the "SELECT" query will be written
	private List<Problem> problems = new ArrayList<Problem>();

	// Inner-class representing a specific problem
	private class Problem {

		private int id;
		private String op;
		private int arg1;
		private int arg2;

		// General Java toString method
		public String toString() {

			return "Problem-" + id + " (" + arg1 + " " + op + " " + arg2 + ")";
		}

		// Construct Problem object from current state of object representing
		// results of "SELECT" query. This results object will correspond to a
		// particular row in the table. The constructed object is added to the
		// relevant array.
		Problem(ResultSet results) throws SQLException {

			id = results.getInt(PROBLEM_ID_NAME);
			op = results.getString(PROBLEM_OP_NAME);
			arg1 = results.getInt(PROBLEM_ARG1_NAME);
			arg2 = results.getInt(PROBLEM_ARG2_NAME);

			problems.add(this);

			System.out.println("RETRIEVED: " + this);
		}

		// Render problem as a line in the output CSV file
		void print(CSVPrinter printer) throws IOException {

			printer.printRecord(id, op, arg1, arg2);
		}
	}

	// Implementation of the "readInput" method as specified by the base-class.
	// This implementation simply reads the "problems.csv" file into the
	// relevant InputTable object.
	protected void readInput() throws IOException {

		problemsInputTable.readFromFile(csvHandler, PROBLEMS_IN_FILE);
	}

	// Implementation of the "runCoreProcess" method as specified by the base-class.
	// This implementation executes the required query, and stores the results as an
	// array of Problem objects.
	protected void runCoreProcess() throws SQLException {

		// Create "PROBLEMS" table in database
		database.createTable(PROBLEMS_TABLE_NAME, PROBLEMS_TABLE_CREATION_ARGS);

		// Write contents of "problems.csv" file from InputTable object into
		// "PROBLEMS" table
		problemsInputTable.writeToDatabase(database, PROBLEMS_TABLE_NAME);

		// Execute query to select everything from the "PROBLEMS" table
		ResultSet results = database.executeQuery(SELECT_ALL_PROBLEMS_QUERY);

		// Save query results as array of Problems objects
		while (results.next()) {

			// Construct Problem object and add it to the array
			// (the adding is done by the Problem constructor)
			new Problem(results);
		}
	}

	// Implementation of the "writeOutput" method as specified by the base-class.
	// This implementation writes the array of Problem objects to the output CSV
	// file.
	protected void writeOutput() throws IOException {

	 	// Get output CSV file for this process. This will be a file
	 	// located in the output-files directory, with a name derived
	 	// from the name of this particular extention of the base-class
	 	// (hence for this class the file will be called "ExampleProcess.csv")
	 	File outCSVFile = getOutputFile();

		// Create "Commons CSV" printer object for writing to output CSV
		// file.
		CSVPrinter printer = csvHandler.createPrinter(outCSVFile);

		// Render each Problem objects as a line in output CSV file
		for (Problem problem : problems) {

			problem.print(printer);
		}
	}

	// Constructor
	ExampleProcess(Database database, CSVHandler csvHandler) {

		this.database = database;
		this.csvHandler = csvHandler;
	}
}
