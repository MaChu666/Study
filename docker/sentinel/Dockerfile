FROM eclipse-temurin:8-jre
WORKDIR /app
COPY sentinel-dashboard-1.8.6.jar /app/sentinel-dashboard.jar
EXPOSE 8089
ENTRYPOINT ["java", "-Dserver.port=8089", "-Dcsp.sentinel.dashboard.server=localhost:8089", "-Dproject.name=sentinel-dashboard", "-jar", "/app/sentinel-dashboard.jar"]