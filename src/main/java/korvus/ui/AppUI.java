package korvus.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import korvus.bot.AppKorvus;
import korvus.views.MainView;

/**
 * Main GUI for Korvus.
 */
public class AppUI extends Application implements UI {
    private static int START_WIDTH = 600;
    private static int START_HEIGHT = 800;
    private static int MIN_WIDTH = 400;
    private static int MIN_HEIGHT = 200;
    private static String TITLE = "Korvus - a Task Tracking App!";
    private static String ICON_IMAGE = "/images/icon.jpg";

    private Image icon = new Image(this.getClass().getResourceAsStream(ICON_IMAGE));

    private AppKorvus korvus;
    private MainView mainView;

    /**
     * @inheritDoc
     */
    @Override
    public void start(Stage stage) throws Exception {
        this.korvus = new AppKorvus(this, "data/");
        this.mainView = new MainView(korvus);
        this.korvus.initialise();

        Scene scene = new Scene(mainView, START_WIDTH, START_HEIGHT);
        stage.setScene(scene);

        this.doWindowSettings(stage);

        stage.show();
    }

    /**
     * Adjusts the setting for the main Korvus Window.
     *
     * @param stage Windows to apply settings to.
     */
    private void doWindowSettings(Stage stage) {
        stage.setTitle(TITLE);
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);

        stage.getIcons().add(icon);
    }

    /**
     * Sends a message to the application from Korvus.
     *
     * @param msg Message from Korvus.
     */
    @Override
    public void say(String msg) {
        rawPrint(msg);
    }

    @Override
    public void divider() {
        return;
    }

    /**
     * Sends a message to the application from Korvus.
     *
     * @param msg Message from Korvus.
     */
    @Override
    public void rawPrint(String msg) {
        mainView.handleKorvusMessage(msg);
    }
}
