package wally;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Runs a chatbot that stores and displays tasks.
 */
public class Wally {
    private final Tasklist tasks = new Tasklist();
    private final Save save;

    /** Creates a chatbot using the default save file. */
    public Wally() {
        save = new Save(tasks);
    }

    /** Creates a chatbot using an isolated save file. */
    Wally(Path saveFile) {
        save = new Save(tasks, saveFile);
    }

    /** Returns the saved background colour, or light blue when no valid setting exists. */
    public String getBackgroundColour() {
        return save.loadBackground();
    }

    /**
     * Validates and persists a background choice before the GUI applies it.
     *
     * @param colour User-provided colour name.
     * @return Validated CSS colour name.
     * @throws InvalidBackgroundColourException If the colour is unsupported.
     * @throws IOException If the setting cannot be saved.
     */
    public String changeBackgroundColour(String colour) throws InvalidBackgroundColourException, IOException {
        String background = Parser.parseBackgroundColour(colour);
        save.saveBackground(background);
        return background;
    }

    /**
     * Returns a greeting with today's local date followed by the saved task schedule.
     *
     * @return Startup message to display when the GUI opens.
     */
    public String getStartupMessage() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH));
        return "Welcome back, today is " + date + "\n\n" + Parser.processCommand("schedule", tasks);
    }

    /**
     * Generates a response for the user's chat message.
     */
    public String getResponse(String userInput) {
        String output = Parser.processCommand(userInput, tasks);
        save.writeToSave(tasks);
        return output;
    }
}
