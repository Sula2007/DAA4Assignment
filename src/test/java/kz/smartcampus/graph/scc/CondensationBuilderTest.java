package kz.smartcampus.graph.scc;

import kz.smartcampus.core.DirectedGraph;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

/**
 * Unit tests for CondensationBuilder
 */
public class CondensationBuilderTest {
    
    @Test
    public void testDAGCondensation() {
        // Pure DAG should have same number of components as vertices
        DirectedGraph graph = new DirectedGraph(4);
        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 3);
        
        KosarajuSCC scc = new KosarajuSCC(graph);
        scc.findSCCs();
        
        CondensationBuilder builder = new CondensationBuilder(graph, scc);
        DirectedGraph condensation = builder.buildCondensation();
        
        assertEquals(4, condensation.getVertexCount());
        assertTrue(builder.isAcyclic());
    }
    
    @Test
    public void testSimpleCycleCondensation() {
        DirectedGraph graph = new DirectedGraph(3);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        
        KosarajuSCC scc = new KosarajuSCC(graph);
        scc.findSCCs();
        
        CondensationBuilder builder = new CondensationBuilder(graph, scc);
        DirectedGraph condensation = builder.buildCondensation();
        
        // All vertices in one SCC
        assertEquals(1, condensation.getVertexCount());
        assertEquals(0, condensation.getEdgeCount());
        assertTrue(builder.isAcyclic());
    }
    
    @Test
    public void testMultipleSCCsCondensation() {
        DirectedGraph graph = new DirectedGraph(7);
        // SCC 1: {0, 1, 2}
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        
        // SCC 2: {3, 4}
        graph.addEdge(3, 4);
        graph.addEdge(4, 3);
        
        // Isolated vertices: {5}, {6}
        
        // Edges between SCCs
        graph.addEdge(2, 3);
        graph.addEdge(4, 5);
        graph.addEdge(5, 6);
        
        KosarajuSCC scc = new KosarajuSCC(graph);
        scc.findSCCs();
        
        CondensationBuilder builder = new CondensationBuilder(graph, scc);
        DirectedGraph condensation = builder.buildCondensation();
        
        assertEquals(4, condensation.getVertexCount());
        assertEquals(3, condensation.getEdgeCount());
        assertTrue(builder.isAcyclic());
    }
    
    @Test
    public void testCondensationIsAlwaysAcyclic() {
        // Complex graph with multiple cycles
        DirectedGraph graph = new DirectedGraph(8);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        graph.addEdge(2, 3);
        graph.addEdge(3, 4);
        graph.addEdge(4, 5);
        graph.addEdge(5, 3);
        graph.addEdge(5, 6);
        graph.addEdge(6, 7);
        
        KosarajuSCC scc = new KosarajuSCC(graph);
        scc.findSCCs();
        
        CondensationBuilder builder = new CondensationBuilder(graph, scc);
        builder.buildCondensation();
        
        assertTrue(builder.isAcyclic());
    }
    
    @Test
    public void testComponentVerticesMapping() {
        DirectedGraph graph = new DirectedGraph(5);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        graph.addEdge(2, 3);
        graph.addEdge(3, 4);
        
        KosarajuSCC scc = new KosarajuSCC(graph);
        scc.findSCCs();
        
        CondensationBuilder builder = new CondensationBuilder(graph, scc);
        builder.buildCondensation();
        
        // Vertices 0, 1, 2 should be in same component
        int comp0 = builder.getComponentForVertex(0);
        int comp1 = builder.getComponentForVertex(1);
        int comp2 = builder.getComponentForVertex(2);
        
        assertEquals(comp0, comp1);
        assertEquals(comp1, comp2);
        
        // Vertices 3 and 4 should be in different components
        int comp3 = builder.getComponentForVertex(3);
        int comp4 = builder.getComponentForVertex(4);
        
        assertNotEquals(comp3, comp4);
    }
    
    @Test
    public void testGetComponentVertices() {
        DirectedGraph graph = new DirectedGraph(5);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        graph.addEdge(3, 4);
        
        KosarajuSCC scc = new KosarajuSCC(graph);
        scc.findSCCs();
        
        CondensationBuilder builder = new CondensationBuilder(graph, scc);
        builder.buildCondensation();
        
        List<Integer> component0 = builder.getComponentVertices(0);
        
        // Should have at least one vertex
        assertFalse(component0.isEmpty());
        
        // All vertices in this component should map back to component 0
        for (int v : component0) {
            assertEquals(0, builder.getComponentForVertex(v));
        }
    }
    
    @Test
    public void testStatistics() {
        DirectedGraph graph = new DirectedGraph(6);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        graph.addEdge(2, 3);
        graph.addEdge(3, 4);
        graph.addEdge(4, 5);
        
        KosarajuSCC scc = new KosarajuSCC(graph);
        scc.findSCCs();
        
        CondensationBuilder builder = new CondensationBuilder(graph, scc);
        builder.buildCondensation();
        
        Map<String, Object> stats = builder.getStatistics();
        
        assertEquals(6, stats.get("original_vertices"));
        assertEquals(6, stats.get("original_edges"));
        assertTrue((int) stats.get("condensation_vertices") <= 6);
        assertTrue((boolean) stats.get("is_acyclic"));
    }
    
    @Test
    public void testSingleVertex() {
        DirectedGraph graph = new DirectedGraph(1);
        
        KosarajuSCC scc = new KosarajuSCC(graph);
        scc.findSCCs();
        
        CondensationBuilder builder = new CondensationBuilder(graph, scc);
        DirectedGraph condensation = builder.buildCondensation();
        
        assertEquals(1, condensation.getVertexCount());
        assertEquals(0, condensation.getEdgeCount());
        assertTrue(builder.isAcyclic());
    }
    
    @Test
    public void testDisconnectedComponents() {
        DirectedGraph graph = new DirectedGraph(6);
        // Component 1: {0, 1}
        graph.addEdge(0, 1);
        graph.addEdge(1, 0);
        
        // Component 2: {2, 3}
        graph.addEdge(2, 3);
        graph.addEdge(3, 2);
        
        // Isolated: {4}, {5}
        
        KosarajuSCC scc = new KosarajuSCC(graph);
        scc.findSCCs();
        
        CondensationBuilder builder = new CondensationBuilder(graph, scc);
        DirectedGraph condensation = builder.buildCondensation();
        
        assertEquals(4, condensation.getVertexCount());
        assertEquals(0, condensation.getEdgeCount()); // No edges between components
        assertTrue(builder.isAcyclic());
    }
}
