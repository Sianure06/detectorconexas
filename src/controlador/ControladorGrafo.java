//Patron controller
package src.controlador;

import java.io.IOException;
import java.util.Map;
import javax.swing.*;

import src.model.Grafo;
import src.vista.PanelGrafo;
import src.vista.VentanaPrincipal;


public class ControladorGrafo {

    private Grafo grafo;
    private VentanaPrincipal vista;
    private GrafoPersistence persistence;
    private Integer nodoSeleccionado;
    private PanelGrafo panelGrafo;

    public ControladorGrafo(Grafo grafo, VentanaPrincipal vista) {
        this.grafo = grafo;
        this.vista = vista;
        this.persistence = new GrafoPersistence();

        this.nodoSeleccionado = null;
    }


    public void setNodoSeleccionado(Integer nodo) {
        this.nodoSeleccionado = nodo;
    }

    public Integer getNodoSeleccionado() {
        return nodoSeleccionado;
    }

    public void detectarComponentes() {
        if (grafo.contarNodos() == 0) {
            vista.actualizarResultados("El grafo está vacío");
            return;
        }

        Map<Integer, Integer> componentes = grafo.componentesConexas();
        int cantidad = componentes.values().stream()
                .mapToInt(Integer::intValue)
                .max().orElse(-1) + 1;

        StringBuilder sb = new StringBuilder();
        sb.append("=== COMPONENTES CONEXAS ===\n");
        sb.append("Total: ").append(cantidad).append("\n\n");

        for (int i = 0; i < cantidad; i++) {
            sb.append("Componente ").append(i).append(": ");
            for (Map.Entry<Integer, Integer> entry : componentes.entrySet()) {
                if (entry.getValue() == i) {
                    sb.append(entry.getKey()).append(" ");
                }
            }
            sb.append("\n");
        }

        vista.mostrarResultados(sb.toString());
    }

    public void calcularGrado() {
        if (grafo.contarNodos() == 0) {
            vista.actualizarResultados("El grafo está vacío");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== INFORMACIÓN DE GRADO ===\n");
        sb.append("Total de nodos: ").append(grafo.contarNodos()).append("\n");
        sb.append("Total de aristas: ").append(grafo.contarAristas()).append("\n\n");

        sb.append("Grado por nodo:\n");
        int sumaGrados = 0;
        for (Integer valor : grafo.getValoresNodos()) {
            int grado = grafo.obtenerGrado(valor);
            sumaGrados += grado;
            sb.append("  Nodo ").append(valor).append(": ").append(grado).append("\n");
        }

        double promedio = (double) sumaGrados / grafo.contarNodos();
        sb.append("\nGrado promedio: ").append(String.format("%.2f", promedio));

        int maxGrado = 0, minGrado = Integer.MAX_VALUE;
        int nodoMax = -1, nodoMin = -1;

        for (Integer valor : grafo.getValoresNodos()) {
            int grado = grafo.obtenerGrado(valor);
            if (grado > maxGrado) {
                maxGrado = grado;
                nodoMax = valor;
            }
            if (grado < minGrado) {
                minGrado = grado;
                nodoMin = valor;
            }
        }

        sb.append("\n\nGrado máximo: ").append(maxGrado).append(" (Nodo ").append(nodoMax).append(")");
        sb.append("\nGrado mínimo: ").append(minGrado).append(" (Nodo ").append(nodoMin).append(")");

        vista.mostrarResultados(sb.toString());
    }

    public void guardarGrafo() {
        if (grafo.contarNodos() == 0) {
            JOptionPane.showMessageDialog(null,
                    "No hay nodos para guardar", "Grafo vacío",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombre = JOptionPane.showInputDialog(null,
                "Ingresa el nombre del archivo (sin extensión):",
                "Guardar Grafo", JOptionPane.QUESTION_MESSAGE);

        if (nombre == null || nombre.trim().isEmpty()) {
            return;
        }

        nombre = nombre.trim().replaceAll("[^a-zA-Z0-9_-]", "_");

        try {
            persistence.guardarGrafo(grafo, vista.getPanelGrafo(), nombre);
            vista.actualizarResultados("Grafo guardado como: " + nombre);
            JOptionPane.showMessageDialog(null,
                    "Grafo guardado exitosamente", "Guardado",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Error al guardar: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cargarGrafo() {
        try {
            String[] archivos = persistence.listarGrafosGuardados();

            if (archivos.length == 0) {
                JOptionPane.showMessageDialog(null,
                        "No hay archivos guardados", "Sin archivos",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String seleccion = (String) JOptionPane.showInputDialog(null,
                    "Selecciona el grafo a cargar:", "Cargar Grafo",
                    JOptionPane.QUESTION_MESSAGE, null, archivos, archivos[0]);

            if (seleccion == null) {
                return;
            }

            persistence.cargarGrafo(grafo, vista.getPanelGrafo(), seleccion);
            vista.actualizarResultados("Grafo cargado: " + seleccion);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Error al cargar: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void limpiarGrafo() {
        int confirm = JOptionPane.showConfirmDialog(null,
                "¿Estás seguro de limpiar todo el grafo?",
                "Confirmar limpieza", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            grafo.limpiarGrafo();
            vista.getPanelGrafo().limpiarPosiciones();
            vista.actualizarResultados("Grafo limpiado");
        }
    }
}
