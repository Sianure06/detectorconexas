package src.vista;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SplashScreen extends JWindow {

    private Timer fadeInTimer;
    private float opacity = 0f;
    private JLabel lblIcono;

    public SplashScreen(int duracion) {
        setBackground(new Color(0, 0, 0, 0));
        setAlwaysOnTop(true);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        java.net.URL iconUrl = getClass().getResource("/resources/icons/Icon.png");

        if (iconUrl != null) {
            ImageIcon iconoOriginal = new ImageIcon(iconUrl);
            Image imagenEscalada = iconoOriginal.getImage()
                    .getScaledInstance(800, 800, Image.SCALE_SMOOTH);
            ImageIcon iconoEscalado = new ImageIcon(imagenEscalada);

            // Crear label con el icono (sin sombras para mantenerlo simple)
            lblIcono = new JLabel(iconoEscalado);
            lblIcono.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(lblIcono, BorderLayout.CENTER);
        }

        setContentPane(panel);
        setSize(850, 850);
        setLocationRelativeTo(null);

        // Iniciar con opacidad 0
        setOpacity(0f);
        setVisible(true);

        // SOLO FADE IN (sin fade out)
        iniciarFadeIn();

        // Timer simple para cerrar después de la duración
        Timer closeTimer = new Timer(duracion, e -> {
            dispose(); // Cerrar directamente sin fade out
        });
        closeTimer.setRepeats(false);
        closeTimer.start();
    }

    private void iniciarFadeIn() {
        fadeInTimer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity += 0.05f;
                if (opacity >= 1f) {
                    opacity = 1f;
                    fadeInTimer.stop();
                }
                setOpacity(opacity);
            }
        });
        fadeInTimer.start();
    }

    public static void mostrarYContinuar(JFrame ventanaPrincipal) {
        SplashScreen splash = new SplashScreen(2000); // 2 segundos

        // Timer para mostrar la ventana principal después del splash
        Timer timer = new Timer(2000, e -> {
            ventanaPrincipal.setVisible(true);
        });
        timer.setRepeats(false);
        timer.start();
    }
}
