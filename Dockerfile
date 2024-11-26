FROM eclipse-temurin:21-alpine
COPY .build/libs/orthanc-kt-*-all.jar .build/libs/orthanc-*-all.jar .
CMD ["/bin/sh","-c","java -XX:+UseZGC -XX:+ZGenerational -Xmx2G -jar ./orthanc-*-all.jar"]
