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
class Q5Process extends DatabaseProcess {

	private Database database;
	private CSVHandler csvHandler;

	private InputTable problemsInputTable = new InputTable();
	private InputTable personsInputTable = new InputTable();
	private InputTable attemptsInputTable = new InputTable();

	private class Q5_Result {
		private String A_last_name;
		private String A_first_name;
		private String B_last_name;
		private String B_first_name;

		int m;
		int n_same_answer;

		Q5_Result(String A_last, String A_first, String B_last, String B_first, int n_m, int n_n)
		{
			A_last_name = A_last;
			A_first_name = A_first;
			B_last_name = B_last;
			B_first_name = B_first;
			m = n_m;
			n_same_answer = n_n;
		}

		void print(CSVPrinter printer) throws IOException {

			printer.printRecord(A_first_name, A_last_name, B_last_name, B_first_name, m, n_same_answer);
		}
	}

	List<Person> persons = new ArrayList<Person>();
	List<Attempt> attempts = new ArrayList<Attempt>();
	List<Q5_Result> results = new ArrayList<Q5_Result>();

	// Implementation of the "readInput" method as specified by the base-class.
	protected void readInput() throws IOException {

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

		ResultSet persons_results = database.executeQuery("SELECT * FROM "+PERSONS_TABLE_NAME);
		while (persons_results.next())
		{
			Person p = new Person(persons_results);
			persons.add(p);
		}

		ResultSet answers_results = database.executeQuery("SELECT * FROM " + ATTEMPTS_TABLE_NAME);
		while (answers_results.next()){
			Attempt a = new Attempt(answers_results);
			attempts.add(a);
		}

		for (Person p1 : persons)
		{
			for (Attempt a : attempts)
			{
				if (p1.id == a.person_id) p1.attempts.add(a);
			}
		}

		for (int i = 0; i < persons.size(); i++)
		{
			Person p1 = persons.get(i);

			for (int j = i + 1; j< persons.size(); j++)
			{
				Person p2 = persons.get(j);
				if (p1.id != p2.id)
				{
					int m = 0;
					if (p1.attempts.size() > p2.attempts.size()) m = p2.attempts.size();
					else m = p1.attempts.size();

					if (m != 0) {
						int n_same_way = 0;
						for (Attempt a : p1.attempts) {
							for (Attempt b : p2.attempts) {
								if (a.problem_id == b.problem_id) {
									if (a.answer == b.answer) {
										n_same_way += 1;
									}
								}
							}
						}

						if ((float) n_same_way >= ((float) m / (float) 2)) {
							Q5_Result collusion = new Q5_Result(p1.last_name, p1.first_name, p2.last_name, p2.first_name, m, n_same_way);
							results.add(collusion);
						}
					}
				}
			}
		}
	}

	// Implementation of the "writeOutput" method as specified by the base-class.
	protected void writeOutput() throws IOException {

		File outCSVFile = getOutputFile();
		CSVPrinter printer = csvHandler.createPrinter(outCSVFile);

		for (Q5_Result out : results) {

			out.print(printer);
		}

		System.out.println("Output file written");
	}

	// Constructor.
	Q5Process(Database database, CSVHandler csvHandler) {

		this.database = database;
		this.csvHandler = csvHandler;
	}
}
