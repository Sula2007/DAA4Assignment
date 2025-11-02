package kz.smartcampus.graph.dagsp;

import kz.smartcampus.core.DirectedGraph;
import kz.smartcampus.core.PerformanceMetrics;
import kz.smartcampus.graph.topo.DFSTopologicalSort;

import java.util.*;

/**
 * Shortest and longest path algorithms for DAGs.
 * Uses topological sort for efficient path computation.
 * 
 * Advantages over Dijkstra:
 * - Handles negative weights
 * - More efficient for DAGs: O(V + E) vs O((V + E) log V)
 * - Can compute longest path easily
 * 
 * Algorithm:
 * 1. Compute topological order
 * 2. Initialize distances (0 for source, ∞ for others)
 * 3. Process vertices in topological order
 * 4. Relax all outgoing edges from each vertex
 */
public class DAGShortestPath {
    private final DirectedGraph graph;
    private final PerformanceMetrics metrics;
    private int[] distances;
    private int[] predecessors;
    private List<Integer> topologicalOrder;
    
    private static final int INFINITY = Integer.MAX_VALUE / 2;
    
    /**
     * Creates shortest path solver for DAG
     */
    public DAGShortestPath(DirectedGraph graph) {
        this.graph = graph;
        this.metrics = new PerformanceMetrics();
    }
    
    /**
     * Computes shortest paths from source to all vertices
     * @param source starting vertex
     * @return true if successful, false if graph is not a DAG
     */
    public boolean computeShortestPaths(int source) {
        metrics.startTimer();
        
        // Get topological order
        DFSTopologicalSort topoSort = new DFSTopologicalSort(graph);
        topologicalOrder = topoSort.sort();
        
        if (!topoSort.isDAG()) {
            metrics.stopTimer();
            return false; // Not a DAG
        }
        
        int n = graph.getVertexCount();
        distances = new int[n];
        predecessors = new int[n];
        
        // Initialize
        Arrays.fill(distances, INFINITY);
        Arrays.fill(predecessors, -1);
        distances[source] = 0;
        metrics.increment("initializations");
        
        // Process vertices in topological order
        for (int u : topologicalOrder) {
            if (distances[u] != INFINITY) {
                // Relax all edges from u
                for (DirectedGraph.Edge edge : graph.getEdges(u)) {
                    int v = edge.getDestination();
                    int weight = edge.getWeight();
                    
                    metrics.increment("relaxations");
                    
                    if (distances[u] + weight < distances[v]) {
                        distances[v] = distances[u] + weight;
                        predecessors[v] = u;
                        metrics.increment("distance_updates");
                    }
                }
            }
            metrics.increment("vertices_processed");
        }
        
        metrics.stopTimer();
        return true;
    }
    
    /**
     * Computes longest paths from source (critical path)
     * @param source starting vertex
     * @return true if successful
     */
    public boolean computeLongestPaths(int source) {
        metrics.startTimer();
        
        // Get topological order
        DFSTopologicalSort topoSort = new DFSTopologicalSort(graph);
        topologicalOrder = topoSort.sort();
        
        if (!topoSort.isDAG()) {
            metrics.stopTimer();
            return false;
        }
        
        int n = graph.getVertexCount();
        distances = new int[n];
        predecessors = new int[n];
        
        // Initialize (use negative infinity for longest path)
        Arrays.fill(distances, -INFINITY);
        Arrays.fill(predecessors, -1);
        distances[source] = 0;
        metrics.increment("initializations");
        
        // Process vertices in topological order
        for (int u : topologicalOrder) {
            if (distances[u] != -INFINITY) {
                for (DirectedGraph.Edge edge : graph.getEdges(u)) {
                    int v = edge.getDestination();
                    int weight = edge.getWeight();
                    
                    metrics.increment("relaxations");
                    
                    // Maximize distance for longest path
                    if (distances[u] + weight > distances[v]) {
                        distances[v] = distances[u] + weight;
                        predecessors[v] = u;
                        metrics.increment("distance_updates");
                    }
                }
            }
            metrics.increment("vertices_processed");
        }
        
        metrics.stopTimer();
        return true;
    }
    
    /**
     * Returns distance to a vertex
     */
    public int getDistance(int vertex) {
        if (distances == null) {
            throw new IllegalStateException("Must compute paths first");
        }
        int dist = distances[vertex];
        return (dist == INFINITY || dist == -INFINITY) ? -1 : dist;
    }
    
    /**
     * Reconstructs path from source to destination
     */
    public List<Integer> getPath(int destination) {
        if (predecessors == null) {
            throw new IllegalStateException("Must compute paths first");
        }
        
        if (distances[destination] == INFINITY || 
            distances[destination] == -INFINITY) {
            return new ArrayList<>(); // No path
        }
        
        LinkedList<Integer> path = new LinkedList<>();
        int current = destination;
        
        while (current != -1) {
            path.addFirst(current);
            current = predecessors[current];
        }
        
        return path;
    }
    
    /**
     * Returns path with vertex labels
     */
    public List<String> getPathLabels(int destination) {
        List<Integer> path = getPath(destination);
        List<String> labels = new ArrayList<>();
        
        for (int v : path) {
            labels.add(graph.getVertexLabel(v));
        }
        
        return labels;
    }
    
    /**
     * Finds the critical path (longest path in the entire DAG)
     */
    public CriticalPathResult findCriticalPath() {
        // Try all possible starting vertices
        int maxLength = -1;
        int bestSource = -1;
        int bestDestination = -1;
        
        for (int source = 0; source < graph.getVertexCount(); source++) {
            computeLongestPaths(source);
            
            for (int dest = 0; dest < graph.getVertexCount(); dest++) {
                int dist = getDistance(dest);
                if (dist > maxLength) {
                    maxLength = dist;
                    bestSource = source;
                    bestDestination = dest;
                }
            }
        }
        
        // Recompute for best source
        if (bestSource != -1) {
            computeLongestPaths(bestSource);
            List<Integer> path = getPath(bestDestination);
            return new CriticalPathResult(path, maxLength);
        }
        
        return new CriticalPathResult(new ArrayList<>(), 0);
    }
    
    /**
     * Returns performance metrics
     */
    public PerformanceMetrics getMetrics() {
        return metrics;
    }
    
    /**
     * Prints all distances from source
     */
    public void printDistances(int source) {
        System.out.println("Distances from " + graph.getVertexLabel(source) + ":");
        for (int v = 0; v < distances.length; v++) {
            int dist = getDistance(v);
            if (dist >= 0) {
                System.out.println("  " + graph.getVertexLabel(v) + ": " + dist);
            } else {
                System.out.println("  " + graph.getVertexLabel(v) + ": unreachable");
            }
        }
    }
    
    /**
     * Result class for critical path
     */
    public static class CriticalPathResult {
        private final List<Integer> path;
        private final int length;
        
        public CriticalPathResult(List<Integer> path, int length) {
            this.path = path;
            this.length = length;
        }
        
        public List<Integer> getPath() {
            return path;
        }
        
        public int getLength() {
            return length;
        }
        
        @Override
        public String toString() {
            return "Critical Path: " + path + ", Length: " + length;
        }
    }
}
