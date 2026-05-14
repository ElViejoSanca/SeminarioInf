-- Creación de la DB
CREATE DATABASE sigiost;

-- Eliminación de tablas existentes para recreación limpia
DROP TABLE IF EXISTS historico_estados;
DROP TABLE IF EXISTS uso_equipamiento;
DROP TABLE IF EXISTS stock;
DROP TABLE IF EXISTS item_inventario;
DROP TABLE IF EXISTS orden_servicio;
DROP TABLE IF EXISTS tecnico;
DROP TABLE IF EXISTS ticket;
DROP TABLE IF EXISTS cliente;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS sucursal;

-- Creación de tabla SUCURSAL
CREATE TABLE sucursal (
    id_sucursal INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL,
    localidad VARCHAR(100) NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    telefono VARCHAR(20),
    activa BOOLEAN DEFAULT TRUE,
    INDEX idx_nombre (nombre),
    INDEX idx_localidad (localidad)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Creación de tabla USUARIO
CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(200) NOT NULL,
    rol ENUM('OPERADOR_CX','COORDINADOR_CAT','TECNICO','ENCARGADO_DEPOSITO','SUPERVISOR') NOT NULL,
    email VARCHAR(100) NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ultimo_acceso TIMESTAMP NULL,
    INDEX idx_username (username),
    INDEX idx_rol (rol)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Creación de tabla CLIENTE
CREATE TABLE cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    localidad VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    fecha_alta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_apellido_nombre (apellido, nombre),
    INDEX idx_localidad (localidad)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Creación de tabla TECNICO
CREATE TABLE tecnico (
    id_tecnico INT AUTO_INCREMENT PRIMARY KEY,
    id_sucursal INT NOT NULL,
    nombre_completo VARCHAR(200) NOT NULL,
    especializacion ENUM('GENERAL','FTTH','WIRELESS','NETWORKING') DEFAULT 'GENERAL',
    telefono VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    activo BOOLEAN DEFAULT TRUE,
    fecha_alta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_sucursal) REFERENCES sucursal(id_sucursal) ON UPDATE CASCADE,
    INDEX idx_sucursal (id_sucursal),
    INDEX idx_activo (activo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Creación de tabla TICKET
CREATE TABLE ticket (
    id_ticket INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_usuario_registro INT NOT NULL,
    descripcion_problema TEXT NOT NULL,
    prioridad ENUM('BAJA','MEDIA','ALTA','URGENTE') DEFAULT 'MEDIA',
    estado ENUM('PENDIENTE','CONVERTIDO','RESUELTO_DIRECTO') DEFAULT 'PENDIENTE',
    area_destino ENUM('CAT','NOC','COMERCIAL') DEFAULT 'CAT',
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario_registro) REFERENCES usuario(id_usuario),
    INDEX idx_estado (estado),
    INDEX idx_cliente (id_cliente),
    INDEX idx_fecha_registro (fecha_registro)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Creación de tabla ORDEN_SERVICIO
CREATE TABLE orden_servicio (
    id_orden INT AUTO_INCREMENT PRIMARY KEY,
    numero_orden VARCHAR(20) UNIQUE NOT NULL,
    id_ticket INT UNIQUE NOT NULL,
    id_tecnico INT,
    id_sucursal INT NOT NULL,
    id_usuario_creacion INT NOT NULL,
    id_usuario_cierre INT,
    tipo_servicio ENUM('INSTALACION','REPARACION','CAMBIO_EQUIPO','MANTENIMIENTO') NOT NULL,
    prioridad ENUM('BAJA','MEDIA','ALTA','URGENTE') NOT NULL,
    estado_actual ENUM('SIN_ASIGNAR','ASIGNADA','CONFIRMADA','EN_CAMINO','EN_SITIO','RESUELTA','PENDIENTE_AUDITORIA','CERRADA','POSPUESTA') DEFAULT 'SIN_ASIGNAR',
    fecha_coordinada DATETIME,
    observaciones TEXT,
    resolucion_aplicada TEXT,
    motivo_postergacion VARCHAR(200),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_cierre TIMESTAMP NULL,
    FOREIGN KEY (id_ticket) REFERENCES ticket(id_ticket) ON DELETE CASCADE,
    FOREIGN KEY (id_tecnico) REFERENCES tecnico(id_tecnico) ON UPDATE CASCADE ON DELETE SET NULL,
    FOREIGN KEY (id_sucursal) REFERENCES sucursal(id_sucursal) ON UPDATE CASCADE,
    FOREIGN KEY (id_usuario_creacion) REFERENCES usuario(id_usuario),
    FOREIGN KEY (id_usuario_cierre) REFERENCES usuario(id_usuario),
    INDEX idx_numero_orden (numero_orden),
    INDEX idx_estado (estado_actual),
    INDEX idx_tecnico (id_tecnico),
    INDEX idx_sucursal (id_sucursal),
    INDEX idx_fecha_coordinada (fecha_coordinada)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Creación de tabla ITEM_INVENTARIO
CREATE TABLE item_inventario (
    id_item INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    categoria ENUM('ONU','CABLE','CONECTOR','MESH','HERRAMIENTA','OTRO') NOT NULL,
    unidad_medida VARCHAR(20) DEFAULT 'unidad',
    INDEX idx_codigo (codigo),
    INDEX idx_categoria (categoria)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Creación de tabla STOCK
CREATE TABLE stock (
    id_stock INT AUTO_INCREMENT PRIMARY KEY,
    id_item INT NOT NULL,
    id_sucursal INT NOT NULL,
    cantidad_disponible INT DEFAULT 0 CHECK (cantidad_disponible >= 0),
    stock_minimo INT DEFAULT 5,
    ultima_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_item) REFERENCES item_inventario(id_item) ON DELETE CASCADE,
    FOREIGN KEY (id_sucursal) REFERENCES sucursal(id_sucursal) ON UPDATE CASCADE,
    UNIQUE KEY unique_item_sucursal (id_item, id_sucursal),
    INDEX idx_sucursal (id_sucursal),
    INDEX idx_stock_bajo (cantidad_disponible, stock_minimo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Creación de tabla USO_EQUIPAMIENTO
CREATE TABLE uso_equipamiento (
    id_uso INT AUTO_INCREMENT PRIMARY KEY,
    id_orden INT NOT NULL,
    id_item INT NOT NULL,
    cantidad_utilizada INT NOT NULL CHECK (cantidad_utilizada > 0),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_orden) REFERENCES orden_servicio(id_orden) ON DELETE CASCADE,
    FOREIGN KEY (id_item) REFERENCES item_inventario(id_item),
    INDEX idx_orden (id_orden),
    INDEX idx_item (id_item)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Creación de tabla HISTORICO_ESTADOS
CREATE TABLE historico_estados (
    id_historico INT AUTO_INCREMENT PRIMARY KEY,
    id_orden INT NOT NULL,
    estado_anterior VARCHAR(50),
    estado_nuevo VARCHAR(50) NOT NULL,
    id_usuario INT NOT NULL,
    observaciones TEXT,
    fecha_cambio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_orden) REFERENCES orden_servicio(id_orden) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
    INDEX idx_orden (id_orden),
    INDEX idx_fecha (fecha_cambio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;