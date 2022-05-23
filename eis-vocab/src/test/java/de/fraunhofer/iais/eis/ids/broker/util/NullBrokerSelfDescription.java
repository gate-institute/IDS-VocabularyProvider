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

package de.fraunhofer.iais.eis.ids.broker.util;

import de.fraunhofer.iais.eis.InfrastructureComponent;
import de.fraunhofer.iais.eis.ids.component.core.SelfDescriptionProvider;
import de.fraunhofer.iais.eis.ids.index.common.impl.IndexSelfDescription;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

public class NullBrokerSelfDescription implements SelfDescriptionProvider {

    private final IndexSelfDescription indexSelfDescription;

    public static final String componentId = "http://example.org/ids/broker";
    public static final String maintainerId = "http://example.org/ids/maintainer";
    public static final String catalogId = "http://example.org/ids/broker/catalog";
    public static final String modelVersion = "4.0.0";
    public static final String componentUri = "https://broker.ids.isst.fraunhofer.de/";


    public NullBrokerSelfDescription() throws MalformedURLException, URISyntaxException {
        indexSelfDescription = new IndexSelfDescription(
                URI.create(componentId),
                URI.create(maintainerId),
                URI.create(catalogId),
                modelVersion,
                null,
                URI.create(componentUri));
    }

    public InfrastructureComponent getSelfDescription() {
        return indexSelfDescription.getSelfDescription();
    }

}
