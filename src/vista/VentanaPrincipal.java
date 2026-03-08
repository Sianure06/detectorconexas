package src.vista;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javax.swing.*;
import src.controlador.ControladorGrafo;
import src.controlador.GrafoPersistence;
import src.model.Grafo;
import src.model.GrafoDirigido;
import src.model.GrafoNoDirigido;

public class VentanaPrincipal extends JFrame {

    // Referencia al grafo y componentes de la interfaz
    private Grafo grafo;
    private boolean esDirigido = false; //false= no dirigido, true= dirigido (Esta en "no dirigido" por defecto)
    private PanelGrafo panelGrafo;
    private JTextArea areaResultados;
    private ControladorGrafo controlador;
    private static final String CARPETA_SAVES = "src/saves/";
    // Variables para el modo de edición
    private Integer nodoSeleccionado = null;
    private GrafoPersistence persistence;
    private Timer autoSaveTimer;
    private String ultimoArchivoGuardado = null;
    private boolean autoSaveEnabled = false;
    private static final int AUTO_SAVE_INTERVAL = 30000; // 30 segundos
    private EstrategiaClick estrategiaActual;
    private ClickInfo clickInfo;

    public VentanaPrincipal() {
        this.grafo = new GrafoNoDirigido();
        this.panelGrafo = new PanelGrafo(grafo);
        this.persistence = new GrafoPersistence();
        this.controlador = new ControladorGrafo(grafo, this);
        this.clickInfo = new ClickInfo(0, 0, grafo, panelGrafo);

        inicializarUI();
        GestorIcono.aplicarIcono(this);
        iniciarAutoSave();

        setEstrategia(new AgregarNodoStrategy());
    }

    public void setEstrategia(EstrategiaClick estrategia) {
        this.estrategiaActual = estrategia;
        actualizarResultados(estrategia.getDescripcion());
    }

    private void iniciarAutoSave() {
        autoSaveTimer = new Timer(AUTO_SAVE_INTERVAL, e -> {
            if (autoSaveEnabled && grafo.contarNodos() > 0) {
                autoGuardar();
            }
        });
        autoSaveTimer.start();
    }

    private void autoGuardar() {
        if (ultimoArchivoGuardado == null) {
            // Crear nombre automático
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            ultimoArchivoGuardado = "autosave_" + timestamp;
        }

        guardarGrafoConNombre(ultimoArchivoGuardado, true);
    }

