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
package org.aalabs.sjcp;

import org.aalabs.sjcp.cp.ConstantPoolInfo;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    public static final JavaClassFile processFile(File f) {
        try {
            DataInputStream di = new DataInputStream(new FileInputStream(f));
            int javaMagic = di.readInt();
            if (javaMagic != 0xCAFEBABE) {
                throw new IllegalArgumentException("Incorrect Java Class File, wrong signature");
            }

            JavaClassFileImpl javaClassFile = new JavaClassFileImpl();
            javaClassFile.setMinorVersion(di.readUnsignedShort());
            javaClassFile.setMajorVersion(di.readUnsignedShort());

            int contantPoolSize = di.readUnsignedShort();
            ArrayList<ConstantPoolInfo> cpiList = new ArrayList<ConstantPoolInfo>(contantPoolSize);
//            StringBuffer sb = new StringBuffer(2048);
//            sb.append("Dump of the CONTANT POOL\n");
            for (int i = 1; i < contantPoolSize; i++) {
                byte tag = di.readByte();
                ConstantPoolInfo cpi = ConstantPoolInfo.readContantPoolInfo(tag, di);
                cpiList.add(cpi);

                // sb.append("[").append(i).append("]").append(cpi.toString()).append("\n");
            }
            javaClassFile.setConstantPoolList(cpiList);

            // logger.info(sb.toString());

            javaClassFile.setAccessFlags(di.readUnsignedShort());
            javaClassFile.setThisClassIndex(di.readUnsignedShort());
            javaClassFile.setSuperClassIndex(di.readUnsignedShort());

//            logger.info("Access flags: " + javaClassFile.getAccessFlags());
//            logger.info("This class: " + javaClassFile.getThisClassIndex());
//            logger.info("Super class: " + javaClassFile.getSuperClassIndex());
//
//            logger.info("Finished.");

            return javaClassFile;
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static final void main(String[] args) {
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