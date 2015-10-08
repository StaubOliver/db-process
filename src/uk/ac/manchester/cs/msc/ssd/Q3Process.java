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
class Q3Process extends DatabaseProcess {

	private Database database;
	private CSVHandler csvHandler;

	private InputTable problemsInputTable = new InputTable();
	private InputTable personsInputTable = new InputTable();
	private InputTable attemptsInputTable = new InputTable();

	private static String COUNT_NAME = "N_PEOPLE_ANS";

	private static String QUERY_Q3 = "SELECT "+PROBLEMS_TABLE_NAME+"."+PROBLEM_ID_NAME+", COUNT("+ATTEMPTS_TABLE_NAME+"."+ATTEMPTS_PERSON_ID+") AS " + COUNT_NAME +
			" FROM  " + PROBLEMS_TABLE_NAME +
			" LEFT JOIN "+ATTEMPTS_TABLE_NAME+" on "+PROBLEMS_TABLE_NAME+"."+PROBLEM_ID_NAME+" = "+ATTEMPTS_TABLE_NAME+"." + ATTEMPTS_PROBLEM_ID +
			" GROUP BY "+PROBLEMS_TABLE_NAME+"."+PROBLEM_ID_NAME;



	private class Q3_result{
		private int problem_id;
		private int n_person_ans;
		private float p_person_ans_corr_from_ans;

		private int n_person_ans_corr_from_ans;

		Q3_result(ResultSet result) throws SQLException{
			problem_id = result.getInt(PROBLEM_ID_NAME);
			n_person_ans = result.getInt(COUNT_NAME);

			p_person_ans_corr_from_ans = 0;
			n_person_ans_corr_from_ans = 0;
		}

		void print(CSVPrinter printer) throws IOException {

			printer.printRecord(problem_id, n_person_ans, Math.round(p_person_ans_corr_from_ans)+"%");
		}
	}

	List<Q3_result> objects_results = new ArrayList<Q3_result>();

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

		ResultSet query_results = database.executeQuery(QUERY_Q3);

		while(query_results.next())
		{
			Q3_result temp = new Q3_result(query_results);
			objects_results.add(temp);
		}

		for (Q3_result item : objects_results) {

			ResultSet results_attempts = database.executeQuery("SELECT * FROM "+ATTEMPTS_TABLE_NAME+" WHERE "+ATTEMPTS_TABLE_NAME+"."+ATTEMPTS_PROBLEM_ID+"=" + item.problem_id);
			List<Attempt> attempts = new ArrayList<Attempt>();

			while (results_attempts.next()) {

				Attempt a = new Attempt(results_attempts);
				ResultSet result_problem = database.executeQuery("SELECT * FROM "+PROBLEMS_TABLE_NAME+" WHERE "+PROBLEMS_TABLE_NAME+"."+PROBLEM_ID_NAME+"=" + item.problem_id);

				if (result_problem.next())
				{
					Problem p = new Problem(result_problem);

					if (right_answer(p,a))
						item.n_person_ans_corr_from_ans += 1;
				}
			}
			if (item.n_person_ans != 0)
				item.p_person_ans_corr_from_ans = ((float)item.n_person_ans_corr_from_ans / (float)item.n_person_ans) * 100;
			else
				item.p_person_ans_corr_from_ans = 0;
		}
	}

	// Implementation of the "writeOutput" method as specified by the base-class.
	protected void writeOutput() throws IOException {

		File outCSVFile = getOutputFile();
		CSVPrinter printer = csvHandler.createPrinter(outCSVFile);

		for (Q3_result out : objects_results) {

			out.print(printer);
		}

		System.out.println("Output file written");
	}

	// Constructor.
	Q3Process(Database database, CSVHandler csvHandler) {

		this.database = database;
		this.csvHandler = csvHandler;
	}
}
