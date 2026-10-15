package bob.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStream;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

/**
 * Tests the responsive sizing configured for dialog rows.
 */
public class DialogBoxLayoutTest {

    @Test
    void dialogBoxFxml_maxWidth_allowsRowToFillConversation() throws Exception {
        try (InputStream fxml = DialogBoxLayoutTest.class.getResourceAsStream("/view/DialogBox.fxml")) {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fxml);

            assertEquals(Double.toString(Double.MAX_VALUE),
                    document.getDocumentElement().getAttribute("maxWidth"));
        }
    }
}
