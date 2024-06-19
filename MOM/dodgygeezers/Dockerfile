FROM openjdk:11.0.8-jre
ENV HOST=localhost
COPY target/*-allinone.jar /dodgygeezers.jar
CMD sleep 20 && java -jar /dodgygeezers.jar $HOST