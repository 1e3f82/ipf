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
package org.openehealth.ipf.platform.camel.ihe.hl7v2

import org.openehealth.ipf.modules.hl7dsl.MessageAdapter


/**
 * Groovy subroutines for HL7 message acceptance checks.
 * @author Dmytro Rud
 */
class AcceptanceCheckUtils {

     private AcceptanceCheckUtils() {
         throw new IllegalStateException('Helper class, do not instantiate');
     }

     
    /**
     * Performs transaction-specific acceptance test of the given request message.
     */
     static void checkRequestAcceptance(
             MessageAdapter msg, 
             Hl7v2TransactionConfiguration config) throws Hl7v2AcceptanceException
     {
         checkMessageAcceptance(msg, config, 'Request')
     }
     
     
     /**
      * Performs transaction-specific acceptance test of the given response message.
      */
     static void checkResponseAcceptance(
             MessageAdapter msg, 
             Hl7v2TransactionConfiguration config) throws Hl7v2AcceptanceException
     {
         checkMessageAcceptance(msg, config, 'Response')
         
         if( ! ['AA', 'AR', 'AE', 'CA', 'CR', 'CE'].contains(msg.MSA[1]?.value)) {
             throw new Hl7v2AcceptanceException("Bad response: missing or invalid MSA segment")
         }
     }
      
      
     /**
      * Performs acceptance test of the given message.
      * @param msg
      *          {@link MessageAdapter} representing the message.
      * @param config
      *          transaction configuration.
      * @param direction
      *          either 'Request' or 'Response'.
      * @throws Hl7v2AcceptanceException
      *          when the message cannot be accepted.
      */
     private static void checkMessageAcceptance(
             MessageAdapter msg, 
             Hl7v2TransactionConfiguration config,
             String direction) throws Hl7v2AcceptanceException
     {
         def version = msg.MSH[12].value 
         if(version != config.hl7Version) {
             throw new Hl7v2AcceptanceException("Invalid HL7 version ${version}", 203)
         }
         
         def msgType = msg.MSH[9][1].value
         if( ! config."isSupported${direction}MessageType"(msgType)) {
             throw new Hl7v2AcceptanceException("Invalid message type ${msgType}", 200)
         }

         def triggerEvent = msg.MSH[9][2].value
         if( ! config."isSupported${direction}TriggerEvent"(msgType, triggerEvent)) {
             throw new Hl7v2AcceptanceException("Invalid trigger event ${triggerEvent}", 201)
         }

         def structure = msg.MSH[9][3].value
         if(structure) {
             def expected = config.parser.getMessageStructureForEvent("${msgType}_${triggerEvent}", version)
             
             // the expected structure must be equal to the actual one,
             // but second components may be omitted in acknowledgements 
             if( ! ((structure == expected) || 
                    (structure.startsWith('ACK') && expected.startsWith('ACK'))))
             {
                 throw new Hl7v2AcceptanceException("Invalid structure map ${structure}", 204)
             }
         }
     }

}
