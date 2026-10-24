package com.mycompany.tienda3a.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Entidad del diagrama: INVENTARIO. */
public class Inventario {
    private int idInventario;
    private int idTienda;
    private int idProducto;
    private BigDecimal stock;
    private BigDecimal stockMinimo;

    public Inventario(int idInventario, int idTienda, int idProducto, BigDecimal stock, BigDecimal stockMinimo) {
        this.idInventario = idInventario;
        this.idTienda = idTienda;
        this.idProducto = idProducto;
        if (stock == null || stock.signum() < 0) throw new IllegalArgumentException("stock no puede ser negativo");
        this.stock = stock.setScale(3, RoundingMode.HALF_UP);
        if (stockMinimo == null || stockMinimo.signum() < 0) throw new IllegalArgumentException("stockMinimo no puede ser negativo");
        this.stockMinimo = stockMinimo.setScale(3, RoundingMode.HALF_UP);
    }

    public int getIdInventario() { return idInventario; }
    public int getIdTienda() { return idTienda; }
    public int getIdProducto() { return idProducto; }
    public BigDecimal getStock() { return stock; }
    public BigDecimal getStockMinimo() { return stockMinimo; }

    public void ingresar(BigDecimal cantidad) {
        if (cantidad == null || cantidad.signum() <= 0)
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        stock = stock.add(cantidad).setScale(3, RoundingMode.HALF_UP);
    }

    public void retirar(BigDecimal cantidad) {
        if (cantidad == null || cantidad.signum() <= 0 || stock.compareTo(cantidad) < 0)
            throw new IllegalArgumentException("Cantidad invalida o stock insuficiente");
        stock = stock.subtract(cantidad).setScale(3, RoundingMode.HALF_UP);
    }

    public boolean necesitaReposicion() { return stock.compareTo(stockMinimo) <= 0; }

    @Override
    public String toString() {
        return "Inventario{" + "idInventario=" + idInventario + ", " + "idTienda=" + idTienda + ", " + "idProducto=" + idProducto + ", " + "stock=" + stock + ", " + "stockMinimo=" + stockMinimo + "}";
    }
}
