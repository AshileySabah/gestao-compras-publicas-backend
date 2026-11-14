# Gestão de Compras Públicas – Back-end

Sistema web para gestão e análise de compras públicas. Ele importa o arquivo original de contratos do governo federal publicado no Portal da Transparência, trata as inconsistências desse arquivo, grava os registros válidos em um banco PostgreSQL hospedado no Neon e permite cadastrar, consultar, alterar, excluir, filtrar e visualizar os dados em gráficos.

Este repositório é a API, feita em Java com Spring Boot. O projeto é a avaliação P3 da disciplina Programação Avançada Orientada a Objetos da FATEC Zona Leste.

## Repositório do front-end

A interface web (React) está em: https://github.com/AshileySabah/gestao-compras-publicas-frontend

## Funcionalidades

- **Importação do arquivo original** da fonte oficial: o ZIP baixado do portal é enviado sem nenhuma alteração, todas as linhas são processadas, e o resultado mostra linhas lidas, importadas e rejeitadas, o tempo, as rejeições por motivo e cada linha rejeitada com o motivo.
- **Tratamento de inconsistências**: dados inválidos, ausentes, duplicados e incompatíveis são tratados linha a linha, e arquivos inválidos são recusados com uma mensagem clara.
- **CRUD completo** de contratações, gravado no Neon, com mensagens de sucesso e erro.
- **Cinco filtros combináveis**: órgão, período, modalidade, faixa de valores e fornecedor.
- **Três gráficos** com dados do banco: valor total por órgão, quantidade por modalidade e evolução mensal dos valores.

---

## Fonte dos dados

| Item | Informação |
|---|---|
| Fonte | Portal da Transparência do Governo Federal – Controladoria-Geral da União (CGU) |
| Conjunto | Contratos (Compras): contratos firmados registrados no Compras.gov.br |
| Página de download | https://portaldatransparencia.gov.br/download-de-dados/compras |
| Dicionário de dados | https://portaldatransparencia.gov.br/dicionario-de-dados/contratos |
| Arquivo usado | `202606_Compras.zip` |
| Período | Junho de 2026 (o arquivo mensal é organizado pela data de publicação do contrato no Diário Oficial da União) |

O ZIP contém quatro arquivos: `202606_Compras.csv`, `202606_ItemCompra.csv`, `202606_TermoAditivo.csv` e `202606_Apostilamento.csv`. O sistema recebe o **ZIP original**, abre em memória, usa o `*_Compras.csv` (obrigatório) e o `*_ItemCompra.csv` (opcional) e ignora os demais. Os arquivos de dados não ficam guardados no repositório.

### Por que essa fonte

Entre as fontes sugeridas (Portal da Transparência – Licitações e Contratos, Compras.gov.br, PNCP e Prefeitura de Sobral), a base de Contratos do Portal da Transparência reúne em uma única linha quase todas as informações necessárias para os cinco filtros e os três gráficos: órgão, data, modalidade, fornecedor e valor. Na base de Licitações, por exemplo, o fornecedor fica em um arquivo separado de participantes. O PNCP é voltado principalmente para consulta por API, e o trabalho pede importação de arquivo.

### Por que junho de 2026

O arquivo de julho de 2026 foi testado primeiro e tinha apenas 18 registros, abaixo do mínimo de 800 pedido. O arquivo de junho tem 1.749 contratos.

---

## Como os dados chegam

### Formato dos arquivos

- Encoding **Windows-1252** (não é UTF-8; lidos como UTF-8, os acentos aparecem quebrados).
- Separador **ponto e vírgula** (`;`), com todos os campos entre aspas duplas.
- Quebra de linha no padrão Windows (CRLF).
- Valores monetários com vírgula decimal e quatro casas: `"800000,0000"`.
- Datas no formato `dd/MM/yyyy`.

### Colunas de `*_Compras.csv` (24 colunas)

