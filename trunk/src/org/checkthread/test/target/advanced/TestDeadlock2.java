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

package org.checkthread.test.target.advanced;

import org.checkthread.annotations.*;
import org.checkthread.policy.*;

public class TestDeadlock2 {
	
	// @MultipleThreads
	public static void foo(Object resource1, Object resource2) {
		// lock resource1
		synchronized (resource1) {
			System.out.println("Thread 1: locked resource 1");
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
			
			// lock resource2
			synchronized (resource2) {
				System.out.println("Thread 1: locked resource 2");
			}
		}
	}
	
	public static void main(String[] args) {
		final Object resource1 = "resource1";
		final Object resource2 = "resource3";

		Thread t1 = new Thread() {
			public void run() {
			    foo(resource1,resource2);
			}
		};

        Thread t2 = new Thread() {
			public void run() {
               foo(resource2,resource1);
			}
		};

		t1.start();
		t2.start();
	}
}
