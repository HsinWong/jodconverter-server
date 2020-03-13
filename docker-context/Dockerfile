FROM ubuntu:16.04

MAINTAINER xinh xinh@xinshiyun.com

# 加速
ADD sources.list /etc/apt/sources.list
RUN apt-get update && apt-get upgrade -y && apt-get install -y apt-transport-https && sed -i 's/http/https/g' /etc/apt/sources.list

# 时区
ENV timezone Asia/Shanghai
RUN apt-get install tzdata && ln -fs /usr/share/zoneinfo/$timezone /etc/localtime && dpkg-reconfigure -f noninteractive tzdata

# openoffice4.1.7 & mupdf1.14.0 & pdf2htmlex & jdk8
ADD deb/* /home/install/
RUN cd /home/install && apt-get install -y ./* pdf2htmlex openjdk-8-jdk && rm -rf /home/install/

COPY app.jar start.sh /
RUN chmod a+x start.sh

# 外挂字体
VOLUME ["/tmp", "/usr/share/fonts/custom"]

EXPOSE 9980

# 指定启动脚本
ENTRYPOINT ["./start.sh"]

# 字体文件放在 /var/lib/docker/volumes/fonts/_data/ 目录中
# docker run -d -p 9980:9980 -v fonts:/usr/share/fonts/custom --restart unless-stopped --name jcs jodconverter-server