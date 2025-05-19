# Usa una imagen base de Tomcat con JDK 17 (o la versión que uses)
FROM tomcat:latest

# Argumento para el nombre del WAR
ARG WAR_FILE=build/libs/administracion.war

# Copia el archivo WAR a la carpeta webapps de Tomcat.
# La aplicación será accesible en /administracion
COPY ${WAR_FILE} /usr/local/tomcat/webapps/administracion.war
