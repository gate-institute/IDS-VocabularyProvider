#!/bin/bash

# login to the docker registry
#docker login -u user -p token registry.gitlab.cc-asp.fraunhofer.de

# broker-vocabulary
docker pull idsvocabularyprovider/eis-vocab 

# vocol
docker pull  idsvocabularyprovider/vocol

# fuseki
docker build fuseki/ -t idsvocabularyprovider/fuseki

# reverseproxy
docker build reverseproxy-vocol/ -t reverseproxy-vocol
