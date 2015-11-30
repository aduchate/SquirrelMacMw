FROM java:8
VOLUME /tmp
ADD squirrel-mac-mw-0.1.0.jar app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-jar","/app.jar"]
