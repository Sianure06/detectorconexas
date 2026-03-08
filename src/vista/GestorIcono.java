package src.vista;

import javax.swing.*;

import javax.swing.JDialog;
import javax.swing.JFrame;
import java.awt.*;

public class GestorIcono {

    public static void aplicarIcono(Window ventana) { 
        try {
            ImageIcon icono = new ImageIcon(GestorIcono.class.getResource("/resources/icons/Icon.png"));
            if (ventana instanceof JFrame) {
                ((JFrame) ventana).setIconImage(icono.getImage());
            } else if (ventana instanceof JDialog) {
                ((JDialog) ventana).setIconImage(icono.getImage());
            }
        } catch (Exception e) {
            System.err.println("Error al cargar o: " + e.getMessage());
        }
    }
}
