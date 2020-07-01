<h1 align="center">
  email-api<br>
  <a href="https://github.com/jvmops/email-api/actions"><img align="center" src="https://github.com/jvmops/email-api/workflows/master/badge.svg"></a>
  <a href="https://codecov.io/gh/jvmops/email-api"><img align="center" src="https://codecov.io/gh/jvmops/email-api/branch/master/graph/badge.svg"></a>
  <a href="https://github.com/jvmops/email-api/blob/master/LICENSE"><img align="center" src="https://img.shields.io/badge/License-MIT-yellow.svg"></a>
  <br><br>
</h1>

## Description
This is a basic email API that allows to create email messages before they will be send.

## Building from sources:
Requirements:
- JDK 14
- docker

```
git clone https://github.com/jvmops/email-api.git
cd email-api
./gradlew build
docker build -t jvmops/email-api .
```

## Running
Requirements:
- docker-compose

Docker image is available at [docker hub](https://hub.docker.com/r/jvmops/email-api). Building from sources is not required to run this app.
```
git clone https://github.com/jvmops/email-api.git
cd gumtree-scraper
docker-compose up -d
```

## Stats
This API runtime after initial warmup takes only 110mb RAM

```
$ docker stats
CONTAINER NAME          MEM USAGE
email-api               109.5MiB
mongo                   77.36MiB

$ docker images
REPOSITORY              SIZE
jvmops/email-api        261MB
```