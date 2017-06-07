import java.util.Vector;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

public class JewelryShopThread implements Runnable {

	private int id;
    public static Semaphore clerk = new Semaphore(Main.num_clerk);//prepare clerk semaphores to use
    public static Semaphore waitingAdv = new Semaphore(0);//create semaphore to decide execution
    private static Semaphore mutex = new Semaphore(1);//create semaphore for mutual exclusion use
    private static Vector<AdventurerThread> adv_shoppinglist = new Vector<AdventurerThread>();//create a container for clerk to choose whom to serve if there's adventurer waiting

      
	public JewelryShopThread(int id) {//initial thread with id
		this.id = id;
	}

	public void run() {
		msg("Start ...");
		while (!Main.jewel_terminate) {           
			try {
				AdventurerThread adv = null;//create adventurer to be assigned
				waitingAdv.acquire();//wait for adventurer or dragon signal
				mutex.acquire();//ensures that the clerk only serve one adventurer
				if (adv_shoppinglist.isEmpty()) {//if nobody comes, unlock me 
					mutex.release();
				} else {
					if (adv_shoppinglist.size() == 1) {//if there's only one adventurer wait to be served
						adv = adv_shoppinglist.remove(0);//the clerk directly choose to serve the adventurer(remove it from waiting list)
					} else {//if more than one adventurer is waiting
						int rand = ThreadLocalRandom.current().nextInt(0, adv_shoppinglist.size());//randomly pick an adventurer from waiting list
						adv = adv_shoppinglist.remove(rand);//remove that adventurer from waiting list
					}		
					mutex.release();
					// serve adventurer
					assistAdventurer(adv);
					adv.canContinue.release();//enable this adventurer to continue his execution
				}
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		msg("Terminiated!");//clerk terminate
	 
	}


	private void assistAdventurer(AdventurerThread adv) {
		msg("is assisting " + adv.getName());
		while ((((adv.getNecklace() > 0 || adv.getRing() > 0) && adv.getPrecious_stone() > 0)
				|| (adv.getPrecious_stone() > 1 && adv.getEarring() > 1))) {//if get enough items to make magical item
			if ((adv.getPrecious_stone() > 1 && adv.getEarring() > 1)) {//if has enough stones and earrings,
				adv.setEarring(adv.getEarring() -2);//use the earrings
				adv.setPrecious_stone(adv.getPrecious_stone() - 2);//use the stones
				adv.addFortune();//make a pair of magical earrings
			}
			if (adv.getPrecious_stone() > 0) {//has stone
				if (adv.getNecklace() > 0) {//if also has necklace
					adv.setPrecious_stone(adv.getPrecious_stone() - 1);//use the stone
					adv.setNecklace(adv.getNecklace() -1);//use necklace
					adv.addFortune();//make a fortune
				} else if (adv.getRing() > 0) {//if also has ring
					adv.setPrecious_stone(adv.getPrecious_stone() - 1);//use the stone
					adv.setRing(adv.getRing() -1);//use the ring
					adv.addFortune();//make a fortune
				}
			}
		}
	}

	public static void addToShoppinglist(AdventurerThread adv) {//add to list method
		adv_shoppinglist.add(adv);
	}

	
	public String getName() {//get name
		return "JewelryShopThread_" + id;
	}

	public void msg(String m) {//print method
		System.out.println("[" + (System.currentTimeMillis() - Main.time) + "]" + getName() + ":" + m);
	}

}
