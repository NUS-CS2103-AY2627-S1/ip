package yuno.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

class FxmlConfigurationTest {
    private static final String FXML_NAMESPACE = "http://javafx.com/fxml/1";

    @Test
    void mainWindowFxml_requiredControllerFieldsAndHandlers_areConnected()
            throws IOException, ParserConfigurationException, SAXException {
        Document document = loadFxml("/view/MainWindow.fxml");
        Element root = document.getDocumentElement();

        assertEquals("AnchorPane", root.getTagName());
        assertEquals("yuno.ui.MainWindow", root.getAttributeNS(FXML_NAMESPACE, "controller"));
        assertEquals("@../css/main.css", root.getAttribute("stylesheets"));
        assertEquals(
                Set.of("scrollPane", "dialogContainer", "userInput", "sendButton", "headerImage"),
                getFxmlIds(document));
        assertTrue(hasElementWithAction(document, "TextField", "#handleUserInput"));
        assertTrue(hasElementWithAction(document, "Button", "#handleUserInput"));
    }

    @Test
    void dialogBoxFxml_requiredRootAndFields_areConnected()
            throws IOException, ParserConfigurationException, SAXException {
        Document document = loadFxml("/view/DialogBox.fxml");
        Element root = document.getDocumentElement();

        assertEquals("fx:root", root.getTagName());
        assertEquals("javafx.scene.layout.HBox", root.getAttribute("type"));
        assertEquals("BOTTOM_RIGHT", root.getAttribute("alignment"));
        assertEquals("@../css/dialog-box.css", root.getAttribute("stylesheets"));
        assertEquals(Set.of("text", "displayPicture"), getFxmlIds(document));
    }

    private Document loadFxml(String resourcePath)
            throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
            assertTrue(inputStream != null, "FXML resource should exist: " + resourcePath);
            return factory.newDocumentBuilder().parse(inputStream);
        }
    }

    private Set<String> getFxmlIds(Document document) {
        Set<String> fxmlIds = new HashSet<>();
        NodeList elements = document.getElementsByTagName("*");
        for (int index = 0; index < elements.getLength(); index++) {
            Element element = (Element) elements.item(index);
            if (element.hasAttributeNS(FXML_NAMESPACE, "id")) {
                fxmlIds.add(element.getAttributeNS(FXML_NAMESPACE, "id"));
            }
        }
        return fxmlIds;
    }

    private boolean hasElementWithAction(Document document, String elementName, String action) {
        NodeList elements = document.getElementsByTagName(elementName);
        for (int index = 0; index < elements.getLength(); index++) {
            Element element = (Element) elements.item(index);
            if (action.equals(element.getAttribute("onAction"))) {
                return true;
            }
        }
        return false;
    }
}
