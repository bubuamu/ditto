/*
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.ditto.services.connectivity.mapping.javascript;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

import org.eclipse.ditto.services.connectivity.mapping.DefaultMessageMapperConfiguration;
import org.eclipse.ditto.services.connectivity.mapping.MessageMapperConfiguration;

/**
 * Immutable implementation of {@link JavaScriptMessageMapperConfiguration}.
 */
@Immutable
final class ImmutableJavaScriptMessageMapperConfiguration implements JavaScriptMessageMapperConfiguration {

    private final MessageMapperConfiguration delegationTarget;

    private ImmutableJavaScriptMessageMapperConfiguration(final MessageMapperConfiguration theDelegationTarget) {
        delegationTarget = theDelegationTarget;
    }

    @Override
    public Map<String, String> getProperties() {
        return delegationTarget.getProperties();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ImmutableJavaScriptMessageMapperConfiguration that = (ImmutableJavaScriptMessageMapperConfiguration) o;
        return Objects.equals(delegationTarget, that.delegationTarget);
    }

    @Override
    public int hashCode() {
        return Objects.hash(delegationTarget);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" +
                "properties=" + getProperties() +
                "]";
    }

    /**
     * Mutable Builder for {@link JavaScriptMessageMapperConfiguration}.
     */
    @NotThreadSafe
    static final class Builder implements JavaScriptMessageMapperConfiguration.Builder {

        private final Map<String, String> properties;

        Builder(final Map<String, String> properties) {
            this.properties = new HashMap<>(properties); // mutable map!
        }

        @Override
        public Map<String, String> getProperties() {
            return properties;
        }

        @Override
        public JavaScriptMessageMapperConfiguration build() {
            return new ImmutableJavaScriptMessageMapperConfiguration(DefaultMessageMapperConfiguration.of(properties));
        }

    }

}
