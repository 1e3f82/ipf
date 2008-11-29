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
package org.openehealth.ipf.platform.camel.core.process.splitter.support;

import static org.apache.camel.util.ObjectHelper.notNull;

/**
 * Split logic for use with the {@link TextFileIterator} that splits read lines
 * using a regex similar to {@link String#split(String)}.
 * 
 * @author Jens Riemschneider
 */
public class SplitStringLineSplitterLogic implements LineSplitterLogic {
    private String regex;
    
    /**
     * Creates the split logic
     * @param regex
     *          split regex as defined in {@link String#split(String)} 
     */
    public SplitStringLineSplitterLogic(String regex) {
        notNull(regex, "regex");
        this.regex = regex;
    }

    /* (non-Javadoc)
     * @see org.openehealth.ipf.platform.camel.core.process.splitter.support.LineSplitterLogic#splitLine(java.lang.String)
     */
    @Override
    public String[] splitLine(String line) {
        return line.split(regex);
    }

}
