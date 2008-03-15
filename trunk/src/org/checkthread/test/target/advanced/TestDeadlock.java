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

public class TestDeadlock {
    
    @MainThread
    public static void main(String[] args) {
        
        // These are the two resource objects we'll try to get locks for
        final Object resource1 = "resource1";
        
        // GuardedBy(name-"resource2") 
        final Object resource2 = "resource2";
        
        // Here's the first thread.  It tries to lock resource1 then resource2
        Thread t1 = new Thread() {
            
            // thread safe
            // two meanings: 
            // what thread=anonymous thread
            // what policy=thread safe
            // implementation=sychronized
            public void run() {
                
                // Lock resource 1
                synchronized (resource1) {
                    System.out.println("Thread 1: locked resource 1");
                    
                    // Pause for a bit, simulating some file I/O or something.
                    // Basically, we just want to give the other thread a chance to
                    // run.  Threads and deadlock are asynchronous things, but we're
                    // trying to force deadlock to happen here...
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                    }
                    
                    // Now wait 'till we can get a lock on resource 2
                    synchronized (resource2) {
                        System.out.println("Thread 1: locked resource 2");
                    }
                }
            }
        };
        
        // Here's the second thread.  It tries to lock resource2 then resource1
        Thread t2 = new Thread() {
            
            // thread safe
            public void run() {
                // This thread locks resource 2 right away
                synchronized (resource2) {
                    System.out.println("Thread 2: locked resource 2");
                    
                    // Then it pauses, for the same reason as the first thread does
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                    }
                    
                    // Then it tries to lock resource1.  But wait!  Thread 1 locked
                    // resource1, and won't release it 'till it gets a lock on
                    // resource2.  This thread holds the lock on resource2, and won't
                    // release it 'till it gets resource1.  We're at an impasse. Neither
                    // thread can run, and the program freezes up.
                    synchronized (resource1) {
                        System.out.println("Thread 2: locked resource 1");
                    }
                }
            }
        };
        
        // Start the two threads. If all goes as planned, deadlock will occur,
        // and the program will never exit.
        t1.start();
        t2.start();
    }
}

