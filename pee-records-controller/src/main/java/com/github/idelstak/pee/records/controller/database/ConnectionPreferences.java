/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.controller.database;

import java.util.Arrays;
import java.util.prefs.Preferences;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public class ConnectionPreferences {

    private static final String URL = "url";
    private static final String USER = "user";
    private static final String PASSWORD = "pass";
    private final Preferences prefs;
    private String url;
    private String username;
    private char[] password;

    public ConnectionPreferences() {
        this.prefs = Preferences.userNodeForPackage(ConnectionPreferences.class);
        
//        try {
//            prefs.clear();
//        } catch (BackingStoreException ex) {
//            Logger.getLogger(ConnectionPreferences.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    public String getUrl() {
        if (url == null) {
            this.url = prefs.get(URL, "");
        }
        return url;
    }

    public void setUrl(String url) {
        prefs.put(URL, url);
        this.url = url;
    }

    public String getUsername() {
        if (username == null) {
            this.username = prefs.get(USER, "");
        }
        return username;
    }

    public void setUsername(String username) {
        prefs.put(USER, username);
        this.username = username;
    }

    public char[] getPassword() {
        if (password == null) {
            this.password = prefs.get(PASSWORD, "").toCharArray();
        }
        return password == null || password.length == 0
                ? new char[]{}
                : Arrays.copyOf(password, password.length);
    }

    public void setPassword(char[] password) {
        prefs.put(PASSWORD, new String(password == null || password.length == 0 ? new char[]{} : password));
        this.password = password;
    }

}
