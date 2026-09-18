package herta.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import herta.exception.HertaException;
import herta.parser.ArchiveRange;
import herta.parser.ArchiveSelection;
import herta.storage.Storage;
import herta.storage.TaskRepository;
import herta.task.Task;
import herta.task.TaskList;
import herta.ui.UiOutput;

/**
 * Represents the command that moves completed active tasks to the archive.
 */
public class ArchiveCommand extends Command {
    private static final String ARCHIVE_FAILURE_PREFIX = "Failed to archive tasks: ";
    private static final String EMPTY_ACTIVE_LIST_MESSAGE =
            "No active tasks. There is nothing here to archive.";
    private static final String NO_COMPLETED_TASKS_MESSAGE =
            "Nothing is ready for archiving. Complete a task first.";
    private static final String INCOMPLETE_TASK_ERROR =
            "That task is still unfinished. Complete it before archiving.";
    private static final String ARCHIVE_SUCCESS_MESSAGE =
            "There. I've archived %d completed %s:";
    private static final String ACTIVE_TASK_COUNT_MESSAGE =
            "The active list is down to %d %s. Much tidier.";
    private static final String INVALID_ACTIVE_TASK_ERROR =
            "That number points to nothing on the active list. Check again.";
    private final ArchiveSelection selection;

    /** Stores the collections and display items produced by an archive operation. */
    private record ArchiveResult(TaskList activeTasks, TaskList archivedTasks,
                                 List<Task> archivedTasksForDisplay) {
    }

    /**
     * Creates an archive command for a validated selection.
     *
     * @param selection the task numbers or ranges to archive.
     */
    public ArchiveCommand(ArchiveSelection selection) {
        this.selection = Objects.requireNonNull(selection, "An archive selection is required.");
    }

    /**
     * Executes the command using an active and archive repository.
     *
     * @param repository the repository containing both task collections.
     * @param ui the output interface used to display responses.
     * @throws HertaException if selection validation fails or persistence fails.
     */
    @Override
    public void execute(TaskRepository repository, UiOutput ui) throws HertaException {
        TaskList activeTasks = repository.getActiveTasks();
        Set<Integer> selectedIndices = getSelectedIndices(activeTasks);
        if (isNoOpArchiveSelection(activeTasks, selectedIndices)) {
            showNoTasksMessage(activeTasks, ui);
            return;
        }

        validateCompletedTasks(activeTasks, selectedIndices);
        ArchiveResult result = archiveSelectedTasks(activeTasks, repository.getArchivedTasks(),
                selectedIndices);
        repository.saveActiveAndArchivedTasks(result.activeTasks(), result.archivedTasks(),
                ARCHIVE_FAILURE_PREFIX);
        repository.replaceActiveAndArchivedTasks(result.activeTasks(), result.archivedTasks());
        showArchiveResult(result, selectedIndices.size(), ui);
    }

    /**
     * Adapts legacy direct command execution to the two-collection repository.
     *
     * @param tasks the active tasks.
     * @param ui the output interface used to display responses.
     * @param storage storage for active tasks.
     * @throws HertaException if the archive cannot be loaded or saved.
     */
    @Override
    public void execute(TaskList tasks, UiOutput ui, Storage storage) throws HertaException {
        execute(TaskRepository.createWithActiveTasks(tasks, storage), ui);
    }

    /**
     * Checks whether an all-task selection would move no tasks.
     *
     * @param activeTasks the active task collection.
     * @param selectedIndices the resolved task indices.
     * @return {@code true} when the selection should stop without archiving.
     */
    private boolean isNoOpArchiveSelection(TaskList activeTasks, Set<Integer> selectedIndices) {
        boolean hasNoActiveTasks = activeTasks.size() == 0;
        boolean hasNoSelectedTasks = selectedIndices.isEmpty();
        return selection.isAllSelected() && (hasNoActiveTasks || hasNoSelectedTasks);
    }

