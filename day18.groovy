import static Maze.*
import static Storage.*
import static Search.*
import static Location.*
import static KeyInfo.*
import groovy.transform.CompileStatic
import java.util.function.Predicate

@CompileStatic
class Part1 implements Successors<Long>, Predicate<Long> {
    
    final int toMatch
    final Maze maze

    public Part1(Maze maze) {
        this.maze = maze
        this.toMatch = fullMatch(maze)
    }

    public void addIfPossible(List<Long> list, int vertical, int horizontal, int keys) {
        Cell cell = maze.at(vertical, horizontal)
        if(cell.passable || (cell.door && canOpen(keys, cell)))
            list.add(encode(vertical, horizontal, keys))
    }
    
    public List<Long> successors(Long current) {
        List<Long> ret = []
        long val = current.longValue()
        int v = decodeVertical(val)
        int h = decodeHorizontal(val)
        Cell cell = maze.at(v, h)
        int keys = decodeKeys(val) | (cell.goal ? toKey(cell) : 0)
        addIfPossible(ret, v+1, h, keys)
        addIfPossible(ret, v-1, h, keys)
        addIfPossible(ret, v, h-1, keys)
        addIfPossible(ret, v, h+1, keys)
        return ret
    }

    public boolean test(Long o) {
        long val = o.longValue()
        int keys = decodeKeys(val)
        int v = decodeVertical(val)
        int h = decodeHorizontal(val)
        Cell cell = maze.at(v, h)
        if(cell.goal) {
            int newKey = keys | toKey(cell)
            return newKey == toMatch
        }
        
        return false
    }
}

static int solution(String str) {
    def m = parse(str.split('\n') as List, SPARSE).deadEndFill()
    m = m.deadEndFill()
    def location = m.whereIs(Cell.parse('@'))
    def start = encode(location.v, location.h, 0)
    def successors = new Part1(m)
    def solution = breadthFirst(start, successors, successors)
    return solution.toPath().size() - 1
}

def one = """
#########
#b.A.@.a#
#########
""".trim()

//assert solution(one) == 8

def two = """
########################
#f.D.E.e.C.b.A.@.a.B.c.#
######################.#
#d.....................#
########################
""".trim()

//assert solution(two) == 86

def three = """
########################
#...............b.C.D.f#
#.######################
#.....@.a.B.c.d.A.e.F.g#
########################
""".trim()

//assert solution(three) == 132

def four ="""
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

//assert solution(four) == 136

def five = """
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
/*
@CompileStatic
class StateP2 {
    final int keys
    final long positions

    public static long initLocations(List<Location> initial) {
        long ret = 0L
        initial.eachWithIndex { Location loc, int idx ->
            ret |= (((long) loc.v) << (8 * (2*idx)))
            ret |= (((long) loc.h) << (8 * (2*idx + 1)))
        }

        return ret
    }
    
    StateP2(List<Location> initial) {
        this(0, initLocations(initial))
    }

    StateP2(int keys, long positions) {
        this.keys = keys
        this.positions = positions;
    }

    int vertical(int robot) { return 0xFF & (positions >>> (8 * (2*robot))) }
    int horizontal(int robot) { return 0xFF & (positions >>> (8 * (2*robot + 1))) }

    StateP2 newState(int keys, int robot, int v, int h) {
        long newPositions = 0L
        if(robot == 0) {
            newPositions = (0xFFFF_FFFF_FFFF_0000L & positions) | (0xFFL & (long) v) | ((0xFFL & (long) h) << 8)
        }
        else if(robot == 1) {
            newPositions = (0xFFFF_FFFF_0000_FFFFL & positions) | ((0xFFL & (long) v) << 16) | ((0xFFL & (long) h) << 24)
        }
        else if(robot == 2) {
            newPositions = (0xFFFF_0000_FFFF_FFFFL & positions) | ((0xFFL & (long) v) << 32) | ((0xFFL & (long) h) << 40)
        }
        else {
            newPositions = (0x0000_FFFF_FFFF_FFFFL & positions) | ((0xFFL & (long) v) << 48) | ((0xFFL & (long) h) << 56)
        }

        return new StateP2(keys, newPositions)
    }

    @Override int hashCode() {
        return 31 * keys + Long.hashCode(positions)
    }
    
    @Override boolean equals(Object o) {
        if(!(o instanceof StateP2)) return false
        
        StateP2 rhs = (StateP2) o
        return keys == rhs.keys && positions == rhs.positions
    }
}

@CompileStatic
class Part2 implements Successors<StateP2>, Predicate<StateP2> {
    final Maze maze
    final int toMatch
    int currentKeys;
    
    Part2(Maze maze) {
        this.maze = maze
        this.toMatch = KeyInfo.fullMatch(maze)
    }

    public void addIfPossible(List<StateP2> list, StateP2 current, int keys, int robot, int vertical, int horizontal) {
        Cell cell = maze.at(vertical, horizontal)
        if(cell.passable || (cell.door && KeyInfo.canOpen(keys, cell)))
            list.add(current.newState(keys, robot, vertical, horizontal))
    }


    private int keyState(StateP2 s) {
        int keys = s.keys
        for(int i = 0; i < 4; ++i) {
            Cell c = maze.at(s.vertical(i), s.horizontal(i))
            if(c.goal) keys |= KeyInfo.toKey(c)
        }

        if(currentKeys < keys) {
            currentKeys = keys
            println KeyInfo.toString(keys)
        }
        
        return keys
    }
    
    public List<StateP2> successors(StateP2 s) {
        List<StateP2> list = []
        int keys = keyState(s)
        for(int i = 0; i < 4; ++i) {
            int v = s.vertical(i)
            int h = s.horizontal(i)
            addIfPossible(list, s, keys, i, v+1, h)
            addIfPossible(list, s, keys, i, v-1, h)
            addIfPossible(list, s, keys, i, v, h+1)
            addIfPossible(list, s, keys, i, v, h-1)
        }

        return list
    }

    public boolean test(StateP2 s) {
        return keyState(s) == toMatch
    }
}

static int solutionp2(String str) {
    def m = parse(str.split('\n') as List, SPARSE).deadEndFill()
    def locations = m.whereAreAll(Cell.parse('@'))
    def start = new StateP2(locations)
    def successors = new Part2(m)
    def solution = breadthFirst(start, successors, successors)
    return solution.toPath().size() - 1
}

//part 2
String one_2 = """
#######
#a.#Cd#
##@#@##
#######
##@#@##
#cB#Ab#
#######
""".trim()

println solutionp2(one_2)

String two_2 = """
###############
#d.ABC.#.....a#
######@#@######
###############
######@#@######
#b.....#.....c#
###############
""".trim()

println solutionp2(two_2)

String three_2 = """
#############
#DcBa.#.GhKl#
#.###@#@#I###
#e#d#####j#k#
###C#@#@###J#
#fEbA.#.FgHi#
#############
""".trim()

println solutionp2(three_2)

String four_2 = """
#############
#g#f.D#..h#l#
#F###e#E###.#
#dCba@#@BcIJ#
#############
#nK.L@#@G...#
#M###N#H###.#
#o#m..#i#jk.#
#############
""".trim()

println solutionp2(four_2)

println solutionp2(new File("18_2").text)

*/
