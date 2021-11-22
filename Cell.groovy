import groovy.transform.Immutable;

public class Cell {
    static final Map<String,Cell> cache = [:]
    
    static final Set<String> WALLS = (('A'..'Z') as Set) + '#'
    static final Set<String> GOALS = ('a'..'z') as Set
    static final Set<String> PASSABLE = GOALS + '.' + '@'

    public static Cell parse(String s) {
        return cache.computeIfAbsent(s, { k -> return new Cell(k) })
    }
    
    public Cell(final String id) { this.id = id }
    @Override public boolean equals(Object o) { return (o instanceof Cell) && id == ((Cell) o).id }
    @Override public int hashCode() { return id.hashCode() }
    @Override public String toString() { return id; }
    
    final String id;
    
    public boolean isWall() { return WALLS.contains(id) }
    public boolean isGoal() { return GOALS.contains(id) }
    public boolean isPassable() { return PASSABLE.contains(id) }
    public boolean isStart() { return id == '@' }
}
