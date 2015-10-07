BUILDING CODE
-------------
1) Ensure that JDK is installed (version 1.6 onwards)
2) Ensure that ANT is installed
3) Go to top-level folder (i.e. the one where this file is located)
4) Type "ant"

RUNNING CODE
------------
1) Go to top-level folder (i.e. the one where this file is located)
2) Run "run.bat" script, on Windows system, or "run.sh" on UNIX, Linux or Mac

NOTE: You may also build and run via Eclipse or other IDE if you wish. 

PROVIDING SOLUTIONS TO PROBLEMS
--------------------------------
* Each solution should be provided via the implementation of a "process" class
* An example process class is provided
* A set of template process classes is provided (one for each problem)
* The implementations of these classes should be provided by the student

PROVIDED CODE
-------------
The provided code consists of the following packages:

1) "uk.ac.manchester.cs.msc.ssd.core":
	* Contains general framework code
	* To be used in implementation of "process" classes
	* Not intended to be edited by student

2) "uk.ac.manchester.cs.msc.ssd":
	* Contains classes:
	  * "ExampleProcess": Shows how to use core code to implement procces class
	  * "Q?Process" (? = 1..5): Template process classes, one for each problem
	  * "Runner": Point-of-entry class whose main method runs, in turn :
	    *  Process represented by "ExampleProcess" class
	    *  Processes represented by all "Q?Process" classes
	    

