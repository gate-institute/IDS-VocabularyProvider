FROM ubuntu:18.04

MAINTAINER  Fraunhofer IAIS , https://vocol.iais.fraunhofer.de

# Install OpenJDK-11
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y  software-properties-common && \
    apt-get install -y openjdk-11-jdk && \
    apt-get clean;

# Setup JAVA_HOME -- useful for docker commandline
ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64/
RUN export JAVA_HOME



# Install Nodejs , git
RUN  apt-get update -yq \
    && apt-get install curl gnupg git ruby psmisc -yq \
    && curl -sL https://deb.nodesource.com/setup_12.x  | bash \
    && apt-get install nodejs -yq

# Build application
RUN mkdir /home/project
RUN mkdir /home/project/vocol
WORKDIR /home/project/vocol

#RUN git clone  https://gitlab.cc-asp.fraunhofer.de/vocoreg/open-src/vocol.git \
#&&  chmod u+x  .
#WORKDIR /home/project/vocol

ADD . /home/project/vocol
RUN npm install

#EXPOSE 3000
#EXPOSE 3030
EXPOSE 8888

#ENV PORT=3000
#CMD [ "npm", "start","3000","3030"]


