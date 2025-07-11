# 贝尔实验室 Spring 官方推荐镜像 JDK下载地址 https://bell-sw.com/pages/downloads/
FROM bellsoft/liberica-openjdk-rocky:17.0.15-cds
#FROM bellsoft/liberica-openjdk-rocky:21.0.7-cds
#FROM findepi/graalvm:java17-native

LABEL maintainer="Alphay Tech"

RUN mkdir -p /d3code/job/logs \
    /d3code/job/temp \
    /d3code/skywalking/agent

WORKDIR /d3code/job

ENV SERVER_PORT=9203 SNAIL_PORT=29203 LANG=C.UTF-8 LC_ALL=C.UTF-8 JAVA_OPTS=""

EXPOSE ${SERVER_PORT}
# 暴露 snail job 客户端端口 用于定时任务调度中心通信
EXPOSE ${SNAIL_PORT}

ADD ./target/d3code-job.jar ./app.jar

SHELL ["/bin/bash", "-c"]

ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom -Dserver.port=${SERVER_PORT} \
           -Dsnail-job.port=${SNAIL_PORT} \
           #-Dskywalking.agent.service_name=d3code-job \
           #-javaagent:/d3code/skywalking/agent/skywalking-agent.jar \
           -XX:+HeapDumpOnOutOfMemoryError -XX:+UseZGC ${JAVA_OPTS} \
           -jar app.jar

