#~/bin/sh
echo "PWCS in the directory: " $PWD
echo "tag: " $tag
echo "BRANCH_OR_TAG: " $BRANCH_OR_TAG
git pull
if [ $tag != 'origin/master'  ] && [ $tag != 'master' ]; then
#  git checkout tags/$tag
#this is for branch checkout for now
	echo "checkout of" $tag
	git checkout $tag
fi
git pull

# Function to check if wildfly is up #
function wait_for_server() {
  until `/opt/wildfly/bin/jboss-cli.sh -c --controller=localhost:9990 ":read-attribute(name=server-state)" 2> /dev/null | grep -q running`; do
    sleep 1
  done
}

echo "=> build application"

echo ant -file build.xml -Ddebug=$debug -DBRANCH_OR_TAG=$BRANCH_OR_TAG -DlogLevel=DEBUG -DOBJCART.DS.PSWD=$OBJCART_DS_PSWD -Dtiername=dev -DOBJCART.DS.USER=$OBJCART_DS_USER -DOBJCART.DS.PORT=$OBJCART_DS_PORT -DOBJCART.DS.HOST=$OBJCART_DS_HOST dist

ant -file build.xml -Ddebug=$debug -DBRANCH_OR_TAG=$BRANCH_OR_TAG -DlogLevel=DEBUG -DOBJCART.DS.PSWD=$OBJCART_DS_PSWD -Dtiername=dev -DOBJCART.DS.USER=$OBJCART_DS_USER -DOBJCART.DS.PORT=$OBJCART_DS_PORT -DOBJCART.DS.HOST=$OBJCART_DS_HOST dist

echo "=> starting wildfly in background"
/opt/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0 &

echo "=> Waiting for the server to boot"
wait_for_server

echo "=> deploying modules"
cp dist/objcart104.war /local/content/cadsrobjectcart/
cp dist/jboss/* /local/content/cadsrobjectcart/jboss

/opt/wildfly/bin/jboss-cli.sh -c --controller=localhost:9990 --file=/local/content/cadsrobjectcart/jboss/objectcart_modules.cli

echo "=> reloading wildfly"
/opt/wildfly/bin/jboss-cli.sh --connect command=:reload

echo "=> Waiting for the server to reload"
wait_for_server

echo "=> deploying"
/opt/wildfly/bin/jboss-cli.sh -c --controller=localhost:9990 --file=/local/content/cadsrobjectcart/jboss/objectcart_setup_deploy.cli

echo "=> shutting wildfly down"
/opt/wildfly/bin/jboss-cli.sh --connect command=:shutdown

echo "=> starting wildfly in foreground"
/opt/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0 
