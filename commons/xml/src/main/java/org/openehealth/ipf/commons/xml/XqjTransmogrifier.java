package org.openehealth.ipf.commons.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQConstants;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;
import javax.xml.xquery.XQPreparedExpression;
import javax.xml.xquery.XQResultSequence;
import javax.xml.xquery.XQSequence;
import javax.xml.xquery.XQStaticContext;

import net.sf.saxon.Configuration;
import net.sf.saxon.FeatureKeys;
import net.sf.saxon.xqj.SaxonXQDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openehealth.ipf.commons.core.modules.api.Transmogrifier;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * XQuery transformer similar to the XsltTransmogrifier
 * 
 * @author Stefan Ivanov
 * 
 * @param <T>
 */
public class XqjTransmogrifier<T> implements Transmogrifier<Source, T> {
    private final static Log LOG = LogFactory.getLog(XqjTransmogrifier.class);

    private static final String RESOURCE_LOCATION = "org.openehealth.ipf.commons.xml.ResourceLocation";

    private final XQDataSource ds;
    private Map<String, String> staticParams;
    private final Class<T> outputFormat;

    private final static ResourceLoader resourceLoader = new DefaultResourceLoader(
            XqjTransmogrifier.class.getClassLoader());

    @SuppressWarnings("unchecked")
    public XqjTransmogrifier() {
        this((Class<T>) String.class);
    }

    /**
     * @param outputFormat
     *            currently supported: String, Writer, OutputStream
     */
    public XqjTransmogrifier(Class<T> outputFormat) {
        super();
        Configuration saxonConfig = new Configuration();
        saxonConfig.setHostLanguage(Configuration.XQUERY);
        saxonConfig.setURIResolver(new ClasspathUriResolver(saxonConfig
                .getURIResolver()));
        saxonConfig.setConfigurationProperty(
                FeatureKeys.PRE_EVALUATE_DOC_FUNCTION, Boolean.TRUE.toString());
        ds = new SaxonXQDataSource(saxonConfig);
        this.outputFormat = outputFormat;
    }

    /**
     * @param outputFormat
     *            currently supported: String
     * @param staticParams
     *            static XQuery parameters
     * @throws XQException
     */
    public XqjTransmogrifier(Class<T> outputFormat,
            Map<String, String> staticParams) throws XQException {
        this(outputFormat);
        this.staticParams = staticParams;
        this.ds.setProperties(properties(staticParams));
    }

    public XqjTransmogrifier(Class<T> outputFormat,
            String xqueryClasspathResource, Map<String, String> staticParams)
            throws XQException {
        this(outputFormat);
        this.staticParams = staticParams;
        this.ds.setProperties(properties(staticParams));
    }

    /**
     * 
     * Converts a Source into a typed result using a XQuery processor.
     * <p>
     * The XQ resource location is mandatory in the first extra parameter. Other
     * parameters may be passed as a Map in the second parameter.
     * 
     * @see org.openehealth.ipf.commons.core.modules.api.Transmogrifier#zap(java.lang.Object,
     *      java.lang.Object[])
     */
    @Override
    public T zap(Source source, Object... params) {
        ResultHolder<T> accessor = ResultHolderFactory.create(outputFormat);
        Result result = accessor.createResult();
        doZap(source, result, params);
        return accessor.getResult();
    }

    private void doZap(Source source, Result result, Object... params) {
        if (params.length == 0) {
            throw new IllegalArgumentException(
                    "Expected XQuery location in first parameter");
        }
        XQResultSequence seq = null;
        try {
            XQConnection conn = ds.getConnection();
            XQStaticContext ctxt = conn.getStaticContext();
            bindDynamicNamespaces(ctxt, params);
            XQPreparedExpression exp = conn.prepareExpression(
                    source(resource(params)), ctxt);
            if (source != null)
                exp.bindDocument(XQConstants.CONTEXT_ITEM, source, null);
            bindDynamicContext(exp, params);
            seq = exp.executeQuery();
            seq.writeSequenceToResult(result);
        } catch (IOException e) {
            throw new IllegalArgumentException("The resource location "
                    + params[0] + " is not valid", e.getCause());
        } catch (XQException e) {
            throw new RuntimeException("XQuery processing failed", e);
        } finally {
            if (seq != null)
                closeQuietly(seq);
        }
    }
    
    private void bindDynamicNamespaces(XQStaticContext ctxt, Object... params)
            throws XQException {
        if (params == null || params.length < 3)
            return;
        for (Entry<String, Object> entry : parameters(params[2]).entrySet()) {
            ctxt.declareNamespace(entry.getKey(), (String) entry.getValue());
        }
    }

    private void bindDynamicContext(XQPreparedExpression exp, Object... params)
            throws XQException {
        if (params == null || params.length < 2 || params[1] == null)
            return;
        for (Entry<String, Object> entry : parameters(params[1]).entrySet()) {
            Object value = entry.getValue();
            if (value instanceof java.lang.String) {
                exp.bindString(new QName(entry.getKey()),
                        (String) value, null);
            } else if (value instanceof javax.xml.transform.Source) {
                exp.bindDocument(new QName(entry.getKey()),
                        (Source) entry.getValue(), null);
            } else if (value instanceof Boolean) {
                exp.bindBoolean(new QName(entry.getKey()),
                        (Boolean) entry.getValue(),
                        null);
            } else {
                LOG.warn("The XQJTransmogrifier allows static parameters of type: "
                        + "java.lang.String, javax.xml.transform.Source, org.w3c.dom.Document");
            }
        }
    }

    private Properties properties(Map<String, String> staticParams) {
        Properties staticConfig = new Properties();
        staticConfig.putAll(staticParams);
        return staticConfig;
    }

    /**
     * Retrieves the dynamic parameters
     * 
     * @param params
     * @return
     */
    @SuppressWarnings("unchecked")
    protected Map<String, Object> parameters(Object... params) {
        if (params[0] instanceof Map) {
            return (Map<String, Object>) params[0];
        } else if (params.length > 1 && params[1] instanceof Map) {
            return (Map<String, Object>) params[1];
        } else {
            return null;
        }
    }

    /**
     * @param params
     * @return the full path to the resource
     */
    protected String resource(Object... params) {
        String resourceLocation = null;
        if (params[0] instanceof String) {
            resourceLocation = (String) params[0];
        } else if (params[0] instanceof Map) {
            resourceLocation = (String) ((Map<?, ?>) params[0])
                    .get(RESOURCE_LOCATION);
        }
        return resourceLocation;
    }

    protected InputStream source(String resource) throws IOException {
        Resource r = resourceLoader.getResource(resource);
        if (r != null) {
            return r.getInputStream();
        } else {
            throw new IllegalArgumentException("XQuery not specified properly");
        }
    }

    private void closeQuietly(XQSequence seq) {
        try {
            seq.close();
        } catch (XQException ignored) {
            LOG.trace("XQLTransmogrifier didn't return a value.");
        }
    }

    public Map<String, String> getStaticParams() {
        return staticParams;
    }

    public void setStaticParams(Map<String, String> staticParams) {
        this.staticParams = staticParams;
    }

}
