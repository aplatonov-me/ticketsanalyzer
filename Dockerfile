FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN apt-get update && \
    apt-get install -y maven && \
    mvn clean package && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

RUN mkdir /data

ENTRYPOINT ["java", "-jar", "/app/target/TicketAnalyzer-1.0-SNAPSHOT-jar-with-dependencies.jar"]