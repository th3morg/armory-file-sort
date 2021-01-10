package armory.file.sort

class App {
    static void main(String[] args) {
        new SortedLogPrinter(
                'src/test/resources',
                /(server-)\w+\.log/)
                .execute()
                .printResult()

    }
}
