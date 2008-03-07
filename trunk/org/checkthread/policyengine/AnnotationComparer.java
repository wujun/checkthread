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
import java.lang.annotation.Annotation;

import org.checkthread.annotations.*;
import org.checkthread.policy.*;
import org.checkthread.parser.*;
import org.checkthread.util.*;

public class AnnotationComparer {
    
    public static ThreadPolicyCompareResults compareThreadPolicy(
            AbstractInvokeDataBean data) {
        
        ThreadPolicyErrorBean retval = null;
        
        AccessibleObject parent = data.getParentMethod();
        AccessibleObject invoked = data.getInvoked();
        
        ThreadPolicy parentPolicy = getThreadPolicy(parent);
        ThreadPolicy invokedPolicy = getThreadPolicy(invoked);
        
        if (parentPolicy!=null && invokedPolicy!=null) {
            if (parentPolicy.isCompliant(invokedPolicy)) {
                // ok
            } else {
                retval = ThreadPolicyErrorBean.newInstance(data,
                        ThreadPolicyErrorEnum.THREADPOLICY);
            }
        } else if (parentPolicy==null) {
            //warningNoPolicy(parent,invoked);
        } else if (invokedPolicy==null) {
            if (parentPolicy.ignore(invoked)) {
                // ok
            } else {
                //warningNoPolicy(parent,invoked);
            }
        }
        //System.out.println("");
        return new ThreadPolicyCompareResults(retval,parentPolicy,invokedPolicy);
    }
    
    public static ThreadPolicy getThreadPolicyForClass(Class cls) {
       ThreadPolicy policy = null;
       policy = getThreadPolicyFromAnnotation(cls.getAnnotations());
       return policy;
    }
    
    public static void debugPrintAnnotations(Annotation [] alist) {
        System.out.println("DEBUG PRINT ANNOTATIONS");
        for(Annotation a : alist) {
            System.out.println(a.toString());
        }
    }
    public static ThreadPolicy getThreadPolicy(AccessibleObject obj) {
        assert(obj!=null);
        //System.out.println("getThreadPolicy: " + obj);
        ThreadPolicy policy = null;
        if(obj==null) {
            return policy;
        }
        Class cls = MiscUtil.getDeclaringClass(obj);
        Annotation [] alist = obj.getDeclaredAnnotations();
        
        // method level annotations
        policy = getThreadPolicyFromAnnotation(alist);
        //debugPrintAnnotations(alist);
        if(policy==null) {
           // debugPrintAnnotations(cls.getDeclaredAnnotations());
            policy = getThreadPolicyFromAnnotation(cls.getDeclaredAnnotations());
        }               
        if(policy==null) {
            policy = HeuristicThreadPolicy.getThreadPolicy(obj);
        }
        return policy;
    }
        
    private static ThreadPolicy getThreadPolicyFromAnnotation(
            Annotation[] alist) {
        ThreadPolicy policy = null;
        for (Annotation a : alist) {
            if( a instanceof EventThreadOnly) {
                String []ignoreList = ((EventThreadOnly)a).ignore();
                policy = new EventThreadPolicy(ignoreList);
            } else if( a instanceof MainThreadOnly) {
                String []ignoreList = ((MainThreadOnly)a).ignore();
                policy = new MainThreadPolicy(ignoreList);
            } else if( a instanceof ThreadSafe) {
                String []ignoreList = ((ThreadSafe)a).ignore();
                policy = new ThreadSafePolicy(ignoreList);
            } else if( a instanceof UniqueThread) {
                String []ignoreList = ((UniqueThread)a).ignore();
                policy = new UniqueThreadPolicy(ignoreList);
            } else if( a instanceof Immutable) {
                String []ignoreList = ((Immutable)a).ignore();
                policy = new ImmutableThreadPolicy(ignoreList);
            } else if(a instanceof GuardedBy) {
                GuardedBy g = (GuardedBy)a;
                String[] ignoreList = new String[]{g.target()};
                policy = new GuardedByThreadPolicy(ignoreList);
            }
        }
        return policy;
    }
    
    private static void warningNoPolicy(AccessibleObject parent,
            AccessibleObject invoked) {
        System.out.println("    WARNING: No thread policy for " + invoked + " in " + parent);
    }
}

