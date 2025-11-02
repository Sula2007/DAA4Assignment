package kz.smartcampus.graph.dagsp;

import kz.smartcampus.core.DirectedGraph;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Unit tests for DAG shortest/longest path algorithms
 */
public class DAGShortestPathTest {
    
    @Test
    public void testShortestPathSimpleDAG() {
        DirectedGraph graph = new DirectedGraph(4);
        graph.addEdge(0, 1, 2);
        graph.addEdge(0, 2, 4);
        graph.addEdge(1, 2, 1);
        graph.addEdge(1, 3, 4);
        graph.addEdge(2, 3, 2);
        
        DAGShortestPath dagSP = new DAGShortestPath(graph);
        assertTrue(dagSP.computeShortestPaths(0));
        
        assertEquals(0, dagSP.getDistance(0));
        assertEquals(2, dagSP.getDistance(1));
        assertEquals(3, dagSP.getDistance(2));
        assertEquals(5, dagSP.getDistance(3));
    }
    
    @Test
    public void testLongestPathSimpleDAG() {
        DirectedGraph graph = new DirectedGraph(4);
        graph.addEdge(0, 1, 2);
        graph.addEdge(0, 2, 4);
        graph.addEdge(1, 2, 1);
        graph.addEdge(1, 3, 4);
        graph.addEdge(2, 3, 2);
        
        DAGShortestPath dagSP = new DAGShortestPath(graph);
        assertTrue(dagSP.computeLongestPaths(0));
        
        assertEquals(0, dagSP.getDistance(0));
        assertEquals(2, dagSP.getDistance(1));
        assertEquals(4, dagSP.getDistance(2));
        assertEquals(6, dagSP.getDistance(3));
    }
    
    @Test
    public void testPathReconstruction() {
        DirectedGraph graph = new DirectedGraph(4);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);
        
        DAGShortestPath dagSP = new DAGShortestPath(graph);
        dagSP.computeShortestPaths(0);
        
        List<Integer> path = dagSP.getPath(3);
        assertEquals(List.of(0, 1, 2, 3), path);
    }
    
    @Test
    public void testUnreachableVertex() {
        DirectedGraph graph = new DirectedGraph(4);
        graph.addEdge(0, 1, 1);
        graph.addEdge(2, 3, 1);
        
        DAGShortestPath dagSP = new DAGShortestPath(graph);
        dagSP.computeShortestPaths(0);
        
        assertEquals(0, dagSP.getDistance(0));
        assertEquals(1, dagSP.getDistance(1));
        assertEquals(-1, dagSP.getDistance(2)); // Unreachable
        assertEquals(-1, dagSP.getDistance(3)); // Unreachable
    }
    
    @Test
    public void testCriticalPath() {
        DirectedGraph graph = new DirectedGraph(6);
        graph.addEdge(0, 1, 3);
        graph.addEdge(0, 2, 2);
        graph.addEdge(1, 3, 4);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 4, 2);
        graph.addEdge(4, 5, 3);
        
        DAGShortestPath dagSP = new DAGShortestPath(graph);
        DAGShortestPath.CriticalPathResult critical = dagSP.findCriticalPath();
        
        assertFalse(critical.getPath().isEmpty());
        assertTrue(critical.getLength() > 0);
    }
    
    @Test
    public void testGraphWithCycle() {
        DirectedGraph graph = new DirectedGraph(3);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1); // Creates cycle
        
        DAGShortestPath dagSP = new DAGShortestPath(graph);
        assertFalse(dagSP.computeShortestPaths(0));
    }
    
    @Test
    public void testSingleVertex() {
        DirectedGraph graph = new DirectedGraph(1);
        DAGShortestPath dagSP = new DAGShortestPath(graph);
        
        assertTrue(dagSP.computeShortestPaths(0));
        assertEquals(0, dagSP.getDistance(0));
    }
    
    @Test
    public void testLinearChain() {
        DirectedGraph graph = new DirectedGraph(5);
        for (int i = 0; i < 4; i++) {
            graph.addEdge(i, i + 1, 1);
        }
        
        DAGShortestPath dagSP = new DAGShortestPath(graph);
        dagSP.computeShortestPaths(0);
        
        for (int i = 0; i < 5; i++) {
            assertEquals(i, dagSP.getDistance(i));
        }
    }
    
    @Test
    public void testDiamondDAG() {
        DirectedGraph graph = new DirectedGraph(4);
        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 2, 2);
        graph.addEdge(1, 3, 2);
        graph.addEdge(2, 3, 1);
        
        DAGShortestPath dagSP = new DAGShortestPath(graph);
        dagSP.computeShortestPaths(0);
        
        // Shortest path to 3 should be 0 -> 1 -> 3 (distance 3)
        assertEquals(3, dagSP.getDistance(3));
    }
    
    @Test
    public void testMetrics() {
        DirectedGraph graph = new DirectedGraph(4);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);
        
        DAGShortestPath dagSP = new DAGShortestPath(graph);
        dagSP.computeShortestPaths(0);
        
        assertTrue(dagSP.getMetrics().getCounter("relaxations") > 0);
        assertTrue(dagSP.getMetrics().getExecutionTimeMs() >= 0);
    }
}
