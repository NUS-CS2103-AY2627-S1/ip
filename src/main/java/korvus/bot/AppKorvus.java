package korvus.bot;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;
import korvus.ui.AppUI;

/**
 * The main class containing logic for Korvus GUI App.
 */
public class AppKorvus extends Korvus {

    private static Duration EXIT_TIMEOUT = Duration.millis(2000);

    /**
     * Returns an instance of the Korvus object, with the provided parameters.
     *
     * @param ui UI tagged to the Korvus instance.
     * @param storagePath Relative path to the directory with files for Korvus to use.
     */
    public AppKorvus(AppUI ui, String storagePath) {
        super(ui, storagePath);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void processUserMessage(String msg) {
        super.processUserMessage(msg);

        if (!this.isActive) {
            exitProgram();
        }
    }

    /**
     * Exits the Korvus program.
     */
    private void exitProgram() {
        Timeline exitTimeLine = new Timeline(new KeyFrame(EXIT_TIMEOUT, _ -> Platform.exit()));
        exitTimeLine.play();
    }
}
