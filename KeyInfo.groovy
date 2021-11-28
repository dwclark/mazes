import groovy.transform.CompileStatic

@CompileStatic
class KeyInfo {
    static final int KEY_FLOOR = 'a' as char as int
    static final int DOOR_FLOOR = 'A' as char as int
    
    static int toKey(Cell cell) {
        return 1 << ((cell.id[0] as char as int) - KEY_FLOOR);
    }

    static int toKeys(Collection<Cell> cell) {
        int ret = 0
        cell.each { Cell c -> ret |= (1 << ((c.id as char as int) - KEY_FLOOR)) }
        return ret;
    }

    static boolean canOpen(int keys, Cell door) {
        return (keys & (1 << ((door.id as char as int) - DOOR_FLOOR))) != 0
    }

    static int fullMatch(Maze maze) {
        int tmp = 0
        for(Cell cell in maze.matchingCells({ it.goal })) {
            tmp |= toKey(cell)
        }

        return tmp
    }

    static List<String> toList(int keys) {
        List<String> ret = []
        ('a'..'z').eachWithIndex { String c, int i ->
            if(((1 << i) & keys) != 0) {
                ret << c
            }
        }

        return ret
    }

    static String toString(int keys) {
        return toList(keys).join('')
    }

    static Long encode(int v, int h, int keys) {
        long ret = (long) v
        ret |= ((long) h << 16)
        ret |= ((long) keys << 32)
        return ret
    }

    static int decodeVertical(long val) {
        return (int) (val & 0xFFFF)
    }

    static int decodeHorizontal(long val) {
        return (int) ((val >> 16) & 0xFFFF)
    }

    static int decodeKeys(long val) {
        return (val >> 32) & 0xFFFF_FFFF
    }
}
