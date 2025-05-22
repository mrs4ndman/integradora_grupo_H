INSERT IGNORE INTO genero (codigo_genero, genero)
VALUES ('M', 'Masculino'),
       ('F', 'Femenino'),
       ('O', 'Otro');


INSERT IGNORE INTO pais (cod_pais, nombre_pais, prefijo_pais)
VALUES ('ES', 'España', '+34'),
       ('FR', 'Francia', '+33'),
       ('US', 'Estados Unidos', '+1'),
       ('IT', 'Italia', '+39'),
       ('AN', 'Andorra', '+376');


SET @tipo_via_calle = UUID_TO_BIN(UUID());
SET @tipo_via_avenida = UUID_TO_BIN(UUID());
SET @tipo_via_paseo = UUID_TO_BIN(UUID());
SET @tipo_via_pasaje = UUID_TO_BIN(UUID());
INSERT IGNORE INTO tipo_via (id, tipo_via)
VALUES (@tipo_via_calle, 'Calle'),
       (@tipo_via_avenida, 'Avenida'),
       (@tipo_via_paseo, 'Paseo'),
       (@tipo_via_pasaje, 'Pasaje');


SET @tipo_doc_dni = UUID_TO_BIN(UUID());
SET @tipo_doc_nie = UUID_TO_BIN(UUID());
INSERT IGNORE INTO tipo_documento (id, cod_tipo_doc)
VALUES (@tipo_doc_dni, 'DNI'),
       (@tipo_doc_nie, 'NIE');


SET @banco_santander = UUID_TO_BIN(UUID());
SET @bbva = UUID_TO_BIN(UUID());
SET @caixabank = UUID_TO_BIN(UUID());
SET @sabadell = UUID_TO_BIN(UUID());

-- Insertar las entidades bancarias usando las variables
INSERT IGNORE INTO entidad_bancaria (id, nombre_entidad)
VALUES (@banco_santander, 'Banco Santander'),
       (@bbva, 'Banco Bilbao Vizcaya Argentaria'),
       (@caixabank, 'Caixabank'),
       (@sabadell, 'Sabadell');


INSERT IGNORE INTO parametros (id, clave, valor)
VALUES (UUID_TO_BIN(UUID()), 'MAX_INTENTOS_FALLIDOS', '3'),
       (UUID_TO_BIN(UUID()), 'DURACION_BLOQUEO', '15'),
       (UUID_TO_BIN(UUID()), 'DURACION_BLOQUEO_ADMIN', '1440'),
       (UUID_TO_BIN(UUID()), 'NOMBRE_EMPRESA', 'ICR | Programacion'),
       (UUID_TO_BIN(UUID()), 'CIF_EMPRESA', 'C48012645'),
       (UUID_TO_BIN(UUID()), 'DIRECCION_EMPRESA', 'C/ Padre Claret, 8 - Madrid, 28002');



SET @departamento_admin = UUID_TO_BIN(UUID());
SET @departamento_prep = UUID_TO_BIN(UUID());
SET @departamento_almac = UUID_TO_BIN(UUID());
SET @departamento_recep = UUID_TO_BIN(UUID());
SET @departamento_venta = UUID_TO_BIN(UUID());

INSERT IGNORE INTO departamento (id, codigo, nombre_dept)
VALUES (@departamento_admin, 'ADMIN', 'Administración'),
       (@departamento_prep, 'PREP', 'Preparación de Pedidos'),
       (@departamento_almac, 'ALMAC', 'Almacenaje'),
       (@departamento_recep, 'RECEP', 'Recepción de Mercancías'),
       (@departamento_venta, 'VENTA', 'Ventas');


SET @especialidad_inventarios = UUID_TO_BIN(UUID());
SET @especialidad_it = UUID_TO_BIN(UUID());
SET @especialidad_logistica = UUID_TO_BIN(UUID());

INSERT IGNORE INTO especialidades_empleado (id, especialidad)
VALUES (@especialidad_inventarios, 'Especialista en Gestión de Inventarios'),
       (@especialidad_it, 'Especialista en IT'),
       (@especialidad_logistica, 'Especialista en Logística');



SET @tarjeta_visa = UUID_TO_BIN(UUID());
SET @tarjeta_amex = UUID_TO_BIN(UUID());
SET @tarjeta_mastercard = UUID_TO_BIN(UUID());

INSERT IGNORE INTO tipo_tarjeta_credito (id, nombre_tipo_tarjeta)
VALUES (@tarjeta_visa, 'VISA'),
       (@tarjeta_amex, 'AMERICAN EXPRESS'),
       (@tarjeta_mastercard, 'MASTER CARD');


-- --- Usuarios + Empleados (Tabla: usuario + empleados) ---

