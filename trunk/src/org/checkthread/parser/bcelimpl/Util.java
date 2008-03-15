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

package org.checkthread.parser.bcelimpl;

import java.lang.reflect.*;
import java.util.regex.*;
import java.util.*;

import org.checkthread.config.*;

final public class Util {
    
    final static String STATICBLOCK = "<clinit>";
    final static String CONSTRUCTOR = "<init>";
    final static private String REGEX = "\\w*";
    final static private Pattern sPattern = Pattern.compile(REGEX);
    
    public static void main(String [] args) throws Exception {
        String foo = "hello ( world";
        Log.info(cleanMethod(foo));
        Class cls = Class.forName("[Ljava.lang.String;");
    }
    
    private static String getFirstWord(String instr) {
        String retval = null;
        Matcher matcher = sPattern.matcher(instr);
        if (matcher.find()) {
            String str = matcher.group();
            retval = str;
        }
        retval = retval.trim();
        return retval;
    }

    public static String cleanMethod(String methodSyntax) throws Exception 
    {
        String retval = null;
        
        if(methodSyntax.startsWith(CONSTRUCTOR)) {
            return CONSTRUCTOR;
        } else if (methodSyntax.startsWith(STATICBLOCK)) {
            return STATICBLOCK;
        } else {
            retval = getFirstWord(methodSyntax);  
            if(retval==null || retval.length()<1) {
                throw new Exception("Could not process input: " + methodSyntax);
            }
            
            return retval;
        }
    }
    
    static String cleanClassName(String className) {
        className = className.replace("/",".");
        if (!className.contains(".")) {
            className = "java.lang." + className;
        }
        return className;
    }
    
    public static Class loadClass(String className) {
        Class cls = null;
        try {
            className = cleanClassName(className);
            cls = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return cls;
    }
    
    public static AccessibleObject loadMethod(
            String className,
            String methodName,
            String[] methodArgs) throws Exception {
        
        if(methodName.length()<1) {
            throw new Exception("Empty Method");
        }
        
        AccessibleObject method = null;
        try {
            className = cleanClassName(className);
            Class cls = Class.forName(className);
            Class[] classArgs = new Class[methodArgs.length];
            for (int n = 0; n<methodArgs.length; n++) {
                classArgs[n] = loadMethodArg(methodArgs[n]);
            }
            method = loadMethodHelper(cls,methodName,classArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return method;
    }
    
    private static Class loadMethodArg(String className) {
        Class cls = null;
        className = className.replace('/','.');
        try {
            if(className.equals("int")) {
                cls = Integer.TYPE;
            } else if (className.equals("int[]")) {
                cls = Class.forName("[I");
            } else if (className.equals("char")) {
                cls = Character.TYPE;
            } else if (className.equals("char[]")) {
                cls = Class.forName("[C");
            } else if (className.equals("double")) {
                cls = Double.TYPE;
            } else if (className.equals("double[]")) {
                cls = Class.forName("[D");
            } else if (className.equals("boolean")) {
                cls = Boolean.TYPE;
            } else if (className.equals("boolean[]")) {
                cls = Class.forName("[Z");
            } else if (className.equals("short")) {
                cls = Short.TYPE;
            } else if (className.equals("short[]")) {
                cls = Class.forName("[S");
            } else if (className.equals("byte")) {
                cls = Byte.TYPE;
            } else if (className.equals("byte[]")) {
                cls = Class.forName("[B");
            } else if (className.equals("long")) {
                cls = Long.TYPE;
            } else if (className.equals("long[]")) {
                cls = Class.forName("[J");
            } else if (className.equals("float")) {
                cls = Float.TYPE;
            } else if (className.equals("float[]")) {
                cls = Class.forName("[F");
            } else if (className.endsWith("[]")) {
                className = "[L"+className.substring(0,className.length()-2)+";";
            }
            if (cls==null) {
                cls = Class.forName(className);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cls;
    }
    
    private static AccessibleObject loadMethodHelper(
            Class cls,
            String methodName,
            Class[] args) throws Exception {
        AccessibleObject method = null;
        
        if(methodName.length()<1) {
            throw new Exception("Empty Method");
        }
        
        try {
            if (methodName.equals(CONSTRUCTOR)) {
                try {
                    method = cls.getConstructor(args);
                } catch (Exception e) {
                    method = cls.getDeclaredConstructor(args);
                }
            } else {
                try {
                    method = cls.getMethod(methodName, args);
                } catch (NoSuchMethodException e) {
                    method = cls.getDeclaredMethod(methodName, args);
                }
            }
        } catch (Exception e) {
            // try super class
            if (cls.getSuperclass()!=null) {
                return loadMethodHelper(cls.getSuperclass(),methodName,args);
            } else {
                e.printStackTrace();
            }
        }
        
        return method;
    }
    
    public static AccessibleObject loadField(
            String className,
            String fieldName) {
        AccessibleObject field = null;
        fieldName = getFirstWord(fieldName);
        try {
            Class cls = Class.forName(className);
            field = loadFieldHelper(cls, fieldName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return field;
    }
    
    private static AccessibleObject loadFieldHelper(
            Class cls,
            String fieldName) {
        AccessibleObject field = null;
        
        //TBD
        if(fieldName.equals("this")) {
            return null;
        }
        
        try {
            try {
                field = cls.getField(fieldName);
            } catch (NoSuchFieldException e) {
                field = cls.getDeclaredField(fieldName);
            }
        } catch (Exception e) {
            // try super class
            if (cls.getSuperclass()!=null) {
                return loadFieldHelper(cls.getSuperclass(),fieldName);
            } else {
            	//ToDo: heuristic, ignore "val" for now
            	if(!fieldName.equals("val")) {
                    e.printStackTrace();
            	}
            }
        }
        return field;
    }
    
}
