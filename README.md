# bankservice-accountservice
은행서비스 중 계좌 서비스 부분

accountservice는 kafka, API Gateway를 통해 연결 되며 고객에게 직접적으로 서비스가 연결되어 있지 않습니다.
- 고객의 정보 조회, 쓰기 등은 accountservice를 통해 이뤄지며 userservice에서는 정보를 조합하고 흐름을 분기하는 역할을 합니다.


![kakaobank_과제_systemarchitecture (10)](https://user-images.githubusercontent.com/24422677/234048735-dacece1d-e439-4cbd-b2fb-47de03ec9cac.jpg)



## Installation
해당 Source Code는 IntelliJ를 통해 개발되었으며, 터미널에서 'mvn spring-boot:run'으로도 실행 가능합니다.
Mac에서는 Logback의 파일 쓰기 권한 문제로 실행이 되지 않을 수 있습니다. 파일로 출력하는 부분을 콘솔로 출력하도록 변경하여 Mac에서도 실행 가능합니다.
![스크린샷 2023-04-25 00 52 35](https://user-images.githubusercontent.com/24422677/234050222-c8e8d74f-1ef1-47e0-967e-05fffbe81930.png)

Dockerfile를 생성하여 Docker 또는 Kubernetes 위에서 실행하는 것도 가능합니다.('mvn clean compile package -DskipTests=true' 실행하여 jar패키지 파일 획득)
```
FROM openjdk:17-ea-11-slim
VOLUME /tmp
COPY target/bankservice-userservice-0.0.1.jar bankservice-userservice.jar
ENTRYPOINT ["java","-jar","bankservice-userservice.jar"]
```

### Server 정보(Port)
- API Gateway : 8000
- Eureka Server : 8761
- config-service : 8888
- user-service : 0
- account-service : 0
- kafka : 8092
- redis : 6379
- MariaDB : 3306
