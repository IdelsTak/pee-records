/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.model.api;

import java.util.Comparator;
import java.util.Objects;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public class Name implements Comparable<Name> {

    private final String firstName;
    private final String lastName;

    public Name(String firstName, String lastName) {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("First name must not be blank");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Last name must not be blank");
        }
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + Objects.hashCode(this.firstName);
        hash = 13 * hash + Objects.hashCode(this.lastName);
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
        final Name other = (Name) obj;
        if (!Objects.equals(this.firstName, other.firstName)) {
            return false;
        }
        return Objects.equals(this.lastName, other.lastName);
    }

    @Override
    public int compareTo(Name otherName) {
        return Comparator.comparing(Name::getFirstName)
                .thenComparing(Name::getLastName)
                .compare(this, otherName);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name{firstName=").append(firstName);
        sb.append(", lastName=").append(lastName);
        sb.append('}');
        return sb.toString();
    }

}
