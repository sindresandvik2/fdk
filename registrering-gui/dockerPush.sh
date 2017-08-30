#!/usr/bin/env bash
set -e


echo "Preparing to push 'registrering-gui' to docker"

docker login --username ${dockerUsername} --password ${dockerPassword}

docker tag dcatno/registration:latest dcat/reg-gui:latest

docker push dcat/reg-gui:latest

# tag with commit ID and push so that we can easily roll back if needed
docker tag dcatno/registration:latest dcat/reg-gui:GIT_${TRAVIS_COMMIT}
docker push dcat/reg-gui:GIT_${TRAVIS_COMMIT}
