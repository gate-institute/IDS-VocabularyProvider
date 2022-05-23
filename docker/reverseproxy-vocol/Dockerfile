FROM nginx:alpine

RUN mkdir /etc/cert

ADD  cert/server.crt /etc/cert/server.crt
ADD  cert/server.key /etc/cert/server.key

COPY nginx.conf /etc/nginx


