package duke.gui;

import duke.CommandHelp;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

/** Presents the shared command reference as small, responsive groups of examples. */
final class HelpView extends VBox {
    private static final double TWO_COLUMN_WIDTH = 680;

    /** Builds point-form help without duplicating the command definitions. */
    HelpView() {
        super(12);
        setPrefWidth(720);
        getStyleClass().add("help-view");
        setAccessibleText(CommandHelp.getText());
        Label title = label("Commands at a glance", "help-title");
        Label intro = label("Try an example below. Change the details to suit you.", "help-intro");
        TilePane groups = new TilePane(12, 12);
        groups.setMinWidth(0);
        groups.setPrefColumns(2);
        groups.setTileAlignment(Pos.TOP_LEFT);
        groups.prefTileWidthProperty().bind(Bindings.when(widthProperty().greaterThanOrEqualTo(TWO_COLUMN_WIDTH))
                .then(widthProperty().subtract(12).divide(2)).otherwise(widthProperty()));
        for (CommandHelp.Section section : CommandHelp.getSections()) {
            groups.getChildren().add(createSection(section));
        }
        getChildren().addAll(title, intro, groups, label(CommandHelp.getNotes(), "help-notes"));
    }

    /** Keeps each explanation beside its example, including at narrow window widths. */
    private VBox createSection(CommandHelp.Section section) {
        VBox card = new VBox(10);
        card.setMinWidth(0);
        card.setMaxWidth(Double.MAX_VALUE);
        card.getStyleClass().add("help-section");
        card.getChildren().add(label(section.title(), "help-section-title"));
        for (CommandHelp.Entry entry : section.entries()) {
            Label example = label(entry.example(), "help-example");
            example.setAccessibleText("Example: " + entry.example() + ". Format: " + entry.format());
            card.getChildren().add(new VBox(3, label("• " + entry.description(), "help-description"), example));
        }
        return card;
    }

    /** Creates wrapping text that can shrink with its card instead of clipping. */
    private Label label(String text, String style) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMinWidth(0);
        label.setMaxWidth(Double.MAX_VALUE);
        label.getStyleClass().add(style);
        return label;
    }
}
