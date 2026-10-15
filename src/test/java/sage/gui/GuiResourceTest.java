package sage.gui;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class GuiResourceTest {
    @Test
    void guiResources_areAvailableOnClasspath() {
        assertNotNull(Main.class.getResource("/view/MainWindow.fxml"));
        assertNotNull(DialogBox.class.getResource("/view/DialogBox.fxml"));
        assertNotNull(Main.class.getResource("/css/main.css"));
        assertNotNull(DialogBox.class.getResource("/css/dialog-box.css"));
    }

    @Test
    void guiViews_areWellFormedAndRelativeStylesheetsResolve() throws Exception {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        documentFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        for (String resourcePath : new String[]{"/view/MainWindow.fxml", "/view/DialogBox.fxml"}) {
            URL viewUrl = Main.class.getResource(resourcePath);
            assertNotNull(viewUrl);
            Document view;
            try (InputStream viewStream = viewUrl.openStream()) {
                view = documentFactory.newDocumentBuilder().parse(viewStream);
            }
            NodeList elements = view.getElementsByTagName("*");
            for (int index = 0; index < elements.getLength(); index++) {
                Element element = (Element) elements.item(index);
                if (!element.hasAttribute("stylesheets")) {
                    continue;
                }
                String stylesheet = element.getAttribute("stylesheets");
                assertTrue(stylesheet.startsWith("@"), "Stylesheets must be portable classpath resources.");
                URI stylesheetUri = viewUrl.toURI().resolve(stylesheet.substring(1));
                try (InputStream stylesheetStream = stylesheetUri.toURL().openStream()) {
                    assertTrue(stylesheetStream.read() != -1, "Referenced stylesheet must not be empty.");
                }
            }
        }
    }
}
