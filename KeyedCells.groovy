import groovy.transform.CompileStatic
import groovy.transform.Immutable

@Immutable @CompileStatic
class KeyedCells {
    List<Cell> cells
    Keys keys
    
    void eachCell(Closure c) {
        cells.eachWithIndex { cell, idx -> c.call(idx, cell) }
    }
    
    KeyedCells replace(int idx, Cell newCell, Keys newKeys) {
        if(idx == -1) throw new IllegalArgumentException("no matching cell")
        List<Cell> newCells = new ArrayList<>(cells)
        newCells[idx] = newCell
        return new KeyedCells(newCells, newKeys)
    }
}
