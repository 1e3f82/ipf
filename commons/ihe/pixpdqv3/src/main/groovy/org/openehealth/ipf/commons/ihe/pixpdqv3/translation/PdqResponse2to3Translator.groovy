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
package org.openehealth.ipf.commons.ihe.pixpdqv3.translation;

import groovy.xml.MarkupBuilder
import groovy.util.slurpersupport.GPathResult

import org.openehealth.ipf.modules.hl7.message.MessageUtils

import org.openehealth.ipf.modules.hl7dsl.GroupAdapter;
import org.openehealth.ipf.modules.hl7dsl.CompositeAdapter;
import org.openehealth.ipf.modules.hl7dsl.MessageAdapter
import org.openehealth.ipf.modules.hl7dsl.SelectorClosure;

import static org.openehealth.ipf.commons.ihe.pixpdqv3.translation.Utils.*
import static org.openehealth.ipf.commons.ihe.hl7v3.Hl7v3Utils.*
import org.openehealth.ipf.commons.xml.XmlYielder
import org.openehealth.ipf.modules.hl7.ErrorLocation;

/**
 * PDQ Response translator HL7 v2 to v3.
 * @author Marek V�clav�k, Dmytro Rud
 */
class PdqResponse2to3Translator implements Hl7TranslatorV2toV3 {

    /**
     * Predefined fix value of
     * <code>//registrationEvent/custodian/assignedEntity/id/@root</code>.
     * In productive environments to be set to the <code>id/@root</code>
     * of the device representing the MPI.
     */
    String mpiSystemIdRoot = '1.2.3'

    /**
     * Predefined fix value of
     * <code>//registrationEvent/custodian/assignedEntity/id/@extension</code>.
     * In productive environments to be set to the <code>id/@extension</code>
     * of the device representing the MPI.
     */
    String mpiSystemIdExtension = ''

    /**
     * If true, <code>&lt;resultTotalQuantity nullFlavor="UNK"/&gt;</code>
     * will appear on the output. Otherwise the element will not be present.
     */
    boolean outputResultTotalQuantity = true

    /**
     * If true, <code>&lt;resultRemainingQuantity nullFlavor="UNK"/&gt;</code>
     * will appear on the output. Otherwise the element will not be present.
     */
    boolean outputResultRemainingQuantity = true

    /**
     * Predefined fix value of <code>//patient/queryMatchObservation/value/@value</code>.
     */
    String defaultMatchQuality = '50'

    /**
     * Predefined fix value of <code>//acknowledgementDetail/code/@codeSystem</code>.
     */
    String errorCodeSystem = '2.16.840.1.113883.12.357'

    /**
     * <tt>root</tt> attribute of message's <tt>id</tt> element.
     */
    String messageIdRoot = '1.2.3'

    /**
     * First character of the acknowledgment code.  Possible values are 'A' and 'C'.
     * <p>
     * Declared as String from technical reasons.
     */
    String ackCodeFirstCharacter = 'A'

    /**
     * Root for values from PID-19.
     */
    String nationalIdentifierRoot = '2.16.840.1.113883.4.1'

