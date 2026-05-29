# ⚡ Quick Start - LOCFLIX Backend em 5 Minutos

[![Quick Setup](https://img.shields.io/badge/Setup-5_minutos-brightgreen)](.)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.13-brightgreen?logo=spring-boot)](.)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14+-blue?logo=postgresql)](.)

## 🎯 Objetivo

Você terá uma **API REST funcional** com autenticação JWT rodando localmente em menos de **5 minutos**.

---

## ⏱️ Timeline

```
Minuto 1: Clone & Configure
Minuto 2: Banco de Dados
Minuto 3: Compilar
Minuto 4: Executar
Minuto 5: Testar ✅
```

---

## 📝 Passo 1: Clone & Configure (1 minuto)

```bash
# Command 1: Clone o repositório
cd ~/projects
git clone https://seu-repo.git
cd LOCFLIX/backend

# Command 2: Copie template de ambiente
cp .env.example .env

# Command 3: Abra e edite .env
# Windows:  notepad .env
# Mac:      nano .env
# Linux:    vim .env
```

**Edite estas linhas em `.env`:**
```env
DB_URL=jdbc:postgresql://localhost:5432/locadora
DB_USERNAME=postgres
DB_PASSWORD=postgres
JWT_SECRET=dev-secret-key-change-in-production-minimum-256bits
SPRING_PROFILE=dev
```

---

## 🗄️ Passo 2: Banco de Dados (1 minuto)

### PostgreSQL instalado e rodando?

**Windows (PowerShell):**
```powershell
# Command 1: Criar database
psql -U postgres -c "CREATE DATABASE locadora;"

# Or via GUI:
# 1. Abra pgAdmin
# 2. Right-click "Databases" → "Create" → "Database"
# 3. Nome: locadora
```

**Mac/Linux:**
```bash
# Command: Criar database
createdb -U postgres locflix_dev

# Verificar:
psql -U postgres -l | grep locflix
```

**Sem PostgreSQL?** 
- Windows: https://www.postgresql.org/download/windows/
- Mac: `brew install postgresql`
- Linux: `sudo apt install postgresql`

✅ **Done!** Migrations rodam automaticamente na primeira execução.

---

## 🔨 Passo 3: Compilar (1 minuto)

```bash
# Command: Compilar projeto
./mvnw clean compile

# Esperado:
# [INFO] BUILD SUCCESS
```

❌ **Erro?**
```bash
# Solução 1: Limpar Maven cache
./mvnw clean -U

# Solução 2: Verificar Java
java -version  # Deve ser 21+

# Solução 3: Verificar Maven
./mvnw -v      # Deve ser 3.8.1+
```

---

## 🚀 Passo 4: Executar (1 minuto)

```bash
# Command: Iniciar aplicação
./mvnw spring-boot:run

# Esperado no console:
# [main] o.s.b.w.e.tomcat.TomcatWebServer: Tomcat started on port(s): 8080
```

✅ **Pronto!** Aplicação está rodando.

---

## 🧪 Passo 5: Testar (1 minuto)

### Abrir Swagger UI

```
http://localhost:8080/swagger-ui.html
```

Você verá documentação interativa de todos os 27 endpoints.

### Teste 1: Registrar Usuário

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "TestPass123!",
    "firstName": "Test",
    "lastName": "User"
  }'

# Response esperado (201):
{
  "id": 1,
  "email": "test@example.com",
  "firstName": "Test",
  "fullName": "Test User",
  "roles": ["USER"]
}
```

### Teste 2: Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "TestPass123!"
  }'

# Response esperado (200):
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "expiresIn": 86400000,
  "user": { ... }
}
```

**Copie o `token` para usar nos próximos testes.**

### Teste 3: Listar Filmes

```bash
curl -X GET "http://localhost:8080/api/v1/movies?page=0&size=20" \
  -H "Authorization: Bearer <seu-token-aqui>"

# Response esperado (200):
{
  "content": [ ... filmes ... ],
  "pageable": { "pageNumber": 0, "pageSize": 20 },
  "totalElements": 5
}
```

### Teste 4: Alugar Filme

```bash
# Primeiro, criar um filme (admin needed, pule este para v1)
# Ou use um filme existente (ID 1):

curl -X POST http://localhost:8080/api/v1/rentals \
  -H "Authorization: Bearer <seu-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "movieId": 1,
    "daysToRent": 7
  }'

# Response esperado (201):
{
  "id": 1,
  "usernam": "Test User",
  "movieTitle": "Matrix",
  "status": "ACTIVE",
  "totalPrice": 111.30,
  "daysRented": 7
}
```

---

## 📊 Endpoints Principais para Testar

| Recurso | Endpoint | Método | Auth |
|---------|----------|--------|------|
| **Autenticação** | `/api/v1/auth/login` | POST | ❌ |
| Registrar | `/api/v1/auth/register` | POST | ❌ |
| **Filmes** | `/api/v1/movies` | GET | ✅ |
| Detalhes | `/api/v1/movies/{id}` | GET | ✅ |
| Stock | `/api/v1/movies/available` | GET | ✅ |
| Popular | `/api/v1/movies/popular` | GET | ✅ |
| **Categorias** | `/api/v1/categories` | GET | ✅ |
| Popular | `/api/v1/categories/popular` | GET | ✅ |
| **Locações** | `/api/v1/rentals` | GET | ✅ |
| Criar | `/api/v1/rentals` | POST | ✅ |
| Devolver | `/api/v1/rentals/{id}/return` | PUT | ✅ |
| Ativas | `/api/v1/rentals/active` | GET | ✅ |
| Histórico | `/api/v1/rentals/history` | GET | ✅ |

---

## 🆘 Troubleshooting

### "Connection Refused" ao compilar?
```
❌ Problema: PostgreSQL não está rodando
✅ Solução:
   Windows: Abra pgAdmin ou Services
   Mac: brew services start postgresql@14
   Linux: sudo systemctl start postgresql
```

### "Port 8080 already in use"?
```
❌ Problema: Porta já está em uso
✅ Solução 1: Encerrar outro processo
   Windows: netstat -ano | findstr :8080
   Mac/Linux: lsof -i :8080
✅ Solução 2: Mudar porta
   ./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### "Invalid JWT"?
```
❌ Problema: Token inválido ou expirado
✅ Solução: Faça login novamente para obter novo token
```

### Compilação lenta?
```
✅ Solução: Use compilação incremental
./mvnw -pl . compile -am -DskipTests
```

---

## 🎓 Próximos Passos

### 1. **Explorar a API**
- Abra Swagger: http://localhost:8080/swagger-ui.html
- Teste todos os 27 endpoints
- Leia as descrições

### 2. **Entender Arquitetura**
- Leia [PHASE1_SUMMARY.md](PHASE1_SUMMARY.md)
- Leia [PHASE2_SUMMARY.md](PHASE2_SUMMARY.md)

### 3. **Modificar Código**
- Explorar `src/main/java/com/locflix/`
- Adicionar features
- Criar testes

### 4. **Deploy**
- Ver [README.md](README.md) seção Deploy
- Configurar produção

---

## 🔧 Ferramentas Úteis

### Testar APIs (alternatives to curl)

**Insomnia** (Recomendado)
```
https://insomnia.rest/
- Import: Swagger json de http://localhost:8080/v3/api-docs
```

**Postman**
```
https://www.postman.com/
- Import: Swagger collection
```

**HTTPie**
```
brew install httpie
http POST http://localhost:8080/api/v1/auth/login \
  email="test@example.com" password="TestPass123!"
```

### Visualizar Banco de Dados

**pgAdmin** (GUI)
```
Port: 5050
http://localhost:5050
```

**DBeaver** (Free)
```
https://dbeaver.io/
- Conectar ao localhost:5432
```

**psql** (CLI)
```
psql -U postgres -d locflix_dev
psql> SELECT * FROM users;
psql> \dt  -- listar tabelas
```

---

## 📈 Load Testing (Bônus)

```bash
# Apache Bench (ab)
ab -n 1000 -c 10 http://localhost:8080/api/v1/movies

# Output: Requests per second, latency, etc.

# Para POST:
apache-ab -n 1000 -c 10 -T application/json \
  -p payload.json \
  http://localhost:8080/api/v1/rentals
```

---

## ✅ Checklist Final

```
☐ PostgreSQL instalado e rodando
☐ Java 21+ instalado (java -version)
☐ Maven funcionando (./mvnw -v)
☐ .env configurado
☐ Projeto compilou sem erros
☐ API rodando em http://localhost:8080
☐ Swagger acessível
☐ Conseguiu fazer login
☐ Conseguiu alugar um filme
☐ Tudo testado ✅
```

---

## 🎯 Status

| Atividade | Status |
|-----------|--------|
| Setup | ✅ Completo |
| Compilação | ✅ Sucesso |
| Execução | ✅ Rodando |
| Testes | ✅ Passando |

> Usuário admin: opcional. Ele só é necessário se você for usar o painel `/admin` e as rotas restritas por role `ADMIN`. Para login/cadastro e uso do catálogo, um usuário com role `USER` já basta.

---

## 🚀 Você está pronto!

```
┌─────────────────────────────┐
│  🎉 API REST em Produção 🎉  │
│                              │
│  27 Endpoints disponíveis    │
│  Autenticação JWT            │
│  Clean Code                  │
│  Production-Ready            │
└─────────────────────────────┘
```

**Happy Coding!** ✨

---

<div align="center">

[📖 README Completo](README.md) | [🏗️ Arquitetura](PHASE2_SUMMARY.md) | [📊 Infraestrutura](PHASE1_SUMMARY.md)

**[⬆ voltar ao topo](#)**

</div>

