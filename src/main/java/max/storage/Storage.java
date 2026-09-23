package max.storage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import max.task.Task;
import max.data.TaskType;
import max.data.Status;
import max.task.Deadline;
import max.task.Event;
import max.task.Todo;
import max.maxexception.InvalidStorageDataException;
import max.maxexception.MaxException;

public class Storage {
    private String filePath;

    public Storage(String filePath) {
        this.filePath = filePath;
    }


   /**
    * Saves list of task into text file 
    *
    * @param tasks list of task to save.
    * @throws IOException when the filepath is invalid 
    */
    public void save(List<Task> tasks) throws IOException {
        StringBuilder res = new StringBuilder();
        Path path = Paths.get(filePath);
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        for (Task task: tasks) {
            res.append(escapeDescription(task)).append("\n");
        }
        try (FileWriter fw = new FileWriter(filePath)) {
            fw.write(res.toString());
        }
    }


    /**
     ** Load list of task from text file and return it 
    *
    * @return ArrayList<Task> list of task from the text file.
    * @throws FileNotFoundException when the file does not exist 
    */
    public ArrayList<Task> load() throws FileNotFoundException, MaxException {
        ArrayList<Task> tasks = new ArrayList<>();
        File f = new File(filePath);
        if (!f.exists()) {
            return tasks;
        }

        Scanner s = new Scanner(f);
        int lineNumber = 0;
        while (s.hasNext()) {
            lineNumber++;
            String line = s.nextLine();
            String[] data = splitRecord(line);
            try {
                TaskType type = TaskType.fromSymbol(data[0]);
                switch (type) {
                    case TODO:
                        if (data.length != 3) {
                            throw new IllegalArgumentException();
                        }
                        tasks.add(new Todo(data[2], Status.fromSymbol(data[1])));
                        break;
                    case DEADLINE:
                        if (data.length != 4) {
                            throw new IllegalArgumentException();
                        }
                        if (data[3].contains("T")) {
                            tasks.add(new Deadline(data[2], Status.fromSymbol(data[1]),
                                    LocalDateTime.parse(data[3])));
                        } else {
                            tasks.add(new Deadline(data[2], Status.fromSymbol(data[1]),
                                    LocalDate.parse(data[3])));
                        }
                        break;
                    case EVENT:
                        if (data.length != 5) {
                            throw new IllegalArgumentException();
                        }
                        if (data[3].contains("T") || data[4].contains("T")) {
                            tasks.add(new Event(data[2], Status.fromSymbol(data[1]),
                                    LocalDateTime.parse(data[3]), LocalDateTime.parse(data[4])));
                        } else {
                            tasks.add(new Event(data[2], Status.fromSymbol(data[1]),
                                    LocalDate.parse(data[3]), LocalDate.parse(data[4])));
                        }
                        break;
                    default:
                        break;
                }

            } catch (IllegalArgumentException | MaxException e) {
                s.close();
                throw new InvalidStorageDataException(lineNumber);
            }
        }
        s.close();
        return tasks;
    }

    /**
     * Escapes separator characters in the task description before saving.
     *
     * @param task task to serialize
     * @return serialized task data with an escaped description
     */
    private static String escapeDescription(Task task) {
        String itemString = task.getItemString();
        int descriptionStart = itemString.indexOf(" | ");
        descriptionStart = descriptionStart < 0
                ? -1
                : itemString.indexOf(" | ", descriptionStart + 3);
        if (descriptionStart < 0) {
            return itemString;
        }

        descriptionStart += 3;
        int descriptionEnd = descriptionStart + task.getDescription().length();
        String prefix = itemString.substring(0, descriptionStart);
        String description = itemString.substring(descriptionStart, descriptionEnd)
                .replace("\\", "\\\\")
                .replace("|", "\\|");
        return prefix + description + itemString.substring(descriptionEnd);
    }

    /**
     * Splits a saved record without treating escaped pipes as separators.
     *
     * @param line saved task record
     * @return record fields with escaped description characters restored
     */
    private static String[] splitRecord(String line) {
        ArrayList<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean escaped = false;
        for (int i = 0; i < line.length(); i++) {
            char current = line.charAt(i);
            if (escaped) {
                field.append(current == '|' || current == '\\' ? current : '\\');
                if (current != '|' && current != '\\') {
                    field.append(current);
                }
                escaped = false;
            } else if (current == '\\') {
                escaped = true;
            } else if (current == '|' && i > 0 && i + 1 < line.length()
                    && line.charAt(i - 1) == ' ' && line.charAt(i + 1) == ' ') {
                field.setLength(field.length() - 1);
                fields.add(field.toString());
                field = new StringBuilder();
                i++;
            } else {
                field.append(current);
            }
        }
        if (escaped) {
            field.append('\\');
        }
        fields.add(field.toString());
        return fields.toArray(new String[0]);
    }
}
