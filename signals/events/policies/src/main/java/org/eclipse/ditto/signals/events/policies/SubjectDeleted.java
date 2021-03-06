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
package org.eclipse.ditto.signals.events.policies;

import static org.eclipse.ditto.model.base.common.ConditionChecker.checkNotNull;

import java.time.Instant;
import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.eclipse.ditto.json.JsonFactory;
import org.eclipse.ditto.json.JsonField;
import org.eclipse.ditto.json.JsonFieldDefinition;
import org.eclipse.ditto.json.JsonObject;
import org.eclipse.ditto.json.JsonObjectBuilder;
import org.eclipse.ditto.json.JsonPointer;
import org.eclipse.ditto.model.base.headers.DittoHeaders;
import org.eclipse.ditto.model.base.json.FieldType;
import org.eclipse.ditto.model.base.json.JsonParsableEvent;
import org.eclipse.ditto.model.base.json.JsonSchemaVersion;
import org.eclipse.ditto.model.policies.Label;
import org.eclipse.ditto.model.policies.SubjectId;
import org.eclipse.ditto.signals.events.base.EventJsonDeserializer;

/**
 * This event is emitted after a {@link org.eclipse.ditto.model.policies.Subject} was deleted.
 */
@Immutable
@JsonParsableEvent(name = SubjectDeleted.NAME, typePrefix= SubjectDeleted.TYPE_PREFIX)
public final class SubjectDeleted extends AbstractPolicyEvent<SubjectDeleted> implements PolicyEvent<SubjectDeleted> {

    /**
     * Name of this event.
     */
    public static final String NAME = "subjectDeleted";

    /**
     * Type of this event.
     */
    public static final String TYPE = TYPE_PREFIX + NAME;

    static final JsonFieldDefinition<String> JSON_LABEL =
            JsonFactory.newStringFieldDefinition("label", FieldType.REGULAR, JsonSchemaVersion.V_2);

    static final JsonFieldDefinition<String> JSON_SUBJECT_ID =
            JsonFactory.newStringFieldDefinition("subjectId", FieldType.REGULAR, JsonSchemaVersion.V_2);

    private final Label label;
    private final SubjectId subjectId;

    private SubjectDeleted(final String policyId,
            final Label label,
            final SubjectId subjectId,
            final long revision,
            @Nullable final Instant timestamp,
            final DittoHeaders dittoHeaders) {

        super(TYPE, checkNotNull(policyId, "Policy identifier"), revision, timestamp, dittoHeaders);
        this.label = checkNotNull(label, "Label");
        this.subjectId = checkNotNull(subjectId, "Subject identifier");
    }

    /**
     * Constructs a new {@code SubjectDeleted} object.
     *
     * @param policyId the identifier of the Policy to which the deleted subject belongs.
     * @param label the label of the Policy Entry to which the deleted subject belongs.
     * @param subjectId the identifier of the deleted {@link org.eclipse.ditto.model.policies.Subject}.
     * @param revision the revision of the Policy.
     * @param dittoHeaders the headers of the command which was the cause of this event.
     * @return the created SubjectDeleted.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static SubjectDeleted of(final String policyId,
            final Label label,
            final SubjectId subjectId,
            final long revision,
            final DittoHeaders dittoHeaders) {

        return new SubjectDeleted(policyId, label, subjectId, revision, null, dittoHeaders);
    }

    /**
     * Constructs a new {@code SubjectDeleted} object.
     *
     * @param policyId the identifier of the Policy to which the deleted subject belongs.
     * @param label the label of the Policy Entry to which the deleted subject belongs.
     * @param subjectId the identifier of the deleted {@link org.eclipse.ditto.model.policies.Subject}.
     * @param revision the revision of the Policy.
     * @param timestamp the timestamp of this event.
     * @param dittoHeaders the headers of the command which was the cause of this event.
     * @return the created SubjectDeleted.
     * @throws NullPointerException if any argument but {@code timestamp} is {@code null}.
     */
    public static SubjectDeleted of(final String policyId,
            final Label label,
            final SubjectId subjectId,
            final long revision,
            @Nullable final Instant timestamp,
            final DittoHeaders dittoHeaders) {

        return new SubjectDeleted(policyId, label, subjectId, revision, timestamp, dittoHeaders);
    }

