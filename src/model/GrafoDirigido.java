package src.model;

import java.util.*;

public class GrafoDirigido extends Grafo {
    //Agrega una arista entre dos nodos (en dirigido, solo se agrega en una dirección)
    @Override
    public void agregarArista(int u, int v) {
        Nodo origen = nodos.get(u);
        Nodo destino = nodos.get(v);

        if (origen != null && destino != null && !origen.adyacentes.contains(destino)) {
            origen.adyacentes.add(destino); // dos direcciones: origen -> destino
            notificarAristaAgregada(u, v);
        }
    }
    //Elimina la arista que esta entre dos nodos
    @Override
    public boolean eliminarArista(int u, int v) {
        Nodo origen = nodos.get(u);
        Nodo destino = nodos.get(v);

        if (origen != null && destino != null) {
            if (origen.adyacentes.remove(destino)) { // Solo eliminar de origen
                notificarAristaEliminada(u, v);
                return true;
            }
        }
        return false;
    }

    @Override
    public int contarAristas() {
        int aristas = 0;
        for (Nodo nodo : nodos.values()) {
            aristas += nodo.adyacentes.size(); // Cada arista se cuenta una vez
        }
        return aristas;
    }
    //Consigue las adyacentes
    @Override
    public List<Integer> getAdyacentes(int valor) {
        Nodo nodo = nodos.get(valor);
        if (nodo == null) {
            return new ArrayList<>();
        }

        List<Integer> adyacentes = new ArrayList<>();
        for (Nodo vecino : nodo.adyacentes) {
            adyacentes.add(vecino.valor);
        }
        return adyacentes;
    }
    //Verifica si existe una arista entre nodos a y b (en dirigido, solo se verifica en una dirección)
    @Override
    public boolean existeArista(int u, int v) {
        Nodo origen = nodos.get(u);
        Nodo destino = nodos.get(v);
        if (origen == null || destino == null) {
            return false;
        }

        return origen.adyacentes.contains(destino); // Solo verificar dirección
    }

    // Método específico para grafos dirigidos: obtener predecesores
    public List<Integer> getPredecesores(int valor) {
        List<Integer> predecesores = new ArrayList<>();
        Nodo nodoBuscado = nodos.get(valor);
        if (nodoBuscado == null) {
            return predecesores;
        }

        for (Nodo posibleOrigen : nodos.values()) {
            if (posibleOrigen.adyacentes.contains(nodoBuscado)) {
                predecesores.add(posibleOrigen.valor);
            }
        }
        return predecesores;
    }

    // Calcular grado de entrada (para dirigidos)
    public int obtenerGradoEntrada(int valor) {
        return getPredecesores(valor).size();
    }

    // Calcular grado de salida (para dirigidos)
    public int obtenerGradoSalida(int valor) {
        Nodo nodo = nodos.get(valor);
        return (nodo != null) ? nodo.adyacentes.size() : -1;
    }

    @Override
    public int obtenerGrado(int valor) {
        // En dirigidos, el "grado" podría ser la suma
        return obtenerGradoEntrada(valor) + obtenerGradoSalida(valor);
    }

    // DFS modificado para dirigidos (respeta dirección)
    protected void dfsDirigido(int u, Set<Integer> visitado, Map<Integer, Integer> comp, int compId) {
        visitado.add(u);
        comp.put(u, compId);
        for (Nodo vecino : nodos.get(u).adyacentes) {
            if (!visitado.contains(vecino.valor)) {
                dfsDirigido(vecino.valor, visitado, comp, compId);
            }
        }
    }

    // Componentes fuertemente conexas (algoritmo de Kosaraju)
    public Map<Integer, Integer> componentesFuertementeConexas() {
        // DFS en grafo original para obtener orden de finalización
        Set<Integer> visitado = new HashSet<>();
        Stack<Integer> pila = new Stack<>();

        for (Integer u : nodos.keySet()) {
            if (!visitado.contains(u)) {
                dfsOrden(u, visitado, pila);
            }
        }

        // Crear grafo transpuesto
        GrafoDirigido transpuesto = obtenerTranspuesto();

        //DFS en transpuesto en orden inverso
        visitado.clear();
        Map<Integer, Integer> componentes = new HashMap<>();
        int compId = 0;

        while (!pila.isEmpty()) {
            int u = pila.pop();
            if (!visitado.contains(u)) {
                dfsComponente(u, visitado, componentes, compId, transpuesto);
                compId++;
            }
        }

        return componentes;
    }
    // DFS para obtener orden de finalización 
    private void dfsOrden(int u, Set<Integer> visitado, Stack<Integer> pila) {
        visitado.add(u);
        for (Nodo vecino : nodos.get(u).adyacentes) {
            if (!visitado.contains(vecino.valor)) {
                dfsOrden(vecino.valor, visitado, pila);
            }
        }
        pila.push(u);
    }
    // DFS para marcar componentes fuertemente conexas en el grafo transpuesto
    private void dfsComponente(int u, Set<Integer> visitado,
            Map<Integer, Integer> comp, int compId,
            GrafoDirigido grafoTranspuesto) {
        visitado.add(u);
        comp.put(u, compId);
        for (Integer vecino : grafoTranspuesto.getAdyacentes(u)) {
            if (!visitado.contains(vecino)) {
                dfsComponente(vecino, visitado, comp, compId, grafoTranspuesto);
            }
        }
    }
    //Metodo para conseguir el grafo transpuesto (invertir todas las aristas)
    private GrafoDirigido obtenerTranspuesto() {
        GrafoDirigido transpuesto = new GrafoDirigido();

        // Copiar todos los nodos
        for (Integer valor : nodos.keySet()) {
            transpuesto.insertarNodo(valor);
        }

        // Invertir todas las aristas
        for (Nodo origen : nodos.values()) {
            for (Nodo destino : origen.adyacentes) {
                transpuesto.agregarArista(destino.valor, origen.valor);
            }
        }

        return transpuesto;
    }
}
