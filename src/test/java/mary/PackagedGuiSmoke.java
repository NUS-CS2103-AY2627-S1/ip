package mary;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.stage.Stage;
import mary.ui.MaryApplication;

/**
 * Opens and renders the real window using only the packaged application's dependencies.
 */
public class PackagedGuiSmoke {
    /**
     * Checks GUI startup in an isolated working directory, then closes the window.
     *
     * @param args unused arguments.
     * @throws Exception if JavaFX cannot initialize or render the window.
     */
    public static void main(String[] args) throws Exception {
        Launcher.configureLocalCaches();
        FutureTask<Void> smoke = new FutureTask<>(() -> {
            Stage stage = new Stage();
            try {
                new MaryApplication().start(stage);
                stage.getScene().snapshot(null);
                if (!stage.isShowing()) {
                    throw new IllegalStateException("MARY's window did not open");
                }
                return null;
            } finally {
                stage.close();
            }
        });
        Platform.startup(smoke);
        try {
            smoke.get(15, TimeUnit.SECONDS);
            System.out.println("GUI_SMOKE_PASS: MARY opened and rendered successfully.");
        } finally {
            Platform.exit();
        }
    }
}
