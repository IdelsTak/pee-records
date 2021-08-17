/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.controller.patient;

import com.calendarfx.model.Calendar;
import static com.calendarfx.model.CalendarEvent.ANY;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.model.Interval;
import com.calendarfx.view.AllDayView;
import com.calendarfx.view.CalendarView;
import com.calendarfx.view.DateControl;
import com.calendarfx.view.EntryViewBase;
import com.calendarfx.view.Messages;
import com.calendarfx.view.VirtualGrid;
import com.github.idelstak.pee.records.model.api.Name;
import com.github.idelstak.pee.records.model.spi.Patient;
import com.github.idelstak.pee.records.model.spi.PeeCycle;
import static java.lang.Thread.sleep;
import java.text.MessageFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public class PatientViewController {

    @FXML
    private Button addCycleButton;
    @FXML
    private Button editCycleButton;
    @FXML
    private Button removeCycleButton;
    @FXML
    private Label usernameLabel;
    @FXML
    private Hyperlink logoutHyperlink;
    @FXML
    private ListView<PeeCycle> cyclesListView;
    @FXML
    private CalendarView calendarView;
    private final Patient patient;

    public PatientViewController(Patient patient) {
        this.patient = patient;
    }

    @FXML
    public void initialize() {
        Name name = patient.getName();
        String firstName = name.getFirstName();
        String lastName = name.getLastName();
        String email = patient.getCredentials().getEmail();

        usernameLabel.setText("%s %s (%s)".formatted(firstName, lastName, email));

        calendarView.getDayPage().getAgendaView().setShowStatusLabel(false);

        calendarView.setEntryDetailsPopOverContentCallback(param -> new BorderPane(new Label("Custom Popup")));
        Calendar dryCalendar = new Calendar("Dry Night");
        dryCalendar.setStyle(Calendar.Style.STYLE1);

        Calendar dropsCalendar = new Calendar("Few Drops");
        dropsCalendar.setStyle(Calendar.Style.STYLE3);

        Calendar wetCalendar = new Calendar("Wet Night");
        wetCalendar.setStyle(Calendar.Style.STYLE6);

        CalendarSource cycle1Source = new CalendarSource("Cycle 1");

        cycle1Source.getCalendars().addAll(dryCalendar, wetCalendar, dropsCalendar);

        calendarView.getCalendarSources().setAll(cycle1Source);
        calendarView.setRequestedTime(LocalTime.now());
        
        calendarView.setEntryFactory(param -> {
            DateControl control = param.getDateControl();

            VirtualGrid grid = control.getVirtualGrid();
            ZonedDateTime time = param.getZonedDateTime();
            DayOfWeek firstDayOfWeek = calendarView.getFirstDayOfWeek();
            ZonedDateTime lowerTime = grid.adjustTime(time, false, firstDayOfWeek);
            ZonedDateTime upperTime = grid.adjustTime(time, true, firstDayOfWeek);

            if (Duration.between(time, lowerTime).abs().minus(Duration.between(time, upperTime).abs()).isNegative()) {
                time = lowerTime;
            } else {
                time = upperTime;
            }

            Entry<Object> entry = new Entry<>(dryCalendar.getName());
            entry.setCalendar(dryCalendar);

            Interval interval = new Interval(time.toLocalDateTime(), time.toLocalDateTime().plusHours(1));
            entry.setInterval(interval);

            if (control instanceof AllDayView) {
                entry.setFullDay(true);
            }

            return entry;
        });

        calendarView.setEntryContextMenuCallback(param -> {
            EntryViewBase<?> entryView = param.getEntryView();
            Entry<?> entry = entryView.getEntry();

            ContextMenu contextMenu = new ContextMenu();

            Menu calendarMenu = new Menu("Pee Type");
            cycle1Source.getCalendars()
                    .stream()
                    .map(calendar -> {
                        MenuItem calendarItem = new MenuItem(calendar.getName());
                        calendarItem.setOnAction(evt -> {
                            entry.setTitle(calendar.getName());
                            entry.setCalendar(calendar);
                        });
                        return calendarItem;
                    })
                    .forEachOrdered(calendarMenu.getItems()::add);

            contextMenu.getItems().add(calendarMenu);

            return contextMenu;
        });

        Thread updateTimeThread = new Thread("Calendar: Update Time Thread") {
            @Override
            public void run() {
                while (true) {
                    Platform.runLater(() -> {
                        calendarView.setToday(LocalDate.now());
                        calendarView.setTime(LocalTime.now());
                    });

                    try {
                        // update every 10 seconds
                        sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        updateTimeThread.setPriority(Thread.MIN_PRIORITY);
        updateTimeThread.setDaemon(true);
        updateTimeThread.start();


    }
    private static final Logger logger = Logger.getLogger(PatientViewController.class.getName());
}
