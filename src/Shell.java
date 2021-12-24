import java.io.*;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The class that holds the entire program.
 * @author Jenna Knudsen
 */
public class Shell {
    /**
     * Static int holding the total time spent in child processes.
     */
    public static double timeSpentInChildProcesses = 0.0;

    /**
     * List of all shell commands.
     */
    public static ArrayList<String> commandHistory;

    /**
     * The main loop of this program.
     * @param args Command-line arguments (they're ignored for this program)
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        commandHistory = new ArrayList<>();

        while(true) {
            // get user input
            System.out.print("[" + System.getProperty("user.dir") + "]: ");

            String[] command = null;

            try {
                command = splitCommand(scanner.nextLine());
            } catch (NoSuchElementException e) {
                // this exception will be thrown if user enters EOF character
                // if this happens, just exit the program
                exit();
            }

            // by this point, command shouldn't be null, so if it is then throw an exception
            assert command != null;

            // if for some reason, the user didn't enter a command, treat is as a blank line and do nothing
            if (command.length != 0) {
                executeCommand(command, true);
            }
        }
    }

    /**
     * Given a command, execute it.
     * @param command The command to execute
     * @param appendToHistory Boolean determining whether to store command in history or not
     */
    public static void executeCommand(String[] command, boolean appendToHistory) {
        // add full command to history unless command called from ^ command
        if (appendToHistory) {
            StringBuilder fullTextOfCommand = new StringBuilder();
            for (int i = 0; i < command.length; i++) {
                fullTextOfCommand.append(command[i]);
                // don't append a space if this is last command
                if (i < command.length - 1) {
                    fullTextOfCommand.append(" ");
                }
            }
            commandHistory.add(fullTextOfCommand.toString());
        }

        boolean areWePiping = false;
        for (String s : command) {
            if (s.equals("|")) {
                areWePiping = true;
                break;
            }
        }

        if (!areWePiping) {
            if (command[0].equals("exit")) {
                exit();
            } else if (command[0].equals("ptime")) {
                System.out.println(ptime());
            } else if (command[0].equals("list")) {
                System.out.println(list());
            } else if (command[0].equals("cd")) {
                System.out.print(cd(command));
            } else if (command[0].equals("here")) {
                System.out.println(here());
            } else if (command[0].equals("mdir")) {
                if (command.length == 1) {
                    System.out.println("mdir: missing operand");
                } else {
                    System.out.print(mdir(command[1]));
                }
            } else if (command[0].equals("rdir")) {
                if (command.length == 1) {
                    System.out.println("rdir: missing operand");
                } else {
                    System.out.print(rdir(command[1]));
                }
            } else if (command[0].equals("history")) {
                System.out.println(history());
            } else if (command[0].equals("^")) {
                if (command.length == 1) {
                    System.out.println("^: missing operand");
                } else {
                    executeHistoryCommand(command[1]);
                }
            }

            // if we get to this point, run it as a generic command
            else {
                runProcess(false, false, "", command);
            }
        } else {
            // if we are piping, things get more complicated
            // must run first command, and then send its output to second program
            String[] firstCommand;
            String[] secondCommand;
            ArrayList<String> firstCommandArrayList = new ArrayList<>();
            ArrayList<String> secondCommandArrayList = new ArrayList<>();

            // separate the commands into two separate arrays
            boolean onSecondCommand = false;
            for (String s : command) {
                if (s.equals("|")) {
                    onSecondCommand = true;
                } else if (onSecondCommand) {
                    secondCommandArrayList.add(s);
                } else {
                    firstCommandArrayList.add(s);
                }
            }

            firstCommand = firstCommandArrayList.toArray(new String[0]);
            secondCommand = secondCommandArrayList.toArray(new String[0]);

            String firstCommandOutput = "";

            if (firstCommand[0].equals("exit")) {
                exit();
            } else if (firstCommand[0].equals("ptime")) {
                firstCommandOutput = ptime();
                System.out.println(firstCommandOutput);
            } else if (firstCommand[0].equals("list")) {
                firstCommandOutput = list();
                System.out.println(firstCommandOutput);
            } else if (firstCommand[0].equals("cd")) {
                firstCommandOutput = cd(firstCommand);
                System.out.print(firstCommandOutput);
            } else if (firstCommand[0].equals("here")) {
                firstCommandOutput = here();
                System.out.println(firstCommandOutput);
            } else if (firstCommand[0].equals("mdir")) {
                if (firstCommand.length == 1) {
                    System.out.println("mdir: missing operand");
                } else {
                    firstCommandOutput = mdir(firstCommand[1]);
                    System.out.print(firstCommandOutput);
                }
            } else if (firstCommand[0].equals("rdir")) {
                if (firstCommand.length == 1) {
                    System.out.println("rdir: missing operand");
                } else {
                    firstCommandOutput = rdir(firstCommand[1]);
                    System.out.print(firstCommandOutput);
                }
            } else if (firstCommand[0].equals("history")) {
                firstCommandOutput = history();
                System.out.println(firstCommandOutput);
            } else if (firstCommand[0].equals("^")) {
                if (firstCommand.length == 1) {
                    System.out.println("^: missing operand");
                } else {
                    executeHistoryCommand(firstCommand[1]);
                }
            }
            // if we get to this point, run it as a generic command
            else {
                firstCommandOutput = runProcess(true, false, "", firstCommand);
            }

            // now, run the second command
            // our shell commands don't care about any of the piping stuff, so we
            // only need to take care of piping for the generic command

            if (secondCommand[0].equals("exit")) {
                exit();
            } else if (secondCommand[0].equals("ptime")) {
                System.out.println(ptime());
            } else if (secondCommand[0].equals("list")) {
                System.out.println(list());
            } else if (secondCommand[0].equals("cd")) {
                System.out.print(cd(secondCommand));
            } else if (secondCommand[0].equals("here")) {
                System.out.println(here());
            } else if (secondCommand[0].equals("mdir")) {
                if (secondCommand.length == 1) {
                    System.out.println("mdir: missing operand");
                } else {
                    System.out.print(mdir(secondCommand[1]));
                }
            } else if (secondCommand[0].equals("rdir")) {
                if (secondCommand.length == 1) {
                    System.out.println("rdir: missing operand");
                } else {
                    System.out.print(rdir(secondCommand[1]));
                }
            } else if (secondCommand[0].equals("history")) {
                System.out.println(history());
            } else if (secondCommand[0].equals("^")) {
                if (secondCommand.length == 1) {
                    System.out.println("^: missing operand");
                } else {
                    executeHistoryCommand(secondCommand[1]);
                }
            }

            // if we get to this point, run it as a generic command
            else {
                runProcess(false, true, firstCommandOutput, secondCommand);
            }
        }
    }

