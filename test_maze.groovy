import static Maze.*
import static Storage.*
import static Cell.*

String str1 = """
##########
#@......X#
##########
""".trim();

def mazes1 = [GRID,MAP].collect { parse(str1.split('\n') as List, it) }
mazes1.each { maze1 ->
    assert maze1.loc(0,0) == WALL
    assert maze1.loc(1,1) == START
    assert maze1.loc(1,8) == GOAL
    assert maze1.loc(2,9) == WALL
    assert maze1.loc(1,7) == EMPTY
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
#.................X#
####################
""".trim()

def mazes2 = [GRID,MAP].collect { parse(str2.split('\n') as List, it) }
mazes2.each { maze2 ->
    assert maze2.vertical == 13
    assert maze2.horizontal == 20
    assert maze2.start == new Location(1,1)
    assert maze2.goal == new Location(11,18)

    assert maze2.successors(1,1) as Set == [new Location(1,2), new Location(2,1) ] as Set
    assert maze2.successors(2,2) as Set == [new Location(1,2), new Location(2,3),
                                            new Location(3,2), new Location(2,1)] as Set

    assert maze2.euclidean(1,1) - 19.72 < 0.01d
    assert maze2.manhattan(1,1) - 27 < 0.01d
}

