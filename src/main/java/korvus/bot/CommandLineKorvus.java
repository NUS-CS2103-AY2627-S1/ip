package korvus.bot;

import java.io.InputStream;
import java.io.PrintStream;

import korvus.ui.CommandLineUI;

/**
 * The main class containing logic for Korvus Command Line App.
 */
public class CommandLineKorvus extends Korvus {
    private static String banner =
            """
              /|
             / | _ ____--___   ___   _  ____
             | |/ |    |  __| / / | | |/ __/
             |   <| [] | | \\ ' /| |_| |\\__ \\
             |_/\\_|____|_|  \\_/  \\__,_|\\___/
            """;

    /**
     * Returns a default instance of the Korvus object.
     */
    public CommandLineKorvus() {
        this(System.in, System.out, "");
    }

    /**
     * Returns an instance of the Korvus object, with the provided parameters.
     *
     * @param input Input Stream that user inputs from.
     * @param output Output Stream that Korvus writes to.
     * @param storagePath Relative path to the directory with files for Korvus to use.
     */
    public CommandLineKorvus(InputStream input, PrintStream output, String storagePath) {
        super(new CommandLineUI(input, output), storagePath);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void initialise() {
        ui.divider();
        ui.rawPrint(banner);

        super.initialise();

        this.updateLineLength();

        this.beginListen();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void processUserMessage(String msg) {
        super.processUserMessage(msg);
        ui.divider();
    }

    /**
     * Updates the maximum line length of the Command Line UI.
     */
    private void updateLineLength() {
        CommandLineUI clUi = ((CommandLineUI) ui);
        clUi.setMaxLength(Integer.parseInt(this.config.getValue("commandline_length")));
    }

    /**
     * Starts listening for user inputs.
     */
    private void beginListen() {
        while (isActive) {
            String userReply = ((CommandLineUI) ui).listen();
            processUserMessage(userReply);
        }
    }
}
