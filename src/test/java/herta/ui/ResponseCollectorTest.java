package herta.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests response collection for graphical presentation.
 */
class ResponseCollectorTest {

    @Test
    void getOutput_emptyCollector_returnsEmptyString() {
        assertEquals("", new ResponseCollector().getOutput());
    }

    @Test
    void showMessage_multipleMessages_joinsWithPlatformSeparator() {
        ResponseCollector collector = new ResponseCollector();

        collector.showMessage("first");
        collector.showMessage("second");

        assertEquals("first" + System.lineSeparator() + "second", collector.getOutput());
    }

    @Test
    void showGoodbye_afterExistingMessage_appendsGoodbyeWithSeparator() {
        ResponseCollector collector = new ResponseCollector();
        collector.showMessage("last command");

        collector.showGoodbye();

        assertEquals("last command" + System.lineSeparator() + UiOutput.GOODBYE_MESSAGE,
                collector.getOutput());
    }

    @Test
    void responseCollector_multilineMessages_preserveOrderAndEmbeddedLineBreaks() {
        ResponseCollector collector = new ResponseCollector();
        String multilineMessage = "first line\nsecond line";

        collector.showMessage(multilineMessage);
        collector.showMessage("next message");
        collector.showGoodbye();

        assertEquals(multilineMessage + System.lineSeparator() + "next message"
                + System.lineSeparator() + UiOutput.GOODBYE_MESSAGE,
                collector.getOutput());
    }
}
