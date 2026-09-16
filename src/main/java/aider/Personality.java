package aider;

import java.time.LocalDate;

import aider.model.Task;

/** Provides Aider's consistent voice across the command-line and graphical interfaces. */
public final class Personality {
    /** Prevents construction of this collection of personality responses. */
    private Personality() {
    }

    /** Returns Aider's welcome message. */
    public static String welcomeMessage() {
        return "Hello! I'm Aider, your calm productivity coach. Let's take it one task at a time.";
    }

    /** Returns Aider's goodbye message. */
    public static String goodbyeMessage() {
        return "All done for now. Take care - I'll be here when you're ready!";
    }

    /** Returns the heading used when showing the complete task list. */
    public static String listHeading() {
        return "Here is your game plan:";
    }

    /** Returns the heading used when showing scheduled tasks. */
    public static String scheduleHeading(String dateOrTime) {
        return "Here is what you have lined up for " + dateOrTime + ":";
    }

    /** Returns the heading used when showing tasks matching a keyword. */
    public static String findHeading() {
        return "Here are the tasks that match:";
    }

    /** Returns the heading used when showing tasks occurring on a date. */
    public static String dateHeading(LocalDate date) {
        return "Here is what is happening on " + date + ":";
    }

    /** Returns a warm response for a newly added task. */
    public static String addedTask(Task task, int count) {
        return "Nice and steady - I've added this task:\n  " + task
                + "\nYou now have " + count + " tasks in the list.";
    }

    /** Returns an encouraging response for a completed task. */
    public static String markedTask(Task task) {
        return "Great progress - I've marked this task as done:\n  " + task;
    }

    /** Returns a reassuring response for an unmarked task. */
    public static String unmarkedTask(Task task) {
        return "No pressure - I've marked this task as not done yet:\n  " + task;
    }

    /** Returns a calm response for a deleted task. */
    public static String deletedTask(Task task, int remaining) {
        return "Cleared from your list:\n  " + task
                + "\nYou now have " + remaining + " tasks in the list.";
    }
}
