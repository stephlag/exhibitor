/*
 * Copyright 2012 Netflix, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.netflix.exhibitor.core.automanage;

import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.MediaType;

import org.apache.curator.test.InstanceSpec;
import org.apache.curator.utils.CloseableUtils;
import org.junit.Assert;
import org.junit.Test;

import com.netflix.exhibitor.core.RemoteConnectionConfiguration;

public class TestRemoteInstanceRequestClient
{
    @Test
    public void testMissingServer() throws URISyntaxException
    {
        RemoteInstanceRequestClientImpl client = new RemoteInstanceRequestClientImpl(
                new RemoteConnectionConfiguration());
        try
        {
            // a non-existent port should generate an exception
            client.getWebResource(new URI("http://localhost:" + InstanceSpec.getRandomPort()), MediaType.WILDCARD_TYPE,
                    Object.class);
        } catch (Exception e)
        {
            Throwable cause = e.getCause();
            if (cause == null)
            {
                cause = e;
            }
            Assert.assertTrue(cause.getClass().getName(), cause instanceof ConnectException);
        } finally
        {
            CloseableUtils.closeQuietly(client);
        }
    }

    @Test
    public void testConnectionTimeout() throws Exception
    {
        int port = InstanceSpec.getRandomPort();

        RemoteInstanceRequestClientImpl client = null;
        ServerSocket server = new ServerSocket(port, 0);
        try
        {
            client = new RemoteInstanceRequestClientImpl(new RemoteConnectionConfiguration());
            client.getWebResource(new URI("http://localhost:" + port), MediaType.WILDCARD_TYPE, Object.class);
        } catch (Exception e)
        {
            Throwable cause = e.getCause();
            Assert.assertTrue(cause instanceof SocketTimeoutException);
        } finally
        {
            CloseableUtils.closeQuietly(client);
            server.close();
        }
    }
}
