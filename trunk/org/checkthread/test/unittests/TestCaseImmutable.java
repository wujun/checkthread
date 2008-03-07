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
import java.lang.reflect.*;

import junit.framework.TestCase;
import org.checkthread.parser.*;
import org.checkthread.test.target.immutable.*;
import org.checkthread.policyengine.*;

public final class TestCaseImmutable extends TestCase  {
    
    protected void setUp() {}
    protected void tearDown() {}
    
    public void testImmutable() {      
        boolean expectedValue = true;
        boolean actualValue = TestUtil.verifyNoThreadPolicyErrors(TestImmutable.class);
        assertEquals(expectedValue,actualValue);   
    }
    
    public void testImmutable2() { 
        TestParseHandler handler = TestUtil.parseClassHelper(TestImmutable2.class);
        ArrayList<ThreadPolicyErrorBean> list = handler.getThreadPolicyErrors();
        
        // Verify that there is one thread policy error
        int actualValue = list.size();
        int expectedValue = 1;
        assertEquals(expectedValue,actualValue);     
    }
    
    public void testImmutable3() { 
        TestParseHandler handler = TestUtil.parseClassHelper(TestImmutable3.class);
        ArrayList<ThreadPolicyErrorBean> list = handler.getThreadPolicyErrors();
        
        // Verify that there is one thread policy error
        int actualValue = list.size();
        int expectedValue = 1;
        assertEquals(expectedValue,actualValue);     
        
        // Verify that the failure occurred in a method called "getData"
        ThreadPolicyErrorBean errorBean = list.get(0);
        String methodName = errorBean.getParentName();
        assertEquals("getData",methodName);
    }
    
    public void testImmutable4() {      
        boolean expectedValue = true;
        boolean actualValue = TestUtil.verifyNoThreadPolicyErrors(TestImmutable4.class);
        assertEquals(expectedValue,actualValue);   
    }
    
    public void testImmutable5() {      
        TestParseHandler handler = TestUtil.parseClassHelper(TestImmutable5.class);
        ArrayList<ThreadPolicyErrorBean> list = handler.getThreadPolicyErrors();
        
        // Verify that there is one thread policy error
        int actualValue = list.size();
        int expectedValue = 1;
        assertEquals(expectedValue,actualValue);     
        
        // Verify that the failure occurred in a method called "getData"
        ThreadPolicyErrorBean errorBean = list.get(0);
        String methodName = errorBean.getParentName();
        assertEquals("getData",methodName);
    }
    
    public void testImmutable6() {      
        TestParseHandler handler = TestUtil.parseClassHelper(TestImmutable6.class);
        ArrayList<ThreadPolicyErrorBean> list = handler.getThreadPolicyErrors();
        
        // Verify that there is one thread policy error
        int actualValue = list.size();
        int expectedValue = 1;
        assertEquals(expectedValue,actualValue);     
        
        // Verify that the failure occurred in a method called "getData"
        ThreadPolicyErrorBean errorBean = list.get(0);
        String methodName = errorBean.getParentName();
        assertEquals("getData",methodName);
    }
}
