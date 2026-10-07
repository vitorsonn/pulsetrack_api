# ⚡ PulseTrack — Real-Time Delivery Tracking System

**PulseTrack** é uma plataforma moderna de rastreamento de entregas em tempo real. O sistema utiliza uma arquitetura reativa com **Redis Streams**, **Server-Sent Events (SSE)** e **Spring Boot** no backend, integrada a uma interface dinâmica em **Angular Standalone** com **Leaflet** no frontend.

---

## 📐 Arquitetura do Sistema

```
[Simulador GPS / Rest Client]
           │ (POST /deliveries/location)
           ▼
   [Spring Boot API] ──► [Redis Stream] ──► [Stream Consumer]
                                                    │
                   ┌────────────────────────────────┴────────────────┐
                   ▼                                                 ▼
          [PostgreSQL]                                      [Redis Key-Value]
     (Histórico de Rotas)                                (Posição Atual em Cache)
                                                                     │
                                                                     ▼
                                                          [SseEmitterService]
                                                                     │ (SSE / EventStream)
                                                                     ▼
                                                          [Angular + Leaflet UI]
```

---

## 🚀 Tecnologias Utilizadas

### **Backend**
- **Java 17 / Spring Boot 3** (Web, Data JPA, Validation)
- **Redis & Redis Streams** (Ingestão de alta performance e pub/sub de coordenadas)
- **PostgreSQL** (Persistência de pedidos e histórico de entregas)
- **Server-Sent Events (SSE)** (Transmissão contínua em tempo real para o cliente)

### **Frontend**
- **Angular 17+ (Standalone Components)**
- **Leaflet.js** (Renderização e manipulação do mapa interativo)
- **Tailwind CSS** (Interface moderna com tema Dark Mode)
- **RxJS** (Consumo reativo de fluxos de eventos)

### **Ferramentas & Scripts**
- **Python 3** (Script simulador de trajetos em tempo real)

---

## ✨ Principais Funcionalidades

- 🛰️ **Rastreamento Ao Vivo:** Atualização contínua da posição do entregador sem necessidade de *polling*.
- 📍 **Interpolação Suave no Mapa:** Transição fluida do marcador a 60fps usando `requestAnimationFrame`.
- 🗺️ **Rastro da Rota (Polyline):** Linha dinâmica desenhando o trajeto percorrido no mapa.
- ⚡ **Persistência de Estado:** Recuperação instantânea da última coordenada registrada no Redis ao recarregar a página (F5).
- 🔄 **Navegação Dinâmica de Pedidos:** Suporte a múltiplos pedidos via rotas com gerenciamento e cancelamento automático de conexões SSE anteriores.
- 🏁 **Encerramento de Entrega:** Atualização do status da ordem com fechamento gracioso do canal de transmissão.

---

## 🛠️ Como Executar o Projeto

### **1. Pré-requisitos**
- Java 17+
- Node.js 18+ & Angular CLI
- Docker & Docker Compose (para Postgres e Redis)
- Python 3 + biblioteca `requests` (para o simulador)

---

### **2. Subir a Infraestrutura (Docker)**

```bash
docker run -d --name pulsetrack-postgres -p 5432:5432 -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=pulsetrack postgres:15
docker run -d --name pulsetrack-redis -p 6379:6379 redis:7-alpine
```

---

### **3. Iniciar o Backend (Spring Boot)**

```bash
cd backend
./mvnw spring-boot:run
```
> O servidor estará rodando em `http://localhost:8080`.

---

### **4. Iniciar o Frontend (Angular)**

```bash
cd frontend
npm install
ng serve
```
> Acesse a aplicação em `http://localhost:4200/tracking/1`.

---

### **5. Simular uma Entrega em Tempo Real**

Instale a dependência do script Python e execute a simulação:

```bash
pip install requests
python scripts/simulate_delivery.py
```

Assista ao pino se deslocar suavemente no mapa e à rota ser desenhada em tempo real!

---

## 📂 Estrutura do Projeto

```text
pulsetrack/
├── backend/                  # API Spring Boot
│   ├── src/main/java/com/api/pulsetrack/
│   │   ├── modules/order/    # Módulo de Pedidos
│   │   └── modules/tracking/ # Módulo de Rastreamento & SSE
├── frontend/                 # Aplicação Angular Standalone
│   ├── src/app/
│   │   ├── core/             # Services & Models
│   │   └── features/tracking/# Componentes da tela de rastreamento
└── scripts/                  # Scripts de simulação
    └── simulate_delivery.py  # Simulador de pulsos de GPS
```

---

## 📄 Licença
Este projeto está sob a licença MIT. Sinta-se à vontade para estudar, modificar e compartilhar!