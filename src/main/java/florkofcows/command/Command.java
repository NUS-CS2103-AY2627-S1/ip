package florkofcows.command;

import java.io.IOException;
import java.time.LocalDate;

import florkofcows.exception.FlorkingExceptions;
import florkofcows.storage.Storage;
import florkofcows.task.Deadline;
import florkofcows.task.Event;
import florkofcows.task.Task;
import florkofcows.task.TaskList;
import florkofcows.task.Todo;
import florkofcows.ui.Ui;

/**
 * Base command type. Concrete command implementations perform actions on the
 * task list and may persist changes via Storage.
 */
public abstract class Command {
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws FlorkingExceptions;

    public boolean isExit() {
        return false;
    }

    protected void save(TaskList tasks, Storage storage) throws FlorkingExceptions {
        try {
            storage.save(tasks.getAll());
        } catch (IOException e) {
            throw new FlorkingExceptions("Couldn't save to disk: " + e.getMessage());
        } catch (RuntimeException e) {
            throw new FlorkingExceptions("Couldn't save to disk: " + e.getMessage());
        }
    }

    /**
     * Command to exit the application.
     */
    public static class ExitCommand extends Command {
        @Override
        public void execute(TaskList tasks, Ui ui, Storage storage) {
            ui.showBye();
        }

        @Override
        public boolean isExit() {
            return true;
        }
    }

    /**
     * Command to list all tasks in the task list.
     */
    public static class ListCommand extends Command {
        @Override
        public void execute(TaskList tasks, Ui ui, Storage storage) {
            ui.showTaskList(tasks);
        }
    }

    /**
     * Command to list all tasks occurring on a specific date.
     */
    public static class OnDateCommand extends Command {
        private final LocalDate queryDate;

        public OnDateCommand(LocalDate queryDate) {
            this.queryDate = queryDate;
        }

        @Override
        public void execute(TaskList tasks, Ui ui, Storage storage) {
            ui.showTasksOnDate(tasks.getTasksOn(queryDate), queryDate);
        }
    }

    /**
     * Command to add a new Todo task.
     */
    public static class AddTodoCommand extends Command {
        private final String description;

        public AddTodoCommand(String description) {
            this.description = description;
        }

        @Override
        public void execute(TaskList tasks, Ui ui, Storage storage) throws FlorkingExceptions {
            Task task = new Todo(description);
            tasks.add(task);
            save(tasks, storage);
            ui.showTaskAdded(task, tasks.size());
        }
    }

    /**
     * Command to add a new Deadline task.
     */
    public static class AddDeadlineCommand extends Command {
        private final String description;
        private final String by;

        /**
         * Constructs a new AddDeadlineCommand with the given description and deadline.
         *
         * @param description the task description
         * @param by          the deadline for the task
         */
        public AddDeadlineCommand(String description, String by) {
            this.description = description;
            this.by = by;
        }

        @Override
        public void execute(TaskList tasks, Ui ui, Storage storage) throws FlorkingExceptions {
            Task task = new Deadline(description, by);
            tasks.add(task);
            save(tasks, storage);
            ui.showTaskAdded(task, tasks.size());
        }
    }

    /**
     * Command to add a new Event task.
     */
    public static class AddEventCommand extends Command {
        private final String description;
        private final String from;
        private final String to;

        /**
         * Constructs a new AddEventCommand with the given description, start time, and end time.
         *
         * @param description the task description
         * @param from        the start time of the event
         * @param to          the end time of the event
         */
        public AddEventCommand(String description, String from, String to) {
            this.description = description;
            this.from = from;
            this.to = to;
        }

        @Override
        public void execute(TaskList tasks, Ui ui, Storage storage) throws FlorkingExceptions {
            Task task = new Event(description, from, to);
            tasks.add(task);
            save(tasks, storage);
            ui.showTaskAdded(task, tasks.size());
        }
    }

    /**
     * Command to mark or unmark a task as done.
     */
    public static class MarkCommand extends Command {
        private final int index;
        private final boolean markAsDone;

        /**
         * Constructs a new MarkCommand with the given index and mark/unmark flag.
         *
         * @param index      the 1-based index of the task to mark/unmark
         * @param markAsDone true to mark the task as done, false to unmark it
         */
        public MarkCommand(int index, boolean markAsDone) {
            this.index = index;
            this.markAsDone = markAsDone;
        }

        @Override
        public void execute(TaskList tasks, Ui ui, Storage storage) throws FlorkingExceptions {
            Task task = markAsDone ? tasks.mark(index) : tasks.unmark(index);
            save(tasks, storage);
            if (markAsDone) {
                ui.showTaskMarked(task.toString());
            } else {
                ui.showTaskUnmarked(task.toString());
            }
        }
    }

    /**
     * Command to delete a task from the task list.
     */
    public static class DeleteCommand extends Command {
        private final int index;

        public DeleteCommand(int index) {
            this.index = index;
        }

        @Override
        public void execute(TaskList tasks, Ui ui, Storage storage) throws FlorkingExceptions {
            Task removed = tasks.delete(index);
            save(tasks, storage);
            ui.showTaskDeleted(removed, tasks.size());
        }
    }

    /**
     * Command to find tasks containing a specific keyword.
     */
    public static class FindCommand extends Command {
        private final String keyword;

        public FindCommand(String keyword) {
            this.keyword = keyword;
        }

        @Override
        public void execute(TaskList tasks, Ui ui, Storage storage) {
            ui.showMatchingTasks(tasks.findTasks(keyword));
        }
    }

    /**
     * Command to add one or more tags to a task.
     */
    public static class TagCommand extends Command {
        private final int index;
        private final String[] tags;

        /**
         * Constructs a new TagCommand with the given index and tags.
         *
         * @param index the 1-based index of the task to tag
         * @param tags  the tags to add to the task
         */
        public TagCommand(int index, String[] tags) {
            this.index = index;
            this.tags = tags;
        }

        @Override
        public void execute(TaskList tasks, Ui ui, Storage storage) throws FlorkingExceptions {
            Task task = tasks.tag(index, tags);
            save(tasks, storage);
            ui.showTaskTagged(task.toString());
        }
    }
}
