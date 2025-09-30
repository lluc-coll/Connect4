# Connect 4 – UPC Project

This project is part of the **PROP Practice** (UPC) and implements a Connect 4 game with an AI powered by the **MiniMax algorithm**, enhanced with **alpha-beta pruning** and **move ordering**.

The main goal of this project is to explore how to improve the efficiency of adversarial search algorithms by combining a **well-designed heuristic** with optimization techniques.

---

## ✨ Heuristic

Our heuristic consists of two components:

1. **Static scoring table (8x8)**

   * Higher values are assigned to central positions, as they provide more opportunities to create a four-in-a-row.
   * Values are added or subtracted depending on whether the position belongs to our player or the opponent.

2. **Consecutive line evaluation**

   * Counts consecutive tokens vertically, horizontally, and diagonally, as long as they can potentially lead to a win.
   * Scoring rules:

     * +50 for more than 3 consecutive pieces
     * +30 for 3 consecutive pieces with at least 1 empty space
     * +5 for 2 consecutive pieces with at least 2 empty spaces
     * +1 for 1 piece with at least 3 empty spaces

   * These values are added to give an heuristic value to each possible state of the game. If the pieces are yours, the value is added, if the pieces are of the enemy, the value is subtracted.

⚡ The heuristic is implemented efficiently: each cell is visited at most 4 times, with optimizations to skip unnecessary lines.

---

## 🧠 MiniMax Algorithm & Alpha-Beta Pruning

* **Without alpha-beta pruning**

  * Explores all nodes until the target depth.
  * At depth 8, this can exceed 136 million nodes, with move times of up to 20 seconds.

* **With alpha-beta pruning**

  * Prunes branches that cannot improve the result.
  * Huge reduction in explored nodes (from 136M to ~2.3M in our experiments).
  * Move times drop below 1 second.

---

## 📊 Move Ordering

Alpha-beta pruning becomes even more effective when promising moves are explored first:

* **Without ordering**

  * ~6–7M nodes explored per game
  * Up to 2 seconds per move

* **With descending ordering by heuristic value**

  * ~3M nodes explored per game
  * Less than 1 second per move
  * Winning or strong moves are prioritized, enabling faster pruning of weak branches.

---

## 🔑 Key Takeaways

* A **good heuristic** is crucial to guide the algorithm effectively.
* **Alpha-beta pruning** is essential to reduce the number of nodes drastically.
* **Move ordering** further enhances pruning, prioritizing strong moves and cutting down computation time.

For this Connect 4 project, the combination of **MiniMax + Alpha-Beta Pruning + Heuristic Move Ordering** provides a much more efficient AI compared to plain MiniMax.

This project was done by [lluc-coll](https://github.com/lluc-coll) and [MaxDT02](https://github.com/MaxDT02)
