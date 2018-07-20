package org.webcurator.core.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptPasswordEncoderGenerator {

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            System.out.println("Please supply a password to encode");
        } else {
            String passwordToEncode = args[0];
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(passwordToEncode);
            System.out.println("Hashed Password: " + hashedPassword);
        }
    }
}
