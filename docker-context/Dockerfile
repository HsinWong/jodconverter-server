FROM ubuntu:16.04

LABEL maintainer="hsinwong@foxmail.com"

# 中文字体支持
ENV LC_ALL zh_CN.UTF-8

# 外挂字体
VOLUME ["/tmp", "/usr/share/fonts/custom"]

RUN apt-get update && apt-get install -y apt-transport-https
COPY sources.list /etc/apt/sources.list
ADD mupdf-1.14.0-source.tar.xz /usr/local/src
RUN apt-get update && apt-get -y upgrade && \
    apt-get install -y locales-all openjdk-8-jdk libreoffice pdf2htmlex inotify-tools psmisc build-essential pkg-config && \
    cd /usr/local/src/mupdf-1.14.0-source && make HAVE_X11=no HAVE_GLUT=no prefix=/usr/local install && \
    rm -rf /var/lib/apt/lists/*

COPY app.jar start.sh /
RUN chmod a+x start.sh

EXPOSE 9980

# 指定启动脚本
ENTRYPOINT ["./start.sh"]

# 字体文件放在 /var/lib/docker/volumes/fonts/_data/ 目录中
# docker run -d -p 9980:9980 -v fonts:/usr/share/fonts/custom --restart unless-stopped --name jodconverter-server registry.cn-shanghai.aliyuncs.com/hsinwong/jodconverter-server