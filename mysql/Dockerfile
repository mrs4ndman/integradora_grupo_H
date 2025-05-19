# Usa una imagen base oficial de MySQL (ej. 8.0 o la que necesites)
FROM mysql:latest

# Copia tus scripts de inicialización. Se ejecutarán en orden alfabético.
COPY ./01-schema.sql /docker-entrypoint-initdb.d/01-schema.sql
COPY ./02-master-data.sql /docker-entrypoint-initdb.d/02-master-data.sql

# Las variables de entorno como MYSQL_ROOT_PASSWORD, MYSQL_DATABASE, MYSQL_USER, MYSQL_PASSWORD
# se pasarán desde docker-compose.yaml al ejecutar el contenedor.
