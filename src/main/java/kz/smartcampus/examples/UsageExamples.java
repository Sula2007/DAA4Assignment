package kz.smartcampus.examples;

import kz.smartcampus.core.DirectedGraph;
import kz.smartcampus.graph.dagsp.DAGShortestPath;
import kz.smartcampus.graph.scc.CondensationBuilder;
import kz.smartcampus.graph.scc.KosarajuSCC;
import kz.smartcampus.graph.topo.KahnTopologicalSort;
import kz.smartcampus.utils.GraphLoader;

import java.util.List;

/**
 * Usage examples for the Smart Campus Scheduling System.
 * These examples demonstrate how to use each algorithm.
 */
public class UsageExamples {
    
    /**
     * Example 1: Detecting Circular Dependencies in Course Prerequisites
     */
    public static void example1_CoursePrerequisites() {
        System.out.println("=== Example 1: Course Prerequisite Analysis ===\n");
        
        // Create a graph representing course dependencies
        DirectedGraph courses = new DirectedGraph(5);
        courses.setVertexLabel(0, "Intro_CS");
        courses.setVertexLabel(1, "Data_Structures");
        courses.setVertexLabel(2, "Algorithms");
        courses.setVertexLabel(3, "Operating_Systems");
        courses.setVertexLabel(4, "Databases");
        
        // Define prerequisites (edges)
        courses.addEdge(0, 1, 1); // Intro_CS -> Data_Structures
        courses.addEdge(1, 2, 1); // Data_Structures -> Algorithms
        courses.addEdge(2, 3, 1); // Algorithms -> Operating_Systems
        courses.addEdge(1, 4, 1); // Data_Structures -> Databases
        
        // Detect strongly connected components (circular prerequisites)
        KosarajuSCC scc = new KosarajuSCC(courses);
        List<List<Integer>> components = scc.findSCCs();
        
        System.out.println("Found " + components.size() + " components:");
        if (components.size() == courses.getVertexCount()) {
            System.out.println("✓ No circular prerequisites - valid curriculum!");
        } else {
            System.out.println("✗ Circular prerequisites detected!");
        }
        
        // Determine course order
        KahnTopologicalSort topo = new KahnTopologicalSort(courses);
        List<String> order = topo.getTopologicalOrderLabels();
        
        System.out.println("\nRecommended course sequence:");
        for (int i = 0; i < order.size(); i++) {
            System.out.println("  Semester " + (i + 1) + ": " + order.get(i));
        }
        
        System.out.println();
    }
    
    /**
     * Example 2: Building Maintenance Task Scheduling
     */
    public static void example2_MaintenanceTasks() {
        System.out.println("=== Example 2: Building Maintenance Scheduling ===\n");
        
        // Create graph with maintenance tasks
        DirectedGraph tasks = new DirectedGraph(7);
        tasks.setVertexLabel(0, "Inspection");
        tasks.setVertexLabel(1, "Electrical_Check");
        tasks.setVertexLabel(2, "Plumbing_Check");
        tasks.setVertexLabel(3, "Repair_Report");
        tasks.setVertexLabel(4, "Order_Parts");
        tasks.setVertexLabel(5, "Perform_Repairs");
        tasks.setVertexLabel(6, "Final_Inspection");
        
        // Task dependencies with durations (hours)
        tasks.addEdge(0, 1, 2);  // Inspection -> Electrical (2h)
        tasks.addEdge(0, 2, 2);  // Inspection -> Plumbing (2h)
        tasks.addEdge(1, 3, 1);  // Electrical -> Report (1h)
        tasks.addEdge(2, 3, 1);  // Plumbing -> Report (1h)
        tasks.addEdge(3, 4, 3);  // Report -> Order Parts (3h)
        tasks.addEdge(4, 5, 5);  // Parts -> Repairs (5h)
        tasks.addEdge(5, 6, 2);  // Repairs -> Final (2h)
        
        // Find critical path (longest path)
        DAGShortestPath dagSP = new DAGShortestPath(tasks);
        DAGShortestPath.CriticalPathResult critical = dagSP.findCriticalPath();
        
        System.out.println("Critical Path Analysis:");
        List<String> pathLabels = critical.getPath().stream()
            .map(tasks::getVertexLabel)
            .toList();
        
        System.out.println("  Path: " + String.join(" → ", pathLabels));
        System.out.println("  Total Duration: " + critical.getLength() + " hours");
        System.out.println("\n  This is the minimum time to complete all tasks.");
        
        System.out.println();
    }
    
