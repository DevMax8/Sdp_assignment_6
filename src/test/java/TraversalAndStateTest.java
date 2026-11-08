import org.junit.jupiter.api.Test;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TraversalAndStateTest {
    @Test
    void shouldVisitEveryElementExactlyOnce() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode nested = new DirectoryNode("nested");
        FileNode file = new FileNode("file.txt", 5);
        nested.addChild(file);
        root.addChild(nested);
        Map<Element, Integer> visits = new IdentityHashMap<>();
        Visitor counter = new Visitor() {
            public void visit(FileNode node) { visits.merge(node, 1, Integer::sum); }
            public void visit(DirectoryNode node) { visits.merge(node, 1, Integer::sum); }
        };
        new FileSystemWalker().walk(root, counter);
        assertEquals(3, visits.size());
        assertTrue(visits.values().stream().allMatch(count -> count == 1));
    }

    @Test
    void searchShouldResetAcrossDifferentRootsAndReturnImmutableSnapshots() {
        DirectoryNode first = new DirectoryNode("first");
        first.addChild(new FileNode("match.java", 50));
        SearchVisitor search = new SearchVisitor(name -> name.endsWith(".java"), 50);
        FileSystemWalker walker = new FileSystemWalker();
        walker.walk(first, search);
        List<String> oldResults = search.getMatches();
        assertEquals(List.of("match.java"), oldResults);
        assertThrows(UnsupportedOperationException.class, () -> oldResults.add("fake"));
        walker.walk(new DirectoryNode("empty"), search);
        assertTrue(search.getMatches().isEmpty());
        assertEquals(List.of("match.java"), oldResults);
    }

    @Test
    void statisticsShouldResetCountsAndDepth() {
        DirectoryNode first = new DirectoryNode("first");
        DirectoryNode nested = new DirectoryNode("nested");
        nested.addChild(new FileNode("data", 10));
        first.addChild(nested);
        StatisticsVisitor statistics = new StatisticsVisitor();
        FileSystemWalker walker = new FileSystemWalker();
        walker.walk(first, statistics);
        assertEquals(2, statistics.getMaxDepth());
        walker.walk(new DirectoryNode("empty"), statistics);
        assertAll(() -> assertEquals(0, statistics.getFileCount()),
                () -> assertEquals(1, statistics.getDirectoryCount()),
                () -> assertEquals(0, statistics.getMaxDepth()));
    }

    @Test
    void selectedSubtreeShouldStartAtDepthZeroAndExcludeItsAncestors() {
        DirectoryNode outer = new DirectoryNode("outer");
        DirectoryNode selected = new DirectoryNode("selected");
        selected.addChild(new FileNode("sample.txt", 25));
        outer.addChild(selected);
        FileSystemWalker walker = new FileSystemWalker();
        SearchVisitor search = new SearchVisitor(name -> true, 0);
        StatisticsVisitor statistics = new StatisticsVisitor();
        walker.walk(selected, search);
        walker.walk(selected, statistics);
        assertEquals(List.of("sample.txt"), search.getMatches());
        assertEquals(1, statistics.getDirectoryCount());
        assertEquals(1, statistics.getMaxDepth());
    }

    @Test
    void fileRootShouldHaveZeroDepthAndDotPath() {
        FileNode file = new FileNode("alone.txt", 9);
        FileSystemWalker walker = new FileSystemWalker();
        SearchVisitor search = new SearchVisitor(name -> true, 9);
        StatisticsVisitor statistics = new StatisticsVisitor();
        TotalSizeVisitor size = new TotalSizeVisitor();
        walker.walk(file, search);
        walker.walk(file, statistics);
        walker.walk(file, size);
        assertEquals(List.of("."), search.getMatches());
        assertEquals(1, statistics.getFileCount());
        assertEquals(0, statistics.getDirectoryCount());
        assertEquals(0, statistics.getMaxDepth());
        assertEquals(9, size.getTotalSize());
    }

    @Test
    void shouldHandleDeepTreesWithoutUsingTheCallStack() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode cursor = root;
        for (int depth = 0; depth < 1500; depth++) {
            DirectoryNode next = new DirectoryNode("d");
            cursor.addChild(next);
            cursor = next;
        }
        cursor.addChild(new FileNode("leaf", 7));
        StatisticsVisitor statistics = new StatisticsVisitor();
        new FileSystemWalker().walk(root, statistics);
        assertEquals(1501, statistics.getDirectoryCount());
        assertEquals(1501, statistics.getMaxDepth());
    }

    @Test
    void invalidNamesAndNullChildrenShouldBeRejected() {
        for (String name : List.of("", " ", ".", "..", "a/b", "a\\b", "a\0b")) {
            assertThrows(IllegalArgumentException.class, () -> new DirectoryNode(name));
            assertThrows(IllegalArgumentException.class, () -> new FileNode(name, 0));
        }
        DirectoryNode root = new DirectoryNode("root");
        assertThrows(NullPointerException.class, () -> root.addChild(null));
        assertTrue(root.getChildren().isEmpty());
    }

    @Test
    void rejectedAttachmentShouldNotClaimOwnership() {
        DirectoryNode first = new DirectoryNode("first");
        DirectoryNode second = new DirectoryNode("second");
        first.addChild(new FileNode("same", 1));
        FileNode rejected = new FileNode("same", 2);
        assertThrows(IllegalArgumentException.class, () -> first.addChild(rejected));
        second.addChild(rejected);
        assertEquals(List.of(rejected), second.getChildren());
    }

    @Test
    void directoriesCannotBeSharedAndFailedCyclesMustLeaveTreesIntact() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode nested = new DirectoryNode("nested");
        DirectoryNode other = new DirectoryNode("other");
        root.addChild(nested);
        assertThrows(IllegalArgumentException.class, () -> other.addChild(nested));
        assertThrows(IllegalArgumentException.class, () -> nested.addChild(root));
        assertTrue(nested.getChildren().isEmpty());
        assertTrue(other.getChildren().isEmpty());
        assertEquals(List.of(nested), root.getChildren());
    }

    @Test
    void invalidSearchConfigurationAndLongOverflowShouldBeRejected() {
        assertThrows(IllegalArgumentException.class, () -> new SearchVisitor(null, 0));
        assertThrows(IllegalArgumentException.class, () -> new SearchVisitor(name -> true, -1));
        DirectoryNode root = new DirectoryNode("root");
        root.addChild(new FileNode("maximum", Long.MAX_VALUE));
        root.addChild(new FileNode("overflow", 1));
        assertThrows(ArithmeticException.class,
                () -> new FileSystemWalker().walk(root, new TotalSizeVisitor()));
    }
}
