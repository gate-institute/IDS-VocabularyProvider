#!/bin/bash


# GENERIC IMAGES

# broker-vocabulary
mvn -f ../eis-vocab/ clean package
rm eis-vocab/eis-vocab-*.jar
cp ../eis-vocab/target/eis-vocab-*.jar eis-vocab/
docker build eis-vocab/ -t idsvocabularyprovider/eis-vocab 

# fuseki
docker build fuseki/ -t idsvocabularyprovider/fuseki

# reverseproxy
docker build reverseproxy-vocol/ -t idsvocabularyprovider/reverseproxy-vocol


# vocol
docker build ../vocol/ -t idsvocabularyprovider/vocol

