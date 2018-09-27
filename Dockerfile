FROM ubuntu:16.04
VOLUME /tmp
RUN apt-get update && apt-get install -y locales fonts-noto openjdk-8-jdk libreoffice pdf2htmlex && rm -rf /var/lib/apt/lists/* \
    && localedef -i en_US -c -f UTF-8 -A /usr/share/locale/locale.alias en_US.UTF-8
ENV LANG en_US.utf8
ADD jodconverter-server-0.0.1-SNAPSHOT.jar app.jar
RUN bash -c 'touch /app.jar'
EXPOSE 9980
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
# docker run -d -p 9980:9980 --restart unless-stopped registry.cn-hangzhou.aliyuncs.com/hebaceous/jodconverter-server
