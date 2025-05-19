# Usa una imagen base de Tomcat con JDK 17 (o la versión que uses)
FROM tomcat:latest

# Argumento para el nombre del WAR (por si necesitas cambiarlo al construir)
ARG WAR_FILE=build/libs/empleados.war

# Copia el archivo WAR a la carpeta webapps de Tomcat.
# La aplicación será accesible en /empleados
COPY ${WAR_FILE} /usr/local/tomcat/webapps/empleados.war

# El puerto 8080 de Tomcat ya está expuesto por la imagen base.
# Las variables de entorno para Spring Boot se pasarán desde docker-compose.
