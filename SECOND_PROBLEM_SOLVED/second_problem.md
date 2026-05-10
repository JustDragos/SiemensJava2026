
# Problem 2: Asynchronous Matchmaking Logic

## Problem Definition

In modern multiplayer gaming, matchmaking servers handle thousands of concurrent "join" requests. A fundamental challenge is the **Threshold Problem**: a match must not begin until exactly **N** players are ready. In an asynchronous environment, standard logic often fails due to:

* **Race Conditions:** Concurrent updates to the player count can lead to over-filled lobbies or "ghost" players.
* **Resource Inefficiency:** "Busy-waiting" (looping to check player counts) consumes excessive CPU cycles and scaling costs.

## The Solution: Synchronized Barriers

The implementation uses a **CyclicBarrier**. Unlike a standard semaphore used for limiting access, a barrier acts as a rendezvous point. It forces threads to pause and wait for their peers. Once the **Nth** player arrives, the barrier "trips," releasing all threads simultaneously to initialize the game session.

## Programmatic Implementation (Java / WebMVC)

```java
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

@RestController
public class MatchmakingController {

    private final int TEAM_SIZE = 10;
    
    private final CyclicBarrier matchBarrier = new CyclicBarrier(TEAM_SIZE, () -> {
        System.out.println("Threshold reached. Initializing game server...");
    });

    @PostMapping("/join-match")
    public String joinMatch() {
        try {
            int arrivalIndex = matchBarrier.await();
            
            if (arrivalIndex == 0) {
                return "Match Started! You were the final player.";
            } else {
                return "Match Started! You were player number " + (TEAM_SIZE - arrivalIndex);
            }

        } catch (InterruptedException | BrokenBarrierException e) {
            return "Matchmaking failed: Lobby was dissolved.";
        }
    }
}

## Why This Solution is Effective

* **Zero CPU Overhead:** Threads are suspended while waiting, eliminating the cost of polling.
* **Guaranteed Atomicity:** The `CyclicBarrier` handles internal counters thread-safely, ensuring exactly **N** players proceed.
* **Automatic Reset:** The "Cyclic" nature ensures the barrier resets immediately for the next 10 players, requiring no cleanup code.