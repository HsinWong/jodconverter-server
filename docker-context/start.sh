#!/bin/sh

# 刷新字体缓存
fc-cache -fv

# 监控自定义字体变更后重启
[ -x /usr/bin/inotifywait -a /usr/bin/killall ] && (
        /usr/bin/inotifywait -r -e "create,delete,modify,move" /usr/share/fonts/custom/
        echo "$(ls -l /usr/share/fonts/custom/) modified --> restarting"
        /usr/bin/killall -1 java
) &

java -Djava.security.egd=file:/dev/./urandom -jar /app.jar $*