    /**
     * Exits the program gracefully.
     */
    public static void exit() {
        // add blank line to make it more "pretty"
        System.out.println("");
        System.exit(0);
    }

    /**
     * Returns the total amount of time spent in child processes.
     * @return Time spent in child processes
     */
    public static String ptime() {
        return "Total time in child processes: " + String.format("%.4f", timeSpentInChildProcesses) + " seconds";
    }

    /**
     * Returns files and folders in a directory with detailed information.
     * @return List of files
     */
    public static String list() {
        /*
        The first four characters indicate: directory, user can read, user can write, user can execute.
        The next 10 characters contains the size of the file in bytes, right justified (no commas).
        The next field is the date of last modification for the file; follow the example formatting.
        The last field is the name of the file.
         */
        File currentDirectory = new File(System.getProperty("user.dir"));
        File[] arrayOfFiles = currentDirectory.listFiles();
        ArrayList<String> listOfFiles = new ArrayList<>();
        for (File file : arrayOfFiles) {
            String filePermissions = "";
            String fileSize = "";
            String fileModified = "";
            String fileName = "";

            StringBuilder filePermissionsSB = new StringBuilder();
            if (file.isDirectory()) {
                filePermissionsSB.append("d");
            } else {
                filePermissionsSB.append("-");
            }
            if (file.canRead()) {
                filePermissionsSB.append("r");
            } else {
                filePermissionsSB.append("-");
            }
            if (file.canWrite()) {
                filePermissionsSB.append("w");
            } else {
                filePermissionsSB.append("-");
            }
            if (file.canExecute()) {
                filePermissionsSB.append("x");
            } else {
                filePermissionsSB.append("-");
            }

            filePermissions = filePermissionsSB.toString();

            fileSize = String.format("%10s", file.length());

            // Date should be MMM dd, yyyy hh:mm
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
            fileModified = sdf.format(file.lastModified());

            fileName = file.getName();

            listOfFiles.add(filePermissions + " " + fileSize + " " + fileModified + " " + fileName);
        }

        StringBuilder returnStringSB = new StringBuilder();
        for (int i = 0; i < listOfFiles.size(); i++) {
            returnStringSB.append(listOfFiles.get(i));
            // don't add line separator for last entry
            if (i < listOfFiles.size() - 1) {
                returnStringSB.append(System.getProperty("line.separator"));
            }
        }

        return returnStringSB.toString();
    }

