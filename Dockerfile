# 멀티 스테이지 빌드를 위한 베이스 이미지 (Java 21 Alpine)
FROM eclipse-temurin:21-alpine AS builder

# 작업 디렉토리 설정
WORKDIR /app

# Gradle Wrapper와 build.gradle 파일들을 먼저 복사 (캐시 최적화)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# buildSrc 디렉토리 복사 (커스텀 플러그인 포함)
COPY buildSrc buildSrc

# API 모듈의 build.gradle 파일 복사
COPY api/build.gradle api/

# 소스 코드 복사
COPY api/src api/src

# Gradle 빌드 실행 (의존성 다운로드 및 애플리케이션 빌드)
RUN ./gradlew :api:bootJar --no-daemon

# 프로덕션 이미지용 베이스 이미지 (Java 21 Alpine)
FROM eclipse-temurin:21-alpine

# 보안을 위한 비root 사용자 생성
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일을 builder 스테이지에서 복사
COPY --from=builder /app/api/build/libs/*.jar app.jar

# 파일 소유권을 appuser로 변경
RUN chown -R appuser:appgroup /app

# 비root 사용자로 전환
USER appuser

# 애플리케이션 포트 노출
EXPOSE 8080

# JVM 옵션 설정 (메모리 최적화 및 GC 튜닝)
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseContainerSupport"

# 애플리케이션 실행 명령어
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
