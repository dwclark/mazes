import groovy.transform.CompileStatic
import groovy.transform.Immutable

@Immutable @CompileStatic
class KeyedCell {
    Cell cell
    Keys keys

    public boolean available(Keys required) {
        return keys.subsetOf(required)
    }
}
