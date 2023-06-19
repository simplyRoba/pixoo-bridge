# Liberica is recomended by Spring
FROM bellsoft/liberica-openjre-alpine-musl:17
VOLUME /tmp
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]