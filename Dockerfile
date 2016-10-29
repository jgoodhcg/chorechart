FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/chorechart.jar /chorechart/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/chorechart/app.jar"]
