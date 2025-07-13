/*
 * Copyright 2011 Andrew Porokhin. All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of Andrew Porokhin.
 */
package org.aalabs.sjcp.cp;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Java Class File ConstantPoolInfo representation.
 * @author Andrew Porokhin
 */
public class ConstantPoolInfo {
    private static final Logger logger = Logger.getLogger(ConstantPoolInfo.class.getName());

    public static final byte CONSTANT_UTF8 = 1;
    public static final byte CONSTANT_INT = 3;
    public static final byte CONSTANT_FLOAT = 4;
    public static final byte CONSTANT_LONG = 5;
    public static final byte CONSTANT_DOUBLE = 6;
    public static final byte CONSTANT_CLASS_INFO = 7;
    public static final byte CONSTANT_STRING = 8;
    public static final byte CONSTANT_FIELD_REF = 9;
    public static final byte CONSTANT_METHOD_REF = 10;
    public static final byte CONSTANT_INTERFACE_METHOD_REF = 11;
    public static final byte CONSTANT_NAME_AND_TYPE = 12;

    /** Class Info TAG. */
    byte tag;

    public static ConstantPoolInfo readConstantPoolInfo(byte tag, DataInputStream di) throws IOException {
        switch (tag) {
            case CONSTANT_STRING:
                return new ConstantString(di.readUnsignedShort());
            case CONSTANT_INT:
                return new ConstantPrimitive<Integer>(tag, di.readInt());
            case CONSTANT_FLOAT:
                return new ConstantPrimitive<Float>(tag, di.readFloat());
            case CONSTANT_LONG:
                return new ConstantPrimitive<Long>(tag, di.readLong());
            case CONSTANT_DOUBLE:
                return new ConstantPrimitive<Double>(tag, di.readDouble());
            case CONSTANT_UTF8:
                String stringVal = di.readUTF();
                return new ConstantPrimitive<String>(tag, stringVal);
            case CONSTANT_NAME_AND_TYPE:
                return new ConstantNameAndType(di.readUnsignedShort(),
                        di.readUnsignedShort());
            case CONSTANT_CLASS_INFO:
                return new ConstantClassInfo(di.readUnsignedShort());
            case CONSTANT_FIELD_REF:
            case CONSTANT_METHOD_REF:
            case CONSTANT_INTERFACE_METHOD_REF:
                return new ConstantReference(tag,
                        di.readUnsignedShort(),
                        di.readUnsignedShort());
            default:
                logger.warning("Unknown constant type tag: " + tag);
        }
        return null;
    }

    ConstantPoolInfo(byte tag) {
        this.tag = tag;
    }

    /**
     * Returns TAG of the ConstantPool info
     * @return byte value.
     */
    public byte getTag() {
        return tag;
    }
}