    private void guardarGrafoConNombre(String nombreArchivo, boolean esAutoSave) {
        // Crear la carpeta saves si no existe
        File carpeta = new File(CARPETA_SAVES);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        String rutaGrafo = CARPETA_SAVES + nombreArchivo + ".txt";
        String rutaPosiciones = CARPETA_SAVES + nombreArchivo + "_pos.txt";

        try {
            GrafoPersistence.guardarGrafo(grafo, panelGrafo, nombreArchivo); // USAR ESTE
            guardarPosiciones(rutaPosiciones);
            if (!esAutoSave) {
                actualizarResultados("Grafo guardado como: " + nombreArchivo + ".txt");
                JOptionPane.showMessageDialog(this,
                        "Grafo guardado exitosamente en:\n" + rutaGrafo,
                        "Guardado",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException e) {
            if (!esAutoSave) {
                JOptionPane.showMessageDialog(this,
                        "Error al guardar el grafo:\n" + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void inicializarUI() {
        setTitle("Detector de Componentes Conexas - DetConv");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel superior con título
        add(crearPanelSuperior(), BorderLayout.NORTH);

        // Panel central con el grafo
        panelGrafo = new PanelGrafo(grafo);
        panelGrafo.setBackground(Color.WHITE);
        panelGrafo.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        // Agregar listener para clics en el panel
        panelGrafo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (estrategiaActual != null) {
                    // Crear ClickInfo con el grafo actual (no el del constructor)
                    clickInfo = new ClickInfo(e.getX(), e.getY(), grafo, panelGrafo);
                    clickInfo.setNodoSeleccionado(controlador.getNodoSeleccionado());
                    estrategiaActual.ejecutarClick(clickInfo);
                    controlador.setNodoSeleccionado(clickInfo.getNodoSeleccionado());
                }
            }
        });

        add(panelGrafo, BorderLayout.CENTER);

        // Panel derecho con herramientas y resultados
        add(crearPanelHerramientas(), BorderLayout.EAST);

        // Panel inferior con botones de acción
        add(crearPanelInferior(), BorderLayout.SOUTH);
    }

    private ImageIcon crearIconoTitulo() {
        try {
            java.net.URL iconUrl = getClass().getResource("/resources/icons/image.png");
            if (iconUrl != null) {
                ImageIcon icon = new ImageIcon(iconUrl);
                Image img = icon.getImage().getScaledInstance(50, 35, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception e) {
            // Si falla, retornar null
        }
        return null;
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(214, 66, 66));
        panel.setPreferredSize(new Dimension(panel.getWidth(), 75));

        JLabel lblTitulo = new JLabel("DetConv- Detector de Componentes Conexas", crearIconoTitulo(), JLabel.LEFT);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);

        // AÑADIR SELECTOR DE TIPO DE GRAFO
        JPanel panelTipo = new JPanel(new FlowLayout());
        panelTipo.setOpaque(false);

        JRadioButton rbNoDirigido = new JRadioButton("No Dirigido", true);
        JRadioButton rbDirigido = new JRadioButton("Dirigido");

        rbNoDirigido.setForeground(Color.WHITE);
        rbDirigido.setForeground(Color.WHITE);
        rbNoDirigido.setOpaque(false);
        rbDirigido.setOpaque(false);

        ButtonGroup grupoTipo = new ButtonGroup();
        grupoTipo.add(rbNoDirigido);
        grupoTipo.add(rbDirigido);

        rbNoDirigido.addActionListener(e -> cambiarTipoGrafo(false));
        rbDirigido.addActionListener(e -> cambiarTipoGrafo(true));

        panelTipo.add(new JLabel("Tipo: "));
        panelTipo.add(rbNoDirigido);
        panelTipo.add(rbDirigido);

        panel.add(lblTitulo, BorderLayout.WEST);
        panel.add(panelTipo, BorderLayout.EAST);

        return panel;
    }

    private JPanel crearPanelHerramientas() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(300, 0));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Título del panel
        JLabel lblHerramientas = new JLabel("HERRAMIENTAS");
        lblHerramientas.setFont(new Font("Arial", Font.BOLD, 16));
        lblHerramientas.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(lblHerramientas, BorderLayout.NORTH);

        // Panel de modos
        JPanel panelModos = new JPanel(new GridLayout(4, 1, 5, 5));
        panelModos.setBackground(new Color(245, 245, 245));
        panelModos.setBorder(BorderFactory.createTitledBorder("Modo de edición"));

        JRadioButton rbNodo = new JRadioButton("Agregar Nodos", true);
        JRadioButton rbArista = new JRadioButton("Conectar Nodos");
        JRadioButton rbElimNodo = new JRadioButton("Eliminar Nodos");
        JRadioButton rbElimArista = new JRadioButton("Eliminar Aristas");

        ButtonGroup grupoModos = new ButtonGroup();
        grupoModos.add(rbNodo);
        grupoModos.add(rbArista);
        grupoModos.add(rbElimNodo);
        grupoModos.add(rbElimArista);

        rbNodo.addActionListener(e -> setEstrategia(new AgregarNodoStrategy()));
        rbArista.addActionListener(e -> setEstrategia(new ConectarAristaStrategy()));
        rbElimNodo.addActionListener(e -> setEstrategia(new EliminarNodoStrategy()));
        rbElimArista.addActionListener(e -> setEstrategia(new EliminarAristaStrategy()));

        panelModos.add(rbNodo);
        panelModos.add(rbArista);
        panelModos.add(rbElimNodo);
        panelModos.add(rbElimArista);



        // Panel de resultados
        JPanel panelResultados = new JPanel(new BorderLayout());
        panelResultados.setBackground(new Color(245, 245, 245));
        panelResultados.setBorder(BorderFactory.createTitledBorder("Resultados"));

        areaResultados = new JTextArea(10, 20);
        areaResultados.setEditable(false);
        areaResultados.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollResultados = new JScrollPane(areaResultados);
        panelResultados.add(scrollResultados, BorderLayout.CENTER);

        // Panel central que contiene todos los subpaneles
        JPanel panelCentral = new JPanel();
        panelCentral.setBackground(new Color(245, 245, 245));
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));

