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
package org.eclipse.ditto.services.connectivity.messaging.amqp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;
import javax.jms.CompletionListener;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

import org.apache.qpid.jms.JmsSession;
import org.apache.qpid.jms.message.JmsMessage;
import org.apache.qpid.jms.message.JmsTextMessage;
import org.apache.qpid.jms.provider.amqp.message.AmqpJmsTextMessageFacade;
import org.eclipse.ditto.model.base.headers.DittoHeaders;
import org.eclipse.ditto.model.connectivity.ConnectivityModelFactory;
import org.eclipse.ditto.model.connectivity.Target;
import org.eclipse.ditto.model.connectivity.Topic;
import org.eclipse.ditto.services.connectivity.messaging.AbstractPublisherActorTest;
import org.eclipse.ditto.services.connectivity.messaging.TestConstants;
import org.eclipse.ditto.services.models.connectivity.ExternalMessage;
import org.eclipse.ditto.services.models.connectivity.ExternalMessageFactory;
import org.eclipse.ditto.services.models.connectivity.OutboundSignal;
import org.eclipse.ditto.services.models.connectivity.OutboundSignalFactory;
import org.eclipse.ditto.signals.base.Signal;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.stubbing.Answer;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.testkit.TestProbe;
import akka.testkit.javadsl.TestKit;

public class AmqpPublisherActorTest extends AbstractPublisherActorTest {


    private JmsSession session;
    private MessageProducer messageProducer;

    @Override
    protected void setupMocks(final TestProbe probe) throws JMSException {
        session = mock(JmsSession.class);
        messageProducer = mock(MessageProducer.class);
        when(session.createProducer(ArgumentMatchers.any(Destination.class))).thenReturn(messageProducer);
        when(session.createTextMessage(anyString())).thenAnswer((Answer<JmsMessage>) invocation -> {
            final String argument = invocation.getArgument(0);
            final JmsTextMessage jmsTextMessage = new JmsTextMessage(new AmqpJmsTextMessageFacade());
            jmsTextMessage.setText(argument);
            return jmsTextMessage;
        });
    }
    
    @Test
    public void testRecoverPublisher() throws Exception {

        new TestKit(actorSystem) {{
            final TestProbe probe = new TestProbe(actorSystem);
            setupMocks(probe);
            
            final OutboundSignal outboundSignal = mock(OutboundSignal.class);
            final Signal source = mock(Signal.class);
            when(source.getId()).thenReturn(TestConstants.Things.THING_ID);
            when(source.getDittoHeaders()).thenReturn(DittoHeaders.empty());
            when(outboundSignal.getSource()).thenReturn(source);
            final Target target = createTestTarget();
            when(outboundSignal.getTargets()).thenReturn(Collections.singletonList(decorateTarget(target)));


            final DittoHeaders dittoHeaders = DittoHeaders.newBuilder().putHeader("device_id", "ditto:thing").build();
            final ExternalMessage externalMessage =
                    ExternalMessageFactory.newExternalMessageBuilder(dittoHeaders).withText("payload").build();
            final OutboundSignal.WithExternalMessage mappedOutboundSignal =
                    OutboundSignalFactory.newMappedOutboundSignal(outboundSignal, externalMessage);

            final Props props = getPublisherActorProps();
            final ActorRef publisherActor = actorSystem.actorOf(props);

            publisherActor.tell(mappedOutboundSignal, getRef());
            publisherActor.tell(mappedOutboundSignal, getRef());
            // producer is cache so created only once
            verify(session, timeout(1_000)).createProducer(ArgumentMatchers.any(Destination.class));
            
            publisherActor.tell(StatusReport.producerClosed(messageProducer), getRef());
            
            publisherActor.tell(mappedOutboundSignal, getRef());
            // second producer created as first one was removed from cache
            verify(session, timeout(1_000).times(2)).createProducer(ArgumentMatchers.any(Destination.class));            
        }};

    }

    @Override
    protected Props getPublisherActorProps() {
        return AmqpPublisherActor.props("theConnection", Collections.emptyList(), session);
    }

    @Override
    protected void verifyPublishedMessage() throws Exception {
        final ArgumentCaptor<JmsMessage> messageCaptor = ArgumentCaptor.forClass(JmsMessage.class);

        verify(messageProducer, timeout(1000)).send(messageCaptor.capture(), any(CompletionListener.class));

        final Message message = messageCaptor.getValue();
        assertThat(message).isNotNull();
        System.out.println(Collections.list(message.getPropertyNames()));
        assertThat(message.getStringProperty("thing_id")).isEqualTo(TestConstants.Things.THING_ID);
        assertThat(message.getStringProperty("suffixed_thing_id")).isEqualTo(
                TestConstants.Things.THING_ID + ".some.suffix");
        assertThat(message.getStringProperty("prefixed_thing_id")).isEqualTo(
                "some.prefix." + TestConstants.Things.THING_ID);
        assertThat(message.getStringProperty("eclipse")).isEqualTo("ditto");
        assertThat(message.getStringProperty("device_id")).isEqualTo(TestConstants.Things.THING_ID);
    }

    @Override
    protected void publisherCreated(final ActorRef publisherActor) {
        // nothing to do
    }

    @Override
    protected Target decorateTarget(final Target target) {
        return target;
    }

    protected String getOutboundAddress() {
        return "outbound";
    }
}
