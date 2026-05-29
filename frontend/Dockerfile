# syntax=docker/dockerfile:1

# ---------- Build stage ----------
FROM node:20-alpine AS build
WORKDIR /app

# URLs relativas: o nginx do próprio container faz proxy de /api para o backend,
# então o frontend chama a API na mesma origem (sem CORS).
ENV VITE_API_BASE_URL="" \
    VITE_API_URL="/api/v1"

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build

# ---------- Runtime stage ----------
FROM nginx:1.27-alpine AS runtime
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80
