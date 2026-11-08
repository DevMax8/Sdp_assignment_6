
public interface Visitor {

    void visit(FileNode file);

    void visit(DirectoryNode directory);

    default void reset() {
    }

    default void setContext(String path, int depth) {
    }
}