    /**
     * When not empty, an element asOtherIDs with ID.nullFlavor=. will be created
     * for each domain not represented in PID-3 patient IDs.
     */
    String nullFlavorWhenScopingOrganizationIdNotFound = 'NA'

        
    /**
     * Translates HL7 v2 response messages <tt>RSP^K22</tt> and <tt>ACK</tt> 
     * into HL7 v3 message <tt>PRPA_IN201306UV02</tt>.
     * <p>
     * Parameters related to interactive continuation are ignored.
     */
    String translateV2toV3(MessageAdapter rsp, String origMessage) {
        def output = new ByteArrayOutputStream()
        def builder = getBuilder(output)

        def xml = slurp(origMessage)

        def messageTimestamp = dropTimeZone(rsp.MSH[7][1].value) ?: MessageUtils.hl7Now()
        def status = getStatusInformation(rsp, xml)
        
        builder.PRPA_IN201306UV02(
                'ITSVersion': 'XML_1.0',
                'xmlns':      HL7V3_NSURI,
                'xmlns:xsi':  'http://www.w3.org/2001/XMLSchema-instance',
                'xmlns:xsd':  'http://www.w3.org/2001/XMLSchema') 
        {
            buildInstanceIdentifier(builder, 'id', false, this.messageIdRoot, rsp.MSH[10].value)
            creationTime(value: MessageUtils.hl7Now())
            interactionId(root: '2.16.840.1.113883.1.6', extension: 'PRPA_IN201306UV02')
            processingCode(code: 'P')
            processingModeCode(code: 'T')
            acceptAckCode(code: 'NE')
            buildReceiverAndSender(builder, xml, HL7V3_NSURI)
            createQueryAcknowledgementElement(builder, xml, status, this.errorCodeSystem, this.ackCodeFirstCharacter) 
            controlActProcess(classCode: 'CACT', moodCode: 'EVN') {
                code(code: 'PRPA_TE201306UV02', codeSystem: '2.16.840.1.113883.1.6')
                effectiveTime(value: messageTimestamp)
                
                if (rsp.MSH[9][1].value == 'RSP') {
                    final Set<String> requestedDomains = rsp.QPD[8]().collect { it[4][2].value } as Set

                    rsp.QUERY_RESPONSE().each { qr ->
                        def pid3collection = qr.PID[3]()
                        Set<String> remainingDomains = (Set<String>) requestedDomains.clone()
                        if (pid3collection) {
                            subject(typeCode: 'SUBJ') {
                                registrationEvent(classCode: 'REG', moodCode: 'EVN') {
                                    statusCode(code: 'active')
                                    subject1(typeCode: 'SBJ') {
                                        patient(classCode: 'PAT') {
                                            for(pid3 in pid3collection) {   
                                                buildInstanceIdentifier(builder, 'id', false, 
                                                        pid3[4][2].value, pid3[1].value, pid3[4][1].value)
                                                remainingDomains.remove(pid3[4][2].value)
                                            }
                                            statusCode(code: 'active')
                                            patientPerson(classCode: 'PSN', determinerCode: 'INSTANCE') {
                                                for (pid5 in qr.PID[5]()) {
                                                    createName(builder, pid5) 
                                                }

                                                translateTelecom(builder, qr.PID[13], 'H')
                                                translateTelecom(builder, qr.PID[14], 'WP')

                                                def gender = (qr.PID[8].value ?: '').mapReverse('hl7v2v3-bidi-administrativeGender-administrativeGender')
                                                administrativeGenderCode(code: gender)
                                                birthTime(value: qr.PID[7][1].value ?: '')
                                                addr {
                                                    def pid11 = qr.PID[11]
                                                    conditional(builder, 'country',           pid11[6].value)
                                                    conditional(builder, 'state',             pid11[4].value)
                                                    conditional(builder, 'postalCode',        pid11[5].value)
                                                    conditional(builder, 'city',              pid11[3].value)
                                                    conditional(builder, 'streetAddressLine', pid11[1][1].value)
                                                }
                                                
                                                def pid4collection = qr.PID[4]()
                                                if (pid4collection) {
                                                    asOtherIDs(classCode: 'PAT') {
                                                        for(pid4 in pid4collection) {   
                                                            buildInstanceIdentifier(builder, 'id', false, 
                                                                    pid4[4][2].value, pid4[1].value, pid4[4][1].value) 
                                                        }
                                                        scopingOrganization(classCode: 'ORG', determinerCode: 'INSTANCE') {
                                                            id(nullFlavor: 'UNK')
                                                        }
                                                    }
                                                }

                                                if (qr.PID[19].value) {
                                                    asOtherIDs(classCode: 'PAT') {
                                                        id(root: nationalIdentifierRoot, extension: qr.PID[19].value)
                                                        scopingOrganization(classCode: 'ORG', determinerCode: 'INSTANCE') {
                                                            id(root: nationalIdentifierRoot)
                                                        }
                                                    }
                                                }

                                                if (nullFlavorWhenScopingOrganizationIdNotFound) {
                                                    for (oid in remainingDomains) {
                                                        asOtherIDs(classCode: 'PAT') {
                                                            id(nullFlavor: nullFlavorWhenScopingOrganizationIdNotFound)
                                                            scopingOrganization(classCode: 'ORG', determinerCode: 'INSTANCE') {
                                                                id(root: oid)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            subjectOf1 {
                                                createQueryMatchObservation(builder, qr)
                                            }
                                        }
                                    }
                                    createCustodian(builder, this.mpiSystemIdRoot, this.mpiSystemIdExtension)
                                }
                            }                            
                        }
                    }
                }
                
                queryAck {
                    int patientCount = (rsp.MSH[9][1].value == 'RSP') ? rsp.QUERY_RESPONSE().size() : 0
                    
                    def queryId = xml.controlActProcess.queryByParameter.queryId
                    buildInstanceIdentifier(builder, 'queryId', false, 
                            queryId.@root.text(), queryId.@extension.text())

                    queryResponseCode(code: status.responseStatus)
                    if (this.outputResultTotalQuantity) {
                        resultTotalQuantity(value: patientCount)
                    }
                    resultCurrentQuantity(value: patientCount)
                    if (this.outputResultRemainingQuantity) {
                        resultRemainingQuantity(value: '0')
                    }
                }
                
                XmlYielder.yieldElement(xml.controlActProcess.queryByParameter, builder, HL7V3_NSURI)
            }
        }

        return output.toString()
    }

     
    private Map getStatusInformation(MessageAdapter rsp, GPathResult xml) {
        def responseStatus = 'OK'
        def errorText      = ''
        def errorCode      = ''
        def errorLocations = []
        def ackCode        = rsp.MSA[1].value
        
        if (ackCode[1] == 'A') {
            if (rsp.MSH[9][1].value == 'RSP') {
                responseStatus = rsp.QAK[2].value ?: ''
            }
        } else {
            responseStatus = ackCode
            errorCode = rsp.ERR[3][1].value ?: ''
            errorText = "PDQv2 Interface Reported [${collectErrorInfo(rsp)}]"
            errorLocations = rsp.ERR[2]()?.collect { err2 -> getV3ErrorLocation(err2, xml) }
        } 
        
        return [ackCode:        ackCode, 
                errorText:      errorText, 
                errorCode:      errorCode, 
                errorLocations: errorLocations, 
                responseStatus: responseStatus]
    }
    
    
    /**
     * Constructs an v3 error location string from the given v2 ERR-2 field. 
     */
    String getV3ErrorLocation(CompositeAdapter err2, GPathResult xml) { 
        if ((err2[1].value == 'QPD') && (err2[2].value == '1')) {
            def location = '/' + xml.interactionId.@extension.text() + '/controlActProcess/queryByParameter'
            if (err2[3].value == '8') {
                location += '/parameterList/otherIDsScopingOrganization'
                if (err2[4].value) {
                    location += "[${Integer.parseInt(err2[4].value) + 1 - ErrorLocation.fieldRepetitionIndexingBase}]"
                } 
            }
            return location
        }
        return '/'
    }


    /**
     * Default (dummy) creation of the queryMatchObservation element,
     * to be customized in derived classes.
     */
    void createQueryMatchObservation(MarkupBuilder builder, GroupAdapter grp) {
        builder.queryMatchObservation(classCode: 'COND', moodCode: 'EVN') {
            code(code: 'IHE_PDQ')
            value('xmlns:xsi': 'http://www.w3.org/2001/XMLSchema-instance', 
                  'xsi:type': 'INT', 
                  value: this.defaultMatchQuality)
        }
    }


    void translateTelecom(MarkupBuilder builder, SelectorClosure repeatableXTN, String defaultUse) {
        repeatableXTN().each { telecom ->
            String number = telecom[1].value ?: telecom[4].value
            if (number) {
                String use = defaultUse
                String schema = 'tel'
                
                switch (telecom[2].value) {
                case 'PRN':
                    use = 'H'
                    break
                case 'WPN':
                    use = 'WP'
                    break
                }
                    
                switch (telecom[3].value) {
                case 'PH':
                    // take the defaults
                    break
                case 'CP':
                    use = 'MC'
                    break
                case 'FX':
                    schema = 'fax'
                    break
                case 'Internet':
                case 'X.400':
                    schema = 'mailto'
                    break
                }
                builder.telecom(value: "${schema}:${number}", use: use)
            }
        }
    }
}
