# Quick Start Guide

## ⚡ Fast Setup

### 1. Prerequisites Check
```bash
java -version   # Should be 11 or higher
mvn -version    # Should be 3.6 or higher
```

### 2. Clone & Build
```bash
git clone <your-repo-url>
cd assignment4
mvn clean compile
```

### 3. Run Tests (Verify Installation)
```bash
mvn test
```

**Expected:** All 33 tests pass ✅

### 4. Run Main Application
```bash
mvn exec:java -Dexec.mainClass="kz.smartcampus.Main"
```

---

## 📁 Project Structure at a Glance

```
assignment4/
├── src/main/java/kz/smartcampus/
│   ├── Main.java                    ← Start here
│   ├── core/                        ← Graph data structures
│   └── graph/
│       ├── scc/                     ← Kosaraju's SCC
│       ├── topo/                    ← Topological sorting
│       └── dagsp/                   ← Shortest/longest paths
├── src/test/java/                   ← Unit tests
├── data/                            ← 9 JSON datasets
└── pom.xml                          ← Maven config
```

---

## 🎯 What Each Algorithm Does

### Kosaraju SCC
**Input:** Directed graph with possible cycles  
**Output:** Groups of strongly connected vertices  
**Use:** Detect circular dependencies

### Topological Sort
**Input:** Directed Acyclic Graph (DAG)  
**Output:** Linear ordering respecting dependencies  
**Use:** Task scheduling, build order

### DAG Shortest/Longest Paths
**Input:** DAG with edge weights  
**Output:** Optimal paths, critical path  
**Use:** Project planning, time optimization

---

## 🧪 Quick Test Commands

```bash
# Test only SCC algorithm
mvn test -Dtest=KosarajuSCCTest

# Test only topological sorting
mvn test -Dtest=TopologicalSortTest

# Test only DAG paths
mvn test -Dtest=DAGShortestPathTest

# Test everything
mvn test
```

---

## 📊 Understanding the Output

When you run the main application, you'll see for each dataset:

1. **SCC Detection:** How many strongly connected components found
2. **Condensation:** Compression ratio (how much the graph simplified)
3. **Topological Order:** Valid execution sequence of components
4. **Shortest Paths:** Minimum time to reach each component
5. **Critical Path:** Longest path (project completion time)

---

## 🔍 Exploring Datasets

All datasets are in `data/` folder:

- `small_*.json` - 6-8 vertices (learning/debugging)
- `medium_*.json` - 12-16 vertices (realistic scenarios)
- `large_*.json` - 25-35 vertices (performance testing)

**Variations:**
- `*_dag_*.json` - No cycles (pure topological sort)
- `*_cyclic_*.json` - Multiple cycles (SCC showcase)
- `*_mixed_*.json` - Mix of both (real-world)

---

## 🛠️ Troubleshooting

### Build fails
```bash
mvn clean install -U
```

### Tests fail
```bash
# Check Java version
java -version

# Rebuild
mvn clean test
```

### Main doesn't run
```bash
# Make sure you're in project root
cd assignment4
mvn exec:java -Dexec.mainClass="kz.smartcampus.Main"
```

### No datasets found
Make sure `data/` folder exists with 9 JSON files

---

## 📚 Next Steps

1. **Read README.md** - Full documentation
2. **Check REPORT.md** - Technical analysis
3. **Explore tests** - Learn algorithm behavior
4. **Modify datasets** - Create your own graphs
5. **Extend code** - Add new features

---

## 💡 Pro Tips

- Start with small datasets to understand output
- Run tests after any code changes
- Check `PerformanceMetrics` to see operation counts
- Use condensation to simplify cyclic graphs
- Critical path = longest path = project completion time

---

## 🆘 Getting Help

1. Check test cases for examples
2. Read algorithm comments in source code
3. Review REPORT.md for detailed explanations
4. Consult course materials

---

## ✅ Success Checklist

- [ ] Project builds without errors
- [ ] All 33 tests pass
- [ ] Main application runs successfully
- [ ] 9 datasets processed correctly
- [ ] Output makes sense (no empty results)
- [ ] Performance metrics shown

If all checked - you're good to go! 🎉

---

**Estimated setup time:** 5 minutes  
**First successful run:** Within 10 minutes
