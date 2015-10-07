package uk.ac.manchester.cs.msc.ssd.core;

import java.io.*;
import java.sql.*;

/*
 * Invokes a particular process to run over the database.
 */
public abstract class DatabaseProcessRunner {

	/**
	 * Invokes the relevant process. Performs the following sequence of
	 * actions:
	 * <ul>
	 *   <li>Creates <code>Database</code> and <code>CSVHandler</code>
	 *	 objects to be used by process
	 *   <li>Invokes <code>createProcess</code> method to create process
	 *   <li>Invokes <code>run</code> on created process
	 *   <li>Clears up by invoking <code>closeAll</code> methods on
	 *	 <code>Database</code> and <code>CSVHandler</code> objects
	 * </ul>
	 *
	 * @throws IOException if any file-system access error occurs
	 * @throws SQLException if any database access error occurs
	 */
	public void run() throws IOException, SQLException {

		Database database = new Database();
		CSVHandler csvHandler = new CSVHandler();

		createProcess(database, csvHandler).run();

		database.closeAll();
		csvHandler.closeAll();
	}

	/**
	 * Abstract method whose implementations will create the process
	 * that is to be run.
	 *
	 * @param database Database handler for process
	 * @param csvHandler CSV handler for process
	 * @return Created process
	 */
	protected abstract DatabaseProcess createProcess(
											Database database,
											CSVHandler csvHandler);
}
