# Sistema de Mensageria / Messaging System

API backend desenvolvida em Java com Spring Boot para automação de envio de e-mails em campanhas, integrando-se ao SMTP (Gmail) e permitindo o disparo personalizado com anexos PDF, conteúdo HTML e gerenciamento de leads persistidos em banco de dados. Suporta múltiplos materiais, campanhas recorrentes e histórico de envios por aluno.

API backend developed in Java with Spring Boot for automating email campaign dispatch, integrating with SMTP (Gmail) and supporting personalized sending with PDF attachments, HTML content, and lead management persisted in a database. Supports multiple materials, recurring campaigns, and per-student sending history.

---

## Portugues / Portuguese

### Funcionalidades

- Gerenciamento de materiais educativos como campanhas (PDF, conteudo HTML, assunto e ordem de exibicao).
- Disparo de e-mails em lote a partir de arquivos CSV.
- Envio de campanhas com conteudo HTML e anexo PDF a partir de CSV.
- Disparo em massa a partir de leads ativos do banco de dados, com regra inteligente baseada no historico de envios.
- Endpoint de teste para validar uma campanha em um unico e-mail, sem afetar a base nem o historico.
- Historico relacional (`tb_historico_envio`) registrando por aluno e por campanha o status do envio (`PENDENTE`, `ENVIADO` ou `FALHOU`).
- Persistencia de dados no MySQL (Spring Data JPA / Hibernate).
- Configuracao de perfis de ambiente (`dev` / `prod`).
- Credenciais protegidas via variaveis de ambiente e arquivos locais ignorados.

### Modelo de dominio

- `tb_aluno`: leads com nome, e-mail, flag `ativo` e flag `emailEnviado` (legado).
- `tb_campanha`: materiais disparados (titulo, assunto do e-mail, conteudo HTML, PDF opcional, ordem de exibicao e data de criacao).
- `tb_historico_envio`: relacao entre aluno e campanha, com `status` (`PENDENTE`, `ENVIADO`, `FALHOU`) e `dataEnvio`.

### Regra de disparo inteligente

- Ao disparar uma campanha, o sistema envia o e-mail para **todos os alunos ativos** da base (`tb_aluno`), tanto os leads recem-chegados quanto a base antiga, garantindo que todos recebam o material inedito.
- O historico registra que o aluno recebeu **aquela campanha especifica**. Ele serve apenas para evitar duplicidade caso o mesmo `campanhaId` seja disparado novamente por engano.
- O historico **nao bloqueia** o envio de uma campanha nova para alunos que participaram apenas de campanhas anteriores de outros materiais.
- O parametro `apenasNovos` restringe o disparo apenas para novos cadastros (alunos sem historico de envio e sem a flag legada `emailEnviado`).
- Envios com falha ficam registrados como `FALHOU` e podem ser reenviados no proximo disparo; apenas registros `ENVIADO` bloqueiam a reentrega da mesma campanha.

### Endpoints

#### Criar campanha

```
POST /api/campanhas
```

Parâmetros multipart/form-data:

| Parâmetro | Tipo | Descrição |
| --------- | ---- | ----------- |
| `titulo` | text | Titulo da campanha/material. |
| `assunto` | text | Assunto do e-mail. |
| `conteudo` | text | Conteudo HTML do e-mail (aceita marcacao HTML). |
| `ordem` | text (opcional) | Ordem de exibicao do material (padrao: 999). |
| `pdf` | file (opcional) | Arquivo PDF a ser anexado. |

Retorna o `id` da campanha criada, usado nos disparos.

#### Disparo em massa a partir do banco

```
POST /api/emails/disparar-banco
```

Parâmetros:

| Parâmetro | Tipo | Descrição |
| --------- | ---- | ----------- |
| `campanhaId` | query | ID da campanha a ser disparada. |
| `apenasNovos` | query (opcional) | `true` restringe o envio a novos cadastros. Padrao: `false`. |

#### Testar envio para um unico e-mail

```
POST /api/emails/testar?campanhaId={id}&email={seu-email@dominio.com}
```

