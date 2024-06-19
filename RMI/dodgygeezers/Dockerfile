FROM openjdk:8-jre-alpine
ENV RMI_SERVER broker
COPY target/*-allinone.jar /dodgygeezers.jar
CMD sleep 10 && java -jar /dodgygeezers.jar broker