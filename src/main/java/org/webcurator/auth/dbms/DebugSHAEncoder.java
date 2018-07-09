/*
 *  Copyright 2006 The National Library of New Zealand
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.webcurator.auth.dbms;

import org.acegisecurity.providers.encoding.BaseDigestPasswordEncoder;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * This is a test password encoder and must NOT be used in production.
 * The password encoder implements the abstract methods from the BaseDigestPasswordEncoder
 * and prints passwords in clear text to the console.
 * @author bprice
 */
public class DebugSHAEncoder extends BaseDigestPasswordEncoder{
    
    public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
        String pass1 = "" + encPass;
        String pass2 = encodePassword(rawPass, salt);
        
        return pass1.equals(pass2);
    }

    public String encodePassword(String rawPass, Object salt) {
        String saltedPass = mergePasswordAndSalt(rawPass, salt, false);
        
        System.out.println("mergedPasswordAndSalt: ["+saltedPass+"]");

        if (!getEncodeHashAsBase64()) {
            System.out.println("Not Doing base 64");
            return DigestUtils.shaHex(saltedPass);
        }

        
        byte[] encoded = Base64.encodeBase64(DigestUtils.sha(saltedPass));
        System.out.println("encodedPass: ["+new String(encoded)+"]");

        return new String(encoded);
    }
}
