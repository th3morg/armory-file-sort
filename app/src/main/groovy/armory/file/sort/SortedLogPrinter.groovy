package armory.file.sort
import groovy.io.FileType

class SortedLogPrinter {
    private String dirName, fileNamePattern
    private PriorityQueue filesToPrint = new PriorityQueue<File>({a, b -> a.size() <=> b.size()})
    private List newFiles = []
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
        boolean processingComplete = false
        while(!processingComplete) {
            //We made a complete pass over the directory
            //swap the files to process with the set that were processed
            if(filesToPrint.size() == 0) {
                reload()
            }
            if(filesToPrint.size() == 1) {
                //if 1 file to process and zero files processed, we are done
                if (newFiles.size() == 0) {
                    processingComplete = true;
                    continue;
                }
                //Otherwise, there's nothing to combine this with, so push it to the newFiles list so it
                //can be combine on the next pass
                newFiles << filesToPrint.poll()
                reload()
                continue;
            }
            //if 2+ files to process, combine them into a new file
            newFiles.push(combineFiles(filesToPrint.poll(),filesToPrint.poll()))
         }
    }

    /**
     * Swap filesToCombine and processedFiles so that we can make another pass over the now smaller file set.
     *
     * TODO - We can constantly leverage priority queue, ensuring we actually always handle the smallest two files
     * TODO - even as the new files are being written, but just placing them into the queue. We don't need a second list.
     */
    void reload(){
        filesToPrint.addAll(newFiles)
        newFiles = [] //there may be a cleaner way to do than letting garbage collection take care of it
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
