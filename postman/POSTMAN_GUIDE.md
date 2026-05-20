# 📬 Guia de Teste - Collection Postman

## ✅ Como Importar a Collection

### Passo 1: Abrir o Postman
1. Abra a aplicação **Postman**
2. Clique em **File** → **Import** (ou use Ctrl+O)

### Passo 2: Selecionar o Arquivo
1. Clique em **Upload Files**
2. Navegue até: `C:\Users\patricia.borges\Documents\atividades\locadora-carros-microservice\`
3. Selecione o arquivo: `Locadora-Carros-Microservices.postman_collection.json`
4. Clique em **Import**

### Passo 3: Collection Importada ✨
A collection aparecerá na esquerda com todos os endpoints organizados!

---

## 🚀 Fluxo de Teste Recomendado

### **Fase 1: Preparação de Dados**

Execute nesta ordem para criar a base de dados:

1. **CARRO-SERVICE → Fabricante - Criar**
   - Cria a fabricante "Toyota"
   - Salva automaticamente o ID para uso posterior

2. **CARRO-SERVICE → Modelo Carro - Criar**
   - Cria modelo "Corolla"
   - Usa o fabricanteId da etapa anterior

3. **CARRO-SERVICE → Acessório - Criar**
   - Cria acessório "Ar Condicionado"
   - Salva o ID automaticamente

4. **CARRO-SERVICE → Carro - Cadastrar**
   - Cadastra veículo completo com modelo e acessório
   - Importante: Salva o carroId para os testes de aluguel

5. **PESSOA-SERVICE → Motorista - Cadastrar**
   - Cadastra motorista (driver)
   - Importante: Salva o motoristaId

---

### **Fase 2: Consultas e Filtros**

6. **CARRO-SERVICE → Carro - Listar todos**
   - Verifica todos os carros cadastrados

7. **CARRO-SERVICE → Carro - Listar Disponíveis**
   - Filtra apenas carros disponíveis (disponivel = true)

8. **CARRO-SERVICE → Carro - Filtrar por Categoria**
   - Filtra carros por categoria (SEDAN_MEDIO)

9. **PESSOA-SERVICE → Motorista - Buscar por ID**
   - Consulta dados do motorista

---

### **Fase 3: Fluxo Principal - Aluguel**

10. **ALUGUEL-SERVICE → Aluguel - Solicitar**
    - Cria um aluguel (PENDENTE status)
    - Chamadas internas automáticas para pessoa-service e carro-service
    - Envia notificação via SQS
    - **IMPORTANTE:** Use os IDs reais de motorista e carro

11. **ALUGUEL-SERVICE → Aluguel - Listar por Motorista**
    - Lista todos os aluguéis do motorista

12. **ALUGUEL-SERVICE → Aluguel - Obter Resumo**
    - Mostra custos detalhados do aluguel (diária, dias, total)

13. **ALUGUEL-SERVICE → Aluguel - Efetuar Checkout**
    - Processa o pagamento
    - Status muda para CONFIRMADO

14. **ALUGUEL-SERVICE → Aluguel - Finalizar**
    - Marca aluguel como concluído (FINALIZADO)

---

### **Fase 4: Testes de Erro (Validação)**

Use a pasta **TESTES DE ERRO** para verificar o tratamento de erros:

- ❌ **Motorista - Buscar ID inválido**: Deve retornar 404
- ❌ **Carro - Cadastrar com placa inválida**: Deve retornar 400
- ❌ **Aluguel - Com motorista inexistente**: Deve retornar 400/404
- ❌ **Aluguel - Com datas inválidas**: Deve retornar 400

---

## 🔧 Variáveis Úteis

Alguns testes salvam IDs automaticamente em **variáveis globais**:

```
fabricanteId  → ID do fabricante criado
modeloId      → ID do modelo criado
acessorioId   → ID do acessório criado
carroId       → ID do carro criado
aluguelId     → ID do aluguel criado
```

Você pode usar essas variáveis nas URLs digitando `{{nomeVariavel}}`

---

## ⚙️ Configuração de Ambiente

### Precondições:
- ✅ PostgreSQL rodando na porta 5433
- ✅ LocalStack rodando na porta 4566
- ✅ Mailpit rodando na porta 1025

### Serviços em Execução:
```bash
# Terminal 1: pessoa-service
cd api/pessoa-service
mvnw spring-boot:run  # Porta 8081

# Terminal 2: carro-service
cd api/carro-service
mvnw spring-boot:run  # Porta 8082

# Terminal 3: aluguel-service
cd api/aluguel-service
mvnw spring-boot:run  # Porta 8083

# Terminal 4: notification-service
cd api/notification-service
mvnw spring-boot:run  # Porta 8095

