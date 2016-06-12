# github-webhook-client


# Build, Deploy and Run on Linux

### For Ubuntu and other systems that do not support systemd configuration.
https://www.digitalocean.com/community/tutorials/the-upstart-event-system-what-it-is-and-how-to-use-it

```bash
mvn clean compile package
sudo mkdir /srv/github-webhook-client/
sudo cp target/github-webhook-client.jar /srv/github-webhook-client/github-webhook-client.jar
sudo cp github-webhook-client.conf /etc/init/
init-checkconf /etc/init/github-webhook-client.conf
sudo /bin/systemctl enable github-webhook-client.service
echo "SECRET_KEY=$(date +%s | sha256sum | base64 | head -c 32 ; echo)" | sudo tee /etc/default/github-webhook-client
sudo systemctl start github-webhook-client.service
```


```bash
mvn clean compile package
sudo mkdir /srv/github-webhook-client/
sudo cp target/github-webhook-client.jar /srv/github-webhook-client/github-webhook-client.jar
sudo cp github-webhook-client.service /etc/systemd/system
sudo chmod +x github-webhook-client.service
sudo /bin/systemctl enable github-webhook-client.service
echo "SECRET_KEY=$(date +%s | sha256sum | base64 | head -c 32 ; echo)" | sudo tee /etc/default/github-webhook-client
sudo systemctl start github-webhook-client.service
```