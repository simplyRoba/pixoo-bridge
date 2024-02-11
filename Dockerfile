# Liberica is recomended by Spring
FROM bellsoft/liberica-openjre-alpine-musl:21 AS builder
WORKDIR application
COPY build/libs/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract


FROM bellsoft/liberica-openjre-alpine-musl:21
ARG UNAME=pixoo
ARG UID=1000
ARG GID=1000

# for healthcheck
RUN apk --no-cache add curl

RUN addgroup -g $GID -S $UNAME
RUN adduser -u $UID -S $UNAME -G $UNAME
USER $UNAME

WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./

# when docker engine version 25 is wildly used, use: --start-period=30s --start-interval=10s --interval=3m
# https://docs.docker.com/engine/reference/builder/#healthcheck
HEALTHCHECK --interval=60s --timeout=10s --retries=3 \
  CMD curl -f -s http://localhost:4000/health/check || exit 1

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
