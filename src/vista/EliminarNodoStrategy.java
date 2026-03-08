package src.vista;

public class EliminarNodoStrategy implements EstrategiaClick {

    @Override
    public void ejecutarClick(ClickInfo info) {
        int x = info.getX();
        int y = info.getY();
        PanelGrafo panel = info.getPanel();

        Integer nodoClickeado = panel.obtenerNodoCercano(x, y, 20);

        if (nodoClickeado != null) {
            info.getGrafo().eliminarNodo(nodoClickeado);
        }
    }

    @Override
    public String getDescripcion() {
        return "Eliminar Nodos - Haz clic en un nodo";
    }
}
