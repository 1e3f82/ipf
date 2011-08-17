package org.openehealth.ipf.commons.xml;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.xml.sax.SAXException;

public class XqjTransmogrifierTest {

    private XqjTransmogrifier<String> transformer;

    @BeforeClass
    public static void setUpClass() {
        XMLUnit.setCompareUnmatched(true);
        XMLUnit.setIgnoreAttributeOrder(true);
        XMLUnit.setIgnoreComments(true);
        XMLUnit.setIgnoreWhitespace(true);
    }

    @Before
    public void setUp() throws Exception {
        transformer = new XqjTransmogrifier<String>(String.class);
    }

    @Test
    public void zapSimple() throws IOException, SAXException {
        Source source = new StreamSource(new ClassPathResource(
                "xquery/string.xml").getInputStream());
        String resultText = IOUtils.toString(new ClassPathResource(
                "xquery/string-q1.xml").getInputStream());
        String result = transformer.zap(source, "xquery/string-q1.xq");
        assertTrue(XMLUnit.compareXML(resultText, result).similar());
    }

    @Test
    public void zapStringParameter() throws IOException, SAXException {
        Source source = new StreamSource(new ClassPathResource(
                "xquery/string.xml").getInputStream());
        String resultText = IOUtils.toString(new ClassPathResource(
                "xquery/string-q2.xml").getInputStream());
        Map<String, Object> dynamicEvalParams = new HashMap<String, Object>();
        dynamicEvalParams.put("language", "English");
        Object[] params = new Object[]{"xquery/string-q2.xq", dynamicEvalParams} ;
        String result = transformer.zap(source, params);
        assertTrue(XMLUnit.compareXML(resultText, result).similar());
    }

    @Test(expected = RuntimeException.class)
    public void zapMissingParameter() throws IOException, SAXException {
        Source source = new StreamSource(new ClassPathResource(
                "xquery/string.xml").getInputStream());
        String resultText = IOUtils.toString(new ClassPathResource(
                "xquery/string-q2.xml").getInputStream());
        String result = transformer.zap(source, "xquery/string-q2.xq");
        assertTrue(XMLUnit.compareXML(resultText, result).similar());
    }

    @Test
    public void zapLocalFunction() throws IOException, SAXException {
        Source source = new StreamSource(new ClassPathResource(
                "xquery/string.xml").getInputStream());
        String resultText = IOUtils.toString(new ClassPathResource(
                "xquery/string-q3.xml").getInputStream());

        Map<String, Object> dynamicEvalParams = new HashMap<String, Object>();
        dynamicEvalParams.put("language", "Bulgarian");
        dynamicEvalParams.put("author_name", "John");
        Object[] params = new Object[] { "xquery/string-q3.xq",
                dynamicEvalParams };
        String result = transformer.zap(source, params);
        assertTrue(XMLUnit.compareXML(resultText, result).similar());
    }

    @Test
    public void zapMainFunction() throws IOException, SAXException {
        Source source = new StreamSource(new ClassPathResource(
                "xquery/string.xml").getInputStream());
        String resultText = IOUtils.toString(new ClassPathResource(
                "xquery/string.xml").getInputStream());
        String result = transformer.zap(source, "xquery/string-q4.xq");
        assertTrue(XMLUnit.compareXML(resultText, result).similar());
    }

    @Test
    public void zapClasspathResolver() throws IOException, SAXException {
        Source source = new StreamSource(new ClassPathResource(
                "xquery/string.xml").getInputStream());
        String resultText = IOUtils.toString(new ClassPathResource(
                "xquery/string.xml").getInputStream());
        String result = transformer.zap(source, "xquery/string-q5.xq");
        assertTrue(XMLUnit.compareXML(resultText, result).similar());
    }

    @Test
    public void zapWithNamespaces() throws IOException, SAXException {
        Source source = new StreamSource(
                new ClassPathResource("xquery/ns.xml").getInputStream());
        String resultText = IOUtils.toString(new ClassPathResource(
                "xquery/ns-q1.xml").getInputStream());
        String result = transformer.zap(source, "xquery/ns-q1.xq");
        assertTrue(XMLUnit.compareXML(resultText, result).similar());
    }

    @Test
    public void zapWithNamespacesWithParam() throws IOException, SAXException {
        Source source = new StreamSource(
                new ClassPathResource("xquery/ns.xml").getInputStream());
        String resultText = IOUtils.toString(new ClassPathResource(
                "xquery/ns-q1.xml").getInputStream());
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("ns", "urn:default:ns");
        namespaces.put("new", "urn:ns:2011");
        namespaces.put("other", "urn:ns:1999");
        Object[] params = new Object[] { "xquery/ns-q2.xq", null, namespaces };
        String result = transformer.zap(source, params);
        assertTrue(XMLUnit.compareXML(resultText, result).similar());
    }

    @Test
    public void zapRCase() throws IOException, SAXException {
        String resultText = IOUtils.toString(new ClassPathResource(
                "xquery/r-q1.xml").getInputStream());
        String result = transformer.zap(null, "xquery/r-q1.xq");
        assertTrue(XMLUnit.compareXML(resultText, result).similar());
    }

}
