-- ========================================================================
-- Script de inicialización de datos (data.sql) - CON UUIDs ALEATORIOS (MySQL)
-- NOTA: Usa UUID_TO_BIN(UUID()) para generar IDs aleatorios para MySQL.
-- NOTA: Las contraseñas están pre-hasheadas con BCrypt.
-- ========================================================================

-- --- Géneros (Tabla: genero) ---
INSERT IGNORE INTO genero (codigo_genero, genero) VALUES
('M', 'Masculino'),
('F', 'Femenino'),
('O', 'Otro');

-- --- Países (Tabla: pais) ---
INSERT IGNORE INTO pais (cod_pais, nombre_pais, prefijo_pais) VALUES
('ES', 'España', '+34'),
('FR', 'Francia', '+33'),
('US', 'Estados Unidos', '+1'),
('IT', 'Italia', '+39'),
('AN', 'Andorra', '+376');

-- --- Tipos de Vía (Tabla: tipo_via) ---
INSERT IGNORE INTO tipo_via (id, tipo_via) VALUES
(UUID_TO_BIN(UUID()), 'Avenida'),
(UUID_TO_BIN(UUID()), 'Calle'),
(UUID_TO_BIN(UUID()), 'Paseo'),
(UUID_TO_BIN(UUID()), 'Pasaje');

-- --- Tipos de Documento (Tabla: tipo_documento) ---
INSERT IGNORE INTO tipo_documento (id, cod_tipo_doc) VALUES
(UUID_TO_BIN(UUID()), 'DNI'),
(UUID_TO_BIN(UUID()), 'NIE');

-- --- Entidades Bancarias (Tabla: entidad_bancaria) ---
INSERT IGNORE INTO entidad_bancaria (id, nombre_entidad) VALUES
(UUID_TO_BIN(UUID()), 'Banco Santander'),
(UUID_TO_BIN(UUID()), 'Banco Bilbao Vizcaya Argentaria'),
(UUID_TO_BIN(UUID()), 'Caixabank'),
(UUID_TO_BIN(UUID()), 'Sabadell');

-- --- Parámetros (Tabla: parametros) ---
INSERT IGNORE INTO parametros (id, clave, valor) VALUES
(UUID_TO_BIN(UUID()), 'MAX_INTENTOS_FALLIDOS', '3'),
(UUID_TO_BIN(UUID()), 'DURACION_BLOQUEO', '15'),
(UUID_TO_BIN(UUID()), 'DURACION_BLOQUEO_ADMIN', '1440');

-- --- Departamentos (Tabla: departamento) ---
INSERT IGNORE INTO departamento (id, codigo, nombre_dept) VALUES
(UUID_TO_BIN(UUID()), 'PREP', 'Preparación de Pedidos'),
(UUID_TO_BIN(UUID()), 'ALMAC', 'Almacenaje'),
(UUID_TO_BIN(UUID()), 'RECEP', 'Recepción de Mercancías'),
(UUID_TO_BIN(UUID()), 'VENTA', 'Ventas'),
(UUID_TO_BIN(UUID()), 'ADMIN', 'Administración');

-- --- Especialidades Empleado (Tabla: especialidades_empleado) ---
INSERT IGNORE INTO especialidades_empleado (id, especialidad) VALUES
(UUID_TO_BIN(UUID()), 'Especialista en Gestión de Inventarios'),
(UUID_TO_BIN(UUID()), 'Especialista en IT'),
(UUID_TO_BIN(UUID()), 'Especialista en Logística');

-- --- Tipos de Tarjeta (Tabla: tipo_tarjeta) ---
INSERT IGNORE INTO tipo_tarjeta_credito (id, nombre_tipo_tarjeta) VALUES
(UUID_TO_BIN(UUID()), 'VISA'),
(UUID_TO_BIN(UUID()), 'AMERICAN EXPRESS'),
(UUID_TO_BIN(UUID()), 'MASTER CARD');

-- --- Administradores (Tabla: administrador) ---
-- Contraseñas prehasheadas
INSERT IGNORE INTO administrador (id, email, contrasena, cuenta_bloqueada, habilitado, intentos_fallidos, sesiones_totales) VALUES
(UUID_TO_BIN(UUID()), 'pablo@pablo.com', '$2a$12$XwAqGxngyhnVs99FLU4H..a7aKgS.n1TouDZf2lfCfcwmD6Q2/vYi', false, true, 0, 0),
(UUID_TO_BIN(UUID()), 'josea@josea.com', '$2a$12$tRLb3ZhIcwivkpvc.TD1G.jlb05MUyW/XOCczyz14hkmWCVJKLEue', false, true, 0, 0),
(UUID_TO_BIN(UUID()), 'juanm@juanm.com', '$2a$12$4cmQWV6S/lSBnVuCrIIETOjSdi/0iP/d/Pfu0rlM79HYeD.L2kF7u', false, true, 0, 0);

