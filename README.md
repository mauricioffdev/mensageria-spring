# Sistema de Mensageria

API backend desenvolvida em Java com Spring Boot para automação de envio de e-mails em campanhas, integrando-se ao SMTP (Gmail) e permitindo o disparo personalizado com anexos PDF, conteúdo HTML e gerenciamento de leads persistidos em banco de dados.

## Funcionalidades

- Disparo de e-mails em lote a partir de arquivos CSV.
- Envio de campanhas com conteúdo HTML e anexo PDF.
- Disparo de campanhas a partir de leads armazenados no banco de dados, com controle de envios realizados.
- Persistência de dados no MySQL (Spring Data JPA / Hibernate).
- Configuração de perfis de ambiente (`dev` / `prod`).
- Utilização de variáveis de ambiente e arquivos locais ignorados para proteção de credenciais.

## Endpoints

Todos os endpoints estão disponíveis sob o prefixo `/api/emails`.

### Enviar campanha a partir de CSV

```
POST /api/emails/disparar-csv
```

Parâmetros multipart/form-data:

| Parâmetro | Tipo | Descrição |
| --------- | ---- | ----------- |
| `arquivo` | file | Arquivo CSV contendo um e-mail por linha. |
| `assunto` | text | Assunto do e-mail. |
| `conteudo` | text | Conteúdo em texto do e-mail. |

### Enviar campanha com HTML e anexo PDF a partir de CSV

```
POST /api/emails/disparar-com-pdf
```

Parâmetros multipart/form-data:

| Parâmetro | Tipo | Descrição |
| --------- | ---- | ----------- |
| `csv` | file | Arquivo CSV contendo um e-mail por linha. |
| `pdf` | file | Arquivo PDF a ser anexado em cada e-mail. |
| `assunto` | text | Assunto do e-mail. |
| `conteudo` | text | Conteúdo HTML do e-mail (aceita marcação HTML). |

### Enviar campanha a partir de leads do banco de dados

```
POST /api/emails/disparar-banco
```

Parâmetros multipart/form-data:

| Parâmetro | Tipo | Descrição |
| --------- | ---- | ----------- |
| `pdf` | file | Arquivo PDF a ser anexado em cada e-mail. |
| `assunto` | text | Assunto do e-mail. |
| `conteudo` | text | Conteúdo HTML do e-mail (aceita marcação HTML). |

Neste modo, a aplicação seleciona os alunos registrados como ativos que ainda não receberam e-mail (`emailEnviado = false`). Cada e-mail enviado com sucesso marca o registro como enviado, impedindo reenvios duplicados em campanhas futuras.

## Tecnologias Utilizadas

- Java 25
- Spring Boot 4.0.6
- Spring Data JPA / Hibernate
- Spring Mail
- MySQL
- Lombok
- Maven

## Pré-requisitos

- JDK 25 ou superior instalado.
- MySQL Server rodando localmente ou acesso a uma instância de banco de dados.
- Conta SMTP configurada (ex.: Gmail com App Password).

## Configuração

Para executar a aplicação localmente, crie o arquivo `application-dev.properties` na pasta `src/main/resources` com as configurações abaixo, substituindo os campos entre `< >` pelos seus dados reais. Esse arquivo já está ignorado pelo `.gitignore` e não deve ser versionado:

```properties
# ===============================
# Banco de Dados Local (MySQL)
# ===============================
spring.datasource.url=jdbc:mysql://localhost:3306/mensageria_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=<seu_usuario_do_banco>
spring.datasource.password=<sua_senha_do_banco>

# ===============================
# Configurações do JPA/Hibernate
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

Em produção, as credenciais são fornecidas por variáveis de ambiente (`DB_URL`, `DB_USER`, `DB_PASSWORD`, `MAIL_USER`, `MAIL_PASSWORD`), conforme configurado no perfil `prod`.

## Segurança

- Credenciais de banco de dados e e-mail nunca são armazenadas no repositório.
- O arquivo `application-dev.properties`, usado para configuração local, faz parte do `.gitignore`.
- Em produção, todos os segredos são injetados via variáveis de ambiente.

## Como executar

1. Clone o repositório.
2. Configure as propriedades de ambiente conforme demonstrado acima.
3. Execute a classe `MensageriaApplication`.
4. A API estará disponível na porta 8080.

By: Mauricio Filadelfo Filho.
