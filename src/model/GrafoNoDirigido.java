package src.model;

import java.util.*;

public class GrafoNoDirigido extends Grafo {
    // Agrega una arista entre dos nodos (en no dirigido, se agrega en ambos sentidos)
    @Override
    public void agregarArista(int u, int v) {
        Nodo nx = nodos.get(u);
        Nodo ny = nodos.get(v);
        if (nx != null && ny != null && !nx.adyacentes.contains(ny)) {
            nx.adyacentes.add(ny);
            ny.adyacentes.add(nx);
            notificarAristaAgregada(u, v);
        }
    }
    //Elimina la arista que esta entre dos nodos 
    @Override
    public boolean eliminarArista(int u, int v) {
        Nodo nx = nodos.get(u);
        Nodo ny = nodos.get(v);
        if (nx != null && ny != null) {
            if (nx.adyacentes.remove(ny) && ny.adyacentes.remove(nx)) {
                notificarAristaEliminada(u, v);
                return true;
            }
        }
        return false;
    }
    //Cuenta las aristas únicas
    @Override
    public int contarAristas() {
        int aristas = 0;
        Set<String> aristasUnicas = new HashSet<>();

        for (Nodo nodo : nodos.values()) {
            for (Nodo vecino : nodo.adyacentes) {
                String aristaId = Math.min(nodo.valor, vecino.valor) + "-"
                        + Math.max(nodo.valor, vecino.valor);
                aristasUnicas.add(aristaId);
            }
        }
        return aristasUnicas.size();
    }
    // Consigue los nodos adyacentes a un nodo dado 
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
    //Busca si existe una arista entre u y v (en no dirigido es simétrico)
    @Override
    public boolean existeArista(int u, int v) {
        Nodo nu = nodos.get(u);
        Nodo nv = nodos.get(v);
        if (nu == null || nv == null) {
            return false;
        }

        return nu.adyacentes.contains(nv);
    }
}
