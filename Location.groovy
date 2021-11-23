class Location {
    public static final Location NOWHERE = new Location(-1,-1)
    public static final Location loc(int v, int h) { return new Location(v, h); }
    
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
}