SET @uuid_usuario_empleado1_str = '11111111-1111-1111-1111-111111111111';
SET @uuid_usuario_empleado2_str = '22222222-2222-2222-2222-222222222222';
SET @uuid_usuario_empleado3_str = '33333333-3333-3333-3333-333333333333';
SET @uuid_usuario_empleado4_str = '44444444-4444-4444-4444-444444444444';
-- SET @uuid_usuario_empleado5_str = '55555555-5555-5555-5555-555555555555';

-- Conversión a binario
SET @uuid_usuario_empleado1 = UUID_TO_BIN(@uuid_usuario_empleado1_str);
SET @uuid_usuario_empleado2 = UUID_TO_BIN(@uuid_usuario_empleado2_str);
SET @uuid_usuario_empleado3 = UUID_TO_BIN(@uuid_usuario_empleado3_str);
SET @uuid_usuario_empleado4 = UUID_TO_BIN(@uuid_usuario_empleado4_str);
-- SET @uuid_usuario_empleado5 = UUID_TO_BIN(@uuid_usuario_empleado5_str);


-- Id Cuentas Corrientes
SET @uuid_cuenta_corriente1 = UUID_TO_BIN(UUID());
SET @uuid_cuenta_corriente2 = UUID_TO_BIN(UUID());
SET @uuid_cuenta_corriente3 = UUID_TO_BIN(UUID());
SET @uuid_cuenta_corriente4 = UUID_TO_BIN(UUID());

-- Generación de cuenta corriente

-- Inserción de Cuenta Corriente 1
INSERT INTO cuenta_corriente (id, numero_cuenta, entidad_bancaria_id)
VALUES (@uuid_cuenta_corriente1, 'ES7620770024003102571111', @sabadell);
-- Relacionado con BBVA

-- Inserción de Cuenta Corriente 2
INSERT INTO cuenta_corriente (id, numero_cuenta, entidad_bancaria_id)
VALUES (@uuid_cuenta_corriente2, 'ES7620770024003102572222', @sabadell);
-- Relacionado con BBVA

-- Inserción de Cuenta Corriente 3
INSERT INTO cuenta_corriente (id, numero_cuenta, entidad_bancaria_id)
VALUES (@uuid_cuenta_corriente3, 'ES7620770024003102573333', @sabadell);
-- Relacionado con BBVA

-- Inserción de Cuenta Corriente 4
INSERT INTO cuenta_corriente (id, numero_cuenta, entidad_bancaria_id)
VALUES (@uuid_cuenta_corriente4, 'ES7620770024003102573333', @sabadell);
-- Relacionado con BBVA

-- Insercción Datos Empleados
INSERT INTO usuario (id, email, contrasena, cuenta_bloqueada, habilitado,
                     intentos_fallidos, sesiones_totales, motivo_bloqueo, tiempo_hasta_desbloqueo)
VALUES (@uuid_usuario_empleado1,
        'juan2.perez@example.com',
        '{noop}1234',
        FALSE, TRUE, 0, 0,
        NULL, NULL);

-- Empleado 1
INSERT INTO empleado (id, nombre, apellidos, fotografia, codigo_genero,
                      fecha_nacimiento, edad, pais_nacimiento, comentarios,
                      tipo_documento_cod_tipo_doc, numero_documento, prefijo_internacional_telf, telf_movil,
                      tipo_via_direccion_ppal_id, nombre_via, numero, portal, planta, puerta,
                      localidad, region, cod_postal, archivo_nombre_original,
                      departamento_id, fecha_alta_en_BD, jefe_id,
                      acepta_informacion, cuenta_corriente_id, version)
VALUES (@uuid_usuario_empleado1,
        'Juan', 'Pérez García', NULL, 'M',
        '1980-07-22', 44, 'ES', 'Empleado experimentado en desarrollo de software.',
        @tipo_doc_dni, '12345678A', '+34', '600112233',
        @tipo_via_calle, 'Calle Mayor', 5, 'B', 1, 'Derecha',
        'Madrid', 'Comunidad de Madrid', '28001', 'cv_juan_perez.pdf',
        @departamento_admin, CURRENT_DATE, NULL, true,@uuid_cuenta_corriente1, 0);

-- Datos Económicos para Empleado 1



-- Inserción de Datos Económicos 1
INSERT INTO datos_economicos (empleados_id, salario, comision, tipo_tarjeta_id, numero_tarjeta_credito, cvc,
                              mes_caducidad, anio_caducidad)
VALUES (@uuid_usuario_empleado1,
        2500.00, -- Salario
        500.00, -- Comisión-- Relación con la cuenta corriente
        @tarjeta_visa, -- Relación con tipo de tarjeta VISA
        '4111111111111111', -- Número de tarjeta ficticia
        '123', -- CVV
        12, -- Mes de caducidad
        2025);


