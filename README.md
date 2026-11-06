# tienda3a — Proyecto para sistema de ventas para tienda de conveniencia

Un solo proyecto Maven.

## Cómo abrirlo en NetBeans

1. File > Open Project > selecciona esta carpeta `tienda3a` (la que tiene `pom.xml`).
2. Botón derecho sobre el proyecto > Run (o Shift+F6 sobre `Main.java`).
3. Se imprime una demostración por consola (objetos, herencia, interfaz +
   polimorfismo, estático) y luego se abre la ventana de venta (Swing).

## Temas abordados

| Temas | Dónde |
|---|---|
| 4a. Objetos y clases | `modelo/Producto.java`, `modelo/Usuario.java`, etc. (atributos privados, set/get, constructores) |
| 4b. Herencia (≥2 relaciones) | `modelo/Comprobante.java` (abstracta) → `modelo/Boleta.java`, `modelo/Factura.java`, `modelo/VentaSimpleComprobante.java` (3 subclases, usan `super(...)`) |
| 4c. Interfaz + polimorfismo | `modelo/Imprimible.java` (implementada por las 3 subclases de Comprobante) y `pago/EstrategiaPago.java` (implementada por `PagoEfectivo`, `PagoTarjeta`, `PagoBilletera`) |
| 4d. Variable estática | `config/ConfiguracionVenta.java` (IGV, fondo de caja, límites — no un contador) |
| 4e. 3 principios SOLID | Explicados en el Javadoc de `controlador/ControladorVenta.java` (SRP, OCP, DIP) |
| Patrón Strategy | `pago/` (EstrategiaPago + ProcesadorPago) |
| Patrón Factory | `factoria/ComprobanteFactory.java` |
| Arquitectura MVC | `modelo/`, `vista/VentanaVenta.java`, `controlador/ControladorVenta.java` |
| Interfaz de programa | `vista/VentanaVenta.java` (JFrame de Swing, no una página web) |
