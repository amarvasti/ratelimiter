FROM java:8-jdk-alpine
VOLUME /tmp
ADD build/libs/ratelimiter.jar ratelimiter.jar
RUN sh -c 'touch /ratelimiter.jar'
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/ratelimiter.jar"]