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
package org.eclipse.ditto.signals.commands.things.exceptions;

import java.net.URI;
import java.text.MessageFormat;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.model.base.common.HttpStatusCode;
import org.eclipse.ditto.model.base.exceptions.DittoRuntimeException;
import org.eclipse.ditto.model.base.exceptions.DittoRuntimeExceptionBuilder;
import org.eclipse.ditto.model.base.headers.DittoHeaders;
import org.eclipse.ditto.model.base.json.JsonParsableException;
import org.eclipse.ditto.model.things.ThingException;

/**
 * Indicates that the Feature Definition cannot be modified.
 */
@Immutable
@JsonParsableException(errorCode = FeatureDefinitionNotModifiableException.ERROR_CODE)
public final class FeatureDefinitionNotModifiableException extends DittoRuntimeException implements ThingException {

    /**
     * Error code of this exception.
     */
    public static final String ERROR_CODE = ERROR_CODE_PREFIX + "feature.definition.notmodifiable";

    private static final String MESSAGE_TEMPLATE = "The Definition of the Feature with ID ''{0}'' on the Thing with ID "
            + "''{1}'' cannot be modified as the requester had insufficient permissions to modify it"
            + " ('WRITE' is required).";

    private static final String DEFAULT_DESCRIPTION =
            "Check if the ID of the Thing and the Feature ID was correct and you have sufficient permissions.";

    private static final long serialVersionUID = 8143172876483707519L;

    private FeatureDefinitionNotModifiableException(final DittoHeaders dittoHeaders,
            @Nullable final String message,
            @Nullable final String description,
            @Nullable final Throwable cause,
            @Nullable final URI href) {
        super(ERROR_CODE, HttpStatusCode.FORBIDDEN, dittoHeaders, message, description, cause, href);
    }

    /**
     * A mutable builder for a {@code FeatureDefinitionNotModifiableException}.
     *
     * @param thingId the ID of the thing.
     * @param featureId the ID of the feature.
     * @return the builder.
     */
    public static FeatureDefinitionNotModifiableException.Builder newBuilder(final String thingId,
            final String featureId) {

        return new FeatureDefinitionNotModifiableException.Builder(thingId, featureId);
    }

    /**
     * Constructs a new {@code FeatureDefinitionNotModifiableException} object with given message.
     *
     * @param message detail message. This message can be later retrieved by the {@link #getMessage()} method.
     * @param dittoHeaders the headers of the command which resulted in this exception.
     * @return the new FeatureDefinitionNotModifiableException.
     */
    public static FeatureDefinitionNotModifiableException fromMessage(final String message,
            final DittoHeaders dittoHeaders) {

        return new FeatureDefinitionNotModifiableException.Builder()
                .dittoHeaders(dittoHeaders)
                .message(message)
                .build();
    }

    /**
     * Constructs a new {@code FeatureDefinitionNotModifiableException} object with the exception message extracted from
     * the given JSON object.
     *
     * @param jsonObject the JSON to read the {@link JsonFields#MESSAGE} field from.
     * @param dittoHeaders the headers of the command which resulted in this exception.
     * @return the new FeatureDefinitionNotModifiableException.
     * @throws org.eclipse.ditto.json.JsonMissingFieldException if the {@code jsonObject} does not have the
     * {@link JsonFields#MESSAGE} field.
     */
    public static FeatureDefinitionNotModifiableException fromJson(final JsonObject jsonObject,
            final DittoHeaders dittoHeaders) {
        return new Builder()
                .dittoHeaders(dittoHeaders)
                .message(readMessage(jsonObject))
                .description(readDescription(jsonObject).orElse(DEFAULT_DESCRIPTION))
                .href(readHRef(jsonObject).orElse(null))
                .build();
    }

    /**
     * A mutable builder with a fluent API for a {@link FeatureDefinitionNotModifiableException}.
     */
    @NotThreadSafe
    public static final class Builder extends DittoRuntimeExceptionBuilder<FeatureDefinitionNotModifiableException> {

        private Builder() {
            description(DEFAULT_DESCRIPTION);
        }

        private Builder(final String thingId, final String featureId) {
            this();
            message(MessageFormat.format(MESSAGE_TEMPLATE, featureId, thingId));
        }

        @Override
        protected FeatureDefinitionNotModifiableException doBuild(final DittoHeaders dittoHeaders,
                @Nullable final String message,
                @Nullable final String description,
                @Nullable final Throwable cause,
                @Nullable final URI href) {
            return new FeatureDefinitionNotModifiableException(dittoHeaders, message, description, cause, href);
        }

    }

}
