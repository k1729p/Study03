@echo off
set PROJECT=study03
set DOCKER_IMAGE=eeengcs/study03:1.0.0-SNAPSHOT
set DOCKER_FILE=docker-config\Dockerfile
set COMPOSE_FILE=docker-config\compose.yaml

pushd %cd%
cd ..
docker compose down	
docker image rm --force %DOCKER_IMAGE%
docker build -f %DOCKER_FILE% --tag %DOCKER_IMAGE% .
docker push %DOCKER_IMAGE%
  
docker compose -f %COMPOSE_FILE% -p %PROJECT% up --detach
echo ------------------------------------------------------------------------------------------
docker compose -f %COMPOSE_FILE% -p %PROJECT% ps
echo ------------------------------------------------------------------------------------------
docker compose -f %COMPOSE_FILE% -p %PROJECT% images
popd
pause