# GitHub Webhook Client - Spring Boot
This is a GitHub Webhook client based on Spring Boot application framework. This is currently in 0.0.1-SNAPSHOT version.

***I am still working on final version of instructions.*

# Build, Deploy and Run
## On Local


## On VPS Linux

### For Ubuntu and other systems that do not support `systemd` configuration.
This configuration uses Upstart configuration to register this hook as a service on linux box. 
For more details on Upstart configuration you can check two tutorials on DigitalOcean - 

 1. [the-upstart-event-system-what-it-is-and-how-to-use-i](https://www.digitalocean.com/community/tutorials/the-upstart-event-system-what-it-is-and-how-to-use-it%22 "the-upstart-event-system-what-it-is-and-how-to-use-it") 
 2. [how-to-configure-a-linux-service-to-start-automatically-after-a-crash-or-reboot-part-2-reference](https://www.digitalocean.com/community/tutorials/how-to-configure-a-linux-service-to-start-automatically-after-a-crash-or-reboot-part-2-reference)

**Note the GHSecretKey generated. Exact same key should be used on Github.com while configuring this webhook.**


```bash
mvn clean package
sudo mkdir -p /srv/github-webhook-client/
sudo cp target/github-webhook-client.jar /srv/github-webhook-client/github-webhook-client.jar
sudo cp src/main/resources/application.properties /srv/github-webhook-client/
sudo cp github-webhook-client.conf /etc/init/
init-checkconf /etc/init/github-webhook-client.conf
echo "GHSecretKey=$(date +%s | sha256sum | base64 | head -c 32 ; echo)" | sudo tee /etc/default/github-webhook-client
sudo service github-webhook-client start
```

```bash
mvn clean package
sudo mkdir /srv/github-webhook-client/
sudo cp target/github-webhook-client.jar /srv/github-webhook-client/github-webhook-client.jar
sudo cp src/main/resources/application.properties /srv/github-webhook-client/
sudo cp github-webhook-client.service /etc/systemd/system
sudo chmod +x github-webhook-client.service
sudo systemctl enable github-webhook-client.service
echo "GHSecretKey=$(date +%s | sha256sum | base64 | head -c 32 ; echo)" | sudo tee /etc/default/github-webhook-client
sudo systemctl start github-webhook-client.service
```
