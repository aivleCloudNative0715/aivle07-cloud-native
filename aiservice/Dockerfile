# aiservice/Dockerfile
FROM eclipse-temurin:21 
WORKDIR /app # 컨테이너 내에서 작업 디렉토리 설정

# Maven 프로젝트 빌드에 필요한 모든 소스 파일을 컨테이너로 복사
# 이렇게 하면 Dockerfile 내부에서 Maven 빌드를 수행할 수 있습니다.
# 먼저 pom.xml을 복사하여 Maven 의존성 레이어를 캐시하는 것이 효율적입니다.
COPY pom.xml .
RUN mvn dependency:go-offline # 의존성만 미리 다운로드하여 캐시

# 나머지 소스 코드 복사 (application.yml 포함)
COPY src ./src

# 애플리케이션 빌드
RUN mvn clean package -DskipTests

# 빌드된 JAR 파일 복사 (일반적으로 target 디렉토리에 생성됨)
COPY target/*.jar app.jar

# Spring Boot 애플리케이션이 사용할 포트 노출
EXPOSE 8084

# 타임존 설정 (선택 사항이지만 일관성을 위해 유지)
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 애플리케이션 실행 명령어
ENTRYPOINT ["java","-Xmx400M","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar","--spring.profiles.active=docker"]