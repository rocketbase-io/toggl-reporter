FROM frolvlad/alpine-oraclejdk8:slim

ADD *.jar /opt/application/app.jar
WORKDIR /opt/application/

EXPOSE 8080
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/urandom", "-Xmx512m", "-Xss256k",  "-jar", "app.jar"]