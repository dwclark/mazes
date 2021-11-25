import groovy.transform.CompileStatic
import java.time.*

@CompileStatic
class Profile {

    static Duration time(Closure c) {
        Instant begin = Instant.now()
        c.call()
        return Duration.between(begin, Instant.now())
    }

    static void printTime(Closure c) {
        Duration d = time(c)
        println "Total time: ${d.seconds}s, ${d.nano}ns"
    }
}
