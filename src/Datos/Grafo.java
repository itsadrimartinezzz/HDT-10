package Datos;

// Importaciones necesarias para estructuras de datos, archivos y GUI
import java.util.*;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import java.nio.file.*;

public class Grafo {
    // Mapa que asocia el nombre de cada ciudad con su índice en las matrices
    private Map<String, Integer> ciudadIndices = new HashMap<>();
    
    // Lista de objetos Ciudad para mantener el orden y la referencia
    private List<Ciudad> ciudades = new ArrayList<>();
    
    // Matriz tridimensional para almacenar los tiempos entre ciudades según el clima [clima][origen][destino]
    private int[][][] matrizPesos;
    
    // Matriz de distancias mínimas calculadas por Floyd
    private int[][] distancias;
    
    // Matriz que permite reconstruir el camino más corto
    private int[][] rutas;
    
    // Clima actual seleccionado (0 = normal, 1 = lluvia, etc.)
    private int clima = 0;
    
    // Valor que representa infinito (usado para indicar que no hay conexión)
    private final int INF = Integer.MAX_VALUE / 2;

    // Almacena la temperatura actual, si se establece
    private Double temperaturaActual = null;

    // Carga el grafo desde un archivo de texto con el formato indicado
    public void cargarDesdeArchivo(String archivo) throws Exception {
        List<String> lineas = Files.readAllLines(Paths.get(archivo));
        
        // Primero se agregan las ciudades al mapa y a la lista
        for (String linea : lineas) {
            String[] partes = linea.split(" ");
            agregarCiudad(partes[0]);
            agregarCiudad(partes[1]);
        }

        int n = ciudades.size();
        matrizPesos = new int[4][n][n]; // 4 climas

        // Inicializa la matriz con valores infinitos
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < n; j++)
                Arrays.fill(matrizPesos[i][j], INF);

        // Llena la matriz con los valores del archivo (por clima)
        for (String linea : lineas) {
            String[] partes = linea.split(" ");
            int i = ciudadIndices.get(partes[0]);
            int j = ciudadIndices.get(partes[1]);
            for (int c = 0; c < 4; c++) {
                matrizPesos[c][i][j] = Integer.parseInt(partes[2 + c]);
            }
        }

