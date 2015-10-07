package uk.ac.manchester.cs.msc.ssd;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.apache.commons.csv.*;

import uk.ac.manchester.cs.msc.ssd.core.*;

//
// THIS CLASS IS A TEMPLATE WHOSE IMPLEMENTATION SHOULD BE PROVIDED
// BY THE STUDENT IN ORDER TO PROVIDE A SOLUTION TO PROBLEM 1.
//
// THE "ExampleProcess" CLASS SHOULD BE USED AS A GUIDE IN CREATING
// THE IMPLEMENTATION.
//
class Q1Process extends DatabaseProcess {

	private Database database;
	private CSVHandler csvHandler;

	private InputTable problemsInputTable = new InputTable();
	private InputTable personsInputTable = new InputTable();
	private InputTable attemptsInputTable = new InputTable();

	private static String COUNT_NAME = "N_PROBLEMS";


	private static String QUERY_Q1 = "select "+PERSONS_TABLE_NAME+"."+PERSON_LAST_NAME+", "+PERSONS_TABLE_NAME+"."+PERSON_FIRST_NAME+ " , COUNT("+ ATTEMPTS_TABLE_NAME + "."+ATTEMPTS_PROBLEM_ID+") AS "+ COUNT_NAME +
			" from "+PERSONS_TABLE_NAME+"" +
			" left join "+ ATTEMPTS_TABLE_NAME + " on "+PERSONS_TABLE_NAME+"."+PERSON_ID_NAME +" = "+ ATTEMPTS_TABLE_NAME + "." + ATTEMPTS_PERSON_ID +
			" group by "+PERSONS_TABLE_NAME+"."+PERSON_LAST_NAME+", "+PERSONS_TABLE_NAME+"."+PERSON_FIRST_NAME;


	private class Q1_result{
		private String last_name;
		private String first_name;
		private int count;

		Q1_result(ResultSet result) throws SQLException{
			last_name = result.getString(PERSON_LAST_NAME);
			first_name = result.getString(PERSON_FIRST_NAME);
			count = result.getInt("N_PROBLEMS");
		}

		void print(CSVPrinter printer) throws IOException {

			printer.printRecord(last_name, first_name, count);
		}
	}

	List<Q1_result> objects_results = new ArrayList<Q1_result>();

	// Implementation of the "readInput" method as specified by the base-class.
	protected void readInput() throws IOException{

		problemsInputTable.readFromFile(csvHandler, PROBLEMS_IN_FILE);
		personsInputTable.readFromFile(csvHandler, PERSONS_IN_FILE);
		attemptsInputTable.readFromFile(csvHandler, ATTEMPTS_IN_FILE);
		System.out.println("Files read");

	}

	// Implementation of the "runCoreProcess" method as specified by the base-class.
	protected void runCoreProcess() throws SQLException {

		initiate_database(database);
		System.out.println("Database initiated");

		problemsInputTable.writeToDatabase(database, PROBLEMS_TABLE_NAME);
		personsInputTable.writeToDatabase(database, PERSONS_TABLE_NAME);
		attemptsInputTable.writeToDatabase(database, ATTEMPTS_TABLE_NAME);
		System.out.println("Database populated");

		ResultSet query_results = database.executeQuery(QUERY_Q1);

		while(query_results.next())
		{
			Q1_result temp = new Q1_result(query_results);
			objects_results.add(temp);
		}

		System.out.println("Results processed");
	}

	// Implementation of the "writeOutput" method as specified by the base-class.
	protected void writeOutput() throws IOException {

		File outCSVFile = getOutputFile();
		CSVPrinter printer = csvHandler.createPrinter(outCSVFile);

		for (Q1_result out : objects_results) {

			out.print(printer);
		}

		System.out.println("Output file written");
	}

	// Constructor.
	Q1Process(Database database, CSVHandler csvHandler) {

		this.database = database;
		this.csvHandler = csvHandler;
	}
}
