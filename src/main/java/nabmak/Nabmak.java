package nabmak;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import nabmak.parser.NabmakException;
import nabmak.parser.Parser;
import nabmak.place.Place;
import nabmak.place.PlaceList;
import nabmak.storage.PlaceStorage;
import nabmak.storage.Storage;
import nabmak.task.Deadline;
import nabmak.task.Event;
import nabmak.task.Task;
import nabmak.task.TaskList;
import nabmak.task.ToDo;
import nabmak.ui.Ui;

/**
 * Creates and runs the Nabmak application.
 * Handles user interaction, parsing and storage.
 */
public class Nabmak {
    private static final DateTimeFormatter DATE_FORMAT =
        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;
    private final PlaceList places;
    private final PlaceStorage placeStorage;

    /**
     * Creates a Nabmak application and loads in saved tasks.
     */
    public Nabmak() {
        this.ui = new Ui();
        this.storage = new Storage("./data/nabmak.txt");
        this.tasks = new TaskList(storage.load());
        this.placeStorage = new PlaceStorage("./data/places.txt");
        this.places = new PlaceList(placeStorage.load());
    }

    /**
     * Adds a task to the task list, saves it, and returns the confirmation.
     *
     * @param task task to add
     * @return confirmation message
     */
    private String addTask(Task task) {
        tasks.add(task);
        storage.save(tasks.getTasks());

        return "Ok new task!\n" + task
            + "\nNow got " + tasks.size() + " tasks.";
    }

    /**
     * Processes a single user command and returns Nabmak's response.
     *
     * @param input the command entered by the user
     * @return response to the command
     */
    public String processCommand(String input) {
        try {
            Parser.parse(input, tasks.size(), places.size());
        } catch (NabmakException e) {
            return "TOUGH! " + e.getMessage();
        }

        if (input.equals("bye")) {
            return "BYE!";
        } else if (input.equals("list")) {
            return handleList();
        } else if (input.startsWith("mark ")) {
            return handleMark(input.substring(5));
        } else if (input.startsWith("unmark ")) {
            return handleUnmark(input.substring(7));
        } else if (input.startsWith("todo ")) {
            return addTask(new ToDo(input.substring(5)));
        } else if (input.startsWith("deadline ")) {
            return handleDeadline(input.substring(9));
        } else if (input.startsWith("event ")) {
            return handleEvent(input.substring(6));
        } else if (input.startsWith("delete ")) {
            return handleDelete(input.substring(7));
        } else if (input.startsWith("find ")) {
            return handleFind(input.substring(5));
        } else if (input.startsWith("place add ")) {
            return handlePlaceAdd(input.substring(10));
        } else if (input.startsWith("place delete ")) {
            return handlePlaceDelete(input.substring(13));
        } else if (input.equals("place list")) {
            return handlePlaceList();
        }

        return "";
    }

    /**
     * Returns all tasks as a numbered list.
     *
     * @return response listing every task
     */
    private String handleList() {
        return formatList("Your TODOLIST\n", tasks.getTasks());
    }

    /**
     * Marks a task as done and saves the change.
     *
     * @param numStr task number given by the user
     * @return confirmation message
     */
    private String handleMark(String numStr) {
        Task task = tasks.get(parseIndex(numStr));
        task.markDone();
        storage.save(tasks.getTasks());

        return "Good that task is DONE\n" + task.toString();
    }

    /**
     * Marks a task as not done and saves the change.
     *
     * @param numStr task number given by the user
     * @return confirmation message
     */
    private String handleUnmark(String numStr) {
        Task task = tasks.get(parseIndex(numStr));
        task.markNotDone();
        storage.save(tasks.getTasks());

        return "Tuff this task not done :(\n" + task.toString();
    }