| Coluna | Uso no sistema |
|---|---|
| Número do Contrato | Usada. Parte da chave que identifica o contrato |
| Objeto | Usada. O prefixo "Objeto: " que vem da fonte é removido |
| Fundamento Legal | Não usada. Vazia em todas as linhas |
| Modalidade Compra | Usada. Primeira opção para a modalidade |
| Situação Contrato | Não usada. "Não se aplica" em todas as linhas |
| Código Órgão Superior / Nome Órgão Superior | Usadas, na entidade Orgao |
| Código Órgão / Nome Órgão | Usadas, na entidade Orgao |
| Código UG / Nome UG | Usadas. Código UG é parte da chave do contrato |
| Data Assinatura Contrato | Usada. Data de referência para filtros e gráficos |
| Data Publicação DOU | Usada. Decide qual linha vale quando o contrato aparece republicado |
| Data Início Vigência / Data Fim Vigência | Usadas |
| Código Contratado / Nome Contratado | Usadas, na entidade Fornecedor |
| Valor Inicial Compra | Usada |
| Valor Final Compra | Usada. Valor de referência para filtros e gráficos |
| Número Licitação | Usada. O valor "-3" vira nulo |
| Código UG Licitação / Nome UG Licitação | Não usadas. Vazias em quase todas as linhas |
| Código Modalidade Compra Licitação / Modalidade Compra Licitação | Usadas só como segunda opção para a modalidade |

O sistema aceita o arquivo com todas as 24 colunas, mesmo usando apenas parte delas.

### Colunas de `*_ItemCompra.csv` (10 colunas)

`Código Órgão; Nome Órgão; Código UG; Nome UG; Número Contrato; Código Item Compra; Descrição Item Compra; Descrição Complementar Item Compra; Quantidade Item; Valor Item`

Esse arquivo é usado apenas para descobrir a modalidade dos contratos (ver "Modalidade" nas regras de tratamento). Os itens não são gravados no banco.

### O que a análise do arquivo de junho mostrou

- 1.749 contratos, todos com as 24 colunas.
- 151 órgãos distintos pelo código. Pelo nome são só 116, porque a fonte corta os nomes e órgãos diferentes acabam com o mesmo nome.
- Todas as datas de assinatura são válidas. A maior parte se concentra entre fevereiro e junho de 2026.
- Modalidade "Sem Informação" em 1.738 das 1.749 linhas. As outras 11 trazem a modalidade, e 5 delas escrevem "Pregão - Registro de Preço".
- 9 linhas totalmente idênticas repetidas.
- 9 contratos que aparecem mais de uma vez com a mesma chave, mas com alguma informação diferente, somando 12 linhas de publicações anteriores.
- 19 contratos com valor zero.
- 601 contratados pessoa física (CPF), 1.122 pessoa jurídica (CNPJ), 22 sigilosos, 3 estrangeiros e 1 com documento inválido.
- "Número Licitação" igual a "-3" em cerca de 1.330 linhas.
- Alguns nomes de órgão chegam cortados pela própria fonte (ex.: "Ministério da Integração e do Desenvolvime").
- Nenhuma data inválida, nenhum fim de vigência anterior ao início, nenhum valor negativo ou não numérico e nenhum campo obrigatório vazio. O sistema trata esses casos mesmo assim, porque outros meses da mesma fonte podem trazê-los.

**Resultado esperado da importação do ZIP de junho:** 1.749 linhas lidas, **1.727 importadas** e **22 rejeitadas**: 9 duplicadas no arquivo, 12 substituídas por publicação mais recente e 1 com documento inválido.

---

## Modelagem

### Entidades

- **Orgao**: código do órgão (único), nome, código e nome do órgão superior.
- **Fornecedor**: documento (único), nome e tipo (PF, PJ, ESTRANGEIRO, SIGILOSO).
- **Contratacao**: número do contrato, código e nome da UG, objeto, modalidade, origem da modalidade, data de assinatura, data de publicação no DOU, datas de início e fim da vigência, valor inicial, valor final, número da licitação, origem do registro (IMPORTACAO ou CADASTRO_MANUAL), e relacionamento muitos-para-um com Orgao e com Fornecedor.

