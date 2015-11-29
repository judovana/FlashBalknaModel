package org.fbb.balkna.model.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.fbb.balkna.Packages;
import org.fbb.balkna.model.primitives.LocalisedString;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import static org.fbb.balkna.model.utils.XmlConstants.*;
import org.w3c.dom.Element;

/**
 *
 * @author jvanek
 */
public class XmlUtils {

    public static List<Element> getDataNodes(String fileRoot, String element) throws ParserConfigurationException, SAXException, IOException {
        return getNodes(Packages.DATA, fileRoot, element);
    }

    public static List<Element> getNodes(String pkg, String fileRoot, String element) throws ParserConfigurationException, SAXException, IOException {
        List<URL> srcs = IoUtils.getFiles(pkg, fileRoot, "xml");
        List<Element> l = new ArrayList<Element>();
        for (URL src : srcs) {
            InputStream in = src.openStream();
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(in);
                NodeList exs = doc.getElementsByTagName(element);
                for (int x = 0; x < exs.getLength(); x++) {
                    Node item = exs.item(x);
                    if (item instanceof Element) {
                        l.add((Element) item);
                    }
                }
            } finally {
                // in.close();
            }
        }
        return l;
    }

    public static List<Element> getRealChilds(Node node) {
        List<Element> interlayer = new ArrayList<Element>();
        NodeList l = node.getChildNodes();
        for (int i = 0; i < l.getLength(); i++) {
            Node n = l.item(i);
            if (n instanceof Element) {
                interlayer.add(((Element) n));
            }
        }
        return interlayer;
    }

    public static String getDefaultName(Element n) {
        return getNonLocalised(NAME, n);

    }

    public static String getLocaleAtt(Element n) {
        String s = n.getAttribute(LOCALE);
        if (s == null) {
            return s;
        }
        if (s.trim().isEmpty()) {
            return null;
        }
        return s;
    }

    public static List<LocalisedString> getLocalisedNames(Element node) {
        return getLocalised(NAME, node);
    }

    public static List<LocalisedString> getLocalised(String name, Element node) {
        List<LocalisedString> r = new ArrayList<LocalisedString>();
        List<Element> l = getRealChilds(node);
        for (Element n : l) {
            if (n.getNodeName().equals(name) && getLocaleAtt(n) != null) {
                r.add(new LocalisedString(getLocaleAtt(n), n.getTextContent()));
            }
        }
        return r;
    }

    private static String getNonLocalised(String name, Element node) {
        List<Element> l = getRealChilds(node);
        for (Element n : l) {
            if (n.getNodeName().equals(name) && getLocaleAtt(n) == null) {
                return n.getTextContent();
            }
        }
        throw new RuntimeException("Non localised form of " + name + " not found!");
    }

    public static String getDefaultDescription(Element n) {
        return getNonLocalised(DESCRIPTION, n);
    }

    public static List<LocalisedString> getLocalisedDescriptions(Element node) {
        return getLocalised(DESCRIPTION, node);
    }

    public static List<String> getImages(Element node) {
        return getByName(IMAGE, node);
    }

    public static List<String> getByName(String name, Element node) {
        List<String> r = new ArrayList<String>();
        List<Element> l = getRealChilds(node);
        for (Element n : l) {
            if (n.getNodeName().equals(name)) {
                r.add(n.getTextContent());
            }
        }
        return r;
    }

}
