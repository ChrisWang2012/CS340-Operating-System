import java.util.Vector;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

public class GreenDragonThread implements Runnable {

	public Semaphore seats = new Semaphore(Main.num_table);//create counting semaphore for adventurer
	public Semaphore waitingAdv = new Semaphore(0);//create semaphore to decide execution
	private Vector<AdventurerThread> adv_waitinglist = new Vector<AdventurerThread>();//create a container for dragon to choose whom to battle if there's adventurer waiting

	public void run() {
		msg("start...");
		while (!Main.dragon_terminate) {// not finish
			try {
				waitingAdv.acquire();//wait until there's adventurer signal the dragon
				if (adv_waitinglist.isEmpty()) {//if no adventurer come to cave
					continue;
				}
				AdventurerThread adv = adv_waitinglist.remove(0);//start battle with the waiting adventurer
				if (!diceBattle()) {//if adventurer win
					adv.winTimes ++;// win time + 1
				}
				adv.roundOfFight ++;// fight time + 1
				msg(adv.getName() + " fights dragon (" + adv.roundOfFight + ") times");
				if (adv.roundOfFight == 3) {//if the dragon has fight with the adventurer for 3 times
					if (adv.winTimes >= 2) { // if win time equal or greater than 2, adventurer win
						giveItem(adv);//dragon randomly gives item
					} else {
						msg(" Dragon wins. No item for " + adv.getName());
					}
					adv.roundOfFight = 0;//set the fight time back to 0 for later use
					adv.winTimes = 0;//set win time back to 0 for later use
					adv.canContinue.release();// say"you can go"to adventurer,let him continue execution
					seats.release();//release a seat for other waiting adventurer
				} else {//dragon fought with the adventurer, but less than 3 round
					adv_waitinglist.add(adv);//add this adventurer to the back of the list for round robin purpose
					waitingAdv.release();//release the lock,ensures that dragon can continue execution(avoid Dead Lock)
				}
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		Main.jewel_terminate = true;//dragon finish the job, tell the clerk to terminate
		JewelryShopThread.waitingAdv.release(Main.num_clerk);//ensures that clerks will check the while statement and go home!
		msg("Terminate!");//dragon terminate
	}

	private void giveItem(AdventurerThread adv) {
		msg("Dragon loss. Give an item to " + adv.getName());
		int rand = ThreadLocalRandom.current().nextInt(1, 5);//create random integer to use
		switch (rand) {//dragon randomly gives an item
		case 1:
			msg(adv.getName() + " get Necklace");
			adv.setNecklace(adv.getNecklace() + 1);//adventurer get a necklace
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
	}

	private boolean diceBattle() {
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
