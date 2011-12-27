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
package org.aalabs.sjcp;

import org.aalabs.sjcp.cp.ConstantPoolInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple Java class file reader.
 * 
 * @author Andrew Porokhin
 */
public class JavaClassReader {
    private static final Logger logger = Logger.getLogger(JavaClassReader.class.getName());

    public static JavaClassFile processFile(File f) {
        DataInputStream di = null;
        try {
            di = new DataInputStream(new FileInputStream(f));
            int javaMagic = di.readInt();
            if (javaMagic != 0xCAFEBABE) {
                throw new IllegalArgumentException("Incorrect Java Class File, wrong signature");
            }

            JavaClassFileImpl javaClassFile = new JavaClassFileImpl();
            javaClassFile.setMinorVersion(di.readUnsignedShort());
            javaClassFile.setMajorVersion(di.readUnsignedShort());

            int constantPoolSize = di.readUnsignedShort();
            ArrayList<ConstantPoolInfo> cpiList = new ArrayList<ConstantPoolInfo>(constantPoolSize);

            final boolean isDebugLevel = logger.isLoggable(Level.FINEST);
            StringBuffer sb = null;
            if (isDebugLevel) {
                logger.finest("Reading " + constantPoolSize + " constants...");

                sb = new StringBuffer(2048);
                sb.append("Dump of the CONSTANT POOL\n");
            }

            while (cpiList.size() < constantPoolSize - 1) {
                byte tag = di.readByte();
                ConstantPoolInfo cpi = ConstantPoolInfo.readConstantPoolInfo(tag, di);
                cpiList.add(cpi);

                if (isDebugLevel) {
                    sb.append("[").append(cpiList.size()).append("]");
                    sb.append(cpi != null ? cpi.toString() : "null").append("\n");
                }

                // According to VM Spec (nightmare... -_-):
                // All 8-byte constants take up two entries in the constant_pool
                // table of the class file. If a CONSTANT_Long_info or
                // CONSTANT_Double_info structure is the item in the constant_pool
                // table at index n, then the next usable item in the pool is
                // located at index n+2. The constant_pool  index n+1 must be
                // valid but is considered unusable.
                if (tag == ConstantPoolInfo.CONSTANT_DOUBLE || tag == ConstantPoolInfo.CONSTANT_LONG) {
                    cpiList.add(null);
                }
            }
            javaClassFile.setConstantPoolList(cpiList);

            if (isDebugLevel) {
                logger.finest(sb.toString());
            }

            javaClassFile.setAccessFlags(di.readUnsignedShort());
            javaClassFile.setThisClassIndex(di.readUnsignedShort());
            javaClassFile.setSuperClassIndex(di.readUnsignedShort());

            return javaClassFile;
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        } finally {
            try {
                di.close();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            JavaClassFile f = JavaClassReader.processFile(new File(args[0]));
            logger.info("Class: " + f.getCanonicalName()
                    + " super type of " + f.getSuperClassCanonicalName());

            logger.info("..Version: " + f.getMajorVersion() + "." + f.getMinorVersion());
        } else {
            logger.warning("Please specify class file name");
        }
    }
}
