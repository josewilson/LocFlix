# CLAUDE.md — LOCFLIX

Guia do projeto para manutenção e contribuições.

## Sobre

Plataforma de streaming estilo Netflix: **backend Spring Boot** (`backend/`) e
**frontend React + TypeScript** (`frontend/`), orquestrados por Docker Compose.
O banco PostgreSQL é **externo** (não sobe no compose); o schema e os dados vêm das
migrations Flyway. Veja o `README.md` para executar.

## Convenção de commits — Conventional Commits (obrigatório)

Toda mensagem de commit segue o formato `<tipo>: <descrição em português do que mudou>`.

| Tipo | Quando usar |
|------|-------------|
| `feat` | Nova funcionalidade ou recurso |
| `fix` | Correção de erros ou bugs |
| `refactor` | Melhoria do código sem alterar a funcionalidade |
| `docs` | Alterações na documentação |
| `style` | Formatação ou estilo (sem alterar lógica) |
| `perf` | Melhorias de performance |
| `test` | Adição ou modificação de testes |
| `chore` | Tarefas de manutenção e configuração |

- Descrição no imperativo; use o corpo (lista com `-`) para detalhar mudanças maiores.
- Escopo opcional: `feat(frontend): ...`, `fix(api): ...`.

## Antes de commitar

- **Frontend:** `cd frontend && npm run lint && npm run test:run && npm run build`
- **Backend:** compila com `./mvnw -q -DskipTests package` (ou via `docker compose build`)
- Não versionar segredos (`.env`) nem binários (a pasta `storage/` de vídeos é ignorada).
