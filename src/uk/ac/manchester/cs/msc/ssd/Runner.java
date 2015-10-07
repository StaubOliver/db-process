package uk.ac.manchester.cs.msc.ssd;

import java.io.*;
import java.sql.*;

import uk.ac.manchester.cs.msc.ssd.core.*;

//
// Point-of-entry class. Provides "main" method that runs process
// represented by "ExampleProcess" class, followed in turn by processes
// represented by all "Q?Process" classes, for ? = 1..5.
//
public class Runner {

	// Runs all required processes.
    public static void main(String[] args) throws IOException, SQLException {

		new ExampleProcessRunner().run();

		new Q1ProcessRunner().run();
		new Q2ProcessRunner().run();
		new Q3ProcessRunner().run();
		new Q4ProcessRunner().run();
		new Q5ProcessRunner().run();
	}
}

class ExampleProcessRunner extends DatabaseProcessRunner {

	protected DatabaseProcess createProcess(
								Database database,
								CSVHandler csvHandler) {

		return new ExampleProcess(database, csvHandler);
	}
}

class Q1ProcessRunner extends DatabaseProcessRunner {

	protected DatabaseProcess createProcess(
								Database database,
								CSVHandler csvHandler) {

		return new Q1Process(database, csvHandler);
	}
}

class Q2ProcessRunner extends DatabaseProcessRunner {

	protected DatabaseProcess createProcess(
								Database database,
								CSVHandler csvHandler) {

		return new Q2Process(database, csvHandler);
	}
}

class Q3ProcessRunner extends DatabaseProcessRunner {

	protected DatabaseProcess createProcess(
								Database database,
								CSVHandler csvHandler) {

		return new Q3Process(database, csvHandler);
	}
}

class Q4ProcessRunner extends DatabaseProcessRunner {

	protected DatabaseProcess createProcess(
								Database database,
								CSVHandler csvHandler) {

		return new Q4Process(database, csvHandler);
	}
}

class Q5ProcessRunner extends DatabaseProcessRunner {

	protected DatabaseProcess createProcess(
								Database database,
								CSVHandler csvHandler) {

		return new Q5Process(database, csvHandler);
	}
}
