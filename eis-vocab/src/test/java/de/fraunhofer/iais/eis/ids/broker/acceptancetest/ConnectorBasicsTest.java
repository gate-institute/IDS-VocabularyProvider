/*
  Copyright Fraunhofer IAIS
  https://www.dataspaces.fraunhofer.de/en/software/broker.html
  Author: Ahmad Hemid

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0
   
  
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

*/

package de.fraunhofer.iais.eis.ids.broker.acceptancetest;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.iais.eis.ids.broker.main.AppConfig;
import de.fraunhofer.iais.eis.ids.broker.util.NullBrokerSelfDescription;
import de.fraunhofer.iais.eis.ids.component.core.RequestType;
import de.fraunhofer.iais.eis.ids.component.core.map.DescriptionRequestMAP;
import de.fraunhofer.iais.eis.ids.component.core.util.CalendarUtil;
import de.fraunhofer.iais.eis.ids.component.interaction.multipart.Multipart;
import de.fraunhofer.iais.eis.ids.component.interaction.multipart.MultipartComponentInteractor;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ConnectorBasicsTest {

    private MultipartComponentInteractor componentInteractorSecTokenIgnoring;
    private final DynamicAttributeToken dummyToken = new DynamicAttributeTokenBuilder()._tokenFormat_(TokenFormat.JWT)._tokenValue_("test1234").build();

    @Before
    public void setUp() throws MalformedURLException, URISyntaxException {
        AppConfig secTokenIgnoringConfig = new AppConfig(new NullBrokerSelfDescription());
        componentInteractorSecTokenIgnoring = secTokenIgnoringConfig.responseSenderAgent(new URI("http://example.org/agent/")).build();

        AppConfig secTokenVerifyingConfig = new AppConfig(new NullBrokerSelfDescription());
        secTokenVerifyingConfig.dapsValidateIncoming(true);
        MultipartComponentInteractor componentInteractorSecTokenVerifying = secTokenVerifyingConfig.build();

    }

    @Test
    public void retrieveSelfDescriptionAsMessage() throws IOException, URISyntaxException {
        DescriptionRequestMessage selfDescriptionRequest = new DescriptionRequestMessageBuilder()
                ._issued_(CalendarUtil.now())
                ._issuerConnector_(new URL("http://example.org/").toURI())
                ._senderAgent_(new URL("http://example.org/agent/").toURI())
                ._modelVersion_("3.0.0")
                ._securityToken_(dummyToken)
                .build();

        Multipart request = new Multipart(new DescriptionRequestMAP(selfDescriptionRequest));
        Multipart response = componentInteractorSecTokenIgnoring.process(request, RequestType.INFRASTRUCTURE);

        Assert.assertNotNull(response.getSerializedPayload());
        Assert.assertTrue(new String(response.getSerializedPayload().getSerialization()).contains("IDS Metadata Broker"));
    }

    @Test
    public void retrieveSelfDescriptionPlain() {
        String selfDescription = componentInteractorSecTokenIgnoring.getSelfDescription();
        Assert.assertTrue(selfDescription.contains("IDS Metadata Broker"));
    }


    @Test
    public void msgWithoutDapsToken() throws IOException, URISyntaxException {
        try { //MUST throw exception, as messages have to have a security token
            DescriptionRequestMessage selfDescriptionRequest = new DescriptionRequestMessageBuilder()
                    ._issued_(CalendarUtil.now())
                    ._issuerConnector_(new URL("http://example.org/").toURI())
                    ._modelVersion_("3.0.0")
                    ._senderAgent_(new URI("http://example.org/"))
                    .build();
        }
        catch (ConstraintViolationException e)
        {
            return;
        }
        Assert.fail();
    }

}
