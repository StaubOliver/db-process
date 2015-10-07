package uk.ac.manchester.cs.msc.ssd.core;

import java.sql.*;
import java.util.*;

/*
 * Handles all database access for a particular process. Upon construction opens
 * a connection to a database named "DATABSE", creating the database if it does
 * not already exist.  After use, the connection will be closed by invocation of
 * the <code>closeAll</code> method. This method will be invoked after the relevant
 * process has completed its run.
 * <p>
 * All <code>Statement</code> and <code>ResultSet</code> objects that are created
 * via the relevant methods, will be registered, and will eventually be closed by
 * invocation of the <code>closeAll</code> method (see above).
 */
public class Database {

	static private final String DB_NAME = "DATABASE";

    static private final String DERBY_PROTOCOL = "jdbc:derby:";
	static private final String CONNECT_ARGS = ";create=true";
	static private final String CONNECT_URL = DERBY_PROTOCOL + DB_NAME + CONNECT_ARGS;

	static private final String CREATE_TABLE_FORMAT = "CREATE TABLE %s (%s)";
	static private final String DROP_TABLE_FORMAT = "DROP TABLE %s";
	static private final String ADD_TABLE_ROW_FORMAT = "INSERT INTO %s VALUES (%s)";

	private Connection connection;

	private List<Statement> openStatements = new ArrayList<Statement>();
	private List<ResultSet> openResultSets = new ArrayList<ResultSet>();

	/**
	 * Constructor that opens database connection.
	 *
	 * @throws SQLException if error occurs whilst opening connection
	 */
	public Database() throws SQLException {

		connection = DriverManager.getConnection(CONNECT_URL);
	}

	/**
	 * Closes all <code>Statement</code> and <code>ResultSet</code> objects
	 * that have been created, then closes the database connection. Will
	 * print warning messages if any errors occur whilst performing any of
	 * the close operations.
	 */
	public void closeAll() {

		closeAllStatements();
		closeAllResultSets();

		closeConnection();
	}

	/**
	 * Creates a database table by executing an update command of the form:
	 * <p>
	 * "CREATE TABLE <tableName> (<args>)"
	 * <p>
	 * If a table with the same name already exists, it will be deleted and
	 * a new table created.
	 *
	 * @param tableName Name of table to create
	 * @param args Arguments to update command specifying table columns, etc.
	 * @throws SQLException if error occurs whilst creating table
	 */
	public void createTable(String tableName, String args) throws SQLException {

		if (tableExists(tableName)) {

			executeUpdate(getDropTableCommand(tableName));
		}

		executeUpdate(getCreateTableCommand(tableName, args));
	}

	/**
	 * Adds a row to a table by executing an update command of the form:
	 * <p>
	 * "INSERT INTO <tableName> VALUES (<args>)</code>"
	 *
	 * @param tableName Name of table to create
	 * @param args Arguments to update command specifying value for each column
	 * @throws SQLException if error occurs whilst adding row
	 */
	public void addTableRow(String tableName, String args) throws SQLException {

		executeUpdate(getAddTableRowCommand(tableName, args));
	}

	/**
	 * Executes an update command over the database.
	 *
	 * @param command Command to be executed
	 * @throws SQLException if error occurs whilst executing command
	 */
	public void executeUpdate(String command) throws SQLException {

		createStatement().executeUpdate(command);
	}

	/**
	 * Executes a query over the database.
	 *
	 * @param query Query to be executed
	 * @return Results of query execution
	 * @throws SQLException if error occurs whilst executing query
	 */
	public ResultSet executeQuery(String query) throws SQLException {

		ResultSet resultSet = createStatement().executeQuery(query);

		openResultSets.add(resultSet);

		return resultSet;
	}

	private boolean tableExists(String tableName) throws SQLException {

		DatabaseMetaData metadata = connection.getMetaData();
		ResultSet resultSet = metadata.getTables(null, null, tableName, null);

		openResultSets.add(resultSet);

		return resultSet.next();
	}

	private Statement createStatement() throws SQLException {

		Statement statement = connection.createStatement();

		openStatements.add(statement);

		return statement;
	}

	private void closeConnection() {

		try {

			connection.close();
		}
		catch (SQLException e) {

			System.out.println("WARNING: Could not close Connection");
		}
	}

	private void closeAllStatements() {

		for (Statement statement : openStatements) {

			try {

				statement.close();
			}
			catch (SQLException e) {

				System.out.println("WARNING: Could not close Statement");
			}
		}
	}

	private void closeAllResultSets() {

		for (ResultSet resultSet : openResultSets) {

			try {

				resultSet.close();
			}
			catch (SQLException e) {

				System.out.println("WARNING: Could not close ResultSet");
			}
		}
	}

	private String getCreateTableCommand(String tableName, String args) {

		return String.format(CREATE_TABLE_FORMAT, tableName, args);
	}

	private String getDropTableCommand(String tableName) {

		return String.format(DROP_TABLE_FORMAT, tableName);
	}

	private String getAddTableRowCommand(String tableName, String args) {

		return String.format(ADD_TABLE_ROW_FORMAT, tableName, args);
	}
}
