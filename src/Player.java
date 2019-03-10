import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Thread.*;

public class Player implements Runnable {

    private GameMaster GM;

    Player(GameMaster GM){

        this.GM = GM;
    }

    // Tries to put termination query to the queue
    private void shoot(){
        // Gets the random Thread Id from the list of alive threads
        Thread target = GM.players.get(ThreadLocalRandom.current().nextInt(0, GM.players.size()));
        // Attempts to kill it
        //GM.registerShoot(new Thread[]{currentThread(), target});
        GM.shots.add(new Thread[]{currentThread(), target});
    }

    //Sleeps for a random amount of time, pretending that it is reloading.
    private void reload() throws InterruptedException {
        sleep(ThreadLocalRandom.current().nextInt(50, 100));
    }

    public void run(){
        while (GM.isGameContinues()){
            try {
                shoot();
                reload();

                if(interrupted()){
                    throw new InterruptedException("You have been killed.");
                }
            }
            catch (InterruptedException e) {
                //System.out.println(currentThread().getName() + ": I have been killed.");
                return;
            }
        }
        //when the match is finished do nothing to show that you are alive
        try{
            currentThread().wait();
        }
        catch (InterruptedException e){
            return;
        }
    }
}
