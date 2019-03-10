
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.Thread.sleep;

public class GameMaster{
    // List of threads which haven't been killed yet. Shared resource
    CopyOnWriteArrayList<Thread> players;
    // Queue of threads' attempts to kill each other. Shared resource.
    BlockingQueue<Thread[]> shots;

    // Constructor. Creates runnables and their threads.
    public GameMaster(int playerNumber) {
        players = new CopyOnWriteArrayList();
        createPlayers(playerNumber);
        shots = new LinkedBlockingQueue<>() {
        };
    }

    private void createPlayers(int playerNumber) {
        for (int i = 1; i <= playerNumber; i++){
            this.players.add(new Thread(new Player(this)));
        }
    }

    // Starts threads and the main game loop
    public void startGame(){
        System.out.println("The game begins!");
        for (Thread thread : players){
            thread.start();
        }
        System.out.println("Players are ready!");
        gameLoop();
    }

    // Main loop. checks shot queue until only one thread left alive.
    private void gameLoop() {
        while (isGameContinues()){
            checkNextShot();
        }
        endGame();
    }

    private void endGame() {
        // One player left, match ends.
        Thread winner = this.players.get(0);
        System.out.println("Match complete! The winner is " + winner.getName());
        // Now kill the winner
        winner.interrupt();
    }


    public boolean isGameContinues() {
        //true if more than one players alive
        return (this.players.size() > 1);
    }

//    public boolean registerShoot(Thread[] killerTarget){
//        if (threadAlive(killerTarget[0]) && threadAlive(killerTarget[1])){
//            shots.add(killerTarget);
//            return true;
//        }
//        return false;
//    }

    // Checks shot queue and kill the thread if both killer are alive
    private boolean checkNextShot(){
        Thread[] nextShot = this.shots.poll();
        if(nextShot != null){
            Thread killer = nextShot[0];
            Thread target = nextShot[1];
            if (!threadAlive(killer)){
                System.out.println(killer.getName()
                        + " was already dead when it tried to shoot "
                        + target.getName());
                return false;
            }
            else if(!threadAlive(target)){
                System.out.println(killer.getName()
                        + " tried to shoot "
                        + target.getName()
                        + " but its prey was already dead");
                return false;
            }
            else {
                killThePlayer(target);
                System.out.println(killer.getName()
                        + " killed "
                        + target.getName() + "! "
                        + (target == killer? "Suicide! " : "") +
                        + players.size()
                        + " players left!");
                return true;
            }
        }
        else {
            //System.out.println("No shots registered... weird");
            return false;
        }
    }

    private void killThePlayer(Thread target){
        target.interrupt();
        this.players.remove(target);
    }

    private boolean threadAlive(Thread t){
        return (this.players.contains(t));
    }
}
