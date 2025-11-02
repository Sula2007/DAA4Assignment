package kz.smartcampus.graph.topo;

import kz.smartcampus.core.DirectedGraph;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Unit tests for topological sorting algorithms
 */
public class TopologicalSortTest {
    
    @Test
    public void testKahnSimpleDAG() {
        DirectedGraph graph = createSimpleDAG();
        KahnTopologicalSort kahn = new KahnTopologicalSort(graph);
        
        List<Integer> order = kahn.sort();
        
        assertTrue(kahn.isDAG());
        assertEquals(4, order.size());
        assertTrue(kahn.verifyOrder());
    }
    
    @Test
    public void testDFSSimpleDAG() {
        DirectedGraph graph = createSimpleDAG();
        DFSTopologicalSort dfs = new DFSTopologicalSort(graph);
        
        List<Integer> order = dfs.sort();
        
        assertTrue(dfs.isDAG());
        assertEquals(4, order.size());
        assertTrue(dfs.verifyOrder());
    }
    
    @Test
    public void testKahnCycleDetection() {
        DirectedGraph graph = createGraphWithCycle();
        KahnTopologicalSort kahn = new KahnTopologicalSort(graph);
        
        List<Integer> order = kahn.sort();
        
        assertFalse(kahn.isDAG());
        assertTrue(order.isEmpty());
    }
    
    @Test
    public void testDFSCycleDetection() {
        DirectedGraph graph = createGraphWithCycle();
        DFSTopologicalSort dfs = new DFSTopologicalSort(graph);
        
        List<Integer> order = dfs.sort();
        
        assertFalse(dfs.isDAG());
        assertTrue(order.isEmpty());
    }
    
    @Test
    public void testKahnLinearChain() {
        DirectedGraph graph = new DirectedGraph(5);
        for (int i = 0; i < 4; i++) {
            graph.addEdge(i, i + 1);
        }
        
        KahnTopologicalSort kahn = new KahnTopologicalSort(graph);
        List<Integer> order = kahn.sort();
        
        assertTrue(kahn.isDAG());
        assertEquals(List.of(0, 1, 2, 3, 4), order);
    }
    
    @Test
    public void testDFSLinearChain() {
        DirectedGraph graph = new DirectedGraph(5);
        for (int i = 0; i < 4; i++) {
            graph.addEdge(i, i + 1);
        }
        
        DFSTopologicalSort dfs = new DFSTopologicalSort(graph);
        List<Integer> order = dfs.sort();
        
        assertTrue(dfs.isDAG());
        assertEquals(5, order.size());
        // First element should be 0
        assertEquals(0, order.get(0).intValue());
    }
    
    @Test
    public void testKahnDisconnectedGraph() {
        DirectedGraph graph = new DirectedGraph(5);
        graph.addEdge(0, 1);
        graph.addEdge(2, 3);
        // Vertex 4 is isolated
        
        KahnTopologicalSort kahn = new KahnTopologicalSort(graph);
        List<Integer> order = kahn.sort();
        
        assertTrue(kahn.isDAG());
        assertEquals(5, order.size());
    }
    
    @Test
    public void testDFSDisconnectedGraph() {
        DirectedGraph graph = new DirectedGraph(5);
        graph.addEdge(0, 1);
        graph.addEdge(2, 3);
        
        DFSTopologicalSort dfs = new DFSTopologicalSort(graph);
        List<Integer> order = dfs.sort();
        
        assertTrue(dfs.isDAG());
        assertEquals(5, order.size());
    }
    
    @Test
    public void testKahnSingleVertex() {
        DirectedGraph graph = new DirectedGraph(1);
        KahnTopologicalSort kahn = new KahnTopologicalSort(graph);
        
        List<Integer> order = kahn.sort();
        
        assertTrue(kahn.isDAG());
        assertEquals(List.of(0), order);
    }
    
    @Test
    public void testDFSSingleVertex() {
        DirectedGraph graph = new DirectedGraph(1);
        DFSTopologicalSort dfs = new DFSTopologicalSort(graph);
        
        List<Integer> order = dfs.sort();
        
        assertTrue(dfs.isDAG());
        assertEquals(List.of(0), order);
    }
    
    @Test
    public void testKahnDiamondDAG() {
        DirectedGraph graph = new DirectedGraph(4);
        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 3);
        
        KahnTopologicalSort kahn = new KahnTopologicalSort(graph);
        List<Integer> order = kahn.sort();
        
        assertTrue(kahn.isDAG());
        assertEquals(4, order.size());
        assertEquals(0, order.get(0).intValue());
        assertEquals(3, order.get(3).intValue());
    }
    
    @Test
    public void testDFSDiamondDAG() {
        DirectedGraph graph = new DirectedGraph(4);
        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 3);
        
        DFSTopologicalSort dfs = new DFSTopologicalSort(graph);
        List<Integer> order = dfs.sort();
        
        assertTrue(dfs.isDAG());
        assertTrue(dfs.verifyOrder());
    }
    
    @Test
    public void testKahnMetrics() {
        DirectedGraph graph = createSimpleDAG();
        KahnTopologicalSort kahn = new KahnTopologicalSort(graph);
        kahn.sort();
        
        assertTrue(kahn.getMetrics().getCounter("vertices_processed") > 0);
        assertTrue(kahn.getMetrics().getExecutionTimeMs() >= 0);
    }
    
    @Test
    public void testDFSMetrics() {
        DirectedGraph graph = createSimpleDAG();
        DFSTopologicalSort dfs = new DFSTopologicalSort(graph);
        dfs.sort();
        
        assertTrue(dfs.getMetrics().getCounter("dfs_visits") > 0);
        assertTrue(dfs.getMetrics().getExecutionTimeMs() >= 0);
    }
    
    // Helper methods
    private DirectedGraph createSimpleDAG() {
        DirectedGraph graph = new DirectedGraph(4);
        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 3);
        return graph;
    }
    
    private DirectedGraph createGraphWithCycle() {
        DirectedGraph graph = new DirectedGraph(3);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        return graph;
    }
}
