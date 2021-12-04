import java.util.function.Predicate
import java.util.function.BiPredicate
import groovy.transform.CompileStatic

@CompileStatic
abstract class Search {

    static <T> SearchNode<T> depthFirst(T initial, Predicate<T> goal, Successors<T> s) {
        Deque<SearchNode<T>> frontier = new ArrayDeque<>()
        frontier.push(SearchNode.create(initial))
        Set<T> explored = new HashSet<>([initial])

        while(!frontier.isEmpty()) {
            SearchNode<T> current = frontier.pop()
            if(goal.test(current.state))
                return current;

            for(T child : s.successors(current.state)) {
                if(!explored.contains(child)) {
                    explored.add(child);
                    frontier.push(SearchNode.create(child, current))
                }
            }
        }

        return null;
    }

    static <T> SearchNode<T> breadthFirst(T initial, Predicate<T> goal, Successors<T> s) {
        Deque<SearchNode<T>> frontier = new ArrayDeque<>()
        frontier.offer(SearchNode.create(initial))
        Set<T> explored = new HashSet<>([initial])
        
        while(!frontier.isEmpty()) {
            SearchNode<T> current = frontier.poll()
            if(goal.test(current.state))
                return current;

            for(T child : s.successors(current.state)) {
                if(!explored.contains(child)) {
                    explored.add(child);
                    frontier.offer(SearchNode.create(child, current))
                }
            }
        }

        return null;
    }

    static class CountNode<E> {
        E state
        int count

        private CountNode(E state) {
            this(state, 0)
        }

        private CountNode(E state, int count) {
            this.state = state
            this.count = count
        }

        CountNode<E> newCount(E newState) {
            return new CountNode<>(newState, count + 1)
        }

        static <E> CountNode<E> create(E initial) {
            return new CountNode(initial)
        }
    }

    static <T> int breadthFirstCount(T initial, Predicate<T> goal, Successors<T> s) {
        Deque<CountNode<T>> frontier = new ArrayDeque<>()
        frontier.offer(CountNode.create(initial))
        Set<T> explored = new HashSet<>([initial])
        
        while(!frontier.isEmpty()) {
            CountNode<T> current = frontier.poll()
            if(goal.test(current.state))
                return current.count
            
            for(T child : s.successors(current.state)) {
                if(!explored.contains(child)) {
                    explored.add(child);
                    frontier.offer(current.newCount(child))
                }
            }
        }

        return -1;
    }
}

