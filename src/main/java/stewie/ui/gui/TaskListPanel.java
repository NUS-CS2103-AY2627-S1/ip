package stewie.ui.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import stewie.model.TaskList;
import stewie.storage.StorageException;

/**
 * Displays grouped tasks and manages the lifetime of their completion delays.
 */
final class TaskListPanel extends VBox {
    private final TaskList taskList;
    private final Runnable onTasksChanged;
    private final Consumer<StorageException> onStorageError;
    private final VBox listTaskContainer = new VBox(8);
    // Each completed task keeps its own delay until navigation cancels the pending callbacks.
    private final Map<Integer, PauseTransition> completionDelays = new HashMap<>();

    /**
     * Creates a list view with callbacks for summary updates and visible save errors.
     */
    TaskListPanel(TaskList taskList, Runnable onTasksChanged, Consumer<StorageException> onStorageError) {
        this.taskList = taskList;
        this.onTasksChanged = onTasksChanged;
        this.onStorageError = onStorageError;
        initializeLayout();
    }

    /**
     * Creates the My List panel with a heading and a scrollable task container.
     */
    private void initializeLayout() {
        getStyleClass().add("chat-panel");

        HBox header = new HBox(14);
        header.getStyleClass().add("chat-header");
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("My List");
        title.getStyleClass().add("chat-title");

        header.getChildren().add(title);
        listTaskContainer.getStyleClass().add("task-group");
        listTaskContainer.setPadding(new Insets(28, 48, 28, 48));

        ScrollPane listScroll = new ScrollPane(listTaskContainer);
        listScroll.getStyleClass().add("conversation-scroll");
        listScroll.setFitToWidth(true);
        listScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        listScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        VBox.setVgrow(listScroll, Priority.ALWAYS);

        getChildren().addAll(header, listScroll);
    }

    /**
     * Stops pending completion timers before discarding their callbacks.
     */
    void cancelCompletionDelays() {
        completionDelays.values().forEach(PauseTransition::stop);
        completionDelays.clear();
    }

    /**
     * Displays unfinished and completed tasks while preserving their original task numbers.
     */
    void refresh() {
        listTaskContainer.getChildren().clear();
        VBox unfinishedTasks = new VBox(8);
        VBox completedTasks = new VBox(8);
        String[] tasks = taskList.produceTaskList();
        for (int index = 0; index < tasks.length; index++) {
            boolean isDone = TaskCardFactory.isDone(tasks[index]);
            boolean isPending = completionDelays.containsKey(index);
            HBox card = createListTaskCard(index, tasks[index], isDone, isPending);
            if (isDone && !isPending) {
                completedTasks.getChildren().add(card);
            } else {
                unfinishedTasks.getChildren().add(card);
            }
        }

        addListSection("Unfinished", unfinishedTasks,
                "No unfinished tasks. Remarkable. Add a task in Chat when ambition returns.");
        addListSection("Completed", completedTasks, "No completed tasks yet. I await your first triumph.");
    }

    /**
     * Creates a list card whose checkbox completes or reopens the original task.
     *
     * @param index The zero-based task index.
     * @param taskText The formatted task description.
     * @param isDone Whether the task is complete.
     * @param isPending Whether its completion delay is still running.
     * @return The task card with its status control.
     */
    private HBox createListTaskCard(int index, String taskText, boolean isDone, boolean isPending) {
        HBox card = TaskCardFactory.create(index + 1, taskText);
        card.getChildren().add(createListStatusCheckbox(index, isDone, isPending));
        if (isPending) {
            card.setOpacity(0.4);
        }
        return card;
    }

    /**
     * Creates a checkbox tied to the task revision shown in My List.
     */
    private CheckBox createListStatusCheckbox(int index, boolean isDone, boolean isPending) {
        long cardRevision = taskList.getRevision();
        CheckBox statusBox = new CheckBox();
        statusBox.getStyleClass().addAll("task-check", "list-complete-check");
        statusBox.setSelected(isDone);
        statusBox.setDisable(isPending);
        statusBox.setAccessibleText("Mark task " + (index + 1) + (isDone ? " as undone" : " as done"));
        statusBox.setOnAction(event -> handleListStatusChange(index, isDone, cardRevision, statusBox));
        return statusBox;
    }

    /**
     * Applies a current list-card action and restores its checkbox if saving fails.
     */
    private void handleListStatusChange(int index, boolean isDone, long cardRevision, CheckBox statusBox) {
        if (cardRevision != taskList.getRevision()) {
            statusBox.setSelected(isDone);
            refresh();
            return;
        }
        try {
            changeListTaskStatus(index, isDone);
        } catch (StorageException exception) {
            statusBox.setSelected(isDone);
            onStorageError.accept(exception);
        }
        onTasksChanged.run();
        refresh();
    }

    /**
     * Reopens a task immediately or completes it with a delay before regrouping its card.
     */
    private void changeListTaskStatus(int index, boolean isDone) {
        if (isDone) {
            taskList.markAsUndone(index);
        } else {
            taskList.markAsDone(index);
            scheduleCompletionRefresh(index);
        }
    }

    /**
     * Gives each completed task its own three-second display delay.
     */
    private void scheduleCompletionRefresh(int index) {
        PauseTransition removalDelay = new PauseTransition(Duration.seconds(3));
        completionDelays.put(index, removalDelay);
        removalDelay.setOnFinished(event -> {
            completionDelays.remove(index);
            refresh();
        });
        removalDelay.play();
    }

    /**
     * Adds a titled task section, showing a message when it has no cards.
     *
     * @param title The section heading.
     * @param cards The task cards in this section.
     * @param emptyText The message for an empty section.
     */
    private void addListSection(String title, VBox cards, String emptyText) {
        Label heading = new Label(title);
        heading.getStyleClass().add("chat-title");
        if (cards.getChildren().isEmpty()) {
            Label emptyMessage = new Label(emptyText);
            emptyMessage.setWrapText(true);
            emptyMessage.getStyleClass().add("muted-label");
            cards.getChildren().add(emptyMessage);
        }
        listTaskContainer.getChildren().addAll(heading, cards);
    }
}
