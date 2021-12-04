import groovy.transform.CompileStatic
import groovy.transform.KnownImmutable
import static java.util.function.Function.identity

@KnownImmutable @CompileStatic
class Location implements Comparable<Location> {
    private static final Map<Location,Location> cache = new HashMap<>()
    
    public static final Location NOWHERE = new Location(-1,-1)

    public static final Location loc(int v, int h) {
        return cache.computeIfAbsent(new Location(v,h), identity())
    }
    
    final int v
    final int h
    
    Location(int v, int h) { this.v = v; this.h = h; }

    @Override int hashCode() { return 31 * h + v }

    @Override boolean equals(Object o) {
        if(!(o instanceof Location)) {
            return false;
        }
        
        final Location rhs = (Location) o
        return v == rhs.v && h == rhs.h
    }

    @Override String toString() { return "($v,$h)" }

    Location up() { return loc(v-1,h) }
    Location down() { return loc(v+1,h) }
    Location left() { return loc(v,h-1) }
    Location right() { return loc(v,h+1) }

    void eachNeighbor(Closure c) {
        c.call(up())
        c.call(left())
        c.call(down())
        c.call(right())
    }

    int compareTo(Location rhs) {
        int cmp = Integer.compare(v, rhs.v)
        if(cmp != 0) return cmp
        else return Integer.compare(h, rhs.h)
    }
}
