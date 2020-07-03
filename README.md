<h1 align="center">
  emails toolkit<br>
  <a href="https://github.com/jvmops/email-api/actions"><img align="center" src="https://github.com/jvmops/email-api/workflows/master/badge.svg"></a>
  <a href="https://codecov.io/gh/jvmops/email-api"><img align="center" src="https://codecov.io/gh/jvmops/email-api/branch/master/graph/badge.svg"></a>
  <a href="https://github.com/jvmops/email-api/blob/master/LICENSE"><img align="center" src="https://img.shields.io/badge/License-MIT-yellow.svg"></a>
  <br><br>
</h1>

## Description
This emails toolkit consist of two apps:
- **api** to create new emails without sending it
- **sender** that delivers pending messages

## Building & Running:
Requirements:
- JDK 14
- docker
- docker-compose
```
git clone https://github.com/jvmops/email-api.git \
  && cd email-api \
  && ./gradlew build \
  && ./gradlew dockerBuildImageApi \
  && ./gradlew dockerBuildImageSender

docker-compose up -d
```

## Test report
```
 GET /emails - is paginated and defaults to first page PASSED
 GET /emails/{id} - retrieving email by an unknown ID result in 404 PASSED
 GET /emails/{id} - you can retrieve existing email by its id PASSED
POST /emails - Validation - payload needs to be provided PASSED
POST /emails - Validation - email sender can't be null PASSED
POST /emails - Validation - email sender can't be an empty string PASSED
POST /emails - Validation - email sender needs to be a valid email address PASSED
POST /emails - Validation - email recipients might be null PASSED
POST /emails - Validation - email recipients may be an empty set PASSED
POST /emails - Validation - email recipients must contain valid email addresses PASSED
POST /emails - Validation - email topic can't be empty PASSED
POST /emails - Validation - mail body can't be empty PASSED
POST /emails - if email was created expect its id to be returned PASSED
POST /emails - created email can be retrieved through API by its id PASSED
POST /emails - created email will have the lowest priority by default PASSED
POST /emails - you can pass custom priority while creating email PASSED
POST /emails - created email will get PENDING status PASSED
 GET /emails - pagination - test dataset contains multiple pages of emails PASSED
```

## Stats
```
$ docker stats
NAME                 MEM USAGE
emails-sender        91.22MiB
emails-api           115MiB
emails-rabbitmq      127.7MiB
emails-mongo         78.59MiB

$ docker images
REPOSITORY               SIZE
jvmops/emails-api        261MB
jvmops/emails-sender     257MB
```