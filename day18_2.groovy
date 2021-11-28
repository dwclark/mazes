import groovy.transform.CompileStatic
import java.util.function.Predicate

@CompileStatic
class SPath implements Successors<Location>, Predicate<Location> {

    final Set<Cell> goals
    final Set<Cell> found
    final Maze maze
    Location lastFound;
    
    public SPath(Maze maze, Location start, Collection<Cell> goals) {
        this.maze = maze
        this.goals = Set.copyOf(goals)
        this.found = new HashSet<>();
        this.lastFound = start
    }

    public void addFound(Set<Cell> s) {
        found.addAll(s)
    }

    public boolean isFinished() {
        return found.containsAll(goals)
    }

    public void addIfPossible(List<Location> list, Location location) {
        Cell cell = maze[location]
        if(cell.passable || (cell.door && found.contains(Cell.parse(cell.id.toLowerCase()))))
            list.add(location)
    }
    
    public List<Location> successors(Location location) {
        List<Location> ret = []
        addIfPossible(ret, location.up())
        addIfPossible(ret, location.down())
        addIfPossible(ret, location.left())
        addIfPossible(ret, location.right())
        return ret
    }

    public boolean test(Location location) {
        Cell cell = maze[location]
        if(cell.goal && found.add(cell)) {
            lastFound = location
            return true
        }
        else {
            return false
        }
    }
}

@CompileStatic
class RestartingSearches {
    final List<SPath> spaths;
    final List<List<Location>> paths = []
    
    RestartingSearches(List<SPath> spaths) {
        this.spaths = spaths
    }

    boolean isFinished() {
        return spaths.every { s -> s.finished }
    }

    void search() {
        spaths.each { SPath spath ->
            if(!spath.finished) {
                SearchNode<Location> node = Search.breadthFirst(spath.lastFound, spath, spath)
                if(node != null) {
                    paths.add(node.toPath())
                    spaths.each { inner -> inner.addFound(spath.found) }
                }
            }
        }
    }

    int getSteps() {
        return (int) paths.sum { list -> list.size() - 1 }
    }

    void printPaths() {
        Maze m = spaths[0].maze
        List<String> strs = paths.collect { List<Location> path ->
            path.findAll { m[it].start || m[it].goal || m[it].door }.collect { m[it].id }.join("-")
        }

        strs.each { println it }
    }
}

def accumulate = { Maze m, Set stuff, Location loc ->
    Cell c = m[loc]
    if(c.goal) stuff.add(c)
    return false
}

def q2info = { String str ->
    def m = Maze.parse(str.split('\n') as List, Storage.SPARSE).deadEndFill()
    def locations = m.whereAreAll(Cell.parse('@'))
    /*locations.each { location ->
        Set stuff = new LinkedHashSet()
        Search.breadthFirst(location, accumulate.curry(m, stuff), m.&successors)
        println "@${location} -> ${stuff}"

        def spath = new SPath(m, stuff.findAll { it.goal })
        def p = Search.breadthFirst(encode(location.v, location.h, 0), spath, spath).toPath().collect { loc(decodeVertical(it), decodeHorizontal(it)) }
        println p.findAll { m[it].start || m[it].goal || m[it].door }.collect { m[it].id }.join("-")
    }*/

    List<SPath> spaths = locations.collect { Location location ->
        Set<Cell> goals = new HashSet<>()
        Search.breadthFirst(location, accumulate.curry(m, goals), m.&successors)
        return new SPath(m, location, goals)
    }

    RestartingSearches searches = new RestartingSearches(spaths)
    
    while(!searches.finished) {
        searches.search()
    }

    searches.printPaths()
    return searches.steps
    
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

println "one_2: "
println q2info(one_2)

String two_2 = """
###############
#d.ABC.#.....a#
######@#@######
###############
######@#@######
#b.....#.....c#
###############
""".trim()

println "two_2: "
println q2info(two_2)

String three_2 = """
#############
#DcBa.#.GhKl#
#.###@#@#I###
#e#d#####j#k#
###C#@#@###J#
#fEbA.#.FgHi#
#############
""".trim()

println "three_2: "
println q2info(three_2)

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

println "four_2: "
println q2info(four_2)

println "simplified2:"
println q2info(new File("18_2").text)

