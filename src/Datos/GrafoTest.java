package Datos;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

public class GrafoTest {
    private Grafo grafo;

    @Before
    public void setUp() {
        grafo = new Grafo();
        // Añade muestras de ciudades y conexiones
        grafo.agregarCiudad("A");
        grafo.agregarCiudad("B");
        grafo.agregarCiudad("C");
        int[] tiempos = {10, 15, 20, 25}; // Normal, Lluvia, Nieve, Tormenta
        grafo.agregarConexion("A", "B", tiempos);
        grafo.agregarConexion("B", "C", tiempos);
        grafo.agregarConexion("A", "C", new int[]{30, 35, 40, 45});
    }

    @Test
    public void testAgregarCiudad() {
        grafo.agregarCiudad("D");
        assertTrue(grafo.getCiudadIndices().containsKey("D"));
        assertEquals(4, grafo.getCiudades().size());
    }

    @Test
    public void testAgregarConexion() {
        int[] tiempos = {5, 10, 15, 20};
        grafo.agregarConexion("A", "D", tiempos);
        assertEquals(5, grafo.getDistancia("A", "D")); // Clima Normal
    }

    @Test
    public void testEliminarConexion() {
        grafo.eliminarConexion("A", "B");
        assertTrue(grafo.getDistancia("A", "B") == -1); // INF indica que no hay conexión
    }

    @Test
    public void testFloydWarshall() {
        List<String> camino = grafo.caminoMasCorto("A", "C");
        assertEquals(List.of("A", "B", "C"), camino); // Camino más corto A->B->C
        assertEquals(20, grafo.getDistancia("A", "C")); // 10 + 10
    }

    @Test
    public void testCentroDelGrafo() {
        assertEquals("A", grafo.centroDelGrafo()); // B tiene menos excentricidad
    }

    @Test
    public void testCambiarClima() {
        grafo.establecerClima(1); // Lluvia
        assertEquals(15 + 15, grafo.getDistancia("A", "C")); // A->B (15) + B->C (15)
    }
}