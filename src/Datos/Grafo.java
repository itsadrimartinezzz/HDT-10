package Datos;
// Implementa la lógica de los grafos dirigidos con pesos variables por clima
// Incluye el algoritmo de Floyd, rutas más cortas y el cálculo del centro del grafo

import java.util.*;
import java.nio.file.*;

public class Grafo {
    private Map<String, Integer> ciudadIndices = new HashMap<>();
    private List<Ciudad> ciudades = new ArrayList<>();
    private int[][][] matrizPesos; // [clima][origen][destino]
    private int[][] distancias;
    private int[][] rutas;
    private int clima = 0; // 0: normal, 1: lluvia, 2: nieve, 3: tormenta
    private final int INF = Integer.MAX_VALUE / 2;

    // Cargar datos desde el archivo logistica.txt
    public void cargarDesdeArchivo(String archivo) throws Exception {
        List<String> lineas = Files.readAllLines(Paths.get(archivo));
        for (String linea : lineas) {
            String[] partes = linea.split(" ");
            agregarCiudad(partes[0]);
            agregarCiudad(partes[1]);
        }

        int n = ciudades.size();
        matrizPesos = new int[4][n][n];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < n; j++)
                Arrays.fill(matrizPesos[i][j], INF);

        for (String linea : lineas) {
            String[] partes = linea.split(" ");
            int i = ciudadIndices.get(partes[0]);
            int j = ciudadIndices.get(partes[1]);
            for (int c = 0; c < 4; c++) {
                matrizPesos[c][i][j] = Integer.parseInt(partes[2 + c]);
            }
        }

        recalcularFloyd();
    }

    public void agregarCiudad(String nombre) {
        if (!ciudadIndices.containsKey(nombre)) {
            ciudadIndices.put(nombre, ciudades.size());
            ciudades.add(new Ciudad(nombre));
        }
    }

    public void eliminarConexion(String origen, String destino) {
        int i = ciudadIndices.get(origen);
        int j = ciudadIndices.get(destino);
        for (int c = 0; c < 4; c++)
            matrizPesos[c][i][j] = INF;
        recalcularFloyd();
    }

    public void agregarConexion(String origen, String destino, int[] tiempos) {
        agregarCiudad(origen);
        agregarCiudad(destino);
        int i = ciudadIndices.get(origen);
        int j = ciudadIndices.get(destino);
        for (int c = 0; c < 4; c++)
            matrizPesos[c][i][j] = tiempos[c];
        recalcularFloyd();
    }

    public void establecerClima(int clima) {
        this.clima = clima;
        recalcularFloyd();
    }

    // Algoritmo de Floyd-Warshall para hallar rutas más cortas
    public void recalcularFloyd() {
        int n = ciudades.size();
        distancias = new int[n][n];
        rutas = new int[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                distancias[i][j] = matrizPesos[clima][i][j];
                rutas[i][j] = (i == j || distancias[i][j] == INF) ? -1 : i;
            }
        }

        for (int k = 0; k < n; k++)
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    if (distancias[i][k] + distancias[k][j] < distancias[i][j]) {
                        distancias[i][j] = distancias[i][k] + distancias[k][j];
                        rutas[i][j] = rutas[k][j];
                    }
    }

    // Retorna el camino más corto como lista de nombres de ciudades
    public List<String> caminoMasCorto(String origen, String destino) {
        int i = ciudadIndices.get(origen);
        int j = ciudadIndices.get(destino);
        if (distancias[i][j] == INF) return Collections.emptyList();
        LinkedList<String> camino = new LinkedList<>();
        while (j != i) {
            camino.addFirst(ciudades.get(j).getNombre());
            j = rutas[i][j];
        }
        camino.addFirst(ciudades.get(i).getNombre());
        return camino;
    }

    // Calcula el centro del grafo usando la menor excentricidad
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

    // Imprime la matriz de distancias en consola
    public void imprimirMatriz() {
        int n = ciudades.size();
        System.out.printf("%15s", "");
        for (Ciudad c : ciudades) System.out.printf("%15s", c);
        System.out.println();
        for (int i = 0; i < n; i++) {
            System.out.printf("%15s", ciudades.get(i));
            for (int j = 0; j < n; j++) {
                String val = (distancias[i][j] == INF) ? "INF" : String.valueOf(distancias[i][j]);
                System.out.printf("%15s", val);
            }
            System.out.println();
        }
    }
}
