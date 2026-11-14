# Gestão de Compras Públicas – Back-end

API em Java + Spring Boot que importa os contratos do governo federal publicados no Portal da Transparência, trata as inconsistências do arquivo original, grava os registros válidos em um PostgreSQL no Neon e oferece CRUD, filtros combináveis e dados para gráficos.

Projeto da avaliação P3 da disciplina Programação Avançada Orientada a Objetos – FATEC Zona Leste.

O front-end (React) fica no repositório irmão `gestao-compras-publicas-frontend`.

## Fonte dos dados

| Item | Informação |
|---|---|
| Fonte | Portal da Transparência do Governo Federal – CGU |
| Conjunto | Contratos (Compras) |
| Download | https://portaldatransparencia.gov.br/download-de-dados/compras |
| Dicionário de dados | https://portaldatransparencia.gov.br/dicionario-de-dados/contratos |
| Arquivo usado | `202606_Compras.zip` (junho de 2026, 1.749 contratos) |

O sistema recebe o **ZIP original do portal, sem nenhuma alteração**. Ele usa o `*_Compras.csv` (obrigatório) e o `*_ItemCompra.csv` (opcional, usado para descobrir a modalidade) e ignora os outros arquivos do ZIP.

## Tecnologias

- Java 17 (compila com JDK 17 ou mais recente)
- Spring Boot 4.1.2 (Spring Web MVC, Spring Data JPA, Validation)
- PostgreSQL no Neon
- Apache Commons CSV
- Maven (com Maven Wrapper, não precisa instalar)

## Configuração do banco

As credenciais não ficam no repositório. Copie o arquivo de exemplo e preencha com os dados do seu projeto no Neon:

```
cp .env.example .env
```

| Variável | Conteúdo |
|---|---|
| `DB_URL` | URL JDBC do Neon: `jdbc:postgresql://HOST.neon.tech/BANCO?sslmode=require&channelBinding=require&reWriteBatchedInserts=true` |
| `DB_USER` | usuário do banco |
| `DB_PASSWORD` | senha do banco |
| `SERVER_PORT` | porta da aplicação (padrão 8080) |

O Spring Boot lê o `.env` sozinho (`spring.config.import` no `application.properties`). As mesmas variáveis também podem vir do sistema operacional. O `.env` está no `.gitignore`.

As tabelas são criadas automaticamente na primeira execução (`ddl-auto=update`).

## Como rodar

### Só o back (desenvolvimento)

```
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS, use `./mvnw spring-boot:run`. A API fica em `http://localhost:8080/api` (ou na porta definida em `SERVER_PORT`).

Para o front em desenvolvimento, rode também `npm run dev` no repositório do front e acesse `http://localhost:5173`. O Vite repassa as chamadas `/api` para este back.

### Aplicação completa com um comando (build único)

No repositório do front:

```
npm install
npm run build:backend
```

Isso gera o front em `src/main/resources/static` deste repositório (pasta fora do Git). Depois, aqui:

```
.\mvnw.cmd spring-boot:run
```

e acesse `http://localhost:8080`. O `PaginaController` devolve o `index.html` para as rotas do React, então recarregar a página em qualquer tela funciona.

### VS Code

O repositório recomenda as extensões **Extension Pack for Java** e **Spring Boot Extension Pack** (`.vscode/extensions.json`). Com elas, a aplicação pode ser iniciada e depurada pelo painel Spring Boot Dashboard ou pelo botão Run/Debug na classe `GestaoComprasPublicasApplication`.

## Endpoints

