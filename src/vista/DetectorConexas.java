package src.vista;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class DetectorConexas {

    public static void main(String[] args) {
        // Configura la visualizacion del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Mostrar splash screen y luego la ventana principal
        SwingUtilities.invokeLater(() -> {
            try {
                // Crear ventana principal (pero sin mostrarla aún)
                VentanaPrincipal ventana = new VentanaPrincipal();

                // Mostrar splash y luego la ventana principal
                SplashScreen.mostrarYContinuar(ventana);
            } catch (Exception e) {
                e.printStackTrace();
                javax.swing.JOptionPane.showMessageDialog(null,
                        "Error al iniciar la aplicación:\n" + e.getMessage(),
                        "Error de Inicio",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