### Por que normalizar Orgao e Fornecedor

No arquivo original, o nome do órgão e o nome do fornecedor se repetem em centenas de linhas. Separar em tabelas evita essa repetição, deixa o banco mais coerente e facilita montar a lista de órgãos para o filtro. O contrato guarda apenas a referência ao órgão e ao fornecedor.

### Chave do contrato

Um contrato é identificado pelo par **(Número do Contrato, Código UG)**, porque o número sozinho se repete entre unidades gestoras diferentes. Existe uma restrição única no banco para esse par.

### Valor e data de referência

Cada contrato tem dois valores e quatro datas, e os filtros e gráficos usam um de cada:

- **Valor de referência: Valor Final Compra.** Ele já considera reajustes e acréscimos. No arquivo de junho, apenas 12 contratos têm valor inicial diferente do final.
- **Data de referência: Data de Assinatura.** É quando a contratação de fato aconteceu. A data de publicação não serviria para o gráfico mensal, porque todo o arquivo é de um único mês de publicação.

### Modalidade como enum

Os códigos de modalidade são fixos e vêm do dicionário de dados: 01 Convite, 02 Tomada de Preços, 03 Concorrência, 04 Concorrência Internacional, 05 Pregão, 06 Dispensa de Licitação, 07 Inexigibilidade de Licitação, 20 Concurso, 22 Tomada de Preços por Técnica e Preço, 33 Concorrência por Técnica e Preço, 44 Concorrência Internacional por Técnica e Preço e -99 Pregão – Registro de Preços, além do valor NAO_INFORMADA. O enum `Modalidade` guarda o código, a descrição e os métodos de conversão a partir do código e a partir do texto, concentrando essa regra em um único lugar.

### Origens da modalidade

Cada contratação guarda de onde veio a modalidade atual:

| Origem | Quando |
|---|---|
| ARQUIVO_COMPRAS | Veio da coluna Modalidade Compra ou Modalidade Compra Licitação |
| DERIVADA_ITEM | Foi descoberta pelo Código Item Compra do arquivo de itens |
| INFORMADA_MANUALMENTE | Foi escolhida ou trocada pela tela de cadastro e edição |
| NAO_INFORMADA | Não foi possível descobrir |

A modalidade só muda junto com a origem, pelo método `informarModalidadeManualmente` da própria entidade `Contratacao`.

---

## Regras de tratamento dos dados

Cada regra informa como o dado chega, o que o sistema faz e por quê.

### Arquivo inválido

**Como pode chegar:** arquivo vazio, extensão diferente de `.zip`, arquivo com extensão `.zip` que não é um ZIP de verdade ou está corrompido, ZIP sem o `*_Compras.csv`, `*_Compras.csv` vazio, de outra base ou com cabeçalho sem as colunas obrigatórias, ou `*_ItemCompra.csv` presente com cabeçalho errado. Também é recusado o envio acima de 50 MB.

**O que o sistema faz:** recusa o envio inteiro antes de processar qualquer linha e mostra uma mensagem explicando o problema. A ausência do `*_ItemCompra.csv` não é erro: a importação segue, só sem derivar a modalidade pelos itens.

**Por quê:** se o arquivo não tem a estrutura esperada, nenhuma linha pode ser interpretada com segurança.

### Encoding

**Como chega:** Windows-1252.

**O que o sistema faz:** lê o arquivo nesse encoding. Se vier em UTF-8 com BOM, também aceita.

**Por quê:** lido como UTF-8, o arquivo original quebra todos os acentos ("Ministério" vira "Minist�rio").

### Valores monetários

**Como chegam:** `"800000,0000"` (vírgula decimal, quatro casas).

