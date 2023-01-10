#!/bin/sh

mvn clean install -q -pl multiple-jwks-validation

for service in frontend-service order-webmvc-service gateway-service task-webflux-service
do
  echo "Generating docker image for ${service}, please wait ..."
  mvn clean package -DskipTests -pl ${service} -B -am
  mvn -DskipTests -pl ${service} spring-boot:build-image
done

echo "Finished docker images!"