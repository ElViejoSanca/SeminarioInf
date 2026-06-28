// SistemaGestion.java
import java.util.ArrayList;
import java.util.List;

public class SistemaGestion {

    // DAOs
    private SucursalDAO sucursalDAO = new SucursalDAO();
    private ClienteDAO clienteDAO = new ClienteDAO();
    private TecnicoDAO tecnicoDAO = new TecnicoDAO();
    private TicketDAO ticketDAO = new TicketDAO();
    private OrdenServicioDAO ordenDAO = new OrdenServicioDAO();
    private ItemInventarioDAO itemDAO = new ItemInventarioDAO();
    private UsoEquipamientoDAO usoDAO = new UsoEquipamientoDAO();
    private HistoricoEstadosDAO historicoDAO = new HistoricoEstadosDAO();

    // Cache en memoria (ArrayList - requisito de la consigna)
    private List<Sucursal> sucursales = new ArrayList<>();
    private List<Cliente> clientes = new ArrayList<>();
    private List<Tecnico> tecnicos = new ArrayList<>();
    private List<Ticket> tickets = new ArrayList<>();
    private List<OrdenServicio> ordenes = new ArrayList<>();
    private List<ItemInventario> inventario = new ArrayList<>();

    // Constructor: carga datos desde MySQL al iniciar
    public SistemaGestion() throws PersistenciaException {
        cargarDesdeBD();
    }

    // Carga todas las listas desde la base de datos
    private void cargarDesdeBD() throws PersistenciaException {
        sucursales = sucursalDAO.listarTodas();
        clientes = clienteDAO.listarTodos();
        tecnicos = tecnicoDAO.listarTodos();
        tickets = ticketDAO.listarPorEstado(EstadoTicket.PENDIENTE);
        ordenes = ordenDAO.listarTodas();
        inventario = itemDAO.listarTodos();
        System.out.println("Datos cargados desde la base de datos correctamente.");
    }

    // ========================
    // MÉTODOS DE ALTA
    // ========================

    public void agregarSucursal(Sucursal sucursal) throws PersistenciaException {
        if (sucursal == null) {
            throw new IllegalArgumentException("La sucursal no puede ser nula");
        }
        sucursalDAO.insertar(sucursal);
        sucursales.add(sucursal);
    }

