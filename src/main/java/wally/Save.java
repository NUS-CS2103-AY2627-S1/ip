package wally;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Saves changes to the task list and loads it when the chatbot starts.
 */
public class Save {
    private static final String SAVE_FILE_PATH = "./wally/Saves/save.txt";
    private final Path saveFile;

    /**
     * Loads the task list stored in Saves/save.txt.
     * If the file does not exist, creates one.
     *
     * @param tasklist Task list to load into.
     */
    public Save(Tasklist tasklist) {
        this(tasklist, Path.of(SAVE_FILE_PATH));
    }

    /** Loads tasks from a specified file, allowing isolated storage in tests. */
    Save(Tasklist tasklist, Path saveFile) {
        this.saveFile = saveFile;
        if (Files.isRegularFile(saveFile)) {
            try (Stream<String> lines = Files.lines(saveFile)) {
                lines.forEach(entry -> Parser.parseSavedTask(entry, tasklist));
            } catch (IOException e) {
                System.out.println("Exception caught: " + e);
            }
        } else {
            try {
                Files.createDirectories(saveFile.toAbsolutePath().getParent());
                Files.createFile(saveFile);
            } catch (IOException e) {
                System.out.println("Exception caught: " + e);
            }
        }
    }

    /**
     * Overwrites the save file with the contents of the task list.
     *
     * @param tasklist Task list to save.
     */
    public void writeToSave(Tasklist tasklist) {
        String contents = "";
        for (int i = 0; i < tasklist.getSize(); i++) {
            if (tasklist.getTask(i + 1).isCompleted()) {
                contents += "[X] ";
            }
            contents += tasklist.getTask(i + 1).getCommand();
            contents += "\n";
        }
        try {
            Files.writeString(saveFile, contents);
        } catch (IOException e) {
            System.out.println("Exception caught: " + e);
        }
    }

    /**
     * Loads a supported background colour, falling back to light blue if
     * unavailable.
     */
    public String loadBackground() {
        Path file = saveFile.resolveSibling("background.txt");
        if (!Files.isRegularFile(file)) {
            return "lightblue";
        }
        try {
            return Parser.parseBackgroundColour(Files.readString(file));
        } catch (IOException | InvalidBackgroundColourException e) {
            return "lightblue";
        }
    }

    /**
     * Saves a validated background colour beside the task file.
     *
     * @param colour Supported colour in CSS form.
     * @throws IOException If the setting cannot be written.
     */
    public void saveBackground(String colour) throws IOException {
        Files.writeString(saveFile.resolveSibling("background.txt"),
                colour.equals("lightblue") ? "light blue" : colour);
    }
}
