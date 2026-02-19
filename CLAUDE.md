# CLAUDE.md — Project Context & Instructions

## 🧠 Project Overview

**Name:** Notification Library 
**Description:** Biblioteca reutilizable que permita enviar notificaciones por múltiples canales (Email, SMS, Push Notification) 
**Status:** Active development  
**Main language:** Java 25

---

## 📂 Available Skills

Antes de ejecutar cualquier tarea, leer el skill correspondiente:

| Tarea                  | Leer primero                                        |
|------------------------|-----------------------------------------------------|
| Codificar en Java       | `.claude/skills/java-fundamentals/SKILL.md`         |
| Crear pruebas unitarias| `.claude/skills/java-testing/SKILL.md`              |
| Implementar Async      | `.claude/skills/java-concurrency/SKILL.md`          |
| Operar Git             | `.claude/skills/git-advanced-workflows/SKILL.md`     |
| Generar imagen docker  | `.claude/skills/java-docker/SKILL.md`     |

---

## 🔍 Execution Transparency

Cada vez que cargues un skill, notificar al usuario con este formato antes de ejecutarlo:

> 📦 Cargando skill: `/skills/pptx/SKILL.md`

---

## 🏗️ Architecture & Stack

- **Runtime:** Java 25
- **Framework:** NO FRAMEWORKS
- **Language:** Java 25
- **Testing:** JUnit + Mockito
- **Container:** Docker
- **Repository:** Github
- **Architecture:** Arquitectura Hexagonal (Ports & Adapters).
- **BuildTool:** Maven

---


## ⚙️ Key Commands

```bash
# Development
mvn compile              # Compile production sources
mvn clean package        # Build JAR

# Testing
mvn test                 # Run all tests
mvn test -Dtest=ClassName        # Run a single test class
mvn test -Dtest=ClassName#method # Run a single test method

```

---

## 📐 Coding Conventions

### General
- framework-agnóstica
- SOLID
- Se comunica con el exterior solo mediante interfaces
- Notificaciones asíncronas con CompletableFuture
- Configurar credenciales de proveedores (API keys, tokens, etc.)
- Configuración 100% mediante código Java
- Soportar múltiples proveedores por canal
- Interfaz unificada que funcione para todos los canales

### Patrones de diseño
- Facade
- Strategy 
- Factory 
- Adapter 

### Logging
- Log4j2
- Logging detallado durante todo el flujo (paso a paso)

### Error Handling
- Distinguir entre errores de validación y errores de envío
- Información clara sobre qué falló
- Implementar código de errores
- Fácil de usar con try-catch

### Async
- Envío no bloqueante de notificaciones
- Usar CompletableFuture para manejo asíncrono
- Permitir envío en lote

---

## 🧪 Testing Guidelines

- Minimo 80% de coverage en services
- Cada módulo debe tener: pruebas unitarias (servicio) 

---

## 🧪 Samples

- un Dockerfile que empaquete todo
- Empaquetar la librería compilada
- Incluir ejemplos de uso ejecutables
- Permitir ejecutar demos sin configurar Java localmente

---

## 🛠️ Error Catalog

- V001–V010: Errores de validación (destinatario vacío, formato de email inválido, cuerpo vacío, etc.)
- S001–S006: Errores de envío (autenticación del proveedor fallida, proveedor no disponible, etc.)
- C001–C002: Errores de configuración (ningún sender configurado, configuración inválida)

---

## 🚫 Things Claude Should NOT Do

- No depende de frameworks
- Sin YAML/properties

---

## ✅ Things Claude Should Always Do

- Mantener funciones pequeñas y con responsabilidad única
- Utilizar gitflow para el manejo del repositorio git y gtihub.

---

## 📦 Estructura de Paquetes (Hexagonal)

```
com.notification
├── domain/
│   ├── model/          → Notification, Recipient, NotificationResult, Channel, NotificationStatus, ProviderConfig
│   ├── port/
│   │   ├── input/      → SendNotificationUseCase, SendBatchNotificationUseCase
│   │   └── output/     → NotificationSender (SPI para proveedores)
│   ├── exception/      → ErrorCode, NotificationException, ValidationException, SendingException, ConfigurationException
│   └── validation/     → NotificationValidator
├── application/
│   └── service/        → NotificationService (orquestador)
├── infrastructure/
│   ├── adapter/
│   │   ├── email/      → SmtpEmailSender, SendGridEmailSender
│   │   ├── sms/        → TwilioSmsSender, MockSmsSender
│   │   └── push/       → FirebasePushSender, MockPushSender
│   └── factory/        → SenderFactory
├── facade/             → NotificationFacade, NotificationConfig, ChannelConfig
└── demo/               → NotificationDemo
```

---

## 🧪 Tabla de Tests

| Test | Qué valida | Prioridad |
|------|-----------|-----------|
| `NotificationValidatorTest` | Todos los códigos V001-V010, @ParameterizedTest para formatos | Alta |
| `NotificationServiceTest` | Envío exitoso, fallos de validación, fallos de proveedor, batch | Crítica (80%+) |
| `SenderFactoryTest` | Registro, obtención, C001 cuando no existe | Alta |
| `NotificationFacadeTest` | Creación con config, delegación a service, provider desconocido | Alta |
| `SmtpEmailSenderTest` | Configuración válida/inválida, canal correcto | Media |
| `SendGridEmailSenderTest` | Configuración, envío simulado | Media |
| `TwilioSmsSenderTest` | Credenciales requeridas, envío | Media |
| `FirebasePushSenderTest` | Configuración, envío | Media |
| `NotificationTest` | Creación de records, inmutabilidad | Media |
| `NotificationConfigTest` | Builder chaining | Baja |
| `ErrorCodeTest` | Códigos únicos, formato correcto | Baja |

---

## 🔄 Flujo de Patrones

```
Cliente → NotificationFacade (FACADE) → NotificationService (ORCHESTRATOR)
            ↓                                    ↓
      NotificationConfig              NotificationValidator
      ChannelConfig                          ↓
                                      SenderFactory (FACTORY)
                                             ↓
                                    NotificationSender (STRATEGY - interfaz)
                                     /       |       \
                              SmtpEmail  TwilioSms  FirebasePush  (ADAPTER)
```

---

## 📝 Additional Notes

- Ser fácil de extender (nuevos canales o proveedores)
- Facilitar el cambio entre canales sin modificar el código cliente.
- El archivo README.md debe describir la instalación (Instalacion), un ejemplo simple (Quick Start), como configurar cada canal y proveedor (Configuración), Clases y métodos principales (Api Reference),
- Responder en español en todo momento

---

