import java.util.function.Predicate

class d18 {

    final String one = """
#########
#b.A.@.a#
#########
""".trim()

    final String two = """
########################
#f.D.E.e.C.b.A.@.a.B.c.#
######################.#
#d.....................#
########################
""".trim()

    final String three = """
########################
#...............b.C.D.f#
#.######################
#.....@.a.B.c.d.A.e.F.g#
########################
""".trim()

    final String four ="""
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

    final String five = """
########################
#@..............ac.GI.b#
###d#e#f################
###A#B#C################
###g#h#i################
########################
""".trim()

    private Maze _maze
    private Keys _allKeys

    public void setMaze(final Maze val) {
        _maze = val
        _allKeys = Keys.forMaze(_maze)
        edgeCache.clear()
    }

    public Maze getMaze() {
        return _maze
    }

    public Keys getAllKeys() {
        return _allKeys
    }

    public void allKeyPaths(Location start, Predicate<Path<KeyedCell>> whenFound) {
        Set<Location> visited = new HashSet<>()
        def inner
        inner = { Location location, Cell current, int pathLength, Keys required ->
            if(current.goal && pathLength != 0) {
                if(whenFound(new Path<>(new KeyedCell(current, required), pathLength))) {
                    return
                }
            }

            visited.add(location)
            location.eachNeighbor { Location neighbor ->
                Cell toVisit = maze[neighbor]
                if(!toVisit.permanentWall && !visited.contains(neighbor)) {
                    Keys nextKeys = required + (toVisit.door ? Keys.forDoor(toVisit) : required)
                    inner(neighbor, toVisit, pathLength + 1, nextKeys)
                }
            }

            visited.remove(location)
        }
        
        inner(start, maze[start], 0, Keys.NONE)
    }

    public void displayPaths(Location start) {
        Cell cell = maze[start]
        allKeyPaths(start) { Path<KeyedCell> p ->
            println "From ${cell} -> ${p.destination.cell} is ${p.length}; requires: ${p.destination.keys}"
            return false
        }
    }

    private final Map<Cell,Collection<Path<KeyedCell>>> edgeCache = new HashMap<>();

    public Collection<Path<KeyedCell>> edgesFrom(Cell cell, Keys currentKeys) {
        return edgeCache.computeIfAbsent(cell) {
            Map<Cell,Path<KeyedCell>> foundPaths = new HashMap<>(32)
            Location location = maze.whereIs(cell)
            allKeyPaths(location) { Path<KeyedCell> path ->
                Cell target = path.destination.cell
                if(!currentKeys.has(target)) {
                    def existing = foundPaths[target]
                    if(!existing || path.length < existing.length ||
                       path.destination.keys.properSubsetOf(existing.destination.keys)) {
                        foundPaths[target] = path
                    }

                    return true
                }
                else {
                    return false
                }
            }
            
            return foundPaths.values()
        }
    }

    void eachRelevantEdge(KeyedCells kcells, Closure toCall) {
        kcells.eachCell { int idx, Cell cell ->
            edgesFrom(cell, kcells.keys).each { Path<KeyedCell> edge ->
                if(edge.destination.available(kcells.keys) && !kcells.keys.has(edge.destination.cell)) {
                    toCall.call(idx, edge)
                }
            }
        }
    }

    int minimumPath() {
        PriorityQueue<Path<KeyedCells>> pending = new PriorityQueue<>()
        Collection<Cell> startingCells = maze.matchingCells { it.start }
        KeyedCells initialKCells = new KeyedCells(cells: List.copyOf(startingCells), keys: Keys.NONE)
        Set<KeyedCells> settled = new HashSet<>()
        pending.add(new Path<>(initialKCells, 0))

        while(true) {
            Path<KeyedCells> current = pending.poll()
            if(current == null) return -1

            KeyedCells kcells = current.destination
            if(kcells.keys == allKeys) {
                return current.length
            }

            settled.add(kcells)
            
            eachRelevantEdge(kcells) { int idx, Path<KeyedCell> edge ->
                KeyedCells neighbor = kcells.replace(idx, edge.destination.cell, kcells.keys + edge.destination.cell)
                if(!settled.contains(neighbor)) {
                    Path<KeyedCells> path = new Path<>(neighbor, current.length + edge.length)
                    println path
                    pending.add(path)
                }
            }
        }
    }
}
