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
class Q4Process extends DatabaseProcess {

	private Database database;
	private CSVHandler csvHandler;

	private InputTable problemsInputTable = new InputTable();
	private InputTable personsInputTable = new InputTable();
	private InputTable attemptsInputTable = new InputTable();

	private static String COUNT_NAME = "N_PEOPLE_ANS";

	private static String QUERY_Q4 = "SELECT "+PROBLEMS_TABLE_NAME+"."+PROBLEM_ID_NAME+", COUNT("+ATTEMPTS_TABLE_NAME+"."+ATTEMPTS_PERSON_ID+") AS " + COUNT_NAME +
			" FROM  " + PROBLEMS_TABLE_NAME +
			" LEFT JOIN "+ATTEMPTS_TABLE_NAME+" on "+PROBLEMS_TABLE_NAME+"."+PROBLEM_ID_NAME+" = "+ATTEMPTS_TABLE_NAME+"." + ATTEMPTS_PROBLEM_ID +
			" GROUP BY "+PROBLEMS_TABLE_NAME+"."+PROBLEM_ID_NAME;


	public enum discrimination {
		good_correct,
		bottom_correct;
	}


	private class Q4_result{
		private int problem_id;
		private int n_person_ans;
		private float p_person_ans_corr_from_ans;
		private Q4Process.discrimination disc;

		private int n_person_ans_corr_from_ans;
		private List<Integer> person_who_answered_correctly;

		Q4_result(ResultSet result) throws SQLException{
			problem_id = result.getInt(PROBLEM_ID_NAME);
			n_person_ans = result.getInt(COUNT_NAME);

			p_person_ans_corr_from_ans = 0;
			n_person_ans_corr_from_ans = 0;
			person_who_answered_correctly = new ArrayList<Integer>();
		}

		void print(CSVPrinter printer) throws IOException {

			printer.printRecord(problem_id, n_person_ans, Math.round(p_person_ans_corr_from_ans)+"%", disc);
		}
	}

	private class Person_with_grade{
		private Person person;
		private int grade;
		private int attempts;

		Person_with_grade(Person p) {
			this.person = p;
			this.grade = 0;
			this.attempts = 0;
		}
	}

	List<Q4_result> objects_results = new ArrayList<Q4_result>();
	List<Person_with_grade> persons = new ArrayList<Person_with_grade>();
	List<Person_with_grade> persons_who_attempted = new ArrayList<Person_with_grade>();
	List<Person> good_students = new ArrayList<Person>();
	List<Person> bottom_students = new ArrayList<Person>();

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

		ResultSet query_results = database.executeQuery(QUERY_Q4);

		while(query_results.next())
		{
			Q4_result temp = new Q4_result(query_results);
			objects_results.add(temp);
		}

		ResultSet persons_results = database.executeQuery("SELECT * FROM PERSONS");

		while(persons_results.next())
		{
			Person_with_grade p = new Person_with_grade(new Person(persons_results));
			persons.add(p);
		}

		for (Q4_result item : objects_results) {

			ResultSet results_attempts = database.executeQuery("SELECT * FROM "+ATTEMPTS_TABLE_NAME+" WHERE "+ATTEMPTS_TABLE_NAME+"."+ATTEMPTS_PROBLEM_ID+"=" + item.problem_id);
			List<Attempt> attempts = new ArrayList<Attempt>();

			while (results_attempts.next()) {

				Attempt a = new Attempt(results_attempts);
				ResultSet result_problem = database.executeQuery("SELECT * FROM "+PROBLEMS_TABLE_NAME+" WHERE "+PROBLEMS_TABLE_NAME+"."+PROBLEM_ID_NAME+"=" + item.problem_id);

				if (result_problem.next())
				{
					Problem p = new Problem(result_problem);

					if (right_answer(p,a)) {
						item.n_person_ans_corr_from_ans += 1;
						item.person_who_answered_correctly.add(a.person_id);
						for(Person_with_grade pers : persons) {
							if(pers.person.id == a.person_id) {
								pers.grade += 1;
								pers.attempts += 1;
							}
						}
					}
					else
					{
						for(Person_with_grade pers : persons) {
							if(pers.person.id == a.person_id) {
								pers.attempts += 1;
							}
						}
					}
				}
			}

			if (item.n_person_ans != 0) {
				item.p_person_ans_corr_from_ans = ((float) item.n_person_ans_corr_from_ans / (float) item.n_person_ans) * 100;
			}
			else {
				item.p_person_ans_corr_from_ans = 0;
			}

		}

		for (Person_with_grade p : persons)
		{
			if (p.attempts != 0)
			{
				persons_who_attempted.add(p);
			}
		}

		Collections.sort(persons_who_attempted, new Comparator<Person_with_grade>() {
			public int compare(Person_with_grade a, Person_with_grade b) {
				if (a.grade > b.grade) return -1;
				else if (a.grade < b.grade) return 1;
				else return a.person.last_name.compareTo(b.person.last_name);
			}
		});


		int size = persons_who_attempted.size();
		int end = Math.round((float) size * ((float) 27 / (float) 100));

		for (int i = 0; i < end ; i++)
		{
			good_students.add(persons_who_attempted.get(i).person);
		}

		int start = Math.round((float)size*((float)73/(float)100));

		for (int i = size*(73/100); i < size; i++)
		{
			bottom_students.add(persons_who_attempted.get(i).person);
		}



		for (Q4_result problems : objects_results)
		{
			int good_who_answered_correctly = 0;
			int bottom_who_answered_correctly = 0;
			for (Person p : good_students) {
				if (problems.person_who_answered_correctly.contains(p.id)) good_who_answered_correctly += 1;

			}
			for (Person p : bottom_students) {
				if (problems.person_who_answered_correctly.contains(p.id)) bottom_who_answered_correctly += 1;
			}



			if ((((float)good_who_answered_correctly / (float)good_students.size()) - ((float)bottom_who_answered_correctly / (float)bottom_students.size())) > 0 ){
				problems.disc = discrimination.good_correct;
			}
			else
			{
				problems.disc = discrimination.bottom_correct;
			}
		}


	}


	// Implementation of the "writeOutput" method as specified by the base-class.
	protected void writeOutput() throws IOException {

		File outCSVFile = getOutputFile();
		CSVPrinter printer = csvHandler.createPrinter(outCSVFile);

		for (Q4_result out : objects_results) {

			out.print(printer);
		}

		System.out.println("Output file written");
	}

	// Constructor.
	Q4Process(Database database, CSVHandler csvHandler) {

		this.database = database;
		this.csvHandler = csvHandler;
	}
}
