# -- ========================================================================
# -- Script de creación de tablas (schema.sql)
# -- Define las tablas ANTES de que data.sql inserte los datos.
# -- ========================================================================
#
# DROP TABLE IF EXISTS producto_ropa_colores;
# DROP TABLE IF EXISTS producto_mueble_colores;
# DROP TABLE IF EXISTS empleado_etiqueta;
# DROP TABLE IF EXISTS empleado_especialidades_empleado;
# DROP TABLE IF EXISTS tarjeta_credito;
# DROP TABLE IF EXISTS datos_economicos;
# DROP TABLE IF EXISTS cuenta_corriente;
# DROP TABLE IF EXISTS entidad_bancaria;
# DROP TABLE IF EXISTS mensaje;
# DROP TABLE IF EXISTS colaboracion;
# DROP TABLE IF EXISTS producto_libro;
# DROP TABLE IF EXISTS producto_mueble;
# DROP TABLE IF EXISTS producto_ropa;
# DROP TABLE IF EXISTS producto;
# DROP TABLE IF EXISTS proveedor;
# DROP TABLE IF EXISTS categoria;
# DROP TABLE IF EXISTS administrador;
# DROP TABLE IF EXISTS empleado;
# DROP TABLE IF EXISTS usuario;
# DROP TABLE IF EXISTS documentodni;
# DROP TABLE IF EXISTS especialidades_empleado;
# DROP TABLE IF EXISTS departamento;
# DROP TABLE IF EXISTS etiqueta;
# DROP TABLE IF EXISTS genero;
# DROP TABLE IF EXISTS pais;
# DROP TABLE IF EXISTS parametros;
# DROP TABLE IF EXISTS periodo_colaboracion;
# DROP TABLE IF EXISTS tipo_tarjeta;
# DROP TABLE IF EXISTS tipo_documento;
# DROP TABLE IF EXISTS tipo_via;
#
#
# -- --- Creación de Tablas ---
#
# -- Tablas sin dependencias (o solo dependencias básicas)
# CREATE TABLE genero (
#                         codigo_genero VARCHAR(1) NOT NULL,
#                         genero VARCHAR(255),
#                         PRIMARY KEY (codigo_genero)
# ) ENGINE=InnoDB;
#
# CREATE TABLE pais (
#                       cod_pais VARCHAR(2) NOT NULL,
#                       nombre_pais VARCHAR(255) UNIQUE,
#                       prefijo_pais VARCHAR(255) UNIQUE,
#                       PRIMARY KEY (cod_pais)
# ) ENGINE=InnoDB;
#
# CREATE TABLE tipo_via (
#                           id VARCHAR(255) NOT NULL,
#                           tipo_via VARCHAR(255),
#                           PRIMARY KEY (id)
# ) ENGINE=InnoDB;
#
# CREATE TABLE tipo_documento (
#                                 id BINARY(16) NOT NULL,
#                                 cod_tipo_doc VARCHAR(3) UNIQUE,
#                                 PRIMARY KEY (id)
# ) ENGINE=InnoDB;
#
# CREATE TABLE parametros (
#                             id BINARY(16) NOT NULL,
#                             clave VARCHAR(255) NOT NULL UNIQUE,
#                             valor VARCHAR(255),
#                             PRIMARY KEY (id)
# ) ENGINE=InnoDB;
#
# CREATE TABLE departamento (
#                               id BINARY(16) NOT NULL,
#                               codigo VARCHAR(255) NOT NULL UNIQUE,
#                               nombre_dept VARCHAR(255) NOT NULL UNIQUE,
#                               PRIMARY KEY (id)
# ) ENGINE=InnoDB;
#
# CREATE TABLE especialidades_empleado (
#                                          id BINARY(16) NOT NULL,
#                                          especialidad VARCHAR(255) NOT NULL,
#                                          PRIMARY KEY (id)
# ) ENGINE=InnoDB;
#
# CREATE TABLE tipo_tarjeta (
#                               id BIGINT NOT NULL AUTO_INCREMENT,
#                               descripcion VARCHAR(255),
#                               nombre VARCHAR(255) NOT NULL UNIQUE,
#                               PRIMARY KEY (id)
# ) ENGINE=InnoDB;
#
# CREATE TABLE etiqueta (
#                           id BINARY(16) NOT NULL,
#                           nombre VARCHAR(100) NOT NULL UNIQUE,
#                           PRIMARY KEY (id)
# ) ENGINE=InnoDB;
#
# CREATE TABLE periodo_colaboracion (
#                                       id BINARY(16) NOT NULL,
#     -- otros campos si los tiene tu entidad PeriodoColaboracion
#                                       PRIMARY KEY (id)
# ) ENGINE=InnoDB;
#
# CREATE TABLE proveedor (
#                            id BINARY(16) NOT NULL,
#                            nombre VARCHAR(255) NOT NULL UNIQUE,
#                            PRIMARY KEY (id)
# ) ENGINE=InnoDB;
#
# CREATE TABLE categoria (
#                            id BINARY(16) NOT NULL,
#                            nombre VARCHAR(255) NOT NULL UNIQUE,
#                            PRIMARY KEY (id)
# ) ENGINE=InnoDB;
#
# -- Tablas con dependencias
# CREATE TABLE entidad_bancaria (
#                                   id BIGINT NOT NULL AUTO_INCREMENT,
#                                   codigo_entidad VARCHAR(4) NOT NULL UNIQUE,
#                                   nombre VARCHAR(255) NOT NULL,
#                                   siglas VARCHAR(255),
#                                   pais_id VARCHAR(2),
#                                   PRIMARY KEY (id),
#                                   CONSTRAINT FK_EntidadBancaria_Pais FOREIGN KEY (pais_id) REFERENCES pais (cod_pais)
# ) ENGINE=InnoDB;
#
# CREATE TABLE cuenta_corriente (
#                                   id BIGINT NOT NULL AUTO_INCREMENT,
#                                   entidad_bancaria_id BIGINT NOT NULL,
#                                   iban VARCHAR(255) NOT NULL UNIQUE,
#                                   numero_cuenta VARCHAR(255) NOT NULL,
#                                   PRIMARY KEY (id),
#                                   CONSTRAINT FK_CuentaCorriente_EntidadBancaria FOREIGN KEY (entidad_bancaria_id) REFERENCES entidad_bancaria (id)
# ) ENGINE=InnoDB;
#
# CREATE TABLE administrador (
#                                id BINARY(16) NOT NULL,
#                                email VARCHAR(255) NOT NULL UNIQUE,
#                                contrasena VARCHAR(255) NOT NULL,
#                                cuenta_bloqueada BIT NOT NULL DEFAULT FALSE,
#                                habilitado BIT NOT NULL DEFAULT TRUE,
#                                intentos_fallidos INTEGER NOT NULL DEFAULT 0,
#                                remember_me_token VARCHAR(100),
#                                remember_me_token_expiry DATETIME(6),
#                                sesiones_totales INTEGER NOT NULL DEFAULT 0,
#                                tiempo_hasta_desbloqueo DATETIME(6),
#                                PRIMARY KEY (id)
# ) ENGINE=InnoDB;
#
# CREATE TABLE usuario (
#                          id BINARY(16) NOT NULL,
#                          email VARCHAR(255) NOT NULL UNIQUE,
#                          contrasena VARCHAR(255) NOT NULL,
#                          cuenta_bloqueada BIT NOT NULL DEFAULT FALSE,
#                          habilitado BIT NOT NULL DEFAULT TRUE,
#                          intentos_fallidos INTEGER NOT NULL DEFAULT 0,
#                          remember_me_token VARCHAR(100),
#                          remember_me_token_expiry DATETIME(6),
#                          sesiones_totales INTEGER NOT NULL DEFAULT 0,
#                          tiempo_hasta_desbloqueo DATETIME(6),
#                          motivo_bloqueo VARCHAR(255), -- Campo añadido en data.sql
#                          PRIMARY KEY (id)
# ) ENGINE=InnoDB;
#
# CREATE TABLE documentodni (
#                               id BINARY(16) NOT NULL,
#                               numero_documento VARCHAR(255),
#                               tipo_documento_cod_tipo_doc VARCHAR(3),
#                               PRIMARY KEY (id),
#                               CONSTRAINT FK_DocumentoDNI_TipoDocumento FOREIGN KEY (tipo_documento_cod_tipo_doc) REFERENCES tipo_documento (cod_tipo_doc)
# ) ENGINE=InnoDB;
#
# -- Tabla base Producto
# CREATE TABLE producto (
#                           id BINARY(16) NOT NULL,
#                           categoria_id BINARY(16),
#                           proveedor_id BINARY(16),
#                           descripcion VARCHAR(255) NOT NULL, -- Cambiado a TEXT por si son largas, y NOT NULL
#                           es_perecedero BIT,
#                           fecha_alta DATETIME(6) NOT NULL,
#                           fecha_fabricacion DATE,
#                           marca VARCHAR(255),
#                           precio DECIMAL(10,2) NOT NULL,
#                           unidades INTEGER NOT NULL,
#                           valoracion DOUBLE PRECISION,
#                           PRIMARY KEY (id),
#                           CONSTRAINT FK_Producto_Categoria FOREIGN KEY (categoria_id) REFERENCES categoria (id),
#                           CONSTRAINT FK_Producto_Proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedor (id)
# ) ENGINE=InnoDB;
#
# -- Tabla subclase Libro
# CREATE TABLE producto_libro (
#                                 id BINARY(16) NOT NULL, -- PK y FK a producto
#                                 autor VARCHAR(255),
#                                 editorial VARCHAR(255),
#                                 numero_paginas INTEGER,
#                                 segunda_mano BIT,
#                                 tapa VARCHAR(255),
#                                 titulo VARCHAR(255) NOT NULL,
#                                 PRIMARY KEY (id),
#                                 CONSTRAINT FK_Libro_Producto FOREIGN KEY (id) REFERENCES producto (id) ON DELETE CASCADE
# ) ENGINE=InnoDB;
#
# -- Tabla subclase Mueble
# CREATE TABLE producto_mueble (
#                                  id BINARY(16) NOT NULL, -- PK y FK a producto
#                                  dimensiones_alto DOUBLE PRECISION,
#                                  dimensiones_ancho DOUBLE PRECISION,
#                                  dimensiones_profundo DOUBLE PRECISION,
#                                  peso DOUBLE PRECISION,
#                                  PRIMARY KEY (id),
#                                  CONSTRAINT FK_Mueble_Producto FOREIGN KEY (id) REFERENCES producto (id) ON DELETE CASCADE
# ) ENGINE=InnoDB;
#
# -- Tabla de unión para colores de Mueble
# CREATE TABLE producto_mueble_colores (
#                                          mueble_id BINARY(16) NOT NULL,
#                                          color VARCHAR(255),
#                                          CONSTRAINT FK_MuebleColores_Mueble FOREIGN KEY (mueble_id) REFERENCES producto_mueble (id) ON DELETE CASCADE
# ) ENGINE=InnoDB;
#
# -- Tabla subclase Ropa
# CREATE TABLE producto_ropa (
#                                id BINARY(16) NOT NULL, -- PK y FK a producto
#                                material_principal VARCHAR(255),
#                                talla VARCHAR(255),
#                                PRIMARY KEY (id),
#                                CONSTRAINT FK_Ropa_Producto FOREIGN KEY (id) REFERENCES producto (id) ON DELETE CASCADE
# ) ENGINE=InnoDB;
#
# -- Tabla de unión para colores de Ropa
# CREATE TABLE producto_ropa_colores (
#                                        ropa_id BINARY(16) NOT NULL,
#                                        color VARCHAR(255),
#                                        CONSTRAINT FK_RopaColores_Ropa FOREIGN KEY (ropa_id) REFERENCES producto_ropa (id) ON DELETE CASCADE
# ) ENGINE=InnoDB;
#
#
# -- Tablas relacionadas con Empleado (Añadidas para completar según StarterEntities)
# CREATE TABLE empleado (
#                           id BINARY(16) NOT NULL,
#                           acepta_informacion BIT,
#                           apellidos VARCHAR(255) NOT NULL,
#                           archivo_nombre_original VARCHAR(255),
#                           cod_postal VARCHAR(255),
#                           codigo_genero VARCHAR(1) NOT NULL,
#                           comentarios VARCHAR(255),
#                           contenido_documento VARCHAR(255),
#                           departamento_id BINARY(16),
#                           documento_dni_id BINARY(16) UNIQUE, -- Asumiendo relación OneToOne con DocumentoDNI
#                           edad INTEGER,
#                           email VARCHAR(255) NOT NULL UNIQUE, -- Asumiendo que email es único
#                           fecha_alta_en_bd DATE,
#                           fecha_nacimiento DATE NOT NULL,
#                           fotografia LONGBLOB,
#                           jefe_id BINARY(16),
#                           localidad VARCHAR(255),
#                           nombre VARCHAR(255) NOT NULL,
#                           nombre_via VARCHAR(255),
#                           numero INTEGER,
#                           pais_nacimiento VARCHAR(2),
#                           planta INTEGER,
#                           portal VARCHAR(255),
#                           prefijo_internacional_telf VARCHAR(255),
#                           puerta VARCHAR(255),
#                           region VARCHAR(255),
#                           telf_movil VARCHAR(255),
#                           tipo_documento_cod_tipo_doc VARCHAR(3), -- FK a TipoDocumento (redundante si se usa DocumentoDNI?) - Revisar entidad
#                           tipo_via_direccion_ppal_id VARCHAR(255),
#                           usuario_id BINARY(16) UNIQUE, -- Asumiendo relación OneToOne con Usuario
#                           PRIMARY KEY (id),
#                           CONSTRAINT FK_Empleado_Departamento FOREIGN KEY (departamento_id) REFERENCES departamento (id),
#                           CONSTRAINT FK_Empleado_DocumentoDNI FOREIGN KEY (documento_dni_id) REFERENCES documentodni (id), -- Asumiendo relación
#                           CONSTRAINT FK_Empleado_Genero FOREIGN KEY (codigo_genero) REFERENCES genero (codigo_genero),
#                           CONSTRAINT FK_Empleado_Jefe FOREIGN KEY (jefe_id) REFERENCES empleado (id),
#                           CONSTRAINT FK_Empleado_PaisNacimiento FOREIGN KEY (pais_nacimiento) REFERENCES pais (cod_pais),
#                           CONSTRAINT FK_Empleado_TipoDocumento FOREIGN KEY (tipo_documento_cod_tipo_doc) REFERENCES tipo_documento (cod_tipo_doc), -- Revisar necesidad
#                           CONSTRAINT FK_Empleado_TipoVia FOREIGN KEY (tipo_via_direccion_ppal_id) REFERENCES tipo_via (id),
#                           CONSTRAINT FK_Empleado_Usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id)
# ) ENGINE=InnoDB;
#
# CREATE TABLE datos_economicos (
#                                   empleados_id BINARY(16) NOT NULL, -- PK y FK
#                                   comision DOUBLE PRECISION,
#                                   salario DOUBLE PRECISION,
#                                   cuenta_corriente_id BIGINT UNIQUE, -- Asumiendo OneToOne
#                                   PRIMARY KEY (empleados_id),
#                                   CONSTRAINT FK_DatosEconomicos_Empleado FOREIGN KEY (empleados_id) REFERENCES empleado (id) ON DELETE CASCADE,
#                                   CONSTRAINT FK_DatosEconomicos_CuentaCorriente FOREIGN KEY (cuenta_corriente_id) REFERENCES cuenta_corriente (id)
# ) ENGINE=InnoDB;
#
# CREATE TABLE tarjeta_credito (
#                                  empleado_id BINARY(16) NOT NULL, -- Parte de la PK compuesta? O solo FK? Revisar entidad
#                                  numero_tarjeta VARCHAR(255) NOT NULL, -- Parte de la PK compuesta? Revisar entidad
#                                  anio_caducidad VARCHAR(255),
#                                  cvc VARCHAR(3) NOT NULL,
#                                  mes_caducidad VARCHAR(255),
#                                  orden INTEGER,
#                                  tipo_tarjeta_id BIGINT NOT NULL,
#                                  PRIMARY KEY (empleado_id, numero_tarjeta), -- Asumiendo PK compuesta, AJUSTAR SEGÚN ENTIDAD REAL
#                                  CONSTRAINT FK_TarjetaCredito_TipoTarjeta FOREIGN KEY (tipo_tarjeta_id) REFERENCES tipo_tarjeta (id),
#                                  CONSTRAINT FK_TarjetaCredito_Empleado FOREIGN KEY (empleado_id) REFERENCES empleado (id) ON DELETE CASCADE
# ) ENGINE=InnoDB;
#
# CREATE TABLE empleado_especialidades_empleado (
#                                                   empleado_id BINARY(16) NOT NULL,
#                                                   especialidades_empleado_id BINARY(16) NOT NULL,
#                                                   PRIMARY KEY (empleado_id, especialidades_empleado_id), -- Clave primaria compuesta para la tabla de unión ManyToMany
#                                                   CONSTRAINT FK_EmpEsp_Empleado FOREIGN KEY (empleado_id) REFERENCES empleado (id) ON DELETE CASCADE,
#                                                   CONSTRAINT FK_EmpEsp_Especialidad FOREIGN KEY (especialidades_empleado_id) REFERENCES especialidades_empleado (id) ON DELETE CASCADE
# ) ENGINE=InnoDB;
#
# CREATE TABLE empleado_etiqueta (
#                                    empleado_id BINARY(16) NOT NULL,
#                                    etiqueta_id BINARY(16) NOT NULL,
#                                    PRIMARY KEY (empleado_id, etiqueta_id),
#                                    CONSTRAINT FK_EmpEti_Empleado FOREIGN KEY (empleado_id) REFERENCES empleado (id) ON DELETE CASCADE,
#                                    CONSTRAINT FK_EmpEti_Etiqueta FOREIGN KEY (etiqueta_id) REFERENCES etiqueta (id) ON DELETE CASCADE
# ) ENGINE=InnoDB;
#
# -- Tablas de Chat (Añadidas para completar según StarterEntities/estructura)
# CREATE TABLE colaboracion (
#                               id BINARY(16) NOT NULL,
#                               empleado_id BINARY(16),
#     -- otros campos si los tiene tu entidad Colaboracion
#                               PRIMARY KEY (id),
#                               CONSTRAINT FK_Colaboracion_Empleado FOREIGN KEY (empleado_id) REFERENCES empleado (id)
# ) ENGINE=InnoDB;
#
# CREATE TABLE mensaje (
#                          id BINARY(16) NOT NULL,
#                          emisor_id BINARY(16),
#                          receptor_id BINARY(16),
#                          fecha_emision DATETIME(6),
#                          mensaje VARCHAR(255) NOT NULL,
#                          PRIMARY KEY (id),
#                          CONSTRAINT FK_Mensaje_Emisor FOREIGN KEY (emisor_id) REFERENCES empleado (id) ON DELETE SET NULL, -- O CASCADE? Revisar Lógica
#                          CONSTRAINT FK_Mensaje_Receptor FOREIGN KEY (receptor_id) REFERENCES empleado (id) ON DELETE SET NULL -- O CASCADE? Revisar Lógica
# ) ENGINE=InnoDB;
#
#
# -- Añadir aquí CREATE TABLE para cualquier otra entidad que falte...