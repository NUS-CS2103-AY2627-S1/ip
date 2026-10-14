package bott.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import bott.Bott;

/**
 * The JavaFX entry point for Bott's GUI: builds the primary window and
 * wires it to a {@link Bott} instance that answers the user's commands.
 */
public class Main extends Application {

    private static final double INITIAL_WIDTH = 400;
    private static final double INITIAL_HEIGHT = 600;
    private static final double MIN_WIDTH = 300;
    private static final double MIN_HEIGHT = 400;

    private final Bott bott = new Bott();

    /**
     * Builds the primary stage: a {@link MainWindow} backed by {@link #bott}.
     *
     * @param stage Primary stage supplied by the JavaFX runtime.
     */
    @Override
    public void start(Stage stage) {
        MainWindow mainWindow = new MainWindow(bott);

        stage.setScene(new Scene(mainWindow, INITIAL_WIDTH, INITIAL_HEIGHT));
        stage.setTitle("Sergeant Bott");
        stage.setMinWidth(MIN_WIDTH);
        stage.setMinHeight(MIN_HEIGHT);
        stage.show();
    }
}
