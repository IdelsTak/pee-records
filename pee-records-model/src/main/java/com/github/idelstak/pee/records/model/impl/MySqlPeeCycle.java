/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.model.impl;

import com.github.idelstak.pee.records.model.spi.Patient;
import com.github.idelstak.pee.records.model.spi.PeeCycle;
import com.github.idelstak.pee.records.model.spi.PeeEvent;
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
public class MySqlPeeCycle implements PeeCycle, Comparable<PeeCycle> {

    private final DataSource dataSource;
    private final int id;
    private Patient patient;
    private LocalDate startDate;
    private LocalDate endDate;

    public MySqlPeeCycle(DataSource dataSource, int id) {
        this(dataSource, id, null, null, null);
    }

    private MySqlPeeCycle(DataSource dataSource, int id, Patient patient, LocalDate startDate, LocalDate endDate) {
        if (dataSource == null) {
            throw new IllegalArgumentException("Data source must not be null");
        }
        this.dataSource = dataSource;
        this.id = id;
        this.patient = patient;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Patient getPatient() {
        if (patient == null) {
            String sql = "SELECT patient_id FROM pee_cycles WHERE id = ?";

            try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);

                try (ResultSet rset = stmt.executeQuery()) {
                    if (rset.next()) {
                        patient = new MySqlPatient(dataSource, rset.getInt(1));
                    }
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        return patient;
    }

    @Override
    public LocalDate getStartDate() {
        if (startDate == null) {
            String sql = "SELECT start_date FROM pee_cycles WHERE id = ?";

            try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);

                try (ResultSet rset = stmt.executeQuery()) {
                    if (rset.next()) {
                        startDate = rset.getDate(1).toLocalDate();
                    }
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        return startDate;
    }

    @Override
    public LocalDate getEndDate() {
        if (endDate == null) {
            String sql = "SELECT end_date FROM pee_cycles WHERE id = ?";

            try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);

                try (ResultSet rset = stmt.executeQuery()) {
                    if (rset.next()) {
                        endDate = rset.getDate(1).toLocalDate();
                    }
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        return endDate;
    }

    @Override
    public Iterable<PeeEvent> getPeeEvents() {
        List<PeeEvent> events = new ArrayList<>();

        String sql = "SELECT id FROM pee_cycle_events WHERE pee_cycle_id = ?";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rset = stmt.executeQuery()) {
                while (rset.next()) {
                    events.add(new MySqlPeeEvent(dataSource, rset.getInt(1)));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return events;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + this.id;
        hash = 23 * hash + Objects.hashCode(this.patient);
        hash = 23 * hash + Objects.hashCode(this.startDate);
        hash = 23 * hash + Objects.hashCode(this.endDate);
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
        final MySqlPeeCycle other = (MySqlPeeCycle) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.getPatient(), other.getPatient())) {
            return false;
        }
        if (!Objects.equals(this.getStartDate(), other.getStartDate())) {
            return false;
        }
        return Objects.equals(this.getEndDate(), other.getEndDate());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PeeCycle{id=").append(id);
        sb.append(", patient=").append(this.getPatient());
        sb.append(", startDate=").append(this.getStartDate());
        sb.append(", endDate=").append(this.getEndDate());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int compareTo(PeeCycle otherCycle) {
        return Comparator.comparing(PeeCycle::getId)
                .thenComparing(cycle -> cycle.getPatient().getId())
                .thenComparing(PeeCycle::getStartDate)
                .thenComparing(PeeCycle::getEndDate)
                .compare(this, otherCycle);
    }

}
