package kz.smartcampus;

import kz.smartcampus.core.DirectedGraph;
import kz.smartcampus.core.PerformanceMetrics;
import kz.smartcampus.graph.dagsp.DAGShortestPath;
import kz.smartcampus.graph.scc.CondensationBuilder;
import kz.smartcampus.graph.scc.KosarajuSCC;
import kz.smartcampus.graph.topo.DFSTopologicalSort;
import kz.smartcampus.graph.topo.KahnTopologicalSort;
import kz.smartcampus.utils.GraphLoader;

import java.io.File;
import java.util.*;

/**
 * Main application for Smart Campus Scheduling System.
 * Demonstrates SCC detection, topological sorting, and shortest/longest paths.
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║   Smart Campus Scheduling System - Assignment 4       ║");
        System.out.println("║   Graph Algorithms: SCC, Topological Sort, DAG Paths  ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");
        
        // Load datasets
        Map<String, DirectedGraph> datasets = loadDatasets();
        
        if (datasets.isEmpty()) {
            System.out.println("No datasets found. Using built-in test graphs...\n");
            runBuiltInTests();
        } else {
            // Process each dataset
            for (Map.Entry<String, DirectedGraph> entry : datasets.entrySet()) {
                processDataset(entry.getKey(), entry.getValue());
            }
            
            // Generate summary report
            generateSummaryReport(datasets);
        }
    }
    
    /**
     * Loads all JSON datasets from the data directory
     */
    private static Map<String, DirectedGraph> loadDatasets() {
        Map<String, DirectedGraph> datasets = new LinkedHashMap<>();
        File dataDir = new File("data");
        
        if (!dataDir.exists() || !dataDir.isDirectory()) {
            return datasets;
        }
        
        File[] files = dataDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) {
            return datasets;
        }
        
        Arrays.sort(files, Comparator.comparing(File::getName));
        
        for (File file : files) {
            try {
                DirectedGraph graph = GraphLoader.loadFromJson(file.getPath());
                datasets.put(file.getName().replace(".json", ""), graph);
            } catch (Exception e) {
                System.err.println("Error loading " + file.getName() + ": " + e.getMessage());
            }
        }
        
        return datasets;
    }
    
    /**
     * Processes a single dataset through all algorithms
     */
    private static void processDataset(String name, DirectedGraph graph) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("DATASET: " + name);
        System.out.println("=".repeat(70));
        System.out.println("Vertices: " + graph.getVertexCount() + 
                         " | Edges: " + graph.getEdgeCount());
        System.out.println();
        
        // 1. Strongly Connected Components
        System.out.println("--- STEP 1: Strongly Connected Components (Kosaraju) ---");
        KosarajuSCC sccFinder = new KosarajuSCC(graph);
        List<List<Integer>> sccs = sccFinder.findSCCs();
        
        System.out.println("Found " + sccs.size() + " SCCs:");
        for (int i = 0; i < sccs.size(); i++) {
            List<Integer> scc = sccs.get(i);
            System.out.print("  SCC-" + i + " [size=" + scc.size() + "]: ");
            
            List<String> labels = new ArrayList<>();
            for (int v : scc) {
                labels.add(graph.getVertexLabel(v));
            }
            System.out.println(String.join(", ", labels));
        }
        
        PerformanceMetrics sccMetrics = sccFinder.getMetrics();
        System.out.println("\nPerformance: " + sccMetrics.getFormattedSummary());
        System.out.println("  DFS visits: " + sccMetrics.getCounter("dfs_visits"));
        System.out.println("  Edge explorations: " + sccMetrics.getCounter("edge_explorations"));
        
        // 2. Condensation Graph
        System.out.println("\n--- STEP 2: Condensation Graph ---");
        CondensationBuilder condBuilder = new CondensationBuilder(graph, sccFinder);
        DirectedGraph condensation = condBuilder.buildCondensation();
        
        System.out.println("Condensation DAG:");
        System.out.println("  Components: " + condensation.getVertexCount());
        System.out.println("  Edges: " + condensation.getEdgeCount());
        System.out.println("  Is Acyclic: " + condBuilder.isAcyclic());
        
        Map<String, Object> condStats = condBuilder.getStatistics();
        System.out.println("  Compression: " + condStats.get("compression_ratio"));
        
        // 3. Topological Sort (on condensation)
        System.out.println("\n--- STEP 3: Topological Sort ---");
        
        // Kahn's Algorithm
        KahnTopologicalSort kahnSort = new KahnTopologicalSort(condensation);
        List<Integer> kahnOrder = kahnSort.sort();
        
        System.out.println("Kahn's Algorithm:");
        if (kahnSort.isDAG()) {
            System.out.print("  Order: ");
            for (int i = 0; i < kahnOrder.size(); i++) {
                if (i > 0) System.out.print(" → ");
                System.out.print(condensation.getVertexLabel(kahnOrder.get(i)));
            }
            System.out.println();
            
            PerformanceMetrics kahnMetrics = kahnSort.getMetrics();
            System.out.println("  Performance: " + kahnMetrics.getFormattedSummary());
        } else {
            System.out.println("  CYCLE DETECTED!");
        }
        
        // DFS-based
        DFSTopologicalSort dfsSort = new DFSTopologicalSort(condensation);
        List<Integer> dfsOrder = dfsSort.sort();
        
        System.out.println("\nDFS-based Algorithm:");
        if (dfsSort.isDAG()) {
            System.out.print("  Order: ");
            for (int i = 0; i < dfsOrder.size(); i++) {
                if (i > 0) System.out.print(" → ");
                System.out.print(condensation.getVertexLabel(dfsOrder.get(i)));
            }
            System.out.println();
            
            PerformanceMetrics dfsMetrics = dfsSort.getMetrics();
            System.out.println("  Performance: " + dfsMetrics.getFormattedSummary());
        } else {
            System.out.println("  CYCLE DETECTED!");
        }
        
        // 4. Shortest/Longest Paths
        if (kahnSort.isDAG() || dfsSort.isDAG()) {
            System.out.println("\n--- STEP 4: DAG Shortest & Longest Paths ---");
            
            DAGShortestPath dagSP = new DAGShortestPath(condensation);
            
            // Shortest paths from first component
            System.out.println("\nShortest Paths from " + 
                             condensation.getVertexLabel(0) + ":");
            dagSP.computeShortestPaths(0);
            
            for (int v = 0; v < condensation.getVertexCount(); v++) {
                int dist = dagSP.getDistance(v);
                if (dist >= 0) {
                    List<String> path = dagSP.getPathLabels(v);
                    System.out.println("  To " + condensation.getVertexLabel(v) + 
                                     ": " + dist + " | Path: " + String.join(" → ", path));
                }
            }
            
            PerformanceMetrics spMetrics = dagSP.getMetrics();
            System.out.println("Performance: " + spMetrics.getFormattedSummary());
            
            // Critical path (longest)
            System.out.println("\nCritical Path (Longest Path):");
            DAGShortestPath.CriticalPathResult critical = dagSP.findCriticalPath();
            
            if (!critical.getPath().isEmpty()) {
                List<String> criticalLabels = new ArrayList<>();
                for (int v : critical.getPath()) {
                    criticalLabels.add(condensation.getVertexLabel(v));
                }
                System.out.println("  Path: " + String.join(" → ", criticalLabels));
                System.out.println("  Length: " + critical.getLength());
            } else {
                System.out.println("  No critical path found");
            }
        }
        
        System.out.println();
    }
    
    /**
     * Runs built-in test graphs
     */
    private static void runBuiltInTests() {
        System.out.println("Running built-in test graphs...\n");
        
        // Test 1: Simple DAG
        System.out.println("Test 1: Simple DAG");
        DirectedGraph dag = GraphLoader.createTestGraph(1);
        processDataset("Built-in-DAG", dag);
        
        // Test 2: Graph with cycle
        System.out.println("\nTest 2: Graph with Cycle");
        DirectedGraph cyclic = GraphLoader.createTestGraph(2);
        processDataset("Built-in-Cyclic", cyclic);
        
        // Test 3: Complex DAG
        System.out.println("\nTest 3: Complex DAG");
        DirectedGraph complex = GraphLoader.createTestGraph(3);
        processDataset("Built-in-Complex", complex);
    }
    
    /**
     * Generates summary report across all datasets
     */
    private static void generateSummaryReport(Map<String, DirectedGraph> datasets) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("SUMMARY REPORT");
        System.out.println("=".repeat(70));
        
        System.out.printf("%-20s %10s %10s %12s %12s\n", 
                         "Dataset", "Vertices", "Edges", "SCCs", "DAG?");
        System.out.println("-".repeat(70));
        
        for (Map.Entry<String, DirectedGraph> entry : datasets.entrySet()) {
            DirectedGraph graph = entry.getValue();
            KosarajuSCC scc = new KosarajuSCC(graph);
            scc.findSCCs();
            
            CondensationBuilder cond = new CondensationBuilder(graph, scc);
            cond.buildCondensation();
            
            System.out.printf("%-20s %10d %10d %12d %12s\n",
                             entry.getKey(),
                             graph.getVertexCount(),
                             graph.getEdgeCount(),
                             scc.getComponentCount(),
                             cond.isAcyclic() ? "Yes" : "No");
        }
        
        System.out.println("=".repeat(70));
        System.out.println("\nAll processing complete!");
    }
}
