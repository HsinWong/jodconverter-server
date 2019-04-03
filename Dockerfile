FROM ubuntu:16.04

LABEL maintainer="hsinwong@foxmail.com"

ENV LC_ALL zh_CN.UTF-8

COPY fonts/* /usr/share/fonts/
COPY sources.list /etc/apt/

VOLUME /tmp

RUN apt-get update && apt-get -y upgrade && apt-get install -y locales-all openjdk-8-jdk libreoffice pdf2htmlex && rm -rf /var/lib/apt/lists/*

ADD jodconverter-server-0.0.1-SNAPSHOT.jar app.jar
RUN bash -c 'touch /app.jar'

EXPOSE 9980

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]

# docker run -d -p 9980:9980 --restart unless-stopped registry.cn-hangzhou.aliyuncs.com/hsinwong/jodconverter-server
