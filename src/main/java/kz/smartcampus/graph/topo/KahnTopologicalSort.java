package kz.smartcampus.graph.topo;

import kz.smartcampus.core.DirectedGraph;
import kz.smartcampus.core.PerformanceMetrics;

import java.util.*;

/**
 * Kahn's algorithm for topological sorting.
 * Uses BFS approach with in-degree tracking.
 * 
 * Algorithm:
 * 1. Calculate in-degrees for all vertices
 * 2. Add all vertices with in-degree 0 to queue
 * 3. Process queue: remove vertex, decrease neighbors' in-degrees
 * 4. If all vertices processed, graph is acyclic
 * 
 * Time Complexity: O(V + E)
 * Space Complexity: O(V)
 */
public class KahnTopologicalSort {
    private final DirectedGraph graph;
    private final PerformanceMetrics metrics;
    private List<Integer> topologicalOrder;
    private boolean isDAG;
    
    /**
     * Creates topological sorter for given graph
     */
    public KahnTopologicalSort(DirectedGraph graph) {
        this.graph = graph;
        this.metrics = new PerformanceMetrics();
        this.isDAG = false;
    }
    
    /**
     * Performs topological sort using Kahn's algorithm
     * @return topological order if graph is DAG, empty list otherwise
     */
    public List<Integer> sort() {
        metrics.startTimer();
        
        int n = graph.getVertexCount();
        topologicalOrder = new ArrayList<>();
        
        // Calculate in-degrees
        int[] inDegree = new int[n];
        for (int v = 0; v < n; v++) {
            for (DirectedGraph.Edge edge : graph.getEdges(v)) {
                inDegree[edge.getDestination()]++;
                metrics.increment("in_degree_calculations");
            }
        }
        
        // Initialize queue with vertices having in-degree 0
        Queue<Integer> queue = new LinkedList<>();
        for (int v = 0; v < n; v++) {
            if (inDegree[v] == 0) {
                queue.offer(v);
                metrics.increment("queue_adds");
            }
        }
        
        // Process vertices
        while (!queue.isEmpty()) {
            int current = queue.poll();
            topologicalOrder.add(current);
            metrics.increment("queue_removals");
            metrics.increment("vertices_processed");
            
            // Reduce in-degree of neighbors
            for (DirectedGraph.Edge edge : graph.getEdges(current)) {
                int neighbor = edge.getDestination();
                inDegree[neighbor]--;
                metrics.increment("in_degree_updates");
                
                if (inDegree[neighbor] == 0) {
                    queue.offer(neighbor);
                    metrics.increment("queue_adds");
                }
            }
        }
        
        // Check if all vertices were processed (DAG check)
        isDAG = (topologicalOrder.size() == n);
        
        if (!isDAG) {
            metrics.increment("cycle_detected");
            topologicalOrder.clear();
        }
        
        metrics.stopTimer();
        return new ArrayList<>(topologicalOrder);
    }
    
    /**
     * Returns the topological order
     */
    public List<Integer> getTopologicalOrder() {
        if (topologicalOrder == null) {
            sort();
        }
        return new ArrayList<>(topologicalOrder);
    }
    
    /**
     * Returns topological order with vertex labels
     */
    public List<String> getTopologicalOrderLabels() {
        if (topologicalOrder == null) {
            sort();
        }
        
        List<String> labels = new ArrayList<>();
        for (int v : topologicalOrder) {
            labels.add(graph.getVertexLabel(v));
        }
        return labels;
    }
    
    /**
     * Checks if the graph is a DAG
     */
    public boolean isDAG() {
        if (topologicalOrder == null) {
            sort();
        }
        return isDAG;
    }
    
    /**
     * Returns performance metrics
     */
    public PerformanceMetrics getMetrics() {
        return metrics;
    }
    
    /**
     * Verifies if the computed order is valid
     */
    public boolean verifyOrder() {
        if (topologicalOrder == null || !isDAG) {
            return false;
        }
        
        // Create position map
        Map<Integer, Integer> position = new HashMap<>();
        for (int i = 0; i < topologicalOrder.size(); i++) {
            position.put(topologicalOrder.get(i), i);
        }
        
        // Check all edges respect the order
        for (int u : topologicalOrder) {
            for (DirectedGraph.Edge edge : graph.getEdges(u)) {
                int v = edge.getDestination();
                if (position.get(u) >= position.get(v)) {
                    return false; // Edge goes backward
                }
            }
        }
        
        return true;
    }
    
    /**
     * Prints the topological order
     */
    public void printOrder() {
        if (topologicalOrder == null) {
            sort();
        }
        
        if (!isDAG) {
            System.out.println("Graph contains cycle - no topological order exists");
            return;
        }
        
        System.out.println("Topological Order (Kahn's Algorithm):");
        System.out.print("  ");
        for (int i = 0; i < topologicalOrder.size(); i++) {
            if (i > 0) System.out.print(" → ");
            System.out.print(graph.getVertexLabel(topologicalOrder.get(i)));
        }
        System.out.println();
    }
}
