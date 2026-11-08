
# Assignment 6 — Visitor Pattern

## Advanced File System

**Course:** Software Design Patterns  
**University:** Astana IT University  
**Language:** Java 17  
**Build Tool:** Maven  
**Testing:** JUnit 5

## 1. Project Description

This project demonstrates the Visitor behavioral
design pattern using an in-memory hierarchical
file system.

The system supports files, directories, nested
structures, and three independent operations.

### Supported Operations

1. TotalSizeVisitor
    - Calculates the total size of all files.
    - Directories contribute zero bytes.

2. SearchVisitor
    - Filters files by filename.
    - Applies an inclusive minimum-size threshold.
    - Returns sorted, root-relative paths.

3. StatisticsVisitor
    - Counts files and directories.
    - Calculates maximum tree depth.
    - Uses root depth = 0.

## 2. Project Structure

Assignment6/
├── pom.xml
├── README.md
├── src/
│   ├── main/java/
│   │   ├── Main.java
│   │   ├── Element.java
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
│       └── TreeIntegrityTest.java
└── docs/
└── uml.puml

## 3. Requirements

- Java Development Kit 17
- Apache Maven 3.8 or newer

Check installed versions:

    java -version
    mvn -version

## 4. How to Run

Open a terminal in the project root.

Compile the project:

    mvn clean compile

Run the application:

    mvn exec:java

Run all automated tests:

    mvn clean test

## 5. Design Pattern

The project uses the Visitor behavioral pattern.

Element declares accept(Visitor).

FileNode and DirectoryNode implement Element.

Visitor declares two overloaded methods:

    visit(FileNode file)
    visit(DirectoryNode directory)

Each element calls the appropriate visit method
using visitor.visit(this).

This mechanism is called double dispatch.

## 6. Traversal Strategy

FileSystemWalker owns tree traversal.

Visitors do not recursively traverse children.

The walker uses an explicit stack for depth-first traversal, starting at the chosen root.
This avoids consuming the Java call stack for deeply nested directories.

Each reachable element is visited once
per operation.

The structure supports arbitrary nesting
and empty directories.

## 7. Tree Integrity

The implementation rejects:

- Negative file sizes
- Duplicate sibling names
- Multiple parent ownership
- Cyclic directory relationships
- Invalid element names

Directory child collections are read-only
through the public API.

## 8. Deterministic Results

Search paths are relative to the selected root.

The path separator is "/".

Results are sorted lexicographically.

Maximum depth starts at zero for the root.

## 9. Visitor State

Visitor instances must not retain results
from previous independent executions.

FileSystemWalker.walk resets visitor state before each new traversal,
then supplies the root-relative path and depth immediately before accept.

Execution order must not affect results.

## 10. Extensibility

New operations can be introduced by implementing
the existing Visitor interface.

Adding StatisticsVisitor does not require
changing the existing element accept methods.

Adding a new element type requires updating
the Visitor contract and its implementations.

## 11. Automated Testing

The test suite covers:

T1 - Correct double dispatch
T2 - Nested total-size calculation
T3 - Combined search filters
T4 - Deterministic root-relative paths
T5 - File-system statistics
T6 - Empty structures and no-match searches
T7 - Repeated execution and state isolation
T8 - Tree integrity and invalid mutations

Run:

    mvn clean test

## 12. UML Diagram

The UML source is located at:

    docs/uml.puml

## 13. GitHub Repository

Repository URL: [Add your GitHub repository URL]

## 14. Academic Integrity

This project is an individual assignment.

Implementation decisions, test results,
and architectural trade-offs must be
understood and explained during defense.


## Verified implementation details

- AbstractElement centralizes name validation and stores parent ownership internally.
- DirectoryNode.addChild accepts Element and uses double dispatch to attach either
  concrete node. It rejects duplicate names, reused children, and ancestor/self
  cycles before modifying the tree. Rejected attachments do not claim a parent.
- Element.accept selects the appropriate Visitor.visit overload. No instanceof,
  reflection, getClass branching, or casts select behavior between node types.
- FileSystemWalker alone owns iteration. SearchVisitor and StatisticsVisitor use
  setContext(path, depth), and all result-bearing visitors implement reset().
- Directory descendants use root-relative paths such as src/App.java, never the
  selected root's name. For a selected file as the root, its relative path is '.'.
  Root depth is zero, including for subtree traversal.
- getChildren and getMatches return immutable snapshots. Search results are sorted
  lexicographically and minimumSize is inclusive. Total-size overflow is rejected
  through Math.addExact rather than silently wrapping.
- Adding an operation requires a new Visitor implementation; node accept methods
  stay unchanged. Adding an element type requires a new visit overload, its
  implementations (including attachment), and the new element's accept method.
- Visitors are reusable for sequential walks; sharing one visitor across concurrent
  walks is unsupported. Use FileSystemWalker.walk for complete operations.

### Latest local verification

Maven 3.9.11, Oracle JDK 25.0.1, compiled with --release 17.
The bytecode targets Java 17; execution on a separate JDK 17 was not performed.

    mvn clean test
    Tests run: 25, Failures: 0, Errors: 0, Skipped: 0
    BUILD SUCCESS

Test classes: DoubleDispatchTest, SearchVisitorTest, StatisticsVisitorTest,
TotalSizeVisitorTest, TreeIntegrityTest, TraversalAndStateTest.

Run the demonstration from a clean checkout:

    mvn compile exec:java

Demonstration output (Main's current input tree):

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
