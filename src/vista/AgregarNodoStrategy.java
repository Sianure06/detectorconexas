package src.vista;
import src.model.Grafo;
public class AgregarNodoStrategy implements EstrategiaClick {

    @Override
    //Al hacer click, se verifica si hay un nodo cercano. Si no lo hay, se crea uno nuevo con un valor único y se posiciona en el panel
    public void ejecutarClick(ClickInfo info) {
        int x = info.getX();
        int y = info.getY();
        Grafo grafo = info.getGrafo();
        PanelGrafo panel = info.getPanel();

        Integer nodoCercano = panel.obtenerNodoCercano(x, y, 20);

        if (nodoCercano == null) {
            int nuevoValor = generarNuevoValor(grafo);
            grafo.insertarNodo(nuevoValor);
            panel.setPosicionNodo(nuevoValor, x, y);
        }
    }

    private int generarNuevoValor(Grafo grafo) {
        int max = 0;
        for (Integer val : grafo.getValoresNodos()) {
            if (val > max) {
                max = val;
            }
        }
        return max + 1;
    }

    @Override
    public String getDescripcion() {
        return "Agregar Nodos - Haz clic en el panel";
    }
}
