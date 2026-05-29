# LOCFLIX Frontend

Frontend React + TypeScript integrado ao backend Spring Boot do LOCFLIX.

## O que ja esta implementado

- Autenticacao JWT (`/login`, `/register`)
- Rotas protegidas com guard de autenticacao
- Guard por role para rota admin (`/admin`)
- Catalogo com busca, filtro de disponiveis e paginacao
- Acao de aluguel direto no catalogo
- Tela de locacoes com abas (ativas e historico)
- Acoes de devolver e cancelar locacao
- Painel admin com CRUD basico de filmes e categorias
- Associacao e remocao de categorias por filme no admin
- Monitoramento de locacoes em atraso (admin)
- Tratamento padronizado de erro da API (`ErrorResponse`)
- Cache e mutacoes com React Query
- Validacao de formularios admin com React Hook Form + Zod
- Toasts globais para feedback de sucesso/erro
- Base de testes com Vitest + Testing Library
- Testes de schemas e provider de toasts

## Stack

- React 18 + TypeScript
- Vite
- React Router
- Axios (interceptor com token JWT)
- React Query
- React Hook Form + Zod
- ESLint
- Vitest + Testing Library

## Estrutura principal

- `src/features/auth`: contexto, sessao e autorizacao
- `src/shared/api`: cliente HTTP, erros e APIs por dominio
- `src/shared/queries`: chaves de query para cache
- `src/shared/auth`: persistencia de sessao com expiracao
- `src/shared/components`: guardas, provider de toasts e componentes reutilizaveis
- `src/pages`: telas da aplicacao
- `src/pages/admin`: schemas e testes do painel admin

## Variaveis de ambiente

Crie o arquivo `.env` a partir de `.env.example`.

```bash
cp .env.example .env
```

## Execucao local

> Instale o Node.js no sistema operacional e garanta que `node` e `npm` estejam acessiveis no `PATH`. O frontend deve ser executado com o Node local do SO.

```bash
npm install
npm run dev
```

## Testes

```bash
npm run test
npm run test:run
```

## Rotas

- `/login`
- `/register`
- `/movies` (protegida)
- `/rentals` (protegida)
- `/admin` (protegida por role `ADMIN`)

## Proximos passos

1. Extrair subcomponentes da `AdminPage` para reduzir complexidade visual do arquivo.
2. Ampliar cobertura de testes para fluxos de CRUD do admin.
3. Adicionar testes de integracao para React Query + mutacoes.
4. Evoluir UI com design system.
