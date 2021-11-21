import groovy.transform.Immutable;

@Immutable
public class Cell {
    public static final Cell WALL = new Cell('#');
    public static final Cell EMPTY = new Cell('.');
    public static final Cell START = new Cell('@');
    public static final Cell GOAL = new Cell('X')
    
    String id;
    
    @Override public String toString() { return id; }
    
    public static Cell parse(String s) {
        switch(s) {
            case '#': return WALL;
            case '.': return EMPTY;
            case '@': return START;
            case 'X': return GOAL;
        }
    }
}