    /**
     * Displays a no-op explanation for an all-task selection that has nothing to archive.
     *
     * @param activeTasks the active task collection.
     * @param ui the output interface.
     */
    private void showNoTasksMessage(TaskList activeTasks, UiOutput ui) {
        if (activeTasks.size() == 0) {
            ui.showMessage(EMPTY_ACTIVE_LIST_MESSAGE);
        } else {
            ui.showMessage(NO_COMPLETED_TASKS_MESSAGE);
        }
    }

    /**
     * Ensures every explicitly selected task is complete before archiving.
     *
     * @param activeTasks the active task collection.
     * @param selectedIndices the resolved task indices.
     * @throws HertaException if an explicitly selected task is incomplete.
     */
    private void validateCompletedTasks(TaskList activeTasks, Set<Integer> selectedIndices)
            throws HertaException {
        if (selection.isAllSelected()) {
            return;
        }
        for (int index : selectedIndices) {
            if (!activeTasks.get(index).isCompleted()) {
                throw new HertaException(INCOMPLETE_TASK_ERROR);
            }
        }
    }

    /**
     * Moves selected tasks into new active and archived collections.
     *
     * @param activeTasks the active task collection.
     * @param archivedTasks the existing archived task collection.
     * @param selectedIndices the resolved task indices.
     * @return the collections and tasks to display after archiving.
     */
    private ArchiveResult archiveSelectedTasks(TaskList activeTasks, TaskList archivedTasks,
                                               Set<Integer> selectedIndices) {
        TaskList updatedActiveTasks = new TaskList();
        TaskList updatedArchivedTasks = new TaskList(archivedTasks.asUnmodifiableList());
        List<Task> archivedTasksForDisplay = new ArrayList<>();
        for (int index = 0; index < activeTasks.size(); index++) {
            Task task = activeTasks.get(index);
            if (selectedIndices.contains(index)) {
                updatedArchivedTasks.add(task);
                archivedTasksForDisplay.add(task);
            } else {
                updatedActiveTasks.add(task);
            }
        }
        return new ArchiveResult(updatedActiveTasks, updatedArchivedTasks, archivedTasksForDisplay);
    }

    /**
     * Displays the result of a successful archive operation.
     *
     * @param result the collections and tasks produced by the operation.
     * @param archivedCount the number of tasks moved.
     * @param ui the output interface.
     */
    private void showArchiveResult(ArchiveResult result, int archivedCount, UiOutput ui) {
        ui.showMessage(ARCHIVE_SUCCESS_MESSAGE.formatted(
                archivedCount, UiOutput.getTaskNoun(archivedCount)));
        for (Task task : result.archivedTasksForDisplay()) {
            ui.showTask(task);
        }
        int activeTaskCount = result.activeTasks().size();
        ui.showMessage(ACTIVE_TASK_COUNT_MESSAGE.formatted(
                activeTaskCount, UiOutput.getTaskNoun(activeTaskCount)));
    }

    /**
     * Resolves selectors into unique zero-based active-list indices.
     *
     * @param activeTasks the active tasks against which bounds are checked.
     * @return selected indices in active-list order.
     * @throws HertaException if any selected task number is out of bounds.
     */
    private Set<Integer> getSelectedIndices(TaskList activeTasks) throws HertaException {
        Set<Integer> selectedIndices = new TreeSet<>();
        if (selection.isAllSelected()) {
            selectedIndices.addAll(activeTasks.findMatchingIndices(Task::isCompleted));
            return selectedIndices;
        }

        for (ArchiveRange range : selection.getRanges()) {
            if (range.start() <= 0 || range.end() > activeTasks.size()) {
                throw new HertaException(INVALID_ACTIVE_TASK_ERROR);
            }
        }
        for (ArchiveRange range : selection.getRanges()) {
            for (int taskNumber = range.start(); ; taskNumber++) {
                selectedIndices.add(taskNumber - 1);
                if (taskNumber == range.end()) {
                    break;
                }
            }
        }
        return selectedIndices;
    }
}
