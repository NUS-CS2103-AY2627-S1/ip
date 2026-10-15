package greenchonk.command;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import greenchonk.exception.StorageException;
import greenchonk.storage.Storage;
import greenchonk.task.Task;
import greenchonk.task.TaskList;
import greenchonk.task.TaskStatus;
import greenchonk.ui.Ui;

/**
 * Provides observable collaborators for isolated command tests.
 */
final class CommandTestSupport {
    private CommandTestSupport() {
    }

    static final class RecordingStorage extends Storage {
        private boolean shouldFail;
        private int saveCount;

        RecordingStorage() {
            super("unused/data.txt");
        }

        void failNextSave() {
            shouldFail = true;
        }

        int getSaveCount() {
            return saveCount;
        }

        @Override
        public void save(TaskList tasks) throws StorageException {
            saveCount++;
            if (shouldFail) {
                throw new StorageException("save failed");
            }
        }
    }

    static final class RecordingUi extends Ui {
        private Task addedTask;
        private Task deletedTask;
        private Task editedOriginalTask;
        private Task editedTask;
        private Task updatedTask;
        private TaskStatus updatedStatus;
        private int taskCount;
        private int remainingTaskCount;
        private boolean isGoodbyeShown;
        private boolean isHelpShown;
        private boolean isFindHeaderShown;
        private boolean isNoMatchingTasksShown;
        private TaskList listedTasks;
        private LocalDate scheduleDate;
        private LocalDate emptyScheduleDate;
        private final List<Integer> shownTaskNumbers = new ArrayList<>();
        private final List<Task> shownTasks = new ArrayList<>();

        Task getAddedTask() {
            return addedTask;
        }

        Task getDeletedTask() {
            return deletedTask;
        }

        Task getEditedOriginalTask() {
            return editedOriginalTask;
        }

        Task getEditedTask() {
            return editedTask;
        }

        Task getUpdatedTask() {
            return updatedTask;
        }

        TaskStatus getUpdatedStatus() {
            return updatedStatus;
        }

        int getTaskCount() {
            return taskCount;
        }

        int getRemainingTaskCount() {
            return remainingTaskCount;
        }

        boolean isGoodbyeShown() {
            return isGoodbyeShown;
        }

        boolean isHelpShown() {
            return isHelpShown;
        }

        boolean isFindHeaderShown() {
            return isFindHeaderShown;
        }

        boolean isNoMatchingTasksShown() {
            return isNoMatchingTasksShown;
        }

        TaskList getListedTasks() {
            return listedTasks;
        }

        LocalDate getScheduleDate() {
            return scheduleDate;
        }

        LocalDate getEmptyScheduleDate() {
            return emptyScheduleDate;
        }

        List<Integer> getShownTaskNumbers() {
            return List.copyOf(shownTaskNumbers);
        }

        List<Task> getShownTasks() {
            return List.copyOf(shownTasks);
        }

        @Override
        public void showGoodbye() {
            isGoodbyeShown = true;
        }

        @Override
        public void showHelp() {
            isHelpShown = true;
        }

        @Override
        public void showScheduleHeader(LocalDate date) {
            scheduleDate = date;
        }

        @Override
        public void showNumberedTask(int taskNumber, Task task) {
            shownTaskNumbers.add(taskNumber);
            shownTasks.add(task);
        }

        @Override
        public void showEmptySchedule(LocalDate date) {
            emptyScheduleDate = date;
        }

        @Override
        public void showTaskStatusUpdated(Task task, TaskStatus status) {
            updatedTask = task;
            updatedStatus = status;
        }

        @Override
        public void showTaskDeleted(Task task, int remainingTaskCount) {
            deletedTask = task;
            this.remainingTaskCount = remainingTaskCount;
        }

        @Override
        public void showTaskAdded(Task task, int taskCount) {
            addedTask = task;
            this.taskCount = taskCount;
        }

        @Override
        public void showTaskEdited(Task originalTask, Task editedTask) {
            editedOriginalTask = originalTask;
            this.editedTask = editedTask;
        }

        @Override
        public void showTaskList(TaskList tasks) {
            listedTasks = tasks;
        }

        @Override
        public void showFindHeader() {
            isFindHeaderShown = true;
        }

        @Override
        public void showNoMatchingTasks() {
            isNoMatchingTasksShown = true;
        }
    }
}
