FROM adoptopenjdk/openjdk11:latest
WORKDIR /
ENV INSTANCE_RANDOM true
ENV EPSILON "0.1"
ENV INSTANCE_MINJOBS 20
ENV INSTNACE_MAXJOBS 200
ENV INSTANCE_MINMACHINES 50
ENV INSTANCE_MAXMACHINES 100
ENV INSTANCE_MAX_SEQUENTIAL_TIME 100
ENV ELASTICSEARCH_HOST "localhost"
ENV ES_INDEX "testdata-"
ENV DETATCHED true
ENV EXECS_BEFORE_PUSH 10

ADD target/bachelorarbeit-1.0-SNAPSHOT-jar-with-dependencies.jar Bachelorarbeit.jar
ENTRYPOINT ["java", "-jar", "Bachelorarbeit.jar"]
