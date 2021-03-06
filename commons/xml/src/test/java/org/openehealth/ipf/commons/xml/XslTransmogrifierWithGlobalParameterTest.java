/*
 * Copyright 2009 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openehealth.ipf.commons.xml;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

/**
 * Tests passing in global parameters (e.g. services) into a stylesheet. See
 * parameter.xslt.
 * 
 * @author Christian Ohr
 * 
 */
public class XslTransmogrifierWithGlobalParameterTest {

    private XsltTransmogrifier<String> transformer;
    Map<String, Object> parameters;

    @Before
    public void setUp() throws Exception {
        parameters = new HashMap<String, Object>();
        parameters.put("service", new XsltTestService());
        transformer = new XsltTransmogrifier<String>(String.class);
    }

    /**
     * Demonstrates how to include a Groovy class to be used in an XSLT script
     * 
     * @throws IOException
     */
    @Test
    public void testConvertString() throws IOException {
        Source content = new StreamSource(new ClassPathResource(
                "xslt/parameterExample.xml").getInputStream());
        String s = transformer.zap(content, "xslt/parameter.xslt", parameters);
        assertTrue(s.contains("ein negeR mi tgaz ellezaG tim regeN niE"));
    }

}
