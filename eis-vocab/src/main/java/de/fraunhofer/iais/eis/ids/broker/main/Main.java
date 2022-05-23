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


import de.fraunhofer.iais.eis.ids.component.core.InfomodelFormalException;
import de.fraunhofer.iais.eis.ids.component.protocol.http.server.ComponentInteractorProvider;
import de.fraunhofer.iais.eis.ids.index.common.main.MainTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Objects;

/**
 * Entry point to the Broker
 */
@Configuration
@EnableAutoConfiguration(exclude = SolrAutoConfiguration.class)
@ComponentScan(basePackages = {"de.fraunhofer.iais.eis.ids.component.protocol.http.server"})
public class Main extends MainTemplate implements ComponentInteractorProvider {

    //Environment allows us to access application.properties
    private final Environment env;
    //Initializing properties which are not inherited from MainTemplate
    @Value("${sparql.url}")
    private String sparqlEndpointUrl;
    @Value("${infomodel.contextUrl}")
    private String contextDocumentUrl;
    @Value("${jwks.trustedHosts}")
    private Collection<String> trustedJwksHosts;
    @Value("${vocol.address}")
    private String vocolAddress;
    @Value("${daps.validateIncoming}")
    private boolean dapsValidateIncoming;
    @Value("${component.responseSenderAgent}")
    private String responseSenderAgent;
    @Value("${infomodel.validateWithShacl}")
    private boolean validateShacl;

    @Autowired
    public Main(Environment env) {
        this.env = env;
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    /**
     * This function is called during startup and takes care of the initialization
     */
    @PostConstruct
    @Override
    public void setUp() {
        //Assigning variables which are inherited from MainTemplate
        componentUri = env.getProperty("component.uri");
        componentMaintainer = env.getProperty("component.maintainer");
        componentCatalogUri = env.getProperty("component.catalogUri");
        componentModelVersion = env.getProperty("component.modelversion");
        sslCertificatePath = env.getProperty("ssl.certificatePath");
        elasticsearchHostname = env.getProperty("elasticsearch.hostname");
        elasticsearchPort = Integer.parseInt(Objects.requireNonNull(env.getProperty("elasticsearch.port")));
        keystorePassword = env.getProperty("keystore.password");
        keystoreAlias = env.getProperty("keystore.alias");
//        componentIdsId = env.getProperty("component.idsid");
        dapsUrl = env.getProperty("daps.url");
        trustAllCerts = Boolean.parseBoolean(env.getProperty("ssl.trustAllCerts"));
        ignoreHostName = Boolean.parseBoolean(env.getProperty("ssl.ignoreHostName"));

        try {
            multipartComponentInteractor = new AppConfig(createSelfDescriptionProvider())
                    .sparqlEndpointUrl(sparqlEndpointUrl)
                    .contextDocumentUrl(contextDocumentUrl)
                    .catalogUri(new URI(componentCatalogUri))
                    .securityTokenProvider(createSecurityTokenProvider())
                    .trustedJwksHosts(trustedJwksHosts)
                    .dapsValidateIncoming(dapsValidateIncoming)
                    .responseSenderAgent(new URI(responseSenderAgent))
                    .performShaclValidation(validateShacl)
                    .setVocolAddress(vocolAddress)
                    .build();
        } catch (URISyntaxException e) {
            throw new InfomodelFormalException(e);
        }
    }

    @Override
    @PreDestroy
    public void shutDown() {

    }
}
