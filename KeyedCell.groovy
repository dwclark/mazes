import groovy.transform.CompileStatic
import groovy.transform.Immutable

@Immutable @CompileStatic
class KeyedCell {
    Cell cell
    Keys keys

    public boolean available(Keys keysInPossesion) {
        return keysInPossesion.contains(keys)
    }
}
