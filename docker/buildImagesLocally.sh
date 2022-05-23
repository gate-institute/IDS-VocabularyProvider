#!/bin/bash


# GENERIC IMAGES

# broker-vocabulary
mvn -f ../eis-vocab/ clean package
rm eis-vocab/eis-vocab-*.jar
cp ../eis-vocab/target/eis-vocab-*.jar eis-vocab/
docker build eis-vocab/ -t registry.gitlab.cc-asp.fraunhofer.de/vocoreg/public-ids-vp/idsvocabularyprovider/eis-vocab 

# fuseki
docker build fuseki/ -t registry.gitlab.cc-asp.fraunhofer.de/vocoreg/public-ids-vp/idsvocabularyprovider/fuseki

# reverseproxy
docker build reverseproxy-vocol/ -t registry.gitlab.cc-asp.fraunhofer.de/vocoreg/public-ids-vp/idsvocabularyprovider/reverseproxy-vocol


# vocol
docker build ../../vocol/ -t registry.gitlab.cc-asp.fraunhofer.de/vocoreg/public-ids-vp/vocol

