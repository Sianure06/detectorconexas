package src.model;


import java.util.*;

public class Nodo {

    protected int valor;
    public List<Nodo> adyacentes;

    //Constructor
    public Nodo(int valor) {
        this.valor = valor;
        this.adyacentes = new ArrayList<>();
    }
    // Getter para el valor

    public int getValor() {
        return valor;
    }

    // Getter para los adyacentes (copia de seguridad)
    public List<Nodo> getAdyacentes() {
        return new ArrayList<>(adyacentes);
    }

    // Método para obtener los valores de los nodos adyacentes
    public List<Integer> getValoresAdyacentes() {
        List<Integer> valores = new ArrayList<>();
        for (Nodo vecino : adyacentes) {
            valores.add(vecino.valor);
        }
        return valores;
    }

    // Método para obtener el grado del nodo
    public int getGrado() {
        return adyacentes.size();
    }

    @Override
    public String toString() {
        return "Nodo{" + "valor=" + valor + ", grado=" + adyacentes.size() + '}';
    }
}
