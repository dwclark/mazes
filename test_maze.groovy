import static Maze.*
import static Storage.*
import static Cell.*
import static Search.*

String str1 = """
##########
#@......x#
##########
""".trim();

def mazes1 = [GRID,MAP].collect { parse(str1.split('\n') as List, it) }
mazes1.each { maze1 ->
    assert maze1.loc(0,0).wall
    assert maze1.loc(1,1).start
    assert maze1.loc(1,8).goal
    assert maze1.loc(2,9).wall
    assert maze1.loc(1,7).passable
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

def mazes2 = [GRID,MAP].collect { parse(str2.split('\n') as List, it) }
mazes2.each { maze2 ->
    assert maze2.vertical == 13
    assert maze2.horizontal == 20
    assert maze2.start == newLoc(1,1)
    assert maze2.goal == newLoc(11,18)

    assert maze2.successors(1,1) as Set == [newLoc(1,2), newLoc(2,1) ] as Set
    assert maze2.successors(2,2) as Set == [newLoc(1,2), newLoc(2,3),
                                            newLoc(3,2), newLoc(2,1)] as Set

    assert maze2.euclidean(1,1) - 19.72 < 0.01d
    assert maze2.manhattan(1,1) - 27 < 0.01d

    Search.Node dSolution = depthFirst(maze2.start, { val -> val == maze2.goal }, maze2.&successors)
    if(dSolution != null) {
        println "Depth First"
        println maze2.toString(dSolution.toPath() as Set)
    }

    Search.Node bSolution = breadthFirst(maze2.start, { val -> val == maze2.goal }, maze2.&successors)
    
    if(bSolution != null) {
        println "Breadth First"
        println maze2.toString(bSolution.toPath() as Set)
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

def mazes3 = [GRID,MAP].collect { parse(str3.split('\n') as List, it) }
mazes3.each { maze ->
    Search.Node dSolution = depthFirst(maze.start, { val -> val == maze.goal }, maze.&successors)
    if(dSolution != null) {
        println "Depth First"
        println maze.toString(dSolution.toPath() as Set)
    }

    Search.Node bSolution = breadthFirst(maze.start, { val -> val == maze.goal }, maze.&successors)
    
    if(bSolution != null) {
        println "Breadth First"
        println maze.toString(bSolution.toPath() as Set)
    }

    Map<Location,Cell> explored = explore(maze.start, { k -> maze.loc(k) }, maze.&successors)
    println "Full exploration"
    println maze.toString(explored.keySet())
}

