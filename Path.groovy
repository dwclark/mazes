import groovy.transform.CompileStatic

@CompileStatic
class Path<T> implements Comparable<Path<T>> {
    final T destination
    final int length

    public Path(T destination, int length) {
        this.destination = destination
        this.length = length
    }

    public int compareTo(final Path<T> rhs) {
        return length <=> rhs.length
    }

    @Override
    public String toString() {
        return "-> ${destination} ${length}"
    }
}
