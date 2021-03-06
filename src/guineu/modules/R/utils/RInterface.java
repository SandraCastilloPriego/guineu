/*
 * Copyright (c) 2003-2008 Fred Hutchinson Cancer Research Center
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guineu.modules.R.utils;

import java.io.*;
import java.util.*;
import org.apache.log4j.Logger;

public class RInterface {

    private static Logger _log = Logger.getLogger(RInterface.class);
    protected static int expressionNumber = 0;
    //maximum number of milliseconds to wait for a response from R before giving up
    protected static final int DEFAULT_MAX_R_WAIT_MILLIS = 5000;
    //number of milliseconds to sleep between checks for response from R
    protected static final int R_SLEEP_INCREMENT_MILLIS = 50;

    /**
     * Choose the right name of the command for running R for this OS
     * @return
     */
    protected static String getRCommandStringForOS() {
        String os = System.getProperty("os.name");
        String cmd;

        if (os.contains("Windows")) {
            cmd = "RCMD";
        } else {
            cmd = "R CMD";
        }
        return cmd;
    }

    /**
     * Run an R script, setting the R directory to be the temp dir.
     * Also mark the Rout file generated by R for deletion when tempfiles for the caller
     * are cleaned up (don't want to do it here because a user might want to view the Rout file).
     *
     * This is the easy and stable way to invoke R.  The alternative is to interact with
     * the input, output, and error streams of the process, which provides some advantages.
     * @param rScriptFile
     * @return true if successful, false otherwise
     */
    public static boolean runRScript(File rScriptFile, Object caller)
            throws RuntimeException {


        //assume failure
        boolean success = false;
        String rScriptFilepath = rScriptFile.getAbsolutePath();
        try {
            String cmd = getRCommandStringForOS();
            cmd = cmd + " BATCH --slave " + rScriptFilepath;
            _log.debug("Before runing R, script file " + rScriptFilepath);
            Process p = Runtime.getRuntime().exec(cmd, null, TempFileManager.getTmpDir());
            _log.debug("after running R");

            int err = p.waitFor();
            _log.debug("process returned, " + err);
            if (err == 0) {
                success = true;
            } else {
                TempFileManager.unmarkFileForDeletion(rScriptFile, caller);
                throw new RuntimeException("Error in executing R, temp dir is " + TempFileManager.getTmpDir() + ".  R process status: " + err, null);
            }

            //Only try to delete the out file if we successfully ran the script
            TempFileManager.markFileForDeletion(new File(rScriptFile.getAbsolutePath() + "out"), caller);

        } catch (Exception e) {
            throw new RuntimeException("Failed to run R code.  Details follow.\n"
                    + "Please make sure that R is a) installed and b) on your PATH.  To do this,\n"
                    + "open a command prompt window and type R (on Linux) or RCMD (on Windows)\n"
                    + "If the R command is not found, your PATH environment variable needs to be\n"
                    + "modified to contain the R binary directory.", e);
        }
        return success;
    }

    /**
     * First index in response indicates row.  Second indicates column.
     * @param rResponse
     * @return
     */
    public static float[][] processRMatrixResponse(String rResponse) {
        String[] textLines = rResponse.split("\n");
        int numRows = textLines.length - 1;

        String firstLine = textLines[0];
        int numCols = 0;
        int lastWhitespaceFirstLine = 0;
        while (Character.isWhitespace(firstLine.charAt(lastWhitespaceFirstLine))) {
            lastWhitespaceFirstLine++;
        }
        firstLine = firstLine.substring(lastWhitespaceFirstLine);
        String[] columnNames = firstLine.split("\\s+");
        numCols = columnNames.length;
        _log.debug("processRMatrixResponse, first line: " + firstLine + ", numCols=" + numCols);

        float[][] result = new float[numRows][numCols];
        for (int i = 0; i < numRows; i++) {
            String line = textLines[i + 1];
            line = line.substring(line.indexOf("]") + 1);
            int lastWhitespace = 0;
            while (Character.isWhitespace(line.charAt(lastWhitespace))) {
                lastWhitespace++;
            }
            line = line.substring(lastWhitespace);
            String[] entries = line.split("\\s+");

//            int numEmpties=0;
//            if (entries[0] == null || entries[0].length()==0)
//                numEmpties++;

            for (int j = 0; j < numCols; j++) {
                try {
                    if (entries[j].equals("NA")) {
                        result[i][j] = Float.NaN;
                    } else {
                        result[i][j] = Float.parseFloat(entries[j]);
                    }
                } catch (RuntimeException e) {
                    throw e;
                }
            }
        }

        return result;
    }

    /**
     * cute little method that handles a n R coefficients() response by ignoring the first line
     * and splitting the second around whitespace.  Brittle, don't give it anything at all funky
     * @param rResponse
     * @return
     */
    public static double[] processRCoefficientResponse(String rResponse)
            throws NumberFormatException {
        _log.debug("Parsing R response:\n***\n" + rResponse + "\n***\n");
        List<Float> resultList = new ArrayList<Float>();

        String justTheNumbers = rResponse;
        if (justTheNumbers.contains("\n")) {
            justTheNumbers = justTheNumbers.substring(rResponse.indexOf('\n'));
        }

        //R responses can get split over multiple lines.  In this case, that means
        //alternating lines of header, values
        String[] textLines = rResponse.split("\n");
        if (textLines.length > 2) {
            _log.debug("Multiple (" + textLines.length + ") lines in R response");
            justTheNumbers = textLines[1];
            for (int i = 3; i < textLines.length; i += 2) {
                justTheNumbers = justTheNumbers + textLines[i];
            }
        }

        _log.debug("just the numbers:\n***\n" + justTheNumbers + "\n***\n");

        List<Float> coeffsThisLine = processCoefficientOnlyLine(justTheNumbers);
        for (float coeff : coeffsThisLine) {
            resultList.add(coeff);
        }

        if (resultList.size() < 2) {
            throw new NumberFormatException("Problem parsing coefficient response from R");
        }

        double[] result = new double[resultList.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = resultList.get(i);
        }
        return result;
    }

    /**
     * Parse the important bits of a response from R's 'coeff' command
     * @param coefficientLine
     * @return
     */
    protected static List<Float> processCoefficientOnlyLine(String coefficientLine) {
        List<Float> result = new ArrayList<Float>();
        String[] stringCoeffs = coefficientLine.split("\\s");
        for (int i = 0; i < stringCoeffs.length; i++) {
            _log.debug("@@@" + stringCoeffs[i] + "@@@");
            if (stringCoeffs[i].length() > 0) {
                result.add(Float.parseFloat(stringCoeffs[i]));
            }
        }
        return result;
    }

    /**
     * Parse an R list result as a list of Floats
     * @param varString
     * @return
     */
    public static List<Float> parseNumericList(String varString) {
        List<Float> result = new ArrayList<Float>();
        String[] probChunks = varString.split("\\s");
        for (String probChunk : probChunks) {
            if (!probChunk.contains("[") && probChunk.length() > 0) {
                result.add(Float.parseFloat(probChunk));
            }
        }
        return result;
    }

    /**
     * Given R output, extract a map from variable names provided by a "list" command, to the strings that
     * contain the values
     * @param listOutput
     * @return
     */
    public static Map<String, String> extractVariableStringsFromListOutput(String listOutput) {
        _log.debug("Extracting variable strings from list output...");
        String[] lines = listOutput.split("\\n");
        Map<String, String> result = new HashMap<String, String>();
        StringBuffer currentVarBuf = null;
        String currentVarName = null;
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("$")) {
                if (currentVarName != null) {
                    _log.debug("extractVarStrings: found var " + currentVarName);
                    result.put(currentVarName, currentVarBuf.toString());
                }
                currentVarName = line.substring(1);
                currentVarBuf = new StringBuffer();
            } else {
                if (currentVarName != null) {
                        currentVarBuf.append("\n").append(line);
                }
            }
        }
        result.put(currentVarName, currentVarBuf.toString());
        return result;
    }

    /**
     * Evaluate an R command, giving it the default amount of time to respond
     * @param rCommand command to execute
     * @return results from R
     */
    public static String evaluateRCommand(String rCommand) {
        return evaluateRCommand(rCommand, DEFAULT_MAX_R_WAIT_MILLIS);
    }

    /**
     * Talks to the ReaderThread to check periodically for the specified sentinel.
     * TODO: make this more efficient.  Right now, at every read, it pulls back the whole response
     * @param readerThread
     * @param endResponseSentinel
     * @param maxMillisToWaitForResponse
     * @return
     * @throws IOException
     */
    protected static String collectInput(RReaderThread readerThread,
            Process p,
            String endResponseSentinel,
            int maxMillisToWaitForResponse)
            throws IOException {
        int totalMillisSlept = 0;
        String responseString = "";

//        _log.debug("collectInput, millis to wait: " + maxMillisToWaitForResponse);

        //loop until sentinel shows up.  Likely we'll get it right away, but
        //loop anyway just in case R is slow, which it can be
        responseString = readerThread.getReadString();
        //every time we get more back from R, we convert the whole thing to a String.
        //This is wasteful, but it's necessary to make ABSOLUTELY SURE we capture
        //the sentinel if it occurs.  Besides, likely we won't do this more than once
        //or maybe twice on typical commands
        while (readerThread.status == RReaderThread.STATUS_READING
                && (endResponseSentinel == null
                || !(responseString).contains(endResponseSentinel))) {
            boolean processIsAlive = false;
            int exitValue = 0;
            try {
                exitValue = p.exitValue();
            } catch (IllegalThreadStateException itse) {
                processIsAlive = true;
            }

            if (!processIsAlive) {
                StringBuffer exceptionMessageBuf = new StringBuffer("R Process exited before done reading, with status " + exitValue + ".\n");
                if (responseString.length() < 5000) {
                    exceptionMessageBuf.append("Output from process: " + responseString);
                } else {
                    exceptionMessageBuf.append("Output from process is too long to display ("
                            + responseString.length() + " chars)");
                }
                throw new IOException(exceptionMessageBuf.toString());
            }

//System.err.println("  loop, " + responseString);
            if (totalMillisSlept > maxMillisToWaitForResponse) {
                break;
            }

            //sleep for one increment
            try {
                Thread.sleep(R_SLEEP_INCREMENT_MILLIS);
                totalMillisSlept += R_SLEEP_INCREMENT_MILLIS;
            } catch (InterruptedException e) {
            }
            responseString = readerThread.getReadString();
        }
        _log.debug("collectInput, readerThread status: " + readerThread.status);
        if (readerThread.status == RReaderThread.STATUS_ERROR) {
            throw readerThread.exception;
        }

        return responseString;
    }

    /**
     * Handles a single write of a byte array to R
     */
    public static class RWriterThread extends Thread {

        protected OutputStream out = null;
        protected BufferedOutputStream internalOut;
        protected byte[] bytes = null;
        public boolean done = false;

        public RWriterThread(DataOutputStream out, byte[] bytes) {
            this.out = out;
            this.bytes = bytes;
        }

        public void run() {

            try {
                for (int i = 0; i < bytes.length; i++) {
                    out.write(bytes[i]);
                    out.flush();
                }
            } catch (Throwable t) {
                t.printStackTrace(System.out);
            }
            done = true;
        }
    }

    /**
     * Manages the R error inputstream.
     * On some systems, the error inputstream must be read periodically or writing to the
     * outputstream will hang.  In particular, I've seen output writing hang after 8kb
     * are written; apparently something gets written to the error outputstream during that
     * write, and unless it's written, nothing doing.
     */
    public static class RErrorReaderThread extends Thread {

        protected InputStream inputStream = null;
        protected StringBuffer accumulatedResponse = new StringBuffer();
        protected StringBuffer newDataBuf = new StringBuffer();
        protected boolean keepReading = true;
        public IOException exception = null;
        public int status = STATUS_READING;
        public static final int STATUS_READING = 0;
        public static final int STATUS_ERROR = 1;
        protected boolean hasNewData = false;

        public RErrorReaderThread(Process p) {
            inputStream = new BufferedInputStream(p.getErrorStream());
        }

        public void run() {
            try {
                _log.debug("Starting error thread");
                while (keepReading) {
                    int currentBytesAvailable = inputStream.available();
                    if (currentBytesAvailable > 0) {
                        byte[] rResponse = new byte[currentBytesAvailable];
                        inputStream.read(rResponse);

                        _log.debug("R ERROR reader got output: " + new String(rResponse));

                        accumulatedResponse.append(new String(rResponse));
                        hasNewData = true;
                        newDataBuf.append(new String(rResponse));
                    }

                    //sleep for one increment
                    try {
                        Thread.sleep(R_SLEEP_INCREMENT_MILLIS);
                    } catch (InterruptedException e) {
                        _log.debug("error thread interrupted");
                    }
                }

                if (inputStream != null) {
                    inputStream.close();
                }
                _log.debug("Error reader successfully shutdown");
            } catch (IOException e) {
                _log.error("Failure while reading R response", e);
                status = STATUS_ERROR;
                exception = e;
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (Exception e) {
                }
            }
        }

        public void shutdown() {
            keepReading = false;
        }

        public String getReadString() {
            return accumulatedResponse.toString();
        }

        public boolean hasNewData() {
            return hasNewData;
        }

        public String getNewData() {
            String result = newDataBuf.toString();
            newDataBuf = new StringBuffer();
            return result;
        }
    }

    /**
     * Latches onto the R process' input stream and doesn't let go
     * TODO: provide a way to get access to just whatever was read since last time you checked
     */
    public static class RReaderThread extends Thread {

        InputStream inputStream = null;
        StringBuffer accumulatedResponse = new StringBuffer();
        protected boolean keepReading = true;
        public int totalMillisSlept = 0;
        public IOException exception = null;
        public int status = STATUS_READING;
        public static final int STATUS_READING = 0;
        public static final int STATUS_ERROR = 1;

        public RReaderThread(Process p) {
            inputStream = p.getInputStream();
        }

        public void run() {
            try {
                _log.debug("Starting R output reader thread");
                while (keepReading) {
                    int currentBytesAvailable = inputStream.available();
                    if (currentBytesAvailable > 0) {
                        byte[] rResponse = new byte[currentBytesAvailable];
                        inputStream.read(rResponse);
                        String responseString = new String(rResponse);
                        _log.debug("R output reader got output: " + responseString);
                        accumulatedResponse.append(responseString);

//System.err.println(new String(rResponse));
                    }

                    //sleep for one increment
                    try {
                        Thread.sleep(R_SLEEP_INCREMENT_MILLIS);
                        totalMillisSlept += R_SLEEP_INCREMENT_MILLIS;
                    } catch (InterruptedException e) {
                    }
                }

                if (inputStream != null) {
                    inputStream.close();
                }
                _log.debug("Reader successfully shutdown");
            } catch (IOException e) {
                _log.error("Failure while reading R response", e);
                status = STATUS_ERROR;
                exception = e;
            }
        }

        public void shutdown() {
            keepReading = false;
        }

        public String getReadString() {
            return accumulatedResponse.toString();
        }
    }

    /**
     * Cover method to start up a writer thread, send it some bytes, and make sure they got written
     * @param rOut
     * @param bytesToWrite
     * @throws IOException
     */
    public static void writeToR(DataOutputStream rOut, byte[] bytesToWrite)
            throws IOException {
        RWriterThread wt = new RWriterThread(rOut, bytesToWrite);
        wt.start();

        while (!wt.done) {
            try {
                Thread.sleep(15);
            } catch (InterruptedException ie) {
            }
        }

    }

    /**
     * Read in a fully-qualified resource on the classpath, i.e., an R script
     * @param resourcePath
     * @return
     */
    public static String readResourceFile(String resourcePath)
            throws IOException {
        _log.debug("readResourceFile, resourcePath: " + resourcePath);
        InputStream in = RInterface.class.getResourceAsStream(resourcePath);
        if (in == null) {
            throw new IOException("ERROR!! null resource!");
        }
        StringBuffer commandBuf = new StringBuffer();

        int readchar;
        while ((readchar = in.read()) != -1) {
            commandBuf.append((char) readchar);
        }
        return commandBuf.toString();
    }

    /**
     * Evaluate an R command, or series of commands.  Time out if we wait longer than maxMillis...
     * Return a every single thing that R gives back, with whitespace trimmed from start and end.
     * In order to make sure we wait the appropriate amount of time, and to make sure that we return
     * only R's response and nothing else, I do the following:
     *
     * 1.  Place a sentinel that will be echoed before the command response
     * 2.  Place a sentinel (that can be evaluated by R) after a newline, so that R will echo it after
     * the command completes.  That way we know to ignore everything after the second sentinel, and we know when
     * we're done
     * 3.  When we get back the response, take everything between the two sentinel responses.  Then, if that
     * response contains any "Package loaded" lines, take them out, too
     *
     * If R fails for any reason, throw a RuntimeException
     * @param rCommand
     * @param maxMillisToWaitForResponse
     * @return the result from R
     */
    public static String evaluateRCommand(String rCommand, int maxMillisToWaitForResponse) {
        _log.debug("Running R command:");
//        _log.debug(rCommand);
        while (Character.isWhitespace(rCommand.charAt(rCommand.length() - 1))) {
            rCommand = rCommand.substring(0, rCommand.length() - 1);
        }

        long startTime = new Date().getTime();

        String result = null;

        String responseString = "";

        boolean timedOut = false;
        Process p = null;
        DataOutputStream rOut = null;
        RReaderThread responseReaderThread = null;
        RErrorReaderThread errorReaderThread = null;
        try {
            _log.debug("Starting R...");
            String cmd = "R --vanilla --slave";
            //Kick off R, set up the input and output streams, write the full command and sentinels
            p = Runtime.getRuntime().exec(cmd, null, TempFileManager.getTmpDir());

            //outputstream
            rOut = new DataOutputStream(new BufferedOutputStream(p.getOutputStream(), 8000));
            _log.debug("R process started.");

            //this is necessary for Windows.  R initially produces some output
            //when you kick it off, and if you don't collect it Windows hangs
            //eternally.
            responseReaderThread = new RReaderThread(p);
            responseReaderThread.start();

            errorReaderThread = new RErrorReaderThread(p);
            errorReaderThread.start();

            byte[] bytesToR = rCommand.getBytes();
            _log.debug("Sending command to R.  " + bytesToR.length + " bytes....");
//System.err.println("****************");
//System.err.println(new String(bytesToR));
//System.err.println("****************");

            String sentinel1 = "\"SENTINEL_SUPERCaliFRAGILIsticEXPIAlidOCIOUS1_SENTINEL\"";
            String sentinel2 = "\"SENTINEL_SUPERCaliFRAGILIsticEXPIAlidOCIOUS2_SENTINEL\"";

            writeToR(rOut, ("\n" + sentinel1 + '\n').getBytes());

            writeToR(rOut, bytesToR);

            writeToR(rOut, ("\n" + sentinel2 + '\n').getBytes());
            _log.debug("Sent command to R.");

            //read from the input stream until we come to the end-command sentinel,
            //which will be after the echo of our input but before the response.
            //Reduce the max time to wait by however long we've already waited.
            _log.debug("Waiting for output...");

            //read from the input stream until we come to the second sentinel,
            //which will be echoed after we get our response.
            //Reduce the max time to wait by however long we've already waited.
            responseString = collectInput(responseReaderThread, p, sentinel2,
                    (int) ((maxMillisToWaitForResponse)
                    - (new Date().getTime() - startTime)));

            if (responseString == null
                    || !responseString.contains(sentinel2)) {
//                _log.debug(responseString);
                if (new Date().getTime() - startTime > maxMillisToWaitForResponse) {
                    timedOut = true;
                    throw new RuntimeException("timed out");
                } else {
                    throw new RuntimeException("unknown error, didn't get sentinel");
                }
            }
//System.err.println("Raw R response: " + responseString);

            _log.debug("Got sentinel.  Response length: " + responseString.length());
            //at this point we've both sentinels.
            //Get rid of the last line, which is the sentinel response
            int startIndex = responseString.indexOf(sentinel1) + sentinel1.length();
            while (Character.isWhitespace(responseString.charAt(startIndex))) {
                startIndex++;
            }
            int firstBadIndex = responseString.indexOf(sentinel2);
            while (responseString.charAt(firstBadIndex) != '\n' && firstBadIndex > startIndex) {
                firstBadIndex--;
            }
            result = responseString.substring(startIndex, firstBadIndex);
            //We may get "package loaded" or "null device" lines.  If so, ignore them
            while (result.startsWith("Package") || result.startsWith("null device")) {
                String firstLine = result.substring(0, result.indexOf("\n"));
                if (result.startsWith("null device") || firstLine.contains("loaded.")) {
                    result = result.substring(firstLine.length() + 1);
                } else {
                    break;
                }
            }

            _log.debug("Important part of response (length " + result.length() + "), with whitespace: " + result);
//_log.debug(result);

            //strip whitespace from beginning and end
            while (result.length() > 0 && Character.isWhitespace(result.charAt(0))) {
                result = result.substring(1);
            }
            while (result.length() > 0 && Character.isWhitespace(result.charAt(result.length() - 1))) {
                result = result.substring(0, result.length() - 1);
            }
            _log.debug("Stripped whitespace from response");
        } catch (Exception e) {
            String failureReason = "";
            if (timedOut) {
                failureReason = "Timed out while calling R.  Max millis: " + maxMillisToWaitForResponse
                        + ", waited " + (new Date().getTime() - startTime);
            } else {
                failureReason = "Error calling R, temp dir is " + TempFileManager.getTmpDir() + ". ";
                //write out the R command that failed to a file
                try {
                    File commandOutFile =
                            TempFileManager.createTempFile("r_failed_command.txt", "RCOMMANDFAILUREFILE_DONOTDELETE");
                    PrintWriter commandOutPW = new PrintWriter(commandOutFile);
                    commandOutPW.println(rCommand);
                    commandOutPW.flush();
                    commandOutPW.close();
                } catch (Exception commandOutE) {
                    _log.error("Failed to create R command output file", commandOutE);
                }

                try {
                    if (responseReaderThread != null) {
                        failureReason += failureReason + "R output:\n"
                                + collectInput(responseReaderThread, p, null, 100);
                    } else {
                        failureReason += "No error output from R to display.  Error Message: " + e.getMessage() + ", Exception class: " + e.getClass().getName();
                    }
                } catch (Exception ex) {
                    failureReason += "Failed while interrogating R error output";
                } finally {
                    if (errorReaderThread.hasNewData()) {
                        failureReason = failureReason + "\n Error Output: " + errorReaderThread.getNewData();
                    }
                    e.printStackTrace(System.err);
                }
            }
            throw new RuntimeException(failureReason, e);
        } finally {
            try {
                if (rOut != null) {
                    tellRToQuit(rOut);
                    rOut.close();

                    //Give R a chance to shut down before destroying the process
                    _log.debug("Shutting down R...");
                    p.waitFor();
                    _log.debug("R successfully shutdown");
                }
            } catch (Exception e) {
                _log.debug("Failed to shut down R properly.");
            }

            //close all the input and output streams
            if (responseReaderThread != null) {
                responseReaderThread.shutdown();
            }
            if (errorReaderThread != null) {
                errorReaderThread.shutdown();
            }
        }
        return result;
    }

    protected static void tellRToQuit(DataOutputStream rOut)
            throws IOException {
        writeToR(rOut, "q(save=\"no\")\n".getBytes());
    }

    /**
     * cover method with default wait time, vector variables only
     * @param rExpression
     * @param variableValues
     * @param dependentPackageNames
     * @return
     */
    public static String evaluateRExpression(String rExpression,
            Map<String, double[]> variableValues,
            String[] dependentPackageNames) {
        return evaluateRExpression(rExpression, variableValues,
                dependentPackageNames, DEFAULT_MAX_R_WAIT_MILLIS);
    }

    /**
     * Generic method for running an R expression and feeding the output to a file.
     * Populates variable values and loads packages if necessary
     * @param rExpression
     * @return
     */
    public static String evaluateRExpression(String rExpression,
            Map<String, double[]> variableValues,
            String[] dependentPackageNames,
            int maxMillisToWaitForResponse) {
        return evaluateRExpression(rExpression, variableValues, null,
                dependentPackageNames, maxMillisToWaitForResponse);
    }

    /**
     * Cover method with default wait time
     * @param rExpression
     * @param vectorVariableValues
     * @param matrixVariableValues
     * @param dependentPackageNames
     * @return
     */
    public static String evaluateRExpression(String rExpression,
            Map<String, double[]> vectorVariableValues,
            Map<String, double[][]> matrixVariableValues,
            String[] dependentPackageNames) {
        return evaluateRExpression(rExpression, vectorVariableValues, matrixVariableValues,
                dependentPackageNames, DEFAULT_MAX_R_WAIT_MILLIS);
    }

    public static String evaluateRExpression(String rExpression,
            Map<String, double[]> vectorVariableValues,
            Map<String, double[][]> matrixVariableValues,
            String[] dependentPackageNames,
            int maxMillisToWaitForResponse) {
        return evaluateRExpression(rExpression, null, vectorVariableValues, matrixVariableValues,
                dependentPackageNames, maxMillisToWaitForResponse);
    }

    /**
     * R doesn't like backslashes, even on Windows.  So replace them with forward slashes
     * @param file
     * @return
     */
    public static String generateRFriendlyPath(File file) {
        String filePath = file.getAbsolutePath();
        return generateRFriendlyPath(filePath);
    }

    public static String generateRFriendlyPath(String filePath) {
        return filePath.replaceAll("\\\\", "/");
    }

    /**
     * Read rFile into a String and run the whole file as an R expression
     * @param rFile
     * @param scalarVariableValues
     * @param vectorVariableValues
     * @param matrixVariableValues
     * @param dependentPackageNames
     * @param maxMillisToWaitForResponse
     * @return
     * @throws FileNotFoundException
     */
    public static String runRScript(File rFile,
            Map<String, Object> scalarVariableValues,
            Map<String, double[]> vectorVariableValues,
            Map<String, double[][]> matrixVariableValues,
            String[] dependentPackageNames,
            int maxMillisToWaitForResponse)
            throws FileNotFoundException {
        FileReader fr = new FileReader(rFile);
        char[] fileArray = new char[(int) rFile.length()];
        return evaluateRExpression(new String(fileArray), scalarVariableValues,
                vectorVariableValues,
                matrixVariableValues,
                dependentPackageNames,
                maxMillisToWaitForResponse);
    }

    /**
     * Generic method for running an R expression and feeding the output to a file.
     * Populates variable values and loads packages if necessary
     * @param rExpression
     * @return
     */
    public static String evaluateRExpression(String rExpression,
            Map<String, Object> scalarVariableValues,
            Map<String, double[]> vectorVariableValues,
            Map<String, double[][]> matrixVariableValues,
            String[] dependentPackageNames,
            int maxMillisToWaitForResponse) {
        StringBuffer rCommand = new StringBuffer();
        if (dependentPackageNames != null && dependentPackageNames.length > 0) {
            for (String dependentPackageName : dependentPackageNames) {
                rCommand.append("library('" + dependentPackageName + "')\n");
            }
        }
        if (scalarVariableValues != null && scalarVariableValues.size() > 0) {
            for (String variableName : scalarVariableValues.keySet()) {
                rCommand.append(variableName + "<-" + scalarVariableValues.get(variableName) + "\n");
            }
        }
        if (vectorVariableValues != null && vectorVariableValues.size() > 0) {
            for (String variableName : vectorVariableValues.keySet()) {
                rCommand.append(variableName + "<-c(");
                double[] thisVarValues = vectorVariableValues.get(variableName);
                for (int i = 0; i < thisVarValues.length; i++) {
                    rCommand.append(Double.isNaN(thisVarValues[i]) ? "NA" : thisVarValues[i]);
                    if (i < thisVarValues.length - 1) {
                        rCommand.append(",\n");
                    }
                }
                rCommand.append(")\n");
            }
        }
        if (matrixVariableValues != null && matrixVariableValues.size() > 0) {
            for (String variableName : matrixVariableValues.keySet()) {
                rCommand.append(variableName + "<-matrix(c(");
                double[][] thisVarValues = matrixVariableValues.get(variableName);
                _log.debug("Building matrix " + variableName + ", " + thisVarValues[0].length + " columns, "
                        + thisVarValues.length + " rows");

                for (int i = 0; i < thisVarValues.length; i++) {
                    for (int j = 0; j < thisVarValues[0].length; j++) {
                        rCommand.append(Double.isNaN(thisVarValues[i][j]) ? "NA" : thisVarValues[i][j]);
                        //append the comma unless we're on the very last cell
                        if (i < thisVarValues.length - 1
                                || j < thisVarValues[0].length - 1) {
                            rCommand.append(",");
                        }
                        //this is just formatting, for debug purposes
                        if (j == thisVarValues[0].length - 1) {
                            rCommand.append("\n");
                        }
                    }
                }
                rCommand.append("),nrow=" + thisVarValues.length
                        + ",ncol=" + thisVarValues[0].length + ", byrow=TRUE)\n");
            }
        }
        rCommand.append(rExpression);
        return evaluateRCommand(rCommand.toString(), maxMillisToWaitForResponse);
    }
    /**
     * Creates a file containing the values to be loaded into the R variable with name variableName,
     * and returns the R syntax necessary to load the values into the variable.
     * If not successful, throws an exception
     * @param variableName
     * @param variableValues
     * @return
     */
    /*       This was useful when everything was file-based.  Not so much any more
    protected static String populateArrayValueFromFile(String variableName, double[] variableValues,
    Object caller)
    throws FileNotFoundException
    {
    File tempVarValueFile =
    TempFileManager.createTempFile("RInterface.populateArrayValueFromFile.var." + variableName,
    caller);
    PrintWriter pw = null;

    try
    {
    pw = new PrintWriter(tempVarValueFile);
    for (double variableValue : variableValues)
    pw.println(variableValue);
    }
    finally
    {
    if (pw != null)
    pw.close();
    }

    return variableName + "<-read.table('" + tempVarValueFile.getAbsolutePath() +"',header=FALSE)[,1]";
    }
     */
}
