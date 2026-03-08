package src.vista;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

import src.model.Grafo;
import src.model.GrafoDirigido;
import src.model.GrafoListener;
import src.model.Nodo;

public class PanelGrafo extends JPanel implements GrafoListener {

    private Grafo grafo;
    private Map<Integer, Point> posicionesNodos;
    private double escala = 1.0;
    private double offsetX = 0, offsetY = 0;
    private Point ultimoArrastre = null;

    public PanelGrafo(Grafo grafo) {
        this.grafo = grafo;
        this.posicionesNodos = new HashMap<>();
        this.grafo.addListener(this);

        // Inicializar valores de zoom 
        this.escala = 1.0;
        this.offsetX = 0;
        this.offsetY = 0;

        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        offsetX = 0;
        offsetY = 0;
        escala = 1.0;

        g2.translate(offsetX, offsetY);
        g2.scale(escala, escala);

        // Dibujar aristas
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));

        boolean esDirigido = grafo instanceof GrafoDirigido;

        // Para DEBUG: Mostrar el tipo en la esquina (más grande y visible)
        g2.setColor(Color.BLUE);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("TIPO: " + (esDirigido ? "DIRIGIDO" : "NO DIRIGIDO"), 20, 40);
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.PLAIN, 12));

        for (Nodo nodo : grafo.getTodosNodos()) {
            Point p1 = posicionesNodos.get(nodo.getValor());
            if (p1 == null) {
                continue;
            }

            for (Nodo vecino : nodo.getAdyacentes()) {
                Point p2 = posicionesNodos.get(vecino.getValor());
                if (p2 == null) {
                    continue;
                }

                if (esDirigido) {
                    // En dirigido: dibujar TODAS las aristas con flecha
                    dibujarFlecha(g2, p1.x, p1.y, p2.x, p2.y);
                } else {
                    // En no dirigido: dibujar solo una vez (nodo menor)
                    if (nodo.getValor() < vecino.getValor()) {
                        g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                    }
                }
            }
        }

        // Dibujar nodos
        for (Integer valor : grafo.getValoresNodos()) {
            Point p = posicionesNodos.get(valor);
            if (p != null) {
                g2.setColor(Color.RED);
                g2.fillOval(p.x - 15, p.y - 15, 30, 30);
                g2.setColor(Color.BLACK);
                g2.drawOval(p.x - 15, p.y - 15, 30, 30);
                g2.setColor(Color.WHITE);

                FontMetrics fm = g2.getFontMetrics();
                String texto = String.valueOf(valor);
                int anchoTexto = fm.stringWidth(texto);
                g2.drawString(texto, p.x - anchoTexto / 2, p.y + fm.getAscent() / 3);
            }
        }
    }

