package src.vista;

public class EliminarAristaStrategy implements EstrategiaClick {

    @Override
    public void ejecutarClick(ClickInfo info) {
        int x = info.getX();
        int y = info.getY();
        PanelGrafo panel = info.getPanel();

        Arista aristaClickeada = panel.obtenerAristaCercana(x, y, 10);

        if (aristaClickeada != null) {
            info.getGrafo().eliminarArista(
                    aristaClickeada.getNodo1(),
                    aristaClickeada.getNodo2()
            );
        }
    }

    @Override
    public String getDescripcion() {
        return "Eliminar Aristas - Haz clic cerca de una arista";
    }
}