**O que o sistema faz:** converte para `BigDecimal`, aceitando também formatos como `1.234,56` e `1234.56`. Texto não numérico, valor vazio ou valor negativo fazem a linha ser rejeitada, com o motivo informado.

**Valor zero é aceito.** Os 19 contratos com valor 0,00 do arquivo de junho são contratos não onerosos: comodato de equipamentos, cessão de espaço, concessão de cantina escolar, cessão gratuita de direitos de exibição. Rejeitá-los tiraria do sistema contratos que existem de verdade.

**Por que `BigDecimal`:** valores monetários não podem sofrer arredondamento de ponto flutuante, como acontece com `double`.

### Datas

**Como chegam:** `dd/MM/yyyy`.

**O que o sistema faz:** converte com validação estrita, então datas impossíveis como 31/02/2026 são rejeitadas. A data de assinatura é obrigatória. Também é rejeitado o contrato cuja data de fim de vigência é anterior à data de início.

**Por quê:** a conversão permissiva "corrige" 31/02 para 03/03 sem avisar, o que gravaria uma data errada.

### Modalidade

**Como chega:** "Sem Informação" em 1.738 das 1.749 linhas. A coluna "Modalidade Compra Licitação" vem vazia nessas mesmas linhas.

**O que o sistema faz:** tenta descobrir a modalidade nesta ordem:

1. Coluna "Modalidade Compra", se tiver um valor conhecido.
2. Coluna "Modalidade Compra Licitação", se tiver um valor conhecido.
3. Se o ZIP trouxer o arquivo de itens, a modalidade é derivada do **Código Item Compra**. Pelo dicionário de dados, esse código tem 22 dígitos: 6 do código da UG, **2 da modalidade**, 5 do número da licitação, 4 do ano e 5 do sequencial do item. O sistema liga o item ao contrato pelo par (Número do Contrato, Código UG) e lê os dígitos 7 e 8. Itens com código de outro tamanho (por exemplo "0", usado para itens sob sigilo) ou com dígitos que não correspondem a uma modalidade conhecida são ignorados. Se os itens de um mesmo contrato apontarem modalidades diferentes, a modalidade não é derivada, porque escolher uma delas seria afirmar algo que a fonte não informa com certeza.
4. Se nada funcionar, a modalidade fica como NAO_INFORMADA.

**Conversão a partir do texto:** a comparação ignora acentos, maiúsculas, pontuação e o "s" final das palavras. A fonte escreve "Pregão - Registro de Preço" (5 contratos), enquanto o dicionário diz "Pregão – Registro de Preços". Sem essa tolerância, esses contratos não seriam reconhecidos pela coluna do próprio arquivo.

**Cadastro e edição pela tela:** se a pessoa escolhe uma modalidade, a origem é INFORMADA_MANUALMENTE; se não escolhe, fica NAO_INFORMADA. Na edição, se a modalidade for trocada (inclusive em um contrato importado), a origem passa para INFORMADA_MANUALMENTE (ou NAO_INFORMADA, se a nova escolha for "não informada"); se outros campos forem editados e a modalidade não mudar, a origem continua como estava.

**Resultado no arquivo de junho:** o arquivo de itens traz a modalidade de 1.695 contratos, sem nenhum caso de modalidades conflitantes. Entre os 1.727 contratos importados, 10 têm a modalidade no próprio arquivo de compras, 1.685 recebem a modalidade derivada dos itens e 32 ficam como NAO_INFORMADA. Distribuição final: 843 Inexigibilidade, 553 Pregão, 266 Dispensa, 28 Concorrência, 5 Pregão – Registro de Preços e 32 não informada.

**Por quê:** sem isso, o filtro de modalidade e o gráfico por modalidade ficariam praticamente com uma categoria só. A regra usa apenas um arquivo do mesmo ZIP e o dicionário oficial, sem alterar nenhum arquivo manualmente.

### Documento do contratado

