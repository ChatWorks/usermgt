# Dockerfile to build a ldap server for DEVELOPMENT #
# None of the following is meant for production, esp. from a security pov #

# inspired by https://registry.hub.docker.com/u/wnameless/cas-mysql/dockerfile

## Use the official docker ubuntu distribution ##
FROM debian:wheezy

## Get some karma ##
MAINTAINER Sebastian Sdorra <sebastian.sdorra@triology.de>

# install wget, java, tomcat, supervisor
RUN echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu precise main"\
  > /etc/apt/sources.list.d/webupd8team-java.list
RUN echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu precise main"\
  >> /etc/apt/sources.list.d/webupd8team-java.list
RUN apt-key adv --keyserver keyserver.ubuntu.com --recv-keys EEA14886
RUN echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections
RUN apt-get update -y
RUN apt-get install -y wget supervisor oracle-java7-installer tomcat7
RUN apt-get clean && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

# configure tomcat
RUN usermod -s /bin/bash tomcat7
ADD keystore.jks /usr/share/tomcat7/.keystore
RUN sed -i "s#</Server>##g" /etc/tomcat7/server.xml; \
    sed -i "s#  </Service>##g" /etc/tomcat7/server.xml; \
    echo '    <Connector port="8443" protocol="HTTP/1.1" SSLEnabled="true" maxThreads="150" scheme="https" secure="true" clientAuth="false" sslProtocol="TLS" keystoreFile="/usr/share/tomcat7/.keystore" keystorePass="changeit" />' >> /etc/tomcat7/server.xml; \
    echo '  </Service>' >> /etc/tomcat7/server.xml; \
    echo '</Server>' >> /etc/tomcat7/server.xml

# install cas
RUN wget http://universe.triology.de/nexus/content/repositories/snapshots/de/triology/scm/universe/scmm-universe-cas/1.0-SNAPSHOT/scmm-universe-cas-1.0-20150205.073803-1.war -O /tmp/cas.war
RUN mkdir /var/lib/tomcat7/webapps/cas;\
    cd /var/lib/tomcat7/webapps/cas;\
    jar xf /tmp/cas.war;\
    rm -rf /tmp/cas.war

ADD cas.properties /root/cas.properties
ADD deployerConfigContext.xml /var/lib/tomcat7/webapps/cas/WEB-INF/deployerConfigContext.xml
ADD ticketRegistry.xml /var/lib/tomcat7/webapps/cas/WEB-INF/spring-configuration/ticketRegistry.xml
ADD run.sh /root/run.sh
RUN chmod +x /root/run.sh
ADD supervisord.conf /etc/supervisord.conf
RUN chown -R tomcat7:tomcat7 /var/lib/tomcat7

EXPOSE 8443

CMD ["/root/run.sh"]
