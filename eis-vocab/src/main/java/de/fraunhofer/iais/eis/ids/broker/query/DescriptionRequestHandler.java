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


package de.fraunhofer.iais.eis.ids.broker.query;

import de.fraunhofer.iais.eis.DescriptionRequestMessage;
import de.fraunhofer.iais.eis.DescriptionResponseMessageBuilder;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.iais.eis.ids.component.core.MessageHandler;
import de.fraunhofer.iais.eis.ids.component.core.RejectMessageException;
import de.fraunhofer.iais.eis.ids.component.core.SecurityTokenProvider;
import de.fraunhofer.iais.eis.ids.component.core.TokenRetrievalException;
import de.fraunhofer.iais.eis.ids.component.core.map.DescriptionRequestMAP;
import de.fraunhofer.iais.eis.ids.component.core.map.DescriptionResponseMAP;
import de.fraunhofer.iais.eis.ids.component.core.util.CalendarUtil;
import de.fraunhofer.iais.eis.ids.index.common.persistence.DescriptionProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;


public class DescriptionRequestHandler implements MessageHandler<DescriptionRequestMAP, DescriptionResponseMAP> {

    Logger logger = LoggerFactory.getLogger(DescriptionRequestHandler.class);

    private final String vocolAddress;
    public DescriptionProvider descriptionProvider;
    private final SecurityTokenProvider securityTokenProvider;
    private final URI responseSenderAgentUri;
    private final URI sender;
    private final String modelVersion;


    /**
     * Constructor
     *
     * @param descriptionProvider    Instance of DescriptionProvider class to retrieve descriptions of the requested objects from triple store
     * @param securityTokenProvider  Instance of SecurityTokenProvider to retrieve a DAT for the response messages
     * @param responseSenderAgentUri The ids:senderAgent which should be provided in response messages
     */
    public DescriptionRequestHandler(DescriptionProvider descriptionProvider, SecurityTokenProvider securityTokenProvider, URI responseSenderAgentUri, URI sender, String modelVersion, String vocolAddress) {
        this.descriptionProvider = descriptionProvider;
        this.securityTokenProvider = securityTokenProvider;
        this.responseSenderAgentUri = responseSenderAgentUri;
        this.sender = sender;
        this.modelVersion = modelVersion;
        this.vocolAddress = vocolAddress;
    }

    /**
     * The actual handle function which is called from the components, if the incoming request can be handled by this class
     *
     * @param messageAndPayload The incoming request
     * @return A DescriptionResponseMessage, if the request could be handled successfully
     * @throws RejectMessageException thrown if an error occurs during the retrieval process, e.g. if the requested object could not be found
     */
    @Override
    public DescriptionResponseMAP handle(DescriptionRequestMAP messageAndPayload) throws RejectMessageException {
        String payload = messageAndPayload.getMessage().getRequestedElement().toString();
        logger.info(payload);

        try {

            CloseableHttpClient client = HttpClientBuilder.create().build();
            HttpPost httpPost = new HttpPost(vocolAddress + "/sparqlServer/construct?" + payload.replace("#", "%23"));

            // set your POST request headers to accept json contents
            httpPost.setHeader("Accept", "application/ld+json");
            // your closeablehttp response
            CloseableHttpResponse response = client.execute(httpPost);

            // print your status code from the response
            System.out.println(response.getStatusLine().getStatusCode());

            // take the response body as a json formatted string
            String responseJSON = EntityUtils.toString(response.getEntity());


            try {
                return new DescriptionResponseMAP(new DescriptionResponseMessageBuilder()
                        ._correlationMessage_(messageAndPayload.getMessage().getId())
                        ._issued_(CalendarUtil.now())
                        ._issuerConnector_(sender)
                        ._modelVersion_(modelVersion)
                        ._securityToken_(securityTokenProvider.getSecurityTokenAsDAT())
                        ._senderAgent_(responseSenderAgentUri)
                        .build(),
                        responseJSON
                );
            } catch (TokenRetrievalException e) {
                throw new RejectMessageException(RejectionReason.INTERNAL_RECIPIENT_ERROR, e);
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RejectMessageException(RejectionReason.INTERNAL_RECIPIENT_ERROR, e);

        }
    }


    /**
     * Determines whether an incoming request can be handled by this class
     *
     * @return true, if the message is of an applicable type (DescriptionRequestMessage only)
     */
    @Override
    public Collection<Class<? extends Message>> getSupportedMessageTypes() {
        return Collections.singletonList(DescriptionRequestMessage.class);
    }

}
