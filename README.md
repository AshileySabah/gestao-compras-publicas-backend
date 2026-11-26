# Gestão de Compras Públicas – Back-end

API em Java com Spring Boot para gestão e análise de compras públicas. O sistema importa o arquivo original de contratos do governo federal, publicado no Portal da Transparência, trata as inconsistências do arquivo, grava os registros válidos em um PostgreSQL hospedado no Neon e permite cadastrar, consultar, alterar, excluir, filtrar e visualizar os dados em gráficos.

Projeto da avaliação P3 da disciplina Programação Avançada Orientada a Objetos (FATEC).

A interface web (React) está em: https://github.com/AshileySabah/gestao-compras-publicas-frontend

## Funcionalidades

- **Importação** do ZIP original baixado do portal, sem nenhuma alteração. O resultado mostra as linhas lidas, importadas e rejeitadas, com o motivo de cada rejeição.
- **Tratamento de inconsistências**: valores e datas inválidos, campos obrigatórios vazios, linhas duplicadas, republicações, formatos de valor diferentes e arquivos inválidos.
- **CRUD** de contratações, com mensagens de sucesso e erro.
- **Cinco filtros combináveis**: órgão, período, modalidade, faixa de valores e fornecedor.
- **Três gráficos**: valor total por órgão, quantidade por modalidade e evolução mensal dos valores.

## Fonte dos dados

| Item                | Informação                                                             |
| ------------------- | ---------------------------------------------------------------------- |
| Fonte               | Portal da Transparência do Governo Federal (CGU) – Contratos (Compras) |
| Download            | https://portaldatransparencia.gov.br/download-de-dados/compras         |
| Dicionário de dados | https://portaldatransparencia.gov.br/dicionario-de-dados/contratos     |
| Arquivo usado       | `202606_Compras.zip` (junho de 2026, 1.749 contratos)                  |

O sistema recebe o ZIP do jeito que sai do portal. Dentro dele, usa o `*_Compras.csv` (obrigatório) e o `*_ItemCompra.csv` (opcional, usado para descobrir a modalidade) e ignora os demais. Os arquivos de dados não ficam no repositório.

## Como os dados são tratados

| Situação                                                                                     | O que o sistema faz                                                                     |
| -------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------- |
| Arquivo inválido (não é ZIP, sem o arquivo de compras, vazio ou com cabeçalho errado)        | Recusa o envio inteiro, com mensagem                                                    |
| Valor não numérico, vazio ou negativo; data impossível (ex.: 31/02); campo obrigatório vazio | Rejeita a linha e informa o motivo                                                      |
| Valor em outro formato (`800000,0000`, `1.234,56`, `1234.56`)                                | Converte para `BigDecimal`                                                              |
| Linha repetida, contrato republicado ou contrato já cadastrado                               | Mantém uma linha, a publicação mais recente ou o registro do banco, e rejeita as demais |
| Modalidade "Sem Informação"                                                                  | Descobre a modalidade pelo código do item no `*_ItemCompra.csv`                         |

Com o arquivo de junho, o resultado esperado é 1.749 linhas lidas, 1.727 importadas e 22 rejeitadas. As justificativas de cada regra e da modelagem estão no relatório técnico.

## Tecnologias

Java 17, Spring Boot 4.1.2 (Web, Data JPA, Validation), Maven (com Maven Wrapper), PostgreSQL no Neon e Apache Commons CSV.

## Configuração do banco

As credenciais não ficam no repositório. Copie o arquivo de exemplo e preencha com os dados do seu projeto no Neon:

```
cp .env.example .env
```

| Variável      | Conteúdo                                                                                                                     |
| ------------- | ---------------------------------------------------------------------------------------------------------------------------- |
| `DB_URL`      | URL JDBC do Neon: `jdbc:postgresql://HOST.neon.tech/BANCO?sslmode=require&channelBinding=require&reWriteBatchedInserts=true` |
| `DB_USER`     | usuário do banco                                                                                                             |
| `DB_PASSWORD` | senha do banco                                                                                                               |
| `SERVER_PORT` | porta da aplicação (padrão 8080)                                                                                             |

As tabelas são criadas automaticamente na primeira execução.

## Como executar

Pré-requisitos: JDK 17 ou mais recente para o back, Node.js 20.19.0 ou maior para o front e o `.env` de cada repositório criado a partir do `.env.example`. As variáveis do banco estão explicadas no README do back-end; no front, `API_URL` é o endereço do back.

Os dois repositórios têm o mesmo script `dev.sh`, executado na raiz do repositório:

| Caso                                                    | Comando        | Endereço                    |
| ------------------------------------------------------- | -------------- | --------------------------- |
| Back com os dados do banco                              | `./dev.sh -BD` | `http://localhost:8080/api` |
| Back zerado (apaga tabelas e dados e recria tudo vazio) | `./dev.sh -BZ` | `http://localhost:8080/api` |
| Front em desenvolvimento (com o back já rodando)        | `./dev.sh -F`  | `http://localhost:5173`     |
| Front e back juntos                                     | `./dev.sh -FB` | `http://localhost:8080`     |

Os comandos são para o Git Bash, Linux ou macOS. No PowerShell ou no cmd, use `.\dev.cmd` com a mesma flag (ex.: `.\dev.cmd -BZ`).

Para subir só o repositório em que você está (`-BD` e `-BZ` no back, `-F` no front), não é preciso ter o outro clonado. Nos demais casos, inclusive `-FB`, os dois repositórios precisam estar clonados na mesma pasta, e o comando pode ser executado em qualquer um deles.

## Como usar

1. Baixe um ZIP mensal de Contratos no Portal da Transparência (por exemplo, `202606_Compras.zip`).
2. Na tela **Importação**, envie o ZIP e confira lidas, importadas, rejeitadas e os motivos.
3. Na tela **Contratações**, consulte, filtre, cadastre, edite ou exclua contratações.
4. Na tela **Dashboard**, veja os três gráficos.

## Endpoints principais

| Método             | Endereço                                                                          | O que faz                                                                                                                 |
| ------------------ | --------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------- |
| POST               | `/api/importacoes`                                                                | Importa o ZIP (multipart, campo `arquivo`, até 50 MB)                                                                     |
| GET                | `/api/contratacoes`                                                               | Lista com filtros opcionais: `orgaoId`, `dataInicio`, `dataFim`, `modalidade`, `valorMinimo`, `valorMaximo`, `fornecedor` |
| POST               | `/api/contratacoes`                                                               | Cadastra uma contratação                                                                                                  |
| GET / PUT / DELETE | `/api/contratacoes/{id}`                                                          | Consulta, atualiza e exclui uma contratação                                                                               |
| GET                | `/api/orgaos`, `/api/fornecedores?busca=`, `/api/modalidades`                     | Listas para os campos de seleção                                                                                          |
| GET                | `/api/graficos/valor-por-orgao`, `/quantidade-por-modalidade`, `/evolucao-mensal` | Dados dos três gráficos                                                                                                   |

## Organização do código

```
src/main/java/br/fatec/compraspublicas/
├── model/        entidades (Orgao, Fornecedor, Contratacao) e enums
├── repository/   acesso ao banco, filtros e consultas dos gráficos
├── service/      regras do CRUD, filtros, gráficos e importação
├── importacao/   cada etapa da importação em uma classe
├── controller/   endpoints REST
├── dto/          records de entrada e saída
└── exception/    exceções e tratamento global de erros
```
