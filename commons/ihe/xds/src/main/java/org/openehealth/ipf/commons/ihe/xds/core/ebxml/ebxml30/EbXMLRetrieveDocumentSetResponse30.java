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
package org.openehealth.ipf.commons.ihe.xds.core.ebxml.ebxml30;

import static org.apache.commons.lang.Validate.notNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openehealth.ipf.commons.ihe.xds.core.ebxml.EbXMLRetrieveDocumentSetRequest;
import org.openehealth.ipf.commons.ihe.xds.core.ebxml.EbXMLRetrieveDocumentSetResponse;
import org.openehealth.ipf.commons.ihe.xds.core.ebxml.ebxml30.RetrieveDocumentSetResponseType.DocumentResponse;
import org.openehealth.ipf.commons.ihe.xds.core.requests.RetrieveDocument;
import org.openehealth.ipf.commons.ihe.xds.core.responses.ErrorCode;
import org.openehealth.ipf.commons.ihe.xds.core.responses.ErrorInfo;
import org.openehealth.ipf.commons.ihe.xds.core.responses.RetrievedDocument;
import org.openehealth.ipf.commons.ihe.xds.core.responses.Severity;
import org.openehealth.ipf.commons.ihe.xds.core.responses.Status;
import org.openehealth.ipf.commons.ihe.xds.core.stub.ebrs30.rs.RegistryError;
import org.openehealth.ipf.commons.ihe.xds.core.stub.ebrs30.rs.RegistryErrorList;

/**
 * The ebXML 3.0 version of the {@link EbXMLRetrieveDocumentSetRequest}.
 * @author Jens Riemschneider
 */
public class EbXMLRetrieveDocumentSetResponse30 implements EbXMLRetrieveDocumentSetResponse {
    private final RetrieveDocumentSetResponseType response;

    /**
     * Constructs a response by wrapping the given ebXML 3.0 object.
     * @param response
     *          the object to wrap.
     */
    public EbXMLRetrieveDocumentSetResponse30(RetrieveDocumentSetResponseType response) {
        notNull(response, "response cannot be null");
        this.response = response;
    }
    
    @Override
    public RetrieveDocumentSetResponseType getInternal() {
        return response;
    }

    @Override
    public List<RetrievedDocument> getDocuments() {
        List<RetrievedDocument> docs = new ArrayList<RetrievedDocument>();
        for (DocumentResponse documentResponse : response.getDocumentResponse()) {
            RetrievedDocument doc = new RetrievedDocument();            
            doc.setDataHandler(documentResponse.getDocument());
            RetrieveDocument requestData = new RetrieveDocument();
            doc.setRequestData(requestData);
            requestData.setDocumentUniqueId(documentResponse.getDocumentUniqueId());
            requestData.setHomeCommunityId(documentResponse.getHomeCommunityId());
            requestData.setRepositoryUniqueId(documentResponse.getRepositoryUniqueId());
            docs.add(doc);
        }
        return docs;
    }

    @Override
    public void setDocuments(List<RetrievedDocument> documents) {
        response.getDocumentResponse().clear();
        if (documents != null) {
            for (RetrievedDocument doc : documents) {
                DocumentResponse documentResponse = new DocumentResponse();
                documentResponse.setDocument(doc.getDataHandler());
                if (doc.getDataHandler() != null) {
                    documentResponse.setMimeType(doc.getDataHandler().getContentType());
                }
                RetrieveDocument requestData = doc.getRequestData();
                if (requestData != null) {
                    documentResponse.setDocumentUniqueId(requestData.getDocumentUniqueId());
                    documentResponse.setHomeCommunityId(requestData.getHomeCommunityId());
                    documentResponse.setRepositoryUniqueId(requestData.getRepositoryUniqueId());
                }
                response.getDocumentResponse().add(documentResponse);
            }
        }
    }

    @Override
    public void setStatus(Status status) {
        response.getRegistryResponse().setStatus(Status.getOpcode30(status));
    }
    
    @Override
    public Status getStatus() {
        return Status.valueOfOpcode(response.getRegistryResponse().getStatus());
    }    

    @Override
    public List<ErrorInfo> getErrors() {
        RegistryErrorList list = response.getRegistryResponse().getRegistryErrorList();
        if (list == null) {
            return Collections.emptyList();
        }
        
        List<ErrorInfo> errors = new ArrayList<ErrorInfo>();
        for (RegistryError regError : list.getRegistryError()) {
            ErrorInfo error = new ErrorInfo();
            error.setCodeContext(regError.getCodeContext());
            error.setLocation(regError.getLocation());
            error.setErrorCode(ErrorCode.valueOfOpcode(regError.getErrorCode()));
            error.setSeverity(Severity.valueOfOpcode30(regError.getSeverity()));
            errors.add(error);
        }
        
        return errors;
    }

    @Override
    public void setErrors(List<ErrorInfo> errors) {
        RegistryErrorList value = EbXMLFactory30.RS_FACTORY.createRegistryErrorList();
        response.getRegistryResponse().setRegistryErrorList(value);
        List<RegistryError> list = value.getRegistryError();
        for (ErrorInfo error : errors) {
            RegistryError regError = EbXMLFactory30.RS_FACTORY.createRegistryError();
            regError.setErrorCode(ErrorCode.getOpcode(error.getErrorCode()));
            regError.setCodeContext(error.getCodeContext());
            regError.setSeverity(Severity.getOpcode30(error.getSeverity()));
            regError.setLocation(error.getLocation());
            list.add(regError);
        }
    }
}
