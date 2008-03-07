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

package org.checkthread.policy;

import java.lang.reflect.AccessibleObject;

public class MainThreadPolicy extends AbstractThreadPolicy {

	public MainThreadPolicy(String[] ignore) {
		super(ignore);
	}
	
   public boolean isCompliant(ThreadPolicy invokedpolicy) {
       boolean iscompliant = true;

       // Okay to call a method that can run on any thread
       if (invokedpolicy instanceof ThreadSafePolicy) {
          iscompliant = true;

       // Not okay to call a method that doesn't have main thread policy
       } else if (!(invokedpolicy instanceof MainThreadPolicy)) {
           iscompliant = false;
       }
       return iscompliant;
   }

}

