# Pré-requis compilation

```bash
mvnw install:install-file -Dfile=libext/yeti-annotations.jar -DgroupId=com.arkham -DartifactId=annotations -Dversion=1.0 -Dpackaging=jar
mvnw install:install-file -Dfile=libext/ojdbc-12.2.jar -DgroupId=com.oracle -DartifactId=driver -Dversion=12.2 -Dpackaging=jar
mvnw install:install-file -Dfile=libext/orai18n-12.2.jar -DgroupId=com.oracle -DartifactId=i18n -Dversion=12.2 -Dpackaging=jar
mvnw install:install-file -Dfile=libext/arkham-common-1.1.jar -DgroupId=com.arkham -DartifactId=common -Dversion=1.1 -Dpackaging=jar
```

# Post file

```bash
cd standalone
\cp -f ../target/yeti-runner.jar .

# 15s (300s par défaut)
java -Xms128m -Xmx512m -XX:+UseZGC -XX:ZUncommitDelay=15 -XX:ZCollectionInterval=5 -jar yeti-runner.jar

curl -sX POST http://localhost:8080/api/v1/stream --data-binary @spl/SOC800_36370.yaml.processed -o spl/test.xls -H 'Content-Type: application/yaml'
```

**Docker**
```bash
docker build -f src/main/docker/Dockerfile.jvm -t yeti-quarkus:latest .

cd standalone

docker run -i --rm -p 8080:8080 yeti-quarkus:latest

curl -sX POST http://172.17.0.2:8080/api/v1/stream --data-binary @spl/SOC800_36370.yaml.processed -o spl/test.xls -H 'Content-Type: application/yaml'
```







# new-yeti-quarkus Project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/new-yeti-quarkus-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Provided Code

### RESTEasy Reactive

Easily start your Reactive RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)

