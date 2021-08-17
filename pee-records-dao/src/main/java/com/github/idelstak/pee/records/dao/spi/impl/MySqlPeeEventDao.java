/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.dao.spi.impl;

import com.github.idelstak.pee.records.dao.spi.PeeEventDao;
import com.github.idelstak.pee.records.model.impl.MySqlPeeEvent;
import com.github.idelstak.pee.records.model.spi.PeeCycle;
import com.github.idelstak.pee.records.model.spi.PeeEvent;
import com.github.idelstak.pee.records.model.spi.core.Entity;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public class MySqlPeeEventDao implements PeeEventDao {

    private final DataSource dataSource;

    public MySqlPeeEventDao(DataSource dataSource) {
        if (dataSource == null) {
            throw new IllegalArgumentException("Data source must not be null");
        }
        this.dataSource = dataSource;
    }

    @Override
    public Iterable<PeeEvent> getAllEvents() throws IOException {
        List<PeeEvent> allEvents = new ArrayList<>();

        String sql = "SELECT id FROM pee_cycle_events";

        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement(); ResultSet rset = stmt.executeQuery(sql)) {
            while (rset.next()) {
                allEvents.add(new MySqlPeeEvent(dataSource, rset.getInt(1)));
            }
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
        return allEvents;
    }

    @Override
    public Optional<PeeEvent> getEvent(Entity eventId) throws IOException {
        if (eventId == null) {
            throw new IllegalArgumentException("Pee event id must not be null");
        }

        Optional<PeeEvent> optionalEvent = Optional.empty();

        for (PeeEvent event : this.getAllEvents()) {
            if (event.getId() == event.getId()) {
                optionalEvent = Optional.of(event);
                break;
            }
        }
        return optionalEvent;
    }

    @Override
    public Optional<Entity> addEvent(Entity cycleId, LocalDateTime whenPeed, PeeEvent.Type type) throws IOException {
        if (cycleId == null) {
            throw new IllegalArgumentException("Pee event cycle's id is must not be null");
        } else if (whenPeed == null) {
            throw new IllegalArgumentException("The time pee occurred must not be null");
        } else if (!this.validPeeDate(cycleId, whenPeed)) {
            throw new IllegalArgumentException("Pee time is not within the cycle's period");
        } else if (type == null) {
            throw new IllegalArgumentException("Pee type must not be null");
        }

        Optional<Entity> optionalEntity = Optional.empty();

        String sql = "INSERT INTO pee_cycle_events (pee_cycle_id, when_peed, pee_type) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, cycleId.getId());
            stmt.setTimestamp(2, Timestamp.valueOf(whenPeed));
            stmt.setString(3, type.toString());

            stmt.executeUpdate();

            try (ResultSet rset = stmt.getGeneratedKeys()) {
                if (rset.next()) {
                    int eventId = rset.getInt(1);

                    optionalEntity = Optional.of(() -> eventId);
                }
            }

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return optionalEntity;
    }

    @Override
    public void updateTime(Entity eventId, LocalDateTime newPeeTime) throws IOException {
        if (eventId == null) {
            throw new IllegalArgumentException("Pee event's id must not be null");
        } else if (newPeeTime == null) {
            throw new IllegalArgumentException("Pee event's time must not be null");
        } else if (!validPeeDate(this.getCyleId(eventId), newPeeTime)) {
            throw new IllegalArgumentException("Pee event's time is not within the cycle's period");
        }

        String sql = "UPDATE pee_cycle_events SET when_peed = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(newPeeTime));
            stmt.setInt(1, eventId.getId());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void updateType(Entity eventId, PeeEvent.Type newType) throws IOException {
        if (eventId == null) {
            throw new IllegalArgumentException("Pee event's id must not be null");
        } else if (newType == null) {
            throw new IllegalArgumentException("Pee event type must not be null");
        }

        String sql = "UPDATE pee_cycle_events SET pee_type = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newType.toString());
            stmt.setInt(1, eventId.getId());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public void removeEvent(Entity eventId) throws IOException {
        if (eventId == null) {
            throw new IllegalArgumentException("Pee event's id must not be null");
        }

        String sql = "DELETE FROM pee_cycle_events WHERE id = ?";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId.getId());

            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
    }

    private boolean validPeeDate(Entity cycleId, LocalDateTime whenPeed) throws IOException {
        MySqlPeeCyclesDao cyclesDao = new MySqlPeeCyclesDao(dataSource);

        return cyclesDao.getPeeCycle(cycleId)
                .map(cycle -> withinCycleDates(cycle, whenPeed))
                .orElse(false);
    }

    private boolean withinCycleDates(PeeCycle cycle, LocalDateTime whenPeed) {
        //The pee date must be between the cycle's start and end dates
        LocalDate datePeed = whenPeed.toLocalDate();

        return !datePeed.isBefore(cycle.getStartDate())
                && !datePeed.isAfter(cycle.getEndDate());
    }

    private Entity getCyleId(Entity eventId) throws IOException {
        return this.getEvent(eventId)
                .map(PeeEvent::getCycle)
                .map(Entity.class::cast)
                .orElse(() -> -1);
    }

}
