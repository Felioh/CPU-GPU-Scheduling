Algorithms for machine scheduling with malleable jobs
---

An Implementation of the algorithms from Jansen & Land and Grage & Jansen, building upon an algorithm from Mounié, Rapine and Trystram.

- [Quick Start](#quick-start)
  - [Run the local test-files](#run-the-local-test-files)
- [Execution with docker-compose](#execution-with-docker-compose)
- [Building docker images](#building-docker-images)
- [Configuration for testing](#configuration-for-testing)
- [A couple of Maven commands](#a-couple-of-maven-commands)

# Quick Start
Build the docker image and run it. The `Dockerfile` contains reasonable default values for all variables.

```
docker build -t malleable . && docker run malleable
```

## Run the local test-files
```
docker build -t malleable . && docker run -e TEST_FILE_PATH=/testfiles -v $(pwd)/TestInstances:/testfiles:ro malleable
```
Output:
```console
ohnesorge@DESKTOP-FELIX:/mnt/d/workspace/CPU-GPU-Scheduling$ docker build -t malleable . && docker run -e TEST_FILE_PATH=/testfiles -v $(pwd)/TestInstances:/testfiles:ro malleable
[+] Building 50.9s (12/12) FINISHED
...
 => => naming to docker.io/library/malleable:latest 
12:12:35.857 [main] INFO  de.ohnes.App - Starting Algorithm!
12:12:36.397 [main] INFO  de.ohnes.App - Reading test file from /testfiles/TestInstance copy 2.json
12:12:36.398 [main] INFO  de.ohnes.DualApproximationFramework - Starting dual approximation Framework with shelvesAlgo: CpuGpuApproach
12:12:36.434 [main] INFO  de.ohnes.App - Ran instance with 3 machines and 8 jobs in 36 milliseconds.
12:12:36.437 [main] INFO  de.ohnes.App - Computed Schedule: 


///////////////////////////////////////////0002///////////////////////////////////////////###########################################0001###########################################/////////////////////////////////////////////////////0007/////////////////////////////////////////////////////#####################################################0006#####################################################
/////////////////////////////////////////////////////0005/////////////////////////////////////////////////////#####################################################0004#####################################################///////////////////////////////////////////0003///////////////////////////////////////////###########################################0000###########################################


12:12:36.557 [main] DEBUG de.ohnes.logger.MyElasticsearchClient - Trying to push test result to Elasticsearch...
12:12:36.684 [main] WARN  de.ohnes.logger.MyElasticsearchClient - Couldn't reach ES Server. Saving data locally until next try.
12:12:36.692 [main] INFO  de.ohnes.App - Reading test file from /testfiles/TestInstance copy 3.json
12:12:36.692 [main] INFO  de.ohnes.DualApproximationFramework - Starting dual approximation Framework with shelvesAlgo: CpuGpuApproach
12:12:36.707 [main] INFO  de.ohnes.App - Ran instance with 3 machines and 5 jobs in 15 milliseconds.
12:12:36.707 [main] INFO  de.ohnes.App - Computed Schedule: 
////////////////////////////0000////////////////////////////
///////////////////////0001///////////////////////
/////////////////////////////////0002/////////////////////////////////##################0004##################
/////////////////////////////////0002/////////////////////////////////
////////////////////////////0003////////////////////////////
...
```

# Execution with docker-compose
Hint: before execution docker-compose the images need to be build using docker. For this see section Docker.

Docker-compose can be started with the following command:
```
docker-compose up -d
```

The logs of each container should look something like this:
```
19:33:00.888 [main] INFO  de.ohnes.App - Starting Algorithm!
19:33:01.337 [main] INFO  de.ohnes.DualApproximationFramework - Starting dual approximation Framework with shelvesAlgo: KilianApproach
19:33:04.141 [main] INFO  de.ohnes.App - Ran instance with 90 machines and 81 jobs in 2 seconds.
19:33:04.142 [main] INFO  de.ohnes.DualApproximationFramework - Starting dual approximation Framework with shelvesAlgo: KilianApproach
```

# Building docker images
The docker image can be build with the following command.
```
docker build -t malleable .
```
The dockerfile is written as a `multi-stage dockerfile`. Therefore there is no need to build the maven project first or making sure you have the correct maven/java version. **Just build the docker container and go!**

# Configuration for testing

All parameters are tunable via the environment variables. Note that the code does support randomly generated instances but also allows reading in user-specified .json files containing an instance. (format as in `./TestInstances/`).


# A couple of Maven commands

Once you have configured your project in your IDE you can build it from there. However if you prefer you can use maven from the command line. In that case you could be interested in this short list of commands:

* `mvn compile`: it will just compile the code of your application and tell you if there are errors
* `mvn test`: it will compile the code of your application and your tests. It will then run your tests (if you wrote any) and let you know if some fails
* `mvn install`: it will do everything `mvn test` does and then if everything looks file it will install the library or the application into your local maven repository (typically under <USER FOLDER>/.m2). In this way you could use this library from other projects you want to build on the same machine

If you need more information please take a look at this [quick tutorial](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html).
