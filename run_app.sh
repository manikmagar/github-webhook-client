if [ -f pid.txt ]
then
	kill `cat pid.txt`
	rm pid.txt
fi
set -a
. /etc/default/github-webhook-client

nohup java -jar /srv/github-webhook-client/github-webhook-client.jar > /srv/github-webhook-client/log.txt 2> /srv/github-webhook-client/errors.txt < /dev/null & PID=$!
echo $PID > pid.txt

