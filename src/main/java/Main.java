
public class Main {

    public static void main(String[] args) {

        // 1. Create the root directory
        DirectoryNode root = new DirectoryNode("project");

        // 2. Create nested directories
        DirectoryNode src = new DirectoryNode("src");
        DirectoryNode utils = new DirectoryNode("utils");
        DirectoryNode docs = new DirectoryNode("docs");

        // 3. Add files
        root.addChild(new FileNode("README.md", 120));

        src.addChild(new FileNode("Main.java", 450));
        src.addChild(new FileNode("App.java", 350));

        utils.addChild(new FileNode("Helper.java", 230));
        utils.addChild(new FileNode("Config.txt", 100));

        docs.addChild(new FileNode("guide.txt", 200));

        // 4. Build the directory tree
        src.addChild(utils);
        root.addChild(src);
        root.addChild(docs);

        // 5. Create walker
        FileSystemWalker walker = new FileSystemWalker();

        // 6. Calculate total size
        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();

        walker.walk(root, sizeVisitor);

        System.out.println("=== TOTAL SIZE ===");
        System.out.println("Total size: "
                + sizeVisitor.getTotalSize() + " bytes");

        // 7. Search Java files with size >= 200
        SearchVisitor searchVisitor = new SearchVisitor(
                name -> name.endsWith(".java"),
                200
        );

        walker.walk(root, searchVisitor);

        System.out.println("\n=== SEARCH RESULTS ===");

        for (String path : searchVisitor.getMatches()) {
            System.out.println(path);
        }

        // 8. Collect statistics
        StatisticsVisitor statisticsVisitor =
                new StatisticsVisitor();

        walker.walk(root, statisticsVisitor);

        System.out.println("\n=== STATISTICS ===");
        System.out.println("Files: "
                + statisticsVisitor.getFileCount());
        System.out.println("Directories: "
                + statisticsVisitor.getDirectoryCount());
        System.out.println("Maximum depth: "
                + statisticsVisitor.getMaxDepth());
    }
}
