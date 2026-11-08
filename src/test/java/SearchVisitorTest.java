
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SearchVisitorTest {

    @Test
    void shouldCombineNameAndMinimumSizeFilters() {
        DirectoryNode root = new DirectoryNode("project");

        root.addChild(new FileNode("small.java", 100));
        root.addChild(new FileNode("large.java", 500));
        root.addChild(new FileNode("large.txt", 800));
        root.addChild(new FileNode("exact.java", 200));

        SearchVisitor visitor = new SearchVisitor(
                name -> name.endsWith(".java"),
                200
        );

        new FileSystemWalker().walk(root, visitor);

        assertEquals(
                List.of("exact.java", "large.java"),
                visitor.getMatches()
        );
    }

    @Test
    void shouldReturnSortedRootRelativePaths() {
        DirectoryNode root = new DirectoryNode("project");
        DirectoryNode src = new DirectoryNode("src");
        DirectoryNode docs = new DirectoryNode("docs");

        src.addChild(new FileNode("Zebra.java", 400));
        src.addChild(new FileNode("Alpha.java", 300));
        docs.addChild(new FileNode("Notes.java", 200));

        root.addChild(src);
        root.addChild(docs);

        SearchVisitor visitor = new SearchVisitor(
                name -> name.endsWith(".java"),
                0
        );

        new FileSystemWalker().walk(root, visitor);

        assertEquals(
                List.of(
                        "docs/Notes.java",
                        "src/Alpha.java",
                        "src/Zebra.java"
                ),
                visitor.getMatches()
        );
    }

    @Test
    void noMatchingFilesShouldReturnEmptyList() {
        DirectoryNode root = new DirectoryNode("root");

        root.addChild(new FileNode("image.png", 500));
        root.addChild(new FileNode("notes.txt", 200));

        SearchVisitor visitor = new SearchVisitor(
                name -> name.endsWith(".java"),
                100
        );

        new FileSystemWalker().walk(root, visitor);

        assertTrue(visitor.getMatches().isEmpty());
    }
}
