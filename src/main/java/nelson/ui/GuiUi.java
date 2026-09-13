package nelson.ui;

import java.util.function.Consumer;

/** Sends Nelson's responses to a JavaFX-friendly output callback. */
public class GuiUi extends Ui {
    /** Receives one response line at a time. */
    private final Consumer<String> output;
    /** Receives error responses so the GUI can distinguish them visually. */
    private final Consumer<String> errorOutput;

    /**
     * Creates a GUI output adapter.
     *
     * @param output callback that receives normal response lines
     * @param errorOutput callback that receives error response lines
     */
    public GuiUi(Consumer<String> output, Consumer<String> errorOutput) {
        this.output = output;
        this.errorOutput = errorOutput;
    }

    /**
     * Sends a normal response line to the GUI.
     *
     * @param messages response text without console indentation
     */
    @Override
    public void show(String... messages) {
        for (String message : messages) {
            output.accept(message);
        }
    }

    /**
     * Sends an error response line to the GUI.
     *
     * @param message error text without console indentation
     */
    @Override
    public void showError(String message) {
        errorOutput.accept(message);
    }

}
