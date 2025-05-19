create table if not exists administrador
(
    id                       binary(16)   not null
        primary key,
    contrasena               varchar(255) not null,
    cuenta_bloqueada         bit          not null,
    email                    varchar(255) not null,
    habilitado               bit          not null,
    intentos_fallidos        int          not null,
    remember_me_token        varchar(100) null,
    remember_me_token_expiry datetime(6)  null,
    sesiones_totales         int          not null,
    tiempo_hasta_desbloqueo  datetime(6)  null,
    constraint UKh121ki47maojpkmvdvqf87ybo
        unique (email)
);

create table if not exists categoria
(
    id     binary(16)   not null
        primary key,
    nombre varchar(255) not null,
    constraint UK35t4wyxqrevf09uwx9e9p6o75
        unique (nombre)
);

create table if not exists departamento
(
    id          binary(16)   not null
        primary key,
    codigo      varchar(255) not null,
    nombre_dept varchar(255) not null,
    constraint UK9jlcjlhaj4otdbp486m8g5hph
        unique (codigo),
    constraint UKrhsa96mdjomt6f1say02xs50w
        unique (nombre_dept)
);

create table if not exists entidad_bancaria
(
    id             binary(16)   not null
        primary key,
    nombre_entidad varchar(255) null
);

create table if not exists cuenta_corriente
(
    id                  binary(16)   not null
        primary key,
    numero_cuenta       varchar(255) not null,
    entidad_bancaria_id binary(16)   not null,
    constraint FKa4eivvcgc7rdl36pwmgdy7l4q
        foreign key (entidad_bancaria_id) references entidad_bancaria (id)
);

create table if not exists especialidades_empleado
(
    id           binary(16)   not null
        primary key,
    especialidad varchar(255) null
);

create table if not exists etiqueta
(
    id     binary(16)   not null
        primary key,
    nombre varchar(100) not null,
    constraint UK18uj0p0ffr26vm9hc4voiwy3j
        unique (nombre)
);

create table if not exists genero
(
    codigo_genero varchar(1)   not null
        primary key,
    genero        varchar(255) null
);

create table if not exists pais
(
    cod_pais     varchar(2)   not null
        primary key,
    nombre_pais  varchar(255) null,
    prefijo_pais varchar(255) null,
    constraint UK7ksojttl4hutfrtnvldhm9e79
        unique (nombre_pais),
    constraint UKfsmfk6mcka7ydtym86m4cjehe
        unique (prefijo_pais)
);

create table if not exists parametros
(
    id    binary(16)   not null
        primary key,
    clave varchar(255) not null,
    valor varchar(255) null,
    constraint UKcd37ujnt9hn2nby8df1b9j1cb
        unique (clave)
);

create table if not exists proveedor
(
    id     binary(16)   not null
        primary key,
    nombre varchar(255) not null,
    constraint UKg641hatkb0jcqqjqu4g5non1w
        unique (nombre)
);

create table if not exists producto
(
    id                binary(16)     not null
        primary key,
    descripcion       varchar(255)   not null,
    es_perecedero     bit            null,
    fecha_alta        datetime(6)    not null,
    fecha_fabricacion date           null,
    marca             varchar(255)   null,
    precio            decimal(10, 2) not null,
    unidades          int            not null,
    valoracion        double         null,
    proveedor_id      binary(16)     null,
    constraint FK_PRODUCTO_PROVEEDOR
        foreign key (proveedor_id) references proveedor (id)
);

create table if not exists producto_categoria
(
    producto_id  binary(16) not null,
    categoria_id binary(16) not null,
    constraint FKh5teore15e6b0uytoftnh3qnd
        foreign key (producto_id) references producto (id),
    constraint FKioqw5isra2i5o5qybtege3sgt
        foreign key (categoria_id) references categoria (id)
);

create table if not exists producto_libro
(
    autor          varchar(255) null,
    editorial      varchar(255) null,
    numero_paginas int          null,
    segunda_mano   bit          null,
    tapa           varchar(255) null,
    titulo         varchar(255) not null,
    id             binary(16)   not null
        primary key,
    constraint FKe05eakkvkibncde9ad2i9c7r3
        foreign key (id) references producto (id)
);

create table if not exists producto_mueble
(
    dimensiones_alto     double     null,
    dimensiones_ancho    double     null,
    dimensiones_profundo double     null,
    peso                 double     null,
    id                   binary(16) not null
        primary key,
    constraint FKftwa0ia7ji4x57gea7qet0elu
        foreign key (id) references producto (id)
);

create table if not exists producto_mueble_colores
(
    mueble_id binary(16)   not null,
    color     varchar(255) null,
    constraint FKgyoqj1cknk4f8n5mc5rv0hwdp
        foreign key (mueble_id) references producto_mueble (id)
);

create table if not exists producto_ropa
(
    talla varchar(255) null,
    id    binary(16)   not null
        primary key,
    constraint FK1wm40rulhr0bg3gvyt1y0i6o
        foreign key (id) references producto (id)
);

