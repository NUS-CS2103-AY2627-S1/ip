package luckynoslacky.luckyui.gui;

import java.io.IOException;
import java.util.function.Supplier;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import luckynoslacky.LuckyNoSlacky;

/**
 * Starts the LuckyNoSlacky JavaFX interface.
 */
public final class LuckyNoGui extends Application {
    private static final double WINDOW_WIDTH = 400.0;
    private static final double WINDOW_HEIGHT = 600.0;
    private static final double MIN_WINDOW_WIDTH = 320.0;
    private static final double MIN_WINDOW_HEIGHT = 480.0;
    private final Supplier<LuckyNoSlacky> chatbotFactory;

    /**
     * Creates the JavaFX application using the production chatbot factory.
     */
    public LuckyNoGui() {
        this(LuckyNoSlacky::new);
    }

    /**
     * Creates the JavaFX application with a supplied chatbot factory.
     *
     * <p>The factory provides a deterministic persistence seam for GUI tests
     * while the public no-argument constructor retains normal startup
     * behavior.</p>
     *
     * @param chatbotFactory factory used to create the chatbot for the window
     * @throws IllegalArgumentException if {@code chatbotFactory} is null
     */
    LuckyNoGui(Supplier<LuckyNoSlacky> chatbotFactory) {
        if (chatbotFactory == null) {
            throw new IllegalArgumentException(
                    "Chatbot factory cannot be null.");
        }
        this.chatbotFactory = chatbotFactory;
    }

    /**
     * Creates and displays the main window.
     *
     * @param stage primary JavaFX window
     * @throws IOException if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        loadPersonalityFonts();
        FXMLLoader loader = new FXMLLoader(
                LuckyNoGui.class.getResource("/view/MainWindow.fxml"));
        Parent root = loader.load();
        addPersonalityStylesheet(root);
        MainWindow controller = loader.getController();
        controller.setChatbot(chatbotFactory.get());

        stage.setTitle("LuckyNoSlacky");
        stage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
        stage.setMinWidth(MIN_WINDOW_WIDTH);
        stage.setMinHeight(MIN_WINDOW_HEIGHT);
        stage.setWidth(WINDOW_WIDTH);
        stage.setHeight(WINDOW_HEIGHT);
        stage.show();
    }

    /** Loads optional personality fonts before FXML applies the CSS selectors. */
    private static void loadPersonalityFonts() {
        loadFont("/fonts/PatrickHand-Regular.ttf");
        loadFont("/fonts/RobotoMono-Regular.ttf");
    }

    /** Loads one bundled font, allowing JavaFX to use a fallback if unavailable. */
    private static void loadFont(String resourcePath) {
        java.net.URL resource = LuckyNoGui.class.getResource(resourcePath);
        if (resource != null) {
            Font.loadFont(resource.toExternalForm(), 16.0);
        }
    }

    /** Adds the personality stylesheet after the main stylesheet is loaded. */
    private static void addPersonalityStylesheet(Parent root) {
        java.net.URL resource = LuckyNoGui.class.getResource(
                "/css/personality.css");
        if (resource != null) {
            root.getStylesheets().add(resource.toExternalForm());
        }
    }
}
