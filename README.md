# Bio-OS: Algorithmic Bio-System Simulator

Bio-OS는 식물을 Tree 자료구조로 모델링하고, 환경 입력과 유전자 규칙에 따라 성장, 수분 분배, 에너지 계산, 가지치기, 회복 과정을 시뮬레이션하는 C++ 기반 생체 시스템 엔진입니다.

## Core Concepts

- Plant structure modeled as Tree
- Water distribution using BFS
- Energy evaluation using DFS
- Survival pruning using Priority Queue + Greedy strategy
- Gene rule parsing with IF-THEN syntax
- Logic inference based on environmental conditions
- State transition for biological behavior
- Simulation history and JSON snapshot export
- Native bridge layer for future JNI integration

## Current Features

- C++ Core Simulation Engine
- PlantNode / PlantTree
- Environment
- TickSystem
- WaterDistributor
- EnergyEvaluator
- PruningStrategy
- GrowthStrategy
- GeneRule
- RuleParser
- LogicInferenceEngine
- StateTransitionEngine
- SimulationLogger
- SimulationState
- SimulationHistory
- EngineFacade
- EngineSnapshot JSON Export
- EngineNativeBridge for JNI preparation

## Demo Scenario

The demo simulates plant adaptation across multiple ticks:

1. Dry but bright environment
2. Severe drought environment
3. Recovery environment
4. Stabilized environment

The plant can grow roots, boost photosynthesis, prune low-value leaves, and recover by growing new leaves.

## Project Goal

This project aims to demonstrate how biological systems can be abstracted using computer science concepts such as data structures, algorithms, state transition, and simulation systems.

Bio-OS is not intended to be a precise biological model. It is a bio-inspired simulation engine designed to show algorithmic modeling and system architecture.