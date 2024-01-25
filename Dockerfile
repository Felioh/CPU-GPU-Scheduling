FROM maven:3.9.4-eclipse-temurin-11-alpine as build
WORKDIR /src
COPY . .
RUN mvn package -DskipTests

FROM adoptopenjdk/openjdk11:latest
WORKDIR /

ENV NB_TESTS=10
ENV EPSILON "0.1"
ENV INSTANCE_MINJOBS 10
ENV INSTANCE_MAXJOBS 10
ENV INSTANCE_MINMACHINES 10
ENV INSTANCE_MAXMACHINES 10
ENV INSTANCE_SEQMACHINES=5
ENV INSTANCE_MAX_SEQUENTIAL_TIME 100
ENV ES_HOST "217.94.61.24"
ENV ES_PORT 9200
ENV ES_INDEX "hpc-2024-optimized"

COPY --from=build --chmod=0777 /src/target/cpu-gpu-scheduling-1.0-SNAPSHOT-jar-with-dependencies.jar /app/SchedulingAlgorithms.jar
# use the Epslion ("noops") GC, because we want to measure the time of these algorithms.
ENTRYPOINT ["java", "-jar", "/app/SchedulingAlgorithms.jar", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseEpsilonGC"]