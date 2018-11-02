package enorsul.com.enorsul.services;

import java.util.List;

import enorsul.com.enorsul.models.OrdenModelo;

public class NewsService {

    private OrdenModelo ordenModelo;
    private List<OrdenModelo> allOrdenes;

    public OrdenModelo getOrdenModelo() {
        return ordenModelo;
    }

    public void setOrdenModelo(OrdenModelo ordenModelo) {
        this.ordenModelo = ordenModelo;
    }

    public List<OrdenModelo> getAllOrdenes() {
        return allOrdenes;
    }

    public void setAllOrdenes(List<OrdenModelo> allOrdenes) {
        this.allOrdenes = allOrdenes;
    }
}
