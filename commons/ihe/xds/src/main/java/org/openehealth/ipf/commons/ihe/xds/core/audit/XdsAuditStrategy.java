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
package org.openehealth.ipf.commons.ihe.xds.core.audit;

import java.util.List;

import org.openehealth.ipf.commons.ihe.ws.cxf.audit.WsAuditDataset;
import org.openehealth.ipf.commons.ihe.ws.cxf.audit.WsAuditStrategy;
import org.openehealth.ipf.commons.ihe.xds.core.ebxml.EbXMLRegistryResponse;
import org.openehealth.ipf.commons.ihe.xds.core.responses.ErrorInfo;
import org.openehealth.ipf.commons.ihe.xds.core.responses.Severity;
import org.openehealth.ipf.commons.ihe.xds.core.responses.Status;
import org.openhealthtools.ihe.atna.auditor.codes.rfc3881.RFC3881EventCodes.RFC3881EventOutcomeCodes;


/**
 * Basis for Strategy pattern implementation for ATNA Auditing in XDS transactions.
 * @author Dmytro Rud
 */
public abstract class XdsAuditStrategy extends WsAuditStrategy {

    /**
     * Constructs an XDS audit strategy.
     *   
     * @param serverSide
     *      whether this is a server-side or a client-side strategy.
     * @param allowIncompleteAudit
     *      whether this strategy should allow incomplete audit records
     *      (parameter initially configurable via endpoint URL).
     */
    public XdsAuditStrategy(boolean serverSide, boolean allowIncompleteAudit) {
        super(serverSide, allowIncompleteAudit);
    }
    

    /**
     * Creates a new XDS audit dataset audit instance. 
     * 
     * @return
     *      newly created audit dataset
     */
    @Override
    public XdsAuditDataset createAuditDataset() {
        return new XdsAuditDataset(isServerSide());
    }

    
    /**
     * A helper method that analyzes the given registry response and 
     * determines the corresponding RFC 3881 event outcome code.
     * @param response
     *          registry to analyze.
     * @return outcome code.
     */
    public static RFC3881EventOutcomeCodes getEventOutcomeCodeFromRegistryResponse(EbXMLRegistryResponse response) {
        if(response == null) {
            return RFC3881EventOutcomeCodes.SERIOUS_FAILURE;
        }
        
        if(response.getStatus() == Status.SUCCESS) {
            return RFC3881EventOutcomeCodes.SUCCESS; 
        }

        List<ErrorInfo> errors = response.getErrors();
        
        // error list is empty
        if((errors == null) || errors.isEmpty()) {
            return RFC3881EventOutcomeCodes.SERIOUS_FAILURE;
        }
        
        // determine the highest severity 
        for(ErrorInfo error : errors) {
            if(error.getSeverity() == Severity.ERROR) {
                return RFC3881EventOutcomeCodes.SERIOUS_FAILURE;
            }
        }
        return RFC3881EventOutcomeCodes.MINOR_FAILURE;
    }


    @Override
    public void enrichDatasetFromResponse(Object response, WsAuditDataset auditDataset) throws Exception {
        auditDataset.setEventOutcomeCode(getEventOutcomeCode(response));
    }
}
