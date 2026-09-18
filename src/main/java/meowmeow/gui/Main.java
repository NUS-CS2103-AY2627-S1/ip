package meowmeow.gui;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import meowmeow.Meowmeow;
import meowmeow.ui.MessageStyle;
import meowmeow.ui.Ui;

/**
 * The JavaFX entry point. Loads the main window from FXML, hands it a
 * {@link Meowmeow} instance to talk to, applies the GUI's stylesheet and
 * font, and shows the stage.
 *
 * <p>Unlike the console entry point ({@link meowmeow.Meowmeow#main}), the GUI
 * seeds its {@link Ui} with a real random source rather than a fixed one:
 * only the console's output is asserted byte-for-byte by the console UI
 * test plan.
 */
public class Main extends Application {

    /**
     * Font families tried in order for the GUI's body text, most preferred
     * first. JavaFX CSS does not reliably fall through a comma-separated
     * {@code -fx-font-family} list the way a browser does, so the first
     * family actually installed is resolved here at start-up and applied
     * directly, instead of hoping the stylesheet's list works everywhere.
     */
    private static final String[] FONT_PREFERENCES = {"Avenir Next", "Segoe UI", "Helvetica Neue", "Arial"};

    private final Meowmeow meowmeow =
            new Meowmeow(new Ui(MessageStyle.RICH, new Random()), "data", "meowmeow.txt");

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        BorderPane root = fxmlLoader.load();
        root.setStyle("-fx-font-family: '" + resolveFontFamily() + "';");

        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("/css/meowmeow.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Meowmeow — reluctantly organised");
        stage.setMinHeight(220);
        stage.setMinWidth(417);
        stage.setResizable(true);
        loadIcon().ifPresent(icon -> stage.getIcons().add(icon));

        fxmlLoader.<MainWindow>getController().setMeowmeow(meowmeow);
        stage.show();
    }

    /**
     * Returns the first font family in {@link #FONT_PREFERENCES} that is
     * actually installed, or the platform default if none of them are.
     */
    private String resolveFontFamily() {
        List<String> available = Font.getFamilies();
        for (String preferred : FONT_PREFERENCES) {
            if (available.contains(preferred)) {
                return preferred;
            }
        }
        return Font.getDefault().getFamily();
    }

    /**
     * Returns the window icon, or nothing if the asset isn't bundled yet.
     * The GUI still runs without one - a missing icon is cosmetic, not
     * fatal - so this reports absence rather than throwing.
     */
    private Optional<Image> loadIcon() {
        InputStream stream = Main.class.getResourceAsStream("/images/app-icon.png");
        return stream == null ? Optional.empty() : Optional.of(new Image(stream));
    }
}
