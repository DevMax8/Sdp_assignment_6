
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TotalSizeVisitorTest {

    @Test
    void shouldCalculateSizeAcrossNestedDirectories() {
        DirectoryNode root = new DirectoryNode("project");
        DirectoryNode src = new DirectoryNode("src");
        DirectoryNode utils = new DirectoryNode("utils");

        root.addChild(new FileNode("README.md", 120));
        src.addChild(new FileNode("Main.java", 450));
        utils.addChild(new FileNode("Helper.java", 230));

        src.addChild(utils);
        root.addChild(src);

        TotalSizeVisitor visitor = new TotalSizeVisitor();

        new FileSystemWalker().walk(root, visitor);

        assertEquals(800, visitor.getTotalSize());
    }

    @Test
    void emptyDirectoryShouldHaveZeroSize() {
        DirectoryNode root = new DirectoryNode("empty");

        TotalSizeVisitor visitor = new TotalSizeVisitor();

        new FileSystemWalker().walk(root, visitor);

        assertEquals(0, visitor.getTotalSize());
    }

    @Test
    void repeatedExecutionShouldNotAccumulateOldResults() {
        DirectoryNode root = new DirectoryNode("root");
        root.addChild(new FileNode("sample.txt", 300));

        TotalSizeVisitor visitor = new TotalSizeVisitor();
        FileSystemWalker walker = new FileSystemWalker();

        walker.walk(root, visitor);
        assertEquals(300, visitor.getTotalSize());

        walker.walk(root, visitor);
        assertEquals(300, visitor.getTotalSize());
    }
}
