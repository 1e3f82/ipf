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
package org.openehealth.ipf.modules.cda.builder

import org.openhealthtools.ihe.common.cdar2.*
import org.junit.Test
import org.junit.Assert
import groovytools.builder.NodeException
/**
 * @author Christian Ohr
 */
public class CDAR2DataTypeTest extends AbstractCDAR2BuilderTest {
	
	@Test
	public void testCEWithNullFlavor() {
		CE ce = builder.build {
			ce(nullFlavor:'UNK', code:'bla')
		}
		assert ce != null
		assert ce.nullFlavor != null
		assert ce.code == null
	}
	
	@Test
	public void testIIWithNullFlavor() {
		II ii = builder.build {
			ii(nullFlavor:'UNK', root:'1.2.3')
		}
		assert ii != null
		assert ii.nullFlavor != null
		assert ii.root == null
	}
	
	@Test
	public void testCEWithNoNullFlavor() {
		try {
			CE ce = builder.build { ce(codeSystem:'bla') }
			fail('Building ce without code must fail')
		} catch (NodeException e) {
			assert e.message.contains('ce')
		}
	}
	
	@Test
	public void testCDWithNoNullFlavor() {
		try {
			CD cd = builder.build { cd(codeSystem:'bla') }
			fail('Building cd without code must fail')
		} catch (NodeException e) {
			assert e.message.contains('cd')
		}
	}  
	
	
	
}
