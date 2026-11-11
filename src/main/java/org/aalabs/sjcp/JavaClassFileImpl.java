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

import org.aalabs.sjcp.cp.ConstantClassInfo;
import org.aalabs.sjcp.cp.ConstantPoolInfo;
import org.aalabs.sjcp.cp.ConstantPrimitive;

import java.util.List;

/**
 * Implementation of the Java class file.
 * @author Andrew Porokhin
 */
public class JavaClassFileImpl extends JavaClassFile {
    private List<ConstantPoolInfo> constantPoolList = null;
    private int majorVersion = -1;
    private int minorVersion = -1;
    private int thisClassIndex = -1;
    private int superClassIndex = -1;

    private int accessFlags = -1;

    void setConstantPoolList(List<ConstantPoolInfo> constantPoolList) {
        this.constantPoolList = constantPoolList;
    }

    void setThisClassIndex(int thisClassIndex) {
        this.thisClassIndex = thisClassIndex;
    }

    int getThisClassIndex() {
        return thisClassIndex;
    }
    
    void setSuperClassIndex(int superClassIndex) {
        this.superClassIndex = superClassIndex;
    }

    int getSuperClassIndex() {
        return superClassIndex;
    }

    void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }

    void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }

    int getAccessFlags() {
        return accessFlags;
    }

    void setAccessFlags(int accessFlags) {
        this.accessFlags = accessFlags;
    }

    ConstantPoolInfo getConstantPoolInfo(int index) {
        return constantPoolList.get(index - 1);
    }

    String getClassName(int classPoolInfoIndex) {
        ConstantPoolInfo cpi = getConstantPoolInfo(classPoolInfoIndex);
        if (cpi instanceof ConstantClassInfo) {
            int nameIndex = ((ConstantClassInfo) cpi).getNameIndex();
            //noinspection unchecked
            return ((ConstantPrimitive<String>) getConstantPoolInfo(nameIndex)).getValue();
        }

        return null;
    }

    @Override
    public String getCanonicalName() {
        return getClassName(thisClassIndex);
    }

    @Override
    public String getSuperClassCanonicalName() {
        return getClassName(superClassIndex);
    }

    @Override
    public int getMajorVersion() {
        return majorVersion;
    }

    @Override
    public int getMinorVersion() {
        return minorVersion;
    }
}
