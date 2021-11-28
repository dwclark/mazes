import static Maze.*
import static Storage.*

def str = new File("18").text
def m = parse(str.split('\n') as List, SPARSE)
def simplified = m.deadEndFill()
new File("simplified") << simplified.toString()

def str2 = new File("18_2").text
def m2 = parse(str2.split('\n') as List, SPARSE)
def simplified2 = m2.deadEndFill()
new File("simplified2") << simplified2.toString()
