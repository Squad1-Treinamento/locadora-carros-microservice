# Architecture & Business Rules – Locadora de Veículos Microservices

A comprehensive guide to the system architecture, domain models, business processes, and integration patterns. This document reflects the **current implementation status** with all recently committed features.

---

## Table of Contents

1. [System Overview](#system-overview)
2. [Implementation Status](#implementation-status)
3. [Services Architecture](#services-architecture)
4. [Domain Models & Entity Relationships](#domain-models--entity-relationships)
5. [Business Processes](#business-processes)
6. [Data Flows](#data-flows)
7. [Integration Patterns](#integration-patterns)
8. [Technical Patterns & Conventions](#technical-patterns--conventions)
9. [Database Design](#database-design)
10. [Error Handling](#error-handling)
11. [Deployment & Infrastructure](#deployment--infrastructure)

---

## System Overview

**Locadora de Veículos** is an educational microservices platform built with Spring Boot 4.0.6 (Java 21, Maven) that implements a **vehicle rental system**. The system manages the complete rental lifecycle from customer registration through vehicle selection to rental finalization.

### Core Principles

- **Educational Focus**: Designed to teach microservices concepts, domain-driven design, and distributed system patterns
- **Shared Database**: All services share a single PostgreSQL database (intentional training pattern; production systems would use independent databases)
- **REST-based Communication**: Services communicate synchronously via HTTP REST endpoints
- **Layered Architecture**: Strict separation of concerns across all services (Controller → Mapper → Service → Repository)
- **Async Notifications**: Decoupled notification layer using AWS SQS (mocked via LocalStack)
- **Modern Java**: Java 21 records used for immutable DTOs

### Technology Stack

- **Language**: Java 21
- **Framework**: Spring Boot 4.0.6
- **Build Tool**: Maven
- **Database**: PostgreSQL (single shared instance)
- **Message Queue**: AWS SQS (LocalStack for development)
- **Email Simulation**: Mailpit SMTP simulator
- **ORM**: JPA/Hibernate with Lombok annotations
- **API Documentation**: Swagger/OpenAPI

---

## Implementation Status

### Completed Features ✅

- [x] **pessoa-service**: Full CRUD for Motorista + notification integration
- [x] **carro-service**: Complete vehicle management (Fabricante, ModeloCarro, Acessorio, Carro)
- [x] **aluguel-service**: Rental orchestration with cross-service validation
- [x] **notification-service**: Multi-endpoint publisher (cadastro, reserva, aluguel, custom)
- [x] **notification-consumer-service**: SQS consumer with Mailpit integration
- [x] **Layered architecture**: All services follow mandatory structure

### In Development / Planned

- [ ] **api-gateway**: Mandatory routing + authentication (Phase 2)
- [ ] **Circuit breaker**: Resilience4j for fault tolerance
- [ ] **Event sourcing**: Migration from shared DB to event-driven sync
- [ ] **Caching**: Redis for vehicle availability
- [ ] **Frontend**: Web UI for user workflows

---

## Services Architecture

### Service Topology

The system consists of **7 microservices** organized in three layers:

```
┌─────────────────────────────────────────────────────┐
│                   API Gateway                       │
│          (Single entry point - optional initially)  │
└──────────────────┬──────────────────────────────────┘
                   │
        ┌──────────┼──────────┐
        │          │          │
    ┌───▼──────┐┌──▼───────┐┌▼──────────┐
    │ Pessoa   ││ Carro    ││ Aluguel   │
    │ Service  ││ Service  ││ Service   │
    │ (Port    ││ (Port    ││ (Port     │
    │ 8081)    ││ 8082)    ││ 8083)     │
    └──────────┘└──────────┘└───────────┘
        △          │           │
        │          │           │
        └──────────┼───────────┘
                   │
       ┌───────────┼────────────┐
       │           │            │
   ┌───▼──────┐┌───▼──────┐ ┌──▼───────────┐
   │Notif.    ││Notif.    │ │Notification  │
   │Service   ││Consumer  │ │Contracts     │
   │(8095)    ││(8096)    │ │(Shared Lib)  │
   └──────────┘└──────────┘ └──────────────┘
       │          ▲
       │          │
       └──▶ SQS Queue
```

### Service Responsibilities

#### **pessoa-service** (Port 8081) – Person Management ✅

Manages all person-related entities: clients, drivers, and employees.

**Entities**:
- `Pessoa` (abstract base class)
  - `id`: Auto-generated primary key
  - `nome`: Person's full name (required)
  - `cpf`: CPF number with validation (required, unique)
  - `email`: Email address (required, unique - identity for login)
  - `dataNascimento`: Birth date (required)
  - `sexo`: Gender (MALE/FEMALE enum)

- `Motorista` extends `Pessoa`
  - `numeroCNH`: Driver's license number (identifies driver type)

- `Funcionario` extends `Pessoa`
  - `matricula`: Employee ID/matriculation number (identifies employee type)

**Implemented Endpoints** ✅:
- `POST /motoristas` → Register new driver (triggers email notification) ✅
- `GET /motoristas/{id}` → Retrieve driver details ✅
- `PUT /motoristas/{id}` → Update driver information ✅
- `GET /funcionarios` → List employees
- Similar endpoints for `funcionario` management

**Business Rules**:
- Email uniqueness enforced at application level
- Driver registration triggers async email notification via notification-service
- Person type determined by presence of `numeroCNH` (driver) or `matricula` (employee)
- Notification failures logged but don't block registration

**Implementation Details** ✅:
- Integrated with `NotificationClient` for WebClient calls
- Uses `PessoaMapper` for DTO ↔ Entity conversion
- Fire-and-forget pattern for notifications (try-catch silently logs failures)
- Constructor injection for all dependencies

---

#### **carro-service** (Port 8083) – Vehicle Management ✅

Manages vehicle inventory, models, manufacturers, and accessories with full CRUD operations.

**Core Entities**:

- `Fabricante` (Manufacturer)
  - `id`: Primary key
  - `nome`: Manufacturer name (e.g., "Toyota", "Honda")

- `Categoria` (enum)
  - `HATCH_COMPACTO`, `HATCH_MEDIO`, `SEDAN_COMPACTO`, `SEDAN_MEDIO`, `SEDAN_GRANDE`
  - `MINIVAN`, `ESPORTIVO`, `UTILITARIO_COMERCIAL`

- `ModeloCarro` (Vehicle Model)
  - `id`: Primary key
  - `descricao`: Model description (e.g., "Corolla")
  - `categoria`: Enum from Categoria
  - `fabricante`: Foreign key to Fabricante
  - Relationship: one-to-many with Carro

- `Carro` (Vehicle/Car) **[UPDATED]** ✅
  - `id`: Primary key
  - `placa`: License plate (PR-001-ABC format, must be unique)
  - `chassi`: VIN (17 alphanumeric, validates format)
  - `cor`: Vehicle color
  - `valorDiaria`: BigDecimal daily rental rate
  - **`imagemUrl`: URL/path to vehicle image (TEXT column)** ✅ NEW
  - **`disponivel`: Boolean flag for availability status** ✅ NEW
  - `modelo`: Foreign key to ModeloCarro
  - `acessorios`: Many-to-many relationship with Acessorio

- `Acessorio` (Accessory/Feature)
  - `id`: Primary key
  - `descricao`: Feature description (e.g., "Air Conditioning", "Power Steering")

**Implemented Endpoints** ✅:
- `GET /carros` → List all vehicles with nested model/accessories ✅
- `GET /carros/{id}` → Vehicle details ✅
- `POST /carros` → Add vehicle to inventory ✅
- `PUT /carros/{id}` → Update vehicle info ✅
- `POST /admin/fabricantes` → Create manufacturer ✅
- `GET /admin/fabricantes` → List manufacturers ✅
- `POST /admin/modelos-carros` → Create model ✅
- `GET /admin/modelos-carros` → List models ✅
- `POST /admin/acessorios` → Create accessory ✅
- `GET /admin/acessorios` → List accessories ✅

**Controllers Implemented** ✅:
- `CarroController.java` - Vehicle CRUD
- `FabricanteController.java` - Manufacturer management
- `ModeloCarroController.java` - Model management
- `AcessorioController.java` - Accessory management

**Business Rules**:
- License plate format strictly validated
- VIN must be 17 characters excluding I, O, Q letters
- Daily rate stored as BigDecimal for precise financial calculations
- Vehicles can have multiple accessories (many-to-many)
- `disponivel` flag tracks real-time availability (updated by aluguel-service during rentals)
- Image URL stored for frontend rendering

---

#### **aluguel-service** (Port 8083) – Rental Orchestrator ✅ FULLY IMPLEMENTED

The **system orchestrator** that coordinates rentals between pessoa-service and carro-service. Only this service is authorized to call other domain services.

**Core Entities**:

- `StatusAluguel` (enum) ✅ NEW
  - `PENDENTE`: Rental request received, awaiting confirmation
  - `CONFIRMADO`: Rental confirmed and active
  - `FINALIZADO`: Rental completed and returned
  - `CANCELADO`: Rental cancelled

- `ApoliceSeguro` (Insurance Policy)
  - `id`: Primary key
  - `valorFranquia`: Deductible amount (BigDecimal)
  - `protecaoTerceiro`: Third-party liability coverage (boolean)
  - `protecaoCausasNaturais`: Natural causes coverage (boolean)
  - `protecaoRoubo`: Theft coverage (boolean)

- `Aluguel` (Rental Contract) **[UPDATED]** ✅
  - `id`: Primary key
  - **`idPessoa`: Foreign ID to Pessoa (driver)** ✅
  - **`idCarro`: Foreign ID to Carro (vehicle)** ✅
  - `dataPedido`: Rental request date/time
  - `dataEntrega`: Vehicle pickup date
  - `dataDevolucao`: Vehicle return date
  - **`valorDiaria`: Daily rate at time of rental** ✅
  - **`quantidadeDias`: Number of rental days (calculated)** ✅
  - `valorTotal`: Total rental cost
  - **`status`: Enum tracking rental lifecycle** ✅
  - `apoliceSeguro`: One-to-one relationship with insurance policy

**Implemented Endpoints** ✅:
- `POST /alugueis` → Create rental (full orchestration) ✅
- `GET /alugueis/{id}` → Retrieve rental details ✅
- `GET /alugueis/motorista/{idMotorista}` → List rentals by driver ✅
- `GET /alugueis` → List all rentals ✅

**Orchestration Logic** (AluguelService.solicitarAluguel) ✅:
1. ✅ Validate rental period: `dataDevolucao > dataEntrega`
2. ✅ Call pessoa-service: Verify Motorista exists (WebClient to 8081)
3. ✅ Call carro-service: Verify Carro exists and get daily rate (WebClient to 8083)
4. ✅ Check availability: Query overlapping rentals in date range
5. ✅ Calculate rental cost: `quantidadeDias × valorDiaria + insurance.valorFranquia`
6. ✅ Create ApoliceSeguro from request
7. ✅ Save Aluguel with PENDENTE status
8. ✅ Trigger notification: Call notification-service POST /send/aluguel (WebClient to 8095)
9. ✅ Return AluguelResponse with confirmation details

**Business Rules** (FULLY ENFORCED):
- ✅ Validates driver exists in pessoa-service before creating rental
- ✅ Validates vehicle exists and retrieves daily rate from carro-service
- ✅ Prevents double-booking: same vehicle cannot be rented overlapping date periods
- ✅ Rental period validation: must have valid start/end dates
- ✅ Rental requires insurance policy selection
- ✅ Total cost calculated: (dataDevolucao - dataEntrega) × vehicle.valorDiaria + insurance costs
- ✅ Only aluguel-service calls other domain services; no peer communication

---

#### **notification-service** (Port 8095) – Notification Publisher ✅ EXPANDED

Receives notification requests from domain services and publishes to message queue.

**REST Interface** (NotificationController) ✅:
- ✅ `POST /send/cadastro` → Receive driver registration event
  - Accepts `CadastroNotificationRequest`
  - Publishes message to SQS queue
  - Returns 200 OK immediately (fire-and-forget)

- ✅ `POST /send/reserva` → Receive vehicle reservation event (NEW)
  - Accepts `ReservaNotificationRequest`
  - Handles pre-rental reservation workflow

- ✅ `POST /send/aluguel` → Receive rental confirmation event (NEW)
  - Accepts `AluguelNotificationRequest`
  - Full rental details including vehicle, driver, dates, costs
  - Called by aluguel-service after successful rental creation

- ✅ `POST /send` → Generic custom notification (NEW)
  - Accepts `CustomNotificationRequest`
  - Flexible payload for arbitrary email types

**Implementation Details** ✅:
- `NotificationController` implements `NotificationControllerDoc` interface (Swagger)
- Uses `NotificationService` to publish messages
- Messages wrapped in `NotificationMessage` envelope with type indicators
- Does NOT access domain database or contain business logic

**Business Rules**:
- Notification failures must not block domain operations
- All notification DTOs defined in shared notification-contracts library
- Service is stateless (no persistence)
- SQS queue `locadora-notifications` created by init-aws.sh on startup

---

#### **notification-consumer-service** (Port 8096) – Message Consumer ✅

Polls SQS queue and sends emails via Mailpit.

**Operation**:
- Scheduled consumer polls `locadora-notifications` queue periodically
- Receives notification messages (CadastroNotificationRequest, AluguelNotificationRequest, etc.)
- Formats email body from message data
- Sends email via SMTP to Mailpit (port 1025)
- Mailpit UI available at http://localhost:8025

**Business Rules**:
- Operates independently; does not block on message availability
- Consumer continues polling even if individual email sends fail
- Mailpit simulates SMTP for development; production would use real email service
- No retry logic (messages processed as-they-arrive)

---

#### **notification-contracts** (Shared Library) ✅ EXPANDED

Centralizes DTOs shared between domain services and notification layer.

**Current Contracts** ✅:
- ✅ `CadastroNotificationRequest`: Driver registration notification
- ✅ `ReservaNotificationRequest`: Vehicle reservation notification (NEW)
- ✅ `AluguelNotificationRequest`: Rental confirmation with full details (NEW)
- ✅ `CustomNotificationRequest`: Generic flexible notifications (NEW)

**Purpose**:
- Provides single source of truth for notification message schemas
- Included in all domain services' pom.xml dependencies
- Reduces DTO duplication
- Enables type-safe cross-service communication


---

#### **api-gateway** (Port 8080) – Entry Point

Single external entry point for all services (initially optional, becoming mandatory).

**Current Role**:
- Optional routing layer
- Direct service access still permitted during development
- Will enforce authentication/authorization in future phases

---

## Domain Models & Entity Relationships

### Entity Relationship Diagram (Logical)

```
┌─────────────────────────────────────────────────────────┐
│                     PESSOA                              │
├─────────────────────────────────────────────────────────┤
│ id (PK)                                                 │
│ nome, cpf, email, dataNascimento, sexo                 │
│ INHERITANCE: JOINED TABLE (abstract base)              │
└──────────────────┬──────────────────────────────────────┘
                   │
        ┌──────────┴──────────┐
        │                     │
   ┌────▼─────┐         ┌────▼──────┐
   │ MOTORISTA│         │FUNCIONARIO│
   ├──────────┤         ├───────────┤
   │numeroCNH │         │matricula  │
   └──────────┘         └───────────┘

┌──────────────────────────────────────────────┐
│               FABRICANTE                     │
├──────────────────────────────────────────────┤
│ id (PK)                                      │
│ nome                                         │
└─────────────────┬──────────────────────────┘
                  │
                  │ 1:N
                  │
        ┌─────────▼─────────────────┐
        │    MODELOCARRO            │
        ├──────────────────────────┤
        │ id (PK)                  │
        │ descricao                │
        │ categoria (enum)         │
        │ fabricante_id (FK)       │
        └──────────┬────────────────┘
                   │
                   │ 1:N
                   │
              ┌────▼──────────────┐
              │     CARRO         │
              ├───────────────────┤
              │ id (PK)           │
              │ placa (unique)    │
              │ chassi (unique)   │
              │ cor               │
              │ valorDiaria       │
              │ imagemUrl ✅      │
              │ disponivel ✅     │
              │ modelo_id (FK)    │
              └────────┬──────────┘
                       │
                       │ M:N
                       │
        ┌──────────────┴────────────────┐
        │                               │
   ┌────▼──────────┐         (join table)
   │  ACESSORIO    │    carro_acessorio
   ├───────────────┤
   │ id (PK)       │
   │ descricao     │
   └───────────────┘

┌──────────────────────────────────────┐
│        APOLICESEGURO                 │
├──────────────────────────────────────┤
│ id (PK)                              │
│ valorFranquia                        │
│ protecaoTerceiro                     │
│ protecaoCausasNaturais               │
│ protecaoRoubo                        │
└────────────────┬─────────────────────┘
                 │
                 │ 1:1
                 │
            ┌────▼──────────┐
            │   ALUGUEL ✅   │
            ├───────────────┤
            │ id (PK)       │
            │ idPessoa (FK) │
            │ idCarro (FK)  │
            │ dataPedido    │
            │ dataEntrega   │
            │ dataDevolucao │
            │ valorDiaria   │
            │ quantidadeDias│
            │ valorTotal    │
            │ status (enum) │
            │ apolice_id(FK)│
            └───────────────┘
```

### Key Design Patterns

**Inheritance Strategy for Pessoa Hierarchy**:
- Uses `@Inheritance(strategy = InheritanceType.JOINED)`
- Each subclass (Motorista, Funcionario) has own table
- Pessoa fields duplicated via FK relationships
- Allows queries on parent type + type-specific queries on subtypes

**Many-to-Many: Carro ↔ Acessorio**:
- `@JoinTable` with explicit table name `carro_acessorio`
- Set-based collection for uniqueness
- Cascade rules: removing Carro removes join records
- Example: one Carro can have {Air Conditioning, Power Steering, GPS}

**Temporal Relationships**:
- Aluguel stores dates as Calendar (request time) and Date (pickup/return)
- Enables overlap detection for availability checks
- Financial calculations use BigDecimal for precision

---

## Business Processes

### US-01: Driver Registration (Cadastro de Motorista) ✅ COMPLETE

**Flow**:
1. Client submits driver registration form via POST /motoristas
2. Controller receives `PessoaRequest` record (DTO)
3. Mapper converts to `Motorista` entity
4. Service validates:
   - Email uniqueness (queries by email) ✅
   - Required fields present ✅
   - CPF format valid (JPA @CPF annotation) ✅
5. MotoristaRepository.save(motorista) ✅
6. Triggers notification:
   - Mapper converts Motorista → CadastroNotificationRequest ✅
   - WebClient calls notification-service POST /send/cadastro ✅
   - Notification failures logged but don't block registration response ✅
7. Returns 201 CREATED with driver data

**Example Request**:
```json
POST /motoristas
{
  "nome": "João Silva",
  "cpf": "123.456.789-00",
  "email": "joao@example.com",
  "dataNascimento": "1990-01-15",
  "numeroCNH": "98765432100",
  "sexo": "M"
}
```

**Key Business Rules**:
- ✅ Email must be unique (identity for login)
- ✅ CNH uniqueness recommended (not yet enforced)
- ✅ Registration always succeeds even if email notification fails
- ✅ Motorista type auto-detected by presence of numeroCNH

---

### US-02: Vehicle Selection (Escolha de Veículo) ✅ IN PROGRESS

**Flow**:
1. ✅ Client queries available vehicles: GET /carros
2. ✅ System returns all vehicles with:
   - Fabricante + ModeloCarro (nested) ✅
   - Accessories list ✅
   - Daily rate ✅
   - Image URL ✅ NEW
   - Availability flag ✅ NEW
3. Client may filter by:
   - Category (HATCH_MEDIO, SEDAN_GRANDE, etc.)
   - Accessories (air conditioning, GPS)
   - Budget (max daily rate)
4. Client selects vehicle and specifies rental period
5. **Validation** (implemented in aluguel-service):
   - ✅ Check vehicle availability for date range
   - ✅ Call aluguel-service to verify no overlapping rentals
6. Client adds to cart (frontend mock)

**Vehicle Data Example**:
```json
GET /carros
[
  {
    "id": 1,
    "placa": "PR-001-ABC",
    "chassi": "1G1FB1S52D1234567",
    "cor": "Branco",
    "valorDiaria": 150.00,
    "imagemUrl": "https://images.example.com/corolla.jpg",
    "modelo": {
      "id": 1,
      "descricao": "Corolla",
      "categoria": "SEDAN_MEDIO",
      "fabricante": {"id": 1, "nome": "Toyota"}
    },
    "acessorios": [
      {"id": 1, "descricao": "Ar Condicionado"},
      {"id": 2, "descricao": "Direção Assistida"}
    ]
  }
]
```

**Key Business Rules**:
- ✅ Only available vehicles displayed (filtered by `disponivel` flag)
- ✅ Vehicle details include manufacturer and category
- ✅ Accessories provided to support filtering
- ✅ Multiple vehicles share same accessories

---

### US-03: Rental Confirmation (Efetivação de Aluguel) ✅ COMPLETE

**Flow** (now fully implemented):
1. Client confirms rental from cart
2. POST /alugueis with:
   - Motorista ID
   - Carro ID
   - Delivery date (dataEntrega)
   - Return date (dataDevolucao)
   - Insurance policy selections
3. **Orchestration** (aluguel-service) ✅ COMPLETE:
   - ✅ Validate Motorista exists (call pessoa-service)
   - ✅ Validate Carro exists (call carro-service)
   - ✅ Check availability: no overlapping rentals in date range
   - ✅ Calculate cost: (dataDevolucao - dataEntrega) days × valorDiaria + insurance
   - ✅ Create ApoliceSeguro entity
   - ✅ Save Aluguel contract with PENDENTE status
4. **Trigger Notification** ✅:
   - ✅ Convert Aluguel → AluguelNotificationRequest
   - ✅ Call notification-service POST /send/aluguel
   - ✅ Email confirmation sent to driver
5. Return 201 CREATED with contract details

**Example Request**:
```json
POST /alugueis
{
  "idMotorista": 1,
  "idCarro": 5,
  "dataEntrega": "2026-06-01",
  "dataDevolucao": "2026-06-08",
  "apoliceSeguro": {
    "valorFranquia": 500.00,
    "protecaoTerceiro": true,
    "protecaoCausasNaturais": true,
    "protecaoRoubo": true
  }
}
```

**Example Response**:
```json
{
  "id": 1,
  "idPessoa": 1,
  "idCarro": 5,
  "dataPedido": "2026-05-19T10:30:00",
  "dataEntrega": "2026-06-01",
  "dataDevolucao": "2026-06-08",
  "valorDiaria": 150.00,
  "quantidadeDias": 7,
  "valorTotal": 1550.00,
  "status": "PENDENTE",
  "apoliceSeguro": {...}
}
```

**Key Business Rules** (ALL ENFORCED):
- ✅ No double-booking: date ranges cannot overlap for same vehicle
- ✅ Payment simulated (implementation ready)
- ✅ Insurance adds to total cost: `insurance.valorFranquia` included in `valorTotal`
- ✅ Rental confirmation creates AluguelContract in PENDENTE status
- ✅ Only aluguel-service calls other domain services; no peer communication

---

## Data Flows

### Registration Flow (Happy Path)

```
┌──────────────────────────────────────────────────────────────────┐
│ Client Browser                                                    │
│ POST /motoristas {nome, email, cpf, numeroCNH, ...}              │
└─────────────────────────────┬──────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│ MotoristaController                                         │
│ - Receives PessoaRequest record                             │
│ - HTTP binding validation (@Valid)                          │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────┐
│ PessoaMapper.toEntity()                                  │
│ - Detects numeroCNH present                              │
│ - Creates Motorista instance                             │
│ - Sets fields from DTO                                   │
└──────────────────┬───────────────────────────────────────┘
                   │
                   ▼
┌──────────────────────────────────────────────────────────┐
│ MotoristaService.cadastrar()                             │
│ - Email uniqueness check: pessoaRepository.findByEmail() │
│ - Throws GlobalHandlerException if duplicate             │
│ - motoristaRepository.save(motorista)                    │
└──────────┬────────┬──────────────────────────────────────┘
           │        │
           ▼        │
    [DATABASE]      │
    INSERT Motorista│
           │        │
           └────────┘
                   │
                   ▼
┌──────────────────────────────────────────────────────────┐
│ NotificationClient (async fire-and-forget)               │
│ - Mapper: Motorista → CadastroNotificationRequest        │
│ - WebClient.post() to http://notification-service:8095  │
│ - Try-catch block: errors logged, NOT re-thrown         │
└──────────────────┬───────────────────────────────────────┘
                   │
                   ├─→ [IF SUCCESS]
                   │   └─→ notification-service publishes to SQS
                   │       └─→ consumer processes message
                   │           └─→ Mailpit receives email
                   │
                   └─→ [IF FAILURE: Connection refused, timeout, etc.]
                       └─→ WARN logged: "Falha ao notificar cadastro..."
                           Response still returned 201 CREATED
                           Motorista saved in DB successfully
                           │
                           ▼
┌──────────────────────────────────────────────────────────┐
│ MotoristaController Response                             │
│ HTTP 201 CREATED                                         │
│ Body: PessoaResponse {id, nome, email, cpf, CNH, ...}   │
└──────────────────────────────────────────────────────────┘
```

### Rental Query Flow

```
┌──────────────────────────────────────────────────────────┐
│ Client: GET /carros?categoria=SEDAN                      │
└─────────────────┬────────────────────────────────────────┘
                  │
                  ▼
        ┌─────────────────────┐
        │ CarroController     │
        │ listar()            │
        └──────────┬──────────┘
                   │
                   ▼
    ┌─────────────────────────────┐
    │ CarroService.listar()       │
    │ carroRepository.findAll()   │
    └──────────┬──────────────────┘
               │
               ▼
       [DATABASE QUERY]
       SELECT * FROM carro
       JOIN modelocarro
       JOIN fabricante
       JOIN carro_acessorio
       JOIN acessorio
               │
               ▼
  ┌─────────────────────────────┐
  │ CarroMapper.toResponse()    │
  │ Entity → DTO records        │
  │ Nested: Modelo, Fabricante  │
  │ Collection: Acessorios      │
  └──────────┬──────────────────┘
             │
             ▼
    ┌──────────────────────────────────┐
    │ HTTP 200 OK                      │
    │ [CarroResponse {...}, ...]       │
    │ With all nested objects          │
    └──────────────────────────────────┘
```

---

## Integration Patterns

### Service-to-Service Communication (REST)

**Aluguel-Service calling Pessoa-Service**:
```java
// In AluguelService
WebClient webClient; // Injected with baseUrl

public void validateMotorista(Integer id) {
    Pessoa pessoa = webClient.get()
        .uri("/motoristas/{id}", id)
        .retrieve()
        .bodyToMono(PessoaResponse.class)
        .block(); // Synchronous wait
    
    if (pessoa == null) {
        throw new PessoaNotFoundException();
    }
}
```

**Characteristics**:
- WebClient-based (reactive, but block() for sync)
- RestTemplate alternative (traditional sync)
- Requests use base URL from application.properties
- Error handling: timeouts, connection refused, 404/500 responses

**Current Limitations**:
- No retry logic (first failure throws exception)
- No circuit breaker pattern
- Synchronous blocking (impacts latency)
- Future: Implement Resilience4j circuit breaker

---

### Notification Service Integration (Fire-and-Forget)

**Pessoa-Service → Notification-Service**:

```java
// In MotoristaService.cadastrar()
Motorista motorista = motoristaRepository.save(...);

try {
    CadastroNotificationRequest request = 
        PessoaMapper.toCadastroNotificationRequest(motorista);
    notificationClient.notificarCadastro(request);
} catch (Exception e) {
    logger.warn("Falha ao notificar cadastro: {}", e.getMessage());
    // DO NOT throw - registration already succeeded
}

return pessoaMapper.toResponse(motorista); // Success always
```

**Characteristics**:
- Synchronous call but response ignored
- Errors don't propagate to caller
- Failures logged for monitoring
- Response sent before consuming worker processes message
- Pattern: "fire-and-forget" / "best-effort delivery"

**Message Flow**:
1. notification-service receives REST POST
2. Validates CadastroNotificationRequest
3. Publishes to SQS queue (LocalStack)
4. Returns 200 OK immediately
5. notification-consumer polls queue independently
6. Consumer processes messages asynchronously
7. Email sent via Mailpit simulated SMTP

---

## Technical Patterns & Conventions

### Mandatory Layered Structure (All Domain Services)

**Directory Structure**:
```
src/main/java/com/cursopcv/[service-name]/
├── controller/              # HTTP endpoints
│   ├── MotoristaController.java
│   └── ...ControllerSwagger.java (interface for docs)
│
├── dto/
│   ├── request/
│   │   └── PessoaRequest.java    # Always records
│   └── response/
│       └── PessoaResponse.java   # Always records
│
├── mapper/
│   └── PessoaMapper.java         # Static methods only
│
├── service/
│   ├── MotoristaService.java     # Implementation
│   └── [Interface planned]       # Contract/interface
│
├── repository/
│   └── MotoristaRepository.java  # JpaRepository<E, ID>
│
├── model/
│   ├── Pessoa.java              # Domain entities
│   └── Motorista.java
│
├── exception/
│   ├── GlobalHandlerException.java
│   └── GlobalExceptionHandler.java
│
└── [ServiceName]ServiceApplication.java
```

**Strict Layer Dependencies**:
- **Controller** → calls only Mapper + Service
- **Mapper** → converts Entity ↔ DTO (static methods)
- **Service** → contains business logic, calls Repository + other Services
- **Repository** → database access only (JPA queries)
- **Model** → domain entities with JPA annotations (@Entity, @ManyToOne, etc.)

**NO shortcuts**: Controllers never call Repository directly, Services never use DTOs internally.

### DTOs as Records

**Modern Java 21 pattern** (immutable, no boilerplate):

```java
// Request DTO
public record PessoaRequest(
    @NotBlank String nome,
    @NotNull String cpf,
    @Email String email,
    @NotNull LocalDate dataNascimento,
    String numeroCNH,  // Optional: identifies Motorista
    String matricula,  // Optional: identifies Funcionario
    String sexo
) {}

// Response DTO
public record PessoaResponse(
    Integer id,
    String nome,
    String cpf,
    LocalDate dataNascimento,
    String matricula,
    String numeroCNH,
    String email,
    String sexo
) {}
```

**Rules**:
- Records are immutable (getters auto-generated)
- No setters
- Validation annotations on fields
- Null-safe by design (fields are explicit)

### Static Mapper Methods

**No instantiation**:
```java
public class PessoaMapper {
    // Static - called as PessoaMapper.toEntity(request)
    public static Pessoa toEntity(PessoaRequest request) {
        Pessoa pessoa;
        if (request.numeroCNH() != null) {
            pessoa = new Motorista();
            ((Motorista)pessoa).setNumeroCNH(request.numeroCNH());
        } else {
            pessoa = new Pessoa();
        }
        pessoa.setNome(request.nome());
        pessoa.setEmail(request.email());
        // ...
        return pessoa;
    }

    // Handles polymorphic mapping (Pessoa → Motorista/Funcionario)
    public static PessoaResponse toResponse(Pessoa entity) {
        String matricula = null, numeroCNH = null;
        if (entity instanceof Motorista motorista) {
            numeroCNH = motorista.getNumeroCNH();
        } else if (entity instanceof Funcionario func) {
            matricula = func.getMatricula();
        }
        return new PessoaResponse(entity.getId(), ..., matricula, numeroCNH, ...);
    }
}
```

**Advantages**:
- No mapper state or configuration
- Lightweight simple conversion
- Type-safe with generics-like patterns

### Dependency Injection via Constructor

**Explicit dependencies**:
```java
@Service
public class MotoristaService {
    private MotoristaRepository motoristaRepository;
    private PessoaRepository pessoaRepository;
    private PessoaMapper pessoaMapper;
    private NotificationClient notificationClient;

    public MotoristaService(
        MotoristaRepository motoristaRepository,
        PessoaRepository pessoaRepository,
        PessoaMapper pessoaMapper,
        NotificationClient notificationClient
    ) {
        this.motoristaRepository = motoristaRepository;
        this.pessoaRepository = pessoaRepository;
        this.pessoaMapper = pessoaMapper;
        this.notificationClient = notificationClient;
    }
    
    // Methods use injected fields
}
```

**Benefits**:
- Testable: easy to mock dependencies
- Clear what service needs
- Immutable after construction
- Spring auto-wires if only one constructor

### Entity Validation (JPA + Jakarta Validation)

**Annotations**:
```java
@Entity
public class Pessoa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    private String nome;

    @NotNull
    @Email
    private String email;

    @CPF  // Custom Brazilian CPF validator
    private String cpf;

    @NotNull
    @Temporal(TemporalType.DATE)
    private Date dataNascimento;

    @Enumerated(EnumType.STRING)
    private Sexo sexo;
}
```

**Validation Timing**:
- DTOs: validated at controller entry (@Valid)
- Entities: validated before @PrePersist
- If invalid: MethodArgumentNotValidException → 400 BAD REQUEST

---

## Database Design

### Schema Overview

**Single Shared Database** (PostgreSQL, intentional for training):
- All services read/write same database
- Later: migrate toward event sourcing or API-based sync (production pattern)
- Current: direct JDBC access via JPA/Hibernate

### Table Structure

**PESSOA Hierarchy** (JOINED inheritance):
```sql
-- Base table
CREATE TABLE pessoa (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    data_nascimento DATE NOT NULL,
    sexo VARCHAR(10),
    pessoa_type VARCHAR(31)  -- Discriminator for JPA JOINED
);

-- Subclass table for Motorista
CREATE TABLE motorista (
    id INT PRIMARY KEY,
    numero_cnh VARCHAR(30),
    FOREIGN KEY (id) REFERENCES pessoa(id)
);

-- Subclass table for Funcionario
CREATE TABLE funcionario (
    id INT PRIMARY KEY,
    matricula VARCHAR(30),
    FOREIGN KEY (id) REFERENCES pessoa(id)
);
```

**Vehicle Tables**:
```sql
CREATE TABLE fabricante (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL
);

CREATE TABLE modelo_carro (
    id SERIAL PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL,
    categoria VARCHAR(50) NOT NULL,  -- Enum: HATCH_COMPACTO, etc.
    fabricante_id INT,
    FOREIGN KEY (fabricante_id) REFERENCES fabricante(id)
);

CREATE TABLE carro (
    id SERIAL PRIMARY KEY,
    placa VARCHAR(20) UNIQUE NOT NULL,
    chassi VARCHAR(17) UNIQUE NOT NULL,
    cor VARCHAR(100),
    valor_diaria DECIMAL(10, 2),
    imagem_url TEXT,                    -- ✅ NEW
    disponivel BOOLEAN DEFAULT TRUE,    -- ✅ NEW
    modelo_id INT,
    FOREIGN KEY (modelo_id) REFERENCES modelo_carro(id)
);

CREATE TABLE acessorio (
    id SERIAL PRIMARY KEY,
    descricao VARCHAR(255) NOT NULL
);

CREATE TABLE carro_acessorio (
    carro_id INT,
    acessorio_id INT,
    PRIMARY KEY (carro_id, acessorio_id),
    FOREIGN KEY (carro_id) REFERENCES carro(id),
    FOREIGN KEY (acessorio_id) REFERENCES acessorio(id)
);
```

**Rental Tables**:
```sql
CREATE TABLE apolice_seguro (
    id SERIAL PRIMARY KEY,
    valor_franquia DECIMAL(10, 2),
    protecao_terceiro BOOLEAN,
    protecao_causas_naturais BOOLEAN,
    protecao_roubo BOOLEAN
);

CREATE TABLE aluguel (
    id SERIAL PRIMARY KEY,
    data_pedido TIMESTAMP,
    data_entrega DATE,
    data_devolucao DATE,
    valor_total DECIMAL(15, 2),
    apolice_id INT,
    FOREIGN KEY (apolice_id) REFERENCES apolice_seguro(id)
);
```

### Unique Constraints

- `pessoa.email` - UNIQUE (required for login identity)
- `pessoa.cpf` - UNIQUE (national ID)
- `carro.placa` - UNIQUE (license plate)
- `carro.chassi` - UNIQUE (VIN)

---

## Error Handling

### Global Exception Architecture

**Exception Hierarchy**:
```
Exception
├── GlobalHandlerException (custom domain exception)
├── EntityNotFoundException (404 - not found)
├── EntityConflictException (409 - duplicate)
├── EntityNotNullException (400 - validation)
├── PlacaInvalidFormatException (400 - format)
├── CategoriaInvalidException (400 - invalid enum)
└── Jakarta ValidationException (400 - constraint violations)
```

### Exception Handling Mechanism

**Global Exception Handler** (Spring ControllerAdvice):
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(EntityConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(EntityConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(ex.getMessage()));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
        MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult()
            .getAllErrors()
            .stream()
            .map(ObjectError::getDefaultMessage)
            .collect(joining(", "));
        return ResponseEntity.badRequest()
            .body(new ErrorResponse(errors));
    }
}
```

### Response Codes

| Status | Condition | Example |
|--------|-----------|---------|
| 200 OK | Success | GET /motoristas/1 → returns driver |
| 201 CREATED | Resource created | POST /motoristas → new driver saved |
| 400 BAD REQUEST | Validation failed | `email` field invalid format |
| 404 NOT FOUND | Resource not found | GET /motoristas/999 (doesn't exist) |
| 409 CONFLICT | Duplicate email/plate | POST /motoristas with existing email |
| 500 INTERNAL SERVER ERROR | Unexpected error | Database connection failure |

### Failure Patterns

**Notification failures are logged, not thrown**:
```java
try {
    notificationClient.notificarCadastro(request);
} catch (WebClientRequestException | TimeoutException e) {
    logger.warn("Falha ao notificar cadastro de motorista [{}]: {}", 
        motorista.getNome(), e.getMessage());
    // Continue - registration already succeeded
}
```

**Service unavailability**:
- If pessoa-service unreachable: aluguel-service throws exception (blocks rental)
- Future: circuit breaker would retry with exponential backoff

---

## Deployment & Infrastructure

### Docker Stack

**Services** (docker-compose.yml):
```yaml
services:
  postgres:
    image: postgres:16
    ports:
      - "5433:5432"
    environment:
      POSTGRES_PASSWORD: password
    volumes:
      - postgres_data:/var/lib/postgresql/data

  localstack:
    image: localstack/localstack
    ports:
      - "4566:4566"  # AWS API endpoint
    environment:
      SERVICES: sqs
      DEFAULT_REGION: us-east-1
    volumes:
      - ./localstack:/var/lib/localstack
      - ./init-aws.sh:/etc/localstack/init/ready.d/init-aws.sh

  hogmail:
    image: mailpit/mailpit
    ports:
      - "1025:1025"  # SMTP receiving
      - "8025:8025"  # Web UI
```

**init-aws.sh** (creates SQS queue on LocalStack startup):
```bash
#!/bin/bash
awslocal sqs create-queue --queue-name locadora-notifications
echo "Queue created"
```

### Startup Sequence

```bash
# 1. Start infrastructure
cd docker && docker-compose up -d
# Wait 10-15 seconds for LocalStack to initialize

# 2. Build all services
cd api && mvnw clean install -DskipTests

# 3. Start each service (separate terminals, or systemctl in production)
cd pessoa-service && mvnw spring-boot:run        # Port 8081
cd carro-service && mvnw spring-boot:run         # Port 8082
cd aluguel-service && mvnw spring-boot:run       # Port 8083
cd notification-service && mvnw spring-boot:run  # Port 8095
cd notification-consumer-service && mvnw spring-boot:run  # Port 8096
```

### Configuration Files

**application.properties** (each service):
```properties
# Server
server.port=8081
spring.application.name=pessoa-service

# Database (shared instance)
spring.datasource.url=jdbc:postgresql://localhost:5433/locadora_db
spring.datasource.username=postgres
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update

# Notification (pessoa-service)
notification.service.url=http://localhost:8095

# AWS/SQS (notification-service)
aws.sqs.queue-url=http://localhost:4566/000000000000/locadora-notifications
aws.region=us-east-1
```

### Monitoring & Logs

**Access Points**:
- Swagger UI: `http://localhost:8081/swagger-ui.html` (pessoa-service)
- Mailpit Web: `http://localhost:8025` (email inbox)
- LocalStack Logs: `docker logs localstack`
- Service Logs: Console output or `docker logs <container>`

---

## Key Decisions & Trade-offs

### Why Shared Database?

**Intended for training** (not production):
- Simplifies setup and testing
- Teaches multi-service queries without federation complexity
- Real projects: each service owns database, sync via events/APIs

### Why REST not gRPC?

- Simpler to learn and debug
- Easier integration testing
- Lower adoption barrier for team
- Future: evaluate gRPC for performance-critical paths

### Why SQS not Kafka?

- Educational value: AWS SQS is industry-standard
- LocalStack provides local mock
- Simpler mental model: queue pulled by consumer
- Kafka adds operational complexity

### Why Records for DTOs?

- Modern Java 21 feature
- Immutability prevents bugs
- Less boilerplate than classes
- Cleaner API contracts

---

## Future Roadmap

1. **Rental Completion**: Implement complete aluguel-service orchestration
2. **API Gateway**: Make mandatory for all external traffic
3. **Authentication**: Add JWT-based auth (Spring Security)
4. **Circuit Breaker**: Resilience4j for cross-service calls
5. **Event Sourcing**: Move from shared DB to event-driven sync
6. **Caching**: Redis layer for vehicle availability
7. **Containerization**: Docker images for each service
8. **Deployment**: Kubernetes manifests
9. **Observability**: Distributed tracing (Spring Cloud Sleuth)
10. **Frontend**: Web UI for registration and rental workflows

---

**Last Updated**: 2026-05-19  
**Audience**: Developers, QA, Project Managers  
**Related Docs**: AGENTS.md (AI workflows), docs/adr/ (decision records)

