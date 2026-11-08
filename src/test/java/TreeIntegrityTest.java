
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TreeIntegrityTest {

    @Test
    void shouldRejectNegativeFileSize() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new FileNode("invalid.txt", -10)
        );
    }

    @Test
    void shouldRejectDuplicateSiblingNames() {
        DirectoryNode root = new DirectoryNode("root");

        root.addChild(new FileNode("data.txt", 100));

        assertThrows(
                IllegalArgumentException.class,
                () -> root.addChild(new FileNode("data.txt", 200))
        );

        assertEquals(1, root.getChildren().size());
    }

    @Test
    void shouldRejectMultipleParentOwnership() {
        DirectoryNode first = new DirectoryNode("first");
        DirectoryNode second = new DirectoryNode("second");

        FileNode shared = new FileNode("shared.txt", 100);

        first.addChild(shared);

        assertThrows(
                IllegalArgumentException.class,
                () -> second.addChild(shared)
        );

        assertTrue(second.getChildren().isEmpty());
    }

    @Test
    void shouldRejectCycles() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode child = new DirectoryNode("child");

        root.addChild(child);

        assertThrows(
                IllegalArgumentException.class,
                () -> child.addChild(root)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> root.addChild(root)
        );
    }

    @Test
    void childrenCollectionShouldBeImmutable() {
        DirectoryNode root = new DirectoryNode("root");

        root.addChild(new FileNode("file.txt", 100));

        assertThrows(
                UnsupportedOperationException.class,
                () -> root.getChildren().clear()
        );

        assertEquals(1, root.getChildren().size());
    }
}
