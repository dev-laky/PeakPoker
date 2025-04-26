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
2. JDK of choice installed (JDK 21 GraalVM recommended)
3. Maven installed (e.g. through IntelliJ IDEA)
4. Git installed

## Local Development

This project uses [Apache Maven][maven] as build tool.

To build from your shell (without an additional local installation of Maven), ensure that `./mvnw`
is executable:

```
chmod +x ./mvnw
```

I recommend not to dive into details about Maven at the beginning.
Instead, you can use [just][just] to build the project.
It reads the repositories `justfile` which maps simplified commands to corresponding sensible Maven
calls.

With _just_ installed, you can simply run this command to perform a build of this project and run
all of its tests:

```
just build
```

## Abstract

[TODO]: # (Write a short description of your project.)

[TODO]: # (State most important features.)

### Project Description

Have you ever wanted to reach the peak of poker?
This project aims to create a poker game that allows players to experience the thrill of climbing to the top of the
poker world - up to the poker everest!
You can play virtual poker games via the *CLI* against other players or a computer opponent.
Start with free 10$ and try to climb up the wealth ladder.

### Challenges

[TODO]: # (State the most interesting problems you encountered during the project.)
tbd

### Features

- Customize amount of computer players
- Customize amount of human players
- Customize difficulty of computer players
- Customize starting money
- Customize number of rounds
- Customize amount of chips

## Feature List

[TODO]: # (For each feature implemented, add a row to the table!)

| Number | Feature | Tests |
|--------|---------|-------|
| 1      | /       | /     |

## Additional Dependencies

[TODO]: # (For each additional dependency your project requires- Add an additional row to the table!)

| Number | Dependency Name | Dependency Description            | Why is it necessary?                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
|--------|-----------------|-----------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1      | clikt           | Command Line Interface for Kotlin | Clikt is necessary for this project because it provides a simple and intuitive way to create command-line interfaces in Kotlin. This is especially important for team collaboration as it ensures that all team members can easily understand and modify the command-line interface code. Additionally, Clikt helps in maintaining consistency and reducing boilerplate code, which improves the overall development efficiency and code quality. Lastly it provides command documentation by default. |

## Game Commands

#### Start a Game

| Command                    | Description                        |
|:---------------------------|:-----------------------------------|
| `$ poker play [ID]`        | Join existing game                 |
| `$ poker init`             | Initialize new game                |
| `$ poker join Alice, 1000` | Join new game as player with money |
| `$ poker play start`       | Start initialized game             |
| `$ poker ID`               | Get current game ID                |

[NOTE]: # (`$ poker init` starts "joining phase" in the CLI that ends with `$ poker play start`)

#### Pre-Flop Phase

| Command                     | Description                      |
|:----------------------------|:---------------------------------|
| `$ poker deal preflop`      | Deal cards to players            |
| `$ poker post blinds`       | Post Small and Big Blinds        |
| `$ poker bet Sblind 10`     | Set and bet the Small Blind      |
| `$ poker bet Bblind 25`     | Set and bet the Big Blind        |
| `$ poker bet Bob raise 100` | Bob raises to 100                |
| `$ poker fold Charlie`      | Charlie folds                    |
| `$ poker bet Alice all-in`  | Alice is all-in                  |
| `$ poker hand Alice`        | Show Alice’s hand for 10 seconds | 
| `$ poker pot`               | Show Pot                         |
| `$ poker ID`                | Get current game ID              |
| `$ poker stack Bob`         | Show Bob's current balance       |

[NOTE]: # (only let players see their hand after all bets are placed)

#### Flop Phase

| Command                     | Description                      |
|:----------------------------|:---------------------------------|
| `$ poker deal flop`         | Reveal three community cards     |
| `$ poker bet Alice check`   | Alice checks                     |
| `$ poker bet Bob raise 150` | Bob raises with 150              |
| `$ poker bet Alice call`    | Alice calls                      |
| `$ poker bet Alice all-in`  | Alice is all-in                  |
| `$ poker hand Bob`          | Show Bob’s hand (optional/debug) |
| `$ poker pot`               | Show Pot                         |
| `$ poker ID`                | Get current game ID              |
| `$ poker board`             | Show community Cards             |
| `$ poker stack Alice`       | Show Alice's current balance     |

#### Turn Phase

| Command                     | Description                        |
|:----------------------------|:-----------------------------------|
| `$ poker deal turn`         | Reveal fourth community card       |
| `$ poker bet Alice check`   | Alice checks                       |
| `$ poker bet Bob raise 200` | Bob bets 200                       |
| `$ poker bet Alice call`    | Alice calls                        |
| `$ poker bet Alice all-in`  | Alice is all-in                    |
| `$ poker hand Alice`        | Show Alice’s hand (optional/debug) |
| `$ poker pot`               | Show Pot                           |
| `$ poker ID`                | Get current game ID                |
| `$ poker board`             | Show community Cards               |
| `$ poker stack Bob`         | Show Bob's current balance         |

#### River Phase

| Command                     | Description                        |
|:----------------------------|:-----------------------------------|
| `$ poker deal river`        | Reveal fifth community card        |
| `$ poker bet Alice check`   | Alice checks                       |
| `$ poker bet Bob raise 250` | Bob bets 250                       |
| `$ poker bet Alice call`    | Alice calls                        |
| `$ poker bet Bob all-in`    | Bob is all-in                      |
| `$ poker hand Alice`        | Show Alice’s hand (optional/debug) |
| `$ poker pot`               | Show Pot                           |
| `$ poker ID`                | Get current game ID                |
| `$ poker board`             | Show community Cards               |
| `$ poker stack Bob`         | Show Bob's current balance         |

#### Showdown

| Command               | Description                      |
|:----------------------|:---------------------------------|
| `$ poker board`       | Show community Cards             |
| `$ poker showdown`    | Compare cards and declare winner |
| `$ poker score board` | Show current chip standings      |
| `$ poker stack Bob`   | Show Bob's current balance       |
| `$ poker next hand`   | Start a new hand                 |
| `$ poker ID`          | Get current game ID              |

[maven]: https://maven.apache.org/

[just]: https://github.com/casey/just
