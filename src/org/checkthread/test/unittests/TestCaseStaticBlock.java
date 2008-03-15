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
import org.checkthread.parser.*;
import org.checkthread.test.target.basic.TestStaticBlock;

public final class TestCaseStaticBlock extends TestCase  {
    
    protected void setUp() {}
    protected void tearDown() {}
    
    public void testStaticBlock() {
        TestParseHandler handler = TestUtil.parseClassHelper(TestStaticBlock.class);
        HashMap<String,InvokeFieldDataBean> fieldHashMap = handler.getFieldData();
        HashMap<String,InvokeMethodDataBean> methodHashMap = handler.getMethodData();
        
        // Verify two methods: constructor and static block
        int actualValue = methodHashMap.size();
        int expectedValue = 2;
        assertEquals(expectedValue,actualValue);
        
        // Verify a method called random was invoked
        InvokeMethodDataBean data = methodHashMap.get("random");
        boolean actualValue2 = data!=null;
        boolean expectedValue2 = true;
        assertEquals(expectedValue2,actualValue2);
        
        // Verify that the random() method is invoked within a parent static block 
        boolean actualValue3 = data.isStaticBlock();
        boolean expectedValue3 = true;
        assertEquals(expectedValue3,actualValue3);
        
        // Verify there is only one invoked field
        int actualValue4 = fieldHashMap.size();
        int expectedValue4 = 1;
        assertEquals(expectedValue4,actualValue4);
        
        // Verify the name of the field is "foo"
        assertEquals(true,fieldHashMap.get("foo")!=null); 
    }
        
}
