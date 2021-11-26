import java.util.function.Predicate
import java.util.function.BiPredicate
import groovy.transform.CompileStatic

@CompileStatic
abstract class Search {

    interface Successors<T> {
        List<T> successors(T val)
    }

    static class Node<E> implements Comparable<Node<E>> {
        final E state
        final Node<E> parent
        final float cost
        final float heuristic

        static <E> Node<E> create(E init) {
            return new Node<>(init, null, 0.0f, 0.0f)
        }

        static <E> Node<E> create(E state, Node<E> parent) {
            return new Node<>(state, parent, 0.0f, 0.0f)
        }

        private Node(E state, Node<E> parent, float cost, float heuristic) {
            this.state = state
            this.parent = parent
            this.cost = cost
            this.heuristic = heuristic
        }

        public List<E> toPath() {
            Node<E> node = this
            List<E> ret = [node.state]
            while(node.parent != null) {
                node = node.parent
                ret << node.state
            }

            return ret.reverse()
        }

        public int compareTo(Node<E> rhs) {
            return Float.compare(cost + heuristic, rhs.cost + rhs.heuristic)
        }
    }

    static <T> Node<T> depthFirst(T initial, Predicate<T> goal, Successors<T> s) {
        Deque<Node<T>> frontier = new ArrayDeque<>()
        frontier.push(Node.create(initial))
        Set<T> explored = new HashSet<>([initial])

        while(!frontier.isEmpty()) {
            Node<T> current = frontier.pop()
            if(goal.test(current.state))
                return current;

            for(T child : s.successors(current.state)) {
                if(!explored.contains(child)) {
                    explored.add(child);
                    frontier.push(Node.create(child, current))
                }
            }
        }

        return null;
    }

    static <T> Node<T> breadthFirst(T initial, Predicate<T> goal, Successors<T> s) {
        Deque<Node<T>> frontier = new ArrayDeque<>()
        frontier.offer(Node.create(initial))
        Set<T> explored = new HashSet<>([initial])
        
        while(!frontier.isEmpty()) {
            Node<T> current = frontier.poll()
            if(goal.test(current.state))
                return current;

            for(T child : s.successors(current.state)) {
                if(!explored.contains(child)) {
                    explored.add(child);
                    frontier.offer(Node.create(child, current))
                }
            }
        }

        return null;
    }
}