-- --- Usuarios (Tabla: usuario) ---
-- Contraseñas pre-hasheadas
INSERT IGNORE INTO usuario (id, email, contrasena, cuenta_bloqueada, habilitado, intentos_fallidos, sesiones_totales, motivo_bloqueo, tiempo_hasta_desbloqueo) VALUES
(UUID_TO_BIN(UUID()), 'juanm@juanm.com', '$2a$12$4cmQWV6S/lSBnVuCrIIETOjSdi/0iP/d/Pfu0rlM79HYeD.L2kF7u', true, true, 0, 0, 'BLOQUEO POR DEFECTO', DATE_ADD(NOW(), INTERVAL 1 DAY)),
(UUID_TO_BIN(UUID()), 'juanito@juanito.com', '$2a$12$OvflzQl3QYhhDlUOOKA...tsv2y4oQTyBiiUJ605n6AO/jFSu.XFm', true, true, 0, 0, 'BLOQUEO POR DEFECTO', DATE_ADD(NOW(), INTERVAL 2 DAY));

# INSERT INTO empleado (
#     id,
#     usuario_id,                 -- FK a Usuario
#     nombre,
#     apellidos,
#     fotografia,                 -- BLOB, puede ser NULL
#     codigo_genero,              -- FK a Genero (o el valor si es directo)
#     email,
#     fecha_nacimiento,
#     edad,
#     pais_nacimiento,            -- FK a Pais (usa Cod_pais)
#     comentarios,
#     tipo_documento_cod_tipo_doc,-- FK a TipoDocumento (usa id de TipoDocumento)
#     contenido_documento,
#     prefijo_internacional_telf,
#     telf_movil,
#     -- Campos de Direccion (embebido)
#     tipo_via_direccion_ppal_id, -- FK a TipoVia (usa valor de tipoVia, ej: 'CALLE')
#     nombre_via,
#     numero,
#     portal,
#     planta,
#     puerta,
#     localidad,
#     region,
#     cod_postal,
#     -- Fin campos Direccion
#     archivo_nombre_original,
#     departamento_id,            -- FK a Departamento
#     fecha_alta_en_BD,
#     jefe_id                     -- FK a Empleado (para la relación de jefe), puede ser NULL
# ) VALUES (
#              UUID_TO_BIN(UUID()),
#              UUID_TO_BIN(UUID()),
#              'Carlos',
#              'Sánchez Rodríguez',
#              NULL,                       -- O un valor de bytes si tienes la foto en formato adecuado para SQL
#              'MASC',                     -- Reemplaza con el código_genero del Genero real (ej: 'MASC')
#              'carlos.sanchez@example.com',
#              '1985-03-15',
#              39,                         -- Edad, o calcula/actualiza después
#              'ES',                       -- Reemplaza con el Cod_pais del Pais real
#              'Empleado con amplia experiencia en desarrollo backend.',
#              UUID_TO_BIN(UUID()),     -- Reemplaza con el UUID del TipoDocumento real
#              '12345678A',
#              '+34',
#              '600112233',
#              -- Direccion
#              'CALLE',                    -- Reemplaza con el valor de tipoVia real de TipoVia
#              'Gran Vía',
#              10,
#              'A',
#              2,
#              'Izquierda',
#              'Madrid',
#              'Comunidad de Madrid',
#              '28013',
#              -- Fin Direccion
#              'cv_carlos_sanchez.pdf',
#              UUID_TO_BIN(UUID()),   -- Reemplaza con el UUID del Departamento real
#              '2024-05-08',               -- O CURRENT_DATE
#              NULL                        -- No tiene jefe en este ejemplo
#          );

-- --- Proveedores (Tabla: proveedor) ---
INSERT IGNORE INTO proveedor (id, nombre) VALUES
(UUID_TO_BIN(UUID()), 'TecnoHogar S.A.'),
(UUID_TO_BIN(UUID()), 'Lecturas Milenio'),
(UUID_TO_BIN(UUID()), 'Productalia'),
(UUID_TO_BIN(UUID()), 'Acme'),
(UUID_TO_BIN(UUID()), 'Papeleria');

