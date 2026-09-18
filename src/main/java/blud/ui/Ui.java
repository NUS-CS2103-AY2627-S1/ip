package blud.ui;

import java.util.List;

import blud.model.TaskList;

/** Formats user-interface sections for the console application. */
public class Ui {
    /** Selects the layout used to print a section. */
    public enum Mode {
        SIMPLE,
        LIST
    }

    /** Prints a formatted section made from a header, body parts, and footer. */
    public void sectionString(String header, List<String> parts, String footer, Mode mode) {
        if (header != null) {
            System.out.println('\t' + header);
        }
        switch (mode) {
            case SIMPLE:
                System.out.println('\t' + String.join("\n\t", parts));
                break;
            default:
                System.out.println("Invalid mode");
        }
        if (footer != null) {
            System.out.println('\t' + footer);
        }
    }

    /** Prints a formatted section containing the tasks in the supplied list. */
    public void sectionTask(String header, TaskList taskList, String footer) {
        if (header != null) {
            System.out.println('\t' + header);
        }
        for (int i = 0; i < taskList.getSize(); i++) {
            System.out.print('\t' + Integer.toString(i + 1) + ". ");
            System.out.println(taskList.getTask(i));
        }
        if (footer != null) {
            System.out.println('\t' + footer);
        }
    }
}
