package thomas.ui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import thomas.place.Place;
import thomas.place.PlaceList;
import thomas.task.Task;
import thomas.task.TaskList;

/**
 * Handles interactions with the user through the command line.
 */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";

    /**
     * Shows the welcome message when Thomas starts.
     */
    public void showWelcome() {
        String banner = """
                ▀▀█▀▀ █  █ █▀▀█ █▀▄▀█ █▀▀█ █▀▀
                  █   █▀▀█ █  █ █ ▀ █ █▄▄█ ▀▀█
                  ▀   ▀  ▀ ▀▀▀▀ ▀   ▀ ▀  ▀▀▀
                """;
        showDivider();
        System.out.println(banner);
        System.out.println("Hello! I'm Thomas.");
        System.out.println("Whats up? What can I do for you?");
        showDivider();
    }

    /**
     * Reads one command from the user.
     *
     * @param scanner Scanner connected to user input.
     * @return The next command, or null when input ends.
     */
    public String readCommand(Scanner scanner) {
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine().trim();
    }

    /**
     * Shows the divider used between user interactions.
     */
    public void showDivider() {
        System.out.println(DIVIDER);
    }

    /**
     * Shows the goodbye message.
     */
    public void showGoodbye() {
        System.out.println("Bye. See yaa!");
        showDivider();
    }

    /**
     * Displays all tasks in the list.
     *
     * @param tasks Task list to display.
     */
    public void showTasks(TaskList tasks) {
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i += 1) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays tasks that match a keyword search.
     *
     * @param matchingTasks matching tasks to display
     */
    public void showMatchingTasks(ArrayList<Task> matchingTasks) {
        System.out.println(" Here are the matching tasks in your list:");
        for (int i = 0; i < matchingTasks.size(); i += 1) {
            System.out.println(" " + (i + 1) + "." + matchingTasks.get(i));
        }
        if (matchingTasks.isEmpty()) {
            System.out.println(" Broo, no matching tasks found.");
        }
    }

    /**
     * Displays tasks occurring on a particular date.
     *
     * @param tasks Task list to search.
     * @param date Date being searched.
     */
    public void showTasksOn(TaskList tasks, LocalDate date) {
        int shown = 0;
        System.out.println(" Here are tasks on " + date + ":");
        for (int i = 0; i < tasks.size(); i += 1) {
            if (tasks.get(i).occursOn(date)) {
                shown++;
                System.out.println(" " + shown + "." + tasks.get(i));
            }
        }
        if (shown == 0) {
            System.out.println(" Lucky broo, got nothing due on " + date + "!");
        }
    }

    /**
     * Displays the confirmation for a newly added task.
     *
     * @param task Task that was added.
     * @param totalTask Current task count.
     */
    public void showTaskAdded(Task task, int totalTask) {
        System.out.println(" Got it, added this task:");
        System.out.println("   " + task);
        System.out.println(" You have " + totalTask + " tasks left in the list.");
    }

    /**
     * Displays a task completion confirmation.
     *
     * @param task Task that was marked.
     */
    public void showTaskMarked(Task task) {
        System.out.println(" Nice! Task has been marked as done:");
        System.out.println("   " + task);
    }

    /**
     * Displays an incomplete-task confirmation.
     *
     * @param task Task that was unmarked.
     */
    public void showTaskUnmarked(Task task) {
        System.out.println(" Ok, task has been marked as not done:");
        System.out.println("   " + task);
    }

    /**
     * Displays a deletion confirmation.
     *
     * @param task Task that was deleted.
     * @param remainingTasks Number of tasks left.
     */
    public void showTaskDeleted(Task task, int remainingTasks) {
        System.out.println(" Alright! This task has been deleted:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + remainingTasks + " tasks left");
    }

    /**
     * Displays a user-facing error.
     *
     * @param message Error message.
     */
    public void showError(String message) {
        System.out.println("oooooooof :<< " + message);
    }

    /**
     * Displays an invalid-number error.
     */
    public void showInvalidTaskNumber() {
        System.out.println("oooooooof Please enter a valid task number.");
    }

    /** Displays all commands available to the user. */
    public void showHelp() {
        System.out.println("Available commands:");
        System.out.println("Tasks:");
        System.out.println("  todo DESCRIPTION");
        System.out.println("  deadline DESCRIPTION /by DATE");
        System.out.println("  event DESCRIPTION /from DATE /to DATE");
        System.out.println("  list");
        System.out.println("  find KEYWORD");
        System.out.println("  on DATE");
        System.out.println("  mark INDEX");
        System.out.println("  unmark INDEX");
        System.out.println("  delete INDEX");
        System.out.println("Places:");
        System.out.println("  place NAME /type TYPE /at ADDRESS /rating 1-5 /price AMOUNT"
                + " [/visited YYYY-MM-DD] [/note NOTE]");
        System.out.println("  listplace");
        System.out.println("  findplace KEYWORD");
        System.out.println("  editplace INDEX /FIELD VALUE");
        System.out.println("  deleteplace INDEX");
        System.out.println("  confirmdeleteplace INDEX");
        System.out.println("Other:");
        System.out.println("  /help");
        System.out.println("  bye");
    }

    /** Displays every saved place. */
    public void showPlaces(PlaceList places) {
        if (places.size() == 0) {
            System.out.println("You have no saved places.");
            return;
        }
        System.out.println("Here are your saved places:");
        for (int i = 0; i < places.size(); i++) {
            showPlace(places.get(i), i + 1);
        }
    }

    /** Displays places whose names match a keyword. */
    public void showMatchingPlaces(PlaceList places, String keyword) {
        ArrayList<Place> matches = places.findByName(keyword);
        if (matches.isEmpty()) {
            System.out.println("No saved places match \"" + keyword + "\".");
            return;
        }
        System.out.println("Here are the matching saved places:");
        for (Place place : matches) {
            showPlace(place, places.getOneBasedIndex(place));
        }
    }

    /** Displays the confirmation for a newly added place. */
    public void showPlaceAdded(Place place, int totalPlaces) {
        System.out.println("Added this place:");
        showPlace(place, totalPlaces);
        System.out.println("You have " + totalPlaces + " " + (totalPlaces == 1 ? "place" : "places")
                + " in your saved places.");
    }

    /** Displays the updated place. */
    public void showPlaceUpdated(Place place, int index) {
        System.out.println("Updated this place:");
        showPlace(place, index);
    }

    /** Requests confirmation before a place is deleted. */
    public void showPlaceDeletionRequest(Place place, int index) {
        System.out.println("Delete this place? Enter `confirmdeleteplace " + index + "` to confirm:");
        showPlace(place, index);
    }

    /** Displays the confirmation for a deleted place. */
    public void showPlaceDeleted(Place place, int index, int remainingPlaces) {
        System.out.println("Deleted this place:");
        System.out.println("  " + index + ". " + place.getName());
        System.out.println("You have " + remainingPlaces + " saved "
                + (remainingPlaces == 1 ? "place." : "places."));
    }

    private void showPlace(Place place, int index) {
        System.out.println("  " + index + ". " + place.getName());
        System.out.println("     Type: " + place.getType());
        System.out.println("     Address: " + place.getAddress());
        System.out.println("     Rating: " + place.getRating() + "/5");
        System.out.println("     Price: " + place.getPrice());
        System.out.println("     Date visited: " + (place.getVisitedDate() == null
                ? "Not recorded" : place.getVisitedDate()));
        System.out.println("     Note: " + (place.getNote() == null ? "Not recorded" : place.getNote()));
    }
}