**Como chega:** a coluna "Código Contratado" deveria trazer CPF (11 dígitos) ou CNPJ (14 dígitos), mas aparecem outros casos:

- "-11" com nome "Sigiloso" (22 contratos): o contratado não é divulgado.
- Código começando com "EX", como "EXAXONENT" (3 contratos): empresas estrangeiras, sem CNPJ brasileiro.
- Um código de 9 dígitos que não corresponde a CPF nem a CNPJ (1 contrato).

**O que o sistema faz:** classifica o fornecedor em PF (11 dígitos), PJ (14 dígitos), ESTRANGEIRO (prefixo "EX") ou SIGILOSO ("-11") e rejeita qualquer outro formato com o motivo "documento inválido".

**Por quê:** contratos sigilosos e com fornecedor estrangeiro são situações reais previstas pela fonte, e excluí-los distorceria os totais por órgão. O código de 9 dígitos não se encaixa em nenhuma regra.

### Campos obrigatórios

Número do contrato, código da UG, código e nome do órgão, documento e nome do contratado, data de assinatura e valor final. Se algum estiver vazio, a linha é rejeitada com o nome do campo no motivo.

### Duplicados

**Como chegam:**

- **Linhas totalmente idênticas** (9 em junho): o mesmo registro repetido. O sistema mantém uma e rejeita as outras com o motivo "duplicada no arquivo".
- **Mesma chave (número + UG) com alguma informação diferente** (9 contratos em junho, 12 linhas a mais): normalmente muda a data de publicação no DOU e, em alguns casos, o objeto, a data de fim de vigência ou o valor. São republicações ou retificações do mesmo contrato. O sistema mantém a linha com a **data de publicação mais recente** e rejeita as anteriores com o motivo "substituída por publicação mais recente".
- **Contrato que já está no banco** (por exemplo, ao importar o mesmo arquivo de novo): rejeitado com o motivo "já cadastrado".

**Por quê:** a publicação mais recente é a que reflete a versão corrigida do contrato.

**Detalhes da regra:**

- A remoção de duplicados acontece **só entre as linhas que já passaram na validação**. Assim, se a versão mais recente de um contrato tiver uma data inválida, ela é rejeitada pelo motivo dela e a versão anterior, que estava correta, continua valendo.
- Ordem: primeiro saem as linhas idênticas a uma anterior; depois, entre as versões restantes de cada chave, fica a de publicação mais recente (em caso de empate, a que aparece por último no arquivo); por fim, se a chave escolhida já existe no banco, ela é "já cadastrado".
- As chaves já cadastradas são carregadas do banco em uma única consulta antes do processamento.

### Outras limpezas

- O prefixo "Objeto: " é removido do campo objeto. Ele é um padrão da própria fonte, presente em praticamente todas as linhas. Só esse prefixo padrão é removido: quando o texto escrito pelo órgão também começa com "OBJETO:" (por exemplo "Objeto: OBJETO: SERVIÇOS DE COLETA…", 114 contratos em junho), esse segundo "OBJETO:" é mantido, porque tirá-lo seria editar o conteúdo escrito pelo órgão.
- Espaços nas pontas de todos os campos são removidos.
- "Número Licitação" igual a "-3" (marcador da fonte para contrato sem licitação) vira nulo.
- Os nomes de órgão truncados pela fonte são mantidos como estão, porque não há no arquivo uma informação confiável para completá-los. Por causa do corte, órgãos diferentes chegam com o mesmo nome (por exemplo, 35 Institutos Federais aparecem como "Instituto Federal de Educação, Ciência e T"). Por isso o órgão é sempre identificado pelo **código oficial**, e a unidade gestora aparece só como contexto para ajudar a reconhecê-lo.

### Resultado e gravação da importação

O sistema processa todas as linhas do arquivo, sem limite de quantidade. Ao final, devolve o total de linhas lidas, importadas e rejeitadas, o tempo de processamento, a quantidade de rejeições por motivo, a quantidade de modalidades por origem e a lista de linhas rejeitadas com o número da linha, o contrato, o motivo e o detalhe.

