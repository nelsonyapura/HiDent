
# Hi Dent - Sistema de Gestion 

Sistema web completo para la gestion de clinicas odontologicas. Permite administrar pacientes, historias clinicas, presupuestos, citas y respaldos automaticos. Desarrollado con Java 21, Spring Boot 4, PostgreSQL y Thymeleaf.

## Tecnologias

- Java 21, Spring Boot 4.0.2, Spring Security, Spring Data JPA
- Thymeleaf + Bootstrap 5.3 + CSS personalizado
- PostgreSQL
- Autenticacion con JWT (stateless, basada en cookies)
- Maven
- Google Drive API para respaldos, Gmail SMTP para notificaciones

## Funcionalidades

### Gestion de Pacientes
- Registro de pacientes con datos personales y de contacto
- Busqueda y filtrado por nombre o numero de documento
- Numero de historia clinica autogenerado

### Historia Clinica
- Anamnesis con formulario completo de antecedentes medicos
- Odontograma interactivo con seguimiento por pieza dental segun la Norma Tecnica del MINSA
- Notas de evolucion cronologicas por tratamiento
- Revision oral de tejidos blandos y duros
- Subida y gestion de documentos por paciente
- Consentimientos informados digitales

### Presupuestos y Proformas
- Creacion de presupuestos con catalogo de 46 servicios dentales en 6 categorias
- Soporte de doble moneda (PEN/USD)
- Edicion de items con cantidad y descuentos
- Generacion de proformas para pacientes prospectivos

### Agenda de Citas
- Vista de calendario por mes, semana y dia
- Creacion de citas vinculadas a pacientes y servicios
- Control de estados (programada, completada, cancelada)

### Respaldos Automaticos
- Backup de PostgreSQL via pg_dump
- Subida automatica a Google Drive
- Notificaciones por correo al completar o fallar el respaldo
- Politica de retencion configurable

## Estructura del Proyecto

```
src/main/java/com/odontologia/odontologia/
├── config/          Configuracion e inicializadores de datos
├── controller/      Controladores MVC y REST API
├── dto/             Objetos de transferencia de datos
├── model/           Entidades JPA
├── repository/      Repositorios Spring Data JPA
├── security/        Filtro JWT, servicio y configuracion de seguridad
└── service/         Logica de negocio

src/main/resources/
├── static/
│   ├── css/         Hojas de estilo
│   ├── img/         Logos de la aplicacion
│   └── js/          JavaScript del lado del cliente
├── templates/       Plantillas Thymeleaf
└── application.properties
```

## Requisitos

- Java 21+
- PostgreSQL 15+
- Maven 3.9+

## Instalacion

1. Crear la base de datos:
```sql
CREATE DATABASE odontologia_db;
```

2. Configurar las variables de entorno (o usar los valores por defecto para desarrollo):
```bash
export DB_PASSWORD=tu_password_db
export JWT_SECRET=tu_secreto_jwt_en_base64
export SMTP_USERNAME=tu_correo@gmail.com
export SMTP_PASSWORD=tu_app_password
export DRIVE_FOLDER_ID=id_de_carpeta_en_google_drive
export DRIVE_CREDENTIALS=/ruta/a/credentials.json
```

3. Ejecutar la aplicacion:
```bash
./mvnw spring-boot:run
```

4. Acceder en `http://localhost:8080`

## Datos Iniciales

Al ejecutar por primera vez, la aplicacion crea automaticamente:
- Un usuario administrador por defecto
- El catalogo de servicios dentales con 46 servicios en 6 categorias
