import groovy.transform.CompileStatic

@CompileStatic
class SearchNode<E> implements Comparable<SearchNode<E>> {
    final E state
    final SearchNode<E> parent
    final float cost
    final float heuristic
    
    static <E> SearchNode<E> create(E init) {
        return new SearchNode<>(init, null, 0.0f, 0.0f)
    }
    
    static <E> SearchNode<E> create(E state, SearchNode<E> parent) {
        return new SearchNode<>(state, parent, 0.0f, 0.0f)
    }
    
    private SearchNode(E state, SearchNode<E> parent, float cost, float heuristic) {
        this.state = state
        this.parent = parent
        this.cost = cost
        this.heuristic = heuristic
    }
    
        public List<E> toPath() {
        SearchNode<E> node = this
        List<E> ret = [node.state]
        while(node.parent != null) {
            node = node.parent
            ret << node.state
        }
        
        return ret.reverse()
    }
    
    public int compareTo(SearchNode<E> rhs) {
        return Float.compare(cost + heuristic, rhs.cost + rhs.heuristic)
    }
}
