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
package org.openehealth.ipf.platform.camel.ihe.hl7v3.pcc1;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.openehealth.ipf.commons.ihe.core.IpfInteractionId;
import org.openehealth.ipf.commons.ihe.hl7v3.Hl7v3ContinuationAwareServiceInfo;
import org.openehealth.ipf.commons.ihe.hl7v3.pcc1.Pcc1PortType;
import org.openehealth.ipf.platform.camel.ihe.ws.AbstractWsComponent;

import javax.xml.namespace.QName;

/**
 * The Camel component for the PCC-1 transaction (QED).
 */
public class Pcc1Component extends AbstractWsComponent<Hl7v3ContinuationAwareServiceInfo> {

    private final static String NS_URI = "urn:ihe:pcc:qed:2007";
    public final static Hl7v3ContinuationAwareServiceInfo WS_CONFIG = new Hl7v3ContinuationAwareServiceInfo(
            IpfInteractionId.PCC_1,
            new QName(NS_URI, "ClinicalDataSource_Service", "qed"),
            Pcc1PortType.class,
            new QName(NS_URI, "ClinicalDataSource_Binding_Soap12", "qed"),
            false,
            "wsdl/pcc1/pcc1-raw.wsdl",
            "QUPC_IN043200UV01",
            true,
            false,
            "QUPC_IN043100UV01",
            "QUPC_IN043200UV01");

    @SuppressWarnings("unchecked") // Required because of base class
    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map parameters) throws Exception {
        return new Pcc1Endpoint(uri, remaining, this, getCustomInterceptors(parameters));
    }

    @Override
    public Hl7v3ContinuationAwareServiceInfo getWebServiceConfiguration() {
        return WS_CONFIG;
    }
}
