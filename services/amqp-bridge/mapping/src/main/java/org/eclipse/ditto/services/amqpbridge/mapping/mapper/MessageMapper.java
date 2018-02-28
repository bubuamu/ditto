/*
 * Copyright (c) 2017 Bosch Software Innovations GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/org/documents/epl-2.0/index.php
 *
 * Contributors:
 *    Bosch Software Innovations GmbH - initial contribution
 */
package org.eclipse.ditto.services.amqpbridge.mapping.mapper;

import org.eclipse.ditto.model.amqpbridge.ExternalMessage;
import org.eclipse.ditto.model.amqpbridge.MessageMapperConfigurationInvalidException;
import org.eclipse.ditto.protocoladapter.Adaptable;

/**
 * Defines a message mapper which maps a {@link org.eclipse.ditto.model.amqpbridge.ExternalMessage} to a
 * {@link org.eclipse.ditto.protocoladapter.Adaptable} and vice versa.
 * <p>
 * Usually a mapper is bound to a content type.
 * </p>
 * A message mapper is considered to be dynamically instantiated at runtime, it therefore can only be configured at
 * runtime.
 */
public interface MessageMapper {

    /**
     * Returns the content type of this mapper. This can be used as a hint for mapper selection.
     *
     * @return the content type
     */
    String getContentType();

    /**
     * TODO TJ rethink this pattern with the default method for validation - seems wrong
     *
     * @param configuration
     * @throws MessageMapperConfigurationInvalidException if configuration is invalid
     */
    default void configureWithValidation(final MessageMapperConfiguration configuration) {
        if (!configuration.findContentType().isPresent()) {
            throw MessageMapperConfigurationInvalidException
                    .newBuilder(MessageMapperConfigurationProperties.CONTENT_TYPE)
                    .build();
        }
        configure(configuration);
    }

    /**
     * @param configuration
     * @throws MessageMapperConfigurationInvalidException if configuration is invalid
     */
    void configure(MessageMapperConfiguration configuration);

    /**
     * Maps an {@link org.eclipse.ditto.model.amqpbridge.ExternalMessage} to an {@link org.eclipse.ditto.protocoladapter.Adaptable}
     *
     * @param message
     * @return
     * @throws org.eclipse.ditto.model.amqpbridge.MessageMappingFailedException if the given message can not be mapped
     * @throws org.eclipse.ditto.model.base.exceptions.DittoRuntimeException if anything during Ditto Adaptable creation
     * went wrong
     */
    Adaptable map(ExternalMessage message);

    /**
     * Maps an {@link org.eclipse.ditto.protocoladapter.Adaptable} to an {@link org.eclipse.ditto.model.amqpbridge.ExternalMessage}
     *
     * @param adaptable the adaptable
     * @return the message
     * @throws org.eclipse.ditto.model.amqpbridge.MessageMappingFailedException if the given adaptable can not be mapped
     */
    ExternalMessage map(Adaptable adaptable);
}
