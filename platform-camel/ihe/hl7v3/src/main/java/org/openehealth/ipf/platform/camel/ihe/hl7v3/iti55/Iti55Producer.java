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
package org.openehealth.ipf.platform.camel.ihe.hl7v3.iti55;

import org.openehealth.ipf.commons.ihe.ws.ItiClientFactory;
import org.openehealth.ipf.commons.ihe.hl7v3.iti55.Iti55PortType;
import org.openehealth.ipf.platform.camel.ihe.ws.DefaultItiProducer;

/**
 * The producer implementation for the ITI-55 component.
 * @author Dmytro Rud
 */
public class Iti55Producer extends DefaultItiProducer<String, String> {
    
    /**
     * Constructs the producer.
     * @param endpoint
     *          the endpoint creating this producer.
     * @param clientFactory
     *          the factory for clients to produce messages for the service.              
     */
    public Iti55Producer(Iti55Endpoint endpoint, ItiClientFactory clientFactory) {
        super(endpoint, clientFactory, true);
    }
    
    
    @Override
    protected String callService(Object client, String body) {
        return ((Iti55PortType) client).respondingGatewayPRPAIN201305UV02(body);
    }
}
