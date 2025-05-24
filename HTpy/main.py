from grafo import GrafoLogistica

grafo = GrafoLogistica()

# Cambia esta ruta si el archivo está en otro lugar
grafo.cargar_desde_archivo("logistica.txt")

print("Centro del grafo:", grafo.centro_del_grafo())

while True:
    print("\nOpciones:")
    print("1. Consultar ruta más corta")
    print("2. Mostrar ciudad centro")
    print("3. Salir")
    opcion = input("Seleccione una opción: ")

    if opcion == "1":
        origen = input("Ciudad origen: ")
        destino = input("Ciudad destino: ")
        ruta, costo = grafo.ruta_mas_corta(origen, destino)
        if ruta:
            print("Ruta más corta:", " -> ".join(ruta))
            print("Costo total:", costo)
        else:
            print("No existe una ruta entre las ciudades.")
    elif opcion == "2":
        print("Centro del grafo:", grafo.centro_del_grafo())
    elif opcion == "3":
        print("Programa finalizado.")
        break
    else:
        print("Opción inválida. Intente de nuevo.")
