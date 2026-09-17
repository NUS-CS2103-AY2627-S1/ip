package panda.gui;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import panda.Panda;

/**
 * Starts Panda's JavaFX graphical user interface.
 */
public class Main extends Application {

    /**
     * Loads Panda's main window and connects it to Panda through pipe streams.
     *
     * @param stage Primary JavaFX stage.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            MainWindow mainWindow = fxmlLoader.getController();

            stage.setScene(new Scene(root));
            stage.setTitle("Panda");
            stage.setOnCloseRequest(event -> {
                event.consume();
                mainWindow.requestExit();
            });
            connectGraphicalInterfaceToPanda(mainWindow);
            stage.show();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to start Panda's graphical interface.", exception);
        }
    }

    /**
     * Creates the pipe streams used to exchange commands and responses with Panda.
     *
     * @param mainWindow Graphical interface to connect to Panda.
     * @throws IOException If the pipe streams cannot be connected.
     */
    private void connectGraphicalInterfaceToPanda(MainWindow mainWindow) throws IOException {
        PipedInputStream pandaCommandInputStream = new PipedInputStream();
        PipedOutputStream graphicalCommandOutputStream = new PipedOutputStream(pandaCommandInputStream);

        PipedOutputStream pandaResponseOutputStream = new PipedOutputStream();
        PipedInputStream graphicalResponseInputStream = new PipedInputStream(pandaResponseOutputStream);

        Panda panda = Panda.createForGraphicalInterface(
                pandaCommandInputStream, pandaResponseOutputStream);
        mainWindow.connectToPandaStreams(
                graphicalCommandOutputStream, graphicalResponseInputStream);
        Thread.ofVirtual()
                .name("panda-command-processor")
                .start(panda::processCommandsUntilInputCloses);
    }
}
