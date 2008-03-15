/*
Copyright (c) 2008 Joe Conti

Permission is hereby granted, free of charge, to any person
obtaining a copy of this software and associated documentation
files (the "Software"), to deal in the Software without
restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the
Software is furnished to do so, subject to the following
conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.
*/

package org.checkthread.test.unittests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ThreadcheckerTestSuite {
    
    public static Test suite() {
        TestSuite suite = new TestSuite();

        // Basic level tests
        suite.addTestSuite(TestCaseMethod.class);
        suite.addTestSuite(TestCaseField.class);
        suite.addTestSuite(TestCaseStaticBlock.class);
        suite.addTestSuite(TestCaseInnerClass.class);

        // Basic annotation tests
        suite.addTestSuite(TestCaseAnnotation.class);
        suite.addTestSuite(TestCaseSwing.class);
        suite.addTestSuite(TestCaseImmutable.class);
        suite.addTestSuite(TestCaseGuardedBy.class);

        return suite;
    }

    /**
     * Runs the test suite using the textual runner.
     */
    public static void main(String[] args) {
    	if(args.length<2) {
    		System.out.println("Invalid arguments");
    	}
		String checkDir = "";
    	if(args[0].equals("-checkdir")) {
			checkDir = args[1];
		}
    	checkDir = "C:/project/checkthread/java/class_eclipse";
    	//System.out.println("Running Tests: " + checkDir);
    	TestUtil.setCheckDir(checkDir);
        junit.textui.TestRunner.run(suite());
    }
}