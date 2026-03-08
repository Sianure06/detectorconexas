/*Funciones para el proyecto:
-insertarNodo
-agregarArista
-componentesConexas
-buscarNodo
-eliminarNodo
-eliminarArista
-obtenerGrado
-obtenerTodosLosNodos
-contarNodos
-contarAristas
-limpiarGrafo
-guardarGrafo
-cargarGrafo
-exportarResultados */
package src.model;

import java.util.*;

public abstract class Grafo {
    protected Map<Integer, Nodo> nodos;
    protected List<GrafoListener> listeners;

    public Grafo() {
        this.nodos = new HashMap<>();
        this.listeners = new ArrayList<>();
    }

    // Métodos de listeners (igual)
    public void addListener(GrafoListener listener) {
        listeners.add(listener);
    }

    public void removeListener(GrafoListener listener) {
        listeners.remove(listener);
    }

    protected void notificarNodoAgregado(int valor) {
        for (GrafoListener l : listeners) l.nodoAgregado(valor);
    }

    protected void notificarNodoEliminado(int valor) {
        for (GrafoListener l : listeners) l.nodoEliminado(valor);
    }

    protected void notificarAristaAgregada(int u, int v) {
        for (GrafoListener l : listeners) l.aristaAgregada(u, v);
    }

    protected void notificarAristaEliminada(int u, int v) {
        for (GrafoListener l : listeners) l.aristaEliminada(u, v);
    }

    protected void notificarGrafoLimpiado() {
        for (GrafoListener l : listeners) l.grafoLimpiado();
    }

    // Métodos abstractos que las subclases deben implementar
    public abstract void agregarArista(int u, int v);
    public abstract boolean eliminarArista(int u, int v);
    public abstract int contarAristas();
    public abstract List<Integer> getAdyacentes(int valor);
    public abstract boolean existeArista(int u, int v);

    // Métodos concretos (comunes a todos los grafos)
    public void insertarNodo(int valor) {
        if (!nodos.containsKey(valor)) {
            nodos.put(valor, new Nodo(valor));
            notificarNodoAgregado(valor);
        }
    }

    public boolean eliminarNodo(int valor) {
        if (nodos.containsKey(valor)) {
            Nodo nodoEliminar = nodos.get(valor);
            List<Nodo> adyacentesCopia = new ArrayList<>(nodoEliminar.adyacentes);

            for (Nodo vecino : adyacentesCopia) {
                vecino.adyacentes.remove(nodoEliminar);
            }

            nodos.remove(valor);
            notificarNodoEliminado(valor);
            return true;
        }
        return false;
    }

    public Map<Integer, Integer> componentesConexas() {
        Set<Integer> visitado = new HashSet<>();
        Map<Integer, Integer> comp = new HashMap<>();
        int compId = 0;
        
        for (Integer u : nodos.keySet()) {
            if (!visitado.contains(u)) {
                dfsComp(u, visitado, comp, compId);
                compId++;
            }
        }
        return comp;
    }

    protected void dfsComp(int u, Set<Integer> visitado, Map<Integer, Integer> comp, int compId) {
        visitado.add(u);
        comp.put(u, compId);
        for (Nodo vecino : nodos.get(u).adyacentes) {
            if (!visitado.contains(vecino.valor)) {
                dfsComp(vecino.valor, visitado, comp, compId);
            }
        }
    }

    public int obtenerGrado(int valor) {
        Nodo nodo = nodos.get(valor);
        return (nodo != null) ? nodo.adyacentes.size() : -1;
    }

    public Set<Integer> getValoresNodos() {
        return nodos.keySet();
    }

    public Collection<Nodo> getTodosNodos() {
        return nodos.values();
    }

    public Nodo getNodo(int valor) {
        return nodos.get(valor);
    }

    public boolean existeNodo(int valor) {
        return nodos.containsKey(valor);
    }

    public int contarNodos() {
        return nodos.size();
    }

    public void limpiarGrafo() {
        nodos.clear();
        notificarGrafoLimpiado();
    }
}