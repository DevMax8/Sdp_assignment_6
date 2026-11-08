
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatisticsVisitorTest {

    @Test
    void shouldCalculateCorrectStatistics() {
        DirectoryNode root = new DirectoryNode("project");
        DirectoryNode src = new DirectoryNode("src");
        DirectoryNode utils = new DirectoryNode("utils");
        DirectoryNode docs = new DirectoryNode("docs");

        root.addChild(new FileNode("README.md", 100));
        src.addChild(new FileNode("Main.java", 300));
        utils.addChild(new FileNode("Helper.java", 200));
        docs.addChild(new FileNode("guide.txt", 150));

        src.addChild(utils);
        root.addChild(src);
        root.addChild(docs);

        StatisticsVisitor visitor = new StatisticsVisitor();

        new FileSystemWalker().walk(root, visitor);

        assertAll(
                () -> assertEquals(4, visitor.getFileCount()),
                () -> assertEquals(4, visitor.getDirectoryCount()),
                () -> assertEquals(3, visitor.getMaxDepth())
        );
    }

    @Test
    void emptyRootShouldHaveCorrectStatistics() {
        DirectoryNode root = new DirectoryNode("empty");

        StatisticsVisitor visitor = new StatisticsVisitor();

        new FileSystemWalker().walk(root, visitor);

        assertAll(
                () -> assertEquals(0, visitor.getFileCount()),
                () -> assertEquals(1, visitor.getDirectoryCount()),
                () -> assertEquals(0, visitor.getMaxDepth())
        );
    }
}
