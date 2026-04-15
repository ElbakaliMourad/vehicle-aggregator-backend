# Vehicle Aggregator API

A fully reactive, non-blocking Spring Boot REST API designed to aggregate and normalize vehicle specifications and safety recalls. This service acts as a high-performance middleware layer between the frontend client and the US Government's NHTSA databases.

## 🚀 Tech Stack
* **Language:** Java 21
* **Framework:** Spring Boot 4.0 (WebFlux)
* **Caching:** Redis (Cache-Aside Pattern)
* **Testing:** JUnit 5, Mockito, WebTestClient, StepVerifier
* **Build Tool:** Maven

## 🏗️ Architecture Highlights
* **Reactive Pipeline:** Utilizes Spring WebFlux (`Mono`/`Flux`) to handle concurrent HTTP requests without blocking system threads, ensuring high throughput.
* **Distributed Caching:** Implements a Redis cache-aside strategy. Vehicle summaries and recall data are cached with a 30-day TTL to drastically reduce latency and rate-limiting risks from the external NHTSA APIs.
* **Resilient Fallbacks:** Dynamically decodes VINs on cache-misses to extract Make, Model, and Year before sequentially chaining the NHTSA Recalls API.

## ⚙️ Prerequisites
* [Java 21](https://aws.amazon.com/corretto/) (Amazon Corretto or similar)
* [Maven](https://maven.apache.org/)
* [Redis](https://redis.io/) running locally on port `6379`

## 🛠️ Getting Started

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/ElbakaliMourad/vehicle-aggregator-backend.git](https://github.com/ElbakaliMourad/vehicle-aggregator-backend.git)
    cd vehicle-aggregator-backend
    ```

2.  **Install dependencies and build the project:**
    ```bash
    mvn clean install
    ```

3.  **Run the application:**
    ```bash
    mvn spring-boot:run
    ```
    *The server will start on `http://localhost:8080`.*

## 🧪 Testing
To execute the comprehensive WebFlux unit and integration test suite:
```bash
mvn test