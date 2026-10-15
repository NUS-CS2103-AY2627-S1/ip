package niulai.gui;

import java.util.stream.Collectors;

import niulai.service.Ui;

/**
 * Captures chatbot output for display in the JavaFX interface.
 *
 * <p>The command classes continue to write through the same {@link Ui} methods as the command-line
 * interface. This class only changes where that output is sent.</p>
 */
public class GuiUi extends Ui {
    /** Stores output until the JavaFX controller turns it into a dialog box. */
    private final StringBuilder output;

    /** Creates an output collector for the JavaFX interface. */
    public GuiUi() {
        output = new StringBuilder();
    }

    /**
     * Returns the most recent response and clears the captured output.
     *
     * <p>Command-line separators and indentation are removed because the GUI supplies its own
     * visual separation and spacing.</p>
     *
     * @return the captured response formatted for a chat dialog
     */
    public String consumeOutput() {
        String capturedOutput = output.toString();
        output.setLength(0);

        return capturedOutput.lines()
                .map(String::strip)
                .filter(line -> !line.isBlank() && !line.matches("_+"))
                .collect(Collectors.joining("\n"));
    }

    /** Captures output instead of writing it to standard output. */
    @Override
    protected void writeLine(String text) {
        output.append(text).append(System.lineSeparator());
    }
}
