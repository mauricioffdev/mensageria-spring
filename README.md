# Sistema de Mensageria

Este projeto é uma API backend desenvolvida em Java com Spring Boot, focada na automação de envio de e-mails via integração SMTP.

## Funcionalidades
- Disparo de e-mails em lote a partir de arquivos CSV.
- Integração com banco de dados MySQL para persistência de dados.
- Configuração de perfis de ambiente (dev/prod).
- Utilização de variáveis de ambiente para segurança de credenciais.

## Tecnologias Utilizadas
- Java 25
- Spring Boot 4.0.6
- Spring Data JPA / Hibernate
- MySQL
- Lombok
- Maven

## Pré-requisitos
- JDK 25 ou superior instalado.
- MySQL Server rodando localmente ou acesso a uma instância de banco de dados.
- Conta SMTP configurada (ex: Gmail com App Password).

## Configuração
Para rodar a aplicação localmente, crie o arquivo `application-dev.properties` na pasta `src/main/resources` com as configurações abaixo (substitua os campos entre `< >` pelos seus dados):

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
## Como executar
Clone o repositório.

Configure as propriedades de ambiente conforme demonstrado acima.

Execute a classe MensageriaApplication.

A API estará disponível na porta 8080.  

By: Mauricio Filadelfo Filho. 