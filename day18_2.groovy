import groovy.transform.CompileStatic
import groovy.transform.CompileDynamic
import java.util.function.Predicate
import static KeyInfo.*

//if feel like this is on the right track, but it needs a better and more rigorous design
//time to take a break and draw some picture and write some words.
@CompileStatic
class Part2 {
    final Maze maze
    final int goals
    final List<Location> initialLocations
    final int[] robotGoals
    
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
    
    Part2(Maze maze, List<Location> s) {
        this.maze = maze
        this.goals = fullMatch(maze)
        this.initialLocations = s
        this.robotGoals = [findRobotGoals(s[0]), findRobotGoals(s[1]),
                           findRobotGoals(s[2]), findRobotGoals(s[3])] as int[]
    }
    
    static class RobotHistory {
        final byte v, h
        final int keys

        RobotHistory(byte v, byte h, int keys) {
            this.v = v
            this.h = h
            this.keys = keys
        }

        @Override
        int hashCode() {
            return 31 * (31 * ((int) v) + ((int) h)) + keys
        }

        @Override
        boolean equals(Object o) {
            if(!(o instanceof RobotHistory)) {
                return false
            }

            RobotHistory rhs = (RobotHistory) o
            return (v == rhs.v && h == rhs.h && keys == rhs.keys)
        }
    }


    static class Phase {
        final byte[] positions
        final int keys
        final int stage

        Phase(byte[] positions, int keys, int stage) {
            this.positions = positions
            this.keys = keys
            this.stage = stage
        }

        @Override
        int hashCode() {
            return 31 * Arrays.hashCode(positions) + keys
        }

        @Override
        boolean equals(Object o) {
            if(!(o instanceof Phase)) {
                return false
            }

            Phase rhs = (Phase) o
            return Arrays.equals(positions, rhs.positions) && keys == rhs.keys
        }

        @Override
        String toString() {
            return "${Arrays.toString(positions)}, ${KeyInfo.toString(keys)}"
        }

        boolean canMove(Cell cell) {
            return (cell.passable || (cell.door && canOpen(keys, cell)))
        }

        byte next(byte b) {
            return b + ((byte) 1)
        }

        byte previous(byte b) {
            return b - ((byte) 1)
        }

        int addPhase(Maze maze, int robot, byte v, byte h, Set<RobotHistory> explored) {
            Cell cell = maze.at(v, h)
            if(canMove(cell)) {
                int newKeys = keys | (cell.goal ? toKey(cell) : 0)
                RobotHistory rh = new RobotHistory(v, h, newKeys)
                if(explored.add(rh)) {
                    byte[] newPositions = Arrays.copyOf(positions, positions.length)
                    newPositions[2 * robot] = v
                    newPositions[2 * robot + 1] = h
                    println "In stage ${stage}, moving to ($v,$h), cell is: <${cell}>"
                    return new Phase(newPositions, newKeys, stage + 1)
                }
            }

            return null;
        }

        void addPhase(List<Phase> list, Phase p) {
            if(p != null)
                list.add(p)
        }

        //if we are here, then the keys did not match, generate all history and successors
        List<Phase> successors(Part2 p2, Set<RobotHistory> explored) {
            Maze maze = p2.maze
            List<Phase> ret = []
            
            for(int robot = 0; robot < 4; ++robot) {
                //see if there are still keys to find in this quadrant
                if(p2.robotGoals[robot] != (p2.robotGoals[robot] & keys)) {
                    byte v = positions[2 * robot]
                    byte h = positions[2 * robot + 1]
                    
                    addPhase(maze, robot, previous(v), h, explored)) //up
                    addPhase(maze, robot, next(v), h, explored)) //down
                    addPhase(maze, robot, v, previous(h), explored)) //left
                    addPhase(maze, robot, v, next(h), explored)) //right
                }
            }

            return ret
        }

        int whoMoved(Phase rhs) {
            for(int robot = 0; robot < 4; ++robot) {
                if((positions[robot * 2] != rhs.positions[robot * 2]) ||
                   (positions[robot * 2 + 1] != rhs.positions[robot * 2 + 1]))
                    return robot;
            }

            throw new IllegalStateException("nobody moved!!!")
        }
    }

    int nextRobot(int lastRobot) {
        return (lastRobot == 3) ? 0 : (lastRobot + 1)
    }

    SearchNode<Phase> search() {
        Phase initialPhase = new Phase([initialLocations[0].v, initialLocations[0].h,
                                        initialLocations[1].v, initialLocations[1].h,
                                        initialLocations[2].v, initialLocations[2].h,
                                        initialLocations[3].v, initialLocations[3].h] as byte[], 0, 0)
        List<RobotHistory> initialHistory = (0..3).collect { int robot ->
            return new RobotHistory(initialPhase.positions[2 * robot], initialPhase.positions[2 * robot + 1], 0)
        }

        Set<RobotHistory> explored = new HashSet<>(initialHistory)
        Deque<SearchNode<Phase>> frontier = new ArrayDeque<>()
        frontier.offer(SearchNode.create(initialPhase))
        Phase previous = initialPhase
        
        while(!frontier.isEmpty()) {
            SearchNode<Phase> current = frontier.poll()
            assert previous.stage <= current.state.stage
            if(current.state.keys == goals) {
                return current
            }

            previous = current.state
            for(Phase phase : current.state.successors(this, explored))
                frontier.offer(SearchNode.create(phase, current))
        }
        
        return null;
    }
}

def shortestPath = { String str ->
    def m = Maze.parse(str.split('\n') as List, Storage.SPARSE).deadEndFill()
    def locations = m.whereAreAll(Cell.parse('@'))
    def p2 = new Part2(m, locations)
    def nodes = p2.search()
    List path = nodes.toPath()
    //path.eachWithIndex { p, i ->  println "${i}: ${p}" }
    return path.size() - 1
}

/*String one_2 = """
#######
#a.#Cd#
##@#@##
#######
##@#@##
#cB#Ab#
#######
""".trim()

assert shortestPath(one_2) == 8
 */

/*String two_2 = """
###############
#d.ABC.#.....a#
######@#@######
###############
######@#@######
#b.....#.....c#
###############
""".trim()

assert shortestPath(two_2) == 24
 */

/*String three_2 = """
#############
#DcBa.#.GhKl#
#.###@#@#I###
#e#d#####j#k#
###C#@#@###J#
#fEbA.#.FgHi#
#############
""".trim()

assert shortestPath(three_2) == 32
*/

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

println shortestPath(four_2)

//println shortestPath(new File("18_2").text)
