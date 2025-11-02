package kz.smartcampus.core;

import java.util.*;

/**
 * Directed weighted graph representation using adjacency list.
 * Supports both weighted and unweighted edges for various graph algorithms.
 */
public class DirectedGraph {
    private final int vertices;
    private final Map<Integer, List<Edge>> adjacencyList;
    private final Map<Integer, String> vertexLabels;
    
    /**
     * Edge representation with destination and weight
     */
    public static class Edge {
        private final int destination;
        private final int weight;
        
        public Edge(int destination, int weight) {
            this.destination = destination;
            this.weight = weight;
        }
        
        public int getDestination() {
            return destination;
        }
        
        public int getWeight() {
            return weight;
        }
        
        @Override
        public String toString() {
            return "→" + destination + "(" + weight + ")";
        }
    }
    
    /**
     * Constructs a directed graph with specified number of vertices
     */
    public DirectedGraph(int vertices) {
        if (vertices <= 0) {
            throw new IllegalArgumentException("Number of vertices must be positive");
        }
        this.vertices = vertices;
        this.adjacencyList = new HashMap<>();
        this.vertexLabels = new HashMap<>();
        
        for (int i = 0; i < vertices; i++) {
            adjacencyList.put(i, new ArrayList<>());
            vertexLabels.put(i, "V" + i);
        }
    }
    
    /**
     * Adds a weighted directed edge from source to destination
     */
    public void addEdge(int source, int destination, int weight) {
        validateVertex(source);
        validateVertex(destination);
        adjacencyList.get(source).add(new Edge(destination, weight));
    }
    
    /**
     * Adds an unweighted edge (default weight = 1)
     */
    public void addEdge(int source, int destination) {
        addEdge(source, destination, 1);
    }
    
    /**
     * Sets a label for a vertex
     */
    public void setVertexLabel(int vertex, String label) {
        validateVertex(vertex);
        vertexLabels.put(vertex, label);
    }
    
    /**
     * Gets the label of a vertex
     */
    public String getVertexLabel(int vertex) {
        return vertexLabels.getOrDefault(vertex, "V" + vertex);
    }
    
    /**
     * Returns all outgoing edges from a vertex
     */
    public List<Edge> getEdges(int vertex) {
        validateVertex(vertex);
        return new ArrayList<>(adjacencyList.get(vertex));
    }
    
    /**
     * Returns the number of vertices
     */
    public int getVertexCount() {
        return vertices;
    }
    
    /**
     * Returns the total number of edges
     */
    public int getEdgeCount() {
        int count = 0;
        for (List<Edge> edges : adjacencyList.values()) {
            count += edges.size();
        }
        return count;
    }
    
    /**
     * Creates a reverse (transpose) graph
     */
    public DirectedGraph reverse() {
        DirectedGraph reversed = new DirectedGraph(vertices);
        
        // Copy labels
        for (int i = 0; i < vertices; i++) {
            reversed.setVertexLabel(i, getVertexLabel(i));
        }
        
        // Reverse all edges
        for (int u = 0; u < vertices; u++) {
            for (Edge edge : adjacencyList.get(u)) {
                reversed.addEdge(edge.destination, u, edge.weight);
            }
        }
        
        return reversed;
    }
    
    /**
     * Returns all vertices in the graph
     */
    public Set<Integer> getVertices() {
        return new HashSet<>(adjacencyList.keySet());
    }
    
    /**
     * Checks if there's an edge from source to destination
     */
    public boolean hasEdge(int source, int destination) {
        validateVertex(source);
        validateVertex(destination);
        
        for (Edge edge : adjacencyList.get(source)) {
            if (edge.destination == destination) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns the in-degree of a vertex
     */
    public int getInDegree(int vertex) {
        validateVertex(vertex);
        int inDegree = 0;
        
        for (int u = 0; u < vertices; u++) {
            for (Edge edge : adjacencyList.get(u)) {
                if (edge.destination == vertex) {
                    inDegree++;
                }
            }
        }
        
        return inDegree;
    }
    
    /**
     * Returns the out-degree of a vertex
     */
    public int getOutDegree(int vertex) {
        validateVertex(vertex);
        return adjacencyList.get(vertex).size();
    }
    
    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= vertices) {
            throw new IllegalArgumentException(
                "Vertex " + vertex + " is out of range [0, " + (vertices - 1) + "]"
            );
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DirectedGraph{vertices=").append(vertices)
          .append(", edges=").append(getEdgeCount()).append("}\n");
        
        for (int u = 0; u < vertices; u++) {
            sb.append(getVertexLabel(u)).append(": ");
            List<Edge> edges = adjacencyList.get(u);
            if (edges.isEmpty()) {
                sb.append("∅");
            } else {
                for (int i = 0; i < edges.size(); i++) {
                    if (i > 0) sb.append(", ");
                    Edge e = edges.get(i);
                    sb.append(getVertexLabel(e.destination))
                      .append("(w=").append(e.weight).append(")");
                }
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }
}
