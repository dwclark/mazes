import static Maze.*
import static Storage.*
import static Search.*
import static Location.*
import groovy.transform.CompileStatic
import java.util.function.Predicate

@CompileStatic
class KeyedLocation {
    static final int KEY_FLOOR = 'a' as char as int
    
    final Location location
    final int keys
    
    KeyedLocation(int v, int h, int keys) {
        this(loc(v,h), keys)
    }

    KeyedLocation(Location location, int keys) {
        this.location = location
        this.keys = keys
    }
    
    @Override int hashCode() { return 31 * location.hashCode() + keys }

    @Override boolean equals(Object o) {
        if(!(o instanceof KeyedLocation)) {
            return false
        }
        
        final KeyedLocation rhs = (KeyedLocation) o
        return location == rhs.location && keys == rhs.keys
    }

    @Override String toString() { return "($location,$keys)" }

    static int toKey(Cell cell) {
        return 1 << ((cell.id[0] as char as int) - KEY_FLOOR);
    }

    static KeyedLocation from(Location location) {
        return new KeyedLocation(location, 0)
    }
}

@CompileStatic
class KeyGoal implements Predicate<KeyedLocation> {
    final int toMatch;
    final Maze maze;

    public KeyGoal(Maze maze) {
        this.maze = maze
        
        int tmp = 0;
        for(Cell cell in maze.matchingCells({ it.goal })) {
            tmp |= KeyedLocation.toKey(cell)
        }
        
        toMatch = tmp
    }

    public boolean test(KeyedLocation kl) {
        Cell cell = maze[kl.location]
        if(cell.goal) {
            int newKey = kl.keys | KeyedLocation.toKey(cell)
            return newKey == toMatch
        }

        return false
    }
}

@CompileStatic
class KeySuccessors {
    static final int DOOR_FLOOR = 'A' as char as int
    
    final Maze maze

    public KeySuccessors(Maze maze) {
        this.maze = maze
    }

    static boolean canOpen(int keys, Cell door) {
        return (keys & (1 << ((door.id as char as int) - DOOR_FLOOR))) != 0
    }

    public void addIfPossible(List<KeyedLocation> list, Location proposed, int keys) {
        Cell cell = maze[proposed]
        if(cell.passable || (cell.door && canOpen(keys, cell)))
            list.add(new KeyedLocation(proposed, keys))
    }
    
    public List<KeyedLocation> call(KeyedLocation current) {
        List<KeyedLocation> ret = []
        Cell cell = maze[current.location]
        int keys = current.keys | (cell.goal ? KeyedLocation.toKey(cell) : 0)
        addIfPossible(ret, current.location.up(), keys)
        addIfPossible(ret, current.location.down(), keys)
        addIfPossible(ret, current.location.left(), keys)
        addIfPossible(ret, current.location.right(), keys)
        return ret
    }
}

static int solution(String str) {
    def m = parse(str.split('\n') as List, SPARSE)
    def start = KeyedLocation.from(m.whereIs(Cell.parse('@')))
    def pred = new KeyGoal(m)
    def successors = new KeySuccessors(m)
    def solution = breadthFirst(start, pred, successors.&call)
    return solution.toPath().size() - 1
}

String one = """
#########
#b.A.@.a#
#########
""".trim()

assert solution(one) == 8

String two = """
########################
#f.D.E.e.C.b.A.@.a.B.c.#
######################.#
#d.....................#
########################
""".trim()

assert solution(two) == 86

String three = """
########################
#...............b.C.D.f#
#.######################
#.....@.a.B.c.d.A.e.F.g#
########################
""".trim()

assert solution(three) == 132

String four ="""
#################
#i.G..c...e..H.p#
########.########
#j.A..b...f..D.o#
########@########
#k.E..a...g..B.n#
########.########
#l.F..d...h..C.m#
#################
""".trim()

assert solution(four) == 136

String five = """
########################
#@..............ac.GI.b#
###d#e#f################
###A#B#C################
###g#h#i################
########################
""".trim()

assert solution(five) == 81

Profile.printTime {
    assert solution(new File("18").text) == 5068
}
