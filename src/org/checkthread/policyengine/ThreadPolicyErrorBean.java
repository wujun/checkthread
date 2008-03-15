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

package org.checkthread.policyengine;

import java.lang.reflect.*;

import org.checkthread.parser.*;
import org.checkthread.util.*;

final public class ThreadPolicyErrorBean {
    
    final private AbstractInvokeDataBean fDataBean;
    final private String fParentName;
    final private String fInvokedName;
    final private ThreadPolicyErrorEnum fErrorEnum;
    
    public static ThreadPolicyErrorBean newInstance(
            AbstractInvokeDataBean data,
            ThreadPolicyErrorEnum  errorEnum) 
    {
        return new ThreadPolicyErrorBean(data,errorEnum);
        
    }
        
    public static ThreadPolicyErrorBean newInstance(
            ThreadPolicyErrorEnum  errorEnum) 
    {
        return new ThreadPolicyErrorBean(null,errorEnum);
        
    }
        
    private ThreadPolicyErrorBean(AbstractInvokeDataBean data,
                                 ThreadPolicyErrorEnum  errorEnum) 
    {
        fDataBean = data;
        fErrorEnum = errorEnum;
        
        if(data!=null) {
            AccessibleObject parent = data.getParentMethod();
            AccessibleObject invoked = data.getInvoked();
        
            fParentName = MiscUtil.getName(parent);
            fInvokedName = MiscUtil.getName(invoked);
        } else {
            fParentName = null;
            fInvokedName = null; 
        }
    }
    
    public ThreadPolicyErrorEnum getErrorEnum() {return fErrorEnum;}
    public AbstractInvokeDataBean getInvoked() {return fDataBean;}
    public String getParentName() {return fParentName;}
    public String getInvokedName() {return fInvokedName;}
    
    public int getLineNumber() {
        if(fDataBean!=null) {
            return fDataBean.getLineNumber();
        } else {
            return 0;
        }
    }
    
    public String getSourceFile() {
        if(fDataBean!=null) {
            return fDataBean.getSourceFile();
        } else {
            return null;
        }
    }
    
    public String getPathToClassFile() {
        if(fDataBean!=null) {
            return fDataBean.getPathToClassFile();
        } else {
            return null;
        }
    }
    
}