| Método | Endereço | O que faz |
|---|---|---|
| GET | `/api/contratacoes` | Lista paginada com filtros opcionais e combináveis: `orgaoId`, `dataInicio`, `dataFim` (aaaa-mm-dd, data de assinatura), `modalidade`, `valorMinimo`, `valorMaximo` (valor final), `fornecedor` (parte do nome). Paginação com `page`, `size` e `sort` |
| GET | `/api/contratacoes/{id}` | Busca uma contratação |
| POST | `/api/contratacoes` | Cadastra. Órgão e fornecedor: `orgaoId` ou `novoOrgao`, `fornecedorId` ou `novoFornecedor` |
| PUT | `/api/contratacoes/{id}` | Atualiza |
| DELETE | `/api/contratacoes/{id}` | Exclui |
| GET | `/api/orgaos` | Lista os órgãos |
| GET | `/api/fornecedores?busca=` | Busca fornecedores por nome ou documento (até 20) |
| GET | `/api/modalidades` | Lista as modalidades |
| POST | `/api/importacoes` | Importa o ZIP (multipart, campo `arquivo`, até 50 MB) |
| GET | `/api/graficos/valor-por-orgao` | Valor final somado dos 10 órgãos com maior valor |
| GET | `/api/graficos/quantidade-por-modalidade` | Quantidade de contratações por modalidade |
| GET | `/api/graficos/evolucao-mensal` | Valor final por mês de assinatura, nos últimos 12 meses com dados |

Os erros seguem um formato único, com mensagens em português:

```json
{ "status": 400, "mensagem": "Dados inválidos. Corrija os campos indicados.", "campos": [{ "campo": "valorFinal", "mensagem": "Informe o valor final." }] }
```

Códigos usados: 400 (validação, regra de negócio, arquivo inválido), 404 (não encontrado), 405 (método não aceito), 409 (duplicado), 413 (arquivo acima de 50 MB), 415 (formato de envio não suportado) e 500 (erro inesperado).

## Organização do código

```
src/main/java/br/fatec/compraspublicas/
├── model/        Orgao, Fornecedor, Contratacao e os enums (Modalidade, TipoFornecedor, origens)
├── repository/   Spring Data JPA, filtros com Specification e consultas dos gráficos
├── service/      CRUD, filtros, gráficos e orquestração da importação
├── importacao/   leitura do ZIP e do CSV, conversão, validação, modalidade, duplicados e gravação em lotes
├── controller/   endpoints REST, sem regra de negócio
├── dto/          records de entrada e saída
└── exception/    exceções de negócio e o tratador global de erros
```

## Regras da importação

- **Arquivo inválido** (não é ZIP, ZIP corrompido, sem `*_Compras.csv`, CSV vazio ou sem as colunas obrigatórias): o envio inteiro é recusado com uma mensagem, antes de processar qualquer linha.
- **Encoding** Windows-1252, como vem do portal (UTF-8 com BOM também é aceito).
- **Valores** convertidos para `BigDecimal`. Zero é aceito (contratos não onerosos); negativo, vazio ou texto rejeitam a linha.
- **Datas** com validação estrita (31/02 é rejeitado). Fim de vigência antes do início rejeita a linha.
- **Documento do contratado**: 11 dígitos PF, 14 dígitos PJ, prefixo "EX" estrangeiro, "-11" sigiloso. Qualquer outro formato rejeita a linha.
- **Modalidade em cascata**: coluna Modalidade Compra → Modalidade Compra Licitação → dígitos 7 e 8 do Código Item Compra (arquivo de itens) → Não informada. A origem fica gravada junto.
- **Duplicados** (só entre linhas válidas): linha idêntica a outra é rejeitada; entre versões do mesmo contrato (número + UG), fica a de publicação no DOU mais recente; contrato que já está no banco é rejeitado como "já cadastrado".
- **Gravação** em lotes de 50, em uma única transação, com órgãos e fornecedores em memória durante a importação.

Resultado com o ZIP de junho de 2026: **1.749 linhas lidas, 1.727 importadas e 22 rejeitadas** (12 substituídas por publicação mais recente, 9 duplicadas no arquivo e 1 documento inválido), 151 órgãos, em cerca de 2,5 segundos. Reimportar o mesmo arquivo resulta em 0 importadas.

## Testes

O projeto não tem testes automatizados. As regras são verificadas pela importação real do ZIP original, cujo resultado esperado está acima, e pelo uso das telas. O tratamento de arquivo inválido pode ser visto enviando qualquer arquivo que não seja o ZIP do portal, por exemplo um PDF.
