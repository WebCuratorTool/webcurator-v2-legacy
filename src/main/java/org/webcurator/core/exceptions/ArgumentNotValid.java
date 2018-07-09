/*
 *  Copyright 2011 The British Library
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
 *
 *  File:        ArgumentNotValid.java
 *  Author:      oakleigh_sk
 */
package org.webcurator.core.exceptions;
  
import java.util.Collection;
  
/**
 * Indicates that one or more arguments are invalid.
 */
@SuppressWarnings("serial")
public class ArgumentNotValid extends RuntimeException {
    /**
     * Constructs new ArgumentNotValid with the specified detail message.
     *
     * @param message The detail message
     */
    public ArgumentNotValid(String message) {
        super(message);
    }
  
    /**
     * Constructs new ArgumentNotValid with the specified detail
     * message and cause.
     *
     * @param message The detail message
     * @param cause   The cause
     */
    public ArgumentNotValid(String message, Throwable cause) {
        super(message, cause);
    }
  
    /**
     * Check if a String argument is null or the empty string.
     *
     * @param val  the value to check
     * @param name the name and type of the value being checked
     * @throws ArgumentNotValid if test fails
     */
    public static void checkNotNullOrEmpty(String val, String name) {
        checkNotNull(val, name);

        if (val.isEmpty()) {
            throw new ArgumentNotValid("The value of the variable '" + name
                    + "' must not be an empty string.");
        }
    }

    /**
     * Check if an Object argument is null.
     *
     * @param val  the value to check
     * @param name the name and type of the value being checked.
     * @throws ArgumentNotValid if test fails
     */
    public static void checkNotNull(Object val, String name) {
        if (val == null) {
            throw new ArgumentNotValid("The value of the variable '" + name
                    + "' must not be null.");
        }
    }

    /**
     * Check if an int argument is less than 0.
     *
     * @param num  argument to check
     * @param name the name and type of the value being checked.
     * @throws ArgumentNotValid if test fails
     */
    public static void checkNotNegative(int num, String name) {
        if (num < 0) {
            throw new ArgumentNotValid("The value of the variable '" + name
                    + "' must be non-negative, but is " + num + ".");
        }
    }

    /**
     * Check if a long argument is less than 0.
     *
     * @param num argument to check
     * @param name the name and type of the value being checked.
     * @throws ArgumentNotValid if test fails
     */
    public static void checkNotNegative(long num, String name) {
        if (num < 0) {
            throw new ArgumentNotValid("The value of the variable '" + name
                   + "' must be non-negative, but is " + num + ".");
        }
    }

    /**
     * Check if an int argument is less than or equal to 0.
     *
     * @param num  argument to check
     * @param name the name and type of the value being checked.
     * @throws ArgumentNotValid if test fails
     */
    public static void checkPositive(int num, String name) {
        if (num <= 0) {
            throw new ArgumentNotValid("The value of the variable '" + name
                    + "' must be positive, but is " + num + ".");
        }
    }

    /**
     * Check if a long argument is less than 0.
     *
     * @param num argument to check
     * @param name the name and type of the value being checked.
     * @throws ArgumentNotValid if test fails
     */
    public static void checkPositive(long num, String name) {
        if (num <= 0) {
            throw new ArgumentNotValid("The value of the variable '" + name
                    + "' must be positive, but is " + num + ".");
        }
    }

    /**
     * Check if a List argument is not null and the list is not empty.
     *
     * @param c argument to check
     * @param name the name and type of the value being checked.
     * @throws ArgumentNotValid if test fails
     */
    public static void checkNotNullOrEmpty(Collection<?> c, String name) {
        checkNotNull(c, name);

        if (c.isEmpty()) {
            throw new ArgumentNotValid("The contents of the variable '" + name
                        + "' must not be empty.");
        }
    }

    /**
     * Check that some condition on input parameters is true and throw an
     * ArgumentNotValid if it is false.
     * @param b the condition to check
     * @param s the error message to be reported
     * @throws ArgumentNotValid if b is false
     */
    public static void checkTrue(boolean b, String s) {
        if (!b) {
            throw new ArgumentNotValid(s);
        }
    }
}

