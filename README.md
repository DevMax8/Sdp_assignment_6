# Assignment 6 — Visitor Pattern

**Student:** Niyazov Maksat  
**Group:** SE-2522  
**Topic:** Visitor Pattern — Advanced File System  
**Repository:** https://github.com/DevMax8/Sdp_assignment_6

## Project Overview

This project demonstrates the Visitor Design Pattern using an
in-memory hierarchical file system.

The application supports files, directories, and nested structures.
Three independent visitors perform different operations:

- `TotalSizeVisitor` calculates the total size of files.
- `SearchVisitor` finds files by filename and minimum size.
- `StatisticsVisitor` counts files and directories and calculates maximum depth.

File names such as `App.java` and `Helper.java` represent virtual files
in the demonstration, not additional Java classes.

## Role Map

| Role | Class | Source Path |
|---|---|---|
| Element interface | Element | src/main/java/Element.java |
| Shared element base | AbstractElement | src/main/java/AbstractElement.java |
| Concrete Element | FileNode | src/main/java/FileNode.java |
| Concrete Element | DirectoryNode | src/main/java/DirectoryNode.java |
| Visitor interface | Visitor | src/main/java/Visitor.java |
| Concrete Visitor | TotalSizeVisitor | src/main/java/TotalSizeVisitor.java |
| Concrete Visitor | SearchVisitor | src/main/java/SearchVisitor.java |
| Concrete Visitor | StatisticsVisitor | src/main/java/StatisticsVisitor.java |
| Traversal owner | FileSystemWalker | src/main/java/FileSystemWalker.java |
| Client | Main | src/main/java/Main.java |

## Visitor Implementation

### Double Dispatch

Each concrete element implements `accept(Visitor)` and calls:

```java
visitor.visit(this);
```

`FileNode` calls `visit(FileNode)`, while `DirectoryNode` calls
`visit(DirectoryNode)`.

This allows visitors to perform element-specific operations without
`instanceof`, reflection, or type-checking chains.

### Traversal

`FileSystemWalker` owns traversal and uses an explicit stack.

Each reachable element is visited exactly once per operation.
Visitors process individual elements without traversing children.

The walker supplies the current path and depth before calling `accept()`.

### State Management

The walker calls `reset()` before each operation.

Visitors can be reused sequentially without accumulating previous results.

### Tree Integrity

The implementation rejects:

- Negative file sizes.
- Invalid element names.
- Null children.
- Duplicate sibling names.
- Multiple parent ownership.
- Cyclic directory relationships.

Child collections and search results are returned as immutable snapshots.

## Build and Run

Requirements:

- JDK 17 or newer.
- Apache Maven 3.8 or newer.

Run all commands from the project root.

Compile:

```shell
mvn compile
```

Run the demonstration:

```shell
java -cp target/classes Main
```

Run all automated tests:

```shell
mvn clean test
```

The project can be compiled, tested, and executed without an IDE.

## Demonstration Output

```text
=== TOTAL SIZE ===
Total size: 1450 bytes

=== SEARCH RESULTS ===
src/App.java
src/Main.java
src/utils/Helper.java

=== STATISTICS ===
Files: 6
Directories: 4
Maximum depth: 3
```

The search selects filenames ending with `.java` and sizes greater
than or equal to 200 bytes.

Search paths are relative to the chosen root, use `/` separators,
and are sorted lexicographically.

The root has depth `0`. Directory counts include the visited root
when it is a directory.

## Automated Tests

The project uses JUnit 5.

| Test Class | Tests | Verification |
|---|---:|---|
| DoubleDispatchTest | 2 | Correct visit method for each element type. |
| TotalSizeVisitorTest | 3 | Nested totals, empty directories, repeated execution. |
| SearchVisitorTest | 3 | Combined filters, sorted paths, no matches. |
| StatisticsVisitorTest | 2 | Counts, maximum depth, empty roots. |
| TreeIntegrityTest | 5 | Invalid mutations and immutable children. |
| TraversalAndStateTest | 10 | Traversal, state reset, subtree roots, deep nesting, invalid inputs, overflow. |

Verified result:

```text
Tests run: 25, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

The test suite was verified with Maven 3.9.11 and Oracle JDK 17.0.4.1.

## Project Structure

```text
Sdp_assignment_6/
├── pom.xml
├── README.md
├── src/
│   ├── main/java/
│   │   ├── Main.java
│   │   ├── Element.java
│   │   ├── AbstractElement.java
│   │   ├── FileNode.java
│   │   ├── DirectoryNode.java
│   │   ├── Visitor.java
│   │   ├── TotalSizeVisitor.java
│   │   ├── SearchVisitor.java
│   │   ├── StatisticsVisitor.java
│   │   └── FileSystemWalker.java
│   └── test/java/
│       ├── DoubleDispatchTest.java
│       ├── TotalSizeVisitorTest.java
│       ├── SearchVisitorTest.java
│       ├── StatisticsVisitorTest.java
│       ├── TreeIntegrityTest.java
│       └── TraversalAndStateTest.java
└── docs/
    ├── uml.puml
    └── verification.txt
```

## Design Pattern Summary

The Visitor Pattern separates operations from the elements on which
they operate.

In this project:

- `Element` defines the common element contract.
- `AbstractElement` centralizes name validation and parent ownership.
- `FileNode` and `DirectoryNode` represent concrete elements.
- `Visitor` defines overloaded operations for both element types.
- Concrete visitors implement independent operations.
- `FileSystemWalker` manages traversal, context, and visitor reset.

A new operation can be added by implementing `Visitor` without
changing existing element `accept()` methods.

Adding a new element type requires updating the Visitor interface
and its implementations.