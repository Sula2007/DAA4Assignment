package kz.smartcampus.graph.scc;

import kz.smartcampus.core.DirectedGraph;

import java.util.*;

/**
 * Builds a condensation graph from SCCs.
 * The condensation graph is a DAG where each node represents an SCC,
 * and edges represent dependencies between components.
 * 
 * Properties:
 * - Always acyclic (DAG)
 * - Preserves reachability information
 * - Useful for hierarchical task scheduling
 */
public class CondensationBuilder {
    private final DirectedGraph originalGraph;
    private final KosarajuSCC sccFinder;
    private DirectedGraph condensationGraph;
    
    /**
     * Creates a condensation builder
     */
    public CondensationBuilder(DirectedGraph graph, KosarajuSCC sccFinder) {
        this.originalGraph = graph;
        this.sccFinder = sccFinder;
    }
    
    /**
     * Builds and returns the condensation DAG
     */
    public DirectedGraph buildCondensation() {
        List<List<Integer>> components = sccFinder.getComponents();
        int componentCount = components.size();
        
        condensationGraph = new DirectedGraph(componentCount);
        
        // Set component labels
        for (int i = 0; i < componentCount; i++) {
            List<Integer> component = components.get(i);
            String label = "C" + i + formatComponentVertices(component);
            condensationGraph.setVertexLabel(i, label);
        }
        
        // Add edges between components
        Set<String> addedEdges = new HashSet<>();
        
        for (int u = 0; u < originalGraph.getVertexCount(); u++) {
            int sourceComp = sccFinder.getComponentId(u);
            
            for (DirectedGraph.Edge edge : originalGraph.getEdges(u)) {
                int v = edge.getDestination();
                int destComp = sccFinder.getComponentId(v);
                
                // Only add edge if it connects different components
                if (sourceComp != destComp) {
                    String edgeKey = sourceComp + "->" + destComp;
                    
                    if (!addedEdges.contains(edgeKey)) {
                        // Use maximum weight among all edges between these components
                        condensationGraph.addEdge(sourceComp, destComp, edge.getWeight());
                        addedEdges.add(edgeKey);
                    }
                }
            }
        }
        
        return condensationGraph;
    }
    
    /**
     * Returns the condensation graph (builds it if not yet built)
     */
    public DirectedGraph getCondensationGraph() {
        if (condensationGraph == null) {
            buildCondensation();
        }
        return condensationGraph;
    }
    
    /**
     * Checks if the condensation graph is acyclic (should always be true)
     */
    public boolean isAcyclic() {
        if (condensationGraph == null) {
            buildCondensation();
        }
        
        // Use DFS to detect cycles
        int n = condensationGraph.getVertexCount();
        boolean[] visited = new boolean[n];
        boolean[] recStack = new boolean[n];
        
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                if (hasCycleDFS(i, visited, recStack)) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    private boolean hasCycleDFS(int v, boolean[] visited, boolean[] recStack) {
        visited[v] = true;
        recStack[v] = true;
        
        for (DirectedGraph.Edge edge : condensationGraph.getEdges(v)) {
            int neighbor = edge.getDestination();
            
            if (!visited[neighbor]) {
                if (hasCycleDFS(neighbor, visited, recStack)) {
                    return true;
                }
            } else if (recStack[neighbor]) {
                return true;
            }
        }
        
        recStack[v] = false;
        return false;
    }
    
    /**
     * Returns the original vertices in a component
     */
    public List<Integer> getComponentVertices(int componentId) {
        List<List<Integer>> components = sccFinder.getComponents();
        if (componentId >= 0 && componentId < components.size()) {
            return new ArrayList<>(components.get(componentId));
        }
        return new ArrayList<>();
    }
    
    /**
     * Maps original vertex to its component in condensation graph
     */
    public int getComponentForVertex(int originalVertex) {
        return sccFinder.getComponentId(originalVertex);
    }
    
    /**
     * Returns statistics about the condensation
     */
    public Map<String, Object> getStatistics() {
        if (condensationGraph == null) {
            buildCondensation();
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("original_vertices", originalGraph.getVertexCount());
        stats.put("original_edges", originalGraph.getEdgeCount());
        stats.put("condensation_vertices", condensationGraph.getVertexCount());
        stats.put("condensation_edges", condensationGraph.getEdgeCount());
        stats.put("compression_ratio", 
            String.format("%.2f%%", 
                100.0 * condensationGraph.getVertexCount() / originalGraph.getVertexCount())
        );
        stats.put("is_acyclic", isAcyclic());
        
        return stats;
    }
    
    private String formatComponentVertices(List<Integer> vertices) {
        if (vertices.size() <= 3) {
            List<String> labels = new ArrayList<>();
            for (int v : vertices) {
                labels.add(originalGraph.getVertexLabel(v));
            }
            return "[" + String.join(",", labels) + "]";
        }
        return "[" + vertices.size() + " vertices]";
    }
    
    @Override
    public String toString() {
        if (condensationGraph == null) {
            buildCondensation();
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Condensation Graph:\n");
        sb.append(condensationGraph.toString());
        sb.append("\nStatistics:\n");
        
        Map<String, Object> stats = getStatistics();
        for (Map.Entry<String, Object> entry : stats.entrySet()) {
            sb.append("  ").append(entry.getKey()).append(": ")
              .append(entry.getValue()).append("\n");
        }
        
        return sb.toString();
    }
}
