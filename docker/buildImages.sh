#!/bin/bash

# login to the docker registry
#docker login -u user -p token registry.gitlab.cc-asp.fraunhofer.de

# broker-vocabulary
docker pull registry.gitlab.cc-asp.fraunhofer.de/vocoreg/public-ids-vp/idsvocabularyprovider/eis-vocab 

# vocol
docker pull  registry.gitlab.cc-asp.fraunhofer.de/vocoreg/public-ids-vp/vocol

# fuseki
docker build fuseki/ -t registry.gitlab.cc-asp.fraunhofer.de/vocoreg/public-ids-vp/idsvocabularyprovider/fuseki

# reverseproxy
docker build reverseproxy-vocol/ -t registry.gitlab.cc-asp.fraunhofer.de/vocoreg/public-ids-vp/idsvocabularyprovider/reverseproxy-vocol