-- Usuario 2
INSERT INTO usuario (id, email, contrasena, cuenta_bloqueada, habilitado,
                     intentos_fallidos, sesiones_totales, motivo_bloqueo, tiempo_hasta_desbloqueo)
VALUES (@uuid_usuario_empleado2,
        'maria.lopez@example.com',
        '{noop}5678',
        FALSE, TRUE, 0, 0,
        NULL, NULL);

-- Empleado 2
INSERT INTO empleado (id, nombre, apellidos, fotografia, codigo_genero,
                      fecha_nacimiento, edad, pais_nacimiento, comentarios,
                      tipo_documento_cod_tipo_doc, numero_documento, prefijo_internacional_telf, telf_movil,
                      tipo_via_direccion_ppal_id, nombre_via, numero, portal, planta, puerta,
                      localidad, region, cod_postal, archivo_nombre_original,
                      departamento_id, fecha_alta_en_BD, jefe_id,
                      acepta_informacion, cuenta_corriente_id, version)
VALUES (@uuid_usuario_empleado2,
        'María', 'López Fernández', NULL, 'F',
        '1990-10-15', 34, 'ES', 'Especialista en marketing digital.',
        @tipo_doc_dni, '17654321B', '+34', '600112234',
        @tipo_via_avenida, 'Avenida de la Paz', 18, 'C', 2, 'Derecha',
        'Madrid', 'Comunidad de Madrid', '28002', 'cv_maria_lopez.pdf',
        @departamento_admin, CURRENT_DATE, NULL, true, @uuid_cuenta_corriente2,0);


-- Datos Económicos para Empleado 2

-- Inserción de Datos Económicos 2
INSERT INTO datos_economicos (empleados_id, salario, comision, tipo_tarjeta_id, numero_tarjeta_credito, cvc,
                              mes_caducidad, anio_caducidad)
VALUES (@uuid_usuario_empleado2,
        2500.00, -- Salario
        500.00, -- Comisión-- Relación con la cuenta corriente
        @tarjeta_mastercard, -- Relación con tipo de tarjeta VISA
        '4111111111112222', -- Número de tarjeta ficticia
        '321', -- CVV
        02, -- Mes de caducidad
        2027);


-- Usuario 3
INSERT INTO usuario (id, email, contrasena, cuenta_bloqueada, habilitado,
                     intentos_fallidos, sesiones_totales, motivo_bloqueo, tiempo_hasta_desbloqueo)
VALUES (@uuid_usuario_empleado3,
        'pedro.sanchez@example.com',
        '{noop}91011',
        FALSE, TRUE, 0, 0,
        NULL, NULL);


-- Empleado 3
INSERT INTO empleado (id, nombre, apellidos, fotografia, codigo_genero,
                      fecha_nacimiento, edad, pais_nacimiento, comentarios,
                      tipo_documento_cod_tipo_doc, numero_documento, prefijo_internacional_telf, telf_movil,
                      tipo_via_direccion_ppal_id, nombre_via, numero, portal, planta, puerta,
                      localidad, region, cod_postal, archivo_nombre_original,
                      departamento_id, fecha_alta_en_BD, jefe_id,
                      acepta_informacion, cuenta_corriente_id, version)
VALUES (@uuid_usuario_empleado3,
        'Pedro', 'Sánchez Jiménez', NULL, 'M',
        '1985-03-25', 39, 'ES', 'Gerente de recursos humanos.',
        @tipo_doc_dni, '11223344C', '+34', '600112235',
        @tipo_via_paseo, 'Paseo de la Castellana', 23, 'D', 3, 'Izquierda',
        'Madrid', 'Comunidad de Madrid', '28003', 'cv_pedro_sanchez.pdf',
        @departamento_venta, CURRENT_DATE, NULL, true, @uuid_cuenta_corriente3,0);

-- Datos Económicos para Empleado 3


-- Inserción de Datos Económicos 3
INSERT INTO datos_economicos (empleados_id, salario, comision, tipo_tarjeta_id, numero_tarjeta_credito, cvc,
                              mes_caducidad, anio_caducidad)
VALUES (@uuid_usuario_empleado3,
        7500.00, -- Salario
        300.00, -- Comisión-- Relación con la cuenta corriente
        @tarjeta_amex, -- Relación con tipo de tarjeta VISA
        '4111111111113333', -- Número de tarjeta ficticia
        '333', -- CVV
        03, -- Mes de caducidad
        2026);


-- Inserción de Usuario 4
INSERT INTO usuario (id, email, contrasena, cuenta_bloqueada, habilitado,
                     intentos_fallidos, sesiones_totales, motivo_bloqueo, tiempo_hasta_desbloqueo)
