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
package org.openehealth.ipf.platform.camel.ihe.xcpd.iti55;

import org.apache.camel.Endpoint;
import org.openehealth.ipf.commons.ihe.core.InteractionId;
import org.openehealth.ipf.commons.ihe.core.IpfInteractionId;
import org.openehealth.ipf.platform.camel.ihe.ws.AbstractWsComponent;

import java.util.Map;

/**
 * The Camel component for the ITI-55 transaction (XCPD).
 */
public class Iti55Component extends AbstractWsComponent {

    /**
     * Name of Camel header where the contents of the incoming CorrelationTimeToLive
     * SOAP header will be stored.
     */
    public static final String XCPD_INPUT_TTL_HEADER_NAME  = "xcpd.input.CorrelationTimeToLive";

    /**
     * Name of Camel header where the user should store the value for the outgoing 
     * CorrelationTimeToLive SOAP header.
     */
    public static final String XCPD_OUTPUT_TTL_HEADER_NAME = "xcpd.output.CorrelationTimeToLive";
    
    
    @SuppressWarnings("unchecked") // Required because of base class
    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map parameters) throws Exception {
        return new Iti55Endpoint(uri, remaining, this, getCustomInterceptors(parameters));
    }

    @Override
    public InteractionId getInteractionId() {
        return IpfInteractionId.ITI_55;
    }
}
