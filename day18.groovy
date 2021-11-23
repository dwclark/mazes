import static Maze.*
import static Storage.*
import static Cell.*
import static Search.*
import static Location.*

//OK, I can solve smallish ones. Need to reduce number of bfs. Should be able to explore all paths
//from starting point, extract the paths to each goal, then branch using each of the goals as a
//new starting point
static void paths(Maze maze, int maxPath,
                  LinkedHashMap<String,List<Location>> path,
                  List<LinkedHashMap<String,List<Location>>> found) {

    if(path.size() == maxPath) {
        found.add(path);
        return
    }
    
    Map<Location,Cell> all = explore(maze.start, maze.&getAt, maze.&successors)
    Map<Location,Cell> newGoals = all.findAll { loc, cell -> cell.goal && loc != maze.start }

    newGoals.each { loc, cell ->
        Maze newMaze = maze.transform([:], maze.start, loc)
        Search.Node<Location> node = breadthFirst(newMaze.start, newMaze.&atGoal, newMaze.&successors)
        if(node != null) {
            String strKey = "${newMaze[newMaze.start].id},${newMaze[newMaze.goal].id}"
            LinkedHashMap<String,List<String>> newPath = path + [(strKey): node.toPath()]
            Location matchingDoor = newMaze.whereIs(Cell.parse(cell.id.toUpperCase()))
            Map subs = [(matchingDoor): EMPTY, (newMaze.start): EMPTY]
            paths(newMaze.transform(subs, newMaze.goal, NOWHERE), maxPath, newPath, found)
        }
    }
}

static List<LinkedHashMap<String,List<Location>>> fullPaths(String strMaze) {
    final Maze maze = parse(strMaze.split('\n') as List, SPARSE)
    final Location startLoc = maze.whereIs(Cell.parse('@'))
    final int maxPath = maze.matchingCells(Cell.&isGoal).size()
    final List<LinkedHashMap<String,List<Location>>> foundPaths = []
    paths(maze.transform([:], startLoc, NOWHERE), maxPath, [:], foundPaths)
    return foundPaths
}

static int pathLength(LinkedHashMap<String,List<Location>> path) {
    int ret = 0
    path.each { key, list -> ret += (list.size() - 1) }
    return ret
}

static int minLength(List<LinkedHashMap<String,List<Location>>> found) {
    int theMin = Integer.MAX_VALUE
    found.each { map ->
        int currentSize = pathLength(map)
        if(currentSize < theMin) theMin = currentSize
    }
        
    return theMin
}

String one = """
#########
#b.A.@.a#
#########
""".trim()

println minLength(fullPaths(one))

String two = """
########################
#f.D.E.e.C.b.A.@.a.B.c.#
######################.#
#d.....................#
########################
""".trim()

println minLength(fullPaths(two))

String three = """
########################
#...............b.C.D.f#
#.######################
#.....@.a.B.c.d.A.e.F.g#
########################
""".trim()

println minLength(fullPaths(three))

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

println minLength(fullPaths(four))