    public void agregarCliente(Cliente cliente) throws PersistenciaException {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo");
        }
        clienteDAO.insertar(cliente);
        clientes.add(cliente);
    }

    public void agregarTecnico(Tecnico tecnico) throws PersistenciaException {
        if (tecnico == null) {
            throw new IllegalArgumentException("El tecnico no puede ser nulo");
        }
        tecnicoDAO.insertar(tecnico);
        tecnicos.add(tecnico);
    }

    public void agregarTicket(Ticket ticket, int idUsuarioRegistro) throws PersistenciaException {
        if (ticket == null) {
            throw new IllegalArgumentException("El ticket no puede ser nulo");
        }
        ticketDAO.insertar(ticket, idUsuarioRegistro);
        tickets.add(ticket);
    }

    public void agregarOrden(OrdenServicio orden, int idUsuarioCreacion) throws PersistenciaException {
        if (orden == null) {
            throw new IllegalArgumentException("La orden no puede ser nula");
        }
        ordenDAO.insertar(orden, idUsuarioCreacion);
        ordenes.add(orden);
    }

    public void agregarItem(ItemInventario item) throws PersistenciaException {
        if (item == null) {
            throw new IllegalArgumentException("El item no puede ser nulo");
        }
        itemDAO.insertar(item);
        inventario.add(item);
    }

    // ========================
    // MÉTODOS DE ACTUALIZACIÓN
    // ========================

    public void asignarTecnicoAOrden(OrdenServicio orden, Tecnico tecnico,
                                      String fecha, int idUsuario) throws PersistenciaException {
        orden.asignarTecnico(tecnico, fecha);
        ordenDAO.actualizarAsignacion(orden);
        HistoricoEstado h = new HistoricoEstado(orden.getIdOrden(),
                                                EstadoOrden.SIN_ASIGNAR.name(),
                                                EstadoOrden.ASIGNADA.name(),
                                                idUsuario, "Tecnico asignado");
        historicoDAO.insertar(h);
    }

    public void cambiarEstadoOrden(OrdenServicio orden, EstadoOrden nuevoEstado,
                                    int idUsuario, String observaciones) throws PersistenciaException {
        String estadoAnterior = orden.getEstadoActual().name();
        orden.cambiarEstado(nuevoEstado);
        ordenDAO.actualizarEstado(orden);
        HistoricoEstado h = new HistoricoEstado(orden.getIdOrden(),
                                                estadoAnterior,
                                                nuevoEstado.name(),
                                                idUsuario, observaciones);
        historicoDAO.insertar(h);
    }

    public void registrarResolucionOrden(OrdenServicio orden, String resolucion,
                                          List<UsoEquipamiento> usos,
                                          int idUsuario) throws PersistenciaException {
        String estadoAnterior = orden.getEstadoActual().name();
        orden.registrarResolucion(resolucion, usos);
        ordenDAO.actualizarResolucion(orden);

        // Persiste cada uso y descuenta stock en BD
        for (UsoEquipamiento uso : usos) {
            usoDAO.insertar(orden.getIdOrden(), uso);
            itemDAO.descontarStock(uso.getItem().getIdItem(),
                                   orden.getSucursal().getIdSucursal(),
                                   uso.getCantidadUtilizada());
        }

        HistoricoEstado h = new HistoricoEstado(orden.getIdOrden(),
                                                estadoAnterior,
                                                EstadoOrden.PENDIENTE_AUDITORIA.name(),
                                                idUsuario, "Resolucion registrada");
        historicoDAO.insertar(h);
    }

    public void cerrarOrden(OrdenServicio orden, int idUsuario,
                             String observacion) throws PersistenciaException {
        String estadoAnterior = orden.getEstadoActual().name();
        orden.cambiarEstado(EstadoOrden.CERRADA);
        if (observacion != null && !observacion.trim().isEmpty()) {
            orden.setObservaciones(observacion);
        }
        ordenDAO.cerrarOrden(orden, idUsuario);
        HistoricoEstado h = new HistoricoEstado(orden.getIdOrden(),
                                                estadoAnterior,
                                                EstadoOrden.CERRADA.name(),
                                                idUsuario, observacion);
        historicoDAO.insertar(h);
    }

    public void rechazarCierreOrden(OrdenServicio orden, int idUsuario,
                                     String motivo) throws PersistenciaException {
        String estadoAnterior = orden.getEstadoActual().name();
        orden.cambiarEstado(EstadoOrden.ASIGNADA);
        orden.setObservaciones("RECHAZO: " + motivo);
        ordenDAO.actualizarEstado(orden);
        HistoricoEstado h = new HistoricoEstado(orden.getIdOrden(),
                                                estadoAnterior,
                                                EstadoOrden.ASIGNADA.name(),
                                                idUsuario, "RECHAZO: " + motivo);
        historicoDAO.insertar(h);
    }

    // ========================
    // MÉTODOS DE BÚSQUEDA EN MEMORIA
    // ========================

    public Cliente buscarClientePorId(int id) {
        for (Cliente c : clientes) {
            if (c.getIdCliente() == id) return c;
        }
        return null;
    }

    public Tecnico buscarTecnicoPorId(int id) {
        for (Tecnico t : tecnicos) {
            if (t.getId() == id) return t;
        }
        return null;
    }

    public Ticket buscarTicketPorId(int id) {
        for (Ticket t : tickets) {
            if (t.getIdTicket() == id) return t;
        }
        return null;
    }

    public OrdenServicio buscarOrdenPorNumero(String numeroOrden) {
        for (OrdenServicio o : ordenes) {
            if (o.getNumeroOrden().equalsIgnoreCase(numeroOrden)) return o;
        }
        return null;
    }

    public ItemInventario buscarItemPorCodigo(String codigo) {
        for (ItemInventario item : inventario) {
            if (item.getCodigo().equalsIgnoreCase(codigo)) return item;
        }
        return null;
    }

    public Sucursal buscarSucursalPorId(int id) {
        for (Sucursal s : sucursales) {
            if (s.getIdSucursal() == id) return s;
        }
        return null;
    }

    public List<Tecnico> buscarTecnicosPorNombre(String nombre) {
        List<Tecnico> resultado = new ArrayList<>();
        for (Tecnico t : tecnicos) {
            if (t.getNombreCompleto().toLowerCase().contains(nombre.toLowerCase())) {
                resultado.add(t);
            }
        }
        return resultado;
    }

    public List<Tecnico> buscarTecnicosPorSucursal(int idSucursal) {
        List<Tecnico> resultado = new ArrayList<>();
        for (Tecnico t : tecnicos) {
            if (t.getSucursal().getIdSucursal() == idSucursal && t.isActivo()) {
                resultado.add(t);
            }
        }
        return resultado;
    }

    public List<OrdenServicio> buscarOrdenesPorEstado(EstadoOrden estado) {
        List<OrdenServicio> resultado = new ArrayList<>();
        for (OrdenServicio o : ordenes) {
            if (o.getEstadoActual() == estado) resultado.add(o);
        }
        return resultado;
    }

    public List<ItemInventario> obtenerItemsStockBajo() {
        List<ItemInventario> resultado = new ArrayList<>();
        for (ItemInventario item : inventario) {
            if (item.verificarStockMinimo()) resultado.add(item);
        }
        return resultado;
    }

    public List<ItemInventario> buscarInventarioPorSucursal(int idSucursal) {
        List<ItemInventario> resultado = new ArrayList<>();
        for (ItemInventario item : inventario) {
            if (item.getSucursal().getIdSucursal() == idSucursal) resultado.add(item);
        }
        return resultado;
    }

    public List<HistoricoEstado> verHistoricoOrden(int idOrden) throws PersistenciaException {
        return historicoDAO.listarPorOrden(idOrden);
    }

    // ========================
    // ALGORITMOS DE ORDENACIÓN (Opción 4 + arreglo nativo Opción 2)
    // ========================

    // Ordena técnicos por carga (burbuja) y retorna arreglo nativo con top 3
    public Tecnico[] topTresTecnicosMenorCarga() {
        List<Tecnico> lista = new ArrayList<>(tecnicos);
        int n = lista.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (lista.get(j).getOrdenesActivas() >
                    lista.get(j + 1).getOrdenesActivas()) {
                    Tecnico temp = lista.get(j);
                    lista.set(j, lista.get(j + 1));
                    lista.set(j + 1, temp);
                }
            }
        }
        // Convierte a arreglo nativo con top 3 (opción 4)
        int tamano = Math.min(3, lista.size());
        Tecnico[] top = new Tecnico[tamano];
        for (int i = 0; i < tamano; i++) {
            top[i] = lista.get(i);
        }
        return top;
    }

    public List<Tecnico> ordenarTecnicosPorCarga() {
        List<Tecnico> lista = new ArrayList<>(tecnicos);
        int n = lista.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (lista.get(j).getOrdenesActivas() >
                    lista.get(j + 1).getOrdenesActivas()) {
                    Tecnico temp = lista.get(j);
                    lista.set(j, lista.get(j + 1));
                    lista.set(j + 1, temp);
                }
            }
        }
        return lista;
    }

    // Dashboard con arreglo indexado por ordinal del enum (opción 2)
    public int[] contarOrdenesPorEstado() {
        int[] contadores = new int[EstadoOrden.values().length];
        for (OrdenServicio o : ordenes) {
            contadores[o.getEstadoActual().ordinal()]++;
        }
        return contadores;
    }

    public List<OrdenServicio> ordenarOrdenesPorPrioridad() {
        List<OrdenServicio> lista = new ArrayList<>(ordenes);
        int n = lista.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (lista.get(j).getPrioridad().ordinal() <
                    lista.get(j + 1).getPrioridad().ordinal()) {
                    OrdenServicio temp = lista.get(j);
                    lista.set(j, lista.get(j + 1));
                    lista.set(j + 1, temp);
                }
            }
        }
        return lista;
    }

    // ========================
    // GETTERS DE LISTAS
    // ========================

    public List<Sucursal> getSucursales() { return sucursales; }
    public List<Cliente> getClientes() { return clientes; }
    public List<Tecnico> getTecnicos() { return tecnicos; }
    public List<Ticket> getTickets() { return tickets; }
    public List<OrdenServicio> getOrdenes() { return ordenes; }
    public List<ItemInventario> getInventario() { return inventario; }
}