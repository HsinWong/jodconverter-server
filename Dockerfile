FROM ubuntu:16.04

LABEL maintainer="hsinwong@foxmail.com"

# 中文字体支持
ENV LC_ALL zh_CN.UTF-8

# 外挂字体
VOLUME ["/tmp", "/usr/share/fonts/custom"]

COPY sources.list /etc/apt/sources.list
RUN apt-get update && apt-get -y upgrade && apt-get install -y locales-all openjdk-8-jdk libreoffice pdf2htmlex inotify-tools psmisc && rm -rf /var/lib/apt/lists/*

COPY app.jar start.sh /

EXPOSE 9980

# 指定启动脚本
ENTRYPOINT ["./start.sh"]

# 字体文件放在 /var/lib/docker/volumes/fonts/_data/ 目录中
# docker run -d -p 9980:9980 -v fonts:/usr/share/fonts/custom --restart unless-stopped --name jodconverter-server registry.cn-hangzhou.aliyuncs.com/hsinwong/jodconverter-server