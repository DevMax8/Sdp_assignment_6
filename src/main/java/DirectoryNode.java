import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class DirectoryNode extends AbstractElement {
    private final List<Element> children = new ArrayList<>();

    public DirectoryNode(String name) {
        super(name);
    }

    public void addChild(Element child) {
        // Double dispatch keeps parent attachment free of casts/type checks.
        Objects.requireNonNull(child, "Child cannot be null").accept(new Visitor() {
            @Override
            public void visit(FileNode file) {
                attachChild(file);
            }

            @Override
            public void visit(DirectoryNode directory) {
                attachChild(directory);
            }
        });
    }

    private void attachChild(AbstractElement child) {
        for (DirectoryNode ancestor = this; ancestor != null; ancestor = ancestor.parent()) {
            if (ancestor == child) {
                throw new IllegalArgumentException("A directory cannot contain itself or an ancestor");
            }
        }
        if (children.stream().anyMatch(existing -> existing.getName().equals(child.getName()))) {
            throw new IllegalArgumentException("Duplicate sibling name: " + child.getName());
        }
        // Claim only after all checks, so a rejected change leaves both trees intact.
        child.attachTo(this);
        children.add(child);
    }

    @Override
    public List<Element> getChildren() {
        return List.copyOf(children);
    }

    @Override
    public void accept(Visitor visitor) {
        Objects.requireNonNull(visitor).visit(this);
    }
}
