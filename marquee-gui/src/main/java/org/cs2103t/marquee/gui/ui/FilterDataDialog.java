package org.cs2103t.marquee.gui.ui;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

import org.cs2103t.marquee.gui.FilterData;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.StackPane;

/**
 * Dialog for setting filter criteria.
 */
public class FilterDataDialog extends Dialog<FilterData> {
    private static class FilterDataDialogView extends DialogPane {

        private final FilterData filterData;

        private final TextFormatter<LocalDateTime> startFormatter =
                new TextFormatter<>(TaskEditorView.DATETIME_STRING_CONVERTER);
        private final TextFormatter<LocalDateTime> endFormatter =
                new TextFormatter<>(TaskEditorView.DATETIME_STRING_CONVERTER);

        @FXML
        private CheckBox mark;
        @FXML
        private TextArea description;
        @FXML
        private StackPane tagsContainer;
        @FXML
        private TextField start;
        @FXML
        private TextField end;

        private final TagListView tags;
        private final AtomicBoolean isUpdatingMark = new AtomicBoolean(false);

        public FilterDataDialogView(FilterData filterData) throws IOException {
            this.filterData = filterData;

            tags = new TagListView();
            tags.setAddAllowed(true);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/FilterDataDialogView.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
            setOnMousePressed(event -> requestFocus());
        }

        @FXML
        void initialize() {
            filterData.markProperty().addListener((_, _, isMarked) -> {
                if (!isUpdatingMark.get()) {
                    isUpdatingMark.set(true);
                    if (isMarked == null) {
                        mark.setIndeterminate(true);
                        isUpdatingMark.set(false);
                    } else {
                        mark.setSelected(isMarked);
                    }
                }
            });
            if (filterData.getMark() == null) {
                mark.setIndeterminate(true);
            } else {
                mark.setIndeterminate(false);
                mark.setSelected(filterData.getMark());
            }
            mark.selectedProperty().addListener((_, _, isMarked) -> {
                isUpdatingMark.set(true);
                filterData.setMark(isMarked);
                isUpdatingMark.set(false);
            });
            mark.indeterminateProperty().addListener((_, _, isIgnored) -> {
                if (!isUpdatingMark.get()) {
                    isUpdatingMark.set(true);
                    if (isIgnored) {
                        filterData.setMark(null);
                        isUpdatingMark.set(false);
                    }
                }
            });

            description.textProperty().bindBidirectional(filterData.descriptionProperty());
            tags.itemsProperty().bindBidirectional(filterData.tagsProperty());
            startFormatter.valueProperty().bindBidirectional(filterData.startProperty());
            endFormatter.valueProperty().bindBidirectional(filterData.endProperty());

            tagsContainer.getChildren().setAll(tags);
            start.setTextFormatter(startFormatter);
            end.setTextFormatter(endFormatter);
        }

        public FilterData getFilterData() {
            return filterData;
        }
    }
    private final FilterDataDialogView dialogPane;

    /**
     * Creates a {@code FilterDataDialog}.
     */
    public FilterDataDialog(FilterData initialFilter) throws IOException {
        setTitle("Search...");
        dialogPane = new FilterDataDialogView(initialFilter);

        setDialogPane(dialogPane);
        setResultConverter(response -> {
            if (response.equals(ButtonType.OK)) {
                return dialogPane.getFilterData();
            } else {
                return null;
            }
        });
    }
}
