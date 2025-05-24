import networkx as nx

class GrafoLogistica:
    def __init__(self):
        self.grafo = nx.DiGraph()

    def cargar_desde_archivo(self, ruta):
        with open(ruta, 'r') as f:
            for linea in f:
                partes = linea.strip().split()
                if len(partes) >= 3:
                    origen, destino = partes[0], partes[1]
                    tiempo_normal = int(partes[2])
                    self.grafo.add_edge(origen, destino, weight=tiempo_normal)

    def floyd_warshall(self):
        return dict(nx.floyd_warshall(self.grafo, weight='weight'))

    def centro_del_grafo(self):
        distancias = self.floyd_warshall()
        eccentricidades = {
            nodo: max(d.values()) for nodo, d in distancias.items() if len(d) > 0
        }
        return min(eccentricidades, key=eccentricidades.get) if eccentricidades else None

    def ruta_mas_corta(self, origen, destino):
        try:
            ruta = nx.shortest_path(self.grafo, origen, destino, weight='weight')
            costo = nx.shortest_path_length(self.grafo, origen, destino, weight='weight')
            return ruta, costo
        except nx.NetworkXNoPath:
            return None, float('inf')
        except nx.NodeNotFound:
            return None, float('inf')