// Método para dibujar flecha
    private void dibujarFlecha(Graphics2D g2, int x1, int y1, int x2, int y2) {
        // Guardar el color original
        Color colorOriginal = g2.getColor();

        // Dibujar línea más gruesa para mejor visibilidad
        g2.setStroke(new BasicStroke(2));
        g2.setColor(Color.BLACK);
        g2.drawLine(x1, y1, x2, y2);

        // Calcular dirección
        double dx = x2 - x1;
        double dy = y2 - y1;

        // Evitar división por cero (nodos en la misma posición)
        if (dx == 0 && dy == 0) {
            return;
        }

        double angulo = Math.atan2(dy, dx);

        // Tamaño de la flecha (un poco más grande para que se note)
        int flechaSize = 15;

        // Calcular puntos de la flecha (en el destino, pero un poco antes para que no se superponga al nodo)
        int offset = 15; // Retroceder un poco para que la flecha no quede dentro del nodo
        int xPunta = (int) (x2 - offset * Math.cos(angulo));
        int yPunta = (int) (y2 - offset * Math.sin(angulo));

        int xFlecha1 = (int) (xPunta - flechaSize * Math.cos(angulo - Math.PI / 6));
        int yFlecha1 = (int) (yPunta - flechaSize * Math.sin(angulo - Math.PI / 6));
        int xFlecha2 = (int) (xPunta - flechaSize * Math.cos(angulo + Math.PI / 6));
        int yFlecha2 = (int) (yPunta - flechaSize * Math.sin(angulo + Math.PI / 6));

        // Dibujar punta de flecha (rellena de negro para que resalte)
        g2.setColor(Color.BLACK);
        g2.fillPolygon(new int[]{xPunta, xFlecha1, xFlecha2},
                new int[]{yPunta, yFlecha1, yFlecha2}, 3);

        // Opcional: borde blanco para contraste (opcional)
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1));
        g2.drawPolygon(new int[]{xPunta, xFlecha1, xFlecha2},
                new int[]{yPunta, yFlecha1, yFlecha2}, 3);

        // Restaurar color y trazo originales
        g2.setColor(colorOriginal);
        g2.setStroke(new BasicStroke(1));
    }

    // Implementación de GrafoListener
    @Override
    public void nodoAgregado(int valor) {
        // No hacer nada aquí (la posición se establece aparte)
        repaint();
    }

    @Override
    public void nodoEliminado(int valor) {
        posicionesNodos.remove(valor);
        repaint();
    }

    @Override
    public void aristaAgregada(int nodo1, int nodo2) {
        repaint();
    }

    @Override
    public void aristaEliminada(int nodo1, int nodo2) {
        repaint();
    }

    @Override
    public void grafoLimpiado() {
        posicionesNodos.clear();
        repaint();
    }

    // Métodos de utilidad (igual que antes)
    public Point screenToWorld(int screenX, int screenY) {
        return new Point(
                (int) ((screenX - offsetX) / escala),
                (int) ((screenY - offsetY) / escala)
        );
    }

    public Integer obtenerNodoCercano(int screenX, int screenY, int radio) {
        Point mundo = screenToWorld(screenX, screenY);

        // Asegurar que el radio es al menos 1
        double radioAjustado = Math.max(radio / escala, 5.0); // Mínimo 5 píxeles

        Integer nodoMasCercano = null;
        double distanciaMinima = Double.MAX_VALUE;

        for (Map.Entry<Integer, Point> entry : posicionesNodos.entrySet()) {
            Point p = entry.getValue();
            double distancia = Math.hypot(p.x - mundo.x, p.y - mundo.y);

            if (distancia <= radioAjustado && distancia < distanciaMinima) {
                distanciaMinima = distancia;
                nodoMasCercano = entry.getKey();
            }
        }

        return nodoMasCercano; // Devuelve el más cercano, no solo el primero
    }

    public Arista obtenerAristaCercana(int screenX, int screenY, int tolerancia) {
        Point mundo = screenToWorld(screenX, screenY);
        double toleranciaAjustada = tolerancia / escala;

        for (Nodo nodo : grafo.getTodosNodos()) {
            Point p1 = posicionesNodos.get(nodo.getValor());
            if (p1 == null) {
                continue;
            }

            for (Nodo vecino : nodo.getAdyacentes()) {
                if (vecino.getValor() <= nodo.getValor()) {
                    continue;
                }

                Point p2 = posicionesNodos.get(vecino.getValor());
                if (p2 == null) {
                    continue;
                }

                double distancia = distanciaPuntoLinea(
                        mundo.x, mundo.y, p1.x, p1.y, p2.x, p2.y);

                if (distancia <= toleranciaAjustada
                        && puntoEnSegmento(mundo.x, mundo.y, p1.x, p1.y, p2.x, p2.y)) {
                    return new Arista(nodo.getValor(), vecino.getValor());
                }
            }
        }
        return null;
    }

    private double distanciaPuntoLinea(int px, int py, int x1, int y1, int x2, int y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double length = Math.hypot(dx, dy);
        if (length == 0) {
            return Double.MAX_VALUE;
        }
        return Math.abs((px - x1) * dy - (py - y1) * dx) / length;
    }

    private boolean puntoEnSegmento(int px, int py, int x1, int y1, int x2, int y2) {
        int minX = Math.min(x1, x2) - 15;
        int maxX = Math.max(x1, x2) + 15;
        int minY = Math.min(y1, y2) - 15;
        int maxY = Math.max(y1, y2) + 15;
        return px >= minX && px <= maxX && py >= minY && py <= maxY;
    }

    public void setPosicionNodo(int valor, int x, int y) {
        posicionesNodos.put(valor, new Point(x, y));
        repaint();
    }

    public Point getPosicionNodo(int valor) {
        return posicionesNodos.get(valor);
    }

    public void limpiarPosiciones() {
        posicionesNodos.clear();
        repaint();
    }

    public void setGrafo(Grafo grafo) {
        // Remover listener del grafo anterior
        if (this.grafo != null) {
            this.grafo.removeListener(this);
        }

        // Asignar nuevo grafo
        this.grafo = grafo;

        // Agregar listener al nuevo grafo
        if (this.grafo != null) {
            this.grafo.addListener(this);
        }

        // Limpiar posiciones y repintar
        limpiarPosiciones();
        repaint(); // Forzar repintado inmediato
    }

    public void zoomIn() {
        escala = Math.min(escala * 1.1, 3.0);
        repaint();
    }

    public void zoomOut() {
        escala = Math.max(escala * 0.9, 0.3);
        repaint();
    }

    public void zoomFit() {
        if (posicionesNodos.isEmpty()) {
            return;
        }

        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

        for (Point p : posicionesNodos.values()) {
            minX = Math.min(minX, p.x);
            minY = Math.min(minY, p.y);
            maxX = Math.max(maxX, p.x);
            maxY = Math.max(maxY, p.y);
        }

        int anchoGrafo = maxX - minX + 60;
        int altoGrafo = maxY - minY + 60;

        double escalaX = getWidth() / (double) anchoGrafo;
        double escalaY = getHeight() / (double) altoGrafo;
        escala = Math.min(escalaX, escalaY) * 0.9;

        offsetX = (getWidth() - (minX + maxX) * escala) / 2;
        offsetY = (getHeight() - (minY + maxY) * escala) / 2;

        repaint();
    }

    public void getPanelGrafo() {
        repaint();
    }
}
// Clase para representar una arista entre dos nodos

class Arista {

    int nodo1, nodo2;

    public Arista(int nodo1, int nodo2) {
        this.nodo1 = Math.min(nodo1, nodo2);
        this.nodo2 = Math.max(nodo1, nodo2);
    }

    public int getNodo1() {
        return nodo1;
    }

    public int getNodo2() {
        return nodo2;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Arista) {
            Arista a = (Arista) obj;
            return this.nodo1 == a.nodo1 && this.nodo2 == a.nodo2;
        }
        return false;
    }
    // Método para actualizar la referencia del grafo

    @Override
    public String toString() {
        return nodo1 + "-" + nodo2;
    }

}
