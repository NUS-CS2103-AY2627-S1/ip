package com.benthecat.kachow.ui.printer;

import com.benthecat.kachow.ui.fx.DialogBox;

import javafx.scene.layout.VBox;

/**
 * Buffers Kachow's response and displays it in the graphical dialog container.
 */
public class FxPrinter implements Printer {

    private final VBox dialogContainer;
    private final StringBuilder data = new StringBuilder();
    private boolean isErrorResponse;

    /**
     * Creates a printer that appends responses to the given dialog container.
     *
     * @param dialogContainer Container that displays Kachow's responses.
     */
    public FxPrinter(VBox dialogContainer) {
        this.dialogContainer = dialogContainer;
    }

    @Override
    public void addData(String message) {
        data.append(message).append("\n");
    }

    @Override
    public void markResponseAsError() {
        isErrorResponse = true;
    }

    @Override
    public void outputData() {
        DialogBox dialogBox = isErrorResponse
                ? DialogBox.createErrorDialog(data.toString())
                : DialogBox.createKachowDialog(data.toString());
        dialogContainer.getChildren().add(dialogBox);
        data.setLength(0);
        isErrorResponse = false;
    }
}
