package org.webcurator.core.harvester.agent;

import java.io.File;

public class Heritrix3WrapperConfiguration {
    /** H3 instance host name or ip address that the core knows about. */
    private String host = "";
    /** The port the H3 instance is listening on for http connections */
    private int port = 8443;
    /** The full path and filename for the keystore file. */
    private File keyStoreFile = null;
    /** The password for the keystore file. */
    private String keyStorePassword = "";
    /** The userName for the H3 instance. */
    private String userName = "admin";
    /** The password for the keystore file. */
    private String password = "admin";

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public File getKeyStoreFile() {
        return keyStoreFile;
    }

    public void setKeyStoreFile(File keyStoreFile) {
        this.keyStoreFile = keyStoreFile;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