        panelCentral.add(panelModos);
        panelCentral.add(Box.createRigidArea(new Dimension(0, 10))); // Espaciado
  
        panelCentral.add(Box.createRigidArea(new Dimension(0, 10))); // Espaciado
        panelCentral.add(panelResultados);

        panel.add(panelCentral, BorderLayout.CENTER);

        return panel;
    }

    // Agregar botón de exportar en crearPanelInferior()
    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(new Color(240, 240, 240));

        JButton btnDetectar = new JButton("Detectar Componentes");
        JButton btnGrado = new JButton("Calcular Grado");
        JButton btnLimpiar = new JButton("Limpiar Grafo");
        JButton btnGuardar = new JButton("Guardar Grafo");
        JButton btnCargar = new JButton("Cargar Grafo");
        JButton btnExportar = new JButton("Exportar CSV");
        JButton btnInfo = new JButton("Información");

        // Eventos
        btnDetectar.addActionListener(e -> detectarComponentes());
        btnGrado.addActionListener(e -> calcularGrado());
        btnGuardar.addActionListener(e -> guardarGrafo());
        btnCargar.addActionListener(e -> cargarGrafo());
        btnLimpiar.addActionListener(e -> limpiarGrafo());
        btnInfo.addActionListener(e -> mostrarInformacion());
        btnExportar.addActionListener(e -> exportarResultados());

        panel.add(btnDetectar);
        panel.add(btnGrado);
        panel.add(btnExportar);
        panel.add(btnGuardar);
        panel.add(btnCargar);
        panel.add(btnLimpiar);
        panel.add(btnInfo);

        return panel;
    }

    private void detectarComponentes() {
        Map<Integer, Integer> componentes = grafo.componentesConexas();
        int cantidad = componentes.values().stream()
                .mapToInt(Integer::intValue)
                .max().orElse(-1) + 1;

        StringBuilder sb = new StringBuilder();
        sb.append("=== COMPONENTES CONEXAS ===\n");
        sb.append("Total: ").append(cantidad).append("\n\n");

        // Agrupar por componente
        for (int i = 0; i < cantidad; i++) {
            sb.append("Componente ").append(i).append(": ");
            for (Map.Entry<Integer, Integer> entry : componentes.entrySet()) {
                if (entry.getValue() == i) {
                    sb.append(entry.getKey()).append(" ");
                }
            }
            sb.append("\n");
        }

        areaResultados.setText(sb.toString());
    }

    private void limpiarGrafo() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de limpiar todo el grafo?\nSe perderán todos los nodos y aristas.",
                "Confirmar limpieza",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            grafo.limpiarGrafo();
            panelGrafo.limpiarPosiciones();
            nodoSeleccionado = null;
            areaResultados.setText("");
            panelGrafo.repaint();
            actualizarResultados("Grafo limpiado");
        }
    }

    private void mostrarInformacion() {
        JOptionPane.showMessageDialog(this,
                "Detector de Componentes Conexas (DetConv)\n"
                + "Versión 2.0\n\n"
                + "INSTRUCCIONES:\n"
                + "1. 'Agregar Nodos': Haz clic en el panel para crear nodos\n"
                + "2. 'Conectar Nodos': Haz clic en dos nodos para crear una arista\n"
                + "3. 'Eliminar Nodos': Haz clic en un nodo para eliminarlo\n"
                + "4. 'Eliminar Aristas': Haz clic cerca de una arista para eliminarla\n\n"
                + "BOTONES:\n"
                + "• Detectar Componentes: Muestra las componentes conexas\n"
                + "• Calcular Grado: Muestra información de grados del grafo\n"
                + "• Guardar Grafo: Guarda el grafo actual en un archivo\n"
                + "• Cargar Grafo: Carga un grafo desde un archivo\n"
                + "• Limpiar Grafo: Elimina todos los nodos y aristas\n"
                + "• Exportar Resultados: Guarda los resultados en un archivo\n"
                + "• Información: Muestra este mensaje\n\n"
                + "© 2026 - Proyecto de Grafos -Samuel González C.I: 32.095.063",
                "Acerca de DetConv",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void actualizarResultados(String mensaje) {
        areaResultados.setText(mensaje + "\n" + areaResultados.getText());
    }

    public void mostrarResultados(String resultados) {
        areaResultados.setText(resultados);
    }

    private void guardarGrafo() {
        if (grafo.contarNodos() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No hay nodos para guardar", "Grafo vacío",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombreArchivo = JOptionPane.showInputDialog(this,
                "Ingresa el nombre del archivo (sin extensión):",
                "Guardar Grafo", JOptionPane.QUESTION_MESSAGE);

        if (nombreArchivo == null || nombreArchivo.trim().isEmpty()) {
            return;
        }

        nombreArchivo = nombreArchivo.trim().replaceAll("[^a-zA-Z0-9_-]", "_");

        try {
            persistence.guardarGrafo(grafo, panelGrafo, nombreArchivo);
            actualizarResultados("Grafo guardado como: " + nombreArchivo);
            JOptionPane.showMessageDialog(this,
                    "Grafo guardado exitosamente", "Guardado",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarGrafo() {
        try {
            String[] archivos = persistence.listarGrafosGuardados();

            if (archivos.length == 0) {
                JOptionPane.showMessageDialog(this,
                        "No hay archivos guardados", "Sin archivos",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String seleccion = (String) JOptionPane.showInputDialog(this,
                    "Selecciona el grafo a cargar:", "Cargar Grafo",
                    JOptionPane.QUESTION_MESSAGE, null, archivos, archivos[0]);

            if (seleccion == null) {
                return;
            }

            persistence.cargarGrafo(grafo, panelGrafo, seleccion);
            actualizarResultados("Grafo cargado: " + seleccion);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

// Método auxiliar para guardar posiciones de nodos
    private void guardarPosiciones(String archivo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(archivo))) {
            for (Integer valor : grafo.getValoresNodos()) {
                Point p = panelGrafo.getPosicionNodo(valor);
                if (p != null) {
                    writer.println(valor + ":" + p.x + "," + p.y);
                }
            }
        } catch (IOException e) {
            System.err.println("No se pudieron guardar las posiciones: " + e.getMessage());
        }
    }

    
    // Método para exportar resultados
private void exportarResultados() {
        if (grafo.contarNodos() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No hay datos para exportar",
                    "Grafo vacío",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Primero preguntar qué tipo de exportación
        String[] opciones = {"Componentes Conexas (CSV)", "Estadísticas Completas (CSV)"};
        int tipoExportacion = JOptionPane.showOptionDialog(this,
                "¿Qué deseas exportar?",
                "Exportar Resultados",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]);

        if (tipoExportacion == -1) {
            return; // Usuario canceló
        }

        // Luego preguntar el nombre del archivo (como en guardarGrafo)
        String nombreArchivo = JOptionPane.showInputDialog(this,
                "Ingresa el nombre del archivo (sin extensión):",
                "Exportar CSV",
                JOptionPane.QUESTION_MESSAGE);

        if (nombreArchivo == null || nombreArchivo.trim().isEmpty()) {
            return; // Usuario canceló o no ingresó nombre
        }

        // Limpiar el nombre (solo caracteres seguros)
        nombreArchivo = nombreArchivo.trim().replaceAll("[^a-zA-Z0-9_-]", "_");

        // Asegurar que tiene extensión .csv
        if (!nombreArchivo.toLowerCase().endsWith(".csv")) {
            nombreArchivo += ".csv";
        }

        // Crear la carpeta saves si no existe
        File carpeta = new File(CARPETA_SAVES);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        // Ruta completa del archivo
        String rutaCompleta = CARPETA_SAVES + nombreArchivo;

        try {
            if (tipoExportacion == 0) {
                // Exportar componentes conexas
                Map<Integer, Integer> componentes = grafo.componentesConexas();
                GrafoPersistence.exportarResultadosCSV(componentes, rutaCompleta);
                actualizarResultados("Componentes exportados como: " + nombreArchivo);
            } else {
                // Exportar estadísticas completas
                GrafoPersistence.exportarEstadisticasCSV(grafo, rutaCompleta);
                actualizarResultados("Estadísticas exportadas como: " + nombreArchivo);
            }

            JOptionPane.showMessageDialog(this,
                    "Resultados exportados exitosamente a:\n" + rutaCompleta,
                    "Exportación exitosa",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al exportar:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public PanelGrafo getPanelGrafo() {
        return panelGrafo;  // Devuelve la referencia al panel
    }
    // Método para cambiar tipo de grafo

    private void cambiarTipoGrafo(boolean dirigido) {
        if (this.esDirigido == dirigido) {
            return;
        }

        // Guardar estado actual si hay nodos
        if (grafo.contarNodos() > 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Cambiar tipo de grafo?\nSe perderá el grafo actual.",
                    "Confirmar cambio",
                    JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }

        this.esDirigido = dirigido;

        // Crear nuevo grafo del tipo apropiado
        Grafo nuevoGrafo = dirigido ? new GrafoDirigido() : new GrafoNoDirigido();

        // Actualizar referencias
        this.grafo = nuevoGrafo;
        this.panelGrafo.setGrafo(nuevoGrafo);
        this.controlador = new ControladorGrafo(nuevoGrafo, this);

        actualizarResultados("Cambiado a grafo " + (dirigido ? "DIRIGIDO" : "NO DIRIGIDO"));
    }

// Modificar el método calcularGrado para mostrar información específica
    private void calcularGrado() {
        if (grafo.contarNodos() == 0) {
            actualizarResultados("El grafo está vacío");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== INFORMACIÓN DE GRADO ===\n");
        sb.append("Tipo: ").append(esDirigido ? "DIRIGIDO" : "NO DIRIGIDO").append("\n");
        sb.append("Total de nodos: ").append(grafo.contarNodos()).append("\n");
        sb.append("Total de aristas: ").append(grafo.contarAristas()).append("\n\n");

        if (esDirigido) {
            GrafoDirigido gd = (GrafoDirigido) grafo;
            sb.append("Grado por nodo (entrada/salida/total):\n");
            int sumaTotal = 0;

            for (Integer valor : grafo.getValoresNodos()) {
                int entrada = gd.obtenerGradoEntrada(valor);
                int salida = gd.obtenerGradoSalida(valor);
                int total = entrada + salida;
                sumaTotal += total;
                sb.append(String.format("  Nodo %d: E=%d, S=%d, T=%d\n",
                        valor, entrada, salida, total));
            }

            double promedio = (double) sumaTotal / grafo.contarNodos();
            sb.append("\nGrado promedio total: ").append(String.format("%.2f", promedio));

        } else {
            // Código original para no dirigidos
            sb.append("Grado por nodo:\n");
            int sumaGrados = 0;
            for (Integer valor : grafo.getValoresNodos()) {
                int grado = grafo.obtenerGrado(valor);
                sumaGrados += grado;
                sb.append("  Nodo ").append(valor).append(": ").append(grado).append("\n");
            }

            double promedio = (double) sumaGrados / grafo.contarNodos();
            sb.append("\nGrado promedio: ").append(String.format("%.2f", promedio));
        }

        areaResultados.setText(sb.toString());
    }
}
