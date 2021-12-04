import groovy.transform.CompileStatic;
import groovy.transform.KnownImmutable

@KnownImmutable @CompileStatic
public class Cell {
    private static final Map<Character,Cell> cache = [:]
    
    static final Cell NONE = parse(' ')
    static final Cell EMPTY = parse('.')
    static final Cell PERMANENT_WALL = Cell.parse('#')

    static final Set<Character> DOORS = Set.of('A'..'Z' as Character[])
    static final Set<Character> PERMANENT_WALLS = Set.of('#' as Character)
    static final Set<Character> WALLS = Set.copyOf(DOORS + PERMANENT_WALLS)
    static final Set<Character> GOALS = Set.of('a'..'z' as Character[])
    static final Set<Character> PASSABLE = Set.copyOf(GOALS + Set.of(['.', '@'] as Character[]))
    static final Set<Character> START = Set.of(['@','2','3','4','5'] as Character[])
    
    public static Cell parse(String s) {
        return parse(s as char)
    }
    
    public static Cell parse(char c) {
        return cache.computeIfAbsent(c, { k -> return new Cell(c) })
    }
    
    public Cell(final char id) { this.id = id }
    @Override public boolean equals(Object o) { return (o instanceof Cell) && id == ((Cell) o).id }
    @Override public int hashCode() { return Character.valueOf(id).hashCode() }
    @Override public String toString() { return id as String; }
    
    final char id;
    
    public boolean isWall() { return WALLS.contains(id) }
    public boolean isPermanentWall() { return id == (char) '#' }
    public boolean isGoal() { return GOALS.contains(id) }
    public boolean isPassable() { return PASSABLE.contains(id) }
    public boolean isStart() { return START.contains(id) }
    public boolean isDoor() { return DOORS.contains(id) }
    public boolean isEmpty() { return id == (char) '.' }
}
