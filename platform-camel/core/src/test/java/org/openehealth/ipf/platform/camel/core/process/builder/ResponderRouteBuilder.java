/*
 * Copyright 2008 the original author or authors.
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
package org.openehealth.ipf.platform.camel.core.process.builder;

import org.apache.camel.Processor;
import org.openehealth.ipf.platform.camel.core.builder.RouteBuilder;
import org.openehealth.ipf.platform.camel.test.transformer.ConstantTransformer;


/**
 * @author Martin Krasser
 */
public class ResponderRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("direct:responder-test-1")
        .intercept(validation(responseGenerator()))
        .to("mock:mock");

        from("direct:responder-test-2")
        .intercept(validation("direct:response-generator"))
        .to("mock:mock");

        from("direct:response-generator").process(responseGenerator());
        
    }

    private Processor responseGenerator() {
        return new ConstantTransformer("blah");
    }
    
}