create table if not exists producto_ropa_colores
(
    ropa_id binary(16)   not null,
    color   varchar(255) null,
    constraint FKtmxedmsn01l67k26pnjclxbnu
        foreign key (ropa_id) references producto_ropa (id)
);

create table if not exists tipo_documento
(
    id           binary(16) not null
        primary key,
    cod_tipo_doc varchar(3) null
);

create table if not exists documentodni
(
    id                          binary(16)   not null
        primary key,
    numero_documento            varchar(255) null,
    tipo_documento_cod_tipo_doc binary(16)   null,
    constraint FKgcossknusj7aw5llhnv13mdfo
        foreign key (tipo_documento_cod_tipo_doc) references tipo_documento (id)
);

create table if not exists tipo_tarjeta_credito
(
    id                  binary(16)   not null
        primary key,
    nombre_tipo_tarjeta varchar(255) not null,
    constraint UKn9jtkkadxobbn9adm0bjd2kk4
        unique (nombre_tipo_tarjeta)
);

create table if not exists tipo_via
(
    id       binary(16)   not null
        primary key,
    tipo_via varchar(255) null
);

create table if not exists usuario
(
    id                       binary(16)   not null
        primary key,
    contrasena               varchar(255) not null,
    cuenta_bloqueada         bit          not null,
    email                    varchar(255) not null,
    habilitado               bit          not null,
    intentos_fallidos        int          not null,
    motivo_bloqueo           varchar(500) null,
    remember_me_token        varchar(100) null,
    remember_me_token_expiry datetime(6)  null,
    sesiones_totales         int          not null,
    tiempo_hasta_desbloqueo  datetime(6)  null,
    constraint UK5171l57faosmj8myawaucatdw
        unique (email)
);

create table if not exists empleado
(
    id                          binary(16)   not null
        primary key,
    acepta_informacion          bit          null,
    apellidos                   varchar(255) not null,
    archivo_nombre_original     varchar(255) null,
    comentarios                 varchar(255) null,
    cod_postal                  varchar(255) null,
    localidad                   varchar(255) null,
    nombre_via                  varchar(255) null,
    numero                      int          null,
    planta                      int          null,
    portal                      varchar(255) null,
    puerta                      varchar(255) null,
    region                      varchar(255) null,
    edad                        int          null,
    fecha_alta_en_bd            date         null,
    fecha_nacimiento            date         not null,
    fotografia                  longblob     null,
    nombre                      varchar(255) not null,
    numero_documento            varchar(255) null,
    prefijo_internacional_telf  varchar(255) null,
    telf_movil                  varchar(255) null,
    cuenta_corriente_id         binary(16)   null,
    departamento_id             binary(16)   null,
    tipo_via_direccion_ppal_id  binary(16)   null,
    codigo_genero               varchar(1)   not null,
    jefe_id                     binary(16)   null,
    pais_nacimiento             varchar(2)   null,
    tipo_documento_cod_tipo_doc binary(16)   null,
    activo                      bit          not null,
    version                     int          null,
    constraint UKnasrrxma712u3u9pitig6ec4v
        unique (numero_documento),
    constraint FK1vh081x7velnqbx2u80u81jh4
        foreign key (jefe_id) references empleado (id),
    constraint FK21ddfrduanruae8nw4y1gbfw
        foreign key (id) references usuario (id),
    constraint FK_EMPLEADO_GENERO
        foreign key (codigo_genero) references genero (codigo_genero),
    constraint FK_PAIS_NACIMIENTO_EMPLEADO
        foreign key (pais_nacimiento) references pais (cod_pais),
    constraint FK_TIPOVIA_DIRECCION
        foreign key (tipo_via_direccion_ppal_id) references tipo_via (id),
    constraint FK_TIPO_DOCUMENTO_EMPLEADO
        foreign key (tipo_documento_cod_tipo_doc) references tipo_documento (id),
    constraint FKhdjjhohpyjsfta5g6p8b8e00i
        foreign key (departamento_id) references departamento (id),
    constraint FKk92y6ipj9g5q56h7fd2u2hyto
        foreign key (cuenta_corriente_id) references cuenta_corriente (id)
);

create table if not exists colaboracion
(
    id                        binary(16)                               not null
        primary key,
    estado                    enum ('ACTIVA', 'BLOQUEADA', 'INACTIVA') null,
    fecha_bloqueo_cancelacion datetime(6)                              null,
    fecha_bloqueo_rechazo     datetime(6)                              null,
    empleado_a_id             binary(16)                               not null,
    empleado_b_id             binary(16)                               not null,
    constraint FK_COLABORACION_EMPLEADO_A
        foreign key (empleado_a_id) references empleado (id),
    constraint FK_COLABORACION_EMPLEADO_B
        foreign key (empleado_b_id) references empleado (id)
);

