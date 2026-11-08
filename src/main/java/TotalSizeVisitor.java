
public final class TotalSizeVisitor implements Visitor {

    private long totalSize;

    @Override
    public void visit(FileNode file) {
        totalSize = Math.addExact(totalSize, file.getSize());
    }

    @Override
    public void visit(DirectoryNode directory) {
        // Directories have no file size.
    }

    @Override
    public void reset() {
        totalSize = 0;
    }

    public long getTotalSize() {
        return totalSize;
    }
}
