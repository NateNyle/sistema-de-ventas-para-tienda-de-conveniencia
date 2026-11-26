package com.mycompany.tienda3a.repositorio;

import com.mycompany.tienda3a.modelo.Caja;
import com.mycompany.tienda3a.modelo.CierreCaja;
import com.mycompany.tienda3a.modelo.Pago;
import com.mycompany.tienda3a.modelo.Venta;
import java.util.List;

public interface RepositorioCajas {
    List<Caja> listarCajas();

    List<CierreCaja> listarCierres();

    void guardarCierre(CierreCaja cierre, List<Venta> ventas, List<Pago> devoluciones);
}
// prueba git hub 1.1