create table if not exists datos_economicos
(
    comision               double       null,
    salario                double       null,
    anio_caducidad         varchar(255) null,
    cvc                    varchar(255) null,
    mes_caducidad          varchar(255) null,
    numero_tarjeta_credito varchar(255) null,
    empleados_id           binary(16)   not null
        primary key,
    tipo_tarjeta_id        binary(16)   not null,
    constraint FKe5dwkyj27i297y31av045ayr3
        foreign key (tipo_tarjeta_id) references tipo_tarjeta_credito (id),
    constraint FKrowqbmel208ld5ljcq47t1hy0
        foreign key (empleados_id) references empleado (id)
);

create table if not exists empleado_especialidades_empleado
(
    empleado_id                binary(16) not null,
    especialidades_empleado_id binary(16) not null,
    constraint FK7tvr0plv5nehi1mxfkk48ay7x
        foreign key (especialidades_empleado_id) references especialidades_empleado (id),
    constraint FK8xl3n1lcrueybc37k21xtf50h
        foreign key (empleado_id) references empleado (id)
);

create table if not exists empleado_etiqueta
(
    empleado_id binary(16) not null,
    etiqueta_id binary(16) not null,
    primary key (empleado_id, etiqueta_id),
    constraint UK4a8fgbdxr41d794nhg5m4fhpp
        unique (etiqueta_id),
    constraint FK7ym2ojyt9kk9ihh6vf9s7eyfr
        foreign key (empleado_id) references empleado (id),
    constraint FKocgirs83o20ec3bwfxubkyqdr
        foreign key (etiqueta_id) references etiqueta (id)
);

create table if not exists mensaje
(
    id              binary(16)   not null
        primary key,
    fecha_emision   datetime(6)  null,
    mensaje         varchar(255) not null,
    colaboracion_id binary(16)   not null,
    emisor_id       binary(16)   not null,
    respuesta_a_id  binary(16)   null,
    receptor_id     binary(16)   not null,
    constraint FK_MENSAJE_COLABORACION
        foreign key (colaboracion_id) references colaboracion (id),
    constraint FK_MENSAJE_EMISOR
        foreign key (emisor_id) references empleado (id),
    constraint FK_MENSAJE_RECEPTOR
        foreign key (receptor_id) references empleado (id),
    constraint FK_MENSAJE_RESPUESTA
        foreign key (respuesta_a_id) references mensaje (id)
);

create table if not exists nomina
(
    id                               binary(16)   not null
        primary key,
    fecha_fin_nomina                 date         not null,
    fecha_inicio_nomina              date         not null,
    numero_seguridad_social_empleado varchar(255) null,
    puesto_empleado_nomina           varchar(255) null,
    empleado_id                      binary(16)   not null,
    constraint FKm8493swspthispthv1lqbup06
        foreign key (empleado_id) references empleado (id)
);

create table if not exists linea_nomina
(
    id         binary(16)   not null
        primary key,
    cantidad   double       null,
    concepto   varchar(255) null,
    porcentaje double       null,
    nomina_id  binary(16)   not null,
    constraint FK5h4scqcp21ddd1jjnsuwm5op0
        foreign key (nomina_id) references nomina (id)
);

create table if not exists notificaciones
(
    id_notificacion       binary(16)    not null
        primary key,
    fecha_hora_creacion   datetime(6)   not null,
    id_referencia_entidad binary(16)    null,
    leida                 bit           not null,
    mensaje               varchar(1000) not null,
    tipo_evento           varchar(100)  null,
    url_referencia        varchar(255)  null,
    empleado_destino_id   binary(16)    not null,
    constraint FK_NOTIFICACION_EMPLEADO_DEST
        foreign key (empleado_destino_id) references empleado (id)
);

create table if not exists periodo_colaboracion
(
    id                 binary(16)  not null
        primary key,
    fecha_fin          datetime(6) null,
    fecha_inicio       datetime(6) null,
    colaboracion_id    binary(16)  not null,
    empleado_fin_id    binary(16)  null,
    empleado_inicio_id binary(16)  not null,
    constraint FK_PERIODO_COLABORACION
        foreign key (colaboracion_id) references colaboracion (id),
    constraint FK_PERIODO_EMPLEADO_FIN
        foreign key (empleado_fin_id) references empleado (id),
    constraint FK_PERIODO_EMPLEADO_INICIO
        foreign key (empleado_inicio_id) references empleado (id)
);

create table if not exists solicitud_colaboracion
(
    id                   binary(16)                                  not null
        primary key,
    estado               enum ('ACEPTADA', 'PENDIENTE', 'RECHAZADA') null,
    fecha_solicitud      datetime(6)                                 null,
    mensaje              varchar(255)                                null,
    colaboracion_id      binary(16)                                  null,
    empleado_emisor_id   binary(16)                                  not null,
    empleado_receptor_id binary(16)                                  not null,
    constraint FK_SOLICITUD_COLABORACION
        foreign key (colaboracion_id) references colaboracion (id),
    constraint FK_SOLICITUD_EMISOR
        foreign key (empleado_emisor_id) references empleado (id),
    constraint FK_SOLICITUD_RECEPTOR
        foreign key (empleado_receptor_id) references empleado (id)
);

