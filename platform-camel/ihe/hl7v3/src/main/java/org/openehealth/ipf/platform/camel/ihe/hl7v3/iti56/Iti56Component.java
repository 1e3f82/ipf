/*
 * Copyright 2010 the original author or authors.
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
package org.openehealth.ipf.platform.camel.ihe.hl7v3.iti56;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.openehealth.ipf.commons.ihe.core.IpfInteractionId;
import org.openehealth.ipf.commons.ihe.hl7v3.Hl7v3ServiceInfo;
import org.openehealth.ipf.commons.ihe.hl7v3.iti56.Iti56PortType;
import org.openehealth.ipf.platform.camel.ihe.ws.AbstractWsComponent;

import javax.xml.namespace.QName;

/**
 * The Camel component for the ITI-56 transaction (XCPD).
 */
public class Iti56Component extends AbstractWsComponent<Hl7v3ServiceInfo> {
    private final static String NS_URI = "urn:ihe:iti:xcpd:2009";
    public final static Hl7v3ServiceInfo WS_CONFIG = new Hl7v3ServiceInfo(
            IpfInteractionId.ITI_56,
            new QName(NS_URI, "RespondingGateway_Service", "xcpd"),
            Iti56PortType.class,
            new QName(NS_URI, "RespondingGateway_Binding_Soap12", "xcpd"),
            false,
            "wsdl/iti56/iti56-raw.wsdl",
            null,
            false,
            true);

    @SuppressWarnings("unchecked") // Required because of base class
    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map parameters) throws Exception {
        return new Iti56Endpoint(uri, remaining, this, getCustomInterceptors(parameters));
    }

    @Override
    public Hl7v3ServiceInfo getWebServiceConfiguration() {
        return WS_CONFIG;
    }
}
