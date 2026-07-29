# Enterprise Financial Risk Assessment Decision Support System

An enterprise-grade, microservices-based decision support system officially named **"Система прийняття рішень для оцінки фінансових ризиків підприємства"**. 
It automates multi-criteria evaluation of investment alternatives, replacing manual spreadsheet-based analysis with a centralized platform that calculates weighted rankings and generates analytical recommendations.

## Overview

The system allows an admin-analyst to define evaluation factors and weights, run calculations across a set of investment alternatives, 
and receive a ranked list of results with detailed analytical breakdowns (radar charts, comparison by factor, etc.).

## Architecture

The system is built as a set of independent Spring Boot microservices, communicating via REST and registered in a Consul service registry. 
The root container encapsulates the individual microservices inside its service layout:

| Service | Description | Default Port |
|---|---|---|
| `decision-service` | Core business logic: manages alternatives, factors, and evaluation runs | `8081` |
| `calculation-service` | Performs multi-criteria calculations and financial risk ranking | `8082` |
| `user-service` | Handles user accounts, roles, and authentication | `8083` |
| `common-api` | Shared DTOs and API contracts | — |
| `common-enum` | Shared enums used across services | — |
| `decision-frontend` | Web UI (login, dashboards, results visualization) | `5500` |

## Tech Stack

- **Backend:** Java 21, Spring Boot, Hibernate
- **Database:** PostgreSQL
- **Service Registry:** HashiCorp Consul (supports both containerized and separate standalone local testing environments)
- **API Docs:** Swagger / OpenAPI
- **Containerization & Deployment:** Docker, Docker Compose

---

## Getting Started

### Prerequisites
- Docker Desktop (with Docker Compose v2+)
- Java 21 (if running standalone services locally)

### Run the whole system

To spin up all microservices, PostgreSQL, Consul, and the frontend with a single command, run:

```bash
git clone [https://github.com/AlexMozheha/decision-system.git](https://github.com/AlexMozheha/decision-system.git)
cd decision-system
docker compose up --build
```

## Demo

You can watch the full video demonstration of the system's features and financial risk calculation flow here:
[Watch System Demo on Google Drive](https://drive.google.com/drive/folders/1sHV7aZ9NsYAVT1Inb3uoqC0ZDoXzRW-J?usp=sharing)
