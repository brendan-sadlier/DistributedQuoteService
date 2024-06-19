FROM openjdk:11.0.8-jre
ENV HOST=localhost
COPY target/*-allinone.jar /broker.jar
CMD java -jar /broker.jar $HOST