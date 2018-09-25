# docker pull registry.cn-hangzhou.aliyuncs.com/hebaceous/jodconverter
FROM registry.cn-hangzhou.aliyuncs.com/hebaceous/jodconverter
VOLUME /tmp
ADD jodconverter-server-0.0.1-SNAPSHOT.jar app.jar
RUN bash -c 'touch /app.jar'
EXPOSE 9980
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
# docker run -it -d -p 9980:9980 jodconverter-server