    /**
     * Change the working directory of the shell.
     * @param directory The directory that the shell should try to switch to
     * @return empty string
     */
    public static String cd(String... directory) {
        // directory[0] will always be the string "cd"
        // if directory is of length 1, cd to home
        // else, follow the cd path
        if (directory.length == 1) {
            System.setProperty("user.dir", System.getProperty("user.home"));
        } else if (directory[1].equals("..")) {
            try {
                System.setProperty("user.dir", new File(System.getProperty("user.dir")).getParent());
            } catch (NullPointerException npe) {
                // NPE if trying to cd out of root
                // don't do anything instead of crashing
            }
        } else if (directory[1].equals(".")) {
            // do nothing if cd into self
        } else {
            File directoryToCDTo = new File(System.getProperty("user.dir") + File.separator + directory[1]);
            if (directoryToCDTo.exists() && directoryToCDTo.isDirectory()) {
                System.setProperty("user.dir", System.getProperty("user.dir") + File.separator + directory[1]);
            } else {
                System.out.println("Error: directory " + directory[1] + " does not exist");
            }
        }

        // no need to return anything from cd
        return "";
    }

    /**
     * Returns cwd
     * @return cwd
     */
    public static String here() {
        return System.getProperty("user.dir");
    }

    /**
     * Creates a directory given a string input
     * @param directoryName Name of directory to be created
     * @return Empty string
     */
    public static String mdir(String directoryName) {
        File newDirectory = new File(System.getProperty("user.dir") + File.separator + directoryName);
        if (newDirectory.exists()) {
            if (newDirectory.isFile()) {
                System.out.println("Error: " + directoryName + " already exists as a file");
            } else {
                System.out.println("Error: directory " + directoryName + " already exists");
            }
        } else {
            if (!newDirectory.mkdir()) {
                System.out.println("Error creating directory " + directoryName);
            }
        }

        // return empty string
        return "";
    }

    /**
     * Removes a directory (or a file) given a string input
     * @param directoryName Name of directory (or file) to be removed
     * @return Empty string
     */
    public static String rdir(String directoryName) {
        File directoryToRemove = new File(System.getProperty("user.dir") + File.separator + directoryName);
        if (directoryToRemove.exists()) {
            if (!directoryToRemove.delete()) {
                System.out.println("Error deleting " + directoryName + " (directory must be empty)");
            }
        } else {
            System.out.println("Error: Directory or file " + directoryName + " does not exist");
        }

        return "";
    }

    /**
     * Gets the history of the shell commands.
     * @return History of shell commands
     */
    public static String history() {
        StringBuilder returnStringSB = new StringBuilder();
        returnStringSB.append("-- Command History --").append(System.getProperty("line.separator"));
        for (int i = 0; i < commandHistory.size(); i++) {
            // 1-based index (not 0-based)
            returnStringSB.append(i + 1).append(" : ").append(commandHistory.get(i));
            // don't add line separator for last entry
            if (i < commandHistory.size() - 1) {
                returnStringSB.append(System.getProperty("line.separator"));
            }
        }

        return returnStringSB.toString();
    }

