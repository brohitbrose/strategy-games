# Strategy Games

Game loops and AIs for various strategy games. Though all of the concrete games
so far happen to be abstract and zero-sum, this is not an intended restriction
of this project.

For interface-level game lifecycle logic, visit the [game](./game) subfolder.

For interface-level AI logic, visit the [game](./ai) subfolder.

For a concrete Niya implementation, visit the [niya](./niya) subfolder.

For a concrete tic-tac-toe implementation, visit the [tictactoe](./tictactoe)
subfolder.

## Build, Test, and Run

We use [Gradle](https://gradle.org/install/) to build this project. After it is
installed on your system, the following commands (and more, but these are the
important ones) will be available from THIS DIRECTORY:

- `gradle compileJava`: compiles the `main` modules in each subfolder

- `gradle compileTestJava`: wherever available, compiles the `test` modules (and
any `main` modules that are not up-to-date)

- `gradle test`: runs all test cases

- `gradle build`: executes the previous three tasks and generates compressed,
executable distributions in `$subfolder/build/distributions` for each subfolder

- `gradle javadoc`: generates .html javadocs in `$subfolder/build/docs` for each
subfolder

Each of these tasks is also available from EACH SUBFOLDER; running them this way
limits their effects to the subfolders themselves. Additionally, a new task
becomes available:

- `gradle run`: runs, in a Gradle VM, the `main` method of the `class` defined
as the `mainClassName` field in the project's `build.gradle`
