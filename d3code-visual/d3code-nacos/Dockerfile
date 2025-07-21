FROM nacos/nacos-server:v3.0.2
LABEL maintainer="Alphay Tech"
WORKDIR /home/nacos

RUN mkdir /home/nacos/plugins

COPY ./plugins/*.jar plugins/
ADD ./bin/docker-startup.sh bin/docker-startup.sh

RUN chmod +x bin/docker-startup.sh

EXPOSE 8848
EXPOSE 8080
EXPOSE 9080

ENTRYPOINT ["sh", "bin/docker-startup.sh"]
