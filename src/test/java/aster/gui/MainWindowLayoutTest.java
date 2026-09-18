package aster.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Tests the layouts the chat window is built from.
 *
 * <p>The layouts are read as plain XML, so nothing here starts JavaFX. That checks what the
 * layouts declare, such as sizes and the controls the window's code relies on, without a
 * display; how the window actually looks is checked by hand.
 */
class MainWindowLayoutTest {
    private static final String FXML_NAMESPACE = "http://javafx.com/fxml/1";
    private static final String WINDOW_LAYOUT = "/view/MainWindow.fxml";
    private static final String COLUMN_WIDTH = "680.0";
    private static final String MARK_REFERENCE = "../images/aster.png";

    @Test
    void layout_conversationColumn_isCentredAndCappedAt680()
            throws IOException, ParserConfigurationException, SAXException {
        Document layout = readLayout(WINDOW_LAYOUT);
        Element conversation = findById(layout, "dialogContainer");
        Element column = ownerOf(conversation);
        Element scrollPane = ownerOf(column);

        assertEquals("VBox", conversation.getTagName());
        assertEquals(COLUMN_WIDTH, conversation.getAttribute("maxWidth"),
                "the conversation must stop at 680 pixels");
        assertTrue(hasStyleClass(conversation, "dialog-container"));
        assertEquals("StackPane", column.getTagName());
        assertEquals("conversationColumn", column.getAttributeNS(FXML_NAMESPACE, "id"));
        assertEquals("TOP_CENTER", column.getAttribute("alignment"), "the conversation must be centred");
        assertEquals("ScrollPane", scrollPane.getTagName());
        assertEquals("scrollPane", scrollPane.getAttributeNS(FXML_NAMESPACE, "id"));
        assertEquals("true", scrollPane.getAttribute("fitToWidth"));
        assertEquals("NEVER", scrollPane.getAttribute("hbarPolicy"));
    }

    @Test
    void layout_headerAndComposer_areCappedToColumnWidth()
            throws IOException, ParserConfigurationException, SAXException {
        Document layout = readLayout(WINDOW_LAYOUT);

        for (String styleClass : new String[] {"header-content", "composer-content"}) {
            Element content = findByStyleClass(layout, styleClass);
            assertEquals("HBox", content.getTagName());
            assertEquals(COLUMN_WIDTH, content.getAttribute("maxWidth"),
                    styleClass + " must line up with the 680 pixel conversation");
        }
    }

    @Test
    void layout_header_showsNameAndSubtitle() throws IOException, ParserConfigurationException, SAXException {
        Document layout = readLayout(WINDOW_LAYOUT);

        Element title = findByStyleClass(layout, "header-title");
        Element subtitle = findByStyleClass(layout, "header-subtitle");
        assertEquals("Label", title.getTagName());
        assertEquals("Aster", title.getAttribute("text"));
        assertEquals("Label", subtitle.getTagName());
        assertEquals("Plan tasks. Stay on track.", subtitle.getAttribute("text"));
    }

    @Test
    void layout_controllerControls_keepTheirIdsAndHandlers()
            throws IOException, ParserConfigurationException, SAXException {
        Document layout = readLayout(WINDOW_LAYOUT);

        Element input = findById(layout, "userInput");
        assertEquals("TextField", input.getTagName());
        assertEquals("#handleUserInput", input.getAttribute("onAction"));
        assertEquals("Type a command, then press Enter", input.getAttribute("promptText"));
        Element send = findById(layout, "sendButton");
        assertEquals("Button", send.getTagName());
        assertEquals("Send", send.getAttribute("text"));
        assertEquals("#handleUserInput", send.getAttribute("onAction"));
    }

    @Test
    void layout_headerMark_resolvesToPackagedPicture()
            throws IOException, ParserConfigurationException, SAXException, URISyntaxException {
        Document layout = readLayout(WINDOW_LAYOUT);
        Element mark = findById(layout, "headerMark");
        Element image = (Element) mark.getElementsByTagName("Image").item(0);
        assertNotNull(image, "the header mark must show a picture");

        assertEquals("@" + MARK_REFERENCE, image.getAttribute("url"));
        URL layoutUrl = MainWindowLayoutTest.class.getResource(WINDOW_LAYOUT);
        // Resolved against the layout, as the layout loader resolves an @ reference.
        Path picture = Path.of(layoutUrl.toURI().resolve(MARK_REFERENCE));
        assertTrue(Files.isRegularFile(picture),
                "the header mark must exist where the layout refers to it: " + picture);
    }

    @Test
    void dialogLayout_picture_is32PixelsAndSmooth() throws IOException, ParserConfigurationException, SAXException {
        Element picture = findById(readLayout("/view/DialogBox.fxml"), "displayPicture");

        assertEquals("ImageView", picture.getTagName());
        assertEquals("32.0", picture.getAttribute("fitWidth"), "the picture must be shown 32 pixels wide");
        assertEquals("32.0", picture.getAttribute("fitHeight"), "the picture must be shown 32 pixels high");
        assertEquals("true", picture.getAttribute("smooth"), "the picture must be scaled smoothly");
    }

    /**
     * Returns a layout on the class path, parsed as XML.
     *
     * @param path the layout's path on the class path.
     * @return the parsed layout.
     */
    private static Document readLayout(String path) throws IOException, ParserConfigurationException, SAXException {
        URL layout = MainWindowLayoutTest.class.getResource(path);
        assertNotNull(layout, "the layout must be on the class path: " + path);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        try (InputStream in = layout.openStream()) {
            return factory.newDocumentBuilder().parse(in);
        }
    }

    /**
     * Returns the element a layout gives the given {@code fx:id}.
     *
     * @param layout the parsed layout.
     * @param id the {@code fx:id} to look for.
     * @return the element with that {@code fx:id}.
     */
    private static Element findById(Document layout, String id) {
        NodeList elements = layout.getElementsByTagName("*");
        for (int i = 0; i < elements.getLength(); i++) {
            Element element = (Element) elements.item(i);
            if (id.equals(element.getAttributeNS(FXML_NAMESPACE, "id"))) {
                return element;
            }
        }
        throw new AssertionError("the layout has no element with fx:id " + id);
    }

    /**
     * Returns the first element a layout gives the given style class.
     *
     * @param layout the parsed layout.
     * @param styleClass the style class to look for.
     * @return the element with that style class.
     */
    private static Element findByStyleClass(Document layout, String styleClass) {
        NodeList elements = layout.getElementsByTagName("*");
        for (int i = 0; i < elements.getLength(); i++) {
            Element element = (Element) elements.item(i);
            if (hasStyleClass(element, styleClass)) {
                return element;
            }
        }
        throw new AssertionError("the layout has no element with style class " + styleClass);
    }

    /**
     * Returns whether an element's {@code styleClass} attribute names the given class.
     *
     * @param element the element to check.
     * @param styleClass the style class to look for.
     * @return {@code true} if the element carries that style class.
     */
    private static boolean hasStyleClass(Element element, String styleClass) {
        return Arrays.asList(element.getAttribute("styleClass").split("[,\\s]+")).contains(styleClass);
    }

    /**
     * Returns the control an element is placed in, skipping the {@code children} or
     * {@code content} element that only groups what the control holds.
     *
     * @param element the element whose owner is wanted.
     * @return the element for the control that holds it.
     */
    private static Element ownerOf(Element element) {
        Element parent = (Element) element.getParentNode();
        String name = parent.getTagName();
        return name.equals("children") || name.equals("content") ? (Element) parent.getParentNode() : parent;
    }
}
