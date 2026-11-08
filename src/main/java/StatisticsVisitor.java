public final class StatisticsVisitor implements Visitor {
    private int fileCount;
    private int directoryCount;
    private int maxDepth;
    private int currentDepth;

    @Override
    public void reset() {
        fileCount = 0;
        directoryCount = 0;
        maxDepth = 0;
        currentDepth = 0;
    }

    @Override
    public void setContext(String path, int depth) {
        currentDepth = depth;
    }

    @Override
    public void visit(FileNode file) {
        fileCount++;
        maxDepth = Math.max(maxDepth, currentDepth);
    }

    @Override
    public void visit(DirectoryNode directory) {
        directoryCount++;
        maxDepth = Math.max(maxDepth, currentDepth);
    }

    public int getFileCount() {
        return fileCount;
    }

    public int getDirectoryCount() {
        return directoryCount;
    }

    public int getMaxDepth() {
        return maxDepth;
    }
}
