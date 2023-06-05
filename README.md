# 1. Introduce 🙌

Hi there ! 👋  본 Repository는 스포츠대회 운영관리 솔루션 Sports-IT의 백엔드 코드가 저장되어 있는 곳입니다. 🙋‍♂️ 🙋‍♀️

본 프로젝트는 JAVA를 활용하여 SPRING BOOT 프레임워크로 짜여진 RESTful API 코드입니다 🤓

기본적으로 Controller, Service, Repository로 구성된 Layered Architecture로 짜여진 코드로 Member, Competition 등 각각의 Domain에 따라 각 계층들이 구분되어 있습니다 📚

본 프로젝트의 개발환경을 개괄적으로 정리하자면 다음과 같습니다 🧐

# 2. 시작 가이드

### Requirements

- OS : Ubuntu 22.04
- JAVA : openjdk 17.0.7
- MySQL : 8.0.32 (AWS RDS 서버 연동 사용중)
- Docker : 24.0.2
- Spring Boot : 3.0.3
- Gradle : 7.6.1

## Installation

### GitHub으로 시작하기

1. **git clone**

```bash
$ git clone https://github.com/PlayMaker-S/Sports-IT-API.git
```

1. Build

```bash
$ cd SportsIT/
$ ./gradlew build
$ java -jar build.libs/SportsIT-0.0.1-SNAPSHOT
$ nohup java -jar build.libs/SportsIT-0.0.1-SNAPSHOT & [BG 실행]
```

### Docker로 시작하기

```bash
$ docker pull gmk0904/sports-it-api:latest
$ docker ps -q --filter name=sports-it-api | grep -q . && docker rm -f 
  \$(docker ps -aq --filter name=sports-it-api) [현재 동일한 이름의 컨테이너가 실행중이라면 종료]
$ docker run -d -p 80:8080 -v {resource 디렉토리 위치}:/src/main/resources --name sports-it-api gmk0904/sports-it-api:latest
```

## Start Locally

### application.properties

```bash
### MySQL ###
spring.datasource.url=jdbc:mysql://{mysql hostname:port/schema}
spring.datasource.username={username}
spring.datasource.password={password}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

### JPA ###
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.platform=org.hibernate.dialect.MySQLDialect

### JWT ###
jwt.header=Authorization
jwt.prefix=Bearer
jwt.secret={jwt-secret}
jwt.token-validity-in-seconds=86400

### S3 ###
cloud.aws.credentials.accessKey={S3 Access-key}
cloud.aws.credentials.secretKey={S3 Secret-key}
cloud.aws.s3.bucket={S3 Bucket Name}
cloud.aws.region.static={S3 Region}
cloud.aws.stack.auto-=false

### OnePort ###
oneport.apikey={Oneport API-key}
oneport.secret={Oneport Secret-key}
oneport.imp.uid={Oneport imp-uid}
```

### Resources

API 인스턴스 실행을 위해 아래와 같은 리소스들이 필요합니다. 
아래 파일들을 생성/다운받아 src/main/resources 밑에 저장해주세요.

**firebase-adminsjdk.json** : FireStore를 사용하기 위한 키를 발급

**application.properties** : 위의 내용을 작성하여 저장

애ㅊ 사용시 별도의 resources 디렉토리 생성 후 docker run
