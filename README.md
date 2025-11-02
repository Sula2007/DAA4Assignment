# Smart Campus Scheduling System - Assignment 4

**Course:** Design and Analysis of Algorithms  
**Topic:** Strongly Connected Components, Topological Sorting, and DAG Shortest/Longest Paths

## 📋 Overview

This project implements three fundamental graph algorithms for task scheduling and dependency management in smart campus systems:

1. **Kosaraju's Algorithm** - Strongly Connected Component (SCC) detection
2. **Topological Sorting** - Task ordering (Kahn's and DFS-based algorithms)
3. **DAG Shortest/Longest Paths** - Optimal path computation and critical path analysis

## 🏗️ Project Structure

```
├── src/
│   ├── main/
│   │   └── java/kz/smartcampus/
│   │       ├── Main.java                          # Main application entry
│   │       ├── core/
│   │       │   ├── DirectedGraph.java             # Graph data structure
│   │       │   └── PerformanceMetrics.java        # Performance tracking
│   │       ├── graph/
│   │       │   ├── scc/
│   │       │   │   ├── KosarajuSCC.java           # SCC detection
│   │       │   │   └── CondensationBuilder.java   # Condensation graph builder
│   │       │   ├── topo/
│   │       │   │   ├── KahnTopologicalSort.java   # Kahn's algorithm
│   │       │   │   └── DFSTopologicalSort.java    # DFS-based topological sort
│   │       │   └── dagsp/
│   │       │       └── DAGShortestPath.java       # Shortest/Longest paths in DAG
│   │       └── utils/
│   │           └── GraphLoader.java               # JSON graph loader
│   └── test/
│       └── java/kz/smartcampus/graph/
│           ├── scc/KosarajuSCCTest.java
│           ├── topo/TopologicalSortTest.java
│           └── dagsp/DAGShortestPathTest.java
├── data/                                          # 9 test datasets
│   ├── small_dag_01.json
│   ├── small_cyclic_01.json
│   ├── small_mixed_01.json
│   ├── medium_dag_01.json
│   ├── medium_cyclic_01.json
│   ├── medium_mixed_01.json
│   ├── large_dag_01.json
│   ├── large_cyclic_01.json
│   └── large_mixed_01.json
├── pom.xml
└── README.md
```

## 🚀 Getting Started

### Prerequisites

- Java 11 or higher
- Maven 3.6+

### Build the Project

```bash
mvn clean compile
```

### Run Tests

```bash
mvn test
```

### Execute Main Application

```bash
mvn exec:java -Dexec.mainClass="kz.smartcampus.Main"
```

## 📊 Datasets

The project includes **9 datasets** across three categories:

| Category | Size | Description | Files |
|----------|------|-------------|-------|
| **Small** | 6-8 vertices | Simple cases for validation | 3 files |
| **Medium** | 12-16 vertices | Mixed structures with SCCs | 3 files |
| **Large** | 25-35 vertices | Performance testing | 3 files |

Each category contains:
- **DAG** - Pure directed acyclic graph
- **Cyclic** - Graphs with multiple strongly connected components
- **Mixed** - Combination of cycles and acyclic paths

All edges have weights representing task durations (1-5 units).

## 🔬 Algorithms

### 1. Kosaraju's SCC Detection

**Complexity:** O(V + E)  
**Approach:** Two-pass DFS algorithm
- First DFS: Record finish times
- Transpose graph
- Second DFS: Find SCCs in decreasing finish time order

**Use Cases:**
- Detect circular dependencies in tasks
- Identify tightly coupled components
- Simplify complex graphs via condensation

### 2. Topological Sorting

**Two Implementations:**

#### Kahn's Algorithm (BFS-based)
- **Complexity:** O(V + E)
- Uses in-degree tracking
- Explicit cycle detection via queue processing

#### DFS-based Algorithm
- **Complexity:** O(V + E)
- Post-order traversal
- Memory efficient with recursion stack

**Use Cases:**
- Task scheduling with dependencies
- Build systems
- Course prerequisite planning

### 3. DAG Shortest/Longest Paths

**Complexity:** O(V + E)  
**Features:**
- Shortest path computation
- Longest path (critical path) finding
- Path reconstruction
- Handles negative weights (unlike Dijkstra)

**Use Cases:**
- Project scheduling (PERT/CPM)
- Resource optimization
- Critical path analysis

## 📈 Performance Metrics

The system tracks:
- **Operation Counts:**
  - DFS visits
  - Edge explorations
  - Queue operations (Kahn's)
  - Relaxation operations (DAG-SP)
- **Execution Time:** Measured in milliseconds
- **Graph Statistics:** Vertices, edges, SCC count

## 🧪 Testing

**Test Coverage:**
- **KosarajuSCCTest:** 10 test cases
  - Single/multiple vertices
  - Simple and complex cycles
  - DAG detection
  - Component verification
  
- **TopologicalSortTest:** 13 test cases (both algorithms)
  - DAG sorting
  - Cycle detection
  - Disconnected graphs
  - Order verification
  
- **DAGShortestPathTest:** 10 test cases
  - Shortest paths
  - Longest paths (critical path)
  - Path reconstruction
  - Unreachable vertices

**Run tests with:**
```bash
mvn test
```

## 📝 Example Output

```
╔════════════════════════════════════════════════════════╗
║   Smart Campus Scheduling System - Assignment 4       ║
║   Graph Algorithms: SCC, Topological Sort, DAG Paths  ║
╚════════════════════════════════════════════════════════╝

======================================================================
DATASET: medium_cyclic_01
======================================================================
Vertices: 14 | Edges: 16

--- STEP 1: Strongly Connected Components (Kosaraju) ---
Found 5 SCCs:
  SCC-0 [size=3]: CycleA1, CycleA2, CycleA3
  SCC-1 [size=3]: CycleB1, CycleB2, CycleB3
  SCC-2 [size=3]: CycleC1, CycleC2, CycleC3
  SCC-3 [size=1]: Init
  SCC-4 [size=4]: Bridge, Process, Finalize, Complete

Performance: Time: 0.245ms | Ops: 42
  DFS visits: 28
  Edge explorations: 32

--- STEP 2: Condensation Graph ---
Condensation DAG:
  Components: 5
  Edges: 4
  Is Acyclic: true
  Compression: 35.71%

--- STEP 3: Topological Sort ---
Kahn's Algorithm:
  Order: C3[Init] → C0[CycleA1,CycleA2,CycleA3] → C1[CycleB1,CycleB2,CycleB3] 
         → C2[CycleC1,CycleC2,CycleC3] → C4[4 vertices]
  Performance: Time: 0.078ms | Ops: 14

--- STEP 4: DAG Shortest & Longest Paths ---
Shortest Paths from C3[Init]:
  To C3[Init]: 0 | Path: C3[Init]
  To C0[CycleA1,CycleA2,CycleA3]: 3 | Path: C3[Init] → C0[...]
  To C1[CycleB1,CycleB2,CycleB3]: 6 | Path: C3[Init] → C0[...] → C1[...]
  To C2[CycleC1,CycleC2,CycleC3]: 10 | Path: C3[Init] → C0[...] → C1[...] → C2[...]
  To C4[4 vertices]: 15 | Path: C3[Init] → ... → C4[4 vertices]

Critical Path (Longest Path):
  Path: C3[Init] → C0[...] → C1[...] → C2[...] → C4[4 vertices]
  Length: 15
```

## 🎯 Key Features

✅ **Kosaraju's Algorithm** - Efficient two-pass SCC detection  
✅ **Condensation Graph** - Automatic DAG construction from SCCs  
✅ **Dual Topological Sort** - Kahn's and DFS implementations  
✅ **DAG Path Algorithms** - Shortest and longest path computation  
✅ **Performance Tracking** - Built-in metrics system  
✅ **Comprehensive Tests** - 33+ JUnit test cases  
✅ **JSON Data Loading** - Flexible graph input format  
✅ **Cycle Detection** - Automatic identification of cyclic dependencies  

## 📚 Algorithm Complexity Summary

| Algorithm | Time Complexity | Space Complexity |
|-----------|----------------|------------------|
| Kosaraju SCC | O(V + E) | O(V) |
| Kahn's Topo Sort | O(V + E) | O(V) |
| DFS Topo Sort | O(V + E) | O(V) |
| DAG Shortest Path | O(V + E) | O(V) |
| DAG Longest Path | O(V + E) | O(V) |

## 🔍 Use Cases

- **Smart Campus:**
  - Course scheduling with prerequisites
  - Building maintenance task ordering
  - Sensor network dependency management

- **Software Engineering:**
  - Build system optimization
  - Module dependency resolution
  - CI/CD pipeline scheduling

- **Project Management:**
  - PERT/CPM analysis
  - Critical path identification
  - Resource allocation

## 👨‍💻 Author

Assignment 4 - Graph Algorithms Implementation  
Design and Analysis of Algorithms Course

## 📄 License

This project is developed for educational purposes as part of university coursework.
