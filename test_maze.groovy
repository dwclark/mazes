import static Maze.*
import static Storage.*
import static Cell.*
import static Search.*
import static Location.*

String str1 = """
##########
#@......x#
##########
""".trim()

def mazes1 = [GRID,SPARSE].collect { parse(str1.split('\n') as List, it) }

mazes1.each { m ->
    assert m[loc(0,0)].wall
    assert m[loc(1,1)].start
    assert m[loc(1,8)].goal
    assert m[loc(2,9)].wall
    assert m[loc(1,7)].passable
    assert loc(1,1) == m.whereIs(Cell.parse('@'))

    def copy = m.transform([(loc(1,4)): Cell.parse('a'), (loc(1,6)): Cell.parse('A')], loc(1,8), loc(1,1))
    assert copy[loc(1,4)].goal
    assert m[loc(1,4)] == Cell.EMPTY
    assert copy[loc(1,6)].wall
    assert m[loc(1,6)] == Cell.EMPTY
    assert copy.start == loc(1,8)
    assert m.start == loc(1,1)
    assert copy.goal == loc(1,1)
    assert m.goal == loc(1,8)
}


String str2 = """
####################
#@.................#
#..................#
#..................#
#..................#
#..................#
#..................#
#..................#
#..................#
#..................#
#..................#
#.................x#
####################
""".trim()

def mazes2 = [GRID,SPARSE].collect { parse(str2.split('\n') as List, it) }
mazes2.each { m ->
    assert m.vertical == 13
    assert m.horizontal == 20
    assert m.start == loc(1,1)
    assert m.goal == loc(11,18)

    assert m.successors(loc(1,1)) as Set == [loc(1,2), loc(2,1) ] as Set
    assert m.successors(loc(2,2)) as Set == [loc(1,2), loc(2,3),
                                             loc(3,2), loc(2,1)] as Set

    assert m.euclidean(loc(1,1)) - 19.72 < 0.01d
    assert m.manhattan(loc(1,1)) - 27 < 0.01d

    Search.Node dSolution = depthFirst(m.start, { val -> val == m.goal }, m.&successors)
    if(dSolution != null) {
        println "Depth First"
        println m.toString(dSolution.toPath() as Set)
    }

    Search.Node bSolution = breadthFirst(m.start, { val -> val == m.goal }, m.&successors)
    
    if(bSolution != null) {
        println "Breadth First"
        println m.toString(bSolution.toPath() as Set)
    }
}

def str3 = """
##########
#.#...#x.#
#.#...#..#
#@#...#..#
#.#.#.#..#
#...#.#..#
###.#.#..#
#...#....#
#...#....#
##########
""".trim()

def mazes3 = [GRID,SPARSE].collect { parse(str3.split('\n') as List, it) }
mazes3.each { m ->
    Search.Node dSolution = depthFirst(m.start, { val -> val == m.goal }, m.&successors)
    if(dSolution != null) {
        println "Depth First"
        println m.toString(dSolution.toPath() as Set)
    }

    Search.Node bSolution = breadthFirst(m.start, { val -> val == m.goal }, m.&successors)
    
    if(bSolution != null) {
        println "Breadth First"
        println m.toString(bSolution.toPath() as Set)
    }

    List<Search.Node<Location>> explored = breadthFirstAll(m.start, { val -> m[val].goal }, m.&successors)
    println "Full exploration"
    println m.toString(explored[0].toPath() as Set)
}

