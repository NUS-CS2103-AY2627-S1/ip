package chattingheads.ui.gui;

import java.io.IOException;

import chattingheads.ChattingHeads;
import chattingheads.exception.StorageException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for ChattingHeads using FXML.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            ChattingHeads chattingHeads = new ChattingHeads();

            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane rootPane = fxmlLoader.load();
            fxmlLoader.<MainWindowController>getController().setChattingHeads(chattingHeads);
            Scene scene = new Scene(rootPane);

            stage.setTitle("Chatting Heads");
            stage.setScene(scene);
            stage.show();
        } catch (StorageException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
