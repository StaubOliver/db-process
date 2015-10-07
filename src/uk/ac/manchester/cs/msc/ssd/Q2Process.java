package uk.ac.manchester.cs.msc.ssd;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.apache.commons.csv.*;

import uk.ac.manchester.cs.msc.ssd.core.*;

import javax.xml.transform.Result;

//
// THIS CLASS IS A TEMPLATE WHOSE IMPLEMENTATION SHOULD BE PROVIDED
// BY THE STUDENT IN ORDER TO PROVIDE A SOLUTION TO PROBLEM 1.
//
// THE "ExampleProcess" CLASS SHOULD BE USED AS A GUIDE IN CREATING
// THE IMPLEMENTATION.
//
class Q2Process extends DatabaseProcess {

	private Database database;
	private CSVHandler csvHandler;

	private static final String N_PROBLEMS_ANSWERED_FROM_ALL = "N_PROBLEMS_ANSWERED_FROM_ALL";
	private static final String P_PROBLEMS_ANSWERED_FROM_ALL = "P_PROBLEMS_ANSWERED_CORR";
	private static final String N_PROBLEMS_ANSWERED_CORR = "N_PROBLEMS_ANSWERED_FROM_ALL";
	private static final String P_PROBLEMS_ANSWERED_CORR_FROM_ANS = "N_PROBLEMS_ANSWERED_CORR_FROM_ALL";
	private static final String P_PROBLEMS_ANSWERED_CORR_FROM_ALL = "P_PROBLEMS_ANSWERED_CORR_FROM_ANS";


	private InputTable problemsInputTable = new InputTable();
	private InputTable personsInputTable = new InputTable();
	private InputTable attemptsInputTable = new InputTable();

	private int N_PROBLEMS_TOTAL;


	private static String QUERY_Q2 = "SELECT PERSONS.PERSON_ID, PERSONS.LAST_NAME, PERSONS.FIRST_NAME, PERSONS.POSTAL, COUNT(DISTINCT ATTEMPTS.PROBLEM_ID) AS "+N_PROBLEMS_ANSWERED_FROM_ALL+//+", COUNT(DISTINCT ATTEMPTS.PROBLEM_ID) / COUNT(DISTINCT PROBLEMS.ID) * 100 AS "+P_PROBLEMS_ANSWERED_FROM_ALL +
			" FROM  PROBLEMS, PERSONS" +
			" LEFT JOIN ATTEMPTS ON PERSONS.PERSON_ID = ATTEMPTS.PERSON_ID" +
			" GROUP BY PERSONS.PERSON_ID, PERSONS.LAST_NAME, PERSONS.FIRST_NAME, PERSONS.POSTAL";

	private class Q2_result{

		private int person_id;
		private String last_name;
		private String first_name;
		private String postal;
		private int n_problems_ans_from_all;
		private float p_problems_ans_from_all;
		private int n_problems_ans_correctly;
		private float p_problems_ans_correctly_from_ans;
		private float p_problems_ans_correctly_from_all;

		Q2_result(ResultSet res) throws SQLException{
			person_id = res.getInt(PERSON_ID_NAME);
			last_name = res.getString(PERSON_LAST_NAME);
			first_name = res.getString(PERSON_FIRST_NAME);
			postal = res.getString(PERSON_POSTAL);
			n_problems_ans_from_all = res.getInt(N_PROBLEMS_ANSWERED_FROM_ALL);

			p_problems_ans_from_all = 0;
			n_problems_ans_correctly = 0;
			p_problems_ans_correctly_from_ans = 0;
			p_problems_ans_correctly_from_all = 0;
		}

		void print(CSVPrinter printer) throws IOException {
			printer.printRecord(last_name, first_name, postal, n_problems_ans_from_all, p_problems_ans_from_all+"%", n_problems_ans_correctly, p_problems_ans_correctly_from_ans+"%", p_problems_ans_correctly_from_all+"%");
		}
	}

	List<Q2_result> objects_results = new ArrayList<Q2_result>();

	List<Person> persons = new ArrayList<Person>();

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

		ResultSet n_total = database.executeQuery("SELECT COUNT(DISTINCT PROBLEMS."+PROBLEM_ID_NAME+") AS N FROM PROBLEMS");

		if (n_total.next()){
			N_PROBLEMS_TOTAL = n_total.getInt("N");
		}

		ResultSet query_results = database.executeQuery(QUERY_Q2);

		while(query_results.next())
		{
			Q2_result temp = new Q2_result(query_results);
			objects_results.add(temp);
		}

		for (Q2_result item : objects_results) {

			if (N_PROBLEMS_TOTAL != 0) {
				item.p_problems_ans_from_all = ((float)item.n_problems_ans_from_all / (float)N_PROBLEMS_TOTAL) * 100;
			} else {
				item.p_problems_ans_from_all = 0;
			}

			ResultSet results_attempts = database.executeQuery("SELECT * FROM ATTEMPTS WHERE ATTEMPTS.PERSON_ID="+item.person_id);
			List<Attempt> attempts = new ArrayList<Attempt>();
			while (results_attempts.next())
			{
				Attempt a = new Attempt(results_attempts);

				ResultSet result_problem = database.executeQuery("SELECT * FROM PROBLEMS WHERE PROBLEMS.ID="+a.person_id);

				if (result_problem.next()) {
					Problem p = new Problem(result_problem);

					if (right_answer(p,a)){
						item.n_problems_ans_correctly += 1;
					}

					if (N_PROBLEMS_TOTAL != 0) {
						item.p_problems_ans_correctly_from_all = ((float)item.n_problems_ans_correctly / (float)N_PROBLEMS_TOTAL) * 100;
					} else {
						item.p_problems_ans_correctly_from_all = 0;
					}

					if (item.n_problems_ans_correctly != 0){
						item.p_problems_ans_correctly_from_ans = ((float)item.n_problems_ans_correctly / item.n_problems_ans_from_all) * 100;
					} else {
						item.p_problems_ans_correctly_from_ans = 0;
					}
				}
			}
		}

		objects_results.sort(new Comparator<Q2_result>(){
			public int compare(Q2_result a, Q2_result b) {
				int postal_comparison = a.postal.compareTo(b.postal);

				if (postal_comparison != 0){
					return postal_comparison;
				}
				else
				{
					if (a.p_problems_ans_correctly_from_ans > b.p_problems_ans_correctly_from_all) return 1;
					else if (a.p_problems_ans_correctly_from_ans < b.p_problems_ans_correctly_from_all) return -1;
					else {
						if (a.p_problems_ans_correctly_from_all > b.p_problems_ans_correctly_from_all) return 1;
						else if (a.p_problems_ans_correctly_from_all < b.p_problems_ans_correctly_from_all) return -1;
						else return 0;
					}
				}
			}
		});

	}

	// Implementation of the "writeOutput" method as specified by the base-class.
	protected void writeOutput() throws IOException {
		File outCSVFile = getOutputFile();
		CSVPrinter printer = csvHandler.createPrinter(outCSVFile);

		for (Q2_result out : objects_results) {

			out.print(printer);
		}

		System.out.println("Output file written");
	}

	// Constructor.
	Q2Process(Database database, CSVHandler csvHandler) {

		this.database = database;
		this.csvHandler = csvHandler;
	}
}
