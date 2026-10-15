package megatron.command;

import megatron.Megatron;
import megatron.exception.MegatronException;
import megatron.parser.CommandType;
import megatron.storage.Storage;
import megatron.task.Deadline;
import megatron.task.Event;
import megatron.task.Task;
import megatron.task.TaskList;
import megatron.ui.Ui;
/** An action produced by parsing one user command. */
public abstract class Command {
    public static Command exit() { return new ExitCommand(); }
    public static Command list() { return new ListCommand(); }
    public static Command add(String input, CommandType type) { return new AddCommand(input, type); }
    public static Command mark(String input, boolean mark) { return new MarkCommand(input, mark); }
    public static Command delete(String input) { return new DeleteCommand(input); }
    public static Command find(String input) { return new FindCommand(input); }
    public static Command snooze(String input) { return new SnoozeCommand(input); }
    /** Executes this action. */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws MegatronException;
    /** Returns whether this action ends the application. */
    public boolean isExit() { return false; }
}

class ExitCommand extends Command {
    public void execute(TaskList tasks, Ui ui, Storage storage) { ui.showGoodbye(); }
    public boolean isExit() { return true; }
}

class ListCommand extends Command {
    public void execute(TaskList tasks, Ui ui, Storage storage) { ui.showTasks(tasks.asList()); }
}

class AddCommand extends Command {
    private final String input;
    private final CommandType type;
    AddCommand(String input, CommandType type) {
        this.input = input;
        this.type = type;
    }
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MegatronException {
        Task task = Megatron.createTaskForCommand(input, type);
        try {
            tasks.add(task);
        } catch (IllegalArgumentException exception) {
            throw new MegatronException("a task with the same details already exists.");
        }
        ui.showAdded(task, tasks.size());
        storage.save(tasks.asList());
    }
}

class MarkCommand extends Command {
    private final String input;
    private final boolean mark;
    MarkCommand(String input, boolean mark) {
        this.input = input;
        this.mark = mark;
    }
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MegatronException {
        int index = Megatron.taskIndexForCommand(input, mark ? "mark" : "unmark", tasks.size());
        if (mark) {
            tasks.get(index).markAsDone();
        } else {
            tasks.get(index).unmark();
        }
        ui.showMarked(tasks.get(index), mark);
        storage.save(tasks.asList());
    }
}

class DeleteCommand extends Command {
    private final String input;
    DeleteCommand(String input) { this.input = input; }
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MegatronException {
        int index = Megatron.taskIndexForCommand(input, "delete", tasks.size());
        Task deleted = tasks.delete(index);
        ui.showDeleted(deleted, tasks.size());
        storage.save(tasks.asList());
    }
}

class FindCommand extends Command {
    private final String keyword;
    FindCommand(String input) {
        this.keyword = input.length() > 5 ? input.substring(5).trim() : "";
    }
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MegatronException {
        if (keyword.isEmpty()) {
            throw new MegatronException("provide a keyword after 'find'.");
        }
        ui.showMatchingTasks(tasks.find(keyword));
    }
}

class SnoozeCommand extends Command {
    private final String input;

    SnoozeCommand(String input) {
        this.input = input;
    }

    public void execute(TaskList tasks, Ui ui, Storage storage) throws MegatronException {
        String remainder = input.substring("snooze".length()).trim();
        int separator = remainder.indexOf(' ');
        if (separator < 0) {
            throw new MegatronException("use 'snooze <task number> <new date/time>'.");
        }
        String taskNumber = remainder.substring(0, separator);
        String newTime = remainder.substring(separator).trim();
        if (newTime.isEmpty()) {
            throw new MegatronException("provide a new date/time after the task number.");
        }
        int index = Megatron.taskIndexForCommand("snooze " + taskNumber, "snooze", tasks.size());
        Task task = tasks.get(index);
        try {
            if (task instanceof Deadline deadline) {
                deadline.reschedule(newTime);
            } else if (task instanceof Event event) {
                event.reschedule(newTime);
            } else {
                throw new MegatronException("only deadline and event tasks can be snoozed.");
            }
        } catch (IllegalArgumentException exception) {
            throw new MegatronException("the new date/time must use yyyy-MM-dd or d/M/yyyy HHmm.");
        }
        ui.showSnoozed(task);
        storage.save(tasks.asList());
    }
}
