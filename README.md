# Event-Driven Microservices with Apache Kafka

> **CTSE Lab 06 — Microservices + Kafka**  
> **Author:** THAMEL W M A

A Spring Boot microservices architecture demonstrating event-driven communication using Apache Kafka (KRaft mode — no ZooKeeper).

## 📐 Architecture

```
Client (Postman / curl)
         │
         ▼  POST /orders
┌─────────────────────┐
│    API Gateway      │  :8080
│  Spring Cloud MVC   │
└─────────┬───────────┘
          │ routes to
          ▼
┌─────────────────────┐
│   Order Service     │  :8081
│  Kafka Producer     │──────────────────────┐
└─────────────────────┘                      │
                                   [order-topic]
                                             │
                    ┌────────────────────────┴──────────────────────┐
                    ▼                                               ▼
     ┌──────────────────────────┐               ┌──────────────────────────┐
     │   Inventory Service      │  :8082         │    Billing Service       │  :8083
     │   Kafka Consumer         │               │    Kafka Consumer        │
     │   → "Stock Updated"      │               │    → "Invoice Generated" │
     └──────────────────────────┘               └──────────────────────────┘
```

## 🛠️ Tech Stack

| Technology | Version |
|---|---|
| Java | 17 |
| Spring Boot | 3.5.x |
| Spring Cloud Gateway (MVC) | 2025.0.x |
| Spring Kafka | Latest (managed by Boot) |
| Apache Kafka | 7.6.0 (Confluent) — KRaft mode |
| Docker / Docker Compose | Latest |
| Lombok | Latest |
| Maven | Wrapper included |

## 📁 Project Structure

```
CTSE_Lab06_Microservices_Kafka/
├── .gitignore                          # Ignore build artifacts, IDE files, and temp files
├── docker-compose.yml                  # Kafka (KRaft) container
├── kafka-microservices.postman_collection.json
│
├── api-gateway/                        # Port 8080
│   └── src/main/resources/
│       └── application.properties
│
├── order-service/                      # Port 8081
│   └── src/main/java/com/microservices/order_service/
│       ├── model/Order.java
│       ├── controller/OrderController.java
│       └── service/OrderProducerService.java
│
├── inventory-service/                  # Port 8082
│   └── src/main/java/com/microservices/inventory_service/
│       ├── model/Order.java
│       └── consumer/InventoryConsumer.java
│
└── billing-service/                    # Port 8083
    └── src/main/java/com/microservices/billing_service/
        ├── model/Order.java
        └── consumer/BillingConsumer.java
```

## ⚡ Quick Start

### Prerequisites
- Java 17+
- Docker Desktop (running)
- Maven (or use the included `mvnw` wrapper)

---

### Step 1 — Start Kafka

From the project root (`CTSE_Lab06_Microservices_Kafka/` directory):

```bash
docker-compose up -d
```

Verify it's running:

```bash
docker ps
# You should see: kafka   Up   0.0.0.0:9092->9092/tcp
```

---

### Step 2 — Start All Services

Open **4 separate terminals**, one per service:

```bash
# Terminal 1
cd api-gateway && ./mvnw spring-boot:run

# Terminal 2
cd order-service && ./mvnw spring-boot:run

# Terminal 3
cd inventory-service && ./mvnw spring-boot:run

# Terminal 4
cd billing-service && ./mvnw spring-boot:run
```

> **Windows users:** use `mvnw.cmd spring-boot:run`

---

### Step 3 — Test the API

**Using curl:**
```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{"orderId": "ORD-1001", "item": "Laptop", "quantity": 1}'
```

**Using PowerShell:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8080/orders" -Method POST `
  -ContentType "application/json" `
  -Body '{"orderId":"ORD-1001","item":"Laptop","quantity":1}'
```

**Expected Response:**
```
Order placed successfully! OrderId: ORD-1001
```

---

### Step 4 — Verify the Event Flow

Check **each service terminal** for the following logs:

| Service | Expected Log |
|---------|-------------|
| **Order Service** | `Order Service: Event Published → orderId=ORD-1001, item=Laptop, quantity=1` |
| **Inventory Service** | `Inventory Service received order → ...` then `Stock Updated` |
| **Billing Service** | `Billing Service received order → ...` then `Invoice Generated` |

## 📬 Postman Collection

Import `kafka-microservices.postman_collection.json` into Postman for ready-made requests with automated test scripts.

**Requests included:**
- `POST http://localhost:8080/orders` — via API Gateway (×3 sample orders)
- `POST http://localhost:8081/orders` — direct to Order Service

## ⚙️ Configuration Summary

| Service | Port | Kafka Role | Group ID |
|---------|------|-----------|----------|
| api-gateway | 8080 | — | — |
| order-service | 8081 | Producer → `order-topic` | — |
| inventory-service | 8082 | Consumer ← `order-topic` | `inventory-group` |
| billing-service | 8083 | Consumer ← `order-topic` | `billing-group` |

> Both Inventory and Billing use **separate consumer groups**, so each service independently receives every message (fan-out pattern).

## 🐳 Kafka Docker Details

```yaml
Image:   confluentinc/cp-kafka:7.6.0
Mode:    KRaft (no ZooKeeper)
Port:    9092
Topic:   order-topic (auto-created on first publish)
```

## 🔍 Troubleshooting

| Problem | Fix |
|---------|-----|
| Services can't connect to Kafka | Ensure `docker-compose up -d` was run first and `docker ps` shows kafka as `Up` |
| No logs in Inventory/Billing | Check that both services started without errors; look for `ConsumerCoordinator` log confirming partition assignment |
| Port already in use | Stop any other process on ports 8080–8083 or 9092 |
| `mvnw` not executable (Linux/Mac) | Run `chmod +x mvnw` in each service directory |

## 🛑 Shutdown

```bash
# Stop Kafka
docker-compose down

# Stop all Spring Boot services: Ctrl+C in each terminal
```

To remove Kafka data volume as well:
```bash
docker-compose down -v
```
