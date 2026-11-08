
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DoubleDispatchTest {

    @Test
    void fileShouldCallFileVisitMethod() {
        int[] calls = new int[2];

        Visitor visitor = new Visitor() {
            @Override
            public void visit(FileNode file) {
                calls[0]++;
            }

            @Override
            public void visit(DirectoryNode directory) {
                calls[1]++;
            }
        };

        new FileNode("data.txt", 100).accept(visitor);

        assertEquals(1, calls[0]);
        assertEquals(0, calls[1]);
    }

    @Test
    void directoryShouldCallDirectoryVisitMethod() {
        int[] calls = new int[2];

        Visitor visitor = new Visitor() {
            @Override
            public void visit(FileNode file) {
                calls[0]++;
            }

            @Override
            public void visit(DirectoryNode directory) {
                calls[1]++;
            }
        };

        new DirectoryNode("documents").accept(visitor);

        assertEquals(0, calls[0]);
        assertEquals(1, calls[1]);
    }
}
