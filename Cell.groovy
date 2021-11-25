import groovy.transform.CompileStatic;

@CompileStatic
public class Cell {
    private static final Map<String,Cell> cache = [:]
    
    static final Cell NONE = parse('')
    static final Cell EMPTY = parse('.')
    static final Cell PERMANENT_WALL = Cell.parse('#')

    static final Set<String> DOORS = ('A'..'Z') as Set
    static final Set<String> PERMANENT_WALLS = Set.of('#')
    static final Set<String> WALLS = DOORS + PERMANENT_WALLS
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
    public boolean isPermanentWall() { return id == '#' }
    public boolean isGoal() { return GOALS.contains(id) }
    public boolean isPassable() { return PASSABLE.contains(id) }
    public boolean isStart() { return id == '@' }
    public boolean isDoor() { return DOORS.contains(id) }
}
