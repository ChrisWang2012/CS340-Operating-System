import java.util.ArrayList;
import java.util.List;

public class JewelryShopThread implements Runnable {

	private int id;

	private static List<AdventurerThread> queue = new ArrayList<AdventurerThread>();//create a list for adventurer to get in the queue

	private static Object lock = new Object();//create lock for mutual exclusion

	public JewelryShopThread(int id) {//to get id
		this.id = id;
	}

	public void run() {
		msg("Start ...");
		while (!Main.jewel_terminate) {
			AdventurerThread adv = null;
			synchronized (lock) {//if one adventurer come in, others would be locked
				if (!queue.isEmpty()) {//there are adventurer waiting
					adv = popAndRemoveFromQueue();//call the adventurer on line 
				}
			}
			if (adv == null) {//if no adventurer come to the shop
				Thread.yield();//busy wait
				continue;
			}
			assistAdventurer(adv);//serve the adventurer
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
		adv.setNeedAssistance(false);//set boolean back, this adventurer does not need assistance any more

	}

	public synchronized static void addToQueue(AdventurerThread adv) {//add to queue method
		queue.add(adv);
	}

	public synchronized static AdventurerThread popAndRemoveFromQueue() {//remove from queue method
		if (queue.isEmpty()) {//if the queue is empty
			return null;
		}
		AdventurerThread adv = queue.get(0);//get the first one in the queue
		queue.remove(0);//remove that one in the queue
		return adv;
	}

	public String getName() {//get name
		return "JewelryShopThread_" + id;
	}

	public void msg(String m) {//print method
		System.out.println("[" + (System.currentTimeMillis() - Main.time) + "]" + getName() + ":" + m);
	}

}
