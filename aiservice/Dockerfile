# ---------- 1단계: 빌드 ----------
# Maven과 JDK가 포함된 이미지를 사용하여 애플리케이션을 빌드합니다.
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# 작업 디렉토리를 /app으로 설정합니다.
WORKDIR /app

# Maven 의존성 캐싱을 위해 pom.xml을 먼저 복사합니다.
# 이렇게 하면 pom.xml이 변경되지 않는 한 의존성 다운로드를 다시 할 필요가 없습니다.
COPY pom.xml .
RUN mvn dependency:go-offline

# 프로젝트의 모든 소스 코드를 복사합니다.
COPY src ./src

# Maven을 사용하여 애플리케이션을 빌드하고 테스트는 건너뜁니다.
RUN mvn clean package -DskipTests

# ---------- 2단계: 런타임 ----------
# 빌드된 애플리케이션을 실행할 가벼운 런타임 이미지를 선택합니다.
# OpenJDK만 포함된 이미지를 사용하여 최종 이미지 크기를 최소화합니다.
FROM eclipse-temurin:21-jre-alpine 
# 작업 디렉토리를 /app으로 설정합니다.
WORKDIR /app

# 빌드 단계에서 생성된 JAR 파일을 런타임 이미지로 복사합니다.
COPY --from=builder /app/target/*.jar app.jar

# 시간대(Asia/Seoul)를 설정합니다.
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 애플리케이션이 사용할 포트(8084)를 노출합니다.
EXPOSE 8084

# 애플리케이션 실행 명령어를 정의합니다.
# -Xmx400M: JVM 최대 힙 메모리 설정 (필요에 따라 조정)
# -Djava.security.egd=file:/dev/./urandom: 엔트로피 소스 설정 (Docker 환경에서 성능 향상)
# --spring.profiles.active=docker: Docker 프로파일을 활성화하여 spring 설정을 로드합니다.
ENTRYPOINT ["java", "-Xmx400M", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar", "--spring.profiles.active=docker"]