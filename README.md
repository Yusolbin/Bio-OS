# BIO-OS

Algorithmic Bio-System Simulator

BIO-OS는 식물의 환경 상태를 입력받고,
그 상태를 알고리즘으로 계산해서 생존 상태, 에너지 변화, 성장 예측을 보여주는 시뮬레이션 프로젝트입니다.

처음에는 C++ 기반 생체 시뮬레이션 엔진으로 시작했고,
현재는 Spring Boot API, MySQL, Web Dashboard까지 연결한 풀스택 구조로 확장했습니다.

---

## 현재 버전

`v0.4`

현재 v0.4에서는 사용자 인증, 사용자별 데이터 분리, 관리자 대시보드까지 구현했습니다.

---

## 프로젝트 목표

이 프로젝트의 목표는 식물 상태를 숫자로 입력받고,
그 값을 기반으로 시스템 내부 상태가 어떻게 변하는지 확인하는 것입니다.

예를 들어 물, 빛, 온도, 습도 값을 입력하면
BIO-OS는 현재 식물이 안정적인지, 가뭄 상태인지, 열 스트레스 상태인지, 회복 상태인지 계산합니다.

그리고 그 결과를 웹 대시보드에서 확인할 수 있습니다.

---

## 주요 기능

### 1. Environment Simulation

물, 빛, 온도, 습도 값을 입력하면 현재 식물 상태를 계산합니다.

계산 결과로 아래 정보를 확인할 수 있습니다.

* Tick
* Water
* Light
* Temperature
* Humidity
* Total Energy
* Energy Delta
* Risk Level
* Last Action
* Visual State
* Active States
* Matched Rules
* Recommendation

---

### 2. Gene Rule System

시뮬레이션 규칙을 DB에 저장하고 관리할 수 있습니다.

예시:

```text
IF Water < 30 THEN DroughtMode = ON
IF Temperature > 35 THEN HeatStress = ON
```

관리자는 웹 화면에서 Gene Rule을 추가할 수 있고,
등록된 Rule은 MySQL에 저장됩니다.

Rule은 시뮬레이션 실행 시 BIO-OS 상태 계산에 반영됩니다.

---

### 3. Plant Pixel View

시뮬레이션 결과에 따라 식물 이미지가 바뀝니다.

현재 사용 중인 visual state는 아래와 같습니다.

```text
stable
drought_mode
heat_stress
pruned
pruning_already_executed
recovery_mode
low_energy
dead_critical
photosynthesis_boost
cold_stress
```

---

### 4. Growth Simulation

현재 환경값을 기반으로 선택한 식물의 N일 뒤 성장 상태를 예측합니다.

Growth Simulation에서는 아래 정보를 계산합니다.

* Plant Type
* Simulation Days
* Final Growth Score
* Final Risk Level
* Final Visual State
* Growth Summary
* Growth Timeline

---

### 5. Growth Timeline

성장 예측 결과를 날짜별 Timeline으로 저장하고 확인할 수 있습니다.

Timeline에는 날짜별로 아래 값이 들어갑니다.

* Day
* Growth Score
* Total Energy
* Risk Level
* Visual State

웹에서는 Timeline 표를 클릭해서 특정 날짜의 식물 상태를 다시 확인할 수 있습니다.

---

### 6. Growth Chart

Growth Timeline 데이터를 Canvas Chart로 시각화했습니다.

차트에서는 아래 두 값을 같이 확인할 수 있습니다.

* Growth Score
* Total Energy

Timeline을 재생하면 현재 선택된 날짜가 차트에서도 표시됩니다.

---

### 7. Saved Growth Simulation

실행한 Growth Simulation은 DB에 저장됩니다.

사용자는 저장된 성장 예측 기록을 다시 불러올 수 있습니다.

저장된 기록을 클릭하면 해당 Simulation의 상세 Timeline과 Growth Chart가 다시 렌더링됩니다.

---

### 8. User Auth

v0.4에서 사용자 인증 기능을 추가했습니다.

현재 지원하는 기능은 아래와 같습니다.

