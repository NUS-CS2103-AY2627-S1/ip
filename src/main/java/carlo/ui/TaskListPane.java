package carlo.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import carlo.task.Task;
import carlo.task.TaskGroup;
import carlo.task.TaskSchedule;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * The main area of the window: a search bar on top, and below it the
 * user's tasks grouped by urgency ({@link TaskGroup}), most urgent first.
 * The "Done" group starts collapsed so finished tasks don't crowd the view.
 */
public class TaskListPane extends BorderPane {
    /**
     * What should happen when the user interacts with a task. The main
     * window provides these, since it owns the task list and Carlo's voice.
     * Passing an interface in (instead of the whole main window) keeps this
     * class independent of how the actions are carried out.
     */
    public interface TaskActions {
        /**
         * Marks a task as done or not done.
         *
         * @param task the task to update
         * @param isDone whether the task should now be done
         */
        void setDone(Task task, boolean isDone);

        /**
         * Opens a task for editing.
         *
         * @param task the task to edit
         */
        void edit(Task task);

        /**
         * Deletes a task.
         *
         * @param task the task to delete
         */
        void delete(Task task);
    }

    private final TaskActions actions;
    private final TextField searchField = new TextField();
    private final DatePicker datePicker = new DatePicker();
    private final VBox groupsBox = new VBox();

    private boolean isDoneExpanded = false;
    /** The last tasks shown, kept so the Done group can be re-drawn when it is expanded or collapsed. */
    private List<Task> lastTasks = List.of();
    private LocalDateTime lastNow = LocalDateTime.now();
    private String lastEmptyMessage = "";

    /**
     * Creates the task list.
     *
     * @param actions what to do when the user ticks or deletes a task
     * @param onFilterChanged called whenever the search text or date filter changes
     */
    public TaskListPane(TaskActions actions, Runnable onFilterChanged) {
        this.actions = actions;
        getStyleClass().add("task-pane");

        setTop(createTopBar(onFilterChanged));

        groupsBox.getStyleClass().add("groups");
        ScrollPane scrollPane = new ScrollPane(groupsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setCenter(scrollPane);
    }

    /**
     * Returns the keyword typed into the search box.
     *
     * @return the search keyword, which may be blank
     */
    public String getSearchText() {
        return searchField.getText();
    }

    /**
     * Returns the date chosen in the date filter.
     *
     * @return the chosen date, or {@code null} if no date filter is set
     */
    public LocalDate getDateFilter() {
        return datePicker.getValue();
    }

    /**
     * Shows the given tasks, grouped by urgency.
     *
     * @param tasks the tasks to show (already filtered by search and date)
     * @param now the current date and time
     * @param emptyMessage what to show instead if {@code tasks} is empty
     */
    public void showTasks(List<Task> tasks, LocalDateTime now, String emptyMessage) {
        lastTasks = tasks;
        lastNow = now;
        lastEmptyMessage = emptyMessage;
        groupsBox.getChildren().clear();

        if (tasks.isEmpty()) {
            Label empty = new Label(emptyMessage);
            empty.getStyleClass().add("empty-message");
            empty.setWrapText(true);
            groupsBox.getChildren().add(empty);
            return;
        }

        // An EnumMap keeps its keys in the order the enum declares them,
        // so the groups come out most urgent first.
        Map<TaskGroup, List<Task>> grouped = new EnumMap<>(TaskGroup.class);
        for (Task task : tasks) {
            grouped.computeIfAbsent(TaskSchedule.groupOf(task, now), group -> new ArrayList<>()).add(task);
        }

        for (Map.Entry<TaskGroup, List<Task>> entry : grouped.entrySet()) {
            TaskGroup group = entry.getKey();
            List<Task> groupTasks = entry.getValue();
            if (group != TaskGroup.DONE) {
                groupTasks.sort(TaskSchedule.bySoonestFirst());
            }
            groupsBox.getChildren().add(createGroup(group, groupTasks, now));
        }
    }

    private HBox createTopBar(Runnable onFilterChanged) {
        Label title = new Label("Your tasks");
        title.getStyleClass().add("pane-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        searchField.setPromptText("Search");
        searchField.setPrefWidth(180);
        searchField.textProperty().addListener((observable, oldText, newText) -> onFilterChanged.run());

        datePicker.setPromptText("Any date");
        datePicker.setPrefWidth(130);
        datePicker.valueProperty().addListener((observable, oldDate, newDate) -> onFilterChanged.run());

        Button clearButton = new Button("Clear");
        clearButton.setOnAction(event -> {
            searchField.clear();
            datePicker.setValue(null);
        });

        HBox topBar = new HBox(8, title, spacer, searchField, datePicker, clearButton);
        topBar.getStyleClass().add("top-bar");
        topBar.setAlignment(Pos.CENTER_LEFT);
        return topBar;
    }

    private VBox createGroup(TaskGroup group, List<Task> groupTasks, LocalDateTime now) {
        String groupClass = "group-" + group.name().toLowerCase().replace('_', '-');
        VBox section = new VBox(6);
        section.getStyleClass().addAll("group", groupClass);

        boolean isCollapsible = group == TaskGroup.DONE;
        String headerText = group.getLabel() + " (" + groupTasks.size() + ")";
        if (isCollapsible) {
            headerText += isDoneExpanded ? "  ▾" : "  ▸";
        }

        Label header = new Label(headerText);
        header.getStyleClass().add("group-header");
        section.getChildren().add(header);

        if (isCollapsible) {
            header.getStyleClass().add("clickable");
            header.setOnMouseClicked(event -> {
                isDoneExpanded = !isDoneExpanded;
                showTasks(lastTasks, lastNow, lastEmptyMessage);
            });
            if (!isDoneExpanded) {
                return section;
            }
        }

        VBox rows = new VBox();
        rows.getStyleClass().add("group-box");
        for (Task task : groupTasks) {
            rows.getChildren().add(new TaskRow(task, group, now, actions));
        }
        section.getChildren().add(rows);
        return section;
    }
}
