package surveyprogram.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import surveyprogram.object.AfterTask;
import surveyprogram.object.Deadline;
import surveyprogram.object.Event;
import surveyprogram.object.Item;
import surveyprogram.object.TaskList;
import surveyprogram.object.Todo;
import surveyprogram.ui.Secret;

/**
 * Serializes task lists to disk and reconstructs them from saved records.
 */
public class TaskStorage {
    private static final Path SAVE_FILE = Path.of("data", "PERSIST.txt");
    private static final DateTimeFormatter FILE_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Loads tasks from the application's default save file.
     *
     * @param taskList task list that receives loaded items
     */
    public static void load(TaskList taskList) {
        load(taskList, SAVE_FILE);
    }

    /**
     * Loads tasks from a specified file, skipping malformed or unknown records.
     *
     * @param taskList task list that receives loaded items
     * @param saveFile file containing serialized task records
     */
    public static void load(TaskList taskList, Path saveFile) {
        File file = saveFile.toFile();
        if (!file.exists()) {
            return; // no previous data
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            List<String> records = readRecords(reader);
            Map<String, Item> itemsById = new HashMap<>();
            List<Item> parsedItems = parseIndependentItems(records, itemsById);
            resolveDependentItems(records, parsedItems, itemsById);
            restoreItems(taskList, parsedItems);
        } catch (IOException exception) {
            Secret.error(false);
        }
    }

