<div align="center">

  <img src="https://capsule-render.vercel.app/api?type=waving&height=190&color=0:020617,45:16A34A,100:2563EB&text=UPI%20Offline%20Mesh%20Payment%20Simulator&fontColor=FFFFFF&fontSize=34&fontAlignY=36&animation=fadeIn" alt="UPI Offline Mesh Payment Simulator Banner" />

  <br />

  <img src="https://readme-typing-svg.demolab.com?font=Fira+Code&weight=600&size=21&duration=2600&pause=900&color=16A34A&center=true&vCenter=true&width=900&lines=Spring+Boot+Backend+for+Offline+Payment+Routing;Hybrid+Encryption+%7C+Idempotency+%7C+Deferred+Settlement;Mesh+Gossip+Simulation+%7C+Duplicate+Protection+%7C+Tamper+Detection" alt="Typing animation" />

  <br />
  <br />

  <img src="https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17" />
  <img src="https://img.shields.io/badge/Spring%20Boot-Backend-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/Spring%20Data%20JPA-Database-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Data JPA" />
  <img src="https://img.shields.io/badge/H2-In--Memory%20DB-2563EB?style=for-the-badge" alt="H2 Database" />
  <img src="https://img.shields.io/badge/Security-RSA%20%2B%20AES--GCM-7C3AED?style=for-the-badge" alt="Security" />

  <br />
  <br />

  <b>A Spring Boot backend and mesh simulator that demonstrates how offline payment packets can be encrypted, routed through nearby devices, deduplicated, and settled exactly once.</b>

</div>

---

## Overview

**UPI Offline Mesh Payment Simulator** is a backend-focused project that demonstrates a possible offline payment flow where a transaction can move through nearby devices before reaching the server.

Imagine a user is in a basement with no internet. They create a payment, the payment is encrypted, and it is passed device-to-device through a Bluetooth-style mesh. When one bridge device later gets internet, it uploads the packet to the backend. The server then validates, decrypts, deduplicates, and settles the payment.

This project includes:

- A Spring Boot backend
- A software-based mesh simulator
- An interactive dashboard
- Hybrid encryption
- Idempotent settlement
- Replay and tamper protection
- Concurrency tests for duplicate packet delivery

---

## What Makes This Project Different

Most payment demos only show a simple API call from user to server. This project handles the harder offline case:

1. **No direct internet from sender**
   The sender does not need to directly reach the backend at payment creation time.

2. **Untrusted middle devices**
   Other devices can carry the packet, but they cannot read or modify the payment.

3. **Duplicate packet delivery**
   The same payment can reach the backend through multiple bridge devices, but it settles only once.

4. **Tamper detection**
   If any byte of the encrypted packet is changed, the backend rejects it.

5. **Deferred settlement**
   Payment is stored and settled later when the packet finally reaches the backend.

That makes it more than a CRUD backend. It demonstrates distributed-system thinking, security, and transaction safety.

---

## Key Features

<table>
  <tr>
    <td><b>Offline Mesh Simulation</b></td>
    <td>Simulates virtual phones passing encrypted payment packets through gossip rounds.</td>
  </tr>
  <tr>
    <td><b>Bridge Upload</b></td>
    <td>A bridge device with internet uploads stored packets to the backend for settlement.</td>
  </tr>
  <tr>
    <td><b>Hybrid Encryption</b></td>
    <td>Uses RSA-OAEP and AES-GCM so intermediaries cannot read or tamper with payment data.</td>
  </tr>
  <tr>
    <td><b>Idempotent Settlement</b></td>
    <td>Uses SHA-256 packet hashing and atomic claim logic to settle duplicate packets exactly once.</td>
  </tr>
  <tr>
    <td><b>Replay Protection</b></td>
    <td>Uses nonce and timestamp checks to reject stale or replayed packets.</td>
  </tr>
  <tr>
    <td><b>Transactional Ledger</b></td>
    <td>Debits sender, credits receiver, and records settlement inside a database transaction.</td>
  </tr>
  <tr>
    <td><b>Concurrency Test</b></td>
    <td>Tests simultaneous delivery of the same packet from multiple bridge nodes.</td>
  </tr>
</table>

---

## Tech Stack

### Backend

