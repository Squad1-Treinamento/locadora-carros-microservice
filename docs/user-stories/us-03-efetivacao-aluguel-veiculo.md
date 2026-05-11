# US-03 – Efetivação do Aluguel de Veículo

## Descrição
**Como** um cliente cadastrado que selecionou um veículo para alugar  
**Quero** poder efetivar o aluguel do veículo selecionado  
**Para** confirmar minha reserva e iniciar o processo de aluguel

## Regras de Negócio
- Um veículo não pode ser alugado por mais de um cliente no mesmo período.
- O aluguel só pode ser efetivado após concordância com os termos.
- O pagamento é obrigatório para a confirmação do aluguel.
- Após a confirmação, o veículo deve ter seu status atualizado e o período bloqueado.

## Critérios de Aceite
- [ ] O cliente deve poder confirmar a reserva a partir do carrinho de aluguel.
- [ ] Deve ser exibida uma página de resumo da reserva.
- [ ] O resumo deve conter veículo, datas, valor total e termos de aluguel.
- [ ] O cliente deve aceitar os termos antes de prosseguir.
- [ ] O sistema deve permitir a escolha de um método de pagamento (simulado).
- [ ] O cliente deve confirmar o pagamento para finalizar o aluguel.
- [ ] Após a confirmação, o sistema deve exibir uma confirmação completa do aluguel.
- [ ] O veículo deve ser marcado como reservado e bloquear as datas no calendário.
- [ ] O cliente deve poder consultar seus aluguéis futuros.

## Observações
- O processo de pagamento é apenas simulado, sem integração real.
