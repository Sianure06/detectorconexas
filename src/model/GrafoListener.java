package src.model;
//Para añadir el patron observer
public interface GrafoListener {
    void nodoAgregado(int valor);
    void nodoEliminado(int valor);
    void aristaAgregada(int nodo1, int nodo2);
    void aristaEliminada(int nodo1, int nodo2);
    void grafoLimpiado();
}