![Java](https://img.shields.io/badge/Java-17-007396?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-Backend-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-ORM-6DB33F?style=flat-square&logo=spring&logoColor=white)
![H2](https://img.shields.io/badge/H2-In--Memory%20Database-2563EB?style=flat-square)

### Security And Reliability

![RSA](https://img.shields.io/badge/RSA--OAEP-Key%20Encryption-7C3AED?style=flat-square)
![AES GCM](https://img.shields.io/badge/AES--256--GCM-Authenticated%20Encryption-16A34A?style=flat-square)
![SHA256](https://img.shields.io/badge/SHA--256-Packet%20Hashing-F97316?style=flat-square)
![Transactions](https://img.shields.io/badge/Transactions-Exactly%20Once%20Settlement-DC2626?style=flat-square)

### Testing

![JUnit](https://img.shields.io/badge/JUnit-Testing-25A162?style=flat-square&logo=junit5&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=flat-square&logo=apachemaven&logoColor=white)

---

## Architecture

```text
Sender Phone
    |
    | 1. Create PaymentInstruction
    | 2. Encrypt using hybrid encryption
    v
Encrypted MeshPacket
    |
    | Gossip through virtual devices
    v
Bridge Device With Internet
    |
    | POST /api/bridge/ingest
    v
Spring Boot Backend
    |
    +--> Hash ciphertext using SHA-256
    +--> Claim hash for idempotency
    +--> Decrypt packet using RSA + AES-GCM
    +--> Check timestamp freshness
    +--> Debit sender and credit receiver
    +--> Write transaction ledger
```

---

## Payment Processing Flow

```text
Compose Payment
      |
      v
Encrypt Payload
      |
      v
Inject Into Mesh
      |
      v
Run Gossip Rounds
      |
      v
Bridge Uploads Packet
      |
      v
Deduplicate Packet
      |
      v
Decrypt And Validate
      |
      v
Settle Transaction Once
```

---

## The Three Hard Problems Solved

### 1. Untrusted Intermediaries

The packet may pass through unknown devices before reaching the backend. To protect the payment, the project uses hybrid encryption.

- RSA-OAEP protects the AES key.
- AES-256-GCM encrypts the payment payload.
- AES-GCM also detects tampering through authentication tags.

So bridge devices can carry the packet, but they cannot read or modify it safely.

---

### 2. Duplicate Delivery

In a mesh network, the same payment may reach the server multiple times. If handled badly, the sender could be debited more than once.

This project prevents that by hashing the ciphertext and claiming the hash atomically before settlement.

```text
Same packet arrives 3 times
        |
        v
SHA-256(ciphertext)
        |
        v
Atomic claim
        |
        +--> First request: SETTLED
        +--> Other requests: DUPLICATE_DROPPED
```

Only the first valid packet moves forward to settlement.

---

### 3. Replay Attacks

An attacker may try to resend an old encrypted packet. The backend checks:

- Timestamp freshness
- Unique nonce
- Packet hash history

This helps reject stale or repeated packets before they affect the ledger.

---

## Project Structure

```text
upi-offline-mesh/
├── pom.xml
├── mvnw
├── mvnw.cmd
├── README.md
└── src/
    ├── main/
    │   ├── resources/
    │   │   ├── application.properties
    │   │   └── templates/dashboard.html
    │   └── java/com/demo/upimesh/
    │       ├── controller/
    │       │   ├── ApiController.java
    │       │   └── DashboardController.java
    │       ├── crypto/
    │       │   ├── HybridCryptoService.java
    │       │   └── ServerKeyHolder.java
    │       ├── model/
    │       │   ├── Account.java
    │       │   ├── Transaction.java
    │       │   ├── MeshPacket.java
    │       │   └── PaymentInstruction.java
    │       ├── service/
    │       │   ├── BridgeIngestionService.java
    │       │   ├── IdempotencyService.java
    │       │   ├── MeshSimulatorService.java
    │       │   └── SettlementService.java
    │       └── UpiMeshApplication.java
    └── test/
        └── java/com/demo/upimesh/
            └── IdempotencyConcurrencyTest.java
```

---

## API Endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| `GET` | `/` | Opens the interactive dashboard |
| `GET` | `/api/server-key` | Returns the server public key |
| `GET` | `/api/accounts` | Shows all accounts and balances |
| `GET` | `/api/transactions` | Shows recent transaction ledger entries |
| `GET` | `/api/mesh/state` | Shows packet state across virtual devices |
| `POST` | `/api/demo/send` | Creates, encrypts, and injects a payment into the mesh |
| `POST` | `/api/mesh/gossip` | Runs one mesh gossip round |
| `POST` | `/api/mesh/flush` | Bridge devices upload packets to backend |
| `POST` | `/api/mesh/reset` | Clears the mesh and idempotency cache |
| `POST` | `/api/bridge/ingest` | Production-style packet ingestion endpoint |
| `GET` | `/h2-console` | Opens the H2 database console |

---

## How To Run

### Prerequisites

- Java 17 or newer
- Maven wrapper is already included

Check Java:

```bash
java -version
```

---

### Run On Windows

```bash
mvnw.cmd spring-boot:run
```

If using PowerShell:

```bash
.\mvnw.cmd spring-boot:run
```

---

### Run On macOS/Linux

```bash
./mvnw spring-boot:run
```

Open:

```text
http://localhost:8080
```

---

## Run Tests

```bash
mvnw.cmd test
```

Important tests include:

- `encryptDecryptRoundTrip`
- `tamperedCiphertextIsRejected`
- `singlePacketDeliveredByThreeBridgesSettlesExactlyOnce`

These tests prove encryption, tamper rejection, and duplicate-safe settlement.

---

## Demo Flow

1. Open the dashboard at `http://localhost:8080`.
2. Compose a payment with sender, receiver, amount, and PIN.
3. Inject the encrypted packet into the mesh.
4. Run gossip rounds to spread the packet across virtual phones.
5. Flush bridge nodes so internet-enabled devices upload packets.
6. Check account balances and transaction ledger.
7. Run tests to verify concurrent duplicate handling.

---

## Sample Ingestion Request

```http
POST /api/bridge/ingest
Content-Type: application/json
X-Bridge-Node-Id: phone-bridge-42
X-Hop-Count: 3

{
  "packetId": "550e8400-e29b-41d4-a716-446655440000",
  "ttl": 2,
  "createdAt": 1730000000000,
  "ciphertext": "base64-encoded-rsa-and-aes-payload"
}
```

Sample response:

```json
{
  "outcome": "SETTLED",
  "packetHash": "a3f8c9...",
  "reason": null,
  "transactionId": 42
}
```

Other possible outcomes:

```text
DUPLICATE_DROPPED
INVALID
```

---

## Screenshots

Add screenshots inside a `screenshots` folder and update the paths below.

<table>
  <tr>
    <td align="center"><b>Dashboard</b></td>
    <td align="center"><b>Mesh State</b></td>
  </tr>
  <tr>
    <td><img src="screenshots/dashboard.png" width="420" alt="Dashboard Screenshot" /></td>
    <td><img src="screenshots/mesh-state.png" width="420" alt="Mesh State Screenshot" /></td>
  </tr>
  <tr>
    <td align="center"><b>Account Balances</b></td>
    <td align="center"><b>Transaction Ledger</b></td>
  </tr>
  <tr>
    <td><img src="screenshots/accounts.png" width="420" alt="Account Balances Screenshot" /></td>
    <td><img src="screenshots/ledger.png" width="420" alt="Transaction Ledger Screenshot" /></td>
  </tr>
</table>

---

## Resume Highlights

This project demonstrates:

- Spring Boot backend development
- REST API design
- Secure payment simulation
- RSA-OAEP and AES-GCM hybrid encryption
- SHA-256 hashing
- Idempotent transaction processing
- Optimistic locking
- Deferred settlement workflow
- Mesh network simulation
- Concurrency testing
- Transaction ledger design

---

## Production Improvements

This is a learning/demo project. A production-grade version would add:

- Real mobile BLE or Wi-Fi Direct communication
- Redis for distributed idempotency
- PostgreSQL or MySQL instead of in-memory H2
- Secure key storage through HSM, AWS KMS, or Vault
- JWT/mTLS authentication for bridge devices
- Rate limiting and fraud checks
- Real UPI/banking integration
- Structured logging and monitoring

---

## Author

<div align="center">

  <b>Aman Sharma</b>

  <br />
  <br />

  <a href="https://github.com/Softeraman">
    <img src="https://img.shields.io/badge/GitHub-Softeraman-181717?style=for-the-badge&logo=github&logoColor=white" alt="GitHub" />
  </a>
  <a href="https://www.linkedin.com/in/aman-sharma-8ab247239">
    <img src="https://img.shields.io/badge/LinkedIn-Aman%20Sharma-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white" alt="LinkedIn" />
  </a>

</div>

---

<div align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&height=120&section=footer&color=0:2563EB,45:16A34A,100:020617" alt="Footer wave" />
</div>
