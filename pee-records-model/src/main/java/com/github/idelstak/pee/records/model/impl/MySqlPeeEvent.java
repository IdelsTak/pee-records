/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.model.impl;

import com.github.idelstak.pee.records.model.spi.PeeCycle;
import com.github.idelstak.pee.records.model.spi.PeeEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;
import javax.sql.DataSource;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public class MySqlPeeEvent implements PeeEvent, Comparable<PeeEvent> {

    private final DataSource dataSource;
    private final int id;
    private PeeCycle cycle;
    private LocalDateTime when;
    private Type type;

    public MySqlPeeEvent(DataSource dataSource, int id) {
        this(dataSource, id, null, null, null);
    }

    private MySqlPeeEvent(DataSource dataSource, int id, PeeCycle cycle, LocalDateTime when, Type type) {
        if (dataSource == null) {
            throw new IllegalArgumentException("Data source must not be null");
        }
        this.dataSource = dataSource;
        this.id = id;
        this.cycle = cycle;
        this.when = when;
        this.type = type;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public PeeCycle getCycle() {
        if (cycle == null) {

            String sql = "SELECT pee_cycle_id FROM pee_cycle_events WHERE id = ?";

            try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);

                try (ResultSet rset = stmt.executeQuery()) {
                    if (rset.next()) {
                        int cycleId = rset.getInt(1);
                        //TODO: implement concrete PeeCycle class first
                    }
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        return cycle;
    }

    @Override
    public LocalDateTime getWhen() {
        if (when == null) {
            String sql = "SELECT when_peed FROM pee_cycle_events WHERE id = ?";

            try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);

                try (ResultSet rset = stmt.executeQuery()) {
                    if (rset.next()) {
                        when = rset.getTimestamp(1).toLocalDateTime();
                    }
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        return when;
    }

    @Override
    public Type getType() {
        if (type == null) {
            String sql = "SELECT pee_type FROM pee_cycle_events WHERE id = ?";

            try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);

                try (ResultSet rset = stmt.executeQuery()) {
                    if (rset.next()) {
                        String t = rset.getString(1);
                        type = new Type.Description(t).toPeeType();
                    }
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
        return type;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.id;
        hash = 79 * hash + Objects.hashCode(this.cycle);
        hash = 79 * hash + Objects.hashCode(this.when);
        hash = 79 * hash + Objects.hashCode(this.type);
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
        final MySqlPeeEvent other = (MySqlPeeEvent) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.getCycle(), other.getCycle())) {
            return false;
        }
        if (!Objects.equals(this.getWhen(), other.getWhen())) {
            return false;
        }
        return this.getType() == other.getType();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PeeEvent{id=").append(id);
        sb.append(", cycle=").append(this.getCycle());
        sb.append(", when=").append(this.getWhen());
        sb.append(", type=").append(this.getType());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int compareTo(PeeEvent otherEvent) {
        return Comparator.comparing(PeeEvent::getId)
                .thenComparing(event -> event.getCycle().getId())
                .thenComparing(PeeEvent::getWhen)
                .thenComparing(PeeEvent::getType)
                .compare(this, otherEvent);
    }

}
