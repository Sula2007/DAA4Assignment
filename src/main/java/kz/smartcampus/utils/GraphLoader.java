package kz.smartcampus.utils;

import com.google.gson.*;
import kz.smartcampus.core.DirectedGraph;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Utility for loading graphs from JSON files.
 * 
 * Expected JSON format:
 * {
 *   "vertices": 5,
 *   "edges": [
 *     {"from": 0, "to": 1, "weight": 3},
 *     {"from": 1, "to": 2, "weight": 2}
 *   ],
 *   "labels": {
 *     "0": "TaskA",
 *     "1": "TaskB"
 *   }
 * }
 */
public class GraphLoader {
    
    /**
     * Loads a graph from JSON file
     */
    public static DirectedGraph loadFromJson(String filePath) throws IOException {
        try (FileReader reader = new FileReader(filePath)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            
            // Get vertex count
            int vertices = json.get("vertices").getAsInt();
            DirectedGraph graph = new DirectedGraph(vertices);
            
            // Load edges
            JsonArray edges = json.getAsJsonArray("edges");
            for (JsonElement edgeElement : edges) {
                JsonObject edge = edgeElement.getAsJsonObject();
                int from = edge.get("from").getAsInt();
                int to = edge.get("to").getAsInt();
                int weight = edge.has("weight") ? edge.get("weight").getAsInt() : 1;
                
                graph.addEdge(from, to, weight);
            }
            
            // Load labels if present
            if (json.has("labels")) {
                JsonObject labels = json.getAsJsonObject("labels");
                for (String key : labels.keySet()) {
                    int vertex = Integer.parseInt(key);
                    String label = labels.get(key).getAsString();
                    graph.setVertexLabel(vertex, label);
                }
            }
            
            return graph;
        } catch (JsonSyntaxException e) {
            throw new IOException("Invalid JSON format: " + e.getMessage(), e);
        }
    }
    
    /**
     * Loads multiple graphs from a directory
     */
    public static Map<String, DirectedGraph> loadMultipleGraphs(String... filePaths) {
        Map<String, DirectedGraph> graphs = new LinkedHashMap<>();
        
        for (String path : filePaths) {
            try {
                DirectedGraph graph = loadFromJson(path);
                String name = extractFileName(path);
                graphs.put(name, graph);
            } catch (IOException e) {
                System.err.println("Failed to load graph from " + path + ": " + e.getMessage());
            }
        }
        
        return graphs;
    }
    
    /**
     * Creates a simple test graph programmatically
     */
    public static DirectedGraph createTestGraph(int type) {
        switch (type) {
            case 1: // Simple DAG
                return createSimpleDAG();
            case 2: // Graph with cycle
                return createGraphWithCycle();
            case 3: // Complex DAG
                return createComplexDAG();
            default:
                throw new IllegalArgumentException("Unknown graph type: " + type);
        }
    }
    
    private static DirectedGraph createSimpleDAG() {
        DirectedGraph graph = new DirectedGraph(6);
        graph.setVertexLabel(0, "Start");
        graph.setVertexLabel(1, "Task1");
        graph.setVertexLabel(2, "Task2");
        graph.setVertexLabel(3, "Task3");
        graph.setVertexLabel(4, "Task4");
        graph.setVertexLabel(5, "End");
        
        graph.addEdge(0, 1, 2);
        graph.addEdge(0, 2, 3);
        graph.addEdge(1, 3, 4);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 4, 2);
        graph.addEdge(4, 5, 3);
        
        return graph;
    }
    
    private static DirectedGraph createGraphWithCycle() {
        DirectedGraph graph = new DirectedGraph(4);
        graph.setVertexLabel(0, "A");
        graph.setVertexLabel(1, "B");
        graph.setVertexLabel(2, "C");
        graph.setVertexLabel(3, "D");
        
        graph.addEdge(0, 1, 2);
        graph.addEdge(1, 2, 3);
        graph.addEdge(2, 0, 1); // Creates cycle
        graph.addEdge(2, 3, 2);
        
        return graph;
    }
    
    private static DirectedGraph createComplexDAG() {
        DirectedGraph graph = new DirectedGraph(10);
        for (int i = 0; i < 10; i++) {
            graph.setVertexLabel(i, "N" + i);
        }
        
        graph.addEdge(0, 1, 3);
        graph.addEdge(0, 2, 2);
        graph.addEdge(1, 3, 4);
        graph.addEdge(1, 4, 5);
        graph.addEdge(2, 4, 2);
        graph.addEdge(2, 5, 3);
        graph.addEdge(3, 6, 2);
        graph.addEdge(4, 6, 1);
        graph.addEdge(4, 7, 4);
        graph.addEdge(5, 7, 2);
        graph.addEdge(6, 8, 3);
        graph.addEdge(7, 8, 2);
        graph.addEdge(7, 9, 5);
        graph.addEdge(8, 9, 1);
        
        return graph;
    }
    
    /**
     * Exports graph to JSON format
     */
    public static String toJson(DirectedGraph graph) {
        JsonObject json = new JsonObject();
        json.addProperty("vertices", graph.getVertexCount());
        
        JsonArray edges = new JsonArray();
        for (int u = 0; u < graph.getVertexCount(); u++) {
            for (DirectedGraph.Edge edge : graph.getEdges(u)) {
                JsonObject edgeObj = new JsonObject();
                edgeObj.addProperty("from", u);
                edgeObj.addProperty("to", edge.getDestination());
                edgeObj.addProperty("weight", edge.getWeight());
                edges.add(edgeObj);
            }
        }
        json.add("edges", edges);
        
        JsonObject labels = new JsonObject();
        for (int v = 0; v < graph.getVertexCount(); v++) {
            labels.addProperty(String.valueOf(v), graph.getVertexLabel(v));
        }
        json.add("labels", labels);
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(json);
    }
    
    private static String extractFileName(String path) {
        int lastSlash = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
        int lastDot = path.lastIndexOf('.');
        
        if (lastDot > lastSlash) {
            return path.substring(lastSlash + 1, lastDot);
        }
        return path.substring(lastSlash + 1);
    }
}