Envia a campanha (com PDF anexado, se houver) exclusivamente para o e-mail informado, sem registrar historico nem alterar a base.

#### Enviar campanha a partir de CSV

```
POST /api/emails/disparar-csv
```

Parâmetros multipart/form-data:

| Parâmetro | Tipo | Descrição |
| --------- | ---- | ----------- |
| `arquivo` | file | Arquivo CSV contendo um e-mail por linha. |
| `assunto` | text | Assunto do e-mail. |
| `conteudo` | text | Conteudo em texto do e-mail. |

#### Enviar campanha com HTML e anexo PDF a partir de CSV

```
POST /api/emails/disparar-com-pdf
```

Parâmetros multipart/form-data:

| Parâmetro | Tipo | Descrição |
| --------- | ---- | ----------- |
| `csv` | file | Arquivo CSV contendo um e-mail por linha. |
| `pdf` | file | Arquivo PDF a ser anexado em cada e-mail. |
| `assunto` | text | Assunto do e-mail. |
| `conteudo` | text | Conteudo HTML do e-mail (aceita marcacao HTML). |

### Tecnologias utilizadas

- Java 25
- Spring Boot 4.0.6
- Spring Data JPA / Hibernate
- Spring Mail
- MySQL
- Lombok
- Maven

### Pre-requisitos

- JDK 25 ou superior instalado.
- MySQL Server rodando localmente ou acesso a uma instancia de banco de dados.
- Conta SMTP configurada (ex.: Gmail com App Password).

### Configuracao

Para executar a aplicacao localmente, crie o arquivo `application-dev.properties` na pasta `src/main/resources` com as configuracos abaixo, substituindo os campos entre `< >` pelos seus dados reais. Esse arquivo ja esta ignorado pelo `.gitignore` e nao deve ser versionado:

