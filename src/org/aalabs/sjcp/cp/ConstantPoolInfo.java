/*
 * Copyright 2010 Andrew Porokhin. All rights reserved.
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
 *
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

    public static final byte CONTANT_UTF8 = 1;
    public static final byte CONTANT_INT = 3;
    public static final byte CONTANT_FLOAT = 4;
    public static final byte CONTANT_LONG = 5;
    public static final byte CONTANT_DOUBLE = 6;
    public static final byte CONTANT_CLASS_INFO = 7;
    public static final byte CONTANT_STRING = 8;
    public static final byte CONTANT_FIELD_REF = 9;
    public static final byte CONTANT_METHOD_REF = 10;
    public static final byte CONTANT_INTERFACE_METHOD_REF = 11;
    public static final byte CONTANT_NAME_AND_TYPE = 12;

    /** Class Info TAG. */
    byte tag;

    public static final ConstantPoolInfo readContantPoolInfo(byte tag, DataInputStream di) throws IOException {
        switch (tag) {
            case CONTANT_STRING:
                return new ConstantString(di.readUnsignedShort());
            case CONTANT_INT:
                return new ConstantPrimitive<Integer>(tag, di.readInt());
            case CONTANT_FLOAT:
                return new ConstantPrimitive<Float>(tag, di.readFloat());
            case CONTANT_LONG:
                return new ConstantPrimitive<Long>(tag, di.readLong());
            case CONTANT_DOUBLE:
                return new ConstantPrimitive<Double>(tag, di.readDouble());
            case CONTANT_UTF8:
                String stringVal = di.readUTF();
                return new ConstantPrimitive<String>(tag, stringVal);
            case CONTANT_NAME_AND_TYPE:
                return new ConstantNameAndType(di.readUnsignedShort(),
                        di.readUnsignedShort());
            case CONTANT_CLASS_INFO:
                return new ConstantClassInfo(di.readUnsignedShort());
            case CONTANT_FIELD_REF:
            case CONTANT_METHOD_REF:
            case CONTANT_INTERFACE_METHOD_REF:
                return new ConstantReference(tag,
                        di.readUnsignedShort(),
                        di.readUnsignedShort());
            default:
                logger.warning("Unknown contant type tag: " + tag);
        }
        return null;
    }

    /**
     * 
     * @param tag
     */
    ConstantPoolInfo(byte tag) {
        this.tag = tag;
    }

    /**
     * Returns TAG of the ContantPool info
     * @return byte value.
     */
    public byte getTag() {
        return tag;
    }
}
