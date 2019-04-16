#!/usr/bin/env bash

echo 'The following Maven command installs your Maven-built Java application'
echo 'into the local Maven repository, which will ultimately be stored in'
echo 'Jenkins''s local Maven repository (and the "maven-repository" Docker data'
echo 'volume).'
set -x
mvn jar:jar install:install help:evaluate -Dexpression=project.name
set +x

echo 'The following complex command extracts the value of the <name/> element'
echo 'within <project/> of your Java/Maven project''s "pom.xml" file.'
set -x
NAME=`mvn help:evaluate -Dexpression=project.name | grep "^[^\[]"`
set +x

echo 'The following complex command behaves similarly to the previous one but'
echo 'extracts the value of the <version/> element within <project/> instead.'
set -x
VERSION=`mvn help:evaluate -Dexpression=project.version | grep "^[^\[]"`
set +x

echo 'The following command will kill previously running process'
echo 'Moving wr file to /usr/share/tomcat8/webapps/ROOT.war'
set -x
sudo mv target/${NAME}-${VERSION}.war /usr/share/tomcat8/webapps/vz.war
set +x

echo 'The following command will kill previously running process'
echo 'Restarting tomcat application server.'
set -x
sudo /etc/init.d/tomcat8  restart
set +x

# echo 'The following command runs and outputs the execution of your Java'
# echo 'application (which Jenkins built using Maven) to the Jenkins UI.'
# set -x
# sudo nohup java -Dserver.port=80 -jar target/${NAME}-${VERSION}.war &
# sudo java -Dhudson.util.ProcessTree.disable=tru -Dserver.port=80 -jar target/${NAME}-${VERSION}.war &