* 회원가입
* 로그인
* 로그아웃
* 로그인 상태 저장
* 사용자별 Simulation Log 저장
* 사용자별 Growth Simulation 저장

로그인 화면은 `auth.html`로 분리했습니다.

대시보드인 `index.html`에서는 로그인된 사용자만 접근할 수 있습니다.

---

### 9. Role-based UI Guard

사용자 역할은 `USER`, `ADMIN`으로 나뉩니다.

현재 동작 방식은 아래와 같습니다.

```text
로그인하지 않은 사용자
- index.html 접근 시 auth.html로 이동

USER
- Simulation 실행 가능
- 자기 Simulation Log 조회 가능
- 자기 Growth Simulation 조회 가능
- Admin Dashboard 사용 불가
- Gene Rule 관리 불가

ADMIN
- Simulation 실행 가능
- Growth Simulation 실행 가능
- Admin Dashboard 사용 가능
- Gene Rule 관리 가능
```

첫 번째로 가입한 사용자는 `ADMIN`이 됩니다.
그 이후 가입자는 `USER`가 됩니다.

현재 화면에는 username, userId 같은 사용자 식별 정보가 노출되지 않게 정리했습니다.

---

### 10. Admin Dashboard

ADMIN 계정은 전체 시스템 통계를 확인할 수 있습니다.

현재 Admin Dashboard에서 확인할 수 있는 정보는 아래와 같습니다.

* Plant Type Count
* Gene Rule Count
* Simulation Log Count
* Growth Simulation Count
* User Count
* Average Growth Score
* Average Water
* Average Light
* Average Temperature
* Average Humidity
* Latest Plant
* Latest Risk
* Latest Visual State
* Risk Distribution
* Risk Distribution Chart
* Environment Average Chart
* Admin Insights
* Overall System Status

---

## 버전별 개발 흐름

### v0.1

C++ 기반 BIO-OS 엔진을 만들었습니다.

주요 내용:

* WaterDistributor
* EnergyEvaluator
* PruningStrategy
* RuleParser
* StateTransitionEngine
* TickSystem
* SimulationLogger
* EngineFacade

---

### v0.2

Spring Boot와 MySQL을 붙였습니다.

주요 내용:

* Simulation API
* Simulation Log 저장
* Gene Rule API
* Plant Type API
* MySQL 연동
* Web Dashboard 기본 구조

---

### v0.3

Growth Simulation 중심으로 기능을 확장했습니다.

주요 내용:

* Plant Type 기반 성장 예측
* Humidity 추가
* Growth Simulation API
* Growth Timeline 저장
* Growth History 조회
* Growth Chart 시각화
* Timeline Replay

---

### v0.4

Auth와 Admin 기능을 추가했습니다.

주요 내용:

* auth.html / auth.js 분리
* 회원가입 / 로그인 / 로그아웃
* 사용자별 Simulation Log 저장
* 사용자별 Growth Simulation 저장
* USER / ADMIN 권한 분리
* Admin Dashboard
* Admin Insights
* Overall System Status
* 사용자 식별 정보 UI 노출 제거

---

## 기술 스택

### Core Engine

```text
C++
Algorithm
State Machine
Rule-based Simulation
```

### Backend

```text
Java 21
Spring Boot
Spring Web
Spring Data JPA
Maven
```

### Database

```text
MySQL
```

### Frontend

```text
HTML
CSS
JavaScript
Canvas API
```

### AI / Data

```text
Python
scikit-learn
DecisionTreeClassifier
CSV dataset
```

### Version Control

```text
Git
GitHub
```

---

## 프로젝트 구조

```text
Bio_OS/
├─ ai/
│  ├─ bio_os_tick_history.csv
│  ├─ train_action_model.py
│  ├─ predict_action.py
│  └─ model files
│
├─ backend/
│  └─ bio-os/
│     ├─ src/
│     │  └─ main/
│     │     ├─ java/
│     │     │  └─ com/yusolbin/bio_os/
│     │     │     ├─ controller/
│     │     │     ├─ dto/
│     │     │     ├─ model/
```