```properties
# ===============================
# Banco de Dados Local (MySQL)
# ===============================
spring.datasource.url=jdbc:mysql://localhost:3306/mensageria_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=<seu_usuario_do_banco>
spring.datasource.password=<sua_senha_do_banco>

# ===============================
# Configuracoes do JPA/Hibernate
# ===============================
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# ===============================
# E-mail (Testes Locais)
# ===============================
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=<seu_email@gmail.com>
spring.mail.password=<sua_senha_de_app>
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

Em producao, as credenciais sao fornecidas por variaveis de ambiente (`DB_URL`, `DB_USER`, `DB_PASSWORD`, `MAIL_USER`, `MAIL_PASSWORD`), conforme configurado no perfil `prod`.

### Seguranca

- Credenciais de banco de dados e e-mail nunca sao armazenadas no repositorio.
- O arquivo `application-dev.properties`, usado para configuracao local, faz parte do `.gitignore`.
- Em producao, todos os segredos sao injetados via variaveis de ambiente.

### Como executar

1. Clone o repositorio.
2. Configure as propriedades de ambiente conforme demonstrado acima.
3. Execute a classe `MensageriaApplication`.
4. A API estara disponivel na porta 8080.

---

## English / English Version

### Features

- Management of educational materials as campaigns (PDF, HTML content, subject, and display order).
- Batch email dispatch from CSV files.
- Campaign sending with HTML content and PDF attachment from CSV.
- Bulk dispatch from active leads in the database, with an intelligent rule based on the sending history.
- Test endpoint to validate a campaign against a single email address, without affecting the base or the history.
- Relational history (`tb_historico_envio`) recording per student and per campaign the send status (`PENDING`, `SENT`, or `FAILED`).
- Data persistence in MySQL (Spring Data JPA / Hibernate).
- Environment profile configuration (`dev` / `prod`).
- Credentials protected via environment variables and ignored local files.

### Domain model

- `tb_aluno`: leads with name, email, `ativo` flag, and legacy `emailEnviado` flag.
- `tb_campanha`: dispatched materials (title, email subject, HTML content, optional PDF, display order, and creation date).
- `tb_historico_envio`: the relationship between student and campaign, with `status` (`PENDENTE`, `ENVIADO`, `FALHOU`) and `dataEnvio`.

### Smart dispatch rule

- When a campaign is dispatched, the system sends the email to **all active students** in the base (`tb_aluno`), both newly arrived leads and the old base, guaranteeing that everyone receives the brand-new material.
- The history records that the student received **that specific campaign**. It only prevents duplicates when the same `campanhaId` is fired again by mistake.
- The history **does not block** sending a new campaign to students who only took part in previous campaigns of other materials.
- The `apenasNovos` parameter restricts the dispatch to new registrations only (students without a sending history and without the legacy `emailEnviado` flag).
- Failed sends are stored as `FALHOU` and can be retried on the next dispatch; only `ENVIADO` records block redelivery of the same campaign.

### Endpoints

#### Create a campaign

```
POST /api/campanhas
```

Multipart/form-data parameters:

| Parameter | Type | Description |
| --------- | ---- | ----------- |
| `titulo` | text | Campaign/material title. |
| `assunto` | text | Email subject. |
| `conteudo` | text | Email HTML content (HTML markup accepted). |
| `ordem` | text (optional) | Material display order (default: 999). |
| `pdf` | file (optional) | PDF file to attach. |

Returns the `id` of the created campaign, used in the dispatches.

#### Bulk dispatch from the database

```
POST /api/emails/disparar-banco
```

Parameters:

| Parameter | Type | Description |
| --------- | ---- | ----------- |
| `campanhaId` | query | ID of the campaign to dispatch. |
| `apenasNovos` | query (optional) | `true` restricts sending to new registrations. Default: `false`. |

#### Test sending to a single email

```
POST /api/emails/testar?campanhaId={id}&email={seu-email@dominio.com}
```

Sends the campaign (with the PDF attached, if any) exclusively to the given email address, without recording history or changing the base.

#### Send campaign from CSV

```
POST /api/emails/disparar-csv
```

Multipart/form-data parameters:

| Parameter | Type | Description |
| --------- | ---- | ----------- |
| `arquivo` | file | CSV file containing one email per line. |
| `assunto` | text | Email subject. |
| `conteudo` | text | Plain text email content. |

#### Send campaign with HTML and PDF attachment from CSV

```
POST /api/emails/disparar-com-pdf
```

Multipart/form-data parameters:

| Parameter | Type | Description |
| --------- | ---- | ----------- |
| `csv` | file | CSV file containing one email per line. |
| `pdf` | file | PDF file to attach to each email. |
| `assunto` | text | Email subject. |
| `conteudo` | text | Email HTML content (HTML markup accepted). |

### Technologies

- Java 25
- Spring Boot 4.0.6
- Spring Data JPA / Hibernate
- Spring Mail
- MySQL
- Lombok
- Maven

### Prerequisites

- JDK 25 or later installed.
- MySQL Server running locally or access to a database instance.
- SMTP account configured (e.g., Gmail with an App Password).

### Configuration

To run the application locally, create the `application-dev.properties` file in the `src/main/resources` folder with the settings below, replacing the fields between `< >` with your real data. This file is already ignored by `.gitignore` and must not be versioned:

```properties
# ===============================
# Local Database (MySQL)
# ===============================
spring.datasource.url=jdbc:mysql://localhost:3306/mensageria_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=<your_database_user>
spring.datasource.password=<your_database_password>

# ===============================
# JPA/Hibernate Settings
# ===============================
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# ===============================
# Email (Local Tests)
# ===============================
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=<seu_email@gmail.com>
spring.mail.password=<sua_senha_de_app>
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

In production, credentials are provided through environment variables (`DB_URL`, `DB_USER`, `DB_PASSWORD`, `MAIL_USER`, `MAIL_PASSWORD`), configured in the `prod` profile.

### Security

- Database and email credentials are never stored in the repository.
- The `application-dev.properties` file, used for local configuration, is part of `.gitignore`.
- In production, all secrets are injected through environment variables.

### How to run

1. Clone the repository.
2. Configure the environment properties as shown above.
3. Run the `MensageriaApplication` class.
4. The API will be available on port 8080.

By: Mauricio Filadelfo Filho.