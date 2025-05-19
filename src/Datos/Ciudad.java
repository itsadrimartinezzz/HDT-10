// El paquete "Datos" agrupa esta clase como parte de la lógica de datos del programa
package Datos;

// Clase que representa una ciudad dentro del sistema
public class Ciudad {
    
    // Atributo privado que almacena el nombre de la ciudad
    private String nombre;

    // Constructor que inicializa una instancia de Ciudad con un nombre específico
    public Ciudad(String nombre) {
        this.nombre = nombre;
    }

    // Método getter que devuelve el nombre de la ciudad
    public String getNombre() {
        return nombre;
    }

    // Sobrescribe el método equals para comparar si dos objetos Ciudad son iguales
    // Compara basándose en el valor del nombre
    @Override
    public boolean equals(Object obj) {
        // Si ambos objetos son la misma instancia, son iguales
        if (this == obj) return true;
        // Si el otro objeto es null o de una clase distinta, no son iguales
        if (obj == null || getClass() != obj.getClass()) return false;

        // Hace un cast seguro a Ciudad y compara los nombres
        Ciudad ciudad = (Ciudad) obj;
        return nombre.equals(ciudad.nombre);
    }

    // Sobrescribe el método hashCode para que coincida con el método equals
    // Esto es importante cuando se usa la clase en estructuras como HashMap o HashSet
    @Override
    public int hashCode() {
        return nombre.hashCode();
    }

    // Sobrescribe el método toString para que al imprimir una Ciudad se muestre su nombre
    @Override
    public String toString() {
        return nombre;
    }
}
