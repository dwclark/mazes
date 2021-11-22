import groovy.transform.Immutable

abstract class Maze {

    static Location newLoc(int vertical, int horizontal) { return new Location(vertical, horizontal); }

    abstract int getVertical()
    abstract int getHorizontal()
    abstract Cell loc(int vertical, int horizontal)
    abstract void loc(int vertical, int horiztonal, Cell c)

    Cell loc(Location val) { return loc(val.v, val.h) }
    Location start
    Location goal

    String toString() {
        StringBuilder sb = new StringBuilder(vertical * horizontal + vertical)
        for(int v = 0; v < vertical; ++v) {
            for(int h = 0; h < horizontal; ++h) {
                sb.append(loc(v, h).id)
            }

            sb.append("\n")
        }

        return sb.toString()
    }

    String toString(Set<Location> solution) {
        StringBuilder sb = new StringBuilder(vertical * horizontal + vertical)
        for(int v = 0; v < vertical; ++v) {
            for(int h = 0; h < horizontal; ++h) {
                Location location = newLoc(v, h)
                Cell tmp = loc(v, h);
                if(solution.contains(location) && !tmp.start && !tmp.goal) sb.append("*")
                else sb.append(tmp.id)
            }

            sb.append("\n")
        }

        return sb.toString()
    }

    List<Location> successors(Location val) {
        return successors(val.v, val.h)
    }

    List<Location> successors(int v, int h) {
        List<Location> ret = new ArrayList<>();
        if(v-1 != 0 && !loc(v-1, h).wall)
            ret << newLoc(v-1, h)
        if(v+1 != vertical && !loc(v+1, h).wall)
            ret << newLoc(v+1, h)
        if(h-1 != 0 && !loc(v, h-1).wall)
            ret << newLoc(v, h-1)
        if(h+1 != horizontal && !loc(v, h+1).wall)
            ret << newLoc(v, h+1)
        return ret;
    }

    float euclidean(int v, int h) {
        int hdist = goal.h - h
        int vdist = goal.v - v
        return Math.sqrt(hdist**2 + vdist**2)
    }

    float manhattan(int v, int h) {
        int hdist = Math.abs(goal.h - h)
        int vdist = Math.abs(goal.v - v)
        return hdist + vdist
    }

    static Maze parse(File file, Storage s) {
        return parse(file.text, s)
    }

    static Maze parse(List<String> list, Storage s) {
        final int horizontal = list.get(0).length()
        final int vertical = list.size()
        final Maze maze = (s == Storage.GRID) ? new Grid(vertical, horizontal) : new MazeMap(vertical, horizontal);
        for(int v = 0; v < vertical; ++v) {
            for(int h = 0; h < horizontal; ++h) {
                maze.loc(v, h, Cell.parse(list.get(v)[h]))
            }
        }

        return maze
    }

    @Immutable
    static class Location {
        int v
        int h
    }

    static class Grid extends Maze {
        final Cell[][] theMaze
        Grid(int v, int h) {
            this.theMaze = new Cell[v][h]
        }

        Cell loc(int v, int h) {
            return theMaze[v][h]
        }

        void loc(int v, int h, Cell c) {
            if(c.start) start = newLoc(v, h)
            else if(c.goal) goal = newLoc(v, h)
            
            theMaze[v][h] = c
        }

        int getVertical() { return theMaze.length }
        int getHorizontal() { return theMaze[0].length }
    }

    static class MazeMap extends Maze {
        final Map<Location,Cell> theMaze
        final int vertical
        final int horizontal
        
        private MazeMap(final int v, final int h) {
            this.vertical = v;
            this.horizontal = h;
            theMaze = new HashMap(v * h);
        }
        
        Cell loc(int v, int h) {
            return theMaze[newLoc(v, h)]
        }

        void loc(int v, int h, Cell c) {
            def location = newLoc(v, h)
            if(c.start) start = location
            else if(c.goal) goal = location
            theMaze[location] = c
        }
    }
}
