import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class GreenDragonThread implements Runnable {

	private Vector<AdventurerThread> adv_waitinglist = new Vector<AdventurerThread>();//create a container for dragon to choose whom to battle if there's adventurer waiting

	public void run() {
		msg("start...");
		while (!Main.dragon_terminate) {// not finish
			if (adv_waitinglist.isEmpty()) {//if no adventurer come to cave
				Thread.yield();//busy wait
				try {
					Thread.sleep(1000);//give dragon some prepare time, don't want to fight with adventurers without rest
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				AdventurerThread adv = null;
				if (adv_waitinglist.size() == 1) {//if there's only one adventurer wait to fight
					adv = adv_waitinglist.remove(0);//the dragon with directly choose with the adventurer(remove it from waiting list)
				} else {//if not
					int rand = ThreadLocalRandom.current().nextInt(0, adv_waitinglist.size());//randomly pick an adventurer from waiting list
					adv = adv_waitinglist.remove(rand);//remove that adventurer from waiting list
				}
				msg("interruptes " + adv.getName());
				Thread advThread = Main.adv_threads[adv.getId()];//get the adventurer thread with the chosen id
				advThread.interrupt();//interrupt him and fight with him
			}
		}
		Main.jewel_terminate = true;//dragon finish the job, tell the clerk to terminate
		msg("Terminate!");//dragon terminate
	}

	public synchronized void fightWithAdventurer(AdventurerThread adv) {
		msg(" fights with " + adv.getName());
		Thread advThread = Main.adv_threads[adv.getId()];//get the adventurer thread with the chosen id
		if (diceBattle()) {//if dragon wins
			adv.setWin_dragon(false);//set adventurer not win 
			if (advThread.getPriority() == Thread.MAX_PRIORITY) {//check if the adventurer has already got the max priority
				msg("Dragon wins second time. " + adv.getName() + " has to wait in the queue.");
				advThread.setPriority(Thread.NORM_PRIORITY);//set the priority back and wait to fight again
			} else {//adventurer has normal priority, he loss the first battle
				msg("Dragon wins. It's happy and gives " + adv.getName() + " another chance.");
				advThread.setPriority(Thread.MAX_PRIORITY);//set this adventurer's priority to max
			}
		} else {//adventurer wins
			msg("Dragon loss. Give an item to " + adv.getName());
			int rand = ThreadLocalRandom.current().nextInt(1, 5);//create random integer to use
			switch (rand) {//dragon randomly gives an item
			case 1:
				msg(adv.getName() + " get Neckless");
				adv.setNecklace(adv.getNecklace() + 1);//adventurer get a neckless
				break;
			case 2:
				msg(adv.getName() + " get Ring");
				adv.setRing(adv.getRing() + 1);//adventurer get a ring
				break;
			case 3:
				msg(adv.getName() + " get Earring");
				adv.setEarring(adv.getEarring() + 1);//adventurer get an earring
				break;
			case 4:
				msg(adv.getName() + " get Stone");
				adv.setPrecious_stone(adv.getPrecious_stone() + 1);//adventurer get a stone
				break;
			default://should not come to this step
				msg("Error number: " + rand + ". No item for " + adv.getName());
				break;
			}
			adv.setWin_dragon(true);//set adventurer win
			if (advThread.getPriority() != Thread.NORM_PRIORITY) {//if the adventurer has max priority, set it back to normal
				advThread.setPriority(Thread.NORM_PRIORITY);
			}
		}
		adv.setFinish_battle(true);//set finish battle
	}

	private boolean diceBattle() {
		int dragonDice = ThreadLocalRandom.current().nextInt(1, 7);//dragon roll a number from 1 to 6
		int advDice = ThreadLocalRandom.current().nextInt(1, 7);//adventurer roll a number from 1 to 6
		return dragonDice > advDice;//if dragon win, return true
	}

	public void addToWaitlist(AdventurerThread adv) {//add to list method
		this.adv_waitinglist.add(adv);
	}

	public String getName() {//get name
		return "GreenDragonThread";
	}

	public void msg(String m) {//print method
		System.out.println("[" + (System.currentTimeMillis() - Main.time) + "]" + getName() + ":" + m);
	}
}
