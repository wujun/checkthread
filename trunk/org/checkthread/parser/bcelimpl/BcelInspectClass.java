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

import java.io.*;
import java.util.*;

import org.apache.bcel.classfile.*;
import org.apache.bcel.Constants;
import org.checkthread.parser.*;

final public class BcelInspectClass {
    
    private JavaClass fJClass;
    private static boolean wide = false;
    private ConstantPool constant_pool;
    ArrayList<ParseClassFileHandler> fHandlerList;
    
    public static void main(String[] args) throws Exception {
        String classFile = "C:/project/threadchecker/netbeans/ThreadChecker/build/classes/javathreads/examples/ch02/example7/AnimatedCharacterDisplayCanvas.class";
        new BcelInspectClass(classFile,null);
    }
    
    BcelInspectClass(String classFile,
        ArrayList<ParseClassFileHandler> handlerList) throws Exception {
        fHandlerList = handlerList;
        ClassParser parser = new ClassParser(classFile);
        fJClass = parser.parse();
        constant_pool = fJClass.getConstantPool();
        inspectClass(fJClass);
    }
    
    private void inspectClass(JavaClass jClass) throws Exception {
        Method[] mlist = jClass.getMethods();
        String className = jClass.getClassName();
        Class cls = Class.forName(className);
        
        // notify handlers
        if(fHandlerList!=null) {
            for(ParseClassFileHandler handler : fHandlerList) {
                handler.handleStartClass(cls);
            }
        }
        
        for(Method m : mlist) {
            inspectMethod(className,m);
        }
        
        // notify handlers
        if(fHandlerList!=null) {
            for(ParseClassFileHandler handler : fHandlerList) {
                handler.handleStopClass(cls);
            }
        }
    }
    
    private void inspectMethod(String className,Method method) throws Exception {
        
        // Get raw signature
        String signature = method.getSignature();
        // Get array of strings containing the argument types
        String[] args = Utility.methodSignatureArgumentTypes(signature, false);
        // Get return type string
        String type = Utility.methodSignatureReturnType(signature, false);
        // Get method name
        String name = method.getName();
        LineNumberTable lineTable = method.getLineNumberTable();
        
        // Get method's access flags
        String access = Utility.accessToString(method.getAccessFlags());
        
        // Get the method's attributes, the Code Attribute in particular
        Attribute[] attributes = method.getAttributes();

        System.out.println("** Searching Parent Method: " + name + "**");
        for(String a : args) {
            System.out.println("** Parent Argument: " + a + "**");
        }
        
        boolean isStaticBlock = false;
        java.lang.reflect.AccessibleObject parent;
        if(name.equals(Util.STATICBLOCK)) {
            parent = null;
            isStaticBlock = true;
        } else {
            String parentMethodName = Util.cleanMethod(name);
            parent = Util.loadMethod(className,parentMethodName,args);
        }
        
        Code code = method.getCode();
        inspectCode(isStaticBlock,parent,code);
    }
    
    private void inspectCode(boolean isStaticBlock,
            java.lang.reflect.AccessibleObject parent,
            Code code)
            throws Exception {
        if (code != null) {
            LineNumberTable lineTable = code.getLineNumberTable();
            ByteReader stream = new ByteReader(code.getCode());
            
            stream.mark(stream.available());
            stream.reset();
            
            for (int i = 0; stream.available() > 0; i++) {
                int offset = stream.getIndex();
                inspectBytes(isStaticBlock,lineTable,parent,stream);
            }
        }
    }
    
    // Convert from BCEL opcodes to enumeration
    private static OperationEnum getOperationEnum(int opcode) {
        OperationEnum op;
        switch(opcode) {
            case Constants.INVOKESPECIAL:
                op = OperationEnum.INVOKESPECIAL;
                break;
            case Constants.INVOKESTATIC:
                op = OperationEnum.INVOKESTATIC;
                break;
            case Constants.INVOKEVIRTUAL:
                op = OperationEnum.INVOKEVIRTUAL;
                break;
            case Constants.INVOKEINTERFACE:
                op = OperationEnum.INVOKEINTERFACE;
                break;     
            case Constants.GETFIELD:
                op = OperationEnum.GETFIELD;
                break;
            case Constants.GETSTATIC:
                op = OperationEnum.GETSTATIC;
                break;
            case Constants.PUTFIELD:
                op = OperationEnum.PUTFIELD;
                break;
            case Constants.PUTSTATIC:
                op = OperationEnum.PUTSTATIC;
                break;
            default:
                op = OperationEnum.UNDEFINED;
                break;   
        }
        return op;
    }
    