    /** Returns all serialized records supplied by the reader. */
    private static List<String> readRecords(BufferedReader reader) throws IOException {
        List<String> records = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            records.add(line);
        }
        return records;
    }

    /** Parses records that do not depend on another saved task. */
    private static List<Item> parseIndependentItems(List<String> records, Map<String, Item> itemsById) {
        List<Item> parsedItems = new ArrayList<>();
        for (String record : records) {
            Item item = parseIndependentItem(record).orElse(null);
            parsedItems.add(item);
            if (item != null) {
                itemsById.put(item.getId(), item);
            }
        }
        return parsedItems;
    }

    /** Resolves parent-dependent records after their parents have been parsed. */
    private static void resolveDependentItems(List<String> records, List<Item> parsedItems,
            Map<String, Item> itemsById) {
        boolean wasRelationshipResolved;
        do {
            wasRelationshipResolved = resolveDependentItemsOnce(records, parsedItems, itemsById);
        } while (wasRelationshipResolved);
    }

    /** Returns whether one pass resolved at least one parent-dependent record. */
    private static boolean resolveDependentItemsOnce(List<String> records, List<Item> parsedItems,
            Map<String, Item> itemsById) {
        boolean wasRelationshipResolved = false;
        for (int index = 0; index < records.size(); index++) {
            if (parsedItems.get(index) != null) {
                continue;
            }
            Optional<Item> parsedItem = parseDependentItem(records.get(index), itemsById);
            if (parsedItem.isPresent()) {
                parsedItems.set(index, parsedItem.get());
                wasRelationshipResolved = true;
            }
        }
        return wasRelationshipResolved;
    }

    /** Restores parsed items until the task list reaches capacity. */
    private static void restoreItems(TaskList taskList, List<Item> parsedItems) {
        for (Item item : parsedItems) {
            if (item != null && !taskList.restore(item)) {
                Secret.error(true);
                return;
            }
        }
    }

    /** Returns a task reconstructed from a valid saved record. */
    private static Optional<Item> parseIndependentItem(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 3) {
            return Optional.empty();
        }

        try {
            boolean isLegacyRecord = isDoneMarker(parts[1]);
            int doneIndex = isLegacyRecord ? 1 : 2;
            int descriptionIndex = isLegacyRecord ? 2 : 3;
            String id = isLegacyRecord ? null : parts[1].trim();
            Item item = createIndependentItem(parts, id, descriptionIndex);
            if (item == null) {
                return Optional.empty();
            }
            item.setDone(parts[doneIndex].trim().equals("y"));
            return Optional.of(item);
        } catch (DateTimeParseException | ArrayIndexOutOfBoundsException exception) {
            return Optional.empty();
        }
    }

    /** Returns a non-dependent task encoded by the record, or {@code null}. */
    private static Item createIndependentItem(String[] parts, String id, int descriptionIndex) {
        String type = parts[0].trim();
        String description = parts[descriptionIndex].trim();
        int detailsIndex = descriptionIndex + 1;
        return switch (type) {
            case "T" -> id == null ? new Todo(description) : new Todo(id, description);
            case "D" -> parts.length <= detailsIndex ? null : createDeadline(
                    id, description, parts[detailsIndex].trim());
            case "E" -> parts.length <= detailsIndex + 1 ? null : createEvent(
                    id, description, parts[detailsIndex].trim(), parts[detailsIndex + 1].trim());
            case "A" -> parts.length <= detailsIndex + 1
                    || !parts[detailsIndex].trim().equals("TIME") ? null
                    : new AfterTask(id, description,
                            LocalDateTime.parse(parts[detailsIndex + 1].trim(), FILE_FORMATTER));
            default -> null;
        };
    }

    /** Returns a deadline with an optional restored identifier. */
    private static Deadline createDeadline(String id, String description, String dateTime) {
        LocalDateTime by = LocalDateTime.parse(dateTime, FILE_FORMATTER);
        return id == null ? new Deadline(description, by) : new Deadline(id, description, by);
    }

    /** Returns an event with an optional restored identifier. */
    private static Event createEvent(String id, String description, String fromText, String toText) {
        LocalDateTime from = LocalDateTime.parse(fromText, FILE_FORMATTER);
        LocalDateTime to = LocalDateTime.parse(toText, FILE_FORMATTER);
        return id == null ? new Event(description, from, to) : new Event(id, description, from, to);
    }

    /** Returns a parent-dependent task when its saved parent can be resolved. */
    private static Optional<Item> parseDependentItem(String line, Map<String, Item> itemsById) {
        String[] parts = line.split("\\|");
        if (parts.length < 6 || !parts[0].trim().equals("A")
                || !parts[4].trim().equals("TASK")) {
            return Optional.empty();
        }

        Item parent = itemsById.get(parts[5].trim());
        if (parent == null) {
            return Optional.empty();
        }
        AfterTask item = new AfterTask(parts[1].trim(), parts[3].trim(), parent);
        item.setDone(parts[2].trim().equals("y"));
        itemsById.put(item.getId(), item);
        return Optional.of(item);
    }

    /** Returns whether a field is a legacy completion marker. */
    private static boolean isDoneMarker(String field) {
        String marker = field.trim();
        return marker.equals("y") || marker.equals("n");
    }

    /**
     * Converts an item into the pipe-delimited representation used in the save file.
     *
     * @param item task to serialize
     * @return serialized task record
     */
    private static String itemToFileString(Item item) {
        String done = item.isDone() ? "y" : "n";
        String prefix = item.getId() + " | " + done + " | ";
        if (item instanceof AfterTask afterTask) {
            return afterTaskToFileString(afterTask, prefix);
        } else if (item instanceof Deadline deadline) {
            return deadlineToFileString(deadline, prefix);
        } else if (item instanceof Event event) {
            return eventToFileString(event, prefix);
        }
        String type = item instanceof Todo ? "T" : "?";
        return type + " | " + prefix + item.getName();
    }

    /** Returns the serialized representation of a dependent task. */
    private static String afterTaskToFileString(AfterTask afterTask, String prefix) {
        if (afterTask.hasParent()) {
            return "A | " + prefix + afterTask.getName() + " | TASK | "
                    + afterTask.getParent().getId();
        }
        return "A | " + prefix + afterTask.getName() + " | TIME | "
                + afterTask.getReleaseAt().format(FILE_FORMATTER);
    }

    /** Returns the serialized representation of a deadline. */
    private static String deadlineToFileString(Deadline deadline, String prefix) {
        return "D | " + prefix + deadline.getName() + " | "
                + deadline.getBy().format(FILE_FORMATTER);
    }

    /** Returns the serialized representation of an event. */
    private static String eventToFileString(Event event, String prefix) {
        return "E | " + prefix + event.getName() + " | "
                + event.getFrom().format(FILE_FORMATTER) + " | "
                + event.getTo().format(FILE_FORMATTER);
    }

    /**
     * Writes all tasks to the application's default save file.
     *
     * @param taskList task list to save
     */
    public static void save(TaskList taskList) {
        save(taskList, SAVE_FILE);
    }

    /**
     * Writes all tasks to a specified file, creating parent directories when needed.
     *
     * @param taskList task list to save
     * @param saveFile destination for serialized task records
     */
    public static void save(TaskList taskList, Path saveFile) {
        List<Item> items = taskList.getList();

        try {
            File file = saveFile.toFile();
            File parent = file.getParentFile();
            if (parent != null) {
                parent.mkdirs(); // ensure directory exists
            }
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                items.stream()
                        .map(TaskStorage::itemToFileString)
                        .forEach(writer::println);
            }
        } catch (IOException exception) {
            Secret.error(false);
        }
    }
}
