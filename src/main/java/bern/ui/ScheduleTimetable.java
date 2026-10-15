package bern.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import bern.logic.DaySchedule;
import bern.logic.ScheduleEntry;
import bern.task.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;

/**
 * Displays a day's timed events in an FXML grid with shared time labels and separate overlap columns.
 */
public class ScheduleTimetable extends GridPane {
    private static final int MINUTES_PER_HOUR = 60;
    /**
     * Index of the first task column, after the time axis at column zero.
     */
    private static final int FIRST_TASK_COLUMN = 1;
    /**
     * Minimum height per minute before the FXML minimum and wrapped content are considered.
     */
    private static final double PIXELS_PER_MINUTE = 1.0;

    /**
     * Creates a timetable for the supplied day's events using their assigned overlap columns.
     * A day without timed events produces an empty grid without time labels or rows.
     *
     * @param schedule The day's timed events and their column assignments.
     * @param taskNumbers The original one-based task numbers; absent entries produce unnumbered cards.
     * @throws IllegalStateException If a required timetable or task card layout cannot be found or loaded.
     */
    public ScheduleTimetable(DaySchedule schedule, Map<Task, Integer> taskNumbers) {
        ScheduleViewLoader.load(this, "/view/ScheduleFrame.fxml");
        List<Integer> boundaries = getTimeBoundaries(schedule.getEntries());
        configureTimetable(schedule.getColumnCount(), boundaries);
        addTaskCards(schedule.getEntries(), boundaries, taskNumbers);
    }

    /**
     * Expands the FXML column and row defaults to match the day's overlaps and exact time boundaries.
     * Reuses the FXML hourly labels and allows rows to grow when task descriptions wrap.
     *
     * @param columnCount The number of task columns required for overlapping events.
     * @param boundaries The sorted, distinct row boundaries in minutes after midnight.
     * @throws IllegalStateException If a task column or row background cannot be loaded.
     */
    private void configureTimetable(int columnCount, List<Integer> boundaries) {
        try {
            configureColumns(columnCount);
            alignTimeLabels(boundaries);
            configureRows(boundaries);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load timetable columns or row backgrounds", e);
        }
    }

    /**
     * Adds task columns between the fixed time axis and the flexible background-only column.
     *
     * @param columnCount The total number of task columns required.
     * @throws IOException If an additional column's FXML cannot be loaded.
     */
    private void configureColumns(int columnCount) throws IOException {
        for (int taskColumn = 1; taskColumn < columnCount; taskColumn++) {
            ColumnConstraints constraints = FXMLLoader.load(
                    ScheduleTimetable.class.getResource("/view/ScheduleTaskColumn.fxml"));
            // Keep the flexible background-only column after all task columns.
            getColumnConstraints().add(taskColumn + FIRST_TASK_COLUMN, constraints);
        }
    }

    /**
     * Places the FXML hourly labels at their corresponding rows, removing hours outside the schedule.
     *
     * @param boundaries The sorted, distinct row boundaries in minutes after midnight.
     */
    private void alignTimeLabels(List<Integer> boundaries) {
        List<Label> timeLabels = getChildren().stream()
                .filter(Label.class::isInstance).map(Label.class::cast).toList();
        for (Label timeLabel : timeLabels) {
            // FXML row indices identify hours; task boundaries can insert extra rows between them.
            int minute = GridPane.getRowIndex(timeLabel) * MINUTES_PER_HOUR;
            int row = boundaries.indexOf(minute);
            if (row < 0) {
                getChildren().remove(timeLabel);
            } else {
                GridPane.setRowIndex(timeLabel, row);
            }
        }
    }

    /**
     * Creates rows with a duration-based minimum height and room for wrapped task descriptions.
     *
     * @param boundaries The sorted, distinct row boundaries in minutes after midnight.
     * @throws IOException If a row background's FXML cannot be loaded.
     */
    private void configureRows(List<Integer> boundaries) throws IOException {
        RowConstraints rowDefaults = getRowConstraints().getFirst();
        getRowConstraints().clear();
        int backgroundColumnCount = getColumnConstraints().size() - FIRST_TASK_COLUMN;
        for (int row = 0; row + 1 < boundaries.size(); row++) {
            int durationMinutes = boundaries.get(row + 1) - boundaries.get(row);
            double minimumHeight = Math.max(rowDefaults.getMinHeight(), durationMinutes * PIXELS_PER_MINUTE);
            getRowConstraints().add(new RowConstraints(minimumHeight,
                    rowDefaults.getPrefHeight(), rowDefaults.getMaxHeight()));
            Region background = FXMLLoader.load(
                    ScheduleTimetable.class.getResource("/view/ScheduleRowBackground.fxml"));
            add(background, FIRST_TASK_COLUMN, row, backgroundColumnCount, 1);
        }
    }

    /**
     * Adds task cards to the loaded grid between their start and end boundaries.
     *
     * @param entries The timed events and their assigned overlap columns.
     * @param boundaries The sorted row boundaries containing every entry's start and end times.
     * @param taskNumbers The original one-based task numbers; absent entries produce unnumbered cards.
     * @throws IllegalStateException If a task card layout cannot be found or loaded.
     */
    private void addTaskCards(List<ScheduleEntry> entries, List<Integer> boundaries, Map<Task, Integer> taskNumbers) {
        for (ScheduleEntry entry : entries) {
            int firstRow = boundaries.indexOf(entry.getStartMinute());
            int lastRow = boundaries.indexOf(entry.getEndMinute());
            ScheduleTaskCard card = new ScheduleTaskCard(entry.getTask(),
                    formatMinute(entry.getStartMinute()) + " - " + formatMinute(entry.getEndMinute()),
                    taskNumbers.get(entry.getTask()));
            card.getStyleClass().add("schedule-event");
            card.setUserData(entry);
            add(card, entry.getColumn() + FIRST_TASK_COLUMN, firstRow, 1, lastRow - firstRow);
        }
    }

    /**
     * Returns sorted event boundaries and hourly ticks covering the occupied part of the day.
     *
     * @param entries The timed events whose boundaries define the occupied range.
     * @return Distinct boundaries in minutes after midnight, or an empty list when there are no events.
     */
    private List<Integer> getTimeBoundaries(List<ScheduleEntry> entries) {
        if (entries.isEmpty()) {
            return List.of();
        }
        TreeSet<Integer> boundaries = new TreeSet<>();
        for (ScheduleEntry entry : entries) {
            boundaries.add(entry.getStartMinute());
            boundaries.add(entry.getEndMinute());
        }
        int firstHourMinute = boundaries.first() / MINUTES_PER_HOUR * MINUTES_PER_HOUR;
        int lastHourMinute = (boundaries.last() + MINUTES_PER_HOUR - 1) / MINUTES_PER_HOUR * MINUTES_PER_HOUR;
        for (int minute = firstHourMinute; minute <= lastHourMinute; minute += MINUTES_PER_HOUR) {
            boundaries.add(minute);
        }
        return new ArrayList<>(boundaries);
    }

    /**
     * Formats minutes after midnight, preserving 24:00 for the end of the day.
     *
     * @param minute The minute offset from midnight, from 0 through 1440 inclusive.
     * @return The time in zero-padded {@code HH:mm} format.
     */
    private String formatMinute(int minute) {
        return String.format("%02d:%02d", minute / MINUTES_PER_HOUR, minute % MINUTES_PER_HOUR);
    }
}
