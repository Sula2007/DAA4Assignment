package kz.smartcampus.graph.scc;

import kz.smartcampus.core.DirectedGraph;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Unit tests for Kosaraju's SCC algorithm
 */
public class KosarajuSCCTest {
    
    @Test
    public void testSingleVertex() {
        DirectedGraph graph = new DirectedGraph(1);
        KosarajuSCC scc = new KosarajuSCC(graph);
        
        List<List<Integer>> components = scc.findSCCs();
        
        assertEquals(1, components.size());
        assertEquals(1, components.get(0).size());
        assertEquals(0, components.get(0).get(0));
    }
    
    @Test
    public void testTwoDisconnectedVertices() {
        DirectedGraph graph = new DirectedGraph(2);
        KosarajuSCC scc = new KosarajuSCC(graph);
        
        List<List<Integer>> components = scc.findSCCs();
        
        assertEquals(2, components.size());
    }
    
    @Test
    public void testSimpleCycle() {
        DirectedGraph graph = new DirectedGraph(3);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        
        KosarajuSCC scc = new KosarajuSCC(graph);
        List<List<Integer>> components = scc.findSCCs();
        
        assertEquals(1, components.size());
        assertEquals(3, components.get(0).size());
    }
    
    @Test
    public void testTwoSeparateCycles() {
        DirectedGraph graph = new DirectedGraph(6);
        // First cycle: 0 -> 1 -> 2 -> 0
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        
        // Second cycle: 3 -> 4 -> 5 -> 3
        graph.addEdge(3, 4);
        graph.addEdge(4, 5);
        graph.addEdge(5, 3);
        
        KosarajuSCC scc = new KosarajuSCC(graph);
        List<List<Integer>> components = scc.findSCCs();
        
        assertEquals(2, components.size());
        assertEquals(3, components.get(0).size());
        assertEquals(3, components.get(1).size());
    }
    
    @Test
    public void testDAG() {
        DirectedGraph graph = new DirectedGraph(4);
        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 3);
        
        KosarajuSCC scc = new KosarajuSCC(graph);
        List<List<Integer>> components = scc.findSCCs();
        
        // DAG should have one SCC per vertex
        assertEquals(4, components.size());
        for (List<Integer> component : components) {
            assertEquals(1, component.size());
        }
    }
    
    @Test
    public void testComplexGraph() {
        DirectedGraph graph = new DirectedGraph(8);
        // SCC 1: {0, 1, 2}
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        
        // SCC 2: {3, 4}
        graph.addEdge(3, 4);
        graph.addEdge(4, 3);
        
        // SCC 3: {5}
        // SCC 4: {6, 7}
        graph.addEdge(6, 7);
        graph.addEdge(7, 6);
        
        // Connections between SCCs
        graph.addEdge(2, 3);
        graph.addEdge(4, 5);
        graph.addEdge(5, 6);
        
        KosarajuSCC scc = new KosarajuSCC(graph);
        List<List<Integer>> components = scc.findSCCs();
        
        assertEquals(4, components.size());
        
        // Verify component sizes
        List<Integer> sizes = scc.getComponentSizes();
        sizes.sort(Integer::compareTo);
        assertEquals(List.of(1, 2, 2, 3), sizes);
    }
    
    @Test
    public void testStronglyConnectedCheck() {
        DirectedGraph graph = new DirectedGraph(5);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        graph.addEdge(2, 3);
        graph.addEdge(3, 4);
        
        KosarajuSCC scc = new KosarajuSCC(graph);
        scc.findSCCs();
        
        // 0, 1, 2 are strongly connected
        assertTrue(scc.areStronglyConnected(0, 1));
        assertTrue(scc.areStronglyConnected(1, 2));
        assertTrue(scc.areStronglyConnected(0, 2));
        
        // 3 and 4 are not strongly connected to each other
        assertFalse(scc.areStronglyConnected(3, 4));
        
        // 0 and 3 are not strongly connected
        assertFalse(scc.areStronglyConnected(0, 3));
    }
    
    @Test
    public void testSelfLoop() {
        DirectedGraph graph = new DirectedGraph(3);
        graph.addEdge(0, 0); // Self loop
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        
        KosarajuSCC scc = new KosarajuSCC(graph);
        List<List<Integer>> components = scc.findSCCs();
        
        // Self loop creates SCC of size 1
        assertEquals(3, components.size());
    }
    
    @Test
    public void testMetricsTracking() {
        DirectedGraph graph = new DirectedGraph(4);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        
        KosarajuSCC scc = new KosarajuSCC(graph);
        scc.findSCCs();
        
        assertTrue(scc.getMetrics().getCounter("dfs_visits") > 0);
        assertTrue(scc.getMetrics().getCounter("edge_explorations") > 0);
        assertTrue(scc.getMetrics().getExecutionTimeMs() >= 0);
    }
}
