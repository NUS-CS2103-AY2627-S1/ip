package avon.gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.StringJoiner;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javafx.scene.layout.Region;
import javafx.scene.shape.SVGPath;

/**
 * Displays the path data from an SVG resource as a scalable JavaFX region.
 */
final class SvgIcon extends Region {
    private static final double ICON_SIZE = 26.0;
    private static final String DISALLOW_DOCTYPE_DECLARATION_FEATURE =
            "http://apache.org/xml/features/disallow-doctype-decl";

    /**
     * Creates an icon from the SVG resource at the given classpath location.
     *
     * @param resourcePath the absolute classpath location of the SVG resource.
     */
    SvgIcon(String resourcePath) {
        URL resource = Objects.requireNonNull(
                SvgIcon.class.getResource(resourcePath),
                "SVG resource not found: " + resourcePath);
        setShape(loadShape(resource));
        setScaleShape(true);
        setCenterShape(true);
        setCacheShape(true);
        setMinSize(ICON_SIZE, ICON_SIZE);
        setPrefSize(ICON_SIZE, ICON_SIZE);
        setMaxSize(ICON_SIZE, ICON_SIZE);
        getStyleClass().add("speaker-icon");
    }

    /**
     * Loads and combines the path data contained in an SVG document.
     *
     * @param resource the SVG resource to load.
     * @return a JavaFX shape containing the SVG path data.
     */
    private static SVGPath loadShape(URL resource) {
        try (InputStream inputStream = resource.openStream()) {
            DocumentBuilderFactory factory = createSecureDocumentBuilderFactory();
            Document document = factory.newDocumentBuilder().parse(inputStream);
            StringJoiner pathData = getPathData(document, resource);

            SVGPath shape = new SVGPath();
            shape.setContent(pathData.toString());
            return shape;
        } catch (IOException | ParserConfigurationException | SAXException exception) {
            throw new IllegalStateException("Unable to load SVG resource: " + resource, exception);
        }
    }

    /**
     * Returns the combined data from every valid path in an SVG document.
     *
     * @param document the parsed SVG document.
     * @param resource the resource represented by the document.
     * @return the combined SVG path data.
     */
    private static StringJoiner getPathData(Document document, URL resource) {
        NodeList paths = document.getElementsByTagName("path");
        StringJoiner pathData = new StringJoiner(" ");
        for (int index = 0; index < paths.getLength(); index++) {
            Node pathDataAttribute = paths.item(index).getAttributes().getNamedItem("d");
            if (pathDataAttribute != null) {
                pathData.add(pathDataAttribute.getNodeValue());
            }
        }
        if (pathData.length() == 0) {
            throw new IllegalArgumentException("SVG resource contains no path data: " + resource);
        }
        return pathData;
    }

    /**
     * Creates an XML parser that cannot resolve external entities or schemas.
     *
     * @return a securely configured document builder factory.
     * @throws ParserConfigurationException if secure XML processing is unavailable.
     */
    private static DocumentBuilderFactory createSecureDocumentBuilderFactory()
            throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(DISALLOW_DOCTYPE_DECLARATION_FEATURE, true);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        return factory;
    }
}