    /**
     * Creates a new {@code SubjectDeleted} from a JSON string.
     *
     * @param jsonString the JSON string from which a new SubjectDeleted instance is to be created.
     * @param dittoHeaders the headers of the command which was the cause of this event.
     * @return the {@code SubjectDeleted} which was created from the given JSON string.
     * @throws NullPointerException if {@code jsonString} is {@code null}.
     * @throws org.eclipse.ditto.json.JsonParseException if the passed in {@code jsonString} was not in the expected 'SubjectDeleted' format.
     */
    public static SubjectDeleted fromJson(final String jsonString, final DittoHeaders dittoHeaders) {
        return fromJson(JsonFactory.newObject(jsonString), dittoHeaders);
    }

    /**
     * Creates a new {@code SubjectDeleted} from a JSON object.
     *
     * @param jsonObject the JSON object from which a new SubjectDeleted instance is to be created.
     * @param dittoHeaders the headers of the command which was the cause of this event.
     * @return the {@code SubjectDeleted} which was created from the given JSON object.
     * @throws NullPointerException if {@code jsonObject} is {@code null}.
     * @throws org.eclipse.ditto.json.JsonParseException if the passed in {@code jsonObject} was not in the expected 'SubjectDeleted' format.
     */
    public static SubjectDeleted fromJson(final JsonObject jsonObject, final DittoHeaders dittoHeaders) {
        return new EventJsonDeserializer<SubjectDeleted>(TYPE, jsonObject).deserialize((revision, timestamp) -> {
            final String policyId = jsonObject.getValueOrThrow(JsonFields.POLICY_ID);
            final Label label = Label.of(jsonObject.getValueOrThrow(JSON_LABEL));
            final SubjectId extractedDeletedSubjectId =
                    SubjectId.newInstance(jsonObject.getValueOrThrow(JSON_SUBJECT_ID));

            return of(policyId, label, extractedDeletedSubjectId, revision, timestamp, dittoHeaders);
        });
    }

    /**
     * Returns the label of the Policy Entry to which the modified subject belongs.
     *
     * @return the label
     */
    public Label getLabel() {
        return label;
    }

    /**
     * Returns the deleted {@link SubjectId}.
     *
     * @return the deleted {@link SubjectId}.
     */
    public SubjectId getSubjectId() {
        return subjectId;
    }

    @Override
    public JsonPointer getResourcePath() {
        final String path = "/entries/" + label + "/subjects/" + subjectId;
        return JsonPointer.of(path);
    }

    @Override
    public SubjectDeleted setRevision(final long revision) {
        return of(getPolicyId(), label, subjectId, revision, getTimestamp().orElse(null), getDittoHeaders());
    }

    @Override
    public SubjectDeleted setDittoHeaders(final DittoHeaders dittoHeaders) {
        return of(getPolicyId(), label, subjectId, getRevision(), getTimestamp().orElse(null), dittoHeaders);
    }

    @Override
    protected void appendPayload(final JsonObjectBuilder jsonObjectBuilder, final JsonSchemaVersion schemaVersion,
            final Predicate<JsonField> thePredicate) {
        final Predicate<JsonField> predicate = schemaVersion.and(thePredicate);
        jsonObjectBuilder.set(JSON_LABEL, label.toString(), predicate);
        jsonObjectBuilder.set(JSON_SUBJECT_ID, subjectId.toString(), predicate);
    }

    @SuppressWarnings("squid:S109")
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hashCode(label);
        result = prime * result + Objects.hashCode(subjectId);
        return result;
    }

    @SuppressWarnings("squid:MethodCyclomaticComplexity")
    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (null == o || getClass() != o.getClass()) {
            return false;
        }
        final SubjectDeleted that = (SubjectDeleted) o;
        return that.canEqual(this) && Objects.equals(label, that.label) && Objects.equals(subjectId, that.subjectId)
                && super.equals(that);
    }

    @Override
    protected boolean canEqual(@Nullable final Object other) {
        return other instanceof SubjectDeleted;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [" + super.toString() + ", label=" + label + ", subjectId=" + subjectId
                + "]";
    }

}
