package org.webcurator.core.reader;


import org.archive.util.ArchiveUtils;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 *
 * Log file access functions copied from the old Heritrix 1 library
 * originally written by Kristinn Sigurdsson
 *
 * @author Hanna Koppelaar (KB, National Library of the Netherlands)
 *
 */
public class Utils {

    /**
     * Implementation of a unix-like 'tail -n' command
     *
     * @param aFileName a file name String
     * @param n int number of lines to be returned
     * @return An array of two strings is returned. At index 0 the String
     *         representation of at most n last lines is located.
     *         At index 1 there is an informational string about how large a
     *         segment of the file is being returned.
     *         Null is returned if errors occur (file not found or io exception)
     */
    public static String[] tail(String aFileName, int n) {
        try {
            return tail(new RandomAccessFile(new File(aFileName),"r"),n);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Implementation of a unix-like 'tail -n' command
     *
     * @param raf a RandomAccessFile to tail
     * @param n int number of lines to be returned
     * @return An array of two strings is returned. At index 0 the String
     *         representation of at most n last lines is located.
     *         At index 1 there is an informational string about how large a
     *         segment of the file is being returned.
     *         Null is returned if errors occur (file not found or io exception)
     */
    public static String[] tail(RandomAccessFile raf, int n) {
        int BUFFERSIZE = 1024;
        long pos;
        long endPos;
        long lastPos;
        int numOfLines = 0;
        String info=null;
        byte[] buffer = new byte[BUFFERSIZE];
        StringBuffer sb = new StringBuffer();
        try {
            endPos = raf.length();
            lastPos = endPos;

            // Check for non-empty file
            // Check for newline at EOF
            if (endPos > 0) {
                byte[] oneByte = new byte[1];
                raf.seek(endPos - 1);
                raf.read(oneByte);
                if ((char) oneByte[0] != '\n') {
                    numOfLines++;
                }
            }

            do {
                // seek back BUFFERSIZE bytes
                // if length of the file if less then BUFFERSIZE start from BOF
                pos = 0;
                if ((lastPos - BUFFERSIZE) > 0) {
                    pos = lastPos - BUFFERSIZE;
                }
                raf.seek(pos);
                // If less then BUFFERSIZE avaliable read the remaining bytes
                if ((lastPos - pos) < BUFFERSIZE) {
                    int remainer = (int) (lastPos - pos);
                    buffer = new byte[remainer];
                }
                raf.readFully(buffer);
                // in the buffer seek back for newlines
                for (int i = buffer.length - 1; i >= 0; i--) {
                    if ((char) buffer[i] == '\n') {
                        numOfLines++;
                        // break if we have last n lines
                        if (numOfLines > n) {
                            pos += (i + 1);
                            break;
                        }
                    }
                }
                // reset last postion
                lastPos = pos;
            } while ((numOfLines <= n) && (pos != 0));

            // print last n line starting from last postion
            for (pos = lastPos; pos < endPos; pos += buffer.length) {
                raf.seek(pos);
                if ((endPos - pos) < BUFFERSIZE) {
                    int remainer = (int) (endPos - pos);
                    buffer = new byte[remainer];
                }
                raf.readFully(buffer);
                sb.append(new String(buffer));
            }

            info = buildDisplayingHeader(sb.length(), raf.length());
        } catch (FileNotFoundException e) {
            sb = null;
        } catch (IOException e) {
            e.printStackTrace();
            sb = null;
        } finally {
            try {
                if (raf != null) {
                    raf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(sb==null){
            return null;
        }
        String[] tmp = {sb.toString(),info};
        return tmp;
    }

    public static String buildDisplayingHeader(int len, long logsize)
    {
        double percent = 0.0;
        if (logsize != 0) {
            percent = ((double) len/logsize) * 100;
        }
        return "Displaying: " + ArchiveUtils.doubleToString(percent,1) +
                "% of " + ArchiveUtils.formatBytesForDisplay(logsize);
    }

    /**
     * Gets a portion of a log file. Starting at a given line number and the n-1
     * lines following that one or until the end of the log if that is reached
     * first.
     *
     * @param aFileName The filename of the log/file
     * @param lineNumber The number of the first line to get (if larger then the
     *                   file an empty string will be returned)
     * @param n How many lines to return (total, including the one indicated by
     *                   lineNumber). If smaller then 1 then an empty string
     *                   will be returned.
     *
     * @return An array of two strings is returned. At index 0 a portion of the
     *         file starting at lineNumber and reaching lineNumber+n is located.
     *         At index 1 there is an informational string about how large a
     *         segment of the file is being returned.
     *         Null is returned if errors occur (file not found or io exception)
     */
    public static String[] get(String aFileName, int lineNumber, int n)
    {
        File f = new File(aFileName);
        long logsize = f.length();
        try {
            return get(new FileReader(aFileName),lineNumber,n,logsize);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets a portion of a log file. Starting at a given line number and the n-1
     * lines following that one or until the end of the log if that is reached
     * first.
     *
     * @param reader source to scan for lines
     * @param lineNumber The number of the first line to get (if larger then the
     *                   file an empty string will be returned)
     * @param n How many lines to return (total, including the one indicated by
     *                   lineNumber). If smaller then 1 then an empty string
     *                   will be returned.
     *
     * @param logsize total size of source
     * @return An array of two strings is returned. At index 0 a portion of the
     *         file starting at lineNumber and reaching lineNumber+n is located.
     *         At index 1 there is an informational string about how large a
     *         segment of the file is being returned.
     *         Null is returned if errors occur (file not found or io exception)
     */
    public static String[] get(InputStreamReader reader,
                               int lineNumber,
                               int n,
                               long logsize)
    {
        StringBuffer ret = new StringBuffer();
        String info = null;
        try{
            BufferedReader bf = new BufferedReader(reader, 8192);

            String line = null;
            int i=1;
            while ((line = bf.readLine()) != null) {
                if(i >= lineNumber && i < (lineNumber+n))
                {
                    ret.append(line);
                    ret.append('\n');
                } else if( i >= (lineNumber+n)){
                    break;
                }
                i++;
            }
            info = buildDisplayingHeader(ret.length(), logsize);
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
        String[] tmp = {ret.toString(),info};
        return tmp;
    }

    /**
     * Returns all lines in a log/file matching a given regular expression.
     * Possible to get lines immediately following the matched line.  Also
     * possible to have each line prepended by it's line number.
     *
     * @param aFileName The filename of the log/file
     * @param regExpr The regular expression that is to be used
     * @param addLines Any lines following a match that <b>begin</b> with this
     *                 string will also be included. We will stop including new
     *                 lines once we hit the first that does not match.
     * @param prependLineNumbers If true, then each line will be prepended by
     *                           it's line number in the file.
     * @param skipFirstMatches The first number of matches up to this value will
     *                         be skipped over.
     * @param numberOfMatches Once past matches that are to be skipped this many
     *                        matches will be added to the return value. A
     *                        value of 0 will cause all matching lines to be
     *                        included.
     * @return An array of two strings is returned. At index 0 tall lines in a
     *         log/file matching a given regular expression is located.
     *         At index 1 there is an informational string about how large a
     *         segment of the file is being returned.
     *         Null is returned if errors occur (file not found or io exception)
     *         If a PatternSyntaxException occurs, it's error message will be
     *         returned and the informational string will be empty (not null).
     */
    public static String[] getByRegExpr(String aFileName,
                                        String regExpr,
                                        String addLines,
                                        boolean prependLineNumbers,
                                        int skipFirstMatches,
                                        int numberOfMatches){
        try {
            File f = new File(aFileName);
            return getByRegExpr(
                    new FileReader(f),
                    regExpr,
                    addLines,
                    prependLineNumbers,
                    skipFirstMatches,
                    numberOfMatches,
                    f.length());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns all lines in a log/file matching a given regular expression.
     * Possible to get lines immediately following the matched line.  Also
     * possible to have each line prepended by it's line number.
     *
     * @param reader The reader of the log/file
     * @param regExpr The regular expression that is to be used
     * @param addLines Any lines following a match that <b>begin</b> with this
     *                 string will also be included. We will stop including new
     *                 lines once we hit the first that does not match.
     * @param prependLineNumbers If true, then each line will be prepended by
     *                           it's line number in the file.
     * @param skipFirstMatches The first number of matches up to this value will
     *                         be skipped over.
     * @param numberOfMatches Once past matches that are to be skipped this many
     *                        matches will be added to the return value. A
     *                        value of 0 will cause all matching lines to be
     *                        included.
     * @param logsize Size of the log in bytes
     * @return An array of two strings is returned. At index 0 tall lines in a
     *         log/file matching a given regular expression is located.
     *         At index 1 there is an informational string about how large a
     *         segment of the file is being returned.
     *         Null is returned if errors occur (file not found or io exception)
     *         If a PatternSyntaxException occurs, it's error message will be
     *         returned and the informational string will be empty (not null).
     */
    public static String[] getByRegExpr(InputStreamReader reader,
                                        String regExpr,
                                        String addLines,
                                        boolean prependLineNumbers,
                                        int skipFirstMatches,
                                        int numberOfMatches,
                                        long logsize) {
        StringBuffer ret = new StringBuffer();
        String info = "";
        try{
            Matcher m = Pattern.compile(regExpr).matcher("");
            BufferedReader bf = new BufferedReader(reader, 8192);

            String line = null;
            int i = 1;
            boolean doAdd = false;
            long linesMatched = 0;
            while ((line = bf.readLine()) != null) {
                m.reset(line);
                if(m.matches()){
                    // Found a match
                    if(numberOfMatches > 0 &&
                            linesMatched >= skipFirstMatches + numberOfMatches){
                        // Ok, we are done.
                        break;
                    }
                    linesMatched++;
                    if(linesMatched > skipFirstMatches){
                        if(prependLineNumbers){
                            ret.append(i);
                            ret.append(". ");
                        }
                        ret.append(line);
                        ret.append("\n");
                        doAdd = true;
                    }
                } else if(doAdd) {
                    if(line.indexOf(addLines)==0){
                        linesMatched++;
                        //Ok, line begins with 'addLines'
                        if(prependLineNumbers){
                            ret.append(i);
                            ret.append(". ");
                        }
                        ret.append(line);
                        ret.append("\n");
                    }else{
                        doAdd = false;
                    }
                }
                i++;
            }
            info = buildDisplayingHeader(ret.length(), logsize);
        }catch(FileNotFoundException e){
            return null;
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }catch(PatternSyntaxException e){
            ret = new StringBuffer(e.getMessage());
        }
        String[] tmp = {ret.toString(),info};
        return tmp;
    }

    /**
     * Return the line number of the first line in the
     * log/file that matches a given regular expression.
     *
     * @param reader The reader of the log/file
     * @param regExpr The regular expression that is to be used
     * @return The line number (counting from 1, not zero) of the first line
     *         that matches the given regular expression. -1 is returned if no
     *         line matches the regular expression. -1 also is returned if
     *         errors occur (file not found, io exception etc.)
     */
    public static int findFirstLineContaining(InputStreamReader reader,
                                              String regExpr)
    {
        Pattern p = Pattern.compile(regExpr);

        try{
            BufferedReader bf = new BufferedReader(reader, 8192);

            String line = null;
            int i = 1;
            while ((line = bf.readLine()) != null) {
                if(p.matcher(line).matches()){
                    // Found a match
                    return i;
                }
                i++;
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Return the line number of the first line in the
     * log/file that that begins with the given string.
     *
     * @param reader The reader of the log/file
     * @param prefix The prefix string to match
     * @return The line number (counting from 1, not zero) of the first line
     *         that matches the given regular expression. -1 is returned if no
     *         line matches the regular expression. -1 also is returned if
     *         errors occur (file not found, io exception etc.)
     */
    public static int findFirstLineBeginning(InputStreamReader reader,
                                             String prefix)
    {

        try{
            BufferedReader bf = new BufferedReader(reader, 8192);

            String line = null;
            int i = 1;
            while ((line = bf.readLine()) != null) {
                if(line.startsWith(prefix)){
                    // Found a match
                    return i;
                }
                i++;
            }
        } catch(IOException e){
            e.printStackTrace();
        }
        return -1;
    }

}
