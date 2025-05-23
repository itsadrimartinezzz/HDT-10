package Datos;

// Importaciones necesarias para la GUI
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Main {
    // Se instancia un objeto global del grafo
    private static Grafo grafo = new Grafo();

    // Método principal del programa
    public static void main(String[] args) throws Exception {
        // Carga el grafo desde el archivo "logistica.txt"
        grafo.cargarDesdeArchivo("logistica.txt");

        // Ejecuta la creación del menú gráfico en el hilo de la interfaz
        SwingUtilities.invokeLater(() -> crearMenu());
    }

    // Crea el menú gráfico principal con botones para cada funcionalidad
    public static void crearMenu() {
        JFrame frame = new JFrame("🚛 Sistema Logístico");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null); // Centra la ventana

        // Panel con diseño de rejilla para los botones del menú
        JPanel panel = new JPanel(new GridLayout(8, 1, 10, 10)); // Increased to 8 for new button
        JLabel titulo = new JLabel("MENÚ PRINCIPAL", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titulo);

        // Botones del menú con emojis para mejor visualización
        JButton btn1 = new JButton("1. 🚗 Camino más corto entre ciudades");
        JButton btn2 = new JButton("2. 🏙️ Ciudad centro del grafo");
        JButton btn3 = new JButton("3. 🔧 Modificar grafo");
        JButton btn4 = new JButton("4. 🌡️ Cambiar temperatura actual");
        JButton btn5 = new JButton("5. 🌦️ Cambiar clima actual");
        JButton btn6 = new JButton("6. 📊 Mostrar matriz de distancias");
        JButton btn7 = new JButton("7. ❌ Salir");

        // Acciones al presionar cada botón
        btn1.addActionListener(e -> mostrarRutaMasCorta());
        btn2.addActionListener(e -> JOptionPane.showMessageDialog(null, "Centro del grafo: " + grafo.centroDelGrafo()));
        btn3.addActionListener(e -> mostrarModificarGrafo());
        btn4.addActionListener(e -> cambiarTemperatura());
        btn5.addActionListener(e -> cambiarClima());
        btn6.addActionListener(e -> grafo.mostrarMatrizEnVentana());
        btn7.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "💖 Gracias por usar el sistema ¡Hasta pronto!");
            System.exit(0);
        });

        // Agrega los botones al panel
        panel.add(btn1);
        panel.add(btn2);
        panel.add(btn3);
        panel.add(btn4);
        panel.add(btn5);
        panel.add(btn6);
        panel.add(btn7);

        // Muestra la ventana principal
        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }

    // Solicita al usuario las ciudades de origen y destino y muestra el camino más corto
    private static void mostrarRutaMasCorta() {
        String origen = JOptionPane.showInputDialog("🌷 Ciudad origen:");
        String destino = JOptionPane.showInputDialog("🌷 Ciudad destino:");
        if (origen == null || destino == null || origen.isEmpty() || destino.isEmpty()) {
            JOptionPane.showMessageDialog(null, "🚫 Por favor, ingrese nombres de ciudades válidos.");
            return;
        }
        if (!grafo.getCiudadIndices().containsKey(origen) || !grafo.getCiudadIndices().containsKey(destino)) {
            JOptionPane.showMessageDialog(null, "🚫 Una o ambas ciudades no existen en el grafo.");
            return;
        }
        List<String> camino = grafo.caminoMasCorto(origen, destino);
        if (camino.isEmpty())
            JOptionPane.showMessageDialog(null, "🚫 No hay ruta disponible.");
        else
            JOptionPane.showMessageDialog(null, "🦋 Ruta más corta: " + String.join(" ➡️ ", camino) + 
                "\n⏱️ Tiempo total: " + grafo.getDistancia(origen, destino) + " horas");
    }

    // Permite al usuario agregar o eliminar conexiones entre ciudades
    private static void mostrarModificarGrafo() {
        String[] opciones = {
            "❌ Interrumpir tráfico (eliminar conexión)", 
            "➕ Agregar conexión nueva"
        };
        int seleccion = JOptionPane.showOptionDialog(null, "Elige una opción:",
                "🔧 Modificar grafo", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, opciones, opciones[0]);

        // Si elige eliminar
        if (seleccion == 0) {
            String origen = JOptionPane.showInputDialog("🔹 Ciudad origen:");
            String destino = JOptionPane.showInputDialog("🔹 Ciudad destino:");
            if (origen == null || destino == null || origen.isEmpty() || destino.isEmpty()) {
                JOptionPane.showMessageDialog(null, "🚫 Por favor, ingrese nombres de ciudades válidos.");
                return;
            }
            if (!grafo.getCiudadIndices().containsKey(origen) || !grafo.getCiudadIndices().containsKey(destino)) {
                JOptionPane.showMessageDialog(null, "🚫 Una o ambas ciudades no existen en el grafo.");
                return;
            }
            grafo.eliminarConexion(origen, destino);
            JOptionPane.showMessageDialog(null, "🧹 Tráfico interrumpido entre " + origen + " y " + destino + ".");

        // Si elige agregar
        } else if (seleccion == 1) {
            String origen = JOptionPane.showInputDialog("🌼 Ciudad origen:");
            String destino = JOptionPane.showInputDialog("🌼 Ciudad destino:");
            if (origen == null || destino == null || origen.isEmpty() || destino.isEmpty()) {
                JOptionPane.showMessageDialog(null, "🚫 Por favor, ingrese nombres de ciudades válidos.");
                return;
            }
            int[] tiempos = new int[4];
            String[] climas = {"normal ☀️", "lluvia 🌧️", "nieve ❄️", "tormenta ⛈️"};

            // Solicita los tiempos para cada tipo de clima
            for (int i = 0; i < 4; i++) {
                String input = JOptionPane.showInputDialog("⏱️ Tiempo con " + climas[i] + ":");
                try {
                    tiempos[i] = Integer.parseInt(input);
                    if (tiempos[i] <= 0) throw new NumberFormatException("Tiempo debe ser positivo.");
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "🚫 Entrada inválida para " + climas[i] + ". Conexión no agregada.");
                    return;
                }
            }
            grafo.agregarConexion(origen, destino, tiempos);
            JOptionPane.showMessageDialog(null, 
                "✅ Conexión agregada entre " + origen + " y " + destino + ". Se usará el tiempo de clima normal por defecto.");
        }
    }

    // Permite al usuario ingresar una temperatura para determinar el clima actual
    private static void cambiarTemperatura() {
        String input = JOptionPane.showInputDialog("🌡️ Ingrese la temperatura actual (°C):");
        try {
            double temperatura = Double.parseDouble(input);
            grafo.establecerClimaPorTemperatura(temperatura);
            JOptionPane.showMessageDialog(null, "✅ Clima actualizado según temperatura (" + temperatura + "°C) y rutas recalculadas.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "🚫 Por favor, ingrese un valor numérico válido para la temperatura.");
        }
    }

    // Permite al usuario seleccionar el clima actual y actualiza las rutas del grafo
    private static void cambiarClima() {
        String[] opciones = {"☀️ Normal", "🌧️ Lluvia", "❄️ Nieve", "⛈️ Tormenta"};
        int clima = JOptionPane.showOptionDialog(null, "Selecciona el clima actual:",
                "🌈 Cambiar clima", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, opciones, opciones[0]);

        if (clima >= 0) {
            grafo.establecerClima(clima);
            JOptionPane.showMessageDialog(null, "✅ Clima actualizado y rutas recalculadas.");
        }
    }
}