/*
 * Copyright (c) 2010 the original author or authors.
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

package org.cometd.server;

import java.util.List;

import org.cometd.bayeux.Message;
import org.cometd.common.HashMapMessage;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.http.HttpHeaders;

public class SubscriptionsWithMultipleChannelsTest extends AbstractBayeuxClientServerTest
{
    public void testSubscribeWithMultipleChannels() throws Exception
    {
        ContentExchange handshake = newBayeuxExchange("[{" +
                "\"channel\": \"/meta/handshake\"," +
                "\"version\": \"1.0\"," +
                "\"minimumVersion\": \"1.0\"," +
                "\"supportedConnectionTypes\": [\"long-polling\"]" +
                "}]");
        httpClient.send(handshake);
        assertEquals(HttpExchange.STATUS_COMPLETED, handshake.waitForDone());
        assertEquals(200, handshake.getResponseStatus());

        String clientId = extractClientId(handshake);
        String bayeuxCookie = extractBayeuxCookie(handshake);

        ContentExchange subscribe = newBayeuxExchange("[{" +
                "\"channel\": \"/meta/subscribe\"," +
                "\"clientId\": \"" + clientId + "\"," +
                "\"subscription\": [\"/foo\",\"/bar\"]" +
                "}]");
        subscribe.setRequestHeader(HttpHeaders.COOKIE, bayeuxCookie);
        httpClient.send(subscribe);
        assertEquals(HttpExchange.STATUS_COMPLETED, subscribe.waitForDone());
        assertEquals(200, subscribe.getResponseStatus());

        List<Message.Mutable> messages = HashMapMessage.parseMessages(subscribe.getResponseContent());
        assertEquals(1, messages.size());
        Message.Mutable response = messages.get(0);
        assertTrue(response.isSuccessful());
        Object subscriptions = response.get(Message.SUBSCRIPTION_FIELD);
        assertTrue(subscriptions instanceof Object[]);
    }

    public void testUnsubscribeWithMultipleChannels() throws Exception
    {
        ContentExchange handshake = newBayeuxExchange("[{" +
                "\"channel\": \"/meta/handshake\"," +
                "\"version\": \"1.0\"," +
                "\"minimumVersion\": \"1.0\"," +
                "\"supportedConnectionTypes\": [\"long-polling\"]" +
                "}]");
        httpClient.send(handshake);
        assertEquals(HttpExchange.STATUS_COMPLETED, handshake.waitForDone());
        assertEquals(200, handshake.getResponseStatus());

        String clientId = extractClientId(handshake);
        String bayeuxCookie = extractBayeuxCookie(handshake);

        ContentExchange subscribe = newBayeuxExchange("[{" +
                "\"channel\": \"/meta/subscribe\"," +
                "\"clientId\": \"" + clientId + "\"," +
                "\"subscription\": [\"/foo\",\"/bar\"]" +
                "}]");
        subscribe.setRequestHeader(HttpHeaders.COOKIE, bayeuxCookie);
        httpClient.send(subscribe);
        assertEquals(HttpExchange.STATUS_COMPLETED, subscribe.waitForDone());
        assertEquals(200, subscribe.getResponseStatus());

        ContentExchange unsubscribe = newBayeuxExchange("[{" +
                "\"channel\": \"/meta/unsubscribe\"," +
                "\"clientId\": \"" + clientId + "\"," +
                "\"subscription\": [\"/foo\",\"/bar\"]" +
                "}]");
        unsubscribe.setRequestHeader(HttpHeaders.COOKIE, bayeuxCookie);
        httpClient.send(unsubscribe);
        assertEquals(HttpExchange.STATUS_COMPLETED, unsubscribe.waitForDone());
        assertEquals(200, unsubscribe.getResponseStatus());

        List<Message.Mutable> messages = HashMapMessage.parseMessages(unsubscribe.getResponseContent());
        assertEquals(1, messages.size());
        Message.Mutable response = messages.get(0);
        assertTrue(response.isSuccessful());
        Object subscriptions = response.get(Message.SUBSCRIPTION_FIELD);
        assertTrue(subscriptions instanceof Object[]);
    }
}