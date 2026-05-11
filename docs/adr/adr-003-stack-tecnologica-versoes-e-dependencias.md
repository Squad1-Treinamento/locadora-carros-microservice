# ADR-003 – Stack tecnológica, versões e dependências

## Status
Aceita

## Contexto
Para garantir padronização, previsibilidade e alinhamento com o conteúdo do treinamento, foi necessário definir uma stack tecnológica única para todos os microserviços do projeto.

As decisões levaram em conta versões estáveis, amplamente adotadas no mercado e já utilizadas pelo time, evitando mudanças recentes que poderiam introduzir instabilidade ou curva de aprendizado desnecessária no início do projeto.

## Decisão
A stack tecnológica definida para a aplicação é:

- **Linguagem:** Java  
- **Versão:** Java 21 (LTS)
- **Framework:** Spring Boot 4.0.6
- **Gerenciador de dependências:** Maven
- **Padrão de GroupId:** `com.cursopcv`
- **ArtifactId:** `<nome>-service` (exceto api-gateway)
- **Configuração:** `application.properties`

As dependências disponíveis para os serviços são:

- Spring Web
- Spring Reactive Web
- Spring Data JPA
- PostgreSQL Driver
- Bean Validation
- Swagger
- Lombok
- Spring Security
- JWT

Nem todas as dependências serão utilizadas por todos os microserviços. Cada serviço deve incluir apenas as dependências necessárias ao seu escopo funcional.

## Consequências
- Padronização completa entre microserviços.
- Redução de divergências técnicas entre os membros do time.
- Adoção de tecnologias amplamente utilizadas no mercado.
- Facilidade de manutenção e evolução inicial do projeto.
- Possibilidade de revisão futura da stack, caso necessário, sem impacto imediato no desenvolvimento inicial.