A gravação é feita em lotes, porque o banco está na nuvem e gravar linha por linha deixaria a importação lenta:

- **Ids por `SEQUENCE`** com alocação de 50 (`orgao_seq`, `fornecedor_seq`, `contratacao_seq`): o Hibernate reserva 50 ids de uma vez e não precisa gravar cada linha para descobrir o id, como aconteceria com `IDENTITY`.
- **`hibernate.jdbc.batch_size=50` e `hibernate.order_inserts=true`**: os inserts são enviados em lotes de 50, agrupados por tabela.
- **`reWriteBatchedInserts=true` na URL do JDBC**: o driver do PostgreSQL transforma cada lote em um único comando `INSERT ... VALUES (...), (...), ...`.
- **Órgãos e fornecedores em memória**: os existentes são carregados uma vez no início, e os novos são criados conforme aparecem, sem consultar o banco a cada linha.
- **Uma única transação**: se o banco falhar no meio, nada fica gravado pela metade.

**Resultado no ZIP de junho:** 1.749 lidas, 1.727 importadas, 22 rejeitadas (12 substituídas por publicação mais recente, 9 duplicadas no arquivo, 1 documento inválido), 151 órgãos, modalidade com 10 do arquivo, 1.685 derivadas e 32 não informadas, em cerca de 2,5 segundos. Reimportar o mesmo arquivo resulta em 0 importadas e 1.727 "já cadastrado".

---

## Filtros e gráficos

### Filtros

Todos são opcionais, podem ser combinados livremente e são executados no PostgreSQL com JPA Specification (`ContratacaoSpecifications`). Só entram na consulta os filtros informados.

| Filtro | Como funciona |
|---|---|
| Órgão | Igual ao órgão escolhido |
| Período | Data de assinatura entre a data inicial e a final (cada uma opcional) |
| Modalidade | Igual à modalidade escolhida |
| Faixa de valores | Valor final entre o mínimo e o máximo (cada um opcional) |
| Fornecedor | Nome contém o texto informado, sem diferenciar maiúsculas de minúsculas |

Data inicial depois da final, ou valor mínimo maior que o máximo, devolvem uma mensagem de erro. A listagem é paginada e pode ser ordenada por qualquer coluna.

### Gráficos

Calculados no banco com agrupamento (`GraficoRepository`). Cada um tem título, subtítulo, eixos formatados, tooltip com os valores e legenda nomeando a série.

1. **Valor total por órgão** – barras horizontais, para o texto caber, com os 10 órgãos de maior valor final somado (todos deixariam o gráfico ilegível). A consulta agrupa por código e nome do órgão e devolve a soma e `MIN(nome da UG)` como unidade de exemplo. O rótulo de cada barra é "nome (código)", e o tooltip mostra o valor em R$ e o exemplo de unidade. Assim cada barra fica única mesmo com os nomes truncados pela fonte.
2. **Quantidade de contratações por modalidade** – barras horizontais, da maior para a menor.
3. **Evolução mensal dos valores** – linha com a soma do valor final por mês da data de assinatura. Mostra os **últimos 12 meses contados a partir do mês mais recente com contrato** (no arquivo de junho, de julho/2025 a junho/2026), com **zero nos meses sem contrato**. Em um gráfico de linha os meses precisam estar igualmente espaçados: mostrar só os meses com contrato ligaria pontos distantes como se fossem vizinhos, e mostrar todo o período daria dezenas de pontos quase todos zerados. Os contratos assinados antes desse intervalo continuam no banco e aparecem na listagem.

---

## Tecnologias

