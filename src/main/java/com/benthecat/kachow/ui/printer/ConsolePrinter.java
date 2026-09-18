package com.benthecat.kachow.ui.printer;

/**
 * Buffers UI messages and writes them to the console.
 */
public class ConsolePrinter implements Printer {
    private static final int UI_INDENT = 4;

    private final StringBuilder data = new StringBuilder();

    @Override
    public void addData(String message) {
        String indentedMessage = message.indent(UI_INDENT)
                .replace("\n", System.lineSeparator());
        data.append(indentedMessage);
    }

    @Override
    public void outputData() {
        System.out.print(data);
        data.setLength(0);
    }
}
