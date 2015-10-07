package uk.ac.manchester.cs.msc.ssd.core;

import java.io.*;
import java.sql.*;
import java.util.*;

/*
 * Represents a process involving the setting up and accessing of
 * a database.
 */
public abstract class DatabaseProcess {

	static private final String IN_FILES_PATH = "files/in";
	static private final String OUT_FILES_PATH = "files/out";

	static private final String CSV_EXTENSION = ".csv";

	/**
	 * Provides a file located in the input-files directory.
	 *
	 * @param name Name of required file
	 * @return File in input-files directory with specified name
	 */
	static public File getInputFile(String name) {

		return getCSVFile(IN_FILES_PATH, name);
	}

	static private File getCSVFile(String path, String name) {

		return new File(path, name + CSV_EXTENSION);
	}

	/**
	 * Main method for running the relevant process. Invokes, in
	 * turn, the <code>readInput</code>, <code>runCoreProcess</code>
	 * and <code>writeOutput</code> methods.
	 *
	 * @throws IOException if any file-system access error occurs
	 * @throws SQLException if any database access error occurs
	 */
	public void run() throws IOException, SQLException {

		log("EXECUTING");

		readInput();
		runCoreProcess();
		writeOutput();

		log("FINISHED");
	}

	/**
	 * Abstract method whose implementations will read in any required
	 * CSV files.
	 *
	 * @throws IOException if any file-system access error occurs
	 */
	protected abstract void readInput() throws IOException;

	/**
	 * Abstract method whose implementations will execute the core
	 * process that will involve all required database acccess.
	 *
	 * @throws SQLException if any database access error occurs
	 */
	protected abstract void runCoreProcess() throws SQLException;

	/**
	 * Abstract method whose implementations will write out any
	 * resulting CSV files.
	 *
	 * @throws IOException if any file-system access error occurs
	 */
	protected abstract void writeOutput() throws IOException;

	/**
	 * Provides the single output file for this process. This file will
	 * be located in the output-files directory, with a name derived
	 * from the name of the relevant concrete extension of this class.
	 *
	 * @return File in output-files directory with name appropriate to
	 * this process
	 */
	protected File getOutputFile() {

		return getCSVFile(OUT_FILES_PATH, getProcessName());
	}

	private void log(String message) {

		System.out.println("\n" + message + ": " + getProcessName() + "\n");
	}

	private String getProcessName() {

		return getClass().getSimpleName();
	}

	//relative to the PROBLEMS file and table

	static public final File PROBLEMS_IN_FILE = getInputFile("problems");

	static public final String PROBLEMS_TABLE_NAME = "PROBLEMS";

	static public final String PROBLEM_ID_NAME = "ID";
	static public final String PROBLEM_OPERATOR_NAME = "OPERATOR";
	static public final String PROBLEM_ARG1_NAME = "ARG1";
	static public final String PROBLEM_ARG2_NAME = "ARG2";

	static public final String PROBLEMS_TABLE_CREATION_ARGS
			= PROBLEM_ID_NAME + " integer NOT NULL, "
			+ PROBLEM_ARG1_NAME + " integer NOT NULL, "
			+ PROBLEM_OPERATOR_NAME + " char(1), "
			+ PROBLEM_ARG2_NAME + " integer NOT NULL";

	public class Problem {

		private int id;
		private int arg1;
		private String operator;
		private int arg2;

		Problem(ResultSet result) throws SQLException {
			id = result.getInt(PROBLEM_ID_NAME);
			arg1 = result.getInt(PROBLEM_ARG1_NAME);
			arg2 = result.getInt(PROBLEM_ARG2_NAME);
			operator = result.getString(PROBLEM_OPERATOR_NAME);
		}

		public String toString() {
			return "Problem-"+id+" ("+arg1+" "+operator+" "+arg2+")";
		}
	}

	//relative to the PERSONS file and table

	public static final File PERSONS_IN_FILE = getInputFile("people");

	public static final String PERSONS_TABLE_NAME = "PERSONS";

