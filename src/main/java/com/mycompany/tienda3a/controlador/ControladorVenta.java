package com.mycompany.tienda3a.controlador;

import com.mycompany.tienda3a.config.Configuracion;
import com.mycompany.tienda3a.excepcion.ExcepcionDatos;
import com.mycompany.tienda3a.excepcion.ExcepcionNegocio;
import com.mycompany.tienda3a.modelo.*;
import com.mycompany.tienda3a.repositorio.RepositorioCatalogo;
import com.mycompany.tienda3a.repositorio.RepositorioClientes;
import com.mycompany.tienda3a.repositorio.RepositorioVentas;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ControladorVenta {
    private final RepositorioCatalogo catalogo;
    private final RepositorioClientes clientes;
    private final RepositorioVentas ventas;
    private final ControladorComprobante controladorComprobante;
    private final Bitacora bitacora;

    private final Map<Integer, Venta> ventasEnCurso = new HashMap<>();

    public ControladorVenta(RepositorioCatalogo catalogo, RepositorioClientes clientes, RepositorioVentas ventas,
                            ControladorComprobante controladorComprobante, Bitacora bitacora) {
        this.catalogo = catalogo;
        this.clientes = clientes;
        this.ventas = ventas;
        this.controladorComprobante = controladorComprobante;
        this.bitacora = bitacora;
    }

    public Venta iniciarVenta(Caja caja, Cajero cajero) {
        Venta pendiente = ventasEnCurso.get(cajero.getIdUsuario());
        if (pendiente != null && pendiente.estaEnCurso() && pendiente.getCaja() == caja) {
            return pendiente;
        }
        Venta v = new Venta(0, caja, cajero, UUID.randomUUID().toString(), LocalDateTime.now());
        ventasEnCurso.put(cajero.getIdUsuario(), v);
        return v;
    }

    public boolean tieneVentaEnCurso(Cajero cajero) {
        Venta v = ventasEnCurso.get(cajero.getIdUsuario());
        return v != null && v.estaEnCurso() && !v.getDetalles().isEmpty();
    }

    public DetalleVenta agregarProducto(Venta venta, String codigoONombre, double cantidad) {
        Producto producto = identificarProducto(codigoONombre);
        try {
            return venta.agregarProducto(producto, cantidad);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new ExcepcionNegocio(e.getMessage());
        }
    }

    public void modificarLinea(Venta venta, DetalleVenta detalle, double cantidad) {
        try {
            venta.modificarCantidad(detalle, cantidad);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new ExcepcionNegocio(e.getMessage());
        }
    }

    public void retirarLinea(Venta venta, DetalleVenta detalle) {
        try {
            venta.retirarProducto(detalle);
        } catch (IllegalStateException e) {
            throw new ExcepcionNegocio(e.getMessage());
        }
    }

    public boolean elegirComprobante(Venta venta, TipoComprobante tipo, Cliente cliente) {
        double maximo = Configuracion.getMontoMaximoVentaSimple();
        switch (tipo) {
            case VENTA_SIMPLE -> {
                if (venta.getTotal() > maximo) {
                    throw new ExcepcionNegocio(String.format(
                            "La venta supera S/ %.2f: emite una boleta con el DNI del cliente o una factura.", maximo));
                }
                venta.asignarCliente(null);
            }
            case BOLETA -> {
                if (cliente == null || cliente.getTipoDocumento() != TipoDocumento.DNI) {
                    throw new ExcepcionNegocio("La boleta necesita el DNI y el nombre del cliente.");
                }
                venta.asignarCliente(cliente);
            }
            case FACTURA -> {
                if (cliente == null || cliente.getTipoDocumento() != TipoDocumento.RUC) {
                    throw new ExcepcionNegocio("La factura necesita el RUC y la razón social del cliente.");
                }
                venta.asignarCliente(cliente);
            }
        }
        return true;
    }

    public Cliente buscarCliente(TipoDocumento tipo, String numero) {
        return numero == null ? null : clientes.buscarPorDocumento(tipo, numero.trim());
    }

    public Cliente obtenerCliente(TipoDocumento tipo, String numero, String nombreRazonSocial, String direccion) {
            String doc = numero == null ? "" : (tipo == TipoDocumento.DNI ? numero : numero.trim());
            validarDocumentoCliente(tipo, doc);
        if (tipo == TipoDocumento.DNI) {
            String errorNombre = Validaciones.errorNombre("nombre del cliente", nombreRazonSocial, Validaciones.MAX_NOMBRE_CLIENTE);
            if (errorNombre != null) {
                throw new ExcepcionNegocio(errorNombre);
            }
            nombreRazonSocial = Validaciones.normalizarNombre(nombreRazonSocial);
        }
        Cliente c = clientes.buscarPorDocumento(tipo, doc);
        try {
            if (c == null) {
                c = new Cliente(0, tipo, doc, nombreRazonSocial, direccion);
            } else {
                c.setNombreRazonSocial(nombreRazonSocial);
                if (direccion != null && !direccion.isBlank()) {
                    c.setDireccion(direccion);
                }
            }
        } catch (IllegalArgumentException e) {
            throw new ExcepcionNegocio(e.getMessage());
        }
        clientes.guardar(c);
        return c;
    }

    private void validarDocumentoCliente(TipoDocumento tipo, String numero) {
                if (tipo == TipoDocumento.DNI) {
            String errorDni = Validaciones.errorDni(numero);
            if (errorDni != null) {
                throw new ExcepcionNegocio(errorDni);
            }
        }
        if (tipo == TipoDocumento.RUC && !numero.matches("(10|15|17|20)\\d{9}")) {
            throw new ExcepcionNegocio("El RUC debe tener 11 dígitos y empezar con 10, 15, 17 o 20.");
        }
    }

    public Pago nuevoPago(Venta venta, MedioPago medio, double montoEntregado, String referencia) {
        if (venta.getTotal() <= 0) {
            throw new ExcepcionNegocio("Agrega productos antes de cobrar.");
        }
        return switch (medio) {
            case EFECTIVO -> new PagoEfectivo(0, venta, venta.getCajero(), venta.getTotal(), LocalDateTime.now(), montoEntregado);
            case TARJETA -> new PagoTarjeta(0, venta, venta.getCajero(), venta.getTotal(), LocalDateTime.now(), referencia);
            case BILLETERA -> new PagoBilletera(0, venta, venta.getCajero(), venta.getTotal(), LocalDateTime.now(), referencia);
        };
    }

    public boolean registrarPago(Venta venta, Pago pago) {
        if (pago == null) {
            throw new ExcepcionNegocio("Elige el medio de pago.");
        }
        if (!pago.validar()) {
            if (pago instanceof PagoEfectivo e) {
                throw new ExcepcionNegocio(String.format("El monto entregado (S/ %.2f) no cubre el total de S/ %.2f.",
                        e.getMontoEntregado(), venta.getTotal()));
            }
            String error = pago instanceof PagoElectronico pe ? ValidacionesPago.errorOperacion(pe.getReferenciaDigital()) : null;
            throw new ExcepcionNegocio(error != null ? error : "El pago no es válido.");        }
        return true;
    }

    public Comprobante confirmarVenta(Venta venta, Pago pago, TipoComprobante tipo) {
        if (!venta.estaEnCurso()) {
            throw new ExcepcionNegocio("Esta venta ya fue confirmada o cancelada.");
        }
        if (venta.getDetalles().isEmpty()) {
            throw new ExcepcionNegocio("La venta no tiene productos.");
        }
        elegirComprobante(venta, tipo, venta.getCliente());
        registrarPago(venta, pago);
        if (!venta.revalidarStock()) {
            throw new ExcepcionNegocio("El stock cambió y ya no cubre alguna línea. Ajusta las cantidades antes de cobrar.");
        }
        Comprobante comprobante;
        try {
            venta.confirmar(pago);
            comprobante = controladorComprobante.emitir(venta, tipo);
            ventas.guardarVentaConfirmada(venta);
        } catch (ExcepcionDatos | ExcepcionNegocio | IllegalArgumentException | IllegalStateException e) {
            venta.deshacerConfirmacion();
            bitacora.registrar(venta.getCajero(), "CONFIRMAR_VENTA", "venta", null, ResultadoOperacion.FALLIDO, e.getMessage());
            throw new ExcepcionNegocio(e instanceof ExcepcionDatos
                    ? "No se pudo guardar la venta. No se cobró ni se descontó stock; la venta sigue en curso."
                    : e.getMessage());
        }
        ventasEnCurso.remove(venta.getCajero().getIdUsuario());
        bitacora.registrar(venta.getCajero(), "CONFIRMAR_VENTA", "venta", venta.getIdVenta(), ResultadoOperacion.EXITO,
                comprobante.getNumero() + String.format(" · S/ %.2f", venta.getTotal()));
        try {
            controladorComprobante.enviar(comprobante);
        } catch (RuntimeException e) {
            System.err.println("Envío a SUNAT pendiente: " + e.getMessage());
        }
        return comprobante;
    }

    public void cancelarVenta(Venta venta) {
        if (!venta.estaEnCurso()) {
            return;
        }
        boolean teniaLineas = !venta.getDetalles().isEmpty();
        venta.cancelar();
        ventasEnCurso.remove(venta.getCajero().getIdUsuario());
        if (teniaLineas) {
            bitacora.registrar(venta.getCajero(), "CANCELAR_VENTA", "venta", null, ResultadoOperacion.EXITO,
                    String.format("S/ %.2f", venta.getTotal()));
        }
    }

    public Venta buscarVentaParaAnular(String numeroComprobante) {
        Comprobante c = controladorComprobante.buscarPorNumero(numeroComprobante);
        if (c == null) {
            throw new ExcepcionNegocio("No existe un comprobante con el número " + numeroComprobante + ".");
        }
        return c.getVenta();
    }

    public List<Venta> ventasRecientes(Caja caja, int limite) {
        List<Venta> lista = new ArrayList<>();
        List<Venta> todas = ventas.listar();
        for (int i = todas.size() - 1; i >= 0 && lista.size() < limite; i--) {
            Venta v = todas.get(i);
            if (v.getCaja() == caja && v.getEstado() != EstadoVenta.EN_CURSO) {
                lista.add(v);
            }
        }
        return lista;
    }

    public void anularVenta(Venta venta, String motivo, Cajero cajero) {
        try {
            venta.anular(motivo, cajero);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new ExcepcionNegocio(e.getMessage());
        }
        if (venta.getPago() instanceof PagoElectronico pe) {
            pe.registrarReferenciaReversion("DEV-" + pe.getReferenciaDigital());
        }
        try {
            ventas.guardarAnulacion(venta);
        } catch (ExcepcionDatos e) {
            bitacora.registrar(cajero, "ANULAR_VENTA", "venta", venta.getIdVenta(), ResultadoOperacion.FALLIDO, e.getMessage());
            throw new ExcepcionNegocio("No se pudo guardar la anulación en la base de datos. Reinicia el sistema e inténtalo de nuevo.");
        }
        if (venta.getComprobante() != null) {
            controladorComprobante.comunicarAnulacion(venta.getComprobante());
        }
        bitacora.registrar(cajero, "ANULAR_VENTA", "venta", venta.getIdVenta(), ResultadoOperacion.EXITO,
                (venta.getComprobante() == null ? "" : venta.getComprobante().getNumero() + " · ") + venta.getMotivoAnulacion());
    }

    private Producto identificarProducto(String codigoONombre) {
        if (codigoONombre == null || codigoONombre.isBlank()) {
            throw new ExcepcionNegocio("Escribe o escanea el código del producto.");
        }
        Producto exacto = catalogo.buscarProductoPorCodigo(codigoONombre.trim());
        if (exacto != null) {
            if (!exacto.isActivo()) {
                throw new ExcepcionNegocio(exacto.getNombre() + " está desactivado y no se puede vender.");
            }
            return exacto;
        }
        String buscado = ControladorProducto.normalizar(codigoONombre);
        List<Producto> coincidencias = new ArrayList<>();
        for (Producto p : catalogo.listarProductos()) {
            if (p.isActivo() && ControladorProducto.normalizar(p.getNombre()).contains(buscado)) {
                coincidencias.add(p);
            }
        }
        if (coincidencias.isEmpty()) {
            throw new ExcepcionNegocio("No se encontró ningún producto con «" + codigoONombre.trim() + "».");
        }
        if (coincidencias.size() > 1) {
            throw new ExcepcionNegocio("Hay " + coincidencias.size() + " productos que coinciden. Elige uno de la lista.");
        }
        return coincidencias.get(0);
    }
}
