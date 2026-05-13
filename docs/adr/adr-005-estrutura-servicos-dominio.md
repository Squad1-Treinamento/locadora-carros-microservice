# ADR-005 – Estrutura padrão dos microserviços de domínio

## Status
Aceita

## Contexto
Foi necessário definir uma estrutura padrão para os microserviços de domínio com o objetivo de garantir consistência entre serviços, organização do código e separação clara de responsabilidades.

A arquitetura deveria evitar acoplamento entre camadas, garantir previsibilidade para o time e seguir um modelo amplamente adotado no mercado, sem introduzir complexidade excessiva.

## Decisão
Foi definida uma estrutura baseada em camadas obrigatórias para todos os microserviços de domínio:


### Estrutura de diretórios

```plaintext
src/main/java/com/cursopcv/<nome>-service/
├── controller/
│   ├── <nome>Controller.java
│   └── <nome>ControllerSwagger.java
│
├── dto/
│   ├── request/
│   │   └── <nome>RequestDTO.java
│   └── response/
│       └── <nome>ResponseDTO.java
│
├── mapper/
│   └── <nome>Mapper.java
│
├── exception/
│   ├── CustomException.java
│   └── GlobalExceptionHandler.java
│
├── model/
│   └── <nome>.java
│
├── repository/
│   └── <nome>Repository.java
│
└── service/
    ├── <nome>Service.java
    └── <nome>ServiceImpl.java
```

### controller
- Responsável por receber requisições HTTP.
- Não possui lógica de negócio.
- Trabalha exclusivamente com DTOs.
- Utiliza interfaces separadas para definição da documentação Swagger.

### dto (record)
- Utilizado para entrada (`request`) e saída (`response`) de dados.
- DTOs são sempre definidos como `record`.
- DTOs não expõem diretamente as entidades de domínio.

### mapper
- Responsável pela conversão entre DTOs e entidades.
- Implementado via métodos estáticos.
- Utilizado exclusivamente pelo controller.
- Garante que o controller não dependa diretamente do model.

### service
- Responsável por toda a lógica de negócio.
- Define um contrato via interface (`service interface`) e uma implementação (`service impl`).
- Opera exclusivamente com entidades de domínio (model).
- Não possui conhecimento sobre DTOs.

### repository
- Responsável pela persistência de dados.
- Utiliza JPA/Hibernate.

### model
- Representa as entidades de domínio.
- Utilizado apenas pelas camadas internas (service e repository).

### exception
- Contém o tratamento global de exceções (`GlobalExceptionHandler`).
- Pode incluir exceções customizadas para regras de negócio.



## Consequências
- Separação clara entre API (DTO) e domínio (Model).
- Redução de acoplamento entre camadas.
- Padronização obrigatória entre todos os microserviços.
- Aumento de quantidade de código devido à necessidade de mapper e interfaces.
- Maior previsibilidade e organização, facilitando manutenção e evolução.
- Arquitetura baseada em camadas (Layered Architecture), amplamente utilizada no mercado.