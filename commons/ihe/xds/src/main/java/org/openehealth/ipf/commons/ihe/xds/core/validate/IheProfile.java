/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openehealth.ipf.commons.ihe.xds.core.validate;

/**
 * Profiles supported by validation classes in this package.
 * @author Dmytro Rud
 */
public enum IheProfile {
    XDS_A, XDS_B, XCA, ContinuaHRN;

    public boolean isEbXml30Based() {
        return (this == XDS_B) || (this == XCA) || (this == ContinuaHRN);
    }
}
