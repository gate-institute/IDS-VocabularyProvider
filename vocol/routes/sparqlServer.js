const express = require('express');
const router = express.Router();
const request = require('request');
const util = require('util');
let endpoint = process.argv[4] + ":" + (process.argv[3] || 3030) + "/connectorData/";
let auth = 'Basic ' + new Buffer('admin:indasp123!').toString('base64');
require('request').debug = true 
require('request').debug = true

const corenlp = require("corenlp-request-wrapper");



router.post('/:method', function (req, res) {
  if (req.params.method)
    if (req.params.method == 'update') {
      request.post({
        headers: {
          'Content-Type': ' application/x-www-form-urlencoded',
          Authorization: auth
        },
        url: endpoint + "update?update=" + req.body.query
      }, function (error, response, body) {
        if (error) {
          res.json({
            errorThrown: error
          });
          console.warn(error);
        }
        if (!error && response.statusCode == 200) {
          res.json({
            action: 'is done'
          });
        }
      });
    }
    else if (req.params.method == 'query') {

      //let queryStr = "";
      // if (req.body.hasOwnProperty('default-graph-uri') && !req.body.hasOwnProperty('source')) {
      //   queryStr = encodeURIComponent(req.body.query)
      //   let graphs = req.body['default-graph-uri'];
      //   if (graphs.length > 1) {

      //     for (let i in graphs) {
      //       queryStr += "&default-graph-uri=" + encodeURIComponent(graphs[i]);
      //     }
      //   }
      //   else {
      //     queryStr += "&default-graph-uri=" + encodeURIComponent(graphs);
      //   }
      // } else 
      // console.log(req.body)
      // if (JSON.parse(req.body).hasOwnProperty('source')) {
      //   queryStr = req.body.query
      //   let graphs = req.body['default_graph_uri'];
      //   if (graphs.length > 1) {
      //     for (let i in graphs) {
      //       queryStr += "&default-graph-uri=" + encodeURIComponent(graphs[i]);
      //     }
      //     console.log("33333333333333333")

      //   }
      //   else {
      //     queryStr += "&default-graph-uri=" + encodeURIComponent(graphs);
      //     console.log("4444444444444444444")

      //   }
      // } else {
      //   queryStr = req.body.query
      //   console.log("5555555555555")
      // }
      //  console.log(req.body)
      let queryStr = req.body.query;
      if (req.body.hasOwnProperty('default-graph-uri')) {
        let graphs = req.body['default-graph-uri'];
        if (graphs.length > 1)
          for (let i in graphs) {
            queryStr += "&default-graph-uri=" + encodeURIComponent(graphs[i]);
          }
        else
          queryStr += "&default-graph-uri=" + encodeURIComponent(graphs);

      }

      request.post({
        headers: {
          'Accept': 'application/sparql-results+json;charset=UTF-8',
          'Content-Type': 'application/x-www-form-urlencoded',
          Authorization: auth
        },
        url: endpoint + 'query?query=' + queryStr
      }, function (error, response, body) {
        if (error) {
          res.json({
            textStatus: response.statusCode,
            errorThrown: error
          });
          console.warn(error);
        }
        if (!error && response.statusCode == 200) {
          res.send(body);
        }
      });
    } else if (req.params.method == 'construct') {

 let queryStr = "";
 let annotate_keyword='';
let acceptHeader4SourceCode = ""; 
      if (req.body.hasOwnProperty("query")) {
        queryStr = req.body.query;

         queryStr = req.body.query;
         acceptHeader4SourceCode = req.originalUrl.split('?')[1];
         //      let acceptHeader4SourceCode = req.originalUrl.split('?')[1];

      if (req.body.hasOwnProperty('default-graph-uri')) {
        let graphs = req.body['default-graph-uri'];
        if (graphs.length > 1)
          for (let i in graphs) {
            queryStr += "&default-graph-uri=" + encodeURIComponent(graphs[i]);
          }
        else
          queryStr += "&default-graph-uri=" + encodeURIComponent(graphs);
      }

      request.post({
        headers: {
          "Accept": acceptHeader4SourceCode,
          "Access-Control-Allow-Origin": "*",
          "Access-Control-Allow-Headers": "Origin, X-Requested-With, Content-Type, Accept",
          Authorization: auth
        },
        url: endpoint + 'query?query=' + queryStr
      }, function (error, response, body) {
        if (error) {
//          res.json({
//            textStatus: response.statusCode,
//            errorThrown: error
//          });
          console.warn(error);
        }
        if (!error && response.statusCode == 200) {
          res.send(body);
        }
      });

      }
      else if(req.originalUrl.includes("https://ids.vocabulary.provider/annotate/")){
        console.log("annotate will run")
        annotate_keyword=req.originalUrl.split("https://ids.vocabulary.provider/annotate/")[1];
        console.log("anootate--keyword",annotate_keyword);


        var options = {
          'method': 'POST',
          'url': 'https://corenlp.run/?properties=%7B%22annotators%22%3A%20%22tokenize%2Cssplit%2Cpos%2Cner%2Cdepparse%2Copenie%22%7D',
          body: annotate_keyword
        };
        request(options, function (error, response) {
          if (error) throw new Error(error);
          console.log(response.body);

          let openIE = JSON.parse(response.body).sentences[0].openie;
          console.log(openIE)
          if(openIE.length){
            let longest = 0;
            let indexTop = 0;
            if(openIE.length){
              for (let index = 0; index < openIE.length; index++) {
                if (
                  longest <
                  openIE[index].subject.length +
                    openIE[index].relation.length +
                    openIE[index].object.length
                ) {
                  longest =
                    openIE[index].subject.length +
                    openIE[index].relation.length +
                    openIE[index].object.length;
                  indexTop = index;
                }
              }  
            
              console.log(openIE[indexTop].subject)
              console.log(openIE[indexTop].relation)
  
              console.log(openIE[indexTop].object)
  
              let queryNlp = `SELECT ?s ?p ?o {
                   ?s ?p ?o
                   FILTER regex(str(?s), "Cassandra")
                }`
  
             
                // PREFIX rdfs: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
                // SELECT DISTINCT ?item
                //               WHERE
                //               {
                //               ?item rdfs:label ?itemLabel .
                //               FILTER ( ?itemLabel = "Store" ) .}
  
  
  
                // CONSTRUCT {
                //   ?s ?p ?o
                // }
                // WHERE {
                //  ?s ?p ?o
                //  FILTER regex(str(?s), "Cassandra")  
                // }
                
  
  
  
              // `SELECT ?subject ?predicate ?object 
              // WHERE {  ?subject ?predicate ?object} 
              // LIMIT 25
              // `
  
            //   SELECT ?s ?p ?o {
            //     ?s ?p ?o
            //     FILTER regex(str(?s), "Cassandra")
            //  }
  
  
              // acceptHeader4SourceCode = "application/ld+json"
              acceptHeader4SourceCode = "application/sparql-results+json";
  
  
              request.post({
                headers: {
                  "Accept": acceptHeader4SourceCode,
                  "Access-Control-Allow-Origin": "*",
                  "Access-Control-Allow-Headers": "Origin, X-Requested-With, Content-Type, Accept",
                  Authorization: auth
                },
                url: endpoint + 'query?query=' + queryNlp
              }, function (error, response, body) {
                if (error) {
                  //          res.json({
                  //            textStatus: response.statusCode,
                  //            errorThrown: error
                  //          });
                  // console.warn(error);
                }
                if (!error && response.statusCode == 200) {
                  res.send(body);
                }
              });
  
  
            }
          }
          else{
            res.status(400).json({
              status: false,
              error: 'No result found'
            })
          }


        });



        // res.send({
        //   term: annotate_keyword,
        //   meaning: "I dont know yet",
        // })

        }  
      else {
         acceptHeader4SourceCode = "application/ld+json"
        let uri = req.originalUrl.split('construct?')[1];
        uri = uri.replace(/%23/ig, '#')
        //	console.log(uri)

         queryStr = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
          "CONSTRUCT{" +
          "<" + uri + "> ?p ?o ." +
          "?s1 rdfs:domain <" + uri + "> ." +
          "?s2 rdfs:range  <" + uri + "> ." +
          "<" + uri + "> rdfs:domain ?domains. " +
          "<" + uri + "> rdfs:range ?ranges. " +
          "?s3 a <" + uri + "> }" +
          "WHERE { {<" + uri + "> ?p ?o . " +
          "optional {?s1 rdfs:domain <" + uri + "> }  " +
          "optional {?s2 rdfs:range <" + uri + "> } " +
          "optional {<" + uri + "> rdfs:domain ?domains } " +
          "optional {<" + uri + "> rdfs:domain ?ranges } " +
          "optional {   ?s3 a <" + uri + "> .   } }}"

        if (uri == "https://w3id.org/idsa/core/") {
          queryStr = "CONSTRUCT{  ?s ?p ?o }WHERE { {  ?s ?p ?o .  }  filter contains(str(?g), \"ids\")}"
        }
        if (uri == "https://w3id.org/i40/rami#") {
          queryStr = "CONSTRUCT{  ?s ?p ?o }WHERE { {  ?s ?p ?o .  }  filter contains(str(?g), \"rami\")}"
        }
        if (uri == "http://vocol.fraunhofer.de/vmo/") {

          queryStr = `PREFIX owl: <http://www.w3.org/2002/07/owl#>
          PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
          PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>
          SELECT DISTINCT ?subject ?preferredNamespaceUri ?graph
          WHERE {{ GRAPH ?graph { ?subject a owl:Ontology.
          OPTIONAL { ?subject <http://purl.org/vocab/vann/preferredNamespaceUri> ?preferredNamespaceUri.}}
          }}`;
          acceptHeader4SourceCode = "application/sparql-results+json";

        }
        if ((uri[uri.length - 1]) == '#') {
          uri = uri.substring(0, uri.length - 1);
          uri = uri.substring(uri.lastIndexOf('/') + 1)
          queryStr = `CONSTRUCT{  ?s ?p ?o } WHERE { {  ?s ?p ?o .  }  }`
        }
        queryStr = encodeURIComponent(queryStr)


          //      let acceptHeader4SourceCode = req.originalUrl.split('?')[1];

      if (req.body.hasOwnProperty('default-graph-uri')) {
        let graphs = req.body['default-graph-uri'];
        if (graphs.length > 1)
          for (let i in graphs) {
            queryStr += "&default-graph-uri=" + encodeURIComponent(graphs[i]);
          }
        else
          queryStr += "&default-graph-uri=" + encodeURIComponent(graphs);
      }
      request.post({
        headers: {
          "Accept": acceptHeader4SourceCode,
          "Access-Control-Allow-Origin": "*",
          "Access-Control-Allow-Headers": "Origin, X-Requested-With, Content-Type, Accept",
          Authorization: auth
        },
        url: endpoint + 'query?query=' + queryStr
      }, function (error, response, body) {
        if (error) {
          //          res.json({
          //            textStatus: response.statusCode,
          //            errorThrown: error
          //          });
          // console.warn(error);
        }
        if (!error && response.statusCode == 200) {
          res.send(body);
        }
      });

      }


    }

});


