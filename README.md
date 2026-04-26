# Plateforme de Suivi des Patients Chroniques

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED)
![License](https://img.shields.io/badge/License-MIT-yellow)

Implémentation des deux microservices :

| Service | Rôle | Port |
|---------|------|------|
| `patient-profile-service` | Données démographiques, pathologies, traitements | `8081` |
| `reporting-service` | Génération de rapports hebdomadaires/mensuels (JSON) | `8085` |

## Architecture

```
+----------------------+        +----------------------+
| patient-profile-svc  |  <---  |  reporting-service   |
| (CRUD patients)      |  Feign |  (rapports JSON)     |
+----------+-----------+        +-----------+----------+
           |                                |
   +-------v-------+                +-------v-------+
   | postgres-     |                | postgres-     |
   | patient       |                | reporting     |
   +---------------+                +---------------+
```

- **Stack** : Java 17, Spring Boot 3.2, Spring Data JPA, Spring Cloud OpenFeign, Resilience4j, PostgreSQL 16, MapStruct, Lombok, springdoc-openapi
- **Pattern** : *Database per service* — chaque microservice possède sa propre base de données
- **Communication** : REST/JSON entre services via client Feign avec circuit-breaker et fallback
- **Observabilité** : Spring Boot Actuator (`/actuator/health`, `/actuator/metrics`, `/actuator/prometheus`)
- **Documentation API** : Swagger UI exposé sur chaque service

## Démarrage rapide (Docker Compose)

```bash
cd patient-platform
docker-compose up --build
```

Puis :

- Patient API : http://localhost:8081/swagger-ui.html
- Reporting API : http://localhost:8085/swagger-ui.html

## Démarrage local (profil dev — H2 en mémoire)

```bash
# Terminal 1
cd patient-profile-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Terminal 2
cd reporting-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Endpoints principaux

### patient-profile-service (`:8081`)

| Méthode | URL | Description |
|---------|-----|-------------|
| `GET`    | `/api/v1/patients`                       | Liste paginée (param `search`) |
| `POST`   | `/api/v1/patients`                       | Créer un patient |
| `GET`    | `/api/v1/patients/{id}`                  | Détail d'un patient |
| `PUT`    | `/api/v1/patients/{id}`                  | Mise à jour |
| `DELETE` | `/api/v1/patients/{id}`                  | Suppression |
| `GET`    | `/api/v1/patients/by-pathology/{type}`   | Patients par type de pathologie |
| `POST`   | `/api/v1/patients/{id}/pathologies`      | Ajouter une pathologie |
| `GET`    | `/api/v1/patients/{id}/pathologies`      | Lister les pathologies |
| `POST`   | `/api/v1/patients/{id}/treatments`       | Ajouter un traitement |
| `GET`    | `/api/v1/patients/{id}/treatments`       | Lister les traitements |
| `GET`    | `/api/v1/patients/stats/count`           | Nombre total de patients |

### reporting-service (`:8085`)

| Méthode | URL | Description |
|---------|-----|-------------|
| `POST` | `/api/v1/reports/generate`            | Générer un rapport (corps : type, période, patientId) |
| `POST` | `/api/v1/reports/weekly`              | Génération hebdo (param optionnel `patientId`) |
| `POST` | `/api/v1/reports/monthly`             | Génération mensuelle |
| `GET`  | `/api/v1/reports`                     | Liste paginée (filtre `type`) |
| `GET`  | `/api/v1/reports/{id}`                | Récupérer un rapport |
| `GET`  | `/api/v1/reports/by-patient/{id}`     | Rapports d'un patient |
| `DELETE` | `/api/v1/reports/{id}`              | Supprimer un rapport |

### Tâches planifiées (reporting-service)

| Tâche | Cron | Action |
|-------|------|--------|
| Rapport hebdomadaire global | `0 0 2 * * MON` | Tous les lundis à 02:00 |
| Rapport mensuel global      | `0 0 3 1 * *`   | Le 1er du mois à 03:00 |

## Exemple de payload

### Créer un patient

```json
POST /api/v1/patients
{
  "firstName": "Marie",
  "lastName": "Durand",
  "socialSecurityNumber": "2850712345678",
  "birthDate": "1985-07-12",
  "gender": "FEMALE",
  "email": "marie.durand@example.com",
  "phoneNumber": "+33612345678",
  "referringDoctor": "Dr. Martin"
}
```

### Ajouter une pathologie

```json
POST /api/v1/patients/{id}/pathologies
{
  "type": "DIABETES_TYPE_2",
  "icd10Code": "E11",
  "name": "Diabète type 2",
  "severity": "MODERATE",
  "diagnosisDate": "2022-03-15",
  "active": true
}
```

### Générer un rapport hebdomadaire global

```bash
curl -X POST http://localhost:8085/api/v1/reports/weekly
```

Retour :

```json
{
  "id": "...",
  "type": "WEEKLY",
  "status": "COMPLETED",
  "periodStart": "2026-04-19",
  "periodEnd": "2026-04-26",
  "title": "Rapport WEEKLY - Global - du 2026-04-19 au 2026-04-26",
  "data": {
    "scope": "GLOBAL",
    "totalPatients": 42,
    "pathologyDistribution": { "DIABETES_TYPE_2": 12, "HYPERTENSION": 18 },
    "genderDistribution": { "MALE": 20, "FEMALE": 22 },
    "ageDistribution": { "0-17": 0, "18-34": 5, "35-49": 12, "50-64": 18, "65+": 7 },
    "activeTreatments": 87,
    "topMedications": [ { "medication": "Metformine", "count": 12 } ]
  }
}
```

## Structure du projet

```
patient-platform/
├── docker-compose.yml
├── README.md
├── .gitignore
├── patient-profile-service/
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/
│       ├── main/java/com/healthcare/patientprofile/
│       │   ├── PatientProfileServiceApplication.java
│       │   ├── controller/    # PatientController, PathologyController, TreatmentController
│       │   ├── service/       # PatientService, PathologyService, TreatmentService
│       │   ├── repository/    # JPA repositories
│       │   ├── entity/        # Patient, Pathology, Treatment
│       │   ├── dto/           # DTOs avec validation
│       │   ├── mapper/        # MapStruct
│       │   ├── exception/     # GlobalExceptionHandler
│       │   └── config/        # OpenApiConfig
│       └── main/resources/application.yml
└── reporting-service/
    ├── pom.xml
    ├── Dockerfile
    └── src/
        ├── main/java/com/healthcare/reporting/
        │   ├── ReportingServiceApplication.java
        │   ├── controller/    # ReportController
        │   ├── service/       # ReportService
        │   ├── scheduler/     # ReportScheduler (cron hebdo/mensuel)
        │   ├── client/        # PatientProfileClient (Feign + Fallback)
        │   ├── repository/    # ReportRepository
        │   ├── entity/        # Report (jsonb data)
        │   ├── dto/           # ReportDTO, GenerateReportRequest, PatientSummaryDTO
        │   ├── enums/         # ReportType, ReportStatus
        │   ├── exception/
        │   └── config/        # OpenApiConfig, FeignConfig
        └── main/resources/application.yml
```

## Tests

```bash
cd patient-profile-service && mvn test
cd reporting-service       && mvn test
```

## Notes de conception

- Les rapports sont **persistés** (table `reports`) ce qui permet de retrouver l'historique et d'éviter de recalculer.
- Le champ `data` est en `jsonb` (PostgreSQL) — flexible pour faire évoluer la structure du rapport sans migration.
- Le client Feign vers `patient-profile-service` est protégé par un circuit-breaker (Resilience4j) avec fallback : si le service est indisponible, le rapport est marqué `FAILED` plutôt que de planter.
- Validation Bean Validation côté DTO + `@RestControllerAdvice` pour formater les erreurs.
- Profil `dev` avec H2 en mémoire pour développement local sans Docker.
