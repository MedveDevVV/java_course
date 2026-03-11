FROM tomcat:10.1.52-jdk17
RUN rm -rf /usr/local/tomcat/webapps/*
COPY autoservice-web/target/autoservice.war /usr/local/tomcat/webapps/ROOT.war
RUN mkdir -p /usr/local/tomcat/conf/autoservice
COPY autoservice.properties /usr/local/tomcat/
COPY database.properties /usr/local/tomcat/
EXPOSE 8080
CMD ["catalina.sh", "run"]