import groovy.transform.CompileStatic
import groovy.transform.CompileDynamic
import java.util.function.Predicate
import static KeyInfo.*

@CompileStatic
class RobotPositions {
    static final int ROBOTS = 4
    final int keys
    final byte[] positions
    
    RobotPositions(List<Location> val) {
        this([val[0].v, val[0].h, val[1].v, val[1].h, val[2].v, val[2].h, val[3].v, val[3].h] as byte[], 0)
    }

    RobotPositions(final byte[] positions, final int keys) {
        this.positions = positions
        this.keys = keys
    }

    @Override
    int hashCode() {
        return 31 * Arrays.hashCode(positions) + keys
    }

    @Override
    boolean equals(Object o) {
        if(!(o instanceof RobotPositions))
            return false

        RobotPositions rhs = (RobotPositions) o
        return Arrays.equals(positions, rhs.positions) && keys == rhs.keys
    }

    @Override
    String toString() {
        return "${Arrays.toString(positions)}, ${KeyInfo.toString(keys)}"
    }

    boolean canMove(Cell cell) {
        return (cell.passable || (cell.door && canOpen(keys, cell)))
    }

    void addIfPossible(List<RobotPositions> list, Maze maze, int robot, byte v, byte h) {
        Cell cell = maze.at(v,h)
        if(canMove(cell)) {
            int newKeys = keys | (cell.goal ? toKey(cell) : 0)
            byte[] newPositions = Arrays.copyOf(positions, positions.length)
            newPositions[2 * robot] = v
            newPositions[2 * robot + 1] = h
            list.add(new RobotPositions(newPositions, newKeys))
        }
    }

    List<RobotPositions> successors(Maze maze, int[] robotGoals) {
        List<RobotPositions> ret = []
        for(int robot = 0; robot < ROBOTS; ++robot) {
            if(robotGoals[robot] != (robotGoals[robot] & keys)) {
                byte v = positions[2 * robot]
                byte h = positions[2 * robot + 1]
                addIfPossible(ret, maze, robot, (byte) (v-1), h) //up
                addIfPossible(ret, maze, robot, (byte) (v+1), h) //down
                addIfPossible(ret, maze, robot, v, (byte) (h-1)) //left
                addIfPossible(ret, maze, robot, v, (byte) (h+1)) //right
            }
        }

        return ret
    }
}

@CompileStatic
class Part2 implements Predicate<RobotPositions>, Successors<RobotPositions> {

    final Maze maze
    final int goals
    final int[] robotGoals
    final RobotPositions initial

    @CompileDynamic
    int findRobotGoals(Location location) {
        Set<Cell> found = new HashSet()
        def pred = { Location theLoc ->
            if(maze[theLoc].goal) found.add(maze[theLoc])
            return false
        }
        
        Search.breadthFirst(location, pred, maze.&successors)
        return toKeys(found)
    }

    Part2(Maze maze, List<Location> locations) {
        this.maze = maze
        this.goals = fullMatch(maze)
        this.robotGoals = [findRobotGoals(locations[0]), findRobotGoals(locations[1]),
                           findRobotGoals(locations[2]), findRobotGoals(locations[3])] as int[]
        this.initial = new RobotPositions(locations)
    }

    boolean test(RobotPositions rp) {
        return goals == rp.keys
    }

    List<RobotPositions> successors(RobotPositions rp) {
        return rp.successors(maze, robotGoals)
    }
}

def shortestPath = { String str ->
    def m = Maze.parse(str.split('\n') as List, Storage.SPARSE).deadEndFill()
    def locations = m.whereAreAll(Cell.parse('@'))
    def p2 = new Part2(m, locations)
    return Search.breadthFirstCount(p2.initial, p2, p2)
    //List path = nodes.toPath()
    //path.eachWithIndex { p, i ->  println "${i}: ${p}" }
    //return path.size() - 1
}

String one_2 = """
#######
#a.#Cd#
##@#@##
#######
##@#@##
#cB#Ab#
#######
""".trim()

assert shortestPath(one_2) == 8

String two_2 = """
###############
#d.ABC.#.....a#
######@#@######
###############
######@#@######
#b.....#.....c#
###############
""".trim()

assert shortestPath(two_2) == 24

String three_2 = """
#############
#DcBa.#.GhKl#
#.###@#@#I###
#e#d#####j#k#
###C#@#@###J#
#fEbA.#.FgHi#
#############
""".trim()

assert shortestPath(three_2) == 32

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

assert shortestPath(four_2) == 72

println shortestPath(new File("18_2").text)
