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
package org.openehealth.ipf.platform.camel.ihe.xds.iti18;

import org.openehealth.ipf.commons.ihe.ws.ItiClientFactory;
import org.openehealth.ipf.commons.ihe.xds.core.stub.ebrs30.query.AdhocQueryRequest;
import org.openehealth.ipf.commons.ihe.xds.core.stub.ebrs30.query.AdhocQueryResponse;
import org.openehealth.ipf.commons.ihe.xds.iti18.Iti18PortType;
import org.openehealth.ipf.platform.camel.ihe.ws.DefaultItiProducer;

/**
 * The producer implementation for the ITI-18 component.
 */
public class Iti18Producer extends DefaultItiProducer<AdhocQueryRequest, AdhocQueryResponse> {
    /**
     * Constructs the producer.
     * @param endpoint
     *          the endpoint creating this producer.
     * @param clientFactory
     *          the factory for clients to produce messages for the service.              
     */
    public Iti18Producer(Iti18Endpoint endpoint, ItiClientFactory clientFactory) {
        super(endpoint, clientFactory);
    }

    @Override
    protected AdhocQueryResponse callService(Object client, AdhocQueryRequest body) {
        return ((Iti18PortType) client).documentRegistryRegistryStoredQuery(body);
    }
}
