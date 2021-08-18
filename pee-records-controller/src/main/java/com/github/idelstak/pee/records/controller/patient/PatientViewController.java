/*
 * Copyright 2021
 */
package com.github.idelstak.pee.records.controller.patient;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.YearMonthView;
import com.github.idelstak.pee.records.controller.util.DateStringConverter;
import com.github.idelstak.pee.records.dao.impl.MySqlPeeCyclesDao;
import com.github.idelstak.pee.records.dao.impl.MySqlPeeEventsDao;
import com.github.idelstak.pee.records.model.api.Name;
import com.github.idelstak.pee.records.model.spi.Patient;
import com.github.idelstak.pee.records.model.spi.PeeCycle;
import com.github.idelstak.pee.records.model.spi.PeeEvent;
import com.github.idelstak.pee.records.model.spi.core.Entity;
import com.github.idelstak.pee.records.view.api.FxmlParent;
import com.github.idelstak.pee.records.view.patient.CycleDetailsFxml;
import com.github.idelstak.pee.records.view.patient.PeeEventFxml;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javax.sql.DataSource;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public class PatientViewController {

    @FXML
    private Label usernameLabel;

    @FXML
    private Hyperlink logoutHyperlink;

    @FXML
    private Button addCycleButton;

    @FXML
    private Button editCycleButton;

    @FXML
    private Button removeCycleButton;

    @FXML
    private ListView<PeeCycle> cyclesListView;

    @FXML
    private BorderPane cycleEventsPane;

    @FXML
    private Button addEventButton;

    @FXML
    private Button editEventButton;

    @FXML
    private Button removeEventButton;

    @FXML
    private Label noCycleSelectedLabel;
    private final DataSource dataSource;
    private final MySqlPeeCyclesDao cyclesDao;
    private final Patient patient;
    private final MySqlPeeEventsDao eventsDao;
    private final ObservableList<Entry<PeeEvent>> cycleEventEntries;
    private final Calendar calendar;
    private final CalendarSource calendarSource;

    public PatientViewController(DataSource dataSource, Patient patient) {
        this.cycleEventEntries = FXCollections.observableArrayList();
        this.cyclesDao = new MySqlPeeCyclesDao(dataSource);
        this.eventsDao = new MySqlPeeEventsDao(dataSource);
        this.dataSource = dataSource;
        this.patient = patient;
        this.calendar = new Calendar();
        this.calendarSource = new CalendarSource();
    }

    @FXML
    public void initialize() {
        calendarSource.getCalendars().setAll(calendar);

        Name name = patient.getName();
        String firstName = name.getFirstName();
        String lastName = name.getLastName();
        String email = patient.getCredentials().getEmail();

        usernameLabel.setText("%s %s (%s)".formatted(firstName, lastName, email));

        this.refreshCyclesList();

        Label noCycleLabel = new Label("<No Cycle Available>");
        noCycleLabel.setDisable(true);

        cyclesListView.setPlaceholder(noCycleLabel);
        cyclesListView.setCellFactory(param -> new CustomCell());

        addCycleButton.setOnAction(eh -> showNewCycleDialog());
        editCycleButton.setOnAction(eh -> showEditCycleDialog());
        removeCycleButton.setOnAction(eh -> showRemoveCycleDialog());

        cyclesListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((o, ov, nv) -> {
                    editCycleButton.setDisable(nv == null);
                    removeCycleButton.setDisable(nv == null);
                });

        addEventButton.setOnAction(eh -> showNewEventDialog());

        cyclesListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((o, ov, nv) -> {
                    noCycleSelectedLabel.setVisible(nv == null);

                    this.refreshEventEntries();
                    this.refreshMonthViews();
                });

        cyclesListView.getItems()
                .addListener((ListChangeListener.Change<? extends PeeCycle> change) -> {
                    if (change.next() && cyclesListView.getItems().isEmpty()) {
                        cyclesListView.getSelectionModel().clearSelection();
                    }
                });

        cycleEventsPane.visibleProperty().bind(noCycleSelectedLabel.visibleProperty().not());
    }

    private void showNewCycleDialog() {
        Alert alert = new Alert(Alert.AlertType.NONE);

        alert.setTitle("Pee Calendar");
        CycleDetailsController controller = new CycleDetailsController(LocalDate.now());
        FxmlParent fp = new FxmlParent(new CycleDetailsFxml(), controller);

        alert.setDialogPane((DialogPane) fp.get());

        alert.showAndWait()
                .filter(btn -> btn == controller.getSaveButton())
                .ifPresent(btn -> {
                    try {
                        Optional<Entity> oe = cyclesDao.addPeeCycle(patient, controller.getStartDate());

                        refreshCyclesList();

                        oe.ifPresent(e -> {
                            try {
                                cyclesDao.getPeeCycle(e)
                                        .ifPresent(c -> {
                                            Platform.runLater(() -> {
                                                cyclesListView.getSelectionModel().select(c);
                                                cyclesListView.requestFocus();
                                            });
                                        });
                            } catch (IOException ex) {
                                throw new RuntimeException("A database error occured");
                            }
                        });

                    } catch (IOException ex) {
                        throw new RuntimeException("A database error occured");
                    }
                });
    }

    private void showEditCycleDialog() {
        PeeCycle selectedCycle = cyclesListView.getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.NONE);

        alert.setTitle("Pee Calendar");
        CycleDetailsController controller = new CycleDetailsController(selectedCycle.getStartDate());
        FxmlParent fp = new FxmlParent(new CycleDetailsFxml(), controller);

        alert.setDialogPane((DialogPane) fp.get());

        alert.showAndWait()
                .filter(btn -> btn == controller.getSaveButton())
                .ifPresent(btn -> {
                    try {
                        cyclesDao.updateStartDate(selectedCycle, controller.getStartDate());

                        refreshCyclesList();

                        Platform.runLater(() -> {
                            cyclesListView.getItems()
                                    .stream()
                                    .filter(cycle -> cycle.getId() == selectedCycle.getId())
                                    .findFirst()
                                    .ifPresent(c -> {
                                        cyclesListView.getSelectionModel().select(c);
                                        cyclesListView.requestFocus();
                                    });
                        });
                    } catch (IOException ex) {
                        throw new RuntimeException("A database error occured");
                    }
                });
    }

    private void showRemoveCycleDialog() {
        PeeCycle selectedCycle = cyclesListView.getSelectionModel().getSelectedItem();

        Alert alert = new Alert(Alert.AlertType.WARNING);

        alert.setTitle("Pee Calendar");
        alert.setHeaderText("Delete cycle permanently?");
        alert.setContentText("This action cannot be undone");

        ButtonType yesBtn = new ButtonType("Yes, Delete", ButtonBar.ButtonData.NO);
        ButtonType noBtn = new ButtonType("No", ButtonBar.ButtonData.OK_DONE);

        alert.getButtonTypes().setAll(yesBtn, noBtn);

        alert.showAndWait()
                .filter(btn -> btn == yesBtn)
                .ifPresent(btn -> {
                    try {
                        cyclesDao.removeCycle(selectedCycle);

                        this.refreshCyclesList();

                        Platform.runLater(() -> {
                            if (!cyclesListView.getItems().isEmpty()) {
                                cyclesListView.getSelectionModel().selectFirst();
                                cyclesListView.requestFocus();
                            }
                        });
                    } catch (IOException ex) {
                        throw new RuntimeException("A database error occured");
                    }
                });
    }

    private void refreshCyclesList() {
        cyclesListView.getItems().clear();

        try {
            Iterable<PeeCycle> cycles = cyclesDao.getAllPeeCycles();
            for (PeeCycle cycle : cycles) {
                cyclesListView.getItems().add(cycle);
            }
        } catch (IOException ex) {
            throw new RuntimeException("A database error occured");
        }
    }

    private void showNewEventDialog() {
        PeeEventDetailsController controller = new PeeEventDetailsController();
        FxmlParent fp = new FxmlParent(new PeeEventFxml(), controller);
        Alert alert = new Alert(Alert.AlertType.NONE);

        alert.setTitle("Pee Calendar");

        alert.setDialogPane((DialogPane) fp.get());

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);

        alert.getButtonTypes().setAll(saveBtn, ButtonType.CANCEL);

        alert.showAndWait()
                .filter(btn -> btn == saveBtn)
                .ifPresent(btn -> {
                    PeeCycle cycle = cyclesListView.getSelectionModel().getSelectedItem();

                    if (cycle != null) {
                        try {
                            LocalDateTime time = controller.getEventDate().atTime(LocalTime.now());
                            PeeEvent.Type type = controller.getType();

                            eventsDao.addEvent(cycle, time, type);

                            this.refreshEventEntries();

                        } catch (IOException ex) {
                            throw new RuntimeException("A database error occured");
                        }
                    }
                });
    }

    private void showEditEventDialog(PeeEvent event) {
        PeeEventDetailsController controller = new PeeEventDetailsController(event.getWhen().toLocalDate(), event.getType());
        FxmlParent fp = new FxmlParent(new PeeEventFxml(), controller);
        Alert alert = new Alert(Alert.AlertType.NONE);

        alert.setTitle("Pee Calendar");

        alert.setDialogPane((DialogPane) fp.get());

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);

        alert.getButtonTypes().setAll(saveBtn, ButtonType.CANCEL);

        alert.showAndWait()
                .filter(btn -> btn == saveBtn)
                .ifPresent(btn -> {
                    try {
                        LocalDateTime time = controller.getEventDate().atTime(LocalTime.now());
                        PeeEvent.Type type = controller.getType();

                        eventsDao.updateTime(event, time);
                        eventsDao.updateType(event, type);

                        this.refreshEventEntries();
                    } catch (IOException ex) {
                        throw new RuntimeException("A database error occured");
                    }
                });
    }

    private void showRemoveEventDialog(PeeEvent event) {
        Alert alert = new Alert(Alert.AlertType.WARNING);

        alert.setTitle("Pee Calendar");
        alert.setHeaderText("Delete pee event permanently?");
        alert.setContentText("This action cannot be undone");

        ButtonType yesBtn = new ButtonType("Yes, Delete", ButtonBar.ButtonData.NO);
        ButtonType noBtn = new ButtonType("No", ButtonBar.ButtonData.OK_DONE);

        alert.getButtonTypes().setAll(yesBtn, noBtn);

        alert.showAndWait()
                .filter(btn -> btn == yesBtn)
                .ifPresent(btn -> {
                    try {
                        eventsDao.removeEvent(event);

                        this.refreshEventEntries();
                    } catch (IOException ex) {
                        throw new RuntimeException("A database error occured");
                    }
                });
    }

    private void refreshMonthViews() {
        PeeCycle cycle = cyclesListView.getSelectionModel().getSelectedItem();

        if (cycle != null) {
            LocalDate startDate = cycle.getStartDate();
            LocalDate endDate = cycle.getEndDate();

            List<LocalDate> dates = startDate.datesUntil(endDate, Period.ofMonths(1)).collect(Collectors.toList());
            dates.add(endDate);

            YearMonthView[] views = new YearMonthView[dates.size()];

            for (int i = 0; i < dates.size(); i++) {
                YearMonthView ymv = new YearMonthView();

                initMonthView(ymv);

                ymv.setDate(dates.get(i));
                ymv.getCalendarSources().setAll(calendarSource);

                ymv.getSelectedDates().addListener((SetChangeListener.Change<? extends LocalDate> change) -> {
                    List<Entry<?>> allEntries = new ArrayList<>();

                    for (PeeEvent.Type type : PeeEvent.Type.values()) {
                        allEntries.addAll(calendar.findEntries(type.toString()));
                    }

                    List<LocalDate> allEntryDates = allEntries.stream()
                            .map(Entry::getStartDate)
                            .collect(Collectors.toList());

                    ymv.getSelectedDates()
                            .stream()
                            .findFirst()
                            .ifPresent(ld -> {
                                editEventButton.setDisable(!allEntryDates.contains(ld));
                                removeEventButton.setDisable(!allEntryDates.contains(ld));

                                allEntries.stream()
                                        .filter(e -> e.getStartDate().equals(ld))
                                        .map(e -> PeeEvent.class.cast(e.getUserObject()))
                                        .findFirst()
                                        .ifPresent(pe -> {
                                            editEventButton.setOnAction(eh -> showEditEventDialog(pe));
                                            removeEventButton.setOnAction(eh -> showRemoveEventDialog(pe));
                                        });
                            });
                });

                views[i] = ymv;
            }

            cycleEventsPane.setCenter(new FlowPane(views));
        }
    }
    private static final Logger logger = Logger.getLogger(PatientViewController.class.getName());

    private void refreshEventEntries() {
        calendar.clear();

        PeeCycle cycle = cyclesListView.getSelectionModel().getSelectedItem();

        if (cycle != null) {
            try {
                Iterable<PeeEvent> events = eventsDao.getAllEvents();

                for (PeeEvent event : events) {
                    if (event.getCycle().equals(cycle)) {

                        switch (event.getType()) {
                            case DRY_NIGHT:
                                Entry<PeeEvent> dryEntry = new Entry(event.getType().toString());
                                dryEntry.setInterval(event.getWhen());
                                dryEntry.setFullDay(true);
                                dryEntry.setUserObject(event);
                                dryEntry.setCalendar(calendar);
                                break;
                            case FEW_DROPS:
                                for (int i = 0; i < 3; i++) {
                                    Entry<PeeEvent> dropsEntry = new Entry(event.getType().toString());
                                    dropsEntry.setInterval(event.getWhen());
                                    dropsEntry.setFullDay(true);
                                    dropsEntry.setUserObject(event);
                                    dropsEntry.setCalendar(calendar);
                                }
                                break;
                            case WET_NIGHT:
                                for (int i = 0; i < 5; i++) {
                                    Entry<PeeEvent> wetEntry = new Entry(event.getType().toString());
                                    wetEntry.setInterval(event.getWhen());
                                    wetEntry.setFullDay(true);
                                    wetEntry.setUserObject(event);
                                    wetEntry.setCalendar(calendar);
                                }
                                break;
                            default:
                                throw new AssertionError();
                        }
                    }
                }
            } catch (IOException ex) {
                throw new RuntimeException("A database error occured");
            }
        }
    }

    private void initMonthView(YearMonthView view) {
        view.setClickBehaviour(YearMonthView.ClickBehaviour.PERFORM_SELECTION);
        view.setShowYearArrows(false);
        view.setShowMonthArrows(false);
        view.setShowToday(false);
        view.setShowTodayButton(false);
        view.setEnableHyperlinks(false);
        view.setSelectionMode(SelectionMode.SINGLE);
        view.setShowUsageColors(true);

        view.setPrefWidth(300);
        view.setPrefHeight(250);
    }

    private class CustomCell extends ListCell<PeeCycle> {

        @Override
        protected void updateItem(PeeCycle item, boolean empty) {
            super.updateItem(item, empty);

            if (item == null || empty) {
                super.setText(null);
            } else {
                DateStringConverter dsc = new DateStringConverter();
                String start = dsc.toString(item.getStartDate());
                String end = dsc.toString(item.getEndDate());

                super.setText("%d. %s to %s".formatted(super.getIndex() + 1, start, end));
            }
        }
    }

}
