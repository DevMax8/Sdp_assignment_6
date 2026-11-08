import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/** Owns traversal, context and reset; visitors perform independent operations. */
public final class FileSystemWalker {
    private record Step(Element element, String path, int depth) { }

    public void walk(Element root, Visitor visitor) {
        Objects.requireNonNull(root, "Root cannot be null");
        Objects.requireNonNull(visitor, "Visitor cannot be null");
        visitor.reset();

        Deque<Step> pending = new ArrayDeque<>();
        // The path of a chosen file root relative to itself is '.'.
        pending.push(new Step(root, ".", 0));
        while (!pending.isEmpty()) {
            Step step = pending.pop();
            visitor.setContext(step.path(), step.depth());
            step.element().accept(visitor);
            enqueueChildren(step, pending);
        }
    }

    private void enqueueChildren(Step parent, Deque<Step> pending) {
        List<Element> children = parent.element().getChildren();
        for (int index = children.size() - 1; index >= 0; index--) {
            Element child = children.get(index);
            String path = parent.depth() == 0
                    ? child.getName()
                    : parent.path() + "/" + child.getName();
            pending.push(new Step(child, path, parent.depth() + 1));
        }
    }
}
