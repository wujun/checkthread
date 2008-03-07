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

public abstract class AbstractInvokeDataBean {
    
    private AccessibleObject fParentMethod;
    private boolean fIsStaticBlock;
    private int fLineNumber;
    private String fPathToClassFile;
    private String fSourceFile;
    private AccessibleObject fInvoked;
    private OperationEnum fOperation;
        
    public AbstractInvokeDataBean(
            boolean isStaticBlock,
            AccessibleObject parent,
            AccessibleObject invoked,
            int lineNumber,
            String pathToClassFile,
            String sourceFile,
            OperationEnum op) {
        fIsStaticBlock = isStaticBlock;
        fParentMethod = parent;
        fLineNumber = lineNumber;
        fPathToClassFile = pathToClassFile;
        fSourceFile = sourceFile;
        fInvoked = invoked;
        fOperation = op;
    }
    
    
    public OperationEnum getOperation() {
        return fOperation;
    }
    
    public boolean isStaticBlock() {
        return fIsStaticBlock;
    }
    
    public AccessibleObject getParentMethod() {
        return fParentMethod;
    }
    
    public int getLineNumber() {
        return fLineNumber;
    }
    
    public String getPathToClassFile() {
        return fPathToClassFile;
    }
    
    public String getSourceFile() {
        return fSourceFile;
    }
    
    public AccessibleObject getInvoked() {
        return fInvoked;
    }
}