    /**
     * Creates and adds a deadline task.
     *
     * @param info text after "deadline "
     * @return confirmation message
     */
    private String handleDeadline(String info) {
        int mid = info.indexOf(" /by ");
        String desc = info.substring(0, mid);
        LocalDateTime deadline = parseDate(info.substring(mid + 5));

        return addTask(new Deadline(desc, deadline));
    }

    /**
     * Creates and adds an event task.
     *
     * @param info text after "event "
     * @return confirmation message
     */
    private String handleEvent(String info) {
        int left = info.indexOf(" /from ");
        int right = info.indexOf(" /to ");
        String desc = info.substring(0, left);
        LocalDateTime start = parseDate(info.substring(left + 7, right));
        LocalDateTime end = parseDate(info.substring(right + 5));

        return addTask(new Event(desc, start, end));
    }

    /**
     * Deletes a task and saves the change.
     *
     * @param numStr task number given by the user
     * @return confirmation message
     */
    private String handleDelete(String numStr) {
        Task deleted = tasks.delete(parseIndex(numStr));
        storage.save(tasks.getTasks());

        return "Noted. I've removed the task:\n" + deleted + "\nNow got " + tasks.size()
            + " tasks.";
    }

    /**
     * Finds tasks whose description contains the keyword.
     *
     * @param keyword keyword
     * @return list of matching tasks
     */
    private String handleFind(String keyword) {
        TaskList matches = tasks.find(keyword);

        return formatList("You looking for these?:\n", matches.getTasks());
    }

    /**
     * Creates and adds a place.
     *
     * @param info text after "place add "
     * @return confirmation message
     */
    private String handlePlaceAdd(String info) {
        int mid = info.indexOf(" /details ");
        Place place = new Place(info.substring(0, mid), info.substring(mid + 10));
        places.add(place);
        placeStorage.save(places.getPlaces());

        return "Ok new place!\n" + place
            + "\nNow got " + places.size() + " places.";
    }

    /**
     * Deletes a place and saves the change.
     *
     * @param numStr place number given by the user
     * @return confirmation message
     */
    private String handlePlaceDelete(String numStr) {
        Place deleted = places.delete(parseIndex(numStr));
        placeStorage.save(places.getPlaces());

        return "Noted. I've removed the place:\n" + deleted + "\nNow got " + places.size()
            + " places.";
    }

    /**
     * Returns all places as a numbered list.
     *
     * @return list of place
     */
    private String handlePlaceList() {
        return formatList("Your PLACES\n", places.getPlaces());
    }

    /**
     * Converts a 1 based number typed by the user into a 0 based list index.
     *
     * @param numStr number given by the user
     * @return 0 based index
     */
    private static int parseIndex(String numStr) {
        return Integer.parseInt(numStr.trim()) - 1;
    }

    /**
     * Converts a date string that has already been validated by the parser.
     *
     * @param text date in 'dd-MM-yyyy HH:mm' format
     * @return the parsed date and time
     */
    private static LocalDateTime parseDate(String text) {
        return LocalDateTime.parse(text, DATE_FORMAT);
    }

    /**
     * Builds a numbered list of items under a header.
     *
     * @param header text shown before the list
     * @param items items to list
     * @return the header followed by items
     */
    private static String formatList(String header, List<?> items) {
        StringBuilder output = new StringBuilder(header);

        for (int i = 0; i < items.size(); i++) {
            output.append(i + 1).append(". ").append(items.get(i)).append("\n");
        }

        return output.toString();
    }

    /**
     * Runs the main command loop of application.
     * Reads and processes user commands until they exit.
     */
    public void run() {
        Scanner sc = new Scanner(System.in);

        ui.showHi();

        while (true) {
            String input = sc.nextLine();
            System.out.println(processCommand(input));

            if (input.equals("bye")) {
                break;
            }
        }
        sc.close();
    }

    /**
     * Starts Nabmak application.
     *
     * @param args arguments supplied to application
     */
    public static void main(String[] args) {
        new Nabmak().run();
    }

}
