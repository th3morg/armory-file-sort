package armory.file.sort
import groovy.io.FileType

class SortedLogPrinter {
    private String dirName, fileNamePattern
    private PriorityQueue filesToPrint = new PriorityQueue<File>({a, b -> a.size() <=> b.size()})
    private int newFileCount = 0
    SortedLogPrinter(String dirName, String fileNamePattern){
        this.dirName = dirName
        this.fileNamePattern = fileNamePattern

    }
    /**
     * Sort all lines among files in a directory and print the result
     * @param dirName
     */
    void execute() {
        readFilesFromDir()
        combineFiles()
    }

    /**
     *
     * @param dir Directory containing log files to combine
     */
    void readFilesFromDir(){
        if(!this.dirName || !this.fileNamePattern) {
            throw new Exception("Directory and file name pattern must be provided.")
        }
        List<File> files = []
        def dir = new File(this.dirName)
        dir.eachFileRecurse (FileType.FILES) { file ->
            if(file.name.matches(this.fileNamePattern)){
                files << file
            }
        }
        //in cases where we have an odd number of files to process, it should benefit us to order from smallest to largest
        //because we'll avoid passing over the largest file
        filesToPrint.addAll(files)
    }

    /**
     * Process files by combining two at a time and storing the new files. Continue until one file with all lines remains.
     */
    void combineFiles(){
        while(filesToPrint.size() > 1) {
            //if 2+ files to process, combine them into a new file
            filesToPrint.add(combineFiles(filesToPrint.poll(),filesToPrint.poll()))
         }
    }

    /**
     * Combine two files line by line ensuring they remain in order by timestamp
     */
    File combineFiles(File left, File right){
        //TODO - Remove println
        println "Combining ${left.name} and ${right.name}"
        //open the left file with buffer
        //open the right file with buffer
        //create a new file with buffered output
        //read line from left and from right and parse dates from each (handling error conditions)
        //compare dates of the two lines
        //if there is no curr line for left or right, write the available line
        //note: it could be slightly more optimal to detect the above case and execute separate loop taking the lines without an if check
        //write the earlier occurring line to the new file and read the next line of that file

        return new File("${left.getParent()}/processed-${++newFileCount}.log")
    }
}
