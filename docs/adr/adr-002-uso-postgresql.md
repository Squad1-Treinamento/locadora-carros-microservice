# ADR-002 – Uso do PostgreSQL como banco de dados

## Status
Aceita

## Contexto
A aplicação de locadora de veículos utilizará um banco de dados relacional desde o início do desenvolvimento.  
Embora o uso do PostgreSQL não seja uma exigência do projeto, todos os membros do time possuem acesso ao ambiente, e o banco foi utilizado como exemplo durante o treinamento.

Foram consideradas alternativas como MySQL e H2. O MySQL, apesar de conhecido pelo time, não agregaria novos aprendizados relevantes. O H2 foi descartado por operar majoritariamente em memória, não atendendo ao requisito de persistência real dos dados ao longo do ciclo da aplicação.

## Decisão
O banco de dados escolhido para o projeto é o **PostgreSQL**, que será utilizado desde o início até a conclusão do projeto.  
O banco será executado via **Docker**, facilitando o setup do ambiente e garantindo padronização entre os desenvolvedores.

O PostgreSQL será compartilhado entre os microserviços na fase inicial do projeto. Existe a possibilidade futura de separação lógica por esquemas, porém essa abordagem não faz parte do escopo atual.

O acesso aos dados será realizado prioritariamente via **JPA/Hibernate**, não sendo previsto o uso de SQL nativo, exceto em situações excepcionais onde não haja alternativa adequada. Não está previsto o uso de ferramentas de migration neste momento.

## Consequências
- Banco de dados próximo a cenários reais de produção.
- Aumento de complexidade em relação a bancos em memória, aceito pelo time.
- Padronização do ambiente via Docker.
- Acoplamento entre serviços devido ao banco compartilhado, considerado aceitável no contexto inicial do projeto.
- Base preparada para possíveis evoluções futuras sem necessidade de troca de tecnologia.