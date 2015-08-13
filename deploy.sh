mvn clean install

cd /home/farazath/DevTools/apache-tomcat-7.0.62/webapps
rm -rf ananya-services
rm ananya-services.war

echo "Removed Old Web App"

cp /home/farazath/Ananya/org.uom.raavana.ananya.services/target/ananya-services.war /home/farazath/DevTools/apache-tomcat-7.0.62/webapps
echo "Copied Latest Web App"


if [ "$1" = "remote" ]; then
	echo "Copying the file to remote server"
	scp ~/DevTools/apache-tomcat-7.0.62/webapps/ananya-services.war  ananya@ananya:/home/ananya/DevTools/apache-tomcat-7.0.62/webapps
else 
    echo ""
fi


echo "Starting the local Tomcat Server"
sh /home/farazath/DevTools/apache-tomcat-7.0.62/bin/catalina.sh run