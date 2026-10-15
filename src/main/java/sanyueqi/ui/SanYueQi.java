package sanyueqi.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SanYueQi {
    static final TaskList masterTaskList = new TaskList();
    List<String> lines;
    int initResponseCode;
    int errorLine;

    private static LocalDateTime now = LocalDateTime.now();
    private static String startDate = now.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
    private static String startTime = now.format(DateTimeFormatter.ofPattern("HH:mm"));

    private static final String GREETING = String.format("""
            Welcome back! It's March.
            Today's date is %s and the current time is %s.
            Are you here to play with me?""", startDate, startTime);

    public SanYueQi() {
        this.initResponseCode = 0;
        this.errorLine = 0;
    }

    public void readCsv() {
        try {
            this.lines = Files.readAllLines(Paths.get("logbook.csv"));
        } catch (IOException e1) {
            try {
                Files.createFile(Paths.get("logbook.csv"));
                this.lines = new ArrayList<>();
                this.initResponseCode = 1;
            } catch (IOException e2) {
                this.initResponseCode = 2;
            }
        }
    }

    public void parseCsv() {
        for (int i = 0; i < this.lines.size(); i++) {
            if (!parseLine(this.lines.get(i))) {
                this.errorLine = i + 1;
                return;
            }
        }
    }

    /**
     * Returns the initial response status to the GUI handler.
     */
    public String getInitResponseStatus() {
        readCsv();
        return this.initResponseCode == 0
            ? "Existing logbook found and loaded. Parsing..."
            : this.initResponseCode == 1
            ? "Existing logbook not found. New logbook created."
            : "Critical error: Unable to load or create logbook";
    }

    /**
     * Returns the final response status to the GUI handler.
     */
    public String getFinalResponseStatus() {
        parseCsv();
        return this.errorLine == 0
            ? GREETING
            : String.format("Critical error: Malformed logbook at line %d", this.errorLine);
    }

    /**
     * Parses a line in the master CSV file and checks whether it is valid.
     *
     * @param line Line in the master CSV file to parse.
     * @return Whether the line is valid.
     */
    private static boolean parseLine(String line) {
        if (line.length() <= 4) return false;
        boolean quote = false;
        StringBuilder[] args = new StringBuilder[4]; // description, dueDate or duration, from, to
        for (int j = 0; j < 4; j++) {
            args[j] = new StringBuilder();
        }
        char type;
        boolean done;
        int index = 0;
        String typeToken = line.substring(0,2);
        String doneToken = line.substring(2,4);

        switch (typeToken) {
            case "T,":
                type = 'T';
                break;
            case "D,":
                type = 'D';
                break;
            case "E,":
                type = 'E';
                break;
            case "F,":
                type = 'F';
                break;
            default:
                return false;
        }

        switch (doneToken) {
            case "0,":
                done = false;
                break;
            case "1,":
                done = true;
                break;
            default:
                return false;
        }

        for (int i = 4; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (!quote) {
                    quote = true;
                } else {
                    if (i == line.length() - 1) break;
                    i++;
                    if (line.charAt(i) == ',') {
                        quote = false;
                        index = index == 0 ? (type == 'D' || type == 'F' ? 1 : 2) : 3;
                    } else if (line.charAt(i) == '"') {
                        assert (index >= 0 && index <= 3);
                        args[index].append('"');
                    } else {
                        return false;
                    }
                }
            } else {
                assert (index >= 0 && index <= 3);
                args[index].append(c);
            }
        }

        switch (type) {
            case 'T':
                ToDo todo = new ToDo(done, args[0].toString());
                masterTaskList.addTask(todo);
                break;
            case 'D':
                Deadline deadline = new Deadline(done, args[0].toString(), args[1].toString());
                masterTaskList.addTask(deadline);
                break;
            case 'E':
                Event event = new Event(done, args[0].toString(), args[2].toString(), args[3].toString());
                masterTaskList.addTask(event);
                break;
            case 'F':
                FixedDuration fixedDuration = new FixedDuration(done, args[0].toString(), args[1].toString());
                masterTaskList.addTask(fixedDuration);
                break;
            default:
                return false;
        }
        return true;
    }

    public String getResponse(String command) {
        String[] parts = command.strip().split("\\s+");

        try {
            return switch (parts[0]) {
                case "bye" -> "Bye. Hope to see you again soon! ";
                case "list" -> masterTaskList.printTasks() + " ";
                case "mark" -> masterTaskList.markTask(parts, true) + " ";
                case "unmark" -> masterTaskList.markTask(parts, false) + " ";
                case "todo" -> masterTaskList.addAndWriteTask(ToDo.makeToDo(parts)) + " ";
                case "deadline" -> masterTaskList.addAndWriteTask(Deadline.makeDeadline(parts)) + " ";
                case "event" -> masterTaskList.addAndWriteTask(Event.makeEvent(parts)) + " ";
                case "fixed" -> masterTaskList.addAndWriteTask(FixedDuration.makeDuration(parts)) + " ";
                case "find" -> masterTaskList.findTasks(parts) + " ";
                case "delete" -> masterTaskList.deleteTask(parts) + " ";
                default -> "Sorry, I don't understand your request! ";
            };
        } catch (SYQException e) {
            return e.toString();
        }
    }
}
