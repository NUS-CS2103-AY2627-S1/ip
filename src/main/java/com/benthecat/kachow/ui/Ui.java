package com.benthecat.kachow.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import com.benthecat.kachow.exception.KachowException;
import com.benthecat.kachow.parser.DateTimeParser;
import com.benthecat.kachow.task.Task;
import com.benthecat.kachow.task.TaskList;
import com.benthecat.kachow.ui.printer.Printer;

/**
 * Handles console input and gives both interfaces Kachow's Cars-inspired pit-crew voice.
 */
public class Ui {
    private static final String UI_DIVIDER = "____________________________________________________________";
    private static final String UI_BANNER = " _  __          _                    \n"
            + "| |/ /__ _  ___| |__   _____      __\n"
            + "| ' // _` |/ __| '_ \\ / _ \\ \\ /\\ / /\n"
            + "| . \\ (_| | (__| | | | (_) \\ V  V / \n"
            + "|_|\\_\\__,_|\\___|_| |_|\\___/ \\_/\\_/  \n";

    private final Scanner scanner;
    private final Printer printer;

    /** Creates a UI that reads commands from standard input. */
    public Ui(Printer printer) {
        this.printer = printer;
        scanner = new Scanner(System.in);
    }

    /** Displays the startup banner and greeting. */
    public void showWelcome() {
        showLines(
                UI_BANNER,
                "Ka-chow! I'm Kachow, your Radiator Springs pit-crew pal.",
                "You bring the big dreams; I'll keep the tasks tuned up. Try list, or todo win the Piston Cup.");
    }


    /** Reports whether another console command is available. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads the next complete console command. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays the divider that begins or ends a regular command response. */
    public void showDivider() {
        showLines(UI_DIVIDER);
    }

    /** Displays the complete task list. */
    public void showTaskList(TaskList taskList) {
        if (taskList.isEmpty()) {
            showLines("Quiet as Radiator Springs before sunrise! Add a task with todo, deadline, or event.");
            return;
        }
        showLines("Crew chief's clipboard! Here are all your tasks, from first lap to finish line:");
        List<Task> tasks = taskList.getTasks();
        for (int i = 0; i < tasks.size(); i++) {
            showLines((i + 1) + "." + tasks.get(i).getStatusText());
        }
    }

    /** Displays deadlines and events occurring on a particular date. */
    public void showTasksOn(LocalDate date, List<TaskList.NumberedTask> matchingTasks) {
        if (matchingTasks.isEmpty()) {
            showLines("Cruise through Radiator Springs! No deadlines or events on "
                    + DateTimeParser.format(date) + ".");
            return;
        }
        showLines("Sally's road map! Here are the deadlines and events on " + DateTimeParser.format(date) + ":");
        for (TaskList.NumberedTask numberedTask : matchingTasks) {
            showLines(numberedTask.number() + "." + numberedTask.task().getStatusText());
        }
    }

    /** Displays tasks whose descriptions match a search keyword. */
    public void showSearchResults(String keyword, List<TaskList.NumberedTask> matchingTasks) {
        if (matchingTasks.isEmpty()) {
            showLines("Mater checked every back road: no tasks matched \""
                    + keyword + "\". Try another keyword, buddy.");
            return;
        }
        showLines("Mater found 'em! Here are the tasks that match your search:");
        for (TaskList.NumberedTask numberedTask : matchingTasks) {
            showLines(numberedTask.number() + "." + numberedTask.task().getStatusText());
        }
    }

    /** Displays confirmation after a task is added. */
    public void showTaskAdded(Task task, int taskCount) {
        String taskLabel = taskCount == 1 ? " task" : " tasks";
        showLines(
                "Green light, buddy! I've rolled this task onto the starting grid:",
                "  " + task.getStatusText(),
                "Your garage now holds " + taskCount + taskLabel + ".");
    }

    /** Displays confirmation after a task is marked as complete. */
    public void showTaskMarked(Task task) {
        showLines(
                "Ka-chow! That's Piston Cup spirit! This task is marked done:",
                "  " + task.getStatusText(),
                "Doc Hudson would be proud. One task at a time, one lap closer.");
    }

    /** Displays confirmation after a task is marked as incomplete. */
    public void showTaskUnmarked(Task task) {
        showLines(
                "Another practice lap! Even Lightning needs those. This task is marked not done:",
                "  " + task.getStatusText());
    }

    /** Displays confirmation after one task detail is edited. */
    public void showTaskEdited(Task task) {
        showLines(
                "Pit stop complete! Guido's updated this task's details:",
                "  " + task.getStatusText());
    }

    /** Displays confirmation after a task is deleted. */
    public void showTaskDeleted(Task task, int taskCount) {
        String taskLabel = taskCount == 1 ? " task" : " tasks";
        showLines(
                "Mater's towing this one off the roster. Task deleted:",
                "  " + task.getStatusText(),
                "Your garage now holds " + taskCount + taskLabel + ".");
    }

    /** Displays a validation or persistence error. */
    public void showError(String message) {
        printer.markResponseAsError();
        showLines("Pit stop, buddy! Let's get you rolling. " + message);
    }

    /** Displays a loading error while allowing the application to start with an empty list. */
    public void showLoadingError(KachowException exception) {
        showError(exception.getMessage());
        showLines("Saving is disabled. Repair the task file or restore a backup, then restart Kachow.");
    }

    /** Displays the farewell and its closing divider. */
    public void showGoodbye() {
        showLines("Time to refuel at Flo's. Rest those tires, buddy. Ka-chow!");
        showDivider();
    }

    /** Sends the accumulated response to the configured output destination. */
    public void outputData() {
        printer.outputData();
    }

    /** Displays one or more UI lines with consistent formatting. */
    private void showLines(String... lines) {
        for (String line : lines) {
            printer.addData(line);
        }
    }
}