    /**
     * Example 3: Sensor Network with Circular Dependencies
     */
    public static void example3_SensorNetwork() {
        System.out.println("=== Example 3: Sensor Network Analysis ===\n");
        
        // Create sensor dependency graph with cycles
        DirectedGraph sensors = new DirectedGraph(6);
        sensors.setVertexLabel(0, "Temp_Sensor_A");
        sensors.setVertexLabel(1, "Temp_Sensor_B");
        sensors.setVertexLabel(2, "Humidity_Sensor");
        sensors.setVertexLabel(3, "Pressure_Sensor");
        sensors.setVertexLabel(4, "Air_Quality");
        sensors.setVertexLabel(5, "Central_Hub");
        
        // Calibration dependencies (some circular for cross-validation)
        sensors.addEdge(0, 1, 1);
        sensors.addEdge(1, 2, 1);
        sensors.addEdge(2, 0, 1);  // Circular dependency for calibration
        sensors.addEdge(2, 3, 2);
        sensors.addEdge(3, 4, 1);
        sensors.addEdge(4, 5, 1);
        
        // Find strongly connected components
        KosarajuSCC scc = new KosarajuSCC(sensors);
        scc.findSCCs();
        
        System.out.println("Sensor Groups (SCCs):");
        scc.printComponents();
        
        // Build condensation to simplify network
        CondensationBuilder builder = new CondensationBuilder(sensors, scc);
        DirectedGraph simplified = builder.buildCondensation();
        
        System.out.println("\nNetwork Simplification:");
        System.out.println("  Original: " + sensors.getVertexCount() + " sensors, " 
                         + sensors.getEdgeCount() + " dependencies");
        System.out.println("  Simplified: " + simplified.getVertexCount() + " groups, " 
                         + simplified.getEdgeCount() + " connections");
        
        // Get initialization order
        KahnTopologicalSort topo = new KahnTopologicalSort(simplified);
        List<String> order = topo.getTopologicalOrderLabels();
        
        System.out.println("\nInitialization Sequence:");
        for (int i = 0; i < order.size(); i++) {
            System.out.println("  Step " + (i + 1) + ": " + order.get(i));
        }
        
        System.out.println();
    }
    
    /**
     * Example 4: Project Task Scheduling with PERT
     */
    public static void example4_ProjectScheduling() {
        System.out.println("=== Example 4: Project Scheduling (PERT Analysis) ===\n");
        
        // Software project tasks
        DirectedGraph project = new DirectedGraph(8);
        project.setVertexLabel(0, "Requirements");
        project.setVertexLabel(1, "Design");
        project.setVertexLabel(2, "Database_Schema");
        project.setVertexLabel(3, "Frontend_Dev");
        project.setVertexLabel(4, "Backend_Dev");
        project.setVertexLabel(5, "Integration");
        project.setVertexLabel(6, "Testing");
        project.setVertexLabel(7, "Deployment");
        
        // Task dependencies with estimated durations (days)
        project.addEdge(0, 1, 5);
        project.addEdge(1, 2, 3);
        project.addEdge(1, 3, 8);
        project.addEdge(1, 4, 10);
        project.addEdge(2, 4, 2);
        project.addEdge(3, 5, 3);
        project.addEdge(4, 5, 2);
        project.addEdge(5, 6, 7);
        project.addEdge(6, 7, 2);
        
        // Compute shortest paths from start
        DAGShortestPath dagSP = new DAGShortestPath(project);
        dagSP.computeShortestPaths(0);
        
        System.out.println("Earliest Start Times:");
        for (int i = 0; i < project.getVertexCount(); i++) {
            int earliestStart = dagSP.getDistance(i);
            System.out.println("  " + project.getVertexLabel(i) + ": Day " + earliestStart);
        }
        
        // Find critical path
        DAGShortestPath.CriticalPathResult critical = dagSP.findCriticalPath();
        
        System.out.println("\nCritical Path:");
        List<String> criticalTasks = critical.getPath().stream()
            .map(project::getVertexLabel)
            .toList();
        System.out.println("  " + String.join(" → ", criticalTasks));
        System.out.println("\nProject Duration: " + critical.getLength() + " days");
        System.out.println("(Tasks on critical path cannot be delayed)");
        
        System.out.println();
    }
    
    /**
     * Example 5: Loading Graph from JSON File
     */
    public static void example5_LoadFromJSON() {
        System.out.println("=== Example 5: Loading Graph from JSON ===\n");
        
        try {
            // Load a dataset
            DirectedGraph graph = GraphLoader.loadFromJson("data/small_dag_01.json");
            
            System.out.println("Loaded graph:");
            System.out.println("  Vertices: " + graph.getVertexCount());
            System.out.println("  Edges: " + graph.getEdgeCount());
            
            // Analyze it
            KosarajuSCC scc = new KosarajuSCC(graph);
            scc.findSCCs();
            
            System.out.println("  SCCs: " + scc.getComponentCount());
            System.out.println("\nProcessing time: " + 
                             scc.getMetrics().getExecutionTimeMs() + "ms");
            
        } catch (Exception e) {
            System.out.println("Error loading graph: " + e.getMessage());
        }
        
        System.out.println();
    }
    
    /**
     * Run all examples
     */
    public static void main(String[] args) {
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║  Smart Campus Scheduling - Usage Examples ║");
        System.out.println("╚════════════════════════════════════════════╝\n");
        
        example1_CoursePrerequisites();
        example2_MaintenanceTasks();
        example3_SensorNetwork();
        example4_ProjectScheduling();
        example5_LoadFromJSON();
        
        System.out.println("All examples completed!\n");
    }
}
