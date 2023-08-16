[mancala]: https://en.wikipedia.org/wiki/Mancala
[game_manager]: src/main/java/com/fun/mancala/application/GameManager.java
[onion_arch]: https://jeffreypalermo.com/2008/07/the-onion-architecture-part-1/
[rfc_7807]: https://datatracker.ietf.org/doc/html/rfc7807
[htmx]: https://htmx.org/

# Mancala

This very simple app tries to use the "clean X" approach pioneered by uncle Bob Martin, but takes more heavily to heart the
"Effective Software Testing" book, as it is approached in a TDD fashion to provide a backend for the game of [Mancala][mancala].
It uses Java as language and Spring Boot as framework for quick prototyping.

It is an extremely simple app, intended as proof of concept, that has the ability of handling two users playing, by making
simple requests.

## Starting the service

As a Spring Boot application, the rules of starting the service from the documentation applies. For any Linux or MacOS computer,
you just need to run

```shell
./mvnw spring-boot:run
```

in order to start the service. In Windows, the command looks like:

```shell
mvnw.cmd spring-boot:run
```

## Design

By following [Jeffrey Palermo's description of the Onion Architecture][onion_arch], this service has three main packages:
Application, Domain, and Infrastructure (infra). You can find more information about them in the blog post, but the simplified
version is that the *Application* layer contains all business logic, the *Domain* contains all business domain objects, and the
*Infrastructure* contains the application-specific interfaces into a particular technology, e.g., Spring's `Rest Controllers`.
The Application and Domain layers should contain *ports* that helps to connect to the outside world via implementations in the
Infrastructure layer.

The simple backend doesn't store any data, it keeps one game in memory in the [`GameManager` class][game_manager], that needs
to be initialized before start playing. Then, all moves should be made by players (it does not matter which player, but
the rules on whose turn is it determines which pits can be played), until the game ends, in which case an end state will be
provided. At all times the game state is accessible and in order to restart the board (once finished or mid-game) an endpoint
is also provided for that.

> Requests are provided in the [`game-requests.http` file](game-requests.http)

The business rules are located in the aforementioned `GameManager` class, which is annotated as a Spring `@Service` stereotype
that acts as the "port -> impl" connection previously mentioned. All exceptions are also described in the application layer only.

> As a point of improvement, as the [RFC-7807][rfc_7807] is used in this service, domain-based exceptions should be used and
> the application layer's should be a "problem-describing" exception instead of using the exception itself as the problem description.

The domain models are simple enough to be used in this PoC, however, these can be extended or packaged into other domains if necessary.

> A second iteration of this simple app, may include a `Game` domain that packages the board, players, etc., to make a more accurate
> description of the system state.

The infrastructure implementations are just all `@RestControllers` or `@RestControllerAdvice` stereotypes that, by the use of
Spring's other annotations, like `@Service` previously mentioned, dependency injection and clean cut concerns are achieved.
The reason why there is a "one-endpoint-one-controller class" approach, is because of the evolvability of the service, so not
many refactors are needed in case that a controller endpoint changes or is removed.

## Rules of the app

> Definitions:
>
> - **Base Pit (BP):** pit with zero stones in the beginning. The user cannot move the stones from here.
> - **Player Pit (PP):** pit with stones, that the player can pick and distribute in the next pits.


1. The game should be first initialized via the `/initialize` endpoint by passing the initial array state, e.g., `[1, 1, 0, 1, 1, 0]`.
    - The minimum board size is 6, two PPs plus a BP for each player.
    - The board size should always be even, and the BPs should be in the middle and at the end of the array.
    - The amount of stones per pit is between 1 and 10 (why 10? no particular reason, just seemed reasonable :sweat_smile:).
      > Calling any other endpoint here throws a `BoardNotInitializedException`
2. From here on, three endpoints are available, `/move`, `/status`, and `/clear`.
3. The `/move` endpoint allows anyone sending requests to the service to move the stones. This endpoint receives a zero-based pit number
   (zero-based because it is easier to use :sweat_smile:) to move the stones in it. Depending on which "player" turn is, the `GameManager`
   class will allow the movement or not.
    - If it is *Player One's* turn, only the pits from 0 to Player One's BP (non-including) are playable.
    - If it is *Player Two's* turn, only the pits from Player One's BP plus one to Player Two's BP (non-including) are playable.
    - If the indicated pit to move is void of stones, you will get an error.
    - If the last stone lands on an own empty PP, *capturing* happens, where all the stones on the opposite PP plus the one that landed
      on the empty one ends up in the current player's BP.
    - If the last stone ends up in your own BP, you get another turn.
      > *Capturing* does not grant another turn.
    - At all points the `/status` endpoint can be called to check what's the status of the current game: whose turn is it, what's the board
      status, and what's the score, as well if the game is still playable or has ended.
4. The game ends once one side is devoid of stones. This will make the `/move` endpoint to throw errors indicating who won. The final score
   can be read by calling the `/status` endpoint, which will provide the status of the game, who won, and the score, as well as the board status.
5. In order to play the game again, the `/clear` endpoint should be called, which indicates the `GameManager` class that the board should be
   cleaned, and then proceed to step 1 again.

## Improvement points

Some of them were already listed above, but the summary is:

- Domain-based exceptions should be used and the application layer's should be a "problem-describing" exception instead of using the
  exception itself as the problem description.
- Include a `Game` domain that packages the board, players, etc., to make a more accurate description of the system state. Also, this
  will allow to extend the game to more players and change board sizes for different players and whatnot.
- Include state persistence logic to allow several users to play this game. This will also allow to make the onion arch approach more
  visible as the repositories and the implementations will show the power of this coding style by demonstrating how easily swappable
  implementations are.
- Include security. You can see in the first commit that Spring's security library was included but was later removed (:sweat_smile:)
  for simplicity's sake. If this layer is added, a JWT provider (better have JWTs!) should be added via this same service, or via a
  docker compose file to deploy an identity provider (or minikube, or kind, or k3s, or k8s, etc., script), or via a free-tier subscription
  service like Auth0, okta, etc.
- Configuration classes. Right now EVERYTHING is hardcoded... but just out of "poc-ness", not because it is hard; but it would be better
  to add configuration classes to define things like default boards, default amount of players, default direction of movement, max amount
  of stones per pit, max amount of stones per player, endgame rules, etc.
- Provide a frontend? This is always a good improvement point. As this is intended to show clean architecture, it is better to use
  [HTMx][htmx] over other frameworks at it provides websocket, AJAX, etc., in a simple and concise way.
