# Hospital Management System

A microservices Hospital Management System built with **Spring Boot 4 / Java 21 / Spring Cloud** and **React + Vite**, featuring an **AI symptom-triage engine** that drives a **Redis priority queue**, an **event-driven billing & notification backbone** on Kafka, and pharmacy inventory management.

---

## Standout feature — AI Symptom Triage + Smart Priority Queue

Most hospital projects queue patients first-come-first-served. This one doesn't.

A patient describes their symptoms in plain language. `triage-service` sends them to an LLM (Groq, free tier) which classifies the **department** and an **urgency level 1–5**. The appointment is then placed in a **Redis sorted-set priority queue** scored so urgency dominates arrival time:

```
score = (6 - urgency) * 10^12 + arrivalEpochSeconds
```

A CRITICAL chest-pain case booked at 4pm is seen before a routine check-up booked at 9am — while patients of equal urgency stay strictly first-come-first-served. If triage is unavailable, booking degrades gracefully to a default urgency rather than failing.

---

## Architecture

```
                    React + Vite (5173)
                            |
                    API Gateway (8080)  <- validates JWT once, forwards identity
                            |
              Eureka Service Registry (8761)
                            |
  +---------+----------+---------+----------+------------+----------+-------------+---------+
 auth     patient    doctor    triage   appointment   billing   notification   pharmacy
 8081      8082       8083      8084       8085         8086        8087         8088
                                  |          |            |           |            |
                                Groq       Redis   ------- Kafka -------        Kafka
                                           queue   appointment_completed
                                                   bill_paid / stock_low
                            |
                    PostgreSQL (database per service)
```

| Service | Port | Responsibility |
|---|---|---|
| `service-registry` | 8761 | Eureka service discovery |
| `api-gateway` | 8080 | Single entry point, JWT validation, CORS, routing |
| `auth-service` | 8081 | Registration, login, JWT issuing, roles |
| `patient-service` | 8082 | Patients, medical history, emergency contacts |
| `doctor-service` | 8083 | Doctors, departments, availability slots |
| `triage-service` | 8084 | **AI symptom triage** (Groq LLM) |
| `appointment-service` | 8085 | Booking, tokens, **Redis priority queue** |
| `billing-service` | 8086 | Bills, invoices, **PaymentStrategy** payments |
| `notification-service` | 8087 | Kafka-driven notifications (email-ready) |
| `pharmacy-service` | 8088 | Medicines, **StockManager** inventory, prescriptions |

### Event flow (Kafka)

```
appointment completed --> appointment_completed --+--> billing-service       (raises the invoice)
                                                  +--> notification-service  (alerts the patient)

bill paid             --> bill_paid            ------> notification-service
stock below reorder   --> stock_low            ------> notification-service  (pharmacy alert)
```

Consumers are **idempotent** — Kafka redelivery will not double-bill a patient.

### Design patterns

- **Strategy** — `PaymentStrategy` (Cash / Card / UPI / Insurance); each validates its own inputs and decides how the total settles. Insurance covers a configured share and collects the rest as co-pay. Adding a method = adding one class.
- **Observer** — `notification-service` reacts to domain events without producers knowing it exists.
- **Single-owner / facade** — `StockManager` owns every stock movement, so the low-stock alert can never be bypassed. Dispensing is all-or-nothing: a prescription never partially depletes stock.
- **Optimistic locking** — `@Version` on `Medicine` prevents two pharmacists dispensing off a stale stock count.

---

## Running it

### Prerequisites
Docker Desktop, JDK 21, Maven, Node 20+.

### 1. Configure
```bash
cp .env.example .env
# optional: add a free Groq key (no credit card) from https://console.groq.com
# GROQ_API_KEY=gsk_...
```
Without a Groq key everything still works — triage falls back to a default urgency.

### 2. Build the service jars
The Dockerfiles copy pre-built jars, so package first:
```bash
for s in service-registry api-gateway auth-service patient-service doctor-service \
         triage-service appointment-service billing-service notification-service pharmacy-service; do
  (cd backend/$s && mvn -DskipTests package)
done
```

### 3. Start everything
```bash
docker compose up --build
```
Frontend: http://localhost:5173 · Eureka: http://localhost:8761 · Gateway: http://localhost:8080

### Running a single service on the host
Services default to Docker hostnames, so override them when running locally:
```bash
java -jar target/appointment-service-0.0.1-SNAPSHOT.jar \
  --eureka.client.service-url.defaultZone=http://localhost:8761/eureka \
  --spring.datasource.url=jdbc:postgresql://localhost:5432/hms_appointment_db \
  --spring.data.redis.host=localhost \
  --spring.kafka.bootstrap-servers=localhost:9092
```

---

## Monitoring

Actuator + Prometheus metrics are exposed:
```
GET /actuator/health      # liveness/readiness, DB + discovery components
GET /actuator/metrics
GET /actuator/prometheus  # scrape endpoint
```

## Database migrations

Schema is generated by `ddl-auto=update`, which is fine for development but **does not alter existing CHECK constraints** when an enum gains a value. `backend/postgres/migrations/` holds the SQL needed when upgrading an existing database. A production deployment should adopt Flyway or Liquibase.

---

## Roadmap status

| Part | Scope | Status |
|---|---|---|
| 1 | Foundation, auth, role dashboards | Done |
| 2 | Patient management | Done |
| 3 | Doctors, departments, availability | Done |
| 4 | Appointments, Redis queue, **AI triage** | Done |
| 5 | Billing, payments, Kafka notifications | Done |
| 6 | Pharmacy, prescriptions, production hardening | Done |
