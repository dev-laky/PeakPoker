![Lint/Fmt](https://github.com/dev-laky/PeakPoker/actions/workflows/check_lint_and_format.yaml/badge.svg?branch=main)
![Testing](https://github.com/dev-laky/PeakPoker/actions/workflows/test.yaml/badge.svg?branch=main)

![image info](/assets/img/peakpoker2.jpeg)

# Peak Poker - Up to Everest!

This repository contains a student project created for an ongoing lecture on object-oriented
programming with Kotlin at HWR Berlin (summer term 2025).

> :warning: This code is for educational purposes only. Do not rely on it!

## Development Prerequisites

Installed:

1. IDE of your choice (e.g. IntelliJ IDEA)
2. JDK of choice (JDK 21 GraalVM recommended)
3. Maven (e.g. through IntelliJ IDEA)
4. Git

## Local Development

This project uses [Apache Maven][maven] as build tool.

To build from your shell (without an additional local installation of Maven), ensure that `./mvnw`
is executable:

```
chmod +x ./mvnw
```

I recommend not diving into details about Maven at the beginning.
Instead, you can use [just][just] to build the project.
It reads the repositories `justfile` which maps simplified commands to corresponding sensible Maven
calls.

With _just_ installed, you can simply run this command to perform a build of this project and run
all of its tests:

```
just build
```

### Formatting

The repository contains an IntelliJ IDEA formatter configuration file.
It is located in the `.intellij` folder (not in `.idea`, which is the folder created by IntelliJ IDEA).
To use the formatter, you need to import the configuration into your IntelliJ IDEA settings.

Under **Settings**, go to **Editor**, then **Code Style**, click the **Gear Symbol** next to the Dropdown, click **Import Scheme**, then **IntelliJ IDEA code style XML**. Finally, select the `.intellij/formatter.xml` file.

Make sure to always use the imported `HWR OOP` code style when formatting your code.
Be aware that it might differ from the code style configured in your *Project*, or IntelliJ's *Default* code style.

## Abstract

### Project Description

Have you ever wanted to reach the peak of poker?
This project aims to create a poker game that allows players to experience the thrill of climbing to the top of the
poker world - up to the poker everest!
You can play virtual poker games via the *CLI* against other players.
Start with free 10$ and try to climb up the wealth ladder.

### Challenges

### Challenges

- **Managing Game State and Player Interaction**  
  Synchronizing game state across multiple players and handling asynchronous CLI inputs smoothly.

- **Team Coordination and Code Integration**  
  Efficiently collaborating and maintaining consistent code quality.

### Features

#### 🎮 Game Management

- **Start a New Game**
    - `poker new-game --players=<name1>:<name2>:<name3>:...`
    - Starts a new poker game with the given list of players.
    - Have multiple games running at the same time.

#### 💰 Betting Commands

- **Raise**
    - `poker raise <amount> --game=<gameID> --player=<name>`
    - Raises the current bet by the specified amount.

- **Call**
    - `poker call --game=<gameID> --player=<name>`
    - Matches the current highest bet.

- **Check**
    - `poker check --game=<gameID> --player=<name>`
    - Passes the turn without betting if no bet is active.

- **Fold**
    - `poker fold --game=<gameID> --player=<name>`
    - Folds the player's hand, removing them from the current round.

- **All-In**
    - `poker all-in --game=<gameID> --player=<name>`
    - Bets all of the player’s remaining chips.

#### 📊 Info Commands

- **Game Info**
    - `poker game-info --game=<gameID>`
    - Displays the current game state, including players, pot, and turn.

- **Show Hand**
    - `poker hand --game=<gameID> --player=<name>`
    - Reveals the cards held by the specified player.

## Feature List

| Number | Feature                     | Tests                                    |
|--------|-----------------------------|------------------------------------------|
| 1      | Start new game              | CreateNewGameTest, GameTestCustomPlayers |
| 2      | Raise                       | RaiseTest                                |
| 3      | Call                        | CallTest                                 |
| 4      | Check                       | CheckTest                                |
| 5      | Fold                        | FoldTest                                 |
| 6      | All-In                      | AllInTest                                |
| 7      | Game Info                   | GameInfoTest                             |
| 8      | Show Hand                   | HandInfoTest                             |
| 9      | Deck Management             | DeckTest                                 |
| 10     | Community Cards             | CommunityCardsTest                       |
| 11     | Hole Cards                  | HoleCardsTest                            |
| 12     | Hand Evaluation             | HandEvaluatorTest, PokerHandTest         |
| 13     | Player Management           | PlayerTest                               |
| 14     | Pot Management              | PotTest, PokerPotsTest                   |
| 15     | Persistence Layer           | FileSystemPersistenceAdapterTest.kt      |
| 16     | Command Parsing/Integration | PokerTest, MainTest                      |

## Additional Dependencies

| Number | Dependency Name          | Dependency Description            | Why is it necessary?                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
|--------|--------------------------|-----------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1      | clikt                    | Command Line Interface for Kotlin | Clikt is necessary for this project because it provides a simple and intuitive way to create command-line interfaces in Kotlin. This is especially important for team collaboration as it ensures that all team members can easily understand and modify the command-line interface code. Additionally, Clikt helps in maintaining consistency and reducing boilerplate code, which improves the overall development efficiency and code quality. Lastly it provides command documentation by default. |
| 2      | kotlinx.serialization    | Kotlin Serialization Library      | This library is used to serialize and deserialize game data such as player states, hands, and game progress. It helps in saving and loading game states efficiently, which is crucial for game persistence and debugging. Serialization also facilitates potential features like network communication or saving game logs.                                                                                                                                                                            |
| 3      | mockito / mockito-kotlin | Mocking Framework for Kotlin/Java | Mockito and its Kotlin extensions are essential for writing unit tests by allowing mocking of classes and interfaces. This helps in isolating components during testing, ensuring the poker game logic behaves correctly under various scenarios without relying on actual implementations of dependencies. It improves test reliability and helps the team maintain high code quality.                                                                                                                |

## Game Commands

### Admin Commands

| Command                                                | Description                                  |
|--------------------------------------------------------|----------------------------------------------|
| `poker new-game --players=<name1>:<name2>:<name3>:...` | Starts a new game with the specified players |

### Betting Commands

| Command                                            | Description           |
|----------------------------------------------------|-----------------------|
| `poker raise 10 --gameID=<gameID> --player=<name>` | Raise the bet by 10   |
| `poker call --gameID=<gameID> --player=<name>`     | Call the current bet  |
| `poker check --gameID=<gameID> --player=<name>`    | Check (pass the turn) |
| `poker fold --gameID=<gameID> --player=<name>`     | Fold the hand         |
| `poker all-in --gameID=<gameID> --player=<name>`   | Go all-in             |

### Info Commands

| Command                                        | Description                   |
|------------------------------------------------|-------------------------------|
| `poker game-info --gameID=<gameID>`            | Show current game information |
| `poker hand --gameID=<gameID> --player=<name>` | Show the hand of the player   |

[maven]: https://maven.apache.org/

[just]: https://github.com/casey/just
