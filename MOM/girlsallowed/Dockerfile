FROM openjdk:11.0.8-jre
ENV HOST=localhost
COPY target/*-allinone.jar /girlsallowed.jar
CMD sleep 30 && java -jar /girlsallowed.jar $HOST