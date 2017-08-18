package com.buschmais.jqassistant.plugin.xml.api.scanner;

import com.buschmais.jqassistant.plugin.common.api.scanner.filesystem.FileResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * Provides functionality to verify if a file resource represents a specific XML
 * document, e.g. by checking for the presence of a root element.
 */
public final class XMLFileFilter {

    private static XMLInputFactory factory ;

    private static final Logger LOGGER = LoggerFactory.getLogger(XMLFileFilter.class);

    static {
        factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
    }

    /**
     * Private constructor.
     */
    private XMLFileFilter() {
    }

    /**
     * Matches a file resource for being an XML file with a specific root
     * element.
     *
     * @param fileResource
     *            The file resource.
     * @param path
     *            The path of the file resource.
     * @param expectedRootElement
     *            The expected local name of the root element.
     * @return `true` If the file is an XML file and contains the
     *         expected root element.
     * @throws IOException
     *             If the file resource cannot be read.
     */
    public static boolean rootElementMatches(FileResource fileResource, String path, String expectedRootElement) throws IOException {
        try (InputStream stream = fileResource.createStream()) {
            XMLStreamReader reader = factory.createXMLStreamReader(stream);
            while (reader.hasNext()) {
                int event = reader.next();
                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        String rootElement = reader.getLocalName();
                        return expectedRootElement.equals(rootElement);
                }
            }
        } catch (XMLStreamException e) {
            LOGGER.warn("Cannot parse XML file '{}'.", path);
        }
        return false;

    }

    public static Boolean rootElementMatches(FileResource fileResource, String path, String expectedRootElement,
                                             String expectedNameSpace) throws IOException {
        try (InputStream stream = fileResource.createStream()) {
            XMLStreamReader reader = factory.createXMLStreamReader(stream);
            while (reader.hasNext()) {
                int event = reader.next();
                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        String rootElement = reader.getLocalName();
                        int namespaces = reader.getNamespaceCount();
                        if (expectedRootElement.equals(rootElement)) {
                            // In case of Java 8 -> for-loops are faster!
                            for (int i = 0; i < namespaces; i++) {
                                String namespaceURI = reader.getNamespaceURI(i);
                                if (expectedNameSpace.equals(namespaceURI)) {
                                    return true;
                                }
                            }
                        }
                        return false;
                }
            }
        } catch (XMLStreamException e) {
            LOGGER.warn("Cannot parse XML file '{}'.", path);
        }
        return false;
    }
}
