USE sigiost;
-- Inserción de SUCURSALES
INSERT INTO sucursal (nombre, localidad, direccion, telefono, activa) VALUES
('Oberá Casa Central', 'Oberá', 'Av. Libertad 1234', '3755-12345', TRUE),
('Wanda', 'Wanda', 'Ruta 19 Km 45', '3757-23456', TRUE),
('San Pedro', 'San Pedro', 'Calle Principal 890', '3751-34567', TRUE),
('Eldorado', 'Eldorado', 'Av. San Martín 567', '3751-45678', TRUE),
('Ituzaingó', 'Ituzaingó', 'Bv. Costanero 123', '3772-56789', TRUE);

-- Inserción de USUARIOS
INSERT INTO usuario (username, password_hash, nombre_completo, rol, email, activo) VALUES
('jperez', '$2y$10$abcdefghijklmnopqrstuv', 'Juan Pérez', 'OPERADOR_CX', 'jperez@obercom.com.ar', TRUE),
('mgarcia', '$2y$10$abcdefghijklmnopqrstuv', 'María García', 'COORDINADOR_CAT', 'mgarcia@obercom.com.ar', TRUE),
('lrodriguez', '$2y$10$abcdefghijklmnopqrstuv', 'Luis Rodríguez', 'TECNICO', 'lrodriguez@obercom.com.ar', TRUE),
('alopez', '$2y$10$abcdefghijklmnopqrstuv', 'Ana López', 'ENCARGADO_DEPOSITO', 'alopez@obercom.com.ar', TRUE),
('cmartinez', '$2y$10$abcdefghijklmnopqrstuv', 'Carlos Martínez', 'SUPERVISOR', 'cmartinez@obercom.com.ar', TRUE),
('rdiaz', '$2y$10$abcdefghijklmnopqrstuv', 'Roberto Díaz', 'OPERADOR_CX', 'rdiaz@obercom.com.ar', TRUE),
('sfernandez', '$2y$10$abcdefghijklmnopqrstuv', 'Silvia Fernández', 'COORDINADOR_CAT', 'sfernandez@obercom.com.ar', TRUE);

-- Inserción de CLIENTES
INSERT INTO cliente (nombre, apellido, direccion, localidad, telefono, email) VALUES
('Carlos', 'González', 'Calle San Martín 456', 'Oberá', '3755-111111', 'cgonzalez@email.com'),
('Laura', 'Martínez', 'Av. Sarmiento 789', 'Wanda', '3757-222222', 'lmartinez@email.com'),
('Pedro', 'Sánchez', 'Ruta 14 Km 23', 'San Pedro', '3751-333333', 'psanchez@email.com'),
('Ana', 'Romero', 'Bv. Belgrano 321', 'Eldorado', '3751-444444', 'aromero@email.com'),
('Miguel', 'Torres', 'Calle Mitre 654', 'Ituzaingó', '3772-555555', 'mtorres@email.com'),
('Sofía', 'Ramírez', 'Av. 9 de Julio 987', 'Oberá', '3755-666666', 'sramirez@email.com'),
('Diego', 'Flores', 'Calle Rivadavia 234', 'Wanda', '3757-777777', NULL),
('Patricia', 'Castro', 'Av. Libertador 567', 'San Pedro', '3751-888888', 'pcastro@email.com');

-- Inserción de TÉCNICOS
INSERT INTO tecnico (id_sucursal, nombre_completo, especializacion, telefono, email, activo) VALUES
(1, 'Roberto Gómez', 'FTTH', '3755-100001', 'rgomez@obercom.com.ar', TRUE),
(1, 'Marcelo Silva', 'GENERAL', '3755-100002', 'msilva@obercom.com.ar', TRUE),
(2, 'Fernando Ortiz', 'FTTH', '3757-100003', 'fortiz@obercom.com.ar', TRUE),
(3, 'Gustavo Morales', 'NETWORKING', '3751-100004', 'gmorales@obercom.com.ar', TRUE),
(4, 'Ricardo Vargas', 'FTTH', '3751-100005', 'rvargas@obercom.com.ar', TRUE),
(5, 'Alejandro Ruiz', 'WIRELESS', '3772-100006', 'aruiz@obercom.com.ar', TRUE),
(1, 'Jorge Medina', 'GENERAL', '3755-100007', 'jmedina@obercom.com.ar', TRUE);

-- Inserción de TICKETS
INSERT INTO ticket (id_cliente, id_usuario_registro, descripcion_problema, prioridad, estado, area_destino) VALUES
(1, 1, 'Cliente reporta sin servicio de internet desde ayer por la tarde', 'ALTA', 'CONVERTIDO', 'CAT'),
(2, 1, 'Solicita instalación de segundo punto de red en dormitorio', 'MEDIA', 'CONVERTIDO', 'CAT'),
(3, 6, 'Velocidad degradada, solo alcanza 50Mbps de los 100Mbps contratados', 'MEDIA', 'PENDIENTE', 'CAT'),
(4, 1, 'ONU con luz roja constante, sin link', 'URGENTE', 'CONVERTIDO', 'CAT'),
(5, 6, 'Cliente quiere cambiar de plan, consulta por opciones', 'BAJA', 'RESUELTO_DIRECTO', 'COMERCIAL'),
(6, 1, 'Problemas intermitentes de conexión durante horario nocturno', 'MEDIA', 'PENDIENTE', 'CAT'),
(7, 6, 'Solicita factura del último mes', 'BAJA', 'RESUELTO_DIRECTO', 'COMERCIAL'),
(8, 1, 'Cliente solicita instalación completa nueva', 'MEDIA', 'CONVERTIDO', 'CAT');

-- Inserción de ÓRDENES DE SERVICIO
INSERT INTO orden_servicio (numero_orden, id_ticket, id_tecnico, id_sucursal, id_usuario_creacion, tipo_servicio, prioridad, estado_actual, fecha_coordinada, observaciones, fecha_creacion) VALUES
('ORD-2026-0001', 1, 1, 1, 2, 'REPARACION', 'ALTA', 'CERRADA', '2026-05-09 14:00:00', 'Verificar OLT y ONT', '2026-05-09 13:30:00'),
('ORD-2026-0002', 2, 3, 2, 2, 'INSTALACION', 'MEDIA', 'ASIGNADA', '2026-05-12 10:00:00', 'Llevar 20m de cable UTP cat6', '2026-05-10 09:00:00'),
('ORD-2026-0003', 4, 2, 1, 7, 'CAMBIO_EQUIPO', 'URGENTE', 'EN_CAMINO', '2026-05-11 09:30:00', 'Llevar ONU de repuesto', '2026-05-11 08:00:00'),
('ORD-2026-0004', 8, NULL, 5, 7, 'INSTALACION', 'MEDIA', 'SIN_ASIGNAR', NULL, 'Instalación completa FTTH con router mesh', '2026-05-13 11:00:00');

-- Actualizar fecha de cierre de orden cerrada
UPDATE orden_servicio SET fecha_cierre = '2026-05-09 16:45:00', id_usuario_cierre = 2, resolucion_aplicada = 'Se detectó fibra cortada en poste, se realizó empalme de emergencia. Cliente con servicio restablecido.' WHERE id_orden = 1;

-- Inserción de ITEMS DE INVENTARIO
INSERT INTO item_inventario (codigo, nombre, descripcion, categoria, unidad_medida) VALUES
('ONU-HUAWEI-HG8310M', 'ONU Huawei HG8310M', 'Optical Network Unit GPON', 'ONU', 'unidad'),
('ONU-ZTE-F660', 'ONU ZTE F660', 'Optical Network Unit GPON con WiFi', 'ONU', 'unidad'),
('CABLE-UTP-CAT6', 'Cable UTP Cat 6', 'Cable de red categoría 6 interior', 'CABLE', 'metro'),
('CONECTOR-SC-APC', 'Conector SC/APC', 'Conector fibra óptica monomodo', 'CONECTOR', 'unidad'),
('MESH-TP-LINK-DECO', 'Router Mesh TP-Link Deco M4', 'Sistema mesh WiFi', 'MESH', 'unidad'),
('PIGTAIL-SC-APC', 'Pigtail SC/APC', 'Pigtail fibra monomodo 1.5m', 'CABLE', 'unidad'),
('FUSIONADORA', 'Fusionadora de fibra óptica', 'Equipo para empalmes', 'HERRAMIENTA', 'unidad'),
('OTDR', 'OTDR', 'Medidor de reflectometría óptica', 'HERRAMIENTA', 'unidad');

