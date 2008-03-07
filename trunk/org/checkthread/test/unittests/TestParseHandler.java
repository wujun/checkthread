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
import org.checkthread.policyengine.*;
import org.checkthread.policy.*;
import org.checkthread.util.*;

final class TestParseHandler implements ParseClassFileHandler {
        private HashMap<String,InvokeMethodDataBean> fMethodHashMap = new HashMap<String,InvokeMethodDataBean>();
        private HashMap<String,InvokeFieldDataBean> fFieldHashMap = new HashMap<String,InvokeFieldDataBean>();
        private ArrayList<ThreadPolicyErrorBean> fPolicyErrorList = new ArrayList<ThreadPolicyErrorBean>();
        private boolean fClassHasThreadPolicy = false;
        private boolean fIsDeclaredImmutable = false;
        private Class fClass;
        
        public boolean classHasThreadPolicy() {
            return fClassHasThreadPolicy;
        }
        
        public HashMap<String,InvokeMethodDataBean> getMethodData() {
            return fMethodHashMap;
        }
        
        public HashMap<String,InvokeFieldDataBean> getFieldData() {
            return fFieldHashMap;
        }
        
        public ArrayList<ThreadPolicyErrorBean> getThreadPolicyErrors() {
            return fPolicyErrorList;
        }
                
        public void handleStartClass(Class cls) {
            
            fClass = cls;
            
            // Check if this class is marked Immutable
            ThreadPolicy policy = AnnotationComparer.getThreadPolicyForClass(cls);
            if(policy instanceof ImmutableThreadPolicy) {
                ImmutableThreadPolicy p = (ImmutableThreadPolicy)policy;
                fIsDeclaredImmutable = true;
            }
            
            // If the class is marked mutable, all fields should be marked final
            if(fIsDeclaredImmutable) {
                Field[] fields = cls.getDeclaredFields();
                for(Field f : fields) {
                    int mod = f.getModifiers();
                    if (!Modifier.isFinal(mod)) {
                        // Error
                        ThreadPolicyErrorBean error = ThreadPolicyErrorBean.newInstance(
                                ThreadPolicyErrorEnum.IMMUTABLE_FIELD_NOT_FINAL);
                        fPolicyErrorList.add(error);
                        System.out.println("ERROR: Immutable field not final: " + f.getName());
                    }
                }
            }
                   
        }
                
        public void handleStopClass(Class cls) {
            
        }
        
        public void handleMethodInvoke(InvokeMethodDataBean data)  {
            //System.out.println("Source: " + data.getSourceFile());
            //System.out.println("File: " + data.getPathToClassFile());
            System.out.println("Invoked Method: " + data.getInvoked() + " at line " + Integer.toString(data.getLineNumber()));
            
            // If this class is marked immutable, then make sure 
            // that all invoked methods are also immutable
            if(fIsDeclaredImmutable) { 
                AccessibleObject parentMethod = data.getParentMethod();    
                // ignore constructor
                if(parentMethod instanceof Method) {
                    
                   // all invoked methods should be immutable objects
                   ThreadPolicy policy = AnnotationComparer.getThreadPolicy(data.getInvoked());
                   if(!(policy instanceof ImmutableThreadPolicy)) {
                       System.out.println("ERROR: immutable invoking mutable");
                       ThreadPolicyErrorBean error = ThreadPolicyErrorBean.newInstance(data,
                       ThreadPolicyErrorEnum.IMMUTABLE_INVOKING_MUTABLE); 
                       fPolicyErrorList.add(error);
                   }
                }
                
            } else {
                AccessibleObject method = data.getInvoked();
                String name = MiscUtil.getName(method);
                fMethodHashMap.put(name,data);     
                checkThreadPolicy(data);
            }
        }
           
        public void handleFieldInvoke(InvokeFieldDataBean data) {
            //System.out.println("Source: " + data.getSourceFile());
            //System.out.println("File: " + data.getPathToClassFile());
            System.out.println("Invoked Field: " + data.getInvoked() + " at line " + Integer.toString(data.getLineNumber()));
           
            ThreadPolicy policy = AnnotationComparer.getThreadPolicy(data.getInvoked());
            
            AccessibleObject field = data.getInvoked();
            
            // field may be empty if inner class referencing "this
            if(field!=null) {
                String name = MiscUtil.getName(field);
                fFieldHashMap.put(name,data);
            }
            
            // If the class is declared to be immutable
            if(fIsDeclaredImmutable) {
                // Make sure the field is not being modified
                // unless it is in the constructor or a static
                // method
                /*
                if(data.getOperation()!=OperationEnum.GETFIELD &&
                   data.getOperation()!=OperationEnum.GETSTATIC) {               
                        ThreadPolicyErrorBean error = ThreadPolicyErrorBean.newInstance(data,
                        ThreadPolicyErrorEnum.IMMUTABLE);
                        System.out.println("ERROR: Immutable");
                } */
            } else {
                checkThreadPolicy(data); 
            }
        }

        private void checkThreadPolicy(AbstractInvokeDataBean data) {
            ThreadPolicyCompareResults result = AnnotationComparer.compareThreadPolicy(data);
           
            if(fClassHasThreadPolicy==false && result.parentHasThreadPolicy()) {
                fClassHasThreadPolicy = true;
            }
            
            ThreadPolicyErrorBean error = result.getError();
            if(error!=null) {
                fPolicyErrorList.add(error);
            }   
        }
    }
