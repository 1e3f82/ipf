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
package org.openehealth.ipf.platform.camel.core.model;

import org.apache.camel.spi.RouteContext;
import org.openehealth.ipf.commons.core.modules.api.Validator;
import org.openehealth.ipf.platform.camel.core.adapter.ProcessorAdapter;
import org.openehealth.ipf.platform.camel.core.adapter.ValidatorAdapter;

/**
 * @author Martin Krasser
 */
public class ValidatorAdapterType extends ProcessorAdapterType {

    private Validator validator;

    private Object profile;
    
    public ValidatorAdapterType(Validator validator) {
        this.validator = validator;
    }
    
    public ValidatorAdapterType profile(Object profile) {
        this.profile = profile;
        return this;
    }
    
    @Override
    public String toString() {
        return "ValidatorAdapter[" + getOutputs() + "]";
    }

    @Override
    public String getShortName() {
        return "validatorAdapter";
    }

    @Override
    protected ProcessorAdapter doCreateProcessor(RouteContext routeContext) {
        ValidatorAdapter adapter = new ValidatorAdapter(validator);
        if (profile != null) {
            return adapter.profile(profile);
        }
        return adapter;
    }

}
