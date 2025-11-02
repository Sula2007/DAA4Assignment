package kz.smartcampus.graph.topo;

import kz.smartcampus.core.DirectedGraph;
import kz.smartcampus.core.PerformanceMetrics;

import java.util.*;

/**
 * DFS-based topological sorting.
 * Uses depth-first search with post-order traversal.
 * 
 * Algorithm:
 * 1. Perform DFS from each unvisited vertex
 * 2. Add vertices to stack in post-order (after visiting all descendants)
 * 3. Reverse the stack to get topological order
 * 4. Detect cycles using recursion stack
 * 
 * Time Complexity: O(V + E)
 * Space Complexity: O(V)
 */
public class DFSTopologicalSort {
    private final DirectedGraph graph;
    private final PerformanceMetrics metrics;
    private List<Integer> topologicalOrder;
    private boolean[] visited;
    private boolean[] recursionStack;
    private boolean hasCycle;
    
    /**
     * Creates DFS-based topological sorter
     */
    public DFSTopologicalSort(DirectedGraph graph) {
        this.graph = graph;
        this.metrics = new PerformanceMetrics();
    }
    
    /**
     * Performs topological sort using DFS
     * @return topological order if DAG, empty list if cycle exists
     */
    public List<Integer> sort() {
        metrics.startTimer();
        
        int n = graph.getVertexCount();
        visited = new boolean[n];
        recursionStack = new boolean[n];
        hasCycle = false;
        
        Stack<Integer> stack = new Stack<>();
        
        // Visit all vertices
        for (int v = 0; v < n; v++) {
            if (!visited[v]) {
                if (dfs(v, stack)) {
                    hasCycle = true;
                    break;
                }
            }
        }
        
        // Build topological order
        topologicalOrder = new ArrayList<>();
        if (!hasCycle) {
            while (!stack.isEmpty()) {
                topologicalOrder.add(stack.pop());
                metrics.increment("stack_pops");
            }
        } else {
            metrics.increment("cycle_detected");
        }
        
        metrics.stopTimer();
        return new ArrayList<>(topologicalOrder);
    }
    
    /**
     * DFS traversal with cycle detection
     * @return true if cycle detected, false otherwise
     */
    private boolean dfs(int vertex, Stack<Integer> stack) {
        visited[vertex] = true;
        recursionStack[vertex] = true;
        metrics.increment("dfs_visits");
        
        // Visit all neighbors
        for (DirectedGraph.Edge edge : graph.getEdges(vertex)) {
            int neighbor = edge.getDestination();
            metrics.increment("edge_explorations");
            
            if (!visited[neighbor]) {
                if (dfs(neighbor, stack)) {
                    return true; // Cycle found in recursion
                }
            } else if (recursionStack[neighbor]) {
                // Back edge found - cycle detected
                metrics.increment("back_edges_found");
                return true;
            }
        }
        
        recursionStack[vertex] = false;
        stack.push(vertex);
        metrics.increment("stack_pushes");
        
        return false;
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
     * Returns topological order with labels
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
     * Checks if graph is a DAG
     */
    public boolean isDAG() {
        if (topologicalOrder == null) {
            sort();
        }
        return !hasCycle;
    }
    
    /**
     * Returns performance metrics
     */
    public PerformanceMetrics getMetrics() {
        return metrics;
    }
    
    /**
     * Verifies the topological order is valid
     */
    public boolean verifyOrder() {
        if (topologicalOrder == null || hasCycle) {
            return false;
        }
        
        Map<Integer, Integer> position = new HashMap<>();
        for (int i = 0; i < topologicalOrder.size(); i++) {
            position.put(topologicalOrder.get(i), i);
        }
        
        for (int u : topologicalOrder) {
            for (DirectedGraph.Edge edge : graph.getEdges(u)) {
                int v = edge.getDestination();
                if (position.get(u) >= position.get(v)) {
                    return false;
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
        
        if (hasCycle) {
            System.out.println("Graph contains cycle - no topological order exists");
            return;
        }
        
        System.out.println("Topological Order (DFS-based):");
        System.out.print("  ");
        for (int i = 0; i < topologicalOrder.size(); i++) {
            if (i > 0) System.out.print(" → ");
            System.out.print(graph.getVertexLabel(topologicalOrder.get(i)));
        }
        System.out.println();
    }
}