router.get('/:instance/:branch/', function (req, res) {
  let instaceName = req.params.instance;
  let branchName = req.params.branch;
  let queryStr = "";
  console.log(req.body)
  // query to find all the named graphs in SPARQL-endpoint
  var allNamedGraphsQuery = 'SELECT DISTINCT ?g ' +
    'WHERE {' +
    '  GRAPH ?g { ?s ?p ?o }' +
    '}';

  if (req.originalUrl.includes('?query='))
    queryStr = req.originalUrl.split('?query=')[1];
  else if (req.body.hasOwnProperty("query"))
    queryStr = req.body.query;


  // query to fuseki to getAllNamedGraphs
  request.get({
    headers: {
      'Accept': 'application/sparql-results+json;charset=UTF-8',
      Authorization: auth
    },
    url: endpoint + '?query=' + allNamedGraphsQuery
  }, function (error, response, data) {
    if (!error && response.statusCode == 200) {
      var list = [];
      // Show the HTML for the Google homepage.
      if (data != null) {
        data = JSON.parse(data)

        var graphs = data.results.bindings;
        if (graphs[0] != null) {
          for (var i = 0; i < graphs.length; i++) {
            if (graphs[i]["g"].value.includes(instaceName + '/' + branchName + '/'))
              list.push(graphs[i]["g"].value);
          }

          if (graphs.length > 1) {
            for (let i in list) {
              queryStr += "&default-graph-uri=" + encodeURIComponent(list[i]);
            }
          }
          else
            queryStr += "&default-graph-uri=" + encodeURIComponent(list);

          request.post({
            headers: {
              'Accept': 'application/sparql-results+json;charset=UTF-8',
              'Content-Type': 'application/x-www-form-urlencoded',
              Authorization: auth
            },
            url: endpoint + 'query?query=' + queryStr
          }, function (error, response, body) {
            if (error) {
              res.json({
                textStatus: response.statusCode,
                errorThrown: error
              });
              console.warn(error);
            }
            if (!error && response.statusCode == 200) {
              res.write(body);
              res.end();
            }
          });
        }
      }
    }
  })
})

router.get('/:method', function (req, res) {
  let queryStr = req.query.query
  queryStr = queryStr.replace(/%23/ig, '#')

  request.post({
    headers: {
      Accept: 'application/sparql-results+json;charset=UTF-8',
      Authorization: auth
    },
    url: endpoint + 'query?query=' + encodeURIComponent(queryStr)
  }, function (error, response, body) {
    if (error) {
      console.log(error)
      res.json({
        textStatus: response.statusCode,
        errorThrown: error
      });
      console.warn(error);
    }
    if (!error && response.statusCode == 200) {
      res.send(body);
    }
  });
})


module.exports = router;
