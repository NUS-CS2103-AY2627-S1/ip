package com.benthecat.kachow.ui.printer;

/**
 * Defines how Kachow accumulates and displays response text.
 */
public interface Printer {
    /** Adds one message to the current response. */
    void addData(String message);

    /** Marks the current response as an error that may need distinctive presentation. */
    default void markResponseAsError() {
        // Text-only printers can keep their existing presentation.
    }

    /** Displays the current response and prepares for the next response. */
    void outputData();
}
