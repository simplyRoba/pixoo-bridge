# Liberica is recomended by Spring
FROM bellsoft/liberica-openjre-alpine-musl:17

ARG UNAME=pixoo
ARG UID=1000
ARG GID=1000

RUN addgroup -g $GID -S $UNAME
RUN adduser -u $UID -S $UNAME -G $UNAME
USER $UNAME

VOLUME /tmp

COPY build/libs/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
