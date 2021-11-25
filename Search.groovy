import java.util.function.Predicate

abstract class Search {

    static class Node<T> implements Comparable<Node<T>> {
        final T state
        final T parent
        final float cost
        final float heuristic

        public Node(T state, T parent) {
            this(state, parent, 0.0f, 0.0f)
        }

        public Node(T state, T parent, float cost, float heuristic) {
            this.state = state
            this.parent = parent
            this.cost = cost
            this.heuristic = heuristic
        }

        public List<T> toPath() {
            Node<T> node = this
            List<T> ret = [node.state]
            while(node.parent != null) {
                node = node.parent
                ret << node.state
            }

            return ret.reverse()
        }

        public int compareTo(Node<T> rhs) {
            return Integer.compare(cost + heuristic, rhs.cost + rhs.heuristic)
        }
    }

    static <T> Node<T> depthFirst(T initial, Predicate<T> goal, Closure<List<T>> successors) {
        Deque<Node<T>> frontier = new ArrayDeque<>()
        frontier.push(new Node(initial, null))
        Set<T> explored = new HashSet<>(frontier)

        while(!frontier.isEmpty()) {
            Node<T> current = frontier.pop()
            if(goal.test(current.state))
                return current;

            successors(current.state).each { child ->
                if(!explored.contains(child)) {
                    explored.add(child);
                    frontier.push(new Node(child, current))
                }
            }
        }

        return null;
    }

    static <T> Node<T> breadthFirst(T initial, Predicate<T> goal, Closure<List<T>> successors) {
        Deque<Node<T>> frontier = new ArrayDeque<>()
        frontier.offer(new Node(initial, null))
        Set<T> explored = new HashSet<>(frontier)
        
        while(!frontier.isEmpty()) {
            Node<T> current = frontier.poll()
            if(goal.test(current.state))
                return current;

            successors(current.state).each { child ->
                if(!explored.contains(child)) {
                    explored.add(child);
                    frontier.offer(new Node(child, current))
                }
            }
        }

        return null;
    }
}

