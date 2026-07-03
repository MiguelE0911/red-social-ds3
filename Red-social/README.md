# Red Social

Proyecto académico de red social con backend en Java, persistencia en MySQL y generación de reportes en PDF.

## Requisitos previos

Antes de clonar y ejecutar el proyecto, asegúrate de tener instalado:

- **JDK 21** (o superior)
- **Maven** (3.8+)
- **Acceso a las bases de datos** (VPN local y/o servidor en la nube, según corresponda) — pide las credenciales a un integrante del equipo si no las tienes.

## 1. Clonar el repositorio

```bash
git clone https://github.com/<usuario-o-organizacion>/red-social-ds3.git
cd red-social-ds3/Red-social
```

## 2. Configurar las credenciales (obligatorio)

Por seguridad, el archivo `config.properties` **no se sube al repositorio** (está en `.gitignore`). Debes crearlo manualmente antes de compilar o ejecutar el proyecto.

### Pasos:

1. Crea el archivo en la siguiente ruta exacta:
   ```
   Red-social/src/config.properties
   ```
2. Copia y completa la siguiente plantilla con los datos que te compartió el equipo:

   ```properties
   # Servidor local / VPN
   db1.address=jdbc:mysql://<direccion-servidor-local>:3306/<nombre-bd>
   db1.user=<usuario>
   db1.password=<contrasena>

   # Servidor en la nube
   db2.address=jdbc:mysql://<direccion-servidor-nube>:3306/<nombre-bd>
   db2.user=<usuario>
   db2.password=<contrasena>
   ```

> ⚠️ **Importante:** si el archivo no existe o está mal ubicado, el programa lanzará un error al iniciar (`Error cargando configuración`) y no podrá conectarse a la base de datos.

## 3. Compilar el proyecto

Desde la carpeta `Red-social` (donde está el `pom.xml`), abre una terminal y ejecuta:

```bash
mvn clean package
```

Esto genera el JAR ejecutable con todas las dependencias incluidas en:

```
target/Red-social-0.0.1-SNAPSHOT.jar
```

> Si usas Eclipse: importa el proyecto como **Maven Project** (`File → Import → Existing Maven Projects`), asegúrate de que `src/config.properties` exista, y luego corre `Maven build...` con el goal `clean package`.

## 4. Ejecutar la aplicación

**Desde terminal** (recomendado para ver mensajes de conexión y posibles errores en consola):

```bash
java -jar target/Red-social-0.0.1-SNAPSHOT.jar
```

**Desde Eclipse:** click derecho en la clase `Main` (paquete `login_test`) → `Run As → Java Application`.

> Nota: si ejecutas el `.jar` con doble clic en Windows, no verás la consola ni sus mensajes. Para depurar problemas de conexión, siempre ejecútalo desde una terminal.

## Solución de problemas comunes

| Problema | Causa probable |
|---|---|
| `RuntimeException: No se encontró config.properties` | El archivo no existe o no está en `src/config.properties` |
| `NoClassDefFoundError: Could not initialize class login_test.Conexion` | El `config.properties` está mal ubicado o vacío |
| No conecta al servidor local | Necesitas estar conectado a la VPN del servidor local |
| El JAR pesa muy poco (unos KB) | Ejecuta `mvn clean package` desde terminal, no solo desde Eclipse |

## Estructura del proyecto

```
Red-social/
├── src/
│   ├── config.properties      # Credenciales (NO se sube al repo)
│   ├── informe/                # Generación de reportes PDF
│   ├── login_test/             # Lógica de conexión, login y registro
│   └── social/                 # Lógica principal de la red social
├── pom.xml
└── target/                     # Generado al compilar (ignorado por git)
```

## Tecnologías usadas

- Java 21
- Maven
- HikariCP (pool de conexiones)
- MySQL Connector/J
- iText 5 (generación de PDF)
- jBCrypt (hash de contraseñas)
- JGoodies Forms (interfaz gráfica Swing)