	public static final String PERSON_ID_NAME = "PERSON_ID";
	public static final String PERSON_FIRST_NAME = "FIRST_NAME";
	public static final String PERSON_LAST_NAME = "LAST_NAME";
	public static final String PERSON_COMPANY_NAME = "COMPANY_NAME";
	public static final String PERSON_ADRESS = "ADRESS";
	public static final String PERSON_CITY = "CITY";
	public static final String PERSON_COUNTY = "COUNTY";
	public static final String PERSON_POSTAL = "POSTAL";
	public static final String PERSON_PHONE_NUMBER1 = "PHONE1";
	public static final String PERSON_PHONE_NUMBER2 = "PHONE2";
	public static final String PERSON_EMAIL = "EMAIL";
	public static final String PERSON_WEB = "WEB";

	public static final String PERSONS_TABLE_CREATION_ARGS
			= PERSON_ID_NAME + " integer NOT NULL,"
			+ PERSON_FIRST_NAME + " varchar(255),"
			+ PERSON_LAST_NAME + " varchar(255),"
			+ PERSON_COMPANY_NAME + " varchar(255),"
			+ PERSON_ADRESS + " varchar(255),"
			+ PERSON_CITY + " varchar(255),"
			+ PERSON_COUNTY + " varchar(255),"
			+ PERSON_POSTAL + " varchar(255),"
			+ PERSON_PHONE_NUMBER1 + " varchar(255),"
			+ PERSON_PHONE_NUMBER2 + " varchar(255),"
			+ PERSON_EMAIL + " varchar(255),"
			+ PERSON_WEB + " varchar(255)";

	public class Person {

		private int id;
		private String first_name;
		private String last_name;
		private String company_name;
		private String adress;
		private String city;
		private String county;
		private String postal;
		private String phone_number1;
		private String phone_number2;
		private String email;
		private String web;

		Person(ResultSet result) throws SQLException {
			id = result.getInt(PERSON_ID_NAME);
			first_name = result.getString(PERSON_FIRST_NAME);
			last_name = result.getString(PERSON_LAST_NAME);
			company_name = result.getString(PERSON_COMPANY_NAME);
			adress = result.getString(PERSON_ADRESS);
			city = result.getString(PERSON_CITY);
			postal = result.getString(PERSON_POSTAL);
			phone_number1 = result.getString(PERSON_PHONE_NUMBER1);
			phone_number2 = result.getString(PERSON_PHONE_NUMBER2);
			email = result.getString(PERSON_EMAIL);
			web = result.getString(PERSON_WEB);
		}
	}

	//relative to ATTEMPTS file and table

	public static final File ATTEMPTS_IN_FILE = getInputFile("attempts");

	public static final String ATTEMPTS_TABLE_NAME = "ATTEMPTS";

	public static final String ATTEMPTS_PERSON_ID = "PERSON_ID";
	public static final String ATTEMPTS_PROBLEM_ID = "PROBLEM_ID";
	public static final String ATTEMPTS_ANSWER = "ANSWER";

	public static final String ATTEMPTS_TABLE_CREATION_ARGS
			= ATTEMPTS_PERSON_ID + " integer NOT NULL,"
			+ ATTEMPTS_PROBLEM_ID + " integer NOT NULL,"
			+ ATTEMPTS_ANSWER + " integer NOT NULL";

	public class Attempt {
		private int person_id;
		private int problem_id;
		private int answer;

		private Person person;
		private Problem problem;

		Attempt(ResultSet result, List<Problem> problems, List<Person> persons) throws SQLException
		{
			person_id = result.getInt(ATTEMPTS_PERSON_ID);
			problem_id = result.getInt(ATTEMPTS_PROBLEM_ID);
			answer = result.getInt(ATTEMPTS_ANSWER);
		}
	}

	public void initiate_database(Database data) throws SQLException{
		data.createTable(PROBLEMS_TABLE_NAME, PROBLEMS_TABLE_CREATION_ARGS);
		data.createTable(PERSONS_TABLE_NAME, PERSONS_TABLE_CREATION_ARGS);
		data.createTable(ATTEMPTS_TABLE_NAME, ATTEMPTS_TABLE_CREATION_ARGS);
	}

	public boolean right_answer(Problem p, Attempt a){
		boolean res = false;
		char o = p.operator.charAt(0);
		switch (o){
			case '+' : return (a.answer == p.arg1 + p.arg2);
			case '-' : return (a.answer == p.arg1 - p.arg2);
			case '*' : return (a.answer == p.arg1 * p.arg2);
			case '/' : return (a.answer == p.arg1 / p.arg2);
		}
		return res;
	}

}
