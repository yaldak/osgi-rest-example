#!/bin/bash

mvn clean install

docker stop restexample
docker rm -f restexample

docker build . -t restexample

docker run --name restexample -p 8181:8181 -t restexample

