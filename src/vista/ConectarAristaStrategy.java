package src.vista;

public class ConectarAristaStrategy implements EstrategiaClick {

    @Override
    public void ejecutarClick(ClickInfo info) {
        int x = info.getX();
        int y = info.getY();
        PanelGrafo panel = info.getPanel();

        Integer nodoClickeado = panel.obtenerNodoCercano(x, y, 20);

        if (nodoClickeado != null) {
            Integer seleccionado = info.getNodoSeleccionado();

            if (seleccionado == null) {
                info.setNodoSeleccionado(nodoClickeado);
            } else if (!seleccionado.equals(nodoClickeado)) {
                info.getGrafo().agregarArista(seleccionado, nodoClickeado);
                info.setNodoSeleccionado(null);
            } else {
                info.setNodoSeleccionado(null);
            }
        }
    }

    @Override
    public String getDescripcion() {
        return "Conectar Nodos - Selecciona dos nodos";
    }
    
}
