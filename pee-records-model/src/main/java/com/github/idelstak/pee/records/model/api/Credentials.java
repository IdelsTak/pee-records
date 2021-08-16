/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.model.api;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public class Credentials implements Comparable<Credentials> {

    private final String email;
    private final char[] password;

    public Credentials(String email, char[] password) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email must not be blank");
        }
        if (password == null || password.length == 0) {
            throw new IllegalArgumentException("Password must not be blank");
        }
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public char[] getPassword() {
        return password;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.email);
        hash = 97 * hash + Arrays.hashCode(this.password);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Credentials other = (Credentials) obj;
        if (!Objects.equals(this.email, other.email)) {
            return false;
        }
        return Arrays.equals(this.password, other.password);
    }

    @Override
    public String toString() {
        return "Login{" + "email=" + email + '}';
    }

    @Override
    public int compareTo(Credentials otherLogin) {
        return Comparator.comparing(Credentials::getEmail).compare(this, otherLogin);
    }

}
