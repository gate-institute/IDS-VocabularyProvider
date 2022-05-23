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

package de.fraunhofer.iais.eis.ids.broker.config;

import de.fraunhofer.iais.eis.ids.component.core.SecurityTokenProvider;
import de.fraunhofer.iais.eis.ids.component.core.SelfDescriptionProvider;
import de.fraunhofer.iais.eis.ids.component.interaction.multipart.MultipartComponentInteractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Collection;

public abstract class AppConfigTemplate {

    final private Logger logger = LoggerFactory.getLogger(AppConfigTemplate.class);

    public String sparqlEndpointUrl = "";
    public String contextDocumentUrl;
    public URI catalogUri;
    public SelfDescriptionProvider selfDescriptionProvider;
    public String vocolAddress;
    public SecurityTokenProvider securityTokenProvider = new SecurityTokenProvider() {
        @Override
        public String getSecurityToken() {
            return "";
        }
    };


    public Collection<String> trustedJwksHosts;
    public boolean dapsValidateIncoming;
    public URI responseSenderAgent;
    public boolean performShaclValidation;

    /**
     * Constructor
     * @param selfDescriptionProvider Object providing a self-description of this indexing service
     */
    public AppConfigTemplate(SelfDescriptionProvider selfDescriptionProvider) {
        this.selfDescriptionProvider = selfDescriptionProvider;
    }

    /**
     * Call this function to set the URL of the SPARQL endpoint. If none is set, an in-memory solution will be used, not permanently persisting data.
     * @param sparqlEndpointUrl Address of the SPARQL endpoint as String
     * @return AppConfig as Builder Object
     */
    public AppConfigTemplate sparqlEndpointUrl(String sparqlEndpointUrl) {
        this.sparqlEndpointUrl = sparqlEndpointUrl;
        logger.info("SPARQL endpoint set to " +sparqlEndpointUrl);
        return this;
    }


    /**
     * Call this function to set the URL of the SPARQL endpoint. If none is set, an in-memory solution will be used, not permanently persisting data.
     * @param vocolAddress Address of the SPARQL endpoint as String
     * @return AppConfig as Builder Object
     */
    public AppConfigTemplate setVocolAddress(String vocolAddress) {
        this.vocolAddress = vocolAddress;
        logger.info("Vocol address set to " +vocolAddress);
        return this;
    }


    /**
     * URL of the context document to be used for JSON-LD context
     * @param contextDocumentUrl URL of the context document
     * @return AppConfig as Builder Object
     */
    public AppConfigTemplate contextDocumentUrl(String contextDocumentUrl) {
        this.contextDocumentUrl = contextDocumentUrl;
        logger.info("Context document URL set to " +contextDocumentUrl);
        return this;
    }

    /**
     * This function allows to set the URI of the catalog of this indexing service. This is required for it to know when the catalog is requested, and to rewrite URIs for the REST scheme
     * @param catalogUri URI of the own catalog.
     * @return AppConfig as Builder Object
     * TODO: Theoretically, this could be a list of URIs in case of multiple Catalogs. Possibly useful to distinguish between ResourceCatalogUri and ConnectorCatalogUri
     */
    public AppConfigTemplate catalogUri(URI catalogUri) {
        this.catalogUri = catalogUri;
        logger.info("Catalog URI set to " + catalogUri.toString());
        return this;
    }

    /**
     * Use this function to turn SHACL validation on or off (configurable at startup only)
     * @param performValidation boolean, indicating whether SHACL validation should be performed
     * @return AppConfig as Builder Object
     */
    public AppConfigTemplate performShaclValidation(boolean performValidation)
    {
        this.performShaclValidation = performValidation;
        logger.info("Perform SHACL Validation is set to " + performValidation);
        return this;
    }

    /**
     * Use this function to set a SecurityTokenProvider for this indexing service, allowing it to send messages with a Dynamic Attribute Token signed by the DAPS
     * @param securityTokenProvider Object
     * @return AppConfig as Builder Object
     */
    public AppConfigTemplate securityTokenProvider(SecurityTokenProvider securityTokenProvider) {
        this.securityTokenProvider = securityTokenProvider;
        return this;
    }

    /**
     * List of hosts whose signature we can trust. Used by DAT validation
     * @param trustedJwksHosts list of hosts
     * @return AppConfig as Builder Object
     */
    public AppConfigTemplate trustedJwksHosts(Collection<String> trustedJwksHosts) {
        this.trustedJwksHosts = trustedJwksHosts;
        return this;
    }

    /**
     * Sets the senderAgent property in all response messages
     * @param responseSenderAgent URI of the senderAgent to be used
     * @return AppConfig as Builder Object
     */
    public AppConfigTemplate responseSenderAgent(URI responseSenderAgent) {
        this.responseSenderAgent = responseSenderAgent;
        return this;
    }

    /**
     * Function to toggle validating incoming messages for having a correct DAT. This should ALWAYS be turned on. Only turn this off if required for debugging!
     * @param dapsValidateIncoming boolean, determining whether messages should be checked for having valid security tokens
     * @return AppConfig as Builder Object
     */
    public AppConfigTemplate dapsValidateIncoming(boolean dapsValidateIncoming) {
        this.dapsValidateIncoming = dapsValidateIncoming;
        logger.info("Incoming messages DAPS token verification enabled: " +dapsValidateIncoming);
        return this;
    }

    /**
     * Build function, turning Builder Object into an actual MultipartComponentInteractor
     * @return MultipartComponentInteractor with previously set settings
     */
    public abstract MultipartComponentInteractor build();


}