    private void inspectBytes(boolean isStaticBlock,
            LineNumberTable lineTable,
            java.lang.reflect.AccessibleObject parent,
            ByteReader bytes)
            throws Exception {
        short opcode = (short) bytes.readUnsignedByte();
        String name, signature;
        String className = null;
        int default_offset = 0, low, high;
        int index, class_index, vindex, constant;
        int[] jump_table;
        int no_pad_bytes = 0, offset;
        
        /* Special case: Skip (0-3) padding bytes, i.e., the
         * following bytes are 4-byte-aligned
         */
        if ((opcode == Constants.TABLESWITCH) || (opcode == Constants.LOOKUPSWITCH)) {
            int remainder = bytes.getIndex() % 4;
            no_pad_bytes = (remainder == 0) ? 0 : 4 - remainder;
            for (int i = 0; i < no_pad_bytes; i++) {
                bytes.readByte();
            }
            // Both cases have a field default_offset in common
            default_offset = bytes.readInt();
        }
        OperationEnum op = getOperationEnum(opcode);
        switch (opcode) {
            case Constants.TABLESWITCH:
                low = bytes.readInt();
                high = bytes.readInt();
                offset = bytes.getIndex() - 12 - no_pad_bytes - 1;
                default_offset += offset;
                
                // Print switch indices in first row (and default)
                jump_table = new int[high - low + 1];
                for (int i = 0; i < jump_table.length; i++) {
                    jump_table[i] = offset + bytes.readInt();
                }
                
                break;
            /* Lookup switch has variable length arguments.
             */
            case Constants.LOOKUPSWITCH:
                int npairs = bytes.readInt();
                offset = bytes.getIndex() - 8 - no_pad_bytes - 1;
                jump_table = new int[npairs];
                default_offset += offset;
                
                // Print switch indices in first row (and default)
                for (int i = 0; i < npairs; i++) {
                    int match = bytes.readInt();
                    jump_table[i] = offset + bytes.readInt();
                }
                break;
                
            /* Two address bytes + offset from start of byte stream form the
             * jump target.
             */
            case Constants.GOTO:
            case Constants.IFEQ:
            case Constants.IFGE:
            case Constants.IFGT:
            case Constants.IFLE:
            case Constants.IFLT:
            case Constants.IFNE:
            case Constants.IFNONNULL:
            case Constants.IFNULL:
            case Constants.IF_ACMPEQ:
            case Constants.IF_ACMPNE:
            case Constants.IF_ICMPEQ:
            case Constants.IF_ICMPGE:
            case Constants.IF_ICMPGT:
            case Constants.IF_ICMPLE:
            case Constants.IF_ICMPLT:
            case Constants.IF_ICMPNE:
            case Constants.JSR:
                index = (int) (bytes.getIndex() + bytes.readShort() - 1);
                break;
                
            /* Same for 32-bit wide jumps
             */
            case Constants.GOTO_W:
            case Constants.JSR_W:
                int windex = bytes.getIndex() + bytes.readInt() - 1;
                break;
                
            /* Index byte references local variable (register)
             */
            case Constants.ALOAD:
            case Constants.ASTORE:
            case Constants.DLOAD:
            case Constants.DSTORE:
            case Constants.FLOAD:
            case Constants.FSTORE:
            case Constants.ILOAD:
            case Constants.ISTORE:
            case Constants.LLOAD:
            case Constants.LSTORE:
            case Constants.RET:
                if (wide) {
                    vindex = bytes.readShort();
                    wide = false; // Clear flag
                } else {
                    vindex = bytes.readUnsignedByte();
                }
                break;
            /*
             * Remember wide byte which is used to form a 16-bit address in the
             * following instruction. Relies on that the method is called again with
             * the following opcode.
             */
            case Constants.WIDE:
                wide = true;
                break;
                
            /* Array of basic type.
             */
            case Constants.NEWARRAY:
                break;
                
            /* Access object/class fields.
             */
            case Constants.GETFIELD:
            case Constants.GETSTATIC:
            case Constants.PUTFIELD:
            case Constants.PUTSTATIC:
                index = bytes.readShort();
                ConstantFieldref c1 = (ConstantFieldref) constant_pool.getConstant(index,
                        Constants.CONSTANT_Fieldref);
                class_index = c1.getClassIndex();
                name = constant_pool.getConstantString(class_index, Constants.CONSTANT_Class);
                className = Utility.compactClassName(name, false);
                index = c1.getNameAndTypeIndex();
                String field_name = constant_pool.constantToString(index, Constants.CONSTANT_NameAndType);
                 
                if(className!=null && className.length()>1) {
                    className = Util.cleanClassName(className);

                    // package up information as a data bean
                    java.lang.reflect.AccessibleObject invoked = Util.loadField(className,field_name);
                    int linenumber = lineTable.getSourceLine(bytes.getIndex());


                    InvokeFieldDataBean bean = InvokeFieldDataBean.newInstance(isStaticBlock,
                            parent,
                            invoked,
                            linenumber,
                            fJClass.getFileName(),
                            fJClass.getSourceFileName(),
                            op);
                 
                    // pass bean to handlers
                    if(fHandlerList!=null) {
                        for(ParseClassFileHandler handler : fHandlerList) {
                            handler.handleFieldInvoke(bean);
                        }
                    }
                }
                break;
                
            /* Operands are references to classes in constant pool
             */
            case Constants.CHECKCAST:
            case Constants.INSTANCEOF:
            case Constants.NEW:
                index = bytes.readShort();
                break;
                
            /* Operands are references to methods in constant pool
             */
            case Constants.INVOKESPECIAL:
            case Constants.INVOKESTATIC:
            case Constants.INVOKEVIRTUAL:
            case Constants.INVOKEINTERFACE:
                int m_index = bytes.readShort();
                String str;
                if (opcode == Constants.INVOKEINTERFACE) { // Special treatment needed
                    int nargs = bytes.readUnsignedByte(); // Redundant
                    int reserved = bytes.readUnsignedByte(); // Reserved
                    ConstantInterfaceMethodref c = (ConstantInterfaceMethodref) constant_pool
                            .getConstant(m_index, Constants.CONSTANT_InterfaceMethodref);
                    class_index = c.getClassIndex();
                    str = constant_pool.constantToString(c);
                    index = c.getNameAndTypeIndex();
                } else {
                    ConstantMethodref c = (ConstantMethodref) constant_pool.getConstant(m_index,
                            Constants.CONSTANT_Methodref);
                    class_index = c.getClassIndex();
                    str = constant_pool.constantToString(c);
                    index = c.getNameAndTypeIndex();
                }
                
                String methodName = constant_pool.constantToString(constant_pool.getConstant(index, Constants.CONSTANT_NameAndType));
                className = referenceClass(class_index);
                
                // Get signature, i.e., types
                ConstantNameAndType c2 = (ConstantNameAndType) constant_pool.getConstant(index,
                        Constants.CONSTANT_NameAndType);
                signature = constant_pool.constantToString(c2.getSignatureIndex(), Constants.CONSTANT_Utf8);
                String[] args = Utility.methodSignatureArgumentTypes(signature, false);
                String returnType = Utility.methodSignatureReturnType(signature, false);
                
                if(methodName==null || methodName.length()<1) {
                    throw new Exception("Invalid Method");
                } else {
                    methodName = Util.cleanMethod(methodName);
     
                    // package up information as a data bean
                    java.lang.reflect.AccessibleObject invoked = Util.loadMethod(className,methodName,args);
                    int linenumber = lineTable.getSourceLine(bytes.getIndex());
                    //System.out.println("getindex: " + bytes.getIndex());
                    //System.out.println("line number: " + linenumber);
                    InvokeMethodDataBean bean = InvokeMethodDataBean.newInstance(isStaticBlock,
                            parent,
                            invoked,
                            linenumber,
                            fJClass.getFileName(),
                            fJClass.getSourceFileName(),
                            op);
                    
                    // pass bean to handlers
                    if(fHandlerList!=null) {
                        for(ParseClassFileHandler handler : fHandlerList) {
                            handler.handleMethodInvoke(bean);
                        }
                    }
                }
                break;
                
            /* Operands are references to items in constant pool
             */
            case Constants.LDC_W:
            case Constants.LDC2_W:
                index = bytes.readShort();
                break;
            case Constants.LDC:
                index = bytes.readUnsignedByte();
                break;
                
            /* Array of references.
             */
            case Constants.ANEWARRAY:
                index = bytes.readShort();
                break;
                
            /* Multidimensional array of references.
             */
            case Constants.MULTIANEWARRAY:
                index = bytes.readShort();
                int dimensions = bytes.readByte();
                break;
                
            /* Increment local variable.
             */
            case Constants.IINC:
                if (wide) {
                    vindex = bytes.readShort();
                    constant = bytes.readShort();
                    wide = false;
                } else {
                    vindex = bytes.readUnsignedByte();
                    constant = bytes.readByte();
                }
                break;
            default:
                if (Constants.NO_OF_OPERANDS[opcode] > 0) {
                    for (int i = 0; i < Constants.TYPE_OF_OPERANDS[opcode].length; i++) {
                        switch (Constants.TYPE_OF_OPERANDS[opcode][i]) {
                            case Constants.T_BYTE:
                                break;
                            case Constants.T_SHORT: // Either branch or index
                                break;
                            case Constants.T_INT:
                                break;
                            default: // Never reached
                                System.err.println("Unreachable default case reached!");
                                System.exit(-1);
                        }
                    }
                }
        }
        
    }
    
    /**
     * Utility method that converts a class reference in the constant pool,
     * i.e., an index to a string.
     */
    private String referenceClass( int index ) {
        String str = constant_pool.getConstantString(index, Constants.CONSTANT_Class);
        str = Utility.compactClassName(str);
        String class_package = "BARF";
        str = Utility.compactClassName(str, class_package + ".", true);
        return str;
    }
}

