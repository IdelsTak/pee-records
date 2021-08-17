/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.model.impl;

import com.github.idelstak.pee.records.model.api.Credentials;
import com.github.idelstak.pee.records.model.api.Name;
import com.github.idelstak.pee.records.model.spi.Patient;
import com.github.idelstak.pee.records.model.spi.PeeCycle;
import com.github.idelstak.pee.records.model.spi.core.Person;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import javax.sql.DataSource;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public class MySqlPatient implements Patient, Comparable<Patient> {

    private final DataSource dataSource;
    private final int id;
    private Name name;
    private Credentials credentials;
    private LocalDate dateOfBirth;
    private LocalDate registrationDate;

    public MySqlPatient(DataSource dataSource, int id) {
        this(dataSource, id, null, null, null, null);
    }

    private MySqlPatient(DataSource dataSource, int id, Name name, Credentials credentials, LocalDate dateOfBirth, LocalDate registrationDate) {
        if (dataSource == null) {
            throw new IllegalArgumentException("Data source must not be null");
        }
        this.dataSource = dataSource;
        this.id = id;
        this.name = name;
        this.credentials = credentials;
        this.dateOfBirth = dateOfBirth;
        this.registrationDate = registrationDate;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Name getName() {
        if (name == null) {
            String sql = "SELECT first_name, last_name FROM patients WHERE id = ?";

            try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);

                try (ResultSet rset = stmt.executeQuery()) {
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
            String sql = "SELECT email, password FROM patients WHERE id = ?";

            try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);

                try (ResultSet rset = stmt.executeQuery()) {
                    if (rset.next()) {
                        String email = rset.getString(1);
                        String password = rset.getString(2);

                        credentials = new Credentials(email, password.toCharArray());
                    }
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        }
        return credentials;
    }

    @Override
    public LocalDate getDateOfBirth() {
        if (dateOfBirth == null) {
            String sql = "SELECT date_of_birth FROM patients WHERE id = ?";

            try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);

                try (ResultSet rset = stmt.executeQuery()) {
                    if (rset.next()) {
                        dateOfBirth = rset.getDate(1).toLocalDate();
                    }
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        return dateOfBirth;
    }

    @Override
    public LocalDate getRegistrationDate() {
        if (registrationDate == null) {
            String sql = "SELECT registration_date FROM patients WHERE id = ?";

            try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);

                try (ResultSet rset = stmt.executeQuery()) {
                    if (rset.next()) {
                        registrationDate = rset.getDate(1).toLocalDate();
                    }
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        return registrationDate;
    }

    @Override
    public Iterable<PeeCycle> getPeeCycles() {
        List<PeeCycle> cycles = new ArrayList<>();

        String sql = "SELECT id FROM pee_cycles WHERE patient_id = ?";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rset = stmt.executeQuery()) {
                while (rset.next()) {
                    //TODO: Implement a concrete PeeCycle first
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return cycles;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + this.id;
        hash = 83 * hash + Objects.hashCode(this.name);
        hash = 83 * hash + Objects.hashCode(this.credentials);
        hash = 83 * hash + Objects.hashCode(this.dateOfBirth);
        hash = 83 * hash + Objects.hashCode(this.registrationDate);
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
        final MySqlPatient other = (MySqlPatient) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.getName(), other.getName())) {
            return false;
        }
        if (!Objects.equals(this.getCredentials(), other.getCredentials())) {
            return false;
        }
        if (!Objects.equals(this.getDateOfBirth(), other.getDateOfBirth())) {
            return false;
        }
        return Objects.equals(this.getRegistrationDate(), other.getRegistrationDate());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Patient{id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", credentials=").append(credentials);
        sb.append(", dateOfBirth=").append(dateOfBirth);
        sb.append(", registrationDate=").append(registrationDate);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int compareTo(Patient otherPatient) {
        return Comparator.comparing(Patient::getId)
                .thenComparing(Person::getName)
                .thenComparing(Person::getCredentials)
                .thenComparing(Patient::getDateOfBirth)
                .thenComparing(Patient::getRegistrationDate)
                .compare(this, otherPatient);
    }

}
