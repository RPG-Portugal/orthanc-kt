#! /bin/bash

java -jar -XX:+UseZGC -XX:+ZGenerational -Xmx2G ./orthanc-kt-1.0.0-all.jar