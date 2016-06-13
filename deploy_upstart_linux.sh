#! /bin/sh
mvn clean compile package
sudo mkdir -p /srv/github-webhook-client/
sudo cp target/github-webhook-client.jar /srv/github-webhook-client/github-webhook-client.jar
sudo cp src/main/resources/application.properties /srv/github-webhook-client/
sudo cp github-webhook-client.conf /etc/init/
init-checkconf /etc/init/github-webhook-client.conf
