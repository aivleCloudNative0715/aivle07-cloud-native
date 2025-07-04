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
- 구독 및 열람 내역 실시간 반영 (CQRS)
- 작가 및 관리자 기능
- 실시간 메시징 및 상태 전달 (Event)

---

## 🚀 배포 및 운영 환경

- **프론트엔드**는 Vercel을 통해 배포됨  
  👉 https://aivle07-cloud-native.vercel.app/

- **백엔드**는 Azure Kubernetes Service(AKS) 환경에서 운영  
  👉 https://yolang.shop/

- **Ingress Gateway**를 활용해 모든 HTTP 요청을 HTTPS로 안전하게 수신
  - Let's Encrypt + cert-manager를 사용한 자동 SSL 인증서 발급/갱신 구성

- **도메인 연결**
  - yolang.shop 도메인을 Ingress Controller와 연결
  - 마이크로서비스는 path 기반 routing으로 구분 (`/users`, `/books` 등)

- **CI/CD 자동화**
  - GitHub Actions 기반 파이프라인 구성
  - Docker 이미지는 Azure 기본 레지스트리를 사용하여 권한 문제 해결

- **보안 고려**
  - 프론트엔드에서 발생하는 HTTP 차단 이슈를 도메인 및 HTTPS 설정으로 해결
