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
package org.openehealth.ipf.platform.camel.ihe.xds.iti41

import org.apache.camel.spring.SpringRouteBuilder
import org.apache.commons.io.IOUtils
import org.openehealth.ipf.commons.ihe.ws.utils.LargeDataSource
import org.openehealth.ipf.commons.ihe.xds.core.requests.ProvideAndRegisterDocumentSet
import org.openehealth.ipf.commons.ihe.xds.core.responses.Response
import org.openehealth.ipf.platform.camel.core.util.Exchanges
import static org.openehealth.ipf.commons.ihe.xds.core.responses.Status.FAILURE
import static org.openehealth.ipf.commons.ihe.xds.core.responses.Status.SUCCESS
import javax.activation.DataHandler

/**
 * @author Jens Riemschneider
 */
public class GroovyRouteBuilder extends SpringRouteBuilder {
    @Override
    public void configure() throws Exception {
        from('xds-iti41:xds-iti41-service1?rejectionHandlingStrategy=#rejectionHandlingStrategy')
            .validate().iti41Request()
            .process { checkValue(it, 'service 1') }
            .validate().iti41Response()
    
        from('xds-iti41:xds-iti41-service2')
            .process { checkValue(it, 'service 2') }
   }

    void checkValue(exchange, expected) {
        def doc = exchange.in.getBody(ProvideAndRegisterDocumentSet.class).documents[0]
        def value = doc.documentEntry.comments.value        
        def status = FAILURE;
        def dataHandler = doc.getContent(DataHandler)
        if (expected == value && dataHandler != null) {
            Collection attachments = dataHandler.dataSource.attachments
            def inputStream = dataHandler.inputStream
            try {
                if (attachments.size() == 1 && attachments.iterator().next().xop) {
                    def length = 0
                    while (inputStream.read() != -1) {
                        ++length
                    }
                    if (length == LargeDataSource.STREAM_SIZE) {
                        status = SUCCESS
                    }
                }
            }
            finally {
                IOUtils.closeQuietly(inputStream)
            }
        }
        
        def response = new Response(status)
        Exchanges.resultMessage(exchange).body = response
    }
}
