FROM openjdk:11.0.8-jre
ENV HOST=localhost
COPY target/*-allinone.jar /auldfellas.jar
CMD sleep 10 && java -jar /auldfellas.jar $HOST