        // Aplica el algoritmo de Floyd al clima actual
        recalcularFloyd();
    }

    // Agrega una ciudad nueva si no existe
    public void agregarCiudad(String nombre) {
        if (!ciudadIndices.containsKey(nombre)) {
            int newIndex = ciudades.size();
            ciudadIndices.put(nombre, newIndex);
            ciudades.add(new Ciudad(nombre));
            // Resize matrices to accommodate the new city
            int n = ciudades.size();
            int[][][] newMatrizPesos = new int[4][n][n];
            int[][] newDistancias = new int[n][n];
            int[][] newRutas = new int[n][n];
            for (int c = 0; c < 4; c++) {
                for (int i = 0; i < n - 1; i++)
                    System.arraycopy(matrizPesos[c][i], 0, newMatrizPesos[c][i], 0, n - 1);
                Arrays.fill(newMatrizPesos[c][n - 1], INF);
                for (int j = 0; j < n; j++)
                    newMatrizPesos[c][j][n - 1] = INF;
            }
            for (int i = 0; i < n - 1; i++)
                System.arraycopy(distancias[i], 0, newDistancias[i], 0, n - 1);
            Arrays.fill(newDistancias[n - 1], INF);
            for (int j = 0; j < n; j++)
                newDistancias[j][n - 1] = INF;
            for (int i = 0; i < n - 1; i++)
                System.arraycopy(rutas[i], 0, newRutas[i], 0, n - 1);
            Arrays.fill(newRutas[n - 1], -1);
            for (int j = 0; j < n; j++)
                newRutas[j][n - 1] = -1;
            matrizPesos = newMatrizPesos;
            distancias = newDistancias;
            rutas = newRutas;
            recalcularFloyd();
        }
    }

    // Elimina una conexión entre dos ciudades en todos los climas
    public void eliminarConexion(String origen, String destino) {
        int i = ciudadIndices.get(origen);
        int j = ciudadIndices.get(destino);
        for (int c = 0; c < 4; c++)
            matrizPesos[c][i][j] = INF;
        recalcularFloyd();
    }

    // Agrega o actualiza una conexión entre ciudades con los tiempos por clima
    public void agregarConexion(String origen, String destino, int[] tiempos) {
        agregarCiudad(origen);
        agregarCiudad(destino);
        int i = ciudadIndices.get(origen);
        int j = ciudadIndices.get(destino);
        for (int c = 0; c < 4; c++)
            matrizPesos[c][i][j] = tiempos[c];
        recalcularFloyd();
    }

    // Cambia el clima actual y recalcula las rutas más cortas
    public void establecerClima(int clima) {
        this.clima = clima;
        this.temperaturaActual = null; // Reset temperature when manually setting climate
        recalcularFloyd();
    }

    // Establece el clima basado en la temperatura en grados Celsius
    public void establecerClimaPorTemperatura(double temperatura) {
        this.temperaturaActual = temperatura;
        if (temperatura >= 15) {
            this.clima = 0; // Normal
        } else if (temperatura >= 5) {
            this.clima = 1; // Lluvia
        } else if (temperatura >= -5) {
            this.clima = 2; // Nieve
        } else {
            this.clima = 3; // Tormenta
        }
        recalcularFloyd();
    }

    // Algoritmo de Floyd-Warshall para encontrar rutas más cortas entre todos los pares
    public void recalcularFloyd() {
        int n = ciudades.size();
        distancias = new int[n][n];
        rutas = new int[n][n];

        // Inicializa matrices de distancias y rutas
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                distancias[i][j] = matrizPesos[clima][i][j];
                rutas[i][j] = (i == j || distancias[i][j] == INF) ? -1 : i;
            }

        // Aplica la fórmula de Floyd
        for (int k = 0; k < n; k++)
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    if (distancias[i][k] != INF && distancias[k][j] != INF &&
                        distancias[i][k] + distancias[k][j] < distancias[i][j]) {
                        distancias[i][j] = distancias[i][k] + distancias[k][j];
                        rutas[i][j] = rutas[k][j];
                    }
    }

    // Devuelve el camino más corto entre dos ciudades en forma de lista
    public List<String> caminoMasCorto(String origen, String destino) {
        int i = ciudadIndices.get(origen);
        int j = ciudadIndices.get(destino);
        if (distancias[i][j] == INF) return Collections.emptyList();

        LinkedList<String> camino = new LinkedList<>();
        camino.add(ciudades.get(j).getNombre());
        while (j != i) {
            j = rutas[i][j];
            camino.addFirst(ciudades.get(j).getNombre());
        }
        return camino;
    }

    // Returns the shortest path distance between two cities
    public int getDistancia(String origen, String destino) {
        int i = ciudadIndices.get(origen);
        int j = ciudadIndices.get(destino);
        return distancias[i][j] == INF ? -1 : distancias[i][j];
    }

    // Calcula el centro del grafo: la ciudad con menor excentricidad
    public String centroDelGrafo() {
        int n = ciudades.size();
        int[] excentricidades = new int[n];
        for (int i = 0; i < n; i++) {
            excentricidades[i] = Arrays.stream(distancias[i]).max().orElse(INF);
        }

        int centro = 0;
        for (int i = 1; i < n; i++) {
            if (excentricidades[i] < excentricidades[centro]) {
                centro = i;
            }
        }
        return ciudades.get(centro).getNombre();
    }

    // Muestra la matriz de distancias en una ventana con formato HTML
    public void mostrarMatrizEnVentana() {
        int n = ciudades.size();
        StringBuilder html = new StringBuilder("<html>");

        // Explicación del contenido
        String[] climas = {"Normal ☀️", "Lluvia 🌧️", "Nieve ❄️", "Tormenta ⛈️"};
        String climaActual = climas[clima];
        html.append("<div style='padding:10px; font-family:Arial;'>")
            .append("<h2 style='text-align:center;'>📍 Matriz de distancias mínimas entre ciudades</h2>")
            .append("<p style='text-align:center;'>")
            .append("🔹 <b>Clima actual: " + climaActual + "</b>");
        if (temperaturaActual != null) {
            html.append(" (Temperatura: " + temperaturaActual + "°C)");
        }
        html.append("<br>")
            .append("🔹 Filas = <b>Ciudad origen</b>, Columnas = <b>Ciudad destino</b><br>")
            .append("🔹 Los valores indican el <b>tiempo mínimo de ruta</b> según el clima actual<br>")
            .append("🔹 <b>∞</b> significa que no existe una conexión disponible<br>")
            .append("</p>")
            .append("</div>");

        // Cabecera de tabla
        html.append("<table border='1' cellpadding='5' cellspacing='0' style='border-collapse: collapse;'>");
        html.append("<tr><th></th>");
        for (Ciudad c : ciudades) {
            html.append("<th>").append(c.getNombre()).append("</th>");
        }
        html.append("</tr>");

        // Filas con valores
        for (int i = 0; i < n; i++) {
            html.append("<tr><th>").append(ciudades.get(i).getNombre()).append("</th>");
            for (int j = 0; j < n; j++) {
                String val = (distancias[i][j] == INF) ? "∞" : String.valueOf(distancias[i][j]);
                html.append("<td align='center'>").append(val).append("</td>");
            }
            html.append("</tr>");
        }

        html.append("</table></html>");

        // Muestra el resultado en un JOptionPane con scroll
        JLabel label = new JLabel(html.toString());
        JScrollPane scrollPane = new JScrollPane(label);
        scrollPane.setPreferredSize(new Dimension(900, 500));
        JOptionPane.showMessageDialog(null, scrollPane, "📊 Matriz de distancias", JOptionPane.INFORMATION_MESSAGE);
    }

    // Getter for ciudadIndices to allow validation of city names
    public Map<String, Integer> getCiudadIndices() {
        return ciudadIndices;
    }

    // Getter for ciudades (for testing)
    public List<Ciudad> getCiudades() {
        return ciudades;
    }
}