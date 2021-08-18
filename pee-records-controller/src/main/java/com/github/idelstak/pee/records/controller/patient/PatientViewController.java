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
import com.github.idelstak.pee.records.model.api.Name;
import com.github.idelstak.pee.records.model.spi.Patient;
import com.github.idelstak.pee.records.model.spi.PeeCycle;
import com.github.idelstak.pee.records.model.spi.core.Entity;
import com.github.idelstak.pee.records.view.api.FxmlParent;
import com.github.idelstak.pee.records.view.patient.CycleDetailsFxml;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
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

    public PatientViewController(DataSource dataSource, Patient patient) {
        this.cyclesDao = new MySqlPeeCyclesDao(dataSource);
        this.dataSource = dataSource;
        this.patient = patient;
    }

    @FXML
    public void initialize() {
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

        Calendar calendar = new Calendar();
        CalendarSource calendarSource = new CalendarSource();
        calendarSource.getCalendars().setAll(calendar);

        //Green usage color
        Entry<?> dryEntry = new Entry();
        dryEntry.setInterval(LocalDate.of(2021, Month.AUGUST, 24));
        dryEntry.setFullDay(true);
        dryEntry.setCalendar(calendar);

        //Yellow usage color
        for (int i = 0; i < 3; i++) {
            Entry<?> dropsEntry = new Entry();
            dropsEntry.setInterval(LocalDate.of(2021, Month.AUGUST, 28));
            dropsEntry.setFullDay(true);
            dropsEntry.setCalendar(calendar);
        }

        //Red usage color
        for (int i = 0; i < 5; i++) {
            Entry<?> wetEntry = new Entry();
            wetEntry.setInterval(LocalDate.of(2021, Month.AUGUST, 25));
            wetEntry.setFullDay(true);
            wetEntry.setCalendar(calendar);
        }

        cyclesListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((o, ov, nv) -> {
                    noCycleSelectedLabel.setVisible(nv == null);

                    if (nv != null) {
                        LocalDate startDate = nv.getStartDate();
                        LocalDate endDate = nv.getEndDate();

                        List<LocalDate> dates = startDate.datesUntil(endDate, Period.ofMonths(1)).collect(Collectors.toList());
                        dates.add(endDate);

                        YearMonthView[] views = new YearMonthView[dates.size()];

                        for (int i = 0; i < dates.size(); i++) {
                            YearMonthView ymv = new YearMonthView();

                            initMonthView(ymv);

                            ymv.setDate(dates.get(i));
                            ymv.getCalendarSources().setAll(calendarSource);

                            views[i] = ymv;
                        }

                        cycleEventsPane.setCenter(new FlowPane(views));
                    }
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
                .stream()
                .filter(btn -> btn == controller.getSaveButton())
                .findFirst()
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
                .stream()
                .filter(btn -> btn == controller.getSaveButton())
                .findFirst()
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
                .stream()
                .filter(btn -> btn == yesBtn)
                .findFirst()
                .ifPresent(btn -> {
                    try {
                        cyclesDao.removeCycle(selectedCycle);

                        refreshCyclesList();

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

    private void initMonthView(YearMonthView view) {
        view.setClickBehaviour(YearMonthView.ClickBehaviour.PERFORM_SELECTION);
        view.setShowYearArrows(false);
        view.setShowMonthArrows(false);
        view.setShowToday(false);
        view.setShowTodayButton(false);
        view.setShowUsageColors(true);

        view.setPrefWidth(300);
        view.setPrefHeight(250);
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
