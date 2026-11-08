import java.util.List;
import java.util.Objects;

public final class FileNode extends AbstractElement {
    private final long size;

    public FileNode(String name, long size) {
        super(name);
        if (size < 0) {
            throw new IllegalArgumentException("Negative file size");
        }
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    @Override
    public List<Element> getChildren() {
        return List.of();
    }

    @Override
    public void accept(Visitor visitor) {
        Objects.requireNonNull(visitor).visit(this);
    }
}
