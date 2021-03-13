#!/usr/bin/env bash

mvn clean package

echo 'Copy files...'

scp -i ~/.ssh/id_rsa1 \
    target/social-media-1.0-SNAPSHOT.jar \
    kirill@192.168.60.128:/home/kirill/

echo 'Restart server...'

ssh -i ~/.ssh/id_rsa1 kirill@192.168.60.128 << EOF
pgrep java | xargs kill -9
nohup java -jar -Dserver.port=8888 social-media-1.0-SNAPSHOT.jar > log.txt &
EOF

echo 'Bye'