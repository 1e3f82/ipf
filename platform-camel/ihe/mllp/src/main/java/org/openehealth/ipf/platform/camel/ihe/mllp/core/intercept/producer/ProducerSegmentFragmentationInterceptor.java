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
package org.openehealth.ipf.platform.camel.ihe.mllp.core.intercept.producer;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Producer;
import org.openehealth.ipf.platform.camel.core.util.Exchanges;
import org.openehealth.ipf.platform.camel.ihe.hl7v2.Hl7v2MarshalUtils;
import org.openehealth.ipf.platform.camel.ihe.mllp.core.FragmentationUtils;
import org.openehealth.ipf.platform.camel.ihe.mllp.core.MllpEndpoint;


/**
 * Interceptor that handles segment fragmentation and defragmentation (\rADD|...).
 * @author Dmytro Rud
 */
public class ProducerSegmentFragmentationInterceptor extends AbstractMllpProducerInterceptor {

    public ProducerSegmentFragmentationInterceptor(MllpEndpoint endpoint, Producer wrappedProducer) {
        super(endpoint, wrappedProducer);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        boolean supportSegmentFragmentation = getMllpEndpoint().isSupportSegmentFragmentation();
        int segmentFragmentationThreshold = getMllpEndpoint().getSegmentFragmentationThreshold();
        Message message;
        
        // preprocess output
        if (supportSegmentFragmentation && (segmentFragmentationThreshold >= 5)) {
            message = exchange.getIn();
            String s = message.getBody(String.class);
            s = FragmentationUtils.ensureMaximalSegmentsLength(s, segmentFragmentationThreshold);
            message.setBody(s);
        }
        
        // run the route
        getWrappedProcessor().process(exchange);
        
        // read in the response
        message = Exchanges.resultMessage(exchange);
        message.setBody(Hl7v2MarshalUtils.convertBodyToString(
                message,
                getMllpEndpoint().getConfiguration().getCharsetName(),
                supportSegmentFragmentation));
    }
}
