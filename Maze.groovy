import groovy.transform.Immutable;

abstract class Maze {
  
    abstract Cell loc(int vertical, int horizontal);
    abstract void loc(int vertical, int horiztonal, Cell c);
    abstract int getVertical();
    abstract int getHorizontal();

    static Grid parse(File file, Storage s) {
        return parse(file.text, s);
    }

    static Grid parse(List<String> list, Storage s) {
        final int horizontal = list.get(0).length();
        final int vertical = list.size();
        final Maze maze = new Grid(vertical, horizontal);
        for(int v = 0; v < vertical; ++v) {
            for(int h = 0; h < horizontal; ++h) {
                maze.loc(v, h, Cell.parse(list.get(v)[h]));
            }
        }

        return maze;
    }

    public String toString() {
        println "vertical: $vertical, horizontal: $horizontal"
        StringBuilder sb = new StringBuilder(vertical * horizontal + vertical)
        for(int v = 0; v < vertical; ++v) {
            for(int h = 0; h < horizontal; ++h) {
                sb.append(loc(v, h));
            }

            sb.append("\n");
        }

        return sb.toString();
    }

    @Immutable
    static class Location {
        int v, h;
    }

    static class Grid extends Maze {
        final Cell[][] theMaze;
        Location start;
        Location goal;
        public Grid(int vertical, int horizontal) {
            this.theMaze = new Cell[vertical][horizontal];
        }

        public Cell loc(int vertical, int horizontal) {
            return theMaze[vertical][horizontal];
        }

        public void loc(int vertical, int horizontal, Cell c) {
            if(c == Cell.START) start = new Location(vertical, horizontal);
            else if(c == Cell.GOAL) goal = new Location(vertical, horizontal);
            
            theMaze[vertical][horizontal] = c;
        }

        public int getVertical() { return theMaze.length; }
        public int getHorizontal() { return theMaze[0].length; }
    }
}
