package armory.file.sort

import spock.lang.Specification

//TODO - Proper testing and additional cases
class SortedLogSpec extends Specification {
    def "reading files from dir ignores files which don't match the naming pattern"() {}

    def "combining two files ignores lines which don't conform to the specification"() {}

    def "combing two files results in a sorted file"() {}

    def "parsing a simple date is successful"() {
    given:
        def printer = new SortedLogPrinter("./", /(server-)\w+\.log/)

    when:
        def date = printer.parseISO8691FileDate("2020-01-12T19:00:01Z")

    then:
        date.toString() == "Sun Jan 12 19:00:01 EST 2020"
    }
}
