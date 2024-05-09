FROM eclipse-temurin:17
LABEL maintainer="mdomingu@mail.com"
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} dan-pedidos.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/dan-pedidos.jar"]