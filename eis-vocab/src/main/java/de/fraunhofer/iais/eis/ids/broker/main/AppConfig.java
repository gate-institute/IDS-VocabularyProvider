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

package de.fraunhofer.iais.eis.ids.broker.main;

import de.fraunhofer.iais.eis.ResourceCatalogBuilder;
import de.fraunhofer.iais.eis.ids.broker.config.AppConfigTemplate;
import de.fraunhofer.iais.eis.ids.broker.query.DescriptionRequestHandler;
import de.fraunhofer.iais.eis.ids.broker.query.QueryForwarder;
import de.fraunhofer.iais.eis.ids.component.core.DefaultComponent;
import de.fraunhofer.iais.eis.ids.component.core.RequestType;
import de.fraunhofer.iais.eis.ids.component.core.SelfDescriptionProvider;
import de.fraunhofer.iais.eis.ids.component.ecosystemintegration.daps.DapsSecurityTokenVerifier;
import de.fraunhofer.iais.eis.ids.component.ecosystemintegration.daps.JWKSFromIssuer;
import de.fraunhofer.iais.eis.ids.component.interaction.multipart.MultipartComponentInteractor;
import de.fraunhofer.iais.eis.ids.component.interaction.validation.ShaclValidator;
import de.fraunhofer.iais.eis.ids.connector.commons.broker.QueryHandler;
import de.fraunhofer.iais.eis.ids.connector.commons.broker.QueryResultsProvider;
import de.fraunhofer.iais.eis.ids.index.common.persistence.ConstructQueryResultHandler;
import de.fraunhofer.iais.eis.ids.index.common.persistence.DescriptionProvider;
import de.fraunhofer.iais.eis.ids.index.common.persistence.RepositoryFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * This class is used to start up a broker with appropriate settings and is only created once from the Main class
 */
public class AppConfig extends AppConfigTemplate {

    private final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    /**
     * Class constructor
     *
     * @param selfDescriptionProvider SelfDescriptionProvider object from which information can be obtained which is required for providing self-descriptions
     */
    public AppConfig(SelfDescriptionProvider selfDescriptionProvider) {
        super(selfDescriptionProvider);
    }

    /**
     * Build method to be called once all configuration has been finished
     *
     * @return MultipartComponentInteractor object, which is configured according to this AppConfig instance
     */
    @Override
    public MultipartComponentInteractor build() {

        //Try to pre-initialize the SHACL validation shapes so that this won't slow us down during message handling
        //TODO: Do this in a separate thread
        if (performShaclValidation) {
            try {
                ShaclValidator.initialize();
            } catch (IOException e) {
                logger.warn("Failed to initialize Shapes for SHACL validation.", e);
            }
        }

        RepositoryFacade repositoryFacade = new RepositoryFacade(sparqlEndpointUrl);

        if (contextDocumentUrl != null && !contextDocumentUrl.isEmpty()) {
            ConstructQueryResultHandler.contextDocumentUrl = contextDocumentUrl;
        }


        ConstructQueryResultHandler.catalogUri = (catalogUri == null) ? new ResourceCatalogBuilder().build().getId().toString() : catalogUri.toString();

        // the broker need to understand an ArtifactRequestMessage?
        //Remove this handler. Catalog should be retrieved via DescriptionRequestMessage
        QueryResultsProvider resultsProvider = new QueryForwarder(vocolAddress);
        QueryHandler queryHandler = new QueryHandler(selfDescriptionProvider.getSelfDescription(), resultsProvider, securityTokenProvider, responseSenderAgent);
        DefaultComponent component = new DefaultComponent(selfDescriptionProvider, securityTokenProvider, responseSenderAgent, false);

        //TODO: Does not work in the case that the catalog is empty
        DescriptionProvider descriptionProvider = new DescriptionProvider(selfDescriptionProvider.getSelfDescription(), repositoryFacade, catalogUri);
        DescriptionRequestHandler descriptionHandler = new DescriptionRequestHandler(descriptionProvider, securityTokenProvider, responseSenderAgent, selfDescriptionProvider.getSelfDescription().getMaintainer(), selfDescriptionProvider.getSelfDescription().getOutboundModelVersion(), vocolAddress);
        component.addMessageHandler(descriptionHandler, RequestType.INFRASTRUCTURE);
        component.addMessageHandler(queryHandler, RequestType.INFRASTRUCTURE);
        logger.info(String.valueOf(queryHandler));


        if (dapsValidateIncoming) {
            component.setSecurityTokenVerifier(new DapsSecurityTokenVerifier(new JWKSFromIssuer(trustedJwksHosts)));
        }

        return new MultipartComponentInteractor(component, securityTokenProvider, responseSenderAgent, performShaclValidation);

    }

}
