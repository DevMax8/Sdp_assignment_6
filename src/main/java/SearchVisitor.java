import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class SearchVisitor implements Visitor {
    private final Predicate<String> filenameCriterion;
    private final long minimumSize;
    private final List<String> matches = new ArrayList<>();
    private String currentPath = "";

    public SearchVisitor(Predicate<String> filenameCriterion, long minimumSize) {
        if (filenameCriterion == null || minimumSize < 0) {
            throw new IllegalArgumentException("Invalid search configuration");
        }
        this.filenameCriterion = filenameCriterion;
        this.minimumSize = minimumSize;
    }

    @Override
    public void reset() {
        matches.clear();
        currentPath = "";
    }

    @Override
    public void setContext(String path, int depth) {
        currentPath = path;
    }

    @Override
    public void visit(FileNode file) {
        if (filenameCriterion.test(file.getName()) && file.getSize() >= minimumSize) {
            matches.add(currentPath);
        }
    }

    @Override
    public void visit(DirectoryNode directory) {
        // Directories are not search results.
    }

    public List<String> getMatches() {
        return matches.stream().sorted().toList();
    }
}
