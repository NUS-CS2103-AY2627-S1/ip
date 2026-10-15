package anders;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;

/** Draws a lantern or compass with vector shapes that stay sharp at any display scale. */
public class ProfileIcon extends StackPane {
    private static final String LANTERN_FRAME = "M10 7 V5 A2 2 0 0 1 14 5 V7 "
            + "M7 10 L9 7 H15 L17 10 V20 H7 Z M6 20 H18 M7 10 H17";
    private static final String LANTERN_LIGHT = "M12 11 C12 14 9 14 10 17 "
            + "C11 19 14 18 14 16 C14 14 12 14 12 11 Z";
    private static final String COMPASS_FRAME = "M12 3 A9 9 0 1 1 12 21 A9 9 0 1 1 12 3 Z";
    private static final String COMPASS_NEEDLE = "M16 7 L13 14 L8 17 L11 10 Z";

    private final SVGPath frame = new SVGPath();
    private final SVGPath light = new SVGPath();

    /** Creates Anders's lantern icon, including when loaded from FXML. */
    public ProfileIcon() {
        getStyleClass().add("profile-icon");
        setMinSize(32, 32);
        setPrefSize(32, 32);
        setMaxSize(32, 32);
        frame.getStyleClass().add("icon-frame");
        light.getStyleClass().add("icon-light");
        Pane drawing = new Pane(frame, light);
        drawing.setMinSize(24, 24);
        drawing.setPrefSize(24, 24);
        drawing.setMaxSize(24, 24);
        getChildren().add(drawing);
        setUser(false);
        setMouseTransparent(true);
    }

    /** Selects the user's compass or Anders's lantern. */
    public void setUser(boolean isUser) {
        frame.setContent(isUser ? COMPASS_FRAME : LANTERN_FRAME);
        light.setContent(isUser ? COMPASS_NEEDLE : LANTERN_LIGHT);
        setAccessibleText(isUser ? "Your compass" : "Anders's lantern");
    }
}