| Parte | Escolha | Motivo |
|---|---|---|
| Linguagem | Java 17 | Mínimo do Spring Boot 4; compila com JDK 17 ou mais recente |
| Framework | Spring Boot 4.1.2 (Spring Web MVC, Spring Data JPA, Validation) | Versão mais recente no início do projeto |
| Build | Maven, com Maven Wrapper | Não precisa instalar o Maven |
| Banco | PostgreSQL no Neon | Banco na nuvem, acessado por JDBC com SSL |
| Leitura de CSV | Apache Commons CSV | Lida bem com aspas, separador e encoding configuráveis |
| Front-end | React + Vite + TypeScript + MUI + Recharts | No repositório do front |

---

## Organização do código

```
src/main/java/br/fatec/compraspublicas/
├── model/        entidades e enums
├── repository/   acesso ao banco, filtros e consultas dos gráficos
├── service/      regras do CRUD, filtros, gráficos e orquestração da importação
├── importacao/   cada etapa da importação em uma classe
├── controller/   endpoints REST, sem regra de negócio
├── dto/          records de entrada e saída
└── exception/    exceções de negócio e tratador global de erros
```

Os controllers só recebem a requisição e devolvem a resposta. Validação, importação e cálculos ficam nos services e nas classes de importação. Não há Lombok: getters e setters estão escritos nas entidades só onde fazem sentido (o `id` não tem setter, por exemplo), e os DTOs são `record`, imutáveis.

### Principais classes

| Classe | Papel |
|---|---|
| `model/Contratacao` | Entidade principal. Relaciona-se com Orgao e Fornecedor e controla a modalidade junto com a origem (`informarModalidadeManualmente`) |
| `service/ImportacaoService` | Orquestra a importação: lê o ZIP, lê os CSVs, valida cada linha, resolve a modalidade, remove duplicados, grava e monta o resultado com o tempo |
| `importacao/LeitorZip` | Abre o ZIP em memória, confere se é um ZIP válido e localiza o `*_Compras.csv` e o `*_ItemCompra.csv` |
| `importacao/LeitorCsv` | Lê o CSV com Commons CSV, detecta o encoding (Windows-1252 ou UTF-8 com BOM) e confere as colunas obrigatórias |
| `importacao/ValidadorLinhaCompras` | Valida uma linha (colunas, obrigatórios, documento, valores, datas, vigência), faz as limpezas e devolve a linha convertida ou o motivo da rejeição |
| `importacao/ConversorDados` | Converte texto, valores monetários e datas com validação estrita |
| `importacao/FiltroDuplicados` | Aplica as três regras de duplicado, na ordem: idêntica, publicação mais recente, já cadastrado |
| `importacao/ModalidadePorItens` | Lê o arquivo de itens e monta o mapa contrato → modalidade pelos dígitos 7 e 8 do Código Item Compra, descartando contratos com modalidades conflitantes |
| `importacao/ResolvedorModalidade` | Aplica a cascata: Modalidade Compra → Modalidade Compra Licitação → itens → NAO_INFORMADA |
| `importacao/GravadorContratacoes` | Grava em uma transação, em lotes, com órgãos e fornecedores em memória |
| `repository/ContratacaoSpecifications` | Monta a consulta com os filtros informados, combinados com "e" |
| `repository/GraficoRepository` | Consultas agregadas dos três gráficos |
| `exception/TratadorDeErros` | Converte cada exceção na resposta de erro padronizada, com status e mensagem em português |

---

## Configuração do banco

As credenciais não ficam no repositório. Copie o arquivo de exemplo e preencha com os dados do seu projeto no Neon:

```
cp .env.example .env
```

| Variável | Conteúdo |
|---|---|
| `DB_URL` | URL JDBC do Neon |
| `DB_USER` | usuário do banco |
| `DB_PASSWORD` | senha do banco |
| `SERVER_PORT` | porta da aplicação (padrão 8080) |

Formato da URL:

```
jdbc:postgresql://HOST.neon.tech/BANCO?sslmode=require&channelBinding=require&reWriteBatchedInserts=true
```

