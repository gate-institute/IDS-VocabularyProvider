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

import de.fraunhofer.iais.eis.ids.connector.commons.broker.QueryResultsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class QueryForwarder implements QueryResultsProvider {
    Logger logger = LoggerFactory.getLogger(QueryForwarder.class);

    private final String vocolAddress;

    public QueryForwarder(String vocolAddress) {
        this.vocolAddress = vocolAddress;
    }


    @Override
    public String getResults(String query) {

        // Forward query to vocol, return result as string
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(vocolAddress + "/sparqlServer/query/?query=" + URLEncoder.encode(query, StandardCharsets.UTF_8)))
                .build();
        logger.info(vocolAddress + "/sparqlServer/query/?query=" + URLEncoder.encode(query, StandardCharsets.UTF_8));

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return response.body();
    }
}
