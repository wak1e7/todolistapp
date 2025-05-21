# TodoListApp

Aplicación de gestión de tareas (To-do list)

---

## 📋 Tecnologías

- **Java 21**  
- **Spring Boot 3.4.5**  
  - spring-boot-starter-web  
  - spring-boot-starter-data-jpa  
  - spring-boot-starter-security  
  - spring-boot-starter-validation  
  - spring-boot-devtools
- **MySQL 8**  
- **JJWT 0.11.5**  
- **Maven**
- **Mockito**
- **Lombok**

---

## ⚙️ Requisitos Previos

1. Java 21 o superior  
2. Maven  
3. MySQL 8 en ejecución  
4. Git  

---

## 🗄️ Creación de la base de datos

Conéctate a tu servidor MySQL y ejecuta:

```sql
-- 1) Crear la base de datos
CREATE DATABASE IF NOT EXISTS todolistdb
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE todolistdb;

-- 2) Tabla de usuarios
CREATE TABLE IF NOT EXISTS users (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  username   VARCHAR(50)  NOT NULL UNIQUE,
  email      VARCHAR(100) NOT NULL UNIQUE,
  password   VARCHAR(255) NOT NULL,
  role       VARCHAR(20)  NOT NULL,
  created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 3) Tabla de tareas (todos)
CREATE TABLE IF NOT EXISTS todos (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  title       VARCHAR(100) NOT NULL,
  description TEXT,
  completed   BOOLEAN      NOT NULL DEFAULT FALSE,
  created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  DATETIME     NULL ON UPDATE CURRENT_TIMESTAMP,
  user_id     BIGINT       NOT NULL,
  CONSTRAINT fk_todo_user FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE
) ENGINE=InnoDB;
```

---

## 🔧 Configuración local

1. **Clonar el repositorio**  
   ```bash
   git clone https://github.com/wak1e7/todolistapp.git
   cd todolistapp
   ```

2. **Ajusta los valores a tu entorno(application.properties)**
   ```bash
   --- DATASOURCE MySQL ---
   spring.datasource.url=jdbc:mysql://localhost:3306/todolistdb?useSSL=false&serverTimezone=UTC
   spring.datasource.username=TU_USUARIO
   spring.datasource.password=TU_CONTRASEÑA
    
   --- JPA / Hibernate ---
   spring.jpa.show-sql=true
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.properties.hibernate.format_sql=true
    
   --- JWT ---
   jwt.secret=<TU_CLAVE_SECRETA_BASE64>
   jwt.expirationMs=86400000
    
   --- PUERTO---
   server.port=8080
   ```

3. **Compilar y ejecutar**
   ```bash
   mvnw clean install
   mvnw spring-boot:run
   ```

---

## 🛠️ Endpoints principales

Todos los endpoints van prefijados con /api

| Método | Ruta    | Descripción         | Roles |
|:------ |:--------|:--------------------|:------|
| POST | `/auth/register` |Registrar nuevo usuario | 👤 cualquiera |
| POST | `/auth/login` | Login y recibe JWT | 👤 cualquiera |
| PUT | `/auth/promote{usuario}` | Promuever usuario a ADMIN | 🛡️ ADMIN |
| DELETE | `/auth/delete/{id}` | Eliminar usuario | 🛡️ ADMIN |
| POST | `/todos` | Crear nueva tarea | 👤 USER+ADMIN |
| GET | `/todos` | Listar todas las tareas del usuario | 👤 USER+ADMIN |
| GET | `/todos/pending` | Listar solo tareas pendientes | 👤 USER+ADMIN |
| GET | `/todos/{id}` | Obtener tarea por ID | 👤 USER+ADMIN |
| PUT | `/todos/{id}` | Reemplazar tarea | 🛡️ ADMIN |
| PATCH | `/todos/{id}?completed=` | 	Marcar completada o no | 👤 USER+ADMIN |
| DELETE | `/todos/{id}` | 	Eliminar tarea | 🛡️ ADMIN |

## 📄 Licencia

[![MIT License](https://img.shields.io/badge/License-MIT-green.svg)](https://choosealicense.com/licenses/mit/)
