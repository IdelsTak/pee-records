/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.dao.impl;

import com.github.idelstak.pee.records.dao.spi.PeeCyclesDao;
import com.github.idelstak.pee.records.model.impl.MySqlPeeCycle;
import com.github.idelstak.pee.records.model.spi.PeeCycle;
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
public class MySqlPeeCyclesDao implements PeeCyclesDao {

    private final DataSource dataSource;

    public MySqlPeeCyclesDao(DataSource dataSource) {
        if (dataSource == null) {
            throw new IllegalArgumentException("Data source must not be null");
        }
        this.dataSource = dataSource;
    }

    @Override
    public Iterable<PeeCycle> getAllPeeCycles() throws IOException {
        List<PeeCycle> allCycles = new ArrayList<>();

        String sql = "SELECT id FROM pee_cycles";

        try ( Connection conn = dataSource.getConnection();  Statement stmt = conn.createStatement();  ResultSet rset = stmt.executeQuery(sql)) {
            while (rset.next()) {
                allCycles.add(new MySqlPeeCycle(dataSource, rset.getInt(1)));
            }
        } catch (SQLException ex) {
            throw new IOException(ex);
        }

        return allCycles;
    }

    @Override
    public Optional<PeeCycle> getPeeCycle(Entity cycleId) throws IOException {
        if (cycleId == null) {
            throw new IllegalArgumentException("Pee cycle's id must not be null");
        }

        Optional<PeeCycle> optionalCycle = Optional.empty();

        for (PeeCycle cycle : this.getAllPeeCycles()) {
            if (cycle.getId() == cycleId.getId()) {
                optionalCycle = Optional.of(cycle);
                break;
            }
        }
        return optionalCycle;
    }

    @Override
    public Optional<Entity> addPeeCycle(Entity patientId, LocalDate startDate) throws IOException {
        if (patientId == null) {
            throw new IllegalArgumentException("Patient's id must not be null");
        } else if (startDate == null) {
            throw new IllegalArgumentException("Cycle's start date must not be null");
        }

        Optional<Entity> optionalEntity = Optional.empty();

        String sql = "INSERT INTO pee_cycles (patient_id, start_date, end_date) VALUES (?, ?, ?)";

        try ( Connection conn = dataSource.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, patientId.getId());
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(startDate.plusWeeks(13L)));

            stmt.executeUpdate();

            try ( ResultSet rset = stmt.getGeneratedKeys()) {
                if (rset.next()) {
                    int cycleId = rset.getInt(1);

                    optionalEntity = Optional.of(() -> cycleId);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return optionalEntity;
    }

    @Override
    public void updateStartDate(Entity cycleId, LocalDate newStartDate) throws IOException {
        if (cycleId == null) {
            throw new IllegalArgumentException("Cycle's id must not be null");
        } else if (newStartDate == null) {
            throw new IllegalArgumentException("Cycle's start date must not be null");
        }

        String sql = "UPDATE pee_cycles SET start_date = ?, end_date = ? WHERE id = ?";

        try ( Connection conn = dataSource.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(newStartDate));
            stmt.setDate(2, Date.valueOf(newStartDate.plusWeeks(13L)));
            stmt.setInt(3, cycleId.getId());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void updateEndDate(Entity cycleId, LocalDate newEndDate) throws IOException {
        if (cycleId == null) {
            throw new IllegalArgumentException("Cycle's id must not be null");
        } else if (newEndDate == null) {
            throw new IllegalArgumentException("Cycle's end date must not be null");
        }

        String sql = "UPDATE pee_cycles SET start_date = ?, end_date = ? WHERE id = ?";

        try ( Connection conn = dataSource.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(newEndDate.minusWeeks(13L)));
            stmt.setDate(2, Date.valueOf(newEndDate));
            stmt.setInt(3, cycleId.getId());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void removeCycle(Entity cycleId) throws IOException {
        if (cycleId == null) {
            throw new IllegalArgumentException("Cycle's id must not be null");
        }

        String sql = "DELETE FROM pee_cycles WHERE id = ?";

        try ( Connection conn = dataSource.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cycleId.getId());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
    }

}
