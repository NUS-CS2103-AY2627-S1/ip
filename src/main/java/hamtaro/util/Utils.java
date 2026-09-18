package hamtaro.util;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;

import hamtaro.storage.Storage;
import hamtaro.task.TaskList;

public class Utils {


    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static LocalDate parseDate(String input) throws IllegalArgumentException {
        String[] formats = {"dd/MM/yyyy", "dd-MM-yyyy", "yyyy-MM-dd"};

        for (String format : formats) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                return LocalDate.parse(input, formatter);
            } catch (DateTimeParseException e) {
                // Try the next format
            }
        }

        throw new IllegalArgumentException("Invalid date format");
    }

    public static LocalDateTime parseDateTime(String input) throws IllegalArgumentException {
        String[] formats = {
                "dd/MM/uuuu HHmm",
                "dd-MM-uuuu HHmm",
                "uuuu/MM/dd HHmm",
                "uuuu-MM-dd HHmm",
        };

        for (String format : formats) {
            try {
                DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                        .appendPattern(format)
                        .toFormatter()
                        .withResolverStyle(ResolverStyle.STRICT);

                return LocalDateTime.parse(input, formatter);
            } catch (DateTimeParseException e) {
                // Try the next format
            }
        }

        // Storage writes LocalDateTime values in ISO format, e.g. 2026-03-22T12:00.
        try {
            return LocalDateTime.parse(input, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            // Throw the usual user-facing error below.
        }

        throw new IllegalArgumentException("Invalid date/time format");
    }

    public static TaskList loadTasks() {
        try {
            return new TaskList(Storage.loadTasks());
        } catch (IOException e) {
            System.out.println("Unable to load saved tasks: " + e.getMessage());
            return new TaskList();        }
    }

    public static void saveTasks(TaskList tasks) {
        try {
            Storage.saveTasks(tasks);
        } catch (IOException e) {
            System.out.println("Unable to save tasks: " + e.getMessage());
        }
    }
}
