package com.fkinh.cmxm

import com.bryansharp.gradle.hibeaver.utils.Log
import com.bryansharp.gradle.hibeaver.utils.MethodLogAdapter
import com.bryansharp.gradle.hibeaver.utils.Util
import com.fkinh.cmxm.ins.BaseXMInstrumentation
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Attribute
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * Author: jinghao fkinh26@gmail.com
 * Date: 2017/7/21
 */

public class SimpleModifyClassUtil {


    public static byte[] modifyInstrumentationClasses(String className, byte[] srcByteCode, List<MethodCell> cells){
        BaseXMInstrumentation ins = new BaseXMInstrumentation(className, cells);
        return getModifiedBytes(className, srcByteCode, ins)
    }


    private static byte[] getModifiedBytes(String className, byte[] srcByteCode, BaseXMInstrumentation ins){
        byte[] classBytesCode = null;
        try {
            Log.info("====start modifying ${className}====");
            classBytesCode = modifyClass(srcByteCode, ins);
            Log.info("====revisit modified ${className}====");
            onlyVisitClassMethod(classBytesCode, ins);
            Log.info("====finish modifying ${className}====");
            return classBytesCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (classBytesCode == null) {
            classBytesCode = srcByteCode;
        }
        return classBytesCode;
    }

    private static byte[] modifyClass(byte[] srcClass, BaseXMInstrumentation ins) throws IOException {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor adapter = new SimpleClassVisitor(classWriter, ins);
        ClassReader cr = new ClassReader(srcClass);
        //cr.accept(visitor, ClassReader.SKIP_DEBUG);
        cr.accept(adapter, 0);
        return classWriter.toByteArray();
    }

    private static void onlyVisitClassMethod(byte[] srcClass, BaseXMInstrumentation ins) throws IOException {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        SimpleClassVisitor visitor = new SimpleClassVisitor(classWriter, ins);
        visitor.onlyVisit = true;
        ClassReader cr = new ClassReader(srcClass);
        cr.accept(visitor, 0);
    }

    static class SimpleClassVisitor extends ClassVisitor implements Opcodes {

        public boolean onlyVisit = false;

        BaseXMInstrumentation monitor;

        public SimpleClassVisitor(final ClassVisitor cv, BaseXMInstrumentation monitor) {
            super(Opcodes.ASM4, cv);
            this.monitor = monitor;
        }

        @Override
        void visitEnd() {
            Log.logEach('* visitEnd *');
            super.visitEnd()
        }

        @Override
        void visitAttribute(Attribute attribute) {
            Log.logEach('* visitAttribute *', attribute, attribute.type, attribute.metaClass, attribute.metaPropertyValues, attribute.properties);
            super.visitAttribute(attribute)
        }

        @Override
        AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            Log.logEach('* visitAnnotation *', desc, visible);
            return super.visitAnnotation(desc, visible)
        }

        @Override
        void visitInnerClass(String name, String outerName,
                             String innerName, int access) {
            Log.logEach('* visitInnerClass *', name, outerName, innerName, Log.accCode2String(access));
            super.visitInnerClass(name, outerName, innerName, access)
        }

        @Override
        void visitOuterClass(String owner, String name, String desc) {
            Log.logEach('* visitOuterClass *', owner, name, desc);
            super.visitOuterClass(owner, name, desc)
        }

        @Override
        void visitSource(String source, String debug) {
            Log.logEach('* visitSource *', source, debug);
            super.visitSource(source, debug)
        }

        @Override
        FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            Log.logEach('* visitField *', Log.accCode2String(access), name, desc, signature, value);
            return super.visitField(access, name, desc, signature, value)
        }

        @Override
        public void visit(int version, int access, String name,
                          String signature, String superName, String[] interfaces) {
            Log.logEach('* visit *', Log.accCode2String(access), name, signature, superName, interfaces);
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name,
                                         String desc, String signature, String[] exceptions) {

            MethodVisitor myMv = null;
            if (!onlyVisit) {
                Log.logEach("* visitMethod *", Log.accCode2String(access), name, desc, signature, exceptions);
            }
            monitor.cells.each{
                MethodCell cell ->
                    String metName = cell.method;
                    String metMatchType = cell.type;
                    String methodDesc = cell.descriptor;
                    if (Util.isPatternMatch(metName, metMatchType, name)) {
                        Closure visit = cell.adapter;
                        if (visit != null) {
                            //methodDesc 不设置，为空，即代表对methodDesc不限制
                            if (methodDesc != null) {
                                if (Util.isPatternMatch(methodDesc, metMatchType, desc)) {
                                    if (onlyVisit) {
                                        myMv = new MethodLogAdapter(cv.visitMethod(access, name, desc, signature, exceptions));
                                    } else {
                                        try {
                                            myMv = visit(cv, access, name, desc, signature, exceptions);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            } else {
                                try {
                                    myMv = visit(cv, access, name, desc, signature, exceptions);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
            }
            if (myMv != null) {
                if (onlyVisit) {
                    Log.logEach("* revisitMethod *", Log.accCode2String(access), name, desc, signature);
                }
                return myMv;
            } else {
                return cv.visitMethod(access, name, desc, signature, exceptions);
            }
        }
    }

}