VALUES (@uuid_usuario_empleado4,
        'luisa.martinez@example.com',
        '{noop}abcd1234',
        FALSE, TRUE, 0, 0,
        NULL, NULL);


-- Inserción de Empleado 4
INSERT INTO empleado (id, nombre, apellidos, fotografia, codigo_genero,
                      fecha_nacimiento, edad, pais_nacimiento, comentarios,
                      tipo_documento_cod_tipo_doc, numero_documento, prefijo_internacional_telf, telf_movil,
                      tipo_via_direccion_ppal_id, nombre_via, numero, portal, planta, puerta,
                      localidad, region, cod_postal, archivo_nombre_original,
                      departamento_id, fecha_alta_en_BD, jefe_id,
                      acepta_informacion, cuenta_corriente_id, version)
VALUES (@uuid_usuario_empleado4,
        'Luisa', 'Martinez Sánchez', NULL, 'F',
        '1990-05-14', 34, 'ES', 'Especialista en desarrollo front-end.',
        @tipo_doc_dni, '87684321B', '+34', '612345678',
        @tipo_via_avenida, 'Avenida de la Constitución', 10, 'A', 2, 'Izquierda',
        'Sevilla', 'Andalucía', '41001', 'cv_maria_lopez.pdf',
        @departamento_prep, CURRENT_DATE, @uuid_usuario_empleado1, true, @uuid_cuenta_corriente4,0);

-- Datos Económicos para Empleado 4



-- Inserción de Datos Económicos 4
INSERT INTO datos_economicos (empleados_id, salario, comision, tipo_tarjeta_id, numero_tarjeta_credito, cvc,
                              mes_caducidad, anio_caducidad)
VALUES (@uuid_usuario_empleado4,
        7500.00, -- Salario
        300.00, -- Comisión-- Relación con la cuenta corriente
        @tarjeta_amex, -- Relación con tipo de tarjeta VISA
        '4111111111114444', -- Número de tarjeta ficticia
        '444', -- CVV
        03, -- Mes de caducidad
        2026);



-- --- Administradores (Tabla: administrador) ---
-- Contraseñas prehasheadas
INSERT IGNORE INTO administrador (id, email, contrasena, cuenta_bloqueada, habilitado, intentos_fallidos,
                                  sesiones_totales)
VALUES (UUID_TO_BIN(UUID()), 'pablo@pablo.com', '$2a$12$XwAqGxngyhnVs99FLU4H..a7aKgS.n1TouDZf2lfCfcwmD6Q2/vYi', false,
        true, 0, 0),
       (UUID_TO_BIN(UUID()), 'josea@josea.com', '$2a$10$GrWiwpWzkrHyoP.ihYPdOebrL74G0E87LS0RAEBWJpRfDw1YU.mym', false,
        true, 0, 0),
       (UUID_TO_BIN(UUID()), 'juanm@juanm.com', '$2a$12$4cmQWV6S/lSBnVuCrIIETOjSdi/0iP/d/Pfu0rlM79HYeD.L2kF7u', false,
        true, 0, 0);

-- --- Usuarios (Tabla: usuario) ---
-- Contraseñas pre-hasheadas
INSERT IGNORE INTO usuario (id, email, contrasena, cuenta_bloqueada, habilitado, intentos_fallidos, sesiones_totales,
                            motivo_bloqueo, tiempo_hasta_desbloqueo)
VALUES (UUID_TO_BIN(UUID()), 'juanm@juanm.com', '$2a$12$4cmQWV6S/lSBnVuCrIIETOjSdi/0iP/d/Pfu0rlM79HYeD.L2kF7u', true,
        true, 0, 0, 'BLOQUEO POR DEFECTO', DATE_ADD(NOW(), INTERVAL 1 DAY)),
       (UUID_TO_BIN(UUID()), 'juanito@juanito.com', '$2a$12$OvflzQl3QYhhDlUOOKA...tsv2y4oQTyBiiUJ605n6AO/jFSu.XFm',
        true, true, 0, 0, 'BLOQUEO POR DEFECTO', DATE_ADD(NOW(), INTERVAL 2 DAY));


-- --- Proveedores (Tabla: proveedor) ---
INSERT IGNORE INTO proveedor (id, nombre)
VALUES (UUID_TO_BIN(UUID()), 'TecnoHogar S.A.'),
       (UUID_TO_BIN(UUID()), 'Lecturas Milenio'),
       (UUID_TO_BIN(UUID()), 'Productalia'),
       (UUID_TO_BIN(UUID()), 'Acme'),
       (UUID_TO_BIN(UUID()), 'Papeleria');


