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
package org.openehealth.ipf.platform.manager.connection;

/**
 * Event generated by the connection management.
 * 
 * @see IConnectionConfiguration
 * 
 * @author Mitko Kolev
 */
public class ConnectionEvent {

    public final static int CONNECTION_ADDED = 1;
    /**
     * Implies Connection Closed.
     */
    public final static int CONNECTION_REMOVED = 2;

    public final static int JMX_CONNECTION_OPEN = 4;

    public final static int JMX_CONNECTION_CLOSED = 8;

    private final int type;

    private final IConnectionConfiguration connectionConfiguration;

    private final Object sessionContext;

    private final Object value;

    /**
     * Creates a new ConnectionEvent.
     * 
     * @param connectionConfiguration
     *            The connection context of this event.
     * @param type
     *            the type of this event. The type Can be defined by subclasses.
     * @param value
     *            a value object. Could be of any type
     * @param sessionContext
     *            reserved for sessions
     */
    public ConnectionEvent(IConnectionConfiguration connectionConfiguration,
            int type, Object value, Object sessionContext) {
        this.type = type;
        this.connectionConfiguration = connectionConfiguration;
        this.sessionContext = sessionContext;
        this.value = value;
    }

    /**
     * Creates a new ConnectionEvent.
     * 
     * @param connectionConfiguration
     *            The connection context of this event.
     * @param type
     *            The type of this event. The type Can be defined by subclasses.
     * @param value
     *            A value object. Could be of any type.
     */
    public ConnectionEvent(IConnectionConfiguration connectionConfiguration,
            int type, Object value) {
        this.type = type;
        this.connectionConfiguration = connectionConfiguration;
        this.sessionContext = new Object();
        this.value = value;
    }

    /**
     * Creates a new ConnectionEvent.
     * 
     * @param connectionConfiguration
     *            The connection context of this event.
     * @param type
     *            The type of this event. The type Can be defined by subclasses.
     */
    public ConnectionEvent(IConnectionConfiguration connectionConfiguration,
            int type) {
        this.type = type;
        this.connectionConfiguration = connectionConfiguration;
        this.sessionContext = new Object();
        this.value = new Object();
    }

    /**
     * @return
     */
    public IConnectionConfiguration getConnectionConfigurationContext() {
        return connectionConfiguration;
    }

    /**
     * Currently not used, intended to be used in RAP application.
     * 
     * @return
     */
    public Object getSessionContext() {
        return sessionContext;
    }

    /**
     * The int code of the type of this event.
     * 
     * @return
     */
    public int getType() {
        return type;
    }

    /**
     * Returns the value which is associated with the event. The semantics of
     * the value are defined in the child classes.
     * 
     * @return
     */
    public Object getValue() {
        return value;
    }
}
