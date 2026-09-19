package yuno.command;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import yuno.exception.FileStorageException;
import yuno.storage.Storage;
import yuno.task.Task;
import yuno.task.TaskList;
import yuno.ui.Ui;

abstract class CommandTestSupport {
    @TempDir
    protected Path tempDir;
    protected TaskList taskList;
    protected RecordingUi ui;
    protected Storage storage;

    @BeforeEach
    void setUpCommandDependencies() throws FileStorageException {
        taskList = new TaskList();
        ui = new RecordingUi();
        storage = new Storage(tempDir.resolve("tasks.txt"));
    }

    static class RecordingUi extends Ui {
        private Task addedTask;
        private TaskList displayedTaskList;
        private List<Task> displayedDateTasks;
        private List<Task> displayedNameTasks;
        private Task markedTask;
        private Task unmarkedTask;
        private Task deletedTask;
        private boolean isByePrinted;
        private boolean isHelpPrinted;
        private boolean areTasksClearedPrinted;

        Task getAddedTask() {
            return addedTask;
        }

        TaskList getDisplayedTaskList() {
            return displayedTaskList;
        }

        List<Task> getDisplayedDateTasks() {
            return displayedDateTasks;
        }

        List<Task> getDisplayedNameTasks() {
            return displayedNameTasks;
        }

        Task getMarkedTask() {
            return markedTask;
        }

        Task getUnmarkedTask() {
            return unmarkedTask;
        }

        Task getDeletedTask() {
            return deletedTask;
        }

        boolean isByePrinted() {
            return isByePrinted;
        }

        boolean isHelpPrinted() {
            return isHelpPrinted;
        }

        boolean areTasksClearedPrinted() {
            return areTasksClearedPrinted;
        }

        @Override
        public void printBye() {
            isByePrinted = true;
        }

        @Override
        public void printHelp() {
            isHelpPrinted = true;
        }

        @Override
        public void printAddTask(Task task) {
            addedTask = task;
        }

        @Override
        public void printList(TaskList taskList) {
            displayedTaskList = taskList;
        }

        @Override
        public void printTasksForDate(List<Task> matchingTasks) {
            displayedDateTasks = matchingTasks;
        }

        @Override
        public void printTasksForName(List<Task> matchingTasks) {
            displayedNameTasks = matchingTasks;
        }

        @Override
        public void printMarkTask(Task task) {
            markedTask = task;
        }

        @Override
        public void printUnmarkTask(Task task) {
            unmarkedTask = task;
        }

        @Override
        public void printDeleteTask(Task task) {
            deletedTask = task;
        }

        @Override
        public void printTasksCleared() {
            areTasksClearedPrinted = true;
        }
    }
}
