package kz.smartcampus.graph.scc;

import kz.smartcampus.core.DirectedGraph;
import kz.smartcampus.core.PerformanceMetrics;

import java.util.*;

/**
 * Kosaraju's algorithm for finding Strongly Connected Components.
 * 
 * Algorithm steps:
 * 1. Perform DFS on original graph and record finish times
 * 2. Create transpose (reverse) graph
 * 3. Perform DFS on transpose in decreasing order of finish times
 * 4. Each DFS tree in step 3 is an SCC
 * 
 * Time Complexity: O(V + E)
 * Space Complexity: O(V)
 */
public class KosarajuSCC {
    private final DirectedGraph graph;
    private final PerformanceMetrics metrics;
    private final List<List<Integer>> components;
    private final Map<Integer, Integer> vertexToComponent;
    private boolean[] visited;
    private Stack<Integer> finishStack;
    
    /**
     * Constructs SCC finder for the given graph
     */
    public KosarajuSCC(DirectedGraph graph) {
        this.graph = graph;
        this.metrics = new PerformanceMetrics();
        this.components = new ArrayList<>();
        this.vertexToComponent = new HashMap<>();
    }
    
    /**
     * Finds all strongly connected components
     * @return List of SCCs, where each SCC is a list of vertex indices
     */
    public List<List<Integer>> findSCCs() {
        metrics.startTimer();
        
        int vertexCount = graph.getVertexCount();
        visited = new boolean[vertexCount];
        finishStack = new Stack<>();
        
        // Step 1: First DFS pass - fill finish time stack
        for (int v = 0; v < vertexCount; v++) {
            if (!visited[v]) {
                firstDFS(v);
            }
        }
        
        // Step 2: Create transpose graph
        DirectedGraph transpose = graph.reverse();
        metrics.increment("graph_reversals");
        
        // Step 3: Second DFS pass on transpose graph
        Arrays.fill(visited, false);
        components.clear();
        vertexToComponent.clear();
        
        int componentId = 0;
        while (!finishStack.isEmpty()) {
            int v = finishStack.pop();
            if (!visited[v]) {
                List<Integer> component = new ArrayList<>();
                secondDFS(transpose, v, component);
                
                // Record component mapping
                for (int vertex : component) {
                    vertexToComponent.put(vertex, componentId);
                }
                
                components.add(component);
                componentId++;
            }
        }
        
        metrics.stopTimer();
        return new ArrayList<>(components);
    }
    
    /**
     * First DFS pass: records finish times (post-order)
     */
    private void firstDFS(int vertex) {
        visited[vertex] = true;
        metrics.increment("dfs_visits");
        
        for (DirectedGraph.Edge edge : graph.getEdges(vertex)) {
            metrics.increment("edge_explorations");
            if (!visited[edge.getDestination()]) {
                firstDFS(edge.getDestination());
            }
        }
        
        finishStack.push(vertex);
        metrics.increment("stack_pushes");
    }
    
    /**
     * Second DFS pass: collects vertices in each SCC
     */
    private void secondDFS(DirectedGraph transpose, int vertex, List<Integer> component) {
        visited[vertex] = true;
        component.add(vertex);
        metrics.increment("dfs_visits");
        
        for (DirectedGraph.Edge edge : transpose.getEdges(vertex)) {
            metrics.increment("edge_explorations");
            if (!visited[edge.getDestination()]) {
                secondDFS(transpose, edge.getDestination(), component);
            }
        }
    }
    
    /**
     * Returns the list of all SCCs
     */
    public List<List<Integer>> getComponents() {
        if (components.isEmpty()) {
            findSCCs();
        }
        return new ArrayList<>(components);
    }
    
    /**
     * Returns the number of SCCs
     */
    public int getComponentCount() {
        if (components.isEmpty()) {
            findSCCs();
        }
        return components.size();
    }
    
    /**
     * Returns which component a vertex belongs to
     */
    public int getComponentId(int vertex) {
        if (components.isEmpty()) {
            findSCCs();
        }
        return vertexToComponent.getOrDefault(vertex, -1);
    }
    
    /**
     * Returns the size of each component
     */
    public List<Integer> getComponentSizes() {
        if (components.isEmpty()) {
            findSCCs();
        }
        
        List<Integer> sizes = new ArrayList<>();
        for (List<Integer> component : components) {
            sizes.add(component.size());
        }
        return sizes;
    }
    
    /**
     * Checks if two vertices are in the same SCC
     */
    public boolean areStronglyConnected(int v1, int v2) {
        if (components.isEmpty()) {
            findSCCs();
        }
        return getComponentId(v1) == getComponentId(v2);
    }
    
    /**
     * Returns performance metrics
     */
    public PerformanceMetrics getMetrics() {
        return metrics;
    }
    
    /**
     * Prints all SCCs with labels
     */
    public void printComponents() {
        if (components.isEmpty()) {
            findSCCs();
        }
        
        System.out.println("Strongly Connected Components: " + components.size());
        for (int i = 0; i < components.size(); i++) {
            List<Integer> component = components.get(i);
            System.out.print("  SCC " + i + " (size " + component.size() + "): ");
            
            List<String> labels = new ArrayList<>();
            for (int v : component) {
                labels.add(graph.getVertexLabel(v));
            }
            System.out.println(String.join(", ", labels));
        }
    }
}
