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
class KeyGoal implements Predicate<Long> {
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

    public boolean test(Long o) {
        long val = o.longValue()
        Location location = KeySuccessors.decodeLocation(val)
        int keys = KeySuccessors.decodeKeys(val)
        Cell cell = maze[location]
        if(cell.goal) {
            int newKey = keys | KeyedLocation.toKey(cell)
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

    static Long encode(Location location, int keys) {
        long ret = (long) location.v
        ret |= ((long) location.h << 16)
        ret |= ((long) keys << 32)
        return ret
    }

    static Location decodeLocation(long val) {
        int vertical = (int) (val & 0xFFFF)
        int horizontal = (int) ((val >> 16) & 0xFFFF)
        return new Location(vertical, horizontal)
    }

    static int decodeKeys(long val) {
        return (val >> 32) & 0xFFFF_FFFF
    }

    static boolean canOpen(int keys, Cell door) {
        return (keys & (1 << ((door.id as char as int) - DOOR_FLOOR))) != 0
    }

    public void addIfPossible(List<Long> list, Location proposed, int keys) {
        Cell cell = maze[proposed]
        if(cell.passable || (cell.door && canOpen(keys, cell)))
            list.add(encode(proposed, keys))
    }
    
    public List<Long> call(Long current) {
        List<Long> ret = []
        long val = current.longValue()
        Location location = decodeLocation(val)
        Cell cell = maze[location]
        int keys = decodeKeys(val) | (cell.goal ? KeyedLocation.toKey(cell) : 0)
        addIfPossible(ret, location.up(), keys)
        addIfPossible(ret, location.down(), keys)
        addIfPossible(ret, location.left(), keys)
        addIfPossible(ret, location.right(), keys)
        return ret
    }
}

static int solution(String str) {
    def m = parse(str.split('\n') as List, SPARSE)
    def start = KeySuccessors.encode(m.whereIs(Cell.parse('@')), 0)
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
