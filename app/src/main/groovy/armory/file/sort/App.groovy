package armory.file.sort

class App {
    static void main(String[] args) {
        new SortedLogPrinter(
                '/Users/th3morg/git/armory-file-sort/app/src/test/resources',
                /(server-)\w+\.log/)
                .execute()
    }
}
