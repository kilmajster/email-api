<h1 align="center">
  gumtree-scraper<br>
  <a href="https://github.com/jvmops/email-api/actions"><img align="center" src="https://github.com/jvmops/email-api/workflows/master/badge.svg"></a>
  <a href="https://codecov.io/gh/jvmops/email-api"><img align="center" src="https://codecov.io/gh/jvmops/email-api/branch/master/graph/badge.svg"></a>
  <a href="https://github.com/jvmops/email-api/blob/master/LICENSE"><img align="center" src="https://img.shields.io/github/license/jvmops/email-api.svg"></a>
  <br><br>
</h1>

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
Now go to: http://localhost:8080 for a Swagger page