    /**
     * Given a command number in the history, execute that command.
     *
     * This won't work if the command number either refers to itself, or a
     * future command (to prevent infinite looping).
     *
     * @param commandNumber The command number to execute.
     */
    public static void executeHistoryCommand(String commandNumber) {
        try {
            int number = Integer.parseInt(commandNumber);

            // don't allow history command to refer to itself
            if (number == commandHistory.size()) {
                throw new IndexOutOfBoundsException();
            }

            String commandToExecute = commandHistory.get(number - 1);   // commandNumber input will be 1-based
            String[] commandsSplitUp = splitCommand(commandToExecute);
            executeCommand(commandsSplitUp, false);
        } catch (NumberFormatException ex) {
            System.out.println("Error: " + commandNumber + " is not int");
        } catch (IndexOutOfBoundsException ex) {
            System.out.println("Error: " + commandNumber + " outside of history bounds");
            // if the command is out of bounds, remove it from command history
            // this will prevent infinite looping
            commandHistory.remove(commandHistory.size() - 1);
        }
    }

    /**
     * Start a process, given zero or more arguments for that process.
     * @param willWePipe Boolean determining whether this is the first in a set of two piped commands.
     * @param areWePiping Boolean determining whether this is the second in a set of two piped commands.
     * @param pipeString The output of the first piped command, inputted to this function as a string.
     * @param command The command to run (including arguments).
     * @return The string output of that process.
     */
    public static String runProcess(boolean willWePipe, boolean areWePiping, String pipeString, String... command) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command);

            // this will let the child process inherit the shell's IO
            // we always want to redirect the error stream
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            // if this is first process in pipe, we don't want our output to be STDOUT
            // return the string that the process outputted instead
            // otherwise, redirect the process's output to our screen
            if (!willWePipe) {
                pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            }
            // if this is second process in pipe, we don't want the process's input to be our STDIN
            // otherwise, redirect the process's input to our input
            if (!areWePiping) {
                pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
            }

            // set the correct directory for the process
            pb.directory(new File(System.getProperty("user.dir")));

            // time the process
            long startTimeMillis = System.currentTimeMillis();
            Process process = pb.start();
            if (areWePiping) {
                OutputStream os = process.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
                writer.write(pipeString);
                writer.flush();
                writer.close();
            }
            process.waitFor();

            long elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis;
            // convert elapsed time in millis to seconds
            timeSpentInChildProcesses += (elapsedTimeMillis) / 1000.0d;

            // we only need to return a string if this is the first process in a pipe
            // if it's not the first process in a pipe, just return an empty string instead
            // we already printed the process output to screen, we don't want to double print it
            if (willWePipe) {
                // read the output of that process and store it into a String
                // Source: user Reimeus, https://stackoverflow.com/a/16714180 {
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    // use line.separator due to line ending differences on Windows and Linux
                    builder.append(System.getProperty("line.separator"));
                }
                // } back to my code

                // return the output of that process
                return builder.toString();
            } else {
                return "";
            }
        } catch (IOException e) {
            StringBuilder fullTextOfCommand = new StringBuilder();
            for (String s : command) {
                fullTextOfCommand.append(s).append(" ");
            }
            System.out.println("Invalid command: " + fullTextOfCommand);
        } catch (InterruptedException e) {
            System.out.println("Command forcefully exited");
        }

        // if failed, just return an empty string
        return "";
    }

    /**
     * Split the user command by spaces, but preserving them when inside double-quotes.
     * Code Adapted from: https://stackoverflow.com/questions/366202/regex-for-splitting-a-string-using-space-when-not-surrounded-by-single-or-double
     */
    public static String[] splitCommand(String command) {
        java.util.List<String> matchList = new java.util.ArrayList<>();

        Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
        Matcher regexMatcher = regex.matcher(command);
        while (regexMatcher.find()) {
            if (regexMatcher.group(1) != null) {
                // Add double-quoted string without the quotes
                matchList.add(regexMatcher.group(1));
            } else if (regexMatcher.group(2) != null) {
                // Add single-quoted string without the quotes
                matchList.add(regexMatcher.group(2));
            } else {
                // Add unquoted word
                matchList.add(regexMatcher.group());
            }
        }

        return matchList.toArray(new String[0]);
    }
}
