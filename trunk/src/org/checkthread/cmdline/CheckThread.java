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

package org.checkthread.cmdline;

import org.checkthread.parser.*;
import org.checkthread.policyengine.ThreadPolicyErrorBean;
import org.checkthread.config.*;

import java.io.*;
import java.util.*;

public class CheckThread {
	
	private ArrayList<File> fClassFileList = new ArrayList<File>();
	private File fRoot;
	private static Config sConfigBean;
	
	public static void main(String [] args) {
		//String filename = "C:/project/checkthread/src/class/org/checkthread/test/target/advanced/TestDeadlock2$1.class";
		String filename = "C:/project/checkthreadexamples/java/class/";
		
		CheckThread h = new CheckThread(filename);
		h.start();
	}
	
	public CheckThread(String checkPath) {
		fRoot= new File(checkPath);
	}
	
	public void start() {
	    recurseClassFile(fRoot);
		analysis();
	}

	private void recurseClassFile(File rootFile) {

		if (rootFile.isFile()) {
			if (rootFile.getAbsolutePath().endsWith(".class")) {
				fClassFileList.add(rootFile);
			}
		} else if (rootFile.isDirectory()) {
			File[] fileList = rootFile.listFiles();
			if (fileList != null) {
				for (File file : fileList) {
				    recurseClassFile(file);
				}
			}
		}

	}
	
	private void analysis() {
		ParseHandler handler = new ParseHandler();
        for(File classFile : fClassFileList) {
            IClassFileParser parser = ClassFileParserFactory.getClassFileParser();
            parser.addHandler(handler);
            parser.parseClassFile(classFile.getAbsolutePath());
            ArrayList<ThreadPolicyErrorBean> list = handler.getThreadPolicyErrors();
            for(ThreadPolicyErrorBean bean : list) {

            	Log.severe("Error with " + bean.getSourceFile()+ " on line " + bean.getLineNumber());
            	Log.severe("Error type: " + bean.getErrorEnum().toString());
            	Log.severe("Invoked method/field: " + bean.getInvokedName());
            	Log.severe("Called from: " + bean.getParentName()); 	
            	Log.severe("");

            }
        }
	}
}
