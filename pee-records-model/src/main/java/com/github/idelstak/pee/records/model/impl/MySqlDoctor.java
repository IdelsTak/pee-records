/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.model.impl;

import com.github.idelstak.pee.records.model.api.Credentials;
import com.github.idelstak.pee.records.model.api.Name;
import com.github.idelstak.pee.records.model.spi.Doctor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Objects;
import javax.sql.DataSource;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public class MySqlDoctor implements Doctor, Comparable<Doctor> {

    private final DataSource dataSource;
    private final int id;
    private Name name;
    private Credentials credentials;
    private Boolean active;

    public MySqlDoctor(DataSource dataSource, int id) {
        this(dataSource, id, null, null);
    }

    private MySqlDoctor(DataSource dataSource, int id, Name name, Credentials credentials) {
        if (dataSource == null) {
            throw new IllegalArgumentException("Data source must not be null");
        }
        this.dataSource = dataSource;
        this.id = id;
        this.name = name;
        this.credentials = credentials;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Name getName() {
        if (name == null) {
            String sql = "SELECT first_name, last_name FROM doctors WHERE id = ?";

            try ( Connection conn = dataSource.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);

                try ( ResultSet rset = stmt.executeQuery()) {
                    if (rset.next()) {
                        String firstName = rset.getString(1);
                        String lastName = rset.getString(2);

                        name = new Name(firstName, lastName);
                    }
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        return name;
    }

    @Override
    public Credentials getCredentials() {
        if (credentials == null) {
            String sql = "SELECT email, password FROM doctors WHERE id = ?";

            try ( Connection conn = dataSource.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);

                try ( ResultSet rset = stmt.executeQuery()) {
                    if (rset.next()) {
                        String email = rset.getString(1);
                        String password = rset.getString(2);

                        char[] passArr = null;

                        if (password != null) {
                            passArr = password.toCharArray();
                        }

                        credentials = new Credentials(email, passArr);
                    }
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        return credentials;
    }

    @Override
    public boolean isActive() {
        if (active == null) {
            String sql = "SELECT active FROM doctors WHERE id = ?";

            try ( Connection conn = dataSource.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);

                try ( ResultSet rset = stmt.executeQuery()) {
                    if (rset.next()) {
                        int activeFlag = Short.valueOf(rset.getShort(1)).intValue();

                        active = (activeFlag == 0) ? Boolean.FALSE : (activeFlag == 1 ? Boolean.TRUE : Boolean.FALSE);
                    }
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        return active;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + this.id;
        hash = 89 * hash + Objects.hashCode(this.name);
        hash = 89 * hash + Objects.hashCode(this.credentials);
        hash = 89 * hash + Objects.hashCode(this.active);
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
        final MySqlDoctor other = (MySqlDoctor) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.getName(), other.getName())) {
            return false;
        }
        if (!Objects.equals(this.getCredentials(), other.getCredentials())) {
            return false;
        }
        return Objects.equals(this.isActive(), other.isActive());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Doctor{id=").append(id);
        sb.append(", name=").append(this.getName());
        sb.append(", credentials=").append(this.getCredentials());
        sb.append(", active=").append(this.isActive());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int compareTo(Doctor otherDoctor) {
        return Comparator.comparing(Doctor::getId)
                .thenComparing(Doctor::getName)
                .thenComparing(Doctor::getCredentials)
                .thenComparing(Doctor::isActive)
                .compare(this, otherDoctor);
    }

}
