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
}

