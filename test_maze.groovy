import static Maze.*
import static Storage.*
import static Cell.*

String str1 = """
##########
#@......X#
##########
""".trim();

def maze1 = parse(str1.split('\n') as List, GRID);
assert maze1.loc(0,0) == WALL
assert maze1.loc(1,1) == START
assert maze1.loc(1,8) == GOAL
assert maze1.loc(2,9) == WALL
assert maze1.loc(1,7) == EMPTY

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

def maze2 = parse(str2.split('\n') as List, GRID)
assert maze2.vertical == 13
assert maze2.horizontal == 20
assert maze2.start == new Location(1,1)
