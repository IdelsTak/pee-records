/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.dao.spi.impl;

import com.github.idelstak.pee.records.dao.spi.PatientsDao;
import com.github.idelstak.pee.records.model.api.Credentials;
import com.github.idelstak.pee.records.model.api.Name;
import com.github.idelstak.pee.records.model.impl.MySqlPatient;
import com.github.idelstak.pee.records.model.spi.Patient;
import com.github.idelstak.pee.records.model.spi.core.Entity;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public class MySqlPatientsDao implements PatientsDao {

    private final DataSource dataSource;

    public MySqlPatientsDao(DataSource dataSource) {
        if (dataSource == null) {
            throw new IllegalArgumentException("Data source must not be null");
        }
        this.dataSource = dataSource;
    }

    @Override
    public Iterable<Patient> getAllPatients() throws IOException {
        List<Patient> allPatients = new ArrayList<>();

        String sql = "SELECT id FROM patients";

        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement(); ResultSet rset = stmt.executeQuery(sql)) {
            while (rset.next()) {
                allPatients.add(new MySqlPatient(dataSource, rset.getInt(1)));
            }
        } catch (SQLException ex) {
            throw new IOException(ex);
        }

        return allPatients;
    }

    @Override
    public Optional<Patient> getPatient(Entity patientId) throws IOException {
        if (patientId == null) {
            throw new IllegalArgumentException("Patient's id must not be null");
        }
        Optional<Patient> optionalPatient = Optional.empty();

        for (Patient patient : this.getAllPatients()) {
            if (patient.getId() == patientId.getId()) {
                optionalPatient = Optional.of(patient);
                break;
            }
        }

        return optionalPatient;
    }

    @Override
    public Optional<Entity> addPatient(Name name, Credentials credentials, LocalDate dateOfBirth, LocalDate registrationDate) throws IOException {
        if (name == null) {
            throw new IllegalArgumentException("Patient's name must not be null");
        } else if (credentials == null) {
            throw new IllegalArgumentException("Patient's login credentials must not be null");
        } else if (this.emailExists(credentials)) {
            throw new IllegalArgumentException("Email address [%s] already exists".formatted(credentials.getEmail()));
        } else if (dateOfBirth == null) {
            throw new IllegalArgumentException("Patient's date of birth must not be null");
        } else if (registrationDate == null) {
            throw new IllegalArgumentException("Patient's registration date must not be null");
        }

        Optional<Entity> optionalEntity = Optional.empty();

        String sql = "INSERT INTO patients (first_name, last_name, date_of_birth, registration_date, email, password) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name.getFirstName());
            stmt.setString(2, name.getLastName());
            stmt.setDate(3, Date.valueOf(dateOfBirth));
            stmt.setDate(4, Date.valueOf(registrationDate));
            stmt.setString(5, credentials.getEmail());
            stmt.setString(6, new String(credentials.getPassword()));

            stmt.executeUpdate();

            try (ResultSet rset = stmt.getGeneratedKeys()) {
                if (rset.next()) {
                    int patientId = rset.getInt(1);
                    optionalEntity = Optional.of(() -> patientId);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return optionalEntity;
    }

    @Override
    public void rename(Entity patientId, Name newName) throws IOException {
        if (patientId == null) {
            throw new IllegalArgumentException("Patient's id must not be null");
        } else if (newName == null) {
            throw new IllegalArgumentException("Patient's name must not be null");
        }

        String sql = "UPDATE patients SET first_name = ?, last_name = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newName.getFirstName());
            stmt.setString(2, newName.getLastName());
            stmt.setInt(3, patientId.getId());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void updateLoginCredentials(Entity patientId, Credentials newCredentials) throws IOException {
        if (patientId == null) {
            throw new IllegalArgumentException("Patient's id must not be null");
        } else if (newCredentials == null) {
            throw new IllegalArgumentException("Patient's login credentials must not be null");
        } else if (this.emailExists(newCredentials)) {
            throw new IllegalArgumentException("Email address [%s] already exists".formatted(newCredentials.getEmail()));
        }

        String sql = "UPDATE patients SET email = ?, password = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newCredentials.getEmail());
            stmt.setString(2, new String(newCredentials.getPassword()));
            stmt.setInt(3, patientId.getId());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void updateDateOfBirth(Entity patientId, LocalDate newDateOfBirth) throws IOException {
        if (patientId == null) {
            throw new IllegalArgumentException("Patient's id must not be null");
        } else if (newDateOfBirth == null) {
            throw new IllegalArgumentException("Patient's date of birth must be null");
        }

        String sql = "UPDATE patients SET date_of_birth = ?, WHERE id = ?";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(newDateOfBirth));
            stmt.setInt(2, patientId.getId());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void updateRegistrationDate(Entity patientId, LocalDate newRegistrationDate) throws IOException {
        if (patientId == null) {
            throw new IllegalArgumentException("Patient's is must not be null");
        } else if (newRegistrationDate == null) {
            throw new IllegalArgumentException("Patient's registration date must not be null");
        }

        String sql = "UPDATE patients SET registration_date = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(newRegistrationDate));
            stmt.setInt(2, patientId.getId());
            
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
    }

    private boolean emailExists(Credentials credentials) throws IOException {
        boolean emailExists = false;

        for (Patient patient : this.getAllPatients()) {
            if (emailsMatch(credentials, patient.getCredentials())) {
                emailExists = true;
                break;
            }
        }
        return emailExists;
    }

    private static boolean emailsMatch(Credentials c, Credentials c1) {
        return c1.getEmail().trim().toLowerCase().equals(c.getEmail().trim().toLowerCase());
    }

}
