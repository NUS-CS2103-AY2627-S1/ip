package walle.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

import walle.note.Note;
import walle.task.Deadline;
import walle.task.Event;
import walle.task.Task;
import walle.task.ToDo;

/**
 * Handles reading tasks from, and writing tasks to, the memory file on disk.
 */
public class Storage {

    private final String filePath;
    private final String noteFilePath;

    /**
     * Creates a Storage backed by the given memory and notes files.
     *
     * @param filePath     path to the task memory file.
     * @param noteFilePath path to the notes file.
     */
    public Storage(String filePath, String noteFilePath) {
        this.filePath = filePath;
        this.noteFilePath = noteFilePath;
    }

    /**
     * Loads tasks previously saved to the memory file, creating the file
     * (and any missing parent directories) if it does not yet exist.
     *
     * <p>
     * Expected line formats in the memory file:
     * <ul>
     * <li>ToDo: {@code <type>;<checkmark>;<task>}</li>
     * <li>Deadline: {@code <type>;<checkmark>;<task>;<endDt>}</li>
     * <li>Event: {@code <type>;<checkmark>;<task>;<startDt>;<endDt>}</li>
     * </ul>
     *
     * @return the tasks read from the memory file, and how many lines were
     *         skipped because they couldn't be parsed.
     * @throws FileNotFoundException if the memory file cannot be found.
     * @throws IOException           if the memory file cannot be created.
     */
    public TaskLoadResult load() throws FileNotFoundException, IOException {
        ArrayList<Task> memory = new ArrayList<>();
        File memFile = new File(filePath);
        ensureFileExists(memFile);

        Scanner memScanner = new Scanner(memFile);
        int skippedLines = 0;

        // Handles saved tasks one by one; a line that fails to parse (wrong field
        // count, unreadable date, unrecognised type marker) is skipped rather than
        // discarding every task that loaded fine before it.
        while (memScanner.hasNext()) {
            try {
                String[] taskData = memScanner.nextLine().split(";");
                Task task = parseTaskLine(taskData);
                if (task != null) {
                    memory.add(task);
                } else {
                    skippedLines++;
                }
            } catch (Exception e) {
                skippedLines++;
            }
        }

        memScanner.close();
        return new TaskLoadResult(memory, skippedLines);
    }

    // Creates the memory file (and any missing parent directories) if it doesn't already exist
    private void ensureFileExists(File memFile) throws IOException {
        if (!memFile.exists()) {
            File parent = memFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            memFile.createNewFile();
        }
    }

    // Parses one semicolon-separated memory-file line, or returns null if its type marker is unrecognized
    private Task parseTaskLine(String[] taskData) {
        switch (taskData[0]) {
            case "T":
                return parseToDoLine(taskData);
            case "D":
                return parseDeadlineLine(taskData);
            case "E":
                return parseEventLine(taskData);
            default:
                return null;
        }
    }

    private Task parseToDoLine(String[] taskData) {
        String task = taskData[2];
        boolean isChecked = taskData[1].equals("X");
        return new ToDo(task, isChecked);
    }

    private Task parseDeadlineLine(String[] taskData) {
        String task = taskData[2];
        boolean isChecked = taskData[1].equals("X");
        String endDt = taskData[3];
        return new Deadline(task, LocalDateTime.parse(endDt), isChecked);
    }

    private Task parseEventLine(String[] taskData) {
        String task = taskData[2];
        boolean isChecked = taskData[1].equals("X");
        String startDt = taskData[3];
        String endDt = taskData[4];
        return new Event(task, LocalDateTime.parse(startDt), LocalDateTime.parse(endDt), isChecked);
    }

    /**
     * Overwrites the memory file with the current contents of the given task list.
     *
     * @param tasks the tasks to persist.
     * @throws IOException if the memory file cannot be written to.
     */
    public void save(ArrayList<Task> tasks) throws IOException {
        String content = tasks.stream()
                .map(Task::getMemoryFormat)
                .collect(Collectors.joining(System.lineSeparator()));

        FileWriter memFw = new FileWriter(filePath);
        // Collectors.joining only places the separator *between* lines, but the original
        // line-by-line loop wrote one after every line (including the last); an empty task
        // list must still produce an empty file, so only add the trailing separator when
        // there's actually content.
        if (!content.isEmpty()) {
            memFw.write(content + System.lineSeparator());
        }
        memFw.close();
    }

    /**
     * Loads notes previously saved to the notes file, creating the file
     * (and any missing parent directories) if it does not yet exist.
     *
     * <p>Expected line format: {@code N;note text}
     *
     * @return the notes read from the notes file, and how many lines were
     *         skipped because they couldn't be parsed.
     * @throws FileNotFoundException if the notes file cannot be found.
     * @throws IOException           if the notes file cannot be created.
     */
    public NoteLoadResult loadNotes() throws FileNotFoundException, IOException {
        ArrayList<Note> notes = new ArrayList<>();
        File noteFile = new File(noteFilePath);
        ensureFileExists(noteFile);

        Scanner noteScanner = new Scanner(noteFile);
        int skippedLines = 0;

        while (noteScanner.hasNext()) {
            try {
                // Limit to 2 parts so a semicolon in the note's own text isn't mistaken
                // for another field.
                String[] noteData = noteScanner.nextLine().split(";", 2);
                if (noteData[0].equals("N")) {
                    notes.add(new Note(noteData[1]));
                } else {
                    skippedLines++;
                }
            } catch (Exception e) {
                skippedLines++;
            }
        }

        noteScanner.close();
        return new NoteLoadResult(notes, skippedLines);
    }

    /**
     * Overwrites the notes file with the current contents of the given note list.
     *
     * @param notes the notes to persist.
     * @throws IOException if the notes file cannot be written to.
     */
    public void saveNotes(ArrayList<Note> notes) throws IOException {
        String content = notes.stream()
                .map(Note::getMemoryFormat)
                .collect(Collectors.joining(System.lineSeparator()));

        FileWriter noteFw = new FileWriter(noteFilePath);
        if (!content.isEmpty()) {
            noteFw.write(content + System.lineSeparator());
        }
        noteFw.close();
    }

}
