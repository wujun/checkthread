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

package org.checkthread.parser;

import java.lang.reflect.*;
   
final public class InvokeFieldDataBean extends AbstractInvokeDataBean {
    
    protected InvokeFieldDataBean(boolean isStaticBlock,
            AccessibleObject parent,
            AccessibleObject invoked,
            int lineNumber,
            String pathToClassFile,
            String sourceFile,
            OperationEnum op) {
        super(isStaticBlock,parent,invoked,lineNumber,pathToClassFile,sourceFile,op);
    }
    
    public static InvokeFieldDataBean newInstance(
            boolean isStaticBlock,
            AccessibleObject parent,
            AccessibleObject invoked,
            int lineNumber,
            String pathToClassFile,
            String sourceFile,
            OperationEnum op) {
        return new InvokeFieldDataBean(isStaticBlock,
                parent,
                invoked,
                lineNumber,
                pathToClassFile,
                sourceFile,
                op);
    }
}