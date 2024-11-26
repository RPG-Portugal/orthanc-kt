FROM eclipse-temurin:21-alpine
COPY ./orthanc-kt-*-all.jar ./orthanc-*-all.jar .
CMD ["/bin/sh","-c","java -XX:+UseZGC -XX:+ZGenerational -Xmx2G -jar ./orthanc-*-all.jar"]
