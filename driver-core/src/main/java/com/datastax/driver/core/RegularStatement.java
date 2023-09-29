/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.datastax.driver.core;

import java.nio.ByteBuffer;

/**
 * A regular (non-prepared and non batched) CQL statement.
 * <p/>
 * This class represents a query string along with query options (and optionally
 * binary values, see {@code getValues}). It can be extended but {@link SimpleStatement}
 * is provided as a simple implementation to build a {@code RegularStatement} directly
 * from its query string.
 */
public abstract class RegularStatement extends Statement {

    /**
     * Creates a new RegularStatement.
     */
    protected RegularStatement() {
    }

    /**
     * Returns the query string for this statement.
     * <p/>
     * It is important to note that the query string is merely
     * a CQL representation of this statement, but it does
     * <em>not</em> convey all the information stored in {@link Statement}
     * objects.
     * <p/>
     * For example, {@link Statement} objects carry numerous protocol-level
     * settings, such as the {@link Statement#getConsistencyLevel() consistency level} to use,
     * or the {@link Statement#isIdempotent() idempotence flag}, among others.
     * <em>None of these settings will be included in the resulting query string.</em>
     * <p/>
     * Similarly, if values have been set on this statement because
     * it has bind markers, these values will not appear in the resulting query string.
     * <p/>
     * Note: the consistency level was conveyed at CQL level in older versions
     * of the CQL grammar, but since <a href="https://issues.apache.org/jira/browse/CASSANDRA-4734">CASSANDRA-4734</a>
     * it is now a protocol-level setting and consequently does not appear in the query string.
     *
     * @return a valid CQL query string.
     */
    public abstract String getQueryString();

    /**
     * The values to use for this statement.
     * <p/>
     * Note: Values for a RegularStatement (i.e. if this method does not return
     * {@code null}) are not supported with the native protocol version 1: you
     * will get an {@link UnsupportedProtocolVersionException} when submitting
     * one if version 1 of the protocol is in use (i.e. if you've force version
     * 1 through {@link Cluster.Builder#withProtocolVersion} or you use
     * Cassandra 1.2).
     *
     * @param protocolVersion the protocol version in which the returned values
     *                        must be serialized for.
     * @return the values to use for this statement or {@code null} if there is
     * no such values.
     * @see SimpleStatement#SimpleStatement(String, Object...)
     */
    public abstract ByteBuffer[] getValues(ProtocolVersion protocolVersion);

    /**
     * The values to use for this statement, for the given numeric protocol version.
     *
     * @throws IllegalArgumentException if {@code protocolVersion} does not correspond to any known version.
     * @deprecated This method is provided for backward compatibility. Use
     * {@link #getValues(ProtocolVersion)} instead.
     */
    @Deprecated
    public ByteBuffer[] getValues(int protocolVersion) {
        return getValues(ProtocolVersion.fromInt(protocolVersion));
    }

    /**
     * @deprecated This method is provided for binary compatibility only. It is no longer supported, will be removed,
     * and simply throws {@link UnsupportedOperationException}. Use {@link #getValues(ProtocolVersion)} instead.
     */
    @Deprecated
    public ByteBuffer[] getValues() {
        throw new UnsupportedOperationException("Method no longer supported; use getValues(ProtocolVersion)");
    }

    /**
     * Whether or not this statement has values, that is if {@code getValues}
     * will return {@code null} or not.
     *
     * @return {@code false} if {@link #getValues} returns {@code null}, {@code true}
     * otherwise.
     */
    public abstract boolean hasValues();

    /**
     * Returns this statement as a CQL query string.
     * <p/>
     * It is important to note that the query string is merely
     * a CQL representation of this statement, but it does
     * <em>not</em> convey all the information stored in {@link Statement}
     * objects.
     * <p/>
     * See the javadocs of {@link #getQueryString()} for more information.
     *
     * @return this statement as a CQL query string.
     * @see #getQueryString()
     */
    @Override
    public String toString() {
        return getQueryString();
    }
}
