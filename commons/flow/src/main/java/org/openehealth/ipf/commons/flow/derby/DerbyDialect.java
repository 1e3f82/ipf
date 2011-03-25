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
package org.openehealth.ipf.commons.flow.derby;

/**
 * <b>Note: This dialect should not be used with Hibernate version 3.5.x and
 * above.</b><br>
 * Starting with Hibernate 3.5.x use the
 * {@link org.hibernate.dialect.DerbyDialect} implementation. It includes the
 * repeatable read isolation level below, as well as many other improvements for
 * Derby.
 * 
 * @author Martin Krasser
 */
@Deprecated
public class DerbyDialect extends org.hibernate.dialect.DerbyDialect {

    @Override
    public String getForUpdateString() {
        // for update with repeatable
        // read isolation level (rs).
        // (see also Derby dev. guide)
        return " for update with rs";
    }

}
