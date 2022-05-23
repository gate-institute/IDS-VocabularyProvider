FROM adoptopenjdk/openjdk11:alpine-slim

ADD eis-vocab-*.jar eis-vocab.jar
ADD run.sh run.sh

RUN sh -c 'touch /eis-vocab.jar'
RUN sh -c 'chmod +x /run.sh'

ENTRYPOINT ["/run.sh"]
