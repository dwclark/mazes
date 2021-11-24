import static Maze.*
import static Storage.*
import static Cell.*
import static Search.*
import static Location.*

class Solution {
    LinkedHashMap<String,List<Location>> steps
    int count

    Solution(LinkedHashMap<String,List<Location>> steps, int count) {
        this.steps = steps
        this.count = count
    }
    Solution() { this([:], 0) }
    
    Solution(Solution rhs) { this(new LinkedHashMap<>(rhs.steps), rhs.count) }

    static Solution max() { return new Solution([:], Integer.MAX_VALUE) }

    Solution add(String key, List<Location> list) {
        return new Solution(steps + [ (key): list ], count + (list.size() - 1))
    }

    void replace(Solution rhs) {
        steps = rhs.steps
        count = rhs.count
    }

    boolean inRange(Solution rhs, List<Location> step) {
        return (count + (step.size() - 1)) < rhs.count
    }

    int getPathCount() {
        return steps.size()
    }

    @Override String toString() {
        return "count: ${count}, steps: ${steps.keySet()}"
    }
}

static boolean hasSubPath(Maze m, List<Location> locs) {
    for(int i = 1; i < (locs.size() - 1); ++i) {
        if(m[locs[i]].goal) {
            return true
        }
    }

    return false
}

static void paths(Maze maze, int maxPath, String startAt,
                  Solution current, Solution shortest) {

    if(current.pathCount == maxPath) {
        shortest.replace(current)
        println shortest
        return
    }
    
    List<Search.Node<Location>> all = breadthFirstAll(maze.start, { val -> maze[val].goal }, maze.&successors)
    if(!all) {
        return
    }

    //prune away paths with sub paths, sort to use heuristic of shortest first is better
    List<List<Location>> pruned = all.collect { n -> n.toPath() }
        .findAll { list -> !hasSubPath(maze, list) && current.inRange(shortest, list) }
        .sort { l1, l2 -> l1.size() <=> l2.size() }

    if(!pruned) {
        return
    }
    
    pruned.each { step ->
        Location newGoal = step.last()
        String newStartAt = maze[newGoal].id
        String strKey = "${startAt},${maze[newGoal].id}"
        Location openedDoor = maze.whereIs(Cell.parse(maze[newGoal].id.toUpperCase()))
        Map subs = [(openedDoor): EMPTY, (maze.start): EMPTY, (newGoal): EMPTY]
        Maze newMaze = maze.transform(subs, newGoal, NOWHERE)
        paths(newMaze, maxPath, newStartAt, current.add(strKey, step), shortest)
    }
}

static Solution findSolution(String strMaze) {
    final Maze maze = parse(strMaze.split('\n') as List, SPARSE)
    final Location startLoc = maze.whereIs(Cell.parse('@'))
    final int maxPath = maze.matchingCells(Cell.&isGoal).size()
    final Solution s = Solution.max()
    paths(maze.transform([:], startLoc, NOWHERE), maxPath, '@', new Solution(), s)
    return s
}

String one = """
#########
#b.A.@.a#
#########
""".trim()

println findSolution(one)

/*String two = """
########################
#f.D.E.e.C.b.A.@.a.B.c.#
######################.#
#d.....................#
########################
""".trim()

println findSolution(two)

String three = """
########################
#...............b.C.D.f#
#.######################
#.....@.a.B.c.d.A.e.F.g#
########################
""".trim()

println findSolution(three)*/

/*String four ="""
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

println findSolution(four)*/

/*String five = """
########################
#@..............ac.GI.b#
###d#e#f################
###A#B#C################
###g#h#i################
########################
""".trim()

println findSolution(five)
*/
