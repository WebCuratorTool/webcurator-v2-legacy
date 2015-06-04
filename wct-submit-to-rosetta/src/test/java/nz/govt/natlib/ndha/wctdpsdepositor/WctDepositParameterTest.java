/**
 * nz.govt.natlib.ndha.wctdpsdepositor - Software License
 *
 * Copyright 2007/2009 National Library of New Zealand.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0 
 *
 * or the file "LICENSE.txt" included with the software.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */

package nz.govt.natlib.ndha.wctdpsdepositor;

import org.junit.Test;

import java.lang.reflect.Field;

import static junit.framework.Assert.fail;


public class WctDepositParameterTest {

    @Test
    public void test_exception_thrown_when_field_not_set() throws IllegalAccessException {
        Class clazz =  WctDepositParameter.class;
        WctDepositParameter parameter = new WctDepositParameter();

        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field  = fields[i];
            field.setAccessible(true);
            field.set(parameter, "a value");
            try {
                parameter.isValid();
               
                if (isNotLastFieldOfArray(fields, i))
                    fail("exception should have been thrown");
            }
            catch (WctDepositParameterValidationException ex) { /*  */ }
        }

        // all parameters should now be set => valid.
        parameter.isValid();
    }

    private boolean isNotLastFieldOfArray(Field[] fields, int i) {
        return i < fields.length - /*number of access codes*/ 4 /**/ - 1;
    }


}
