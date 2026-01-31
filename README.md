# Genetic Algorithm Performance Comparison: Julia vs Java  
### Traveling Salesman Problem (TSP)

## 📌 Overview

This repository contains the source code developed as part of a **diploma thesis** focused on comparing the performance of **Julia** and **Java** in numerically intensive applications.  
The comparison is based on **genetic algorithm (GA)** implementations solving the **Traveling Salesman Problem (TSP)**.

Equivalent versions of the genetic algorithm were implemented in both languages to ensure a fair comparison. The study evaluates and analyzes differences in **execution time** and **peak memory usage** across multiple problem instances and algorithm configurations.

---

## 🎯 Objectives

The main goals of this project are:

- To implement equivalent genetic algorithms for TSP in **Julia** and **Java**
- To identify GA configurations that achieve the best solution quality (path cost)
- To compare performance in terms of:
  - **Execution time**
  - **Peak memory usage**
- To statistically analyze the experimental results
- To assess whether Julia is a viable alternative to Java for computationally intensive tasks

---

## 🧬 Genetic Algorithm Description

The genetic algorithm used in this project follows a classical GA structure:

- Population initialization  
- Fitness evaluation (based on tour length)  
- Selection  
- Crossover  
- Mutation  
- Replacement  
- Termination based on generation count  

Multiple configurations of GA parameters were tested (e.g. population size, mutation rate) to identify setups yielding the best solution quality.

---

## 🧪 Experimental Methodology

The experiments were conducted in two stages:

### Stage 1 – Solution Quality
- Various algorithm configurations were tested
- Configurations achieving the best **path cost** were selected for further analysis

### Stage 2 – Performance Evaluation
- Selected configurations were benchmarked
- Measurements included:
  - Execution time
  - Peak memory usage
- Experiments were performed on selected TSP instances
- Results were analyzed using **statistical tests**

---

## 📊 Results Summary

- **Julia achieved significantly lower execution times** for all tested instances and configurations
- **Peak memory usage** was also generally lower in Julia
- Memory usage results are harder to interpret due to differences in measurement methods between languages
- Overall, the results suggest that **Julia is an attractive alternative to Java** for numerically intensive applications

