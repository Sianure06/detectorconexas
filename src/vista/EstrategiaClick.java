//Para añadir el patron strategy
package src.vista;

public interface EstrategiaClick {

    void ejecutarClick(ClickInfo info);

    String getDescripcion();
}
