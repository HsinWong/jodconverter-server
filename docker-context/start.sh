#!/bin/sh

# 刷新字体缓存
fc-cache -fv

java -Djava.security.egd=file:/dev/./urandom -jar /app.jar $*