# Terminal 5: notification-consumer-service
cd api/notification-consumer-service
mvnw spring-boot:run  # Porta 8096
```

---

## 📊 Estrutura da Collection

```
Locadora de Carros - Microserviços
├── PESSOA-SERVICE (8081)
│   ├── Motorista - Cadastrar (POST)
│   ├── Motorista - Buscar por ID (GET)
│   └── Motorista - Atualizar (PUT)
│
├── CARRO-SERVICE (8082)
│   ├── Fabricante - Criar (POST)
│   ├── Fabricante - Listar (GET)
│   ├── Modelo Carro - Criar (POST)
│   ├── Modelo Carro - Listar (GET)
│   ├── Acessório - Criar (POST)
│   ├── Acessório - Listar (GET)
│   ├── Carro - Cadastrar (POST)
│   ├── Carro - Listar todos (GET)
│   ├── Carro - Buscar por ID (GET)
│   ├── Carro - Listar Disponíveis (GET)
│   ├── Carro - Filtrar por Categoria (GET)
│   ├── Carro - Filtrar por Acessório (GET)
│   ├── Carro - Filtro Avançado (GET)
│   ├── Carro - Atualizar (PUT)
│   ├── Carro - Atualizar Disponibilidade (PATCH)
│   └── Carro - Deletar (DELETE)
│
├── ALUGUEL-SERVICE (8083)
│   ├── Aluguel - Solicitar (POST)
│   ├── Aluguel - Listar todos (GET)
│   ├── Aluguel - Buscar por ID (GET)
│   ├── Aluguel - Listar por Motorista (GET)
│   ├── Aluguel - Obter Resumo (GET)
│   ├── Aluguel - Efetuar Checkout (POST)
│   ├── Aluguel - Cancelar (PATCH)
│   └── Aluguel - Finalizar (PATCH)
│
└── TESTES DE ERRO
    ├── Motorista - Buscar ID inválido
    ├── Carro - Cadastrar com placa inválida
    ├── Aluguel - Com motorista inexistente
    └── Aluguel - Com dataDevolucao anterior a dataEntrega
```

---

## 📝 Exemplo de Uso

### 1. Criar um Motorista
```bash
POST http://localhost:8081/motoristas
{
  "nome": "João Silva",
  "cpf": "12345678900",
  "email": "joao.silva@example.com",
  "dataNascimento": "1990-01-15",
  "numeroCNH": "98765432100",
  "sexo": "M"
}

Response (201 CREATED):
{
  "id": 1,
  "nome": "João Silva",
  "cpf": "12345678900",
  "email": "joao.silva@example.com",
  "dataNascimento": "1990-01-15",
  "numeroCNH": "98765432100",
  "sexo": "M"
}
```

### 2. Solicitar um Aluguel
```bash
POST http://localhost:8083/alugueis
{
  "idMotorista": 1,
  "idCarro": 1,
  "dataEntrega": "2026-06-01",
  "dataDevolucao": "2026-06-08",
  "apoliceSeguro": {
    "valorFranquia": 500.00,
    "protecaoTerceiro": true,
    "protecaoCausasNaturais": true,
    "protecaoRoubo": true
  }
}

Response (201 CREATED):
{
  "id": 1,
  "idPessoa": 1,
  "idCarro": 1,
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

---

## 🧪 Testes Automáticos

Cada requisição tem testes automáticos que:
- ✅ Validam o status HTTP esperado
- ✅ Verificam a estrutura da resposta
- ✅ Salvam variáveis para uso em próximos testes

**Como visualizar os testes:**
1. Abra uma requisição
2. Clique na aba **Tests**
3. Veja os testes em JavaScript

---

## 🔍 Debugar e Monitorar

### Logs do Postman:
- Clique em **Console** (canto inferior esquerdo)
- Veja requisições e respostas em tempo real

### Swagger UI (Documentação Interativa):
- http://localhost:8081/swagger-ui.html (pessoa-service)
- http://localhost:8082/swagger-ui.html (carro-service)
- http://localhost:8083/swagger-ui.html (aluguel-service)

### Mailpit (Verificar Emails):
- http://localhost:8025
- Veja os emails de notificação do aluguel

---

## ⚠️ Troubleshooting

### Erro: "Connection refused"
- Verifique se todos os serviços estão rodando
- Confirme as portas: 8081, 8082, 8083

### Erro: "Motorista não encontrado"
- Certifique-se de criar o motorista antes do aluguel
- Use o ID correto retornado na criação

### Erro: "Placa duplicada"
- Cada carro precisa de uma placa única
- Mude a placa se testar múltiplas vezes

### Erro: "Database connection failed"
- Verifique se PostgreSQL está rodando
- Confirme credenciais em application.properties

---

## 📞 Suporte

Dúvidas sobre a API? Consulte:
- `/docs/PROJECT_ARCHITECTURE.md` - Documentação completa
- `/docs/adr/` - Decisões arquiteturais
- Swagger UI dos serviços

**Bom teste! 🚀**

