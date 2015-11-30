#!/bin/bash
#docker build -t taktik/squirrel-mac-mw .
docker run -d -t --name transcoder -e SPRING_APPLICATION_NAME=squirrel-mac-mw taktik/squirrel-mac-mw -e MW_NEXUS_SERVER="$NEXUS_SERVER" -e MW_NEXUS_USERNAME="$NEXUS_USERNAME" -e MW_NEXUS_PASSWORD="$NEXUS_PASSWORD" -e MW_NEXUS_REPOSITORY="$NEXUS_REPOSITORY"