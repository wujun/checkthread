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
import org.checkthread.test.target.annotation.*;
import org.checkthread.policyengine.*;

public class TestCaseAnnotation extends TestCase {
    
    protected void setUp() {}
    protected void tearDown() {}
    
    public void testAnnotation() {
        
        TestParseHandler handler = TestUtil.parseClassHelper(TestBasicAnnotation.class);
        ArrayList<ThreadPolicyErrorBean> list = handler.getThreadPolicyErrors();
        
        // Verify that there is one thread policy error
        int actualValue = list.size();
        int expectedValue = 1;
        assertEquals(expectedValue,actualValue);
        
        // Verify that the failure occurred in a method called "foo""
        ThreadPolicyErrorBean errorBean = list.get(0);
        String methodName = errorBean.getParentName();
        assertEquals("foo",methodName);
        
        // Verify that the error occurred when invoking the method "bar"
        String invokedName = errorBean.getInvokedName();
        assertEquals("bar",invokedName);
    }
    
    public void testAnnotation2() {
        
        TestParseHandler handler = TestUtil.parseClassHelper(TestBasicAnnotation2.class);
        ArrayList<ThreadPolicyErrorBean> list = handler.getThreadPolicyErrors();
        
        // Verify that there is one thread policy error
        int actualValue = list.size();
        int expectedValue = 1;
        assertEquals(expectedValue,actualValue);
        
        // Verify that the failure occurred in a method called "foo""
        ThreadPolicyErrorBean errorBean = list.get(0);
        String methodName = errorBean.getParentName();
        assertEquals("mymethod",methodName);
        
        // Verify that the error occurred when invoking the method "bar"
        String invokedName = errorBean.getInvokedName();
        assertEquals("myfield",invokedName);
        
        // Verify line number
        assertEquals(16,errorBean.getLineNumber());
    }
    
    public void testAnnotation3() {
        
        TestParseHandler handler = TestUtil.parseClassHelper(TestBasicAnnotation3.class);
        ArrayList<ThreadPolicyErrorBean> list = handler.getThreadPolicyErrors();
        
        // Verify that there is one thread policy error
        int actualValue = list.size();
        int expectedValue = 1;
        assertEquals(expectedValue,actualValue);
        
        // Verify that the failure occurred in a method called "foo""
        ThreadPolicyErrorBean errorBean = list.get(0);
        String methodName = errorBean.getParentName();
        assertEquals("mymethod",methodName);
        
        // Verify that the error occurred when invoking the method "bar"
        String invokedName = errorBean.getInvokedName();
        assertEquals("myfield",invokedName);
        
        // Verify line number
        assertEquals(16,errorBean.getLineNumber());
    }
    
    public void testAnnotation4() {
        
        TestParseHandler handler = TestUtil.parseClassHelper(TestBasicAnnotation4.class);
        ArrayList<ThreadPolicyErrorBean> list = handler.getThreadPolicyErrors();
        
        // Verify that there is one thread policy error
        int actualValue = list.size();
        int expectedValue = 0;
        assertEquals(expectedValue,actualValue);
       
    }
    
    public void testAnnotation5() {
        
        TestParseHandler handler = TestUtil.parseClassHelper(TestBasicAnnotation5A.class);
        ArrayList<ThreadPolicyErrorBean> list = handler.getThreadPolicyErrors();
        
        // Verify that there is one thread policy error
        int actualValue = list.size();
        int expectedValue = 1;
        assertEquals(expectedValue,actualValue);
        
        // Verify that the failure occurred in a method called "foo""
        ThreadPolicyErrorBean errorBean = list.get(0);
        String methodName = errorBean.getParentName();
        assertEquals("mymethodA",methodName);
        
        // Verify that the error occurred when invoking the method "bar"
        String invokedName = errorBean.getInvokedName();
        assertEquals("mymethodB",invokedName);
        
        // Verify line number
        assertEquals(11,errorBean.getLineNumber());
    }
}
