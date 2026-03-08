
package src.vista;

import src.model.Grafo;

//Clase para encapsular toda la información relevante de un click en el panel, facilitando la comunicación entre estrategias
public class ClickInfo {

    private final int x, y;
    private final Grafo grafo;
    private final PanelGrafo panel;
    private Integer nodoSeleccionado;

    public ClickInfo(int x, int y, Grafo grafo, PanelGrafo panel) {
        this.x = x;
        this.y = y;
        this.grafo = grafo;
        this.panel = panel;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Grafo getGrafo() {
        return grafo;
    }

    public PanelGrafo getPanel() {
        return panel;
    }

    public Integer getNodoSeleccionado() {
        return nodoSeleccionado;
    }

    public void setNodoSeleccionado(Integer nodo) {
        this.nodoSeleccionado = nodo;
    }
}
