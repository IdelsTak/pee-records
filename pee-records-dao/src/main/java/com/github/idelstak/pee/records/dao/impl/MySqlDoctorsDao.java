/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.dao.impl;

import com.github.idelstak.pee.records.dao.spi.DoctorsDao;
import com.github.idelstak.pee.records.model.api.Credentials;
import com.github.idelstak.pee.records.model.api.Name;
import com.github.idelstak.pee.records.model.impl.MySqlDoctor;
import com.github.idelstak.pee.records.model.spi.Doctor;
import com.github.idelstak.pee.records.model.spi.core.Entity;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public class MySqlDoctorsDao implements DoctorsDao {

    private final DataSource dataSource;

    public MySqlDoctorsDao(DataSource dataSource) {
        if (dataSource == null) {
            throw new IllegalArgumentException("Data source must not be null");
        }
        this.dataSource = dataSource;
    }

    @Override
    public Iterable<Doctor> getAllDoctors() throws IOException {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors";

        try ( Connection conn = dataSource.getConnection();  Statement stmt = conn.createStatement();  ResultSet rset = stmt.executeQuery(sql)) {
            while (rset.next()) {
                doctors.add(new MySqlDoctor(dataSource, rset.getInt(1)));
            }

        } catch (SQLException ex) {
            throw new IOException(ex);
        }
        return doctors;
    }

    @Override
    public Optional<Doctor> getDoctor(Entity doctorId) throws IOException {
        if (doctorId == null) {
            throw new IllegalArgumentException("Doctor's id must not be null");
        }

        Optional<Doctor> optionalDoctor = Optional.empty();
        Iterable<Doctor> allDoctors = this.getAllDoctors();

        for (Doctor doctor : allDoctors) {
            if (doctor.getId() == doctorId.getId()) {
                optionalDoctor = Optional.of(doctor);
                break;
            }
        }

        return optionalDoctor;
    }

    @Override
    public Optional<Entity> addDoctor(Name name, Credentials credentials) throws IOException {
        if (name == null) {
            throw new IllegalArgumentException("Doctor's name must not be null");
        } else if (credentials == null) {
            throw new IllegalArgumentException("Doctor's login credentials must not be null");
        }

        Optional<Entity> optionalDoctorId = Optional.empty();
        String sql = "INSERT INTO doctors (first_name, last_name, email, password, active) VALUES (?, ?, ?, ?, ?)";

        try ( Connection conn = dataSource.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name.getFirstName());
            stmt.setString(2, name.getLastName());
            stmt.setString(3, credentials.getEmail());
            stmt.setString(4, new String(credentials.getPassword()));
            stmt.setShort(5, Integer.valueOf(1).shortValue());

            stmt.executeUpdate();

            try ( ResultSet rset = stmt.getGeneratedKeys()) {
                if (rset.next()) {
                    int id = rset.getInt(1);
                    optionalDoctorId = Optional.of(() -> id);
                }
            }
        } catch (SQLException ex) {
            throw new IOException(ex);
        }

        return optionalDoctorId;
    }

    @Override
    public void rename(Entity doctorId, Name newName) throws IOException {
        if (doctorId == null) {
            throw new IllegalArgumentException("Doctor's id must not be null");
        } else if (newName == null) {
            throw new IllegalArgumentException("Doctor's name must not be null");
        }

        String sql = "UPDATE doctors SET first_name = ?, last_name = ? WHERE id = ?";

        try ( Connection conn = dataSource.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newName.getFirstName());
            stmt.setString(2, newName.getLastName());
            stmt.setInt(3, doctorId.getId());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void updateLoginCredentials(Entity doctorId, Credentials newCredentials) throws IOException {
        if (doctorId == null) {
            throw new IllegalArgumentException("Doctor's id must not be null");
        } else if (newCredentials == null) {
            throw new IllegalArgumentException("Doctor's login credentials must not be null");
        }

        String sql = "UPDATE doctors SET email = ?, password = ? WHERE id = ?";

        try ( Connection conn = dataSource.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newCredentials.getEmail());
            stmt.setString(2, new String(newCredentials.getPassword()));
            stmt.setInt(3, doctorId.getId());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void activate(Entity doctorId) throws IOException {
        this.doActivate(doctorId, true);
    }

    @Override
    public void deactivate(Entity doctorId) throws IOException {
        this.doActivate(doctorId, false);
    }

    private void doActivate(Entity doctorId, boolean activate) throws IOException {
        if (doctorId == null) {
            throw new IllegalArgumentException("Doctor's id must not be null");
        }

        String sql = "UPDATE doctors SET active = ? WHERE id = ?";

        try ( Connection conn = dataSource.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setShort(1, activate ? Integer.valueOf(1).shortValue() : Integer.valueOf(0).shortValue());
            stmt.setInt(2, doctorId.getId());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
    }
}
