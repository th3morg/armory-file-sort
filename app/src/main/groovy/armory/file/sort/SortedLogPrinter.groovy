package armory.file.sort
import groovy.io.FileType
import java.text.SimpleDateFormat

class SortedLogPrinter {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
     * TODO - Error handling.
     */
    File combineFiles(File left, File right){
        //TODO - Remove println, leaving in tact for illustration of successful ordering
        println "Combining ${left.name} and ${right.name}"
        File newFile = new File("${left.getParent()}/processed-${++newFileCount}.log")
        String leftLine, rightLine
        //open the left file with buffer
        left.withReader {leftBuffer  ->
            //short circuit if left file is empty
             if(!(leftLine = leftBuffer.readLine())){
                 return right;
             }
            //open the right file with buffer
            right.withReader {rightBuffer ->
                //short circuit if right file is empty
                if(!(rightLine = rightBuffer.readLine())){
                    return left;
                }
                //since neither file is empty, create a new file with buffered output
                newFile.withWriter {newBuffer ->
                    // TODO - Could we use a PriorityQueue here to simplify this?
                    Date leftDate = parseISO8691FileDate(leftLine), rightDate = parseISO8691FileDate(rightLine)
                    boolean rightComplete = false, leftComplete = false
                    while(!(rightComplete && leftComplete)){
                        if(!leftComplete && (leftDate <= rightDate || rightComplete)){
                            newBuffer.writeLine(leftLine)
                            if(!(leftLine = leftBuffer.readLine())){
                                leftComplete = true;
                            } else {
                                leftDate = parseISO8691FileDate(leftLine)
                            }
                        } else {
                            newBuffer.writeLine(rightLine)
                            if(!(rightLine = rightBuffer.readLine())){
                                rightComplete = true;
                            } else {
                                rightDate = parseISO8691FileDate(rightLine)
                            }
                        }
                    }
                }
            }
        }
        //TODO - We may want to delete the processed files if we don't have reason to keep them, especially the newly generated ones

        return newFile
    }

    /**
     * Parse ISO 8691 Date Format 'yyyy-MM-ddTHH:mm:ssZ'
     * @param line
     * @return parsed Date
     */
    Date parseISO8691FileDate(String line){
        return simpleDateFormat.parse(line.substring(0, line.indexOf("Z")).replace('T', ' '))
    }
}
