package com.cms.cmxm;

import com.bryansharp.gradle.hibeaver.utils.MethodLogAdapter;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Author: jinghao fkinh26@gmail.com
 * Date: 2017/7/21
 */

public abstract class SimpleAdapter extends MethodLogAdapter {
    public SimpleAdapter(MethodVisitor mv) {
        super(mv);
    }

    public abstract void onStart();
    public abstract void onEnd();

    @Override
    public void visitCode() {
        super.visitCode();
        onStart();
    }

    @Override
    public void visitInsn(int opcode) {
        switch(opcode) {
            case Opcodes.IRETURN:
            case Opcodes.FRETURN:
            case Opcodes.ARETURN:
            case Opcodes.LRETURN:
            case Opcodes.DRETURN:
            case Opcodes.RETURN:
                onEnd();
                break;
            default:
                break;// do nothing
        }
        super.visitInsn(opcode);
    }

}
