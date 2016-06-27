# GitHub Webhook Client - Spring Boot
This is a GitHub Webhook client based on Spring Boot application framework. This is currently in 0.0.1-SNAPSHOT version.

***I am still working on final version of instructions.*

[![CircleCI](https://circleci.com/gh/manikmagar/github-webhook-client.svg?style=shield&circle-token=6cecc228ed9da14bf4faa962b338dbb3c38d285d)](https://circleci.com/gh/manikmagar/github-webhook-client)

# Prerequisite
- Java Runtime and JAVA_HOME is properly set.
- Maven is installed on your box (Check [sdkman.io](http://sdkman.io) for installing frameworks easily).


# Build, Deploy and Run

On your VPS linux box, clone the github project and execute below commands from project directory.
 
**Note the GHSecretKey generated. Exact same key should be used on Github.com while configuring this webhook.**


```bash
mvn clean package
sudo mkdir -p /srv/github-webhook-client/
sudo cp target/github-webhook-client.jar /srv/github-webhook-client/github-webhook-client.jar
sudo cp src/main/resources/application.properties /srv/github-webhook-client/
#Copy run_app.sh and make sure its executable.
sudo cp run_app.sh /srv/github-webhook-client/ && sudo chmod +x /srv/github-webhook-client
# DO NOT run below GHSecretKey generation command if you already have generated one.
# Regeneration will overrite existing and you will have to up Github.com too with new key.
echo "GHSecretKey=$(date +%s | sha256sum | base64 | head -c 32 ; echo)" | sudo tee /etc/default/github-webhook-client

#To enable webhook start on startup open cron tab
crontab -e
#Then append below line to crontab file and save
@reboot /srv/github-webhook-client/run_app.sh

#If you can't reboot system now, then just run it manually first tie -
sh /srv/github-webhook-client/run_app.sh

#To verify it started, check java process
ps -ef | grep github-webhook-client
```

Reference: [Why @reboot will works?](http://askubuntu.com/a/816)

Once webhook runs, it will be accessible over http(/s)://{your ip/domain}:9050/util/github-webhook-client

# Configuration on Github.com
Go to your repository -> Settings -> Webhooks & Services. Add a new webhook with your address there and use the exact same Secret Key generated in GHSecretKey step during deploying.

# How to configure script calling
If you want to execute a shell script when Github calls your webhook then you can configure that in /srv/github-webhook-client/application.properties.

For a sample repo of manikmagar/test, entry should look like below (you can have shell script anywhere but accessible and executable with current linux user):
REPO_MANIKMAGAR_TEST_SHELL=/tmp/myshell.sh

# License
You guess'ed it right, its FREE under [MIT License](license.md).