A connection string que o Neon mostra começa com `postgresql://usuario:senha@...`. Para o JDBC, troque o início por `jdbc:postgresql://`, tire o usuário e a senha da URL (eles vão em `DB_USER` e `DB_PASSWORD`) e escreva `channelBinding` no lugar de `channel_binding`.

O `application.properties` lê essas variáveis, que podem vir do `.env` na pasta do back (carregado pelo próprio Spring Boot com `spring.config.import=optional:file:.env[.properties]`) ou de variáveis de ambiente do sistema. O `.env` está no `.gitignore`.

**Porta:** configurável por `SERVER_PORT` (padrão 8080).

**Criação das tabelas:** `spring.jpa.hibernate.ddl-auto=update`. O Hibernate cria e ajusta as tabelas a partir das entidades na primeira execução. Cuidado: o Hibernate cria uma restrição CHECK com os valores de cada enum, e o `update` não altera colunas nem CHECKs de tabelas que já existem. Se um enum ganhar um valor novo ou uma coluna mudar de tamanho, as tabelas precisam ser recriadas uma vez, o que **apaga os dados**:

```
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--spring.jpa.hibernate.ddl-auto=create"
```

Depois, pare a aplicação e suba de novo normalmente.

---

## Como executar

### Pré-requisitos

- JDK 17 ou mais recente.
- Node.js (só para o front e para o build único).
- Um banco PostgreSQL no Neon e o `.env` configurado.

### Só o back

```
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS, use `./mvnw spring-boot:run`. A API fica em `http://localhost:8080/api` (ou na porta definida em `SERVER_PORT`).

### Desenvolvimento com o front

Com o back rodando, no repositório do front:

```
npm install
npm run dev
```

e acesse `http://localhost:5173`. O Vite repassa as chamadas `/api` para este back.

### Build único (aplicação completa com um comando)

No repositório do front, que deve estar em uma pasta ao lado deste:

```
npm install
npm run build:backend
```

Isso gera o front em `src/main/resources/static` deste repositório (pasta fora do Git). Depois, aqui:

```
.\mvnw.cmd spring-boot:run
```

e acesse `http://localhost:8080`. O `PaginaController` devolve o `index.html` para as rotas do React, então recarregar a página em qualquer tela funciona. Rode o `build:backend` de novo sempre que o front mudar.

### VS Code

O repositório recomenda as extensões **Extension Pack for Java** e **Spring Boot Extension Pack** (`.vscode/extensions.json`). Com elas, a aplicação pode ser iniciada e depurada pelo painel Spring Boot Dashboard ou pelo botão Run/Debug na classe `GestaoComprasPublicasApplication`.

---

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

### Formato dos erros

Todos os erros seguem o mesmo formato, com mensagens em português:

```json
{ "status": 400, "mensagem": "Dados inválidos. Corrija os campos indicados.", "campos": [{ "campo": "valorFinal", "mensagem": "Informe o valor final." }] }
```

| Status | Quando |
|---|---|
| 400 | Validação, regra de negócio ou arquivo inválido |
| 404 | Registro ou endereço não encontrado |
| 405 | Método não aceito no endereço |
| 409 | Registro duplicado |
| 413 | Arquivo acima de 50 MB |
| 415 | Formato de envio não suportado |
| 500 | Erro inesperado |

---

## Verificação e demonstração

O projeto não tem testes automatizados. As regras são verificadas pela importação real do ZIP original de junho, que precisa produzir o resultado esperado descrito em "Como os dados chegam" (1.749 lidas, 1.727 importadas, 22 rejeitadas), e pela reimportação do mesmo arquivo, que precisa resultar em 0 importadas.

Para a apresentação, existem arquivos de demonstração com casos de dados inválidos, ausentes, duplicados e incompatíveis, e arquivos inválidos. Os ZIPs foram obtidos em https://portaldatransparencia.gov.br/download-de-dados/compras e ficam apenas na máquina local, fora do repositório.
