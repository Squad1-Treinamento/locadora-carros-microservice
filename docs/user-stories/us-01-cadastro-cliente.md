# US-01 – Cadastro de Cliente

## Descrição
**Como** um cliente em potencial  
**Quero** poder me cadastrar na locadora de automóveis  
**Para** ter acesso aos serviços e alugar veículos

## Regras de Negócio
- O cliente deve ser identificado de forma única pelo e-mail.
- Não deve ser permitido o cadastro de clientes com e-mail já existente.
- Os dados cadastrais básicos do cliente são obrigatórios para utilização do sistema.

## Critérios de Aceite
- [ ] Deve haver um formulário de cadastro na página inicial.
- [ ] O formulário deve solicitar: nome completo, data de nascimento, CPF, número da CNH e e-mail.
- [ ] O sistema deve validar a unicidade do e-mail.
- [ ] Após cadastro bem-sucedido, o cliente deve receber uma confirmação na tela.
- [ ] O cliente deve ser redirecionado para a página inicial após o cadastro.

## Observações
- O processo de confirmação por e-mail pode ser simulado.