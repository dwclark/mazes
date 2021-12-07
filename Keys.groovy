import groovy.transform.CompileStatic
import groovy.transform.KnownImmutable

@KnownImmutable @CompileStatic
class Keys {
    static final Range KEY_RANGE = ('a'..'z')
    static final Range DOOR_RANGE = ('A'..'Z')

    static final int KEY_FLOOR = KEY_RANGE[0] as char as int
    static final int DOOR_FLOOR = DOOR_RANGE[0] as char as int

    static final Keys ALL = new Keys(KEY_RANGE)
    static final Keys NONE = new Keys()
    
    final int keys

    public Keys() { this(0) }
    
    public Keys(int val) {
        this.keys = val
    }

    public Keys(String s) {
        int accum = 0
        for(char c in s)
            accum |= toBit(c)
        this.keys = accum
    }

    public Keys(Range range) {
        int accum = 0
        for(String s in range)
            accum |= toBit(s as char)
        this.keys = accum
    }

    private static int toBit(char c) {
        return (1 << ((c as int) - KEY_FLOOR))
    }

    public Keys plus(String key) {
        if(key.length() == 1) {
            assert key in KEY_RANGE
            int bit = toBit(key as char)
            return (bit == (bit & keys)) ? this : new Keys(keys | bit)
        }
        else {
            int accum = 0
            for(char c in key) {
                assert c in KEY_RANGE
                accum |= toBit(c)
            }

            return (accum == keys) ? this : new Keys(accum | keys)
        }
    }

    public Keys plus(Keys rhs) {
        return (keys == rhs.keys) ? this : new Keys(keys | rhs.keys)
    }

    public Keys plus(Cell cell) {
        int bit = toBit(cell.id)
        return (bit == (bit & keys)) ? this : new Keys(keys | bit);
    }

    public static Keys forDoor(char c) {
        assert c in DOOR_RANGE
        return new Keys(toBit(Character.toLowerCase(c)))
    }
    
    public static Keys forDoor(Cell c) {
        return forDoor(c.id)
    }

    public static Keys forMaze(Maze m) {
        String s = m.matchingCells { Cell c -> c.goal }.collect { it.id }.join('')
        return new Keys(s)
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder()
        for(String c in KEY_RANGE) {
            int combined = toBit(c as char) & keys
            if(combined != 0) {
                sb.append(c)
            }
        }

        return sb.toString()
    }

    @Override
    public boolean equals(final Object o) {
        if(!(o instanceof Keys)) {
            return false
        }

        Keys rhs = (Keys) o
        return keys == rhs.keys
    }

    @Override
    public int hashCode() { return keys }

    public boolean properSubsetOf(Keys rhs) {
        return ((keys & rhs.keys) != 0) && keys != rhs.keys
    }

    public boolean contains(Cell cell) {
        assert cell.goal
        return contains(cell.id)
    }

    public boolean contains(char c) {
        return (toBit(c) & keys) != 0
    }

    public boolean contains(Keys rhs) {
        return (keys & rhs.keys) == rhs.keys
    }
}
