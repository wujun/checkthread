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

import java.util.*;

import junit.framework.TestCase;
import java.lang.reflect.*;

import org.checkthread.parser.*;
import org.checkthread.test.target.swing.*;
import org.checkthread.policyengine.*;

public class TestCaseSwing extends TestCase {
    
    protected void setUp() {}
    protected void tearDown() {}
    
    public void testBasicSwing() {
        
        TestParseHandler handler = TestUtil.parseClassHelper(TestBasicSwing.class);
        ArrayList<ThreadPolicyErrorBean> list = handler.getThreadPolicyErrors();
        
        // Verify that there is one thread policy error
        int actualValue = list.size();
        int expectedValue = 1;
        assertEquals(expectedValue,actualValue);
        
        // Verify that the failure occurred in a method called "mymethod"
        ThreadPolicyErrorBean errorBean = list.get(0);
        String methodName = errorBean.getParentName();
        assertEquals("mymethod",methodName);
    }
    
    public void testBasicSwing2() { 
        boolean expectedValue = true;
        boolean actualValue = TestUtil.verifyNoThreadPolicyErrors(TestBasicSwing2.class);
        assertEquals(expectedValue,actualValue);
    }
    
    public void testBasicSwing3() { 
        boolean expectedValue = true;
        boolean actualValue = TestUtil.verifyNoThreadPolicyErrors(TestBasicSwing3.class);
        assertEquals(expectedValue,actualValue);
    }
    
    public void testBasicSwing4() { 
        boolean expectedValue = true;
        boolean actualValue = TestUtil.verifyNoThreadPolicyErrors(TestBasicSwing4.class);
        assertEquals(expectedValue,actualValue);
    }
    
    public void testBasicSwing5() { 
        TestParseHandler handler = TestUtil.parseClassHelper(TestBasicSwing5.class);
        boolean actualValue = handler.classHasThreadPolicy();
        boolean expectedValue = false;
        assertEquals(expectedValue,actualValue);
    }
}