"# Maze-solver" 
# 🎮 Algorithmic Maze Solver & Visualizer

A core desktop application built from scratch in Java using Swing and AWT. This project visually demonstrates the execution loops of classic graph traversal algorithms by generating a perfect maze and animating AI pathfinding agents solving it step-by-step.

> **Note:** To deeply understand memory layouts and pointer manipulation, this project completely bans `java.util` collection structures. All Stacks and Queues are custom-built generic implementations.

---

## 🚀 Features

- **Procedural Generation:** Carves out a perfect, loop-free grid environment using a **Randomized Depth-First Search (DFS)** backtracking algorithm.
- **Real-Time Visualizations:** Watch the AI wavefront search the grid at 30 FPS.
  - **Breadth-First Search (BFS):** Explores uniformly outward, expanding like water filling a room; guarantees the absolute shortest path.
  - **Depth-First Search (DFS):** Aggressively pursues a single branch down to dead ends before backtracking.
- **Asynchronous Loop Control:** Driven by a non-blocking `javax.swing.Timer` loop to prevent interface freeze frames.
- **Interactive Control Bar:** Includes real-time animation speed adjustments via sliders, instant maze generation resets, and algorithm toggles.

---

## 🛠️ Architecture Breakdown

The project follows a decoupled, modular design strategy:

- **`Cell.java`**: The node primitive managing position indices, wall arrays `[T, R, B, L]`, and search state flags.
- **`MyStack.java` / `MyQueue.java`**: Custom generic LIFO and FIFO pointer implementations built without standard framework classes.
- **`MazePanel.java`**: The rendering pipeline overriding `paintComponent` to redraw the cell matrix state on every timer tick.
- **`Main.java`**: Houses the application entry point and structural `JFrame` layout wrappers.

---

## 🖥️ How To Run Locally

Ensure you have the Java Development Kit (JDK) installed.

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/YOUR_ACTUAL_USERNAME/YOUR_ACTUAL_REPO_NAME.git](https://github.com/YOUR_ACTUAL_USERNAME/YOUR_ACTUAL_REPO_NAME.git)
