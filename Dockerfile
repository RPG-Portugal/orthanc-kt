FROM gradle:latest AS build
WORKDIR /usr/app/
COPY ../.. .
RUN gradle fatJar || return 0

FROM eclipse-temurin:21
COPY --from=build /usr/app/build/libs/orthanc-*-all.jar .
CMD ["/bin/sh","-c","java -XX:+UseZGC -XX:+ZGenerational -Xmx2G -jar ./orthanc-*-all.jar"]