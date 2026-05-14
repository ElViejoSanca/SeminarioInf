USE sigiost;
-- Consulta 1: Listar órdenes pendientes con información completa del cliente y técnico asignado
SELECT 
    o.numero_orden,
    o.tipo_servicio,
    o.prioridad,
    o.estado_actual,
    CONCAT(c.nombre, ' ', c.apellido) AS cliente,
    c.direccion AS direccion_cliente,
    c.telefono AS telefono_cliente,
    t.nombre_completo AS tecnico_asignado,
    t.telefono AS telefono_tecnico,
    s.nombre AS sucursal,
    o.fecha_coordinada,
    o.observaciones
FROM orden_servicio o
INNER JOIN ticket tk ON o.id_ticket = tk.id_ticket
INNER JOIN cliente c ON tk.id_cliente = c.id_cliente
LEFT JOIN tecnico t ON o.id_tecnico = t.id_tecnico
INNER JOIN sucursal s ON o.id_sucursal = s.id_sucursal
WHERE o.estado_actual IN ('ASIGNADA', 'CONFIRMADA', 'EN_CAMINO', 'EN_SITIO')
ORDER BY 
    FIELD(o.prioridad, 'URGENTE', 'ALTA', 'MEDIA', 'BAJA'),
    o.fecha_coordinada ASC;
    
-- Consulta 2: Calcular tiempo promedio de resolución por tipo de servicio
SELECT 
    tipo_servicio,
    COUNT(*) AS total_ordenes_cerradas,
    ROUND(AVG(TIMESTAMPDIFF(HOUR, fecha_creacion, fecha_cierre)), 2) AS promedio_horas_resolucion,
    MIN(TIMESTAMPDIFF(HOUR, fecha_creacion, fecha_cierre)) AS tiempo_minimo_horas,
    MAX(TIMESTAMPDIFF(HOUR, fecha_creacion, fecha_cierre)) AS tiempo_maximo_horas
FROM orden_servicio
WHERE estado_actual = 'CERRADA' AND fecha_cierre IS NOT NULL
GROUP BY tipo_servicio
ORDER BY promedio_horas_resolucion ASC;

-- Consulta 3: Técnicos con mayor carga de trabajo actual
SELECT 
    t.nombre_completo,
    t.especializacion,
    s.nombre AS sucursal_base,
    COUNT(o.id_orden) AS ordenes_activas,
    SUM(CASE WHEN o.prioridad = 'URGENTE' THEN 1 ELSE 0 END) AS urgentes,
    SUM(CASE WHEN o.prioridad = 'ALTA' THEN 1 ELSE 0 END) AS altas
FROM tecnico t
INNER JOIN sucursal s ON t.id_sucursal = s.id_sucursal
LEFT JOIN orden_servicio o ON t.id_tecnico = o.id_tecnico 
    AND o.estado_actual IN ('ASIGNADA', 'CONFIRMADA', 'EN_CAMINO', 'EN_SITIO')
WHERE t.activo = TRUE
GROUP BY t.id_tecnico, t.nombre_completo, t.especializacion, s.nombre
ORDER BY ordenes_activas DESC, urgentes DESC;

-- Consulta 4: Items de inventario con stock por debajo del mínimo
SELECT 
    i.codigo,
    i.nombre,
    s.nombre AS sucursal,
    st.cantidad_disponible,
    st.stock_minimo,
    (st.stock_minimo - st.cantidad_disponible) AS deficit,
    st.ultima_actualizacion
FROM stock st
INNER JOIN item_inventario i ON st.id_item = i.id_item
INNER JOIN sucursal s ON st.id_sucursal = s.id_sucursal
WHERE st.cantidad_disponible < st.stock_minimo
ORDER BY deficit DESC, s.nombre, i.nombre;

-- Consulta 5: Historial completo de una orden específica
SELECT 
    he.fecha_cambio,
    he.estado_anterior,
    he.estado_nuevo,
    u.nombre_completo AS usuario,
    u.rol,
    he.observaciones
FROM historico_estados he
INNER JOIN usuario u ON he.id_usuario = u.id_usuario
WHERE he.id_orden = 1
ORDER BY he.fecha_cambio ASC;

-- Consulta 6: Dashboard de órdenes por estado y sucursal
SELECT 
    s.nombre AS sucursal,
    COUNT(CASE WHEN o.estado_actual = 'SIN_ASIGNAR' THEN 1 END) AS sin_asignar,
    COUNT(CASE WHEN o.estado_actual = 'ASIGNADA' THEN 1 END) AS asignadas,
    COUNT(CASE WHEN o.estado_actual IN ('EN_CAMINO','EN_SITIO') THEN 1 END) AS en_ejecucion,
    COUNT(CASE WHEN o.estado_actual = 'PENDIENTE_AUDITORIA' THEN 1 END) AS pendiente_auditoria,
    COUNT(CASE WHEN o.estado_actual = 'CERRADA' THEN 1 END) AS cerradas,
    COUNT(CASE WHEN o.estado_actual = 'POSPUESTA' THEN 1 END) AS pospuestas,
    COUNT(*) AS total
FROM sucursal s
LEFT JOIN orden_servicio o ON s.id_sucursal = o.id_sucursal
WHERE s.activa = TRUE
GROUP BY s.id_sucursal, s.nombre
ORDER BY s.nombre;

-- Consulta 7: Equipamiento más utilizado en el último mes
SELECT 
    i.codigo,
    i.nombre,
    i.categoria,
    COUNT(ue.id_uso) AS veces_utilizado,
    SUM(ue.cantidad_utilizada) AS cantidad_total_consumida
FROM uso_equipamiento ue
INNER JOIN item_inventario i ON ue.id_item = i.id_item
WHERE ue.fecha_registro >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH)
GROUP BY i.id_item, i.codigo, i.nombre, i.categoria
ORDER BY cantidad_total_consumida DESC
LIMIT 10;

-- Consulta 8: Eliminar (cancelar) una orden no asignada
-- Primero eliminar registros dependientes del histórico
DELETE FROM historico_estados WHERE id_orden = 4;

-- Luego actualizar el ticket a estado PENDIENTE
UPDATE ticket SET estado = 'PENDIENTE' WHERE id_ticket = 8;

-- Finalmente eliminar la orden
DELETE FROM orden_servicio WHERE id_orden = 4 AND estado_actual = 'SIN_ASIGNAR';