-- Inserción de STOCK
INSERT INTO stock (id_item, id_sucursal, cantidad_disponible, stock_minimo) VALUES
(1, 1, 45, 10), (1, 2, 20, 8), (1, 3, 15, 8), (1, 4, 10, 5), (1, 5, 8, 5),
(2, 1, 30, 10), (2, 2, 12, 5), (2, 3, 10, 5), (2, 4, 8, 5), (2, 5, 5, 3),
(3, 1, 500, 100), (3, 2, 200, 50), (3, 3, 150, 50), (3, 4, 100, 30), (3, 5, 80, 30),
(4, 1, 150, 30), (4, 2, 60, 20), (4, 3, 40, 20), (4, 4, 30, 15), (4, 5, 25, 15),
(5, 1, 20, 5), (5, 2, 8, 3), (5, 3, 6, 3), (5, 4, 5, 2), (5, 5, 4, 2),
(6, 1, 100, 20), (6, 2, 40, 15), (6, 3, 30, 15), (6, 4, 25, 10), (6, 5, 20, 10),
(7, 1, 2, 1), (7, 3, 1, 1),
(8, 1, 1, 1);

-- Inserción de USO DE EQUIPAMIENTO (para orden cerrada)
INSERT INTO uso_equipamiento (id_orden, id_item, cantidad_utilizada) VALUES
(1, 6, 2),  -- Usó 2 pigtails
(1, 4, 2);  -- Usó 2 conectores SC/APC

-- Actualizar stock tras uso (simulando descuento automático)
UPDATE stock SET cantidad_disponible = cantidad_disponible - 2 WHERE id_sucursal = 1 AND id_item = 6;
UPDATE stock SET cantidad_disponible = cantidad_disponible - 2 WHERE id_sucursal = 1 AND id_item = 4;

-- Inserción de HISTÓRICO DE ESTADOS
INSERT INTO historico_estados (id_orden, estado_anterior, estado_nuevo, id_usuario, observaciones) VALUES
(1, NULL, 'SIN_ASIGNAR', 2, 'Orden creada desde ticket urgente'),
(1, 'SIN_ASIGNAR', 'ASIGNADA', 2, 'Asignada a Roberto Gómez para atención inmediata'),
(1, 'ASIGNADA', 'EN_CAMINO', 3, 'Técnico salió hacia domicilio del cliente'),
(1, 'EN_CAMINO', 'EN_SITIO', 3, 'Técnico arribó al domicilio'),
(1, 'EN_SITIO', 'RESUELTA', 3, 'Problema identificado y solucionado'),
(1, 'RESUELTA', 'PENDIENTE_AUDITORIA', 3, 'Esperando validación de CAT'),
(1, 'PENDIENTE_AUDITORIA', 'CERRADA', 2, 'Auditado y aprobado'),
(2, NULL, 'SIN_ASIGNAR', 2, 'Orden creada'),
(2, 'SIN_ASIGNAR', 'ASIGNADA', 2, 'Asignada a Fernando Ortiz'),
(3, NULL, 'SIN_ASIGNAR', 7, 'Orden urgente creada'),
(3, 'SIN_ASIGNAR', 'ASIGNADA', 7, 'Asignada a Marcelo Silva'),
(3, 'ASIGNADA', 'EN_CAMINO', 3, 'Técnico en ruta'),
(4, NULL, 'SIN_ASIGNAR', 7, 'Orden creada');