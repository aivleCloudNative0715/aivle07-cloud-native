# Aivle 걷다가서재

AI 기반 자동 출간 및 구독 플랫폼  
**도메인 주도 설계(DDD)** 와 **마이크로서비스 아키텍처(MSA)** 기반으로 구현된  
클라우드 네이티브 애플리케이션 프로젝트입니다.

## 🧩 주요 기술 스택

- **Spring Boot**, **React** 기반 마이크로서비스
- **Kubernetes** + **Azure** 배포
- **Istio** 기반 **Service Mesh**
- **CI/CD** 자동화 파이프라인 구성
- **Event-Driven Architecture**
- **Domain-Driven Design (DDD)** 적용

## 🎯 핵심 기능

- AI 기반 자동 출판 기능
- 포인트 기반 전자책 구독 시스템
- 구독 및 열람 내역 실시간 반영 (CQRS)
- 작가 및 관리자 기능
- 실시간 메시징 및 상태 전달 (Event)

---

## 🔌 API Gateway

본 프로젝트의 API Gateway는 **8088 포트**에서 실행됩니다.  
각 마이크로서비스의 라우팅 테스트 시 반드시 **http://localhost:8088** 주소를 사용해야 합니다.

예시 (회원가입 테스트):
```bash
http POST http://localhost:8088/users/signup name=홍길동 email=test@example.com
```

> 📁 Repository 구조, 사용 방법, 시연 영상 등은 추후 업데이트 예정입니다.
