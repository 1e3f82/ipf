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
package org.openehealth.ipf.platform.camel.ihe.mllp.iti10

import org.openehealth.ipf.platform.camel.ihe.mllp.core.MllpAuditDataset;
import org.openhealthtools.ihe.atna.auditor.codes.rfc3881.RFC3881EventCodes.RFC3881EventOutcomeCodes
import org.openehealth.ipf.commons.ihe.core.atna.AuditorManager;

/**
 * Client (aka Camel producer, aka PIX Manager) audit strategy for ITI-10 (PIX Update Notification).
 * @author Dmytro Rud
 */
class Iti10ClientAuditStrategy extends Iti10AuditStrategy {

    Iti10ClientAuditStrategy() {
        super(false)
    }


    void doAudit(RFC3881EventOutcomeCodes eventOutcome, MllpAuditDataset auditDataset) {
        AuditorManager.getPIXManagerAuditor().auditUpdateNotificationEvent(
                eventOutcome,
                auditDataset.localAddress,
                auditDataset.sendingFacility,
                auditDataset.sendingApplication,
                auditDataset.remoteAddress,
                auditDataset.receivingFacility,
                auditDataset.receivingApplication,
                auditDataset.messageControlId,
                auditDataset.patientIds)
    }
    
}
 
