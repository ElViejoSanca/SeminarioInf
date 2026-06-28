// Main.java
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static SistemaGestion sistema;
    private static Coordinador coordinadorActivo;
    // ID fijo del coordinador activo para registrar en historico
    private static final int ID_COORDINADOR = 1;

    public static void main(String[] args) {
        try {
            sistema = new SistemaGestion();
            coordinadorActivo = new Coordinador("Maria", "Garcia", "3755-99999",
                                                "mgarcia", "cat123", "mgarcia@obercom.com.ar");
            coordinadorActivo.setIdUsuario(ID_COORDINADOR);
        } catch (PersistenciaException e) {
            System.out.println("Error al conectar con la base de datos: " + e.getMessage());
            System.out.println("Verifique que MySQL este corriendo y la base sigiost exista.");
            return;
        }

        System.out.println("================================================");
        System.out.println("  SIGIOST - Sistema de Gestion de Ordenes       ");
        System.out.println("  Obercom - NEA Argentina                        ");
        System.out.println("================================================");

        int opcion = -1;
        do {
            mostrarMenuPrincipal();
            try {
                opcion = scanner.nextInt();
                scanner.nextLine();
                procesarOpcionPrincipal(opcion);
            } catch (InputMismatchException e) {
                System.out.println("Error: ingrese un numero valido.");
                scanner.nextLine();
            }
        } while (opcion != 0);

        System.out.println("Sistema cerrado. Hasta luego.");
        ConexionBD.getInstancia_sinExcepcion();
        scanner.close();
    }

    private static void mostrarMenuPrincipal() {
        System.out.println("\n--- MENU PRINCIPAL ---");
        System.out.println("1. Modulo Tecnicos");
        System.out.println("2. Modulo Ordenes de Servicio");
        System.out.println("3. Modulo Inventario");
        System.out.println("4. Dashboard general");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opcion: ");
    }

    private static void procesarOpcionPrincipal(int opcion) {
        switch (opcion) {
            case 1: menuTecnicos(); break;
            case 2: menuOrdenes(); break;
            case 3: menuInventario(); break;
            case 4: mostrarDashboard(); break;
            case 0: break;
            default: System.out.println("Opcion invalida.");
        }
    }

    // ========================
    // DASHBOARD
    // ========================

    private static void mostrarDashboard() {
        System.out.println("\n--- DASHBOARD GENERAL ---");

        // Arreglo indexado por enum (opcion 2)
        int[] contadores = sistema.contarOrdenesPorEstado();
        EstadoOrden[] estados = EstadoOrden.values();
        System.out.println("Ordenes por estado:");
        for (int i = 0; i < estados.length; i++) {
            if (contadores[i] > 0) {
                System.out.println("  " + estados[i] + ": " + contadores[i]);
            }
        }

        // Arreglo nativo top 3 tecnicos (opcion 4)
        System.out.println("\nTop 3 tecnicos con menor carga:");
        Tecnico[] top = sistema.topTresTecnicosMenorCarga();
        for (int i = 0; i < top.length; i++) {
            System.out.println("  " + (i + 1) + ". " + top[i].getNombreCompleto() +
                               " - Ordenes activas: " + top[i].getOrdenesActivas());
        }

        System.out.println("\nItems con stock bajo:");
        List<ItemInventario> alertas = sistema.obtenerItemsStockBajo();
        if (alertas.isEmpty()) {
            System.out.println("  Todos los items tienen stock suficiente.");
        } else {
            for (ItemInventario item : alertas) {
                System.out.println("  " + item);
            }
        }
    }

    // ========================
    // MODULO TECNICOS
    // ========================

    private static void menuTecnicos() {
        int opcion = -1;
        do {
            System.out.println("\n--- MODULO TECNICOS ---");
            System.out.println("1. Registrar tecnico");
            System.out.println("2. Listar tecnicos por sucursal");
            System.out.println("3. Buscar tecnico por nombre");
            System.out.println("4. Ver carga de trabajo de tecnicos");
            System.out.println("0. Volver al menu principal");
            System.out.print("Seleccione una opcion: ");
            try {
                opcion = scanner.nextInt();
                scanner.nextLine();
                switch (opcion) {
                    case 1: registrarTecnico(); break;
                    case 2: listarTecnicosPorSucursal(); break;
                    case 3: buscarTecnicoPorNombre(); break;
                    case 4: verCargaTecnicos(); break;
                    case 0: break;
                    default: System.out.println("Opcion invalida.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: ingrese un numero valido.");
                scanner.nextLine();
            }
        } while (opcion != 0);
    }

    private static void registrarTecnico() {
        System.out.println("\n-- Registrar Tecnico --");
        try {
            System.out.print("Nombre: ");
            String nombre = scanner.nextLine();
            System.out.print("Apellido: ");
            String apellido = scanner.nextLine();
            System.out.print("Telefono: ");
            String telefono = scanner.nextLine();
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();
            System.out.print("Especializacion (GENERAL/FTTH/WIRELESS/NETWORKING): ");
            String especializacion = scanner.nextLine().toUpperCase();

            mostrarSucursales();
            System.out.print("ID de sucursal: ");
            int idSucursal = scanner.nextInt();
            scanner.nextLine();

            Sucursal sucursal = sistema.buscarSucursalPorId(idSucursal);
            if (sucursal == null) {
                System.out.println("Error: sucursal no encontrada.");
                return;
            }
            Tecnico tecnico = new Tecnico(nombre, apellido, telefono,
                                          username, password, especializacion, sucursal, email);
            sistema.agregarTecnico(tecnico);
            System.out.println("Tecnico registrado con ID: " + tecnico.getId());
            tecnico.mostrarInfo();

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (PersistenciaException e) {
            System.out.println("Error al guardar tecnico: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("Error: valor invalido ingresado.");
            scanner.nextLine();
        }
    }

    private static void listarTecnicosPorSucursal() {
        System.out.println("\n-- Tecnicos por Sucursal --");
        mostrarSucursales();
        System.out.print("ID de sucursal: ");
        try {
            int idSucursal = scanner.nextInt();
            scanner.nextLine();
            List<Tecnico> tecnicos = sistema.buscarTecnicosPorSucursal(idSucursal);
            if (tecnicos.isEmpty()) {
                System.out.println("No hay tecnicos activos en esa sucursal.");
                return;
            }
            for (Tecnico t : tecnicos) {
                t.mostrarInfo();
            }
        } catch (InputMismatchException e) {
            System.out.println("Error: ingrese un numero valido.");
            scanner.nextLine();
        }
    }

    private static void buscarTecnicoPorNombre() {
        System.out.println("\n-- Buscar Tecnico por Nombre --");
        System.out.print("Ingrese nombre o apellido: ");
        String nombre = scanner.nextLine();
        List<Tecnico> resultado = sistema.buscarTecnicosPorNombre(nombre);
        if (resultado.isEmpty()) {
            System.out.println("No se encontraron tecnicos con ese nombre.");
            return;
        }
        for (Tecnico t : resultado) {
            t.mostrarInfo();
        }
    }

    private static void verCargaTecnicos() {
        System.out.println("\n-- Carga de Trabajo (menor a mayor) --");
        List<Tecnico> ordenados = sistema.ordenarTecnicosPorCarga();
        if (ordenados.isEmpty()) {
            System.out.println("No hay tecnicos registrados.");
            return;
        }
        for (Tecnico t : ordenados) {
            System.out.println(t);
        }
    }

    // ========================
    // MODULO ORDENES
    // ========================

    private static void menuOrdenes() {
        int opcion = -1;
        do {
            System.out.println("\n--- MODULO ORDENES DE SERVICIO ---");
            System.out.println("1. Registrar ticket");
            System.out.println("2. Crear orden desde ticket");
            System.out.println("3. Asignar tecnico a orden");
            System.out.println("4. Cambiar estado de orden");
            System.out.println("5. Listar ordenes por estado");
            System.out.println("6. Ver detalle de orden");
            System.out.println("7. Auditar orden");
            System.out.println("8. Ver historico de orden");
            System.out.println("0. Volver al menu principal");
            System.out.print("Seleccione una opcion: ");
            try {
                opcion = scanner.nextInt();
                scanner.nextLine();
                switch (opcion) {
                    case 1: registrarTicket(); break;
                    case 2: crearOrdenDesdeTicket(); break;
                    case 3: asignarTecnicoAOrden(); break;
                    case 4: cambiarEstadoOrden(); break;
                    case 5: listarOrdenesPorEstado(); break;
                    case 6: verDetalleOrden(); break;
                    case 7: auditarOrden(); break;
                    case 8: verHistoricoOrden(); break;
                    case 0: break;
                    default: System.out.println("Opcion invalida.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: ingrese un numero valido.");
                scanner.nextLine();
            }
        } while (opcion != 0);
    }

    private static void registrarTicket() {
        System.out.println("\n-- Registrar Ticket --");
        try {
            System.out.println("Clientes disponibles:");
            for (Cliente c : sistema.getClientes()) {
                System.out.println("  " + c);
            }
            System.out.print("ID de cliente: ");
            int idCliente = scanner.nextInt();
            scanner.nextLine();

            Cliente cliente = sistema.buscarClientePorId(idCliente);
            if (cliente == null) {
                System.out.println("Error: cliente no encontrado.");
                return;
            }
            System.out.print("Descripcion del problema: ");
            String descripcion = scanner.nextLine();
            System.out.print("Prioridad (BAJA/MEDIA/ALTA/URGENTE): ");
            Prioridad prioridad;
            try {
                prioridad = Prioridad.valueOf(scanner.nextLine().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Prioridad invalida, se asignara MEDIA.");
                prioridad = Prioridad.MEDIA;
            }
            Ticket ticket = new Ticket(cliente, descripcion, prioridad, "CAT");
            sistema.agregarTicket(ticket, ID_COORDINADOR);
            System.out.println("Ticket registrado con ID: " + ticket.getIdTicket());
            ticket.mostrarInfo();

        } catch (InputMismatchException e) {
            System.out.println("Error: valor invalido.");
            scanner.nextLine();
        } catch (PersistenciaException e) {
            System.out.println("Error al guardar ticket: " + e.getMessage());
        }
    }

    private static void crearOrdenDesdeTicket() {
        System.out.println("\n-- Crear Orden desde Ticket --");
        try {
            System.out.println("Tickets pendientes:");
            boolean hayPendientes = false;
            for (Ticket t : sistema.getTickets()) {
                if (t.getEstado() == EstadoTicket.PENDIENTE) {
                    t.mostrarInfo();
                    hayPendientes = true;
                }
            }
            if (!hayPendientes) {
                System.out.println("No hay tickets pendientes.");
                return;
            }
            System.out.print("ID de ticket: ");
            int idTicket = scanner.nextInt();
            scanner.nextLine();

            Ticket ticket = sistema.buscarTicketPorId(idTicket);
            if (ticket == null) {
                System.out.println("Error: ticket no encontrado.");
                return;
            }
            System.out.print("Tipo (INSTALACION/REPARACION/CAMBIO_EQUIPO/MANTENIMIENTO): ");
            TipoServicio tipo = TipoServicio.valueOf(scanner.nextLine().toUpperCase());
            System.out.print("Prioridad (BAJA/MEDIA/ALTA/URGENTE): ");
            Prioridad prioridad = Prioridad.valueOf(scanner.nextLine().toUpperCase());

            mostrarSucursales();
            System.out.print("ID de sucursal: ");
            int idSucursal = scanner.nextInt();
            scanner.nextLine();

            Sucursal sucursal = sistema.buscarSucursalPorId(idSucursal);
            if (sucursal == null) {
                System.out.println("Error: sucursal no encontrada.");
                return;
            }
            System.out.print("Observaciones (Enter para omitir): ");
            String obs = scanner.nextLine();

            OrdenServicio orden = ticket.convertirAOrden(tipo, prioridad, sucursal,
                                                         obs.isEmpty() ? null : obs);
            sistema.agregarOrden(orden, ID_COORDINADOR);

            // Actualiza estado del ticket en BD
            new TicketDAO().actualizarEstado(ticket);

            // Registra estado inicial en historico
            HistoricoEstado h = new HistoricoEstado(orden.getIdOrden(), null,
                                                    EstadoOrden.SIN_ASIGNAR.name(),
                                                    ID_COORDINADOR, "Orden creada");
            new HistoricoEstadosDAO().insertar(h);

            System.out.println("Orden creada: " + orden.getNumeroOrden());
            orden.mostrarInfo();

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (PersistenciaException e) {
            System.out.println("Error al guardar orden: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("Error: valor invalido.");
            scanner.nextLine();
        }
    }

    private static void asignarTecnicoAOrden() {
        System.out.println("\n-- Asignar Tecnico a Orden --");
        try {
            List<OrdenServicio> sinAsignar = sistema.buscarOrdenesPorEstado(EstadoOrden.SIN_ASIGNAR);
            if (sinAsignar.isEmpty()) {
                System.out.println("No hay ordenes sin asignar.");
                return;
            }
            for (OrdenServicio o : sinAsignar) o.mostrarResumen();

            System.out.print("Numero de orden: ");
            String numOrden = scanner.nextLine();
            OrdenServicio orden = sistema.buscarOrdenPorNumero(numOrden);
            if (orden == null) {
                System.out.println("Orden no encontrada.");
                return;
            }

            System.out.println("Tecnicos disponibles (ordenados por carga):");
            List<Tecnico> ordenados = sistema.ordenarTecnicosPorCarga();
            for (Tecnico t : ordenados) System.out.println("  " + t);

            System.out.print("ID de tecnico: ");
            int idTecnico = scanner.nextInt();
            scanner.nextLine();
            Tecnico tecnico = sistema.buscarTecnicoPorId(idTecnico);
            if (tecnico == null) {
                System.out.println("Tecnico no encontrado.");
                return;
            }
            System.out.print("Fecha coordinada (ej: 2026-06-15 10:00): ");
            String fecha = scanner.nextLine();

            sistema.asignarTecnicoAOrden(orden, tecnico, fecha, ID_COORDINADOR);
            System.out.println("Orden " + orden.getNumeroOrden() + " asignada correctamente.");

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (PersistenciaException e) {
            System.out.println("Error al guardar asignacion: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("Error: valor invalido.");
            scanner.nextLine();
        }
    }

    private static void cambiarEstadoOrden() {
        System.out.println("\n-- Cambiar Estado de Orden --");
        try {
            System.out.print("Numero de orden: ");
            String numOrden = scanner.nextLine();
            OrdenServicio orden = sistema.buscarOrdenPorNumero(numOrden);
            if (orden == null) {
                System.out.println("Orden no encontrada.");
                return;
            }
            orden.mostrarResumen();
            System.out.println("Estados: CONFIRMADA/EN_CAMINO/EN_SITIO/RESUELTA/POSPUESTA");
            System.out.print("Nuevo estado: ");
            EstadoOrden nuevoEstado = EstadoOrden.valueOf(scanner.nextLine().toUpperCase());
            System.out.print("Observacion (Enter para omitir): ");
            String obs = scanner.nextLine();

            sistema.cambiarEstadoOrden(orden, nuevoEstado, ID_COORDINADOR,
                                        obs.isEmpty() ? null : obs);
            System.out.println("Estado actualizado correctamente.");

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (PersistenciaException e) {
            System.out.println("Error al guardar estado: " + e.getMessage());
        }
    }

    private static void listarOrdenesPorEstado() {
        System.out.println("\n-- Listar Ordenes por Estado --");
        System.out.println("Estados: SIN_ASIGNAR/ASIGNADA/CONFIRMADA/EN_CAMINO/" +
                           "EN_SITIO/RESUELTA/PENDIENTE_AUDITORIA/CERRADA/POSPUESTA");
        System.out.print("Estado: ");
        try {
            EstadoOrden estado = EstadoOrden.valueOf(scanner.nextLine().toUpperCase());
            List<OrdenServicio> resultado = sistema.buscarOrdenesPorEstado(estado);
            if (resultado.isEmpty()) {
                System.out.println("No hay ordenes en estado " + estado);
                return;
            }
            for (OrdenServicio o : resultado) o.mostrarResumen();
        } catch (IllegalArgumentException e) {
            System.out.println("Estado invalido.");
        }
    }

    private static void verDetalleOrden() {
        System.out.println("\n-- Detalle de Orden --");
        System.out.print("Numero de orden: ");
        String numOrden = scanner.nextLine();
        OrdenServicio orden = sistema.buscarOrdenPorNumero(numOrden);
        if (orden == null) {
            System.out.println("Orden no encontrada.");
            return;
        }
        orden.mostrarInfo();
    }

    private static void auditarOrden() {
        System.out.println("\n-- Auditar Orden --");
        try {
            List<OrdenServicio> pendientes = sistema.buscarOrdenesPorEstado(
                                             EstadoOrden.PENDIENTE_AUDITORIA);
            if (pendientes.isEmpty()) {
                System.out.println("No hay ordenes pendientes de auditoria.");
                return;
            }
            for (OrdenServicio o : pendientes) o.mostrarResumen();

            System.out.print("Numero de orden a auditar: ");
            String numOrden = scanner.nextLine();
            OrdenServicio orden = sistema.buscarOrdenPorNumero(numOrden);
            if (orden == null) {
                System.out.println("Orden no encontrada.");
                return;
            }
            orden.mostrarInfo();
            System.out.print("Aprobar cierre? (S/N): ");
            String respuesta = scanner.nextLine().toUpperCase();

            if (respuesta.equals("S")) {
                System.out.print("Observacion final (Enter para omitir): ");
                String obs = scanner.nextLine();
                sistema.cerrarOrden(orden, ID_COORDINADOR, obs.isEmpty() ? null : obs);
                System.out.println("Orden cerrada correctamente.");
            } else {
                System.out.print("Motivo de rechazo: ");
                String motivo = scanner.nextLine();
                sistema.rechazarCierreOrden(orden, ID_COORDINADOR, motivo);
                System.out.println("Cierre rechazado. Orden devuelta al tecnico.");
            }

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (PersistenciaException e) {
            System.out.println("Error al auditar orden: " + e.getMessage());
        }
    }

    private static void verHistoricoOrden() {
        System.out.println("\n-- Historico de Orden --");
        System.out.print("Numero de orden: ");
        String numOrden = scanner.nextLine();
        OrdenServicio orden = sistema.buscarOrdenPorNumero(numOrden);
        if (orden == null) {
            System.out.println("Orden no encontrada.");
            return;
        }
        try {
            List<HistoricoEstado> historico = sistema.verHistoricoOrden(orden.getIdOrden());
            if (historico.isEmpty()) {
                System.out.println("Sin registros en el historico.");
                return;
            }
            System.out.println("Historico de " + orden.getNumeroOrden() + ":");
            for (HistoricoEstado h : historico) {
                h.mostrarInfo();
            }
        } catch (PersistenciaException e) {
            System.out.println("Error al obtener historico: " + e.getMessage());
        }
    }

    // ========================
    // MODULO INVENTARIO
    // ========================

    private static void menuInventario() {
        int opcion = -1;
        do {
            System.out.println("\n--- MODULO INVENTARIO ---");
            System.out.println("1. Registrar item");
            System.out.println("2. Consultar stock por sucursal");
            System.out.println("3. Registrar uso de equipamiento en orden");
            System.out.println("4. Ver alertas de stock minimo");
            System.out.println("0. Volver al menu principal");
            System.out.print("Seleccione una opcion: ");
            try {
                opcion = scanner.nextInt();
                scanner.nextLine();
                switch (opcion) {
                    case 1: registrarItem(); break;
                    case 2: consultarStockPorSucursal(); break;
                    case 3: registrarUsoEquipamiento(); break;
                    case 4: verAlertasStock(); break;
                    case 0: break;
                    default: System.out.println("Opcion invalida.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: ingrese un numero valido.");
                scanner.nextLine();
            }
        } while (opcion != 0);
    }

    private static void registrarItem() {
        System.out.println("\n-- Registrar Item de Inventario --");
        try {
            System.out.print("Codigo: ");
            String codigo = scanner.nextLine();
            System.out.print("Nombre: ");
            String nombre = scanner.nextLine();
            System.out.print("Categoria (ONU/CABLE/CONECTOR/MESH/HERRAMIENTA/OTRO): ");
            CategoriaItem categoria = CategoriaItem.valueOf(scanner.nextLine().toUpperCase());
            System.out.print("Unidad de medida (unidad/metro/etc): ");
            String unidad = scanner.nextLine();
            System.out.print("Cantidad disponible: ");
            int cantidad = scanner.nextInt();
            System.out.print("Stock minimo: ");
            int minimo = scanner.nextInt();
            scanner.nextLine();

            mostrarSucursales();
            System.out.print("ID de sucursal: ");
            int idSucursal = scanner.nextInt();
            scanner.nextLine();

            Sucursal sucursal = sistema.buscarSucursalPorId(idSucursal);
            if (sucursal == null) {
                System.out.println("Sucursal no encontrada.");
                return;
            }
            ItemInventario item = new ItemInventario(codigo, nombre, null,
                                                     categoria, unidad,
                                                     cantidad, minimo, sucursal);
            sistema.agregarItem(item);
            System.out.println("Item registrado con ID: " + item.getIdItem());
            item.mostrarInfo();

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (PersistenciaException e) {
            System.out.println("Error al guardar item: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("Error: valor invalido.");
            scanner.nextLine();
        }
    }

    private static void consultarStockPorSucursal() {
        System.out.println("\n-- Stock por Sucursal --");
        mostrarSucursales();
        System.out.print("ID de sucursal: ");
        try {
            int idSucursal = scanner.nextInt();
            scanner.nextLine();
            List<ItemInventario> items = sistema.buscarInventarioPorSucursal(idSucursal);
            if (items.isEmpty()) {
                System.out.println("No hay items en esa sucursal.");
                return;
            }
            for (ItemInventario item : items) item.mostrarInfo();
        } catch (InputMismatchException e) {
            System.out.println("Error: valor invalido.");
            scanner.nextLine();
        }
    }

    private static void registrarUsoEquipamiento() {
        System.out.println("\n-- Registrar Uso de Equipamiento --");
        try {
            System.out.print("Numero de orden: ");
            String numOrden = scanner.nextLine();
            OrdenServicio orden = sistema.buscarOrdenPorNumero(numOrden);
            if (orden == null) {
                System.out.println("Orden no encontrada.");
                return;
            }
            List<UsoEquipamiento> usos = new ArrayList<>();
            String continuar = "S";
            while (continuar.equals("S")) {
                System.out.println("Items disponibles:");
                for (ItemInventario item : sistema.getInventario()) {
                    System.out.println("  " + item);
                }
                System.out.print("Codigo de item: ");
                String codigo = scanner.nextLine();
                ItemInventario item = sistema.buscarItemPorCodigo(codigo);
                if (item == null) {
                    System.out.println("Item no encontrado.");
                } else {
                    System.out.print("Cantidad utilizada: ");
                    int cantidad = scanner.nextInt();
                    scanner.nextLine();
                    usos.add(new UsoEquipamiento(item, cantidad));
                }
                System.out.print("Agregar otro item? (S/N): ");
                continuar = scanner.nextLine().toUpperCase();
            }
            System.out.print("Descripcion de la resolucion: ");
            String resolucion = scanner.nextLine();

            sistema.registrarResolucionOrden(orden, resolucion, usos, ID_COORDINADOR);
            System.out.println("Resolucion registrada. Orden pendiente de auditoria.");

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (PersistenciaException e) {
            System.out.println("Error al guardar resolucion: " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("Error: valor invalido.");
            scanner.nextLine();
        }
    }

    private static void verAlertasStock() {
        System.out.println("\n-- Alertas de Stock Minimo --");
        List<ItemInventario> alertas = sistema.obtenerItemsStockBajo();
        if (alertas.isEmpty()) {
            System.out.println("Todos los items tienen stock suficiente.");
            return;
        }
        for (ItemInventario item : alertas) item.mostrarInfo();
    }

    // ========================
    // UTILIDADES
    // ========================

    private static void mostrarSucursales() {
        System.out.println("Sucursales disponibles:");
        for (Sucursal s : sistema.getSucursales()) {
            System.out.println("  " + s);
        }
    }
}