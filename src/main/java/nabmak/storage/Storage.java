package nabmak.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

import nabmak.task.Deadline;
import nabmak.task.Event;
import nabmak.task.Task;
import nabmak.task.ToDo;

/**
 * Handles loading and saving tasks from and to a storage file.
 */
public class Storage {

    private static final DateTimeFormatter DATE_FORMAT =
        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private final String filePath;

    /**
     * Creates file path for data to be stored.
     *
     * @param filePath path of file where tasks are stored
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Saves list of tasks to storage file.
     *
     * @param tasks list of tasks being saved
     */
    public void save(ArrayList<Task> tasks) {
        try {
            File file = new File(this.filePath);
            File parent = file.getParentFile();

            if (parent != null) {
                parent.mkdirs();
            }

            FileWriter writer = new FileWriter(file);

            for (Task task: tasks) {
                writer.write(taskToStr(task));
                writer.write(System.lineSeparator());
            }

            writer.close();
        } catch (IOException e) {
            System.out.println("Couldnt save tasks");
        }
    }

    /**
     * Converts task into String format to store in file.
     *
     * @param task task to convert
     * @return string format of task
     */
    private String taskToStr(Task task) {
        String done = task.getStatus().equals("[X]") ? "1" : "0";

        if (task instanceof ToDo) {
            return "T | " + done + " | " + task.getDesc();
        } else if (task instanceof Deadline) {
            Deadline deadline = (Deadline) task;
            return "D | " + done + " | " + task.getDesc() + " | "
                + deadline.getDead().format(DATE_FORMAT);
        } else if (task instanceof Event) {
            Event event = (Event) task;
            return "E | " + done + " | " + task.getDesc()
                + " | " + event.getStart().format(DATE_FORMAT)
                + " | " + event.getEnd().format(DATE_FORMAT);
        }

        return "";
    }

    /**
     * Loads existing tasks into application.
     *
     * @return list of tasks loaded
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(this.filePath);

        if (!file.exists()) {
            return tasks;
        }

        try {
            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                Task task = strToTask(line);

                if (task != null) {
                    tasks.add(task);
                }
            }

            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println("Couldnt load tasks.");
        }

        return tasks;
    }

    /**
     * Converts stored string into a Task object.
     *
     * @param str stored string
     * @return task constructed from string
     */
    private Task strToTask(String str) {
        String[] data = str.split(" \\| ");
        String type = data[0];
        boolean isDone = data[1].equals("1");
        String desc = data[2];
        Task task;

        if (type.equals("T")) {
            task = new ToDo(desc);
        } else if (type.equals("D")) {
            String deadlineString = data[3];
            LocalDateTime deadline = LocalDateTime.parse(deadlineString, DATE_FORMAT);
            task = new Deadline(desc, deadline);
        } else if (type.equals("E")) {
            String startString = data[3];
            String endString = data[4];
            LocalDateTime start = LocalDateTime.parse(startString, DATE_FORMAT);
            LocalDateTime end = LocalDateTime.parse(endString, DATE_FORMAT);
            task = new Event(desc, start, end);
        } else {
            return null;
        }

        if (isDone) {
            task.markDone();
        }

        return task;
    }
}
