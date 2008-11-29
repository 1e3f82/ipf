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
package org.openehealth.ipf.modules.hl7dsl

import static org.openehealth.ipf.modules.hl7dsl.AdapterHelper.adaptStructure
import static org.openehealth.ipf.modules.hl7dsl.util.Messages.copyMessage

import ca.uhn.hl7v2.model.Message
import ca.uhn.hl7v2.parser.Parser

/**
 * @author Martin Krasser
 */
class MessageAdapter extends GroupAdapter implements Writable {

    Parser parser

    MessageAdapter(Message message) {
        this(MessageAdapters.defaultParser(), message)
    }

    MessageAdapter(Parser parser, Message message) {
        super(message)
        this.parser = parser
    }

    MessageAdapter empty() {
        adaptStructure(group.class.newInstance())
    }
    
    MessageAdapter copy() {
        MessageAdapter copy = empty()
        adaptStructure(copyMessage(this.target, copy.target))
        return copy
    }

    Writer writeTo(Writer writer) {
        String s = parser.encode(group)
        writer.write(s)
        writer.flush()
    }
    
    String toString() {
        def writer = new StringWriter()
        writer << this
        writer.buffer.toString()
    }

    boolean matches(String code, String type, String version) {
    	
    	// MSH-12 can be either Primitive or Composite (starting with v2.4)
    	def messageVersion = get('MSH')[12]
    	if (messageVersion instanceof CompositeAdapter) {
    		messageVersion = messageVersion[1]
    	}
    	(code == '*' || code == get('MSH')[9][1].toString()) && 
    	(type == '*' || type == get('MSH')[9][2].toString()) && 
    	(version == '*' || version == messageVersion.toString())    
    }

    
}