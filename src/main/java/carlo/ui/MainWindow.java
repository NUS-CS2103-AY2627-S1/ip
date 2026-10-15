package carlo.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import carlo.Carlo;
import carlo.exception.CarloException;
import carlo.persona.CarloMessage;
import carlo.persona.CarloVoice;
import carlo.task.Task;
import carlo.task.TaskSchedule;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * The Carlo desktop app. It places {@link CarloSidebar} on the left and
 * {@link TaskListPane} on the right, and connects them to the task list
 * ({@link Carlo}) and to what Carlo says ({@link CarloVoice}).
 *
 * <p>Every time a task changes, the window calls {@link #refresh()} to
 * redraw the list, and Carlo reacts in his speech bubble. A clock also
 * ticks once a minute to keep "due in..." times current and to remind
 * the user about anything starting soon.
 */
public class MainWindow extends Application implements TaskListPane.TaskActions {
    private static final String STYLESHEET = "/css/carlo.css";
    private static final String ICON_PATH = "/images/carlo.png";
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 680;

    private final CarloVoice voice = new CarloVoice();
    /**
     * Tasks Carlo has already reminded the user about, so each reminder is
     * only given once. Tasks don't define their own {@code equals}, so this
     * set compares them by identity (the same object), which is what we want.
     */
    private final Set<Task> remindedTasks = Collections.newSetFromMap(new IdentityHashMap<>());

    private Carlo carlo;
    private CarloSidebar sidebar;
    private TaskListPane taskList;

    /**
     * Builds and shows the main window.
     *
     * @param stage the window provided by JavaFX
     */
    @Override
    public void start(Stage stage) {
        carlo = new Carlo();
        sidebar = new CarloSidebar(this::handleCommand, this::openAddTaskForm, this::undo);
        taskList = new TaskListPane(this, this::refresh);

        BorderPane root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(taskList);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add(stylesheetUrl());
        // Ctrl+N (Cmd+N on a Mac) opens the add-task form.
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN), this::openAddTaskForm);
        // Ctrl+Z undoes the last change. (Inside a text box, Ctrl+Z undoes typing instead, as usual.)
        scene.getAccelerators().put(
                new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN), this::undo);

        refresh();
        LocalDateTime now = LocalDateTime.now();
        sidebar.say(voice.greet(carlo.getTasks(), now));
        // Anything already coming up is covered by the greeting, so don't remind about it again.
        skipCurrentReminders(now);
        startReminderClock();

        stage.setTitle("Carlo");
        stage.getIcons().add(new Image(getClass().getResourceAsStream(ICON_PATH)));
        stage.setMinWidth(760);
        stage.setMinHeight(520);
        stage.setScene(scene);
        stage.show();
        sidebar.focusCommandBar();
    }

    /**
     * Marks a task as done or not done, and has Carlo react.
     *
     * @param task the task to update
     * @param isDone whether the task should now be done
     */
    @Override
    public void setDone(Task task, boolean isDone) {
        try {
            int index = carlo.getTasks().indexOf(task);
            if (isDone) {
                carlo.markTask(index);
                long openCount = carlo.getTasks().stream().filter(t -> !t.isDone()).count();
                sidebar.say(voice.taskCompleted(task, openCount), true);
            } else {
                carlo.unmarkTask(index);
                sidebar.say(voice.taskReopened(task), true);
            }
        } catch (CarloException e) {
            sidebar.say(voice.problem(e.getMessage()));
        }
        refresh();
    }

    /**
     * Deletes a task, and has Carlo react.
     *
     * @param task the task to delete
     */
    @Override
    public void delete(Task task) {
        try {
            carlo.deleteTask(carlo.getTasks().indexOf(task));
            remindedTasks.remove(task);
            sidebar.say(voice.taskDeleted(task), true);
        } catch (CarloException e) {
            sidebar.say(voice.problem(e.getMessage()));
        }
        refresh();
    }

    /**
     * Opens a task in the form so the user can change it, then saves the
     * changes in the same position in the list.
     *
     * @param task the task to edit
     */
    @Override
    public void edit(Task task) {
        TaskFormDialog.forEditing(stylesheetUrl(), task).showAndWait().ifPresent(command -> {
            try {
                Task edited = carlo.editTask(carlo.getTasks().indexOf(task), command);
                remindedTasks.remove(task);
                sidebar.say(voice.taskEdited(edited, LocalDateTime.now()), true);
            } catch (CarloException e) {
                sidebar.say(voice.problem(e.getMessage()));
            }
            refresh();
        });
    }

    /**
     * Undoes the most recent change, and offers to undo the one before it
     * too, if there is one.
     */
    private void undo() {
        try {
            String undoneAction = carlo.undo();
            // Undo rebuilds every task as a new object, so the reminder record starts fresh.
            remindedTasks.clear();
            skipCurrentReminders(LocalDateTime.now());
            sidebar.say(voice.undone(undoneAction), carlo.canUndo());
        } catch (CarloException e) {
            sidebar.say(voice.problem(e.getMessage()));
        }
        refresh();
    }

    /**
     * Records every task that is already coming up as "reminded", so the
     * reminder clock only speaks up about things that become due later.
     */
    private void skipCurrentReminders(LocalDateTime now) {
        carlo.getTasks().stream()
                .filter(task -> TaskSchedule.isComingUp(task, now, CarloVoice.REMINDER_WINDOW))
                .forEach(remindedTasks::add);
    }

    /**
     * Adds the task described by a command typed into the command bar.
     *
     * @param command the text the user typed
     * @return {@code true} if a task was added, so the command bar can be cleared
     */
    private boolean handleCommand(String command) {
        if (command == null || command.isBlank()) {
            return false;
        }
        if (command.trim().equalsIgnoreCase("help")) {
            sidebar.say(voice.help());
            return true;
        }
        try {
            Task task = carlo.addTaskFromCommand(command);
            sidebar.say(voice.taskAdded(task, LocalDateTime.now()), true);
            refresh();
            return true;
        } catch (CarloException e) {
            sidebar.say(voice.problem(e.getMessage()));
            return false;
        }
    }

    /**
     * Opens the add-task form, then adds whatever the user filled in.
     */
    private void openAddTaskForm() {
        TaskFormDialog.forAdding(stylesheetUrl()).showAndWait().ifPresent(this::handleCommand);
    }

    /**
     * Redraws the task list (applying the search and date filters) and
     * updates the stats in the sidebar.
     */
    private void refresh() {
        LocalDateTime now = LocalDateTime.now();
        List<Task> allTasks = carlo.getTasks();
        List<Task> visible = new ArrayList<>(allTasks);

        String keyword = taskList.getSearchText();
        boolean isFiltered = false;
        if (keyword != null && !keyword.isBlank()) {
            try {
                visible.retainAll(carlo.findTasks(keyword));
                isFiltered = true;
            } catch (CarloException e) {
                // findTasks only rejects blank keywords, which were already skipped above
            }
        }
        LocalDate date = taskList.getDateFilter();
        if (date != null) {
            visible.retainAll(carlo.getTasksOnDate(date));
            isFiltered = true;
        }

        String emptyMessage = isFiltered
                ? "No tasks match that search."
                : "Your list is empty. Tell Carlo what's coming up!";
        taskList.showTasks(visible, now, emptyMessage);
        sidebar.updateStats(allTasks, now);
    }

    /**
     * Starts a clock that ticks once a minute. Each tick keeps relative
     * times like "due in 2 hours" up to date, and has Carlo remind the user
     * about anything starting or due within {@link CarloVoice#REMINDER_WINDOW}.
     */
    private void startReminderClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.minutes(1), event -> {
            LocalDateTime now = LocalDateTime.now();
            refresh();
            for (Task task : carlo.getTasks()) {
                // Set.add returns false if the task was already reminded about.
                if (TaskSchedule.isComingUp(task, now, CarloVoice.REMINDER_WINDOW) && remindedTasks.add(task)) {
                    CarloMessage reminder = voice.reminder(task, now);
                    sidebar.say(reminder);
                }
            }
        }));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    private String stylesheetUrl() {
        return getClass().getResource(STYLESHEET).toExternalForm();
    }
}
