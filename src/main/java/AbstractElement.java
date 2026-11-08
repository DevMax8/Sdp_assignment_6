import java.util.Objects;

/** Shared name validation and parent ownership; operations use Visitor dispatch. */
abstract class AbstractElement implements Element {
    private final String name;
    private DirectoryNode parent;

    protected AbstractElement(String name) {
        Objects.requireNonNull(name, "Name cannot be null");
        if (name.isBlank() || name.equals(".") || name.equals("..")
                || name.contains("/") || name.contains("\\") || name.indexOf('\0') >= 0) {
            throw new IllegalArgumentException("Invalid element name: " + name);
        }
        this.name = name;
    }

    @Override
    public final String getName() {
        return name;
    }

    final DirectoryNode parent() {
        return parent;
    }

    final void attachTo(DirectoryNode directory) {
        if (parent != null) {
            throw new IllegalArgumentException("Element already has a parent");
        }
        parent = Objects.requireNonNull(directory);
    }
}
