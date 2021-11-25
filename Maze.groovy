import groovy.transform.CompileStatic
import java.util.function.Predicate
import static Location.*
import static Storage.*
import static Cell.*

@CompileStatic
abstract class Maze {

    static Location newLoc(int vertical, int horizontal) { return new Location(vertical, horizontal); }

    abstract int getVertical()
    abstract int getHorizontal()
    abstract Location whereIs(Cell c);
    abstract Collection<Cell> matchingCells(Predicate<Cell> p)
    abstract Cell getAt(Location loc);
    abstract boolean wallAt(Location loc);
    abstract Maze transform(Map<Location,Cell> replace, Location start, Location goal)
    abstract Location getStart()
    abstract Location getGoal()

    boolean atGoal(Location loc) { return loc == goal }
    
    boolean inRange(Location val) {
        return (val.v >= 0) && (val.v < vertical) && (val.h >= 0) && (val.h < horizontal)
    }
    
    String toString() {
        StringBuilder sb = new StringBuilder(vertical * horizontal + vertical)
        for(int v = 0; v < vertical; ++v) {
            for(int h = 0; h < horizontal; ++h) {
                sb.append(this[loc(v, h)].id)
            }

            sb.append("\n")
        }

        return sb.toString()
    }

    String toString(Set<Location> solution) {
        StringBuilder sb = new StringBuilder(vertical * horizontal + vertical)
        for(int v = 0; v < vertical; ++v) {
            for(int h = 0; h < horizontal; ++h) {
                Location location = loc(v,h)
                Cell tmp = this[location]
                if(solution.contains(location) && location != start && location != goal) sb.append("*")
                else sb.append(tmp.id)
            }

            sb.append("\n")
        }

        return sb.toString()
    }

    void addIfValid(List<Location> list, Location val) {
        if(inRange(val) && !wallAt(val)) {
            list.add(val)
        }
    }
    
    List<Location> successors(Location val) {
        List<Location> ret = new ArrayList<>();
        addIfValid(ret, val.up())
        addIfValid(ret, val.down())
        addIfValid(ret, val.left())
        addIfValid(ret, val.right())
        return ret;
    }
    
    float euclidean(Location val) {
        int hdist = goal.h - val.h
        int vdist = goal.v - val.v
        return Math.sqrt((double) (hdist**2 + vdist**2))
    }
    
    float manhattan(Location val) {
        int hdist = Math.abs(goal.h - val.h)
        int vdist = Math.abs(goal.v - val.v)
        return hdist + vdist
    }
    
    static Maze parse(File file, Storage s) {
        return parse(file.readLines(), s)
    }

    static Map parse(List<String> strings) {
        def list = strings.collect { String str -> str.collect { String sub -> Cell.parse(sub) } }
        def start, goal;
        list.eachWithIndex { subList, v ->
            subList.eachWithIndex { cell, h ->
                if(cell.start && !start) start = loc(v,h)
                if(cell.goal && !goal) goal = loc(v,h) } }
        return [ cells: list, start: start, goal: goal ]
    }

    static Maze parse(List<String> list, Storage s) {
        Map p = parse(list)
        if (s == GRID)
            return new Grid(p.cells as List, p.start as Location, p.goal as Location)
        else
            return new Sparse(p.cells as List, p.start as Location, p.goal as Location)
    }

    static class Grid extends Maze {
        final Cell[][] theMaze
        final Location start
        final Location goal

        Grid(Cell[][] theMaze, Location start, Location goal) {
            this.theMaze = theMaze;
            this.start = start;
            this.goal = goal;
        }
        
        Grid(List<List<Cell>> cells, Location start, Location goal) {
            this(new Cell[cells.size()][cells[0].size()], start, goal)
            cells.eachWithIndex { row, v ->
                row.eachWithIndex { cell, h ->
                    theMaze[v][h] = cell } }
        }

        Grid transform(Map<Location,Cell> replace, Location start, Location goal) {
            Cell[][] copy = new Cell[theMaze.length][theMaze[0].length]
            for(int v = 0; v < theMaze.length; ++v) {
                for(int h = 0; h < theMaze[0].length; ++h) {
                    copy[v][h] = theMaze[v][h]
                }
            }

            replace.each { loc, cell -> copy[loc.v][loc.h] = cell }
            return new Grid(copy, start, goal)
        }

        Cell getAt(Location val) {
            if(inRange(val))
                return theMaze[val.v][val.h]
            else
                return NONE
        }

        boolean wallAt(Location val) {
            return theMaze[val.v][val.h].wall
        }

        Location whereIs(Cell c) {
            for(int v = 0; v < vertical; ++v) {
                for(int h = 0; h < horizontal; ++h) {
                    if(theMaze[v][h] == c) {
                        return loc(v, h)
                    }
                }
            }

            return NOWHERE
        }

        Collection<Cell> matchingCells(Predicate<Cell> p) {
            List<Cell> ret = []
            for(int v = 0; v < vertical; ++v) {
                for(int h = 0; h < horizontal; ++h) {
                    if(p.test(theMaze[v][h])) {
                        ret << theMaze[v][h]
                    }
                }
            }

            return ret
        }

        int getVertical() { return theMaze.length }
        int getHorizontal() { return theMaze[0].length }
    }

    static class Sparse extends Maze {
        final Map<Location,Cell> theMaze
        final Location start
        final Location goal
        final int vertical
        final int horizontal

        Sparse(Map<Location,Cell> theMaze, Location start, Location goal,
               int vertical, int horizontal) {
            this.theMaze = theMaze
            this.start = start
            this.goal = goal
            this.vertical = vertical
            this.horizontal = horizontal
        }
        
        Sparse(List<List<Cell>> cells, Location start, Location goal) {
            this([:], start, goal, cells.size(), cells[0].size())
            cells.eachWithIndex { row, v ->
                row.eachWithIndex { cell, h ->
                    if(!cell.permanentWall) theMaze[loc(v,h)] = cell } }
        }

        Sparse transform(Map<Location,Cell> replace, Location start, Location goal) {
            return new Sparse(theMaze + replace, start, goal, vertical, horizontal)
        }
        
        Cell getAt(Location val) {
            if(inRange(val))
                return theMaze[val] ?: PERMANENT_WALL
            else
                return NONE
        }

        boolean wallAt(Location val) {
            if(inRange(val))
                return (theMaze[val] ?: PERMANENT_WALL).wall
            else
                return false
        }

        Location whereIs(Cell c) {
            return theMaze.findResult { location, cell -> (cell == c) ? location: null } ?: NOWHERE
        }
        
        Collection<Cell> matchingCells(Predicate<Cell> p) {
            return theMaze.values().findAll { p.test(it) }
        }
    }
}
