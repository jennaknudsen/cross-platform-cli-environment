## Cross-Platform CLI Environment

https://user-images.githubusercontent.com/45336771/138984593-ee84fffa-cf18-45ea-9aae-c0496f5516ce.mp4

This program was written in Java and is compiled using the Gradle
build system. The program can be run in the base directory of this repo
by running `gradle build` followed by `java -jar build/libs/Shell.jar`.

Included in the `src/` director are a `TestInput.java` file and a
`TestOutput.java` file to test the piping functionality of this program.

### Technical Notes

* This shell is designed to work out-of-the-box with Windows, macOS, and Linux.
* Processes can be ran by name from the command line.
  * The shell checks files in the current directory as well as the system PATH.
* This command shell supports the following builtins:
  * `exit` Exits the shell. 
    * Shell can also be exited using the EOF character.
  * `ptime` Displays the amount of time spent executing child processes (not builtins).
  * `list` Displays contents of the current folder.
    * Shows `drwx` permissions for current user, file size in bytes, date of last modification, and
      file name.
  * `cd [name]` Changes directory to specified folder name, or to home directory if no
    arguments were specified.
  * `here` Prints current working directory.
  * `mdir <name>` Creates a directory with the specified name.
  * `rdir <name>` Removes a directory with the specified name.
  * `history` Prints a list of all previously executed commands in this session.
  * `^ <number>` Executes the command at the specified position in the history.
* Piping using the `|` symbol is supported between two processes.
