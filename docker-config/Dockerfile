FROM maven:3.9.11-eclipse-temurin-25 AS maven_tool
COPY pom.xml /tmp/
COPY src /tmp/src/
WORKDIR /tmp/
RUN mvn clean install

FROM eclipse-temurin:25
COPY --from=maven_tool /tmp/target/Study03-1.0.0-SNAPSHOT.jar application.jar
ENTRYPOINT ["java","--enable-native-access=ALL-UNNAMED","-jar","application.jar"]

HEALTHCHECK --interval=5m --timeout=3s \
  CMD curl -f http://localhost:8080/healthcheck || exit 1