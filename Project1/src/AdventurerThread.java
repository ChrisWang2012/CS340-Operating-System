import java.util.concurrent.ThreadLocalRandom;

public class AdventurerThread implements Runnable {

	private int id;

	private int precious_stone;
	private int necklace;
	private int earring;
	private int ring;
	private int fortune_size;

	private boolean need_assistance;

	private boolean finish_battle;
	private boolean win_dragon;

	public AdventurerThread(int id) {//initial the thread
		this.id = id;//to get the id
		randomGetItem();//when adventurer thread is created, get each item randomly
	}

	public void run() {
		while (this.fortune_size < Main.fortune_size) {// not enough fortune, either go to shop or go to battle
			if (((necklace > 0 || ring > 0) && precious_stone > 0) || (precious_stone > 1 && earring > 1)) {//has enough item, go to shop
				goToJewelShop();
			} else {//not enough items, go to battle
				goToBattle();
			}
		}
		leaveForHome();// get enough fortune, leave for home
	}

	public void goToBattle() {
		msg("Arrives cave and waits for fighting with Dragon.");
		finish_battle = false;
		win_dragon = false;
		Main.greenDragon.addToWaitlist(this);//add this adventurer thread to wait list
		while (true) {
			try {
				Thread.sleep(1000000);//sleep a long time, wait for the dragon to interrupt
			} catch (InterruptedException e) {//if interrupted fight with dragon
				msg(" is interrupted by Dragon.");
				figintDragon();
				break;
			}
		}

		while (!finish_battle) {//if battle not complete, busy waiting for the battle to finish
			Thread.yield();
		}

		if (!win_dragon) {//if lose to the dragon, fight again
			figintDragon();
		}

	}

	private void figintDragon() {// call the method to send this current adventurer to fight with the running dragon in main 
		Main.greenDragon.fightWithAdventurer(this);
	}

	private void goToJewelShop() {
		spentRandomTime(1000);//wait at the front door of the shop
		msg("Arrives JewelerShop and waits for assistance. Current fortune size is " + fortune_size
				+ ", (precious stone: " + precious_stone + ", Ring: " + ring + ", Necklace: " + necklace + ", Earring: "
				+ earring + ")");//check current fortune and item status
		need_assistance = true;
		JewelryShopThread.addToQueue(this);//put this adventurer in the queue
		while (need_assistance) {//busy wait
			Thread.yield();
		}
		msg("has new fortunes. Fortune size is " + fortune_size);//check the updated fortune size
	}

	public void leaveForHome() {
		spentRandomTime(1000);
		msg(" has enough fortune and ready to leave.");
		if (id < (Main.num_adv - 1)) {//if this adventurer does not has the largest id
			Thread advThread = Main.adv_threads[id + 1];//find the next id
			if (advThread.isAlive()) {// if that id is still fighting or shopping
				try {
					msg("join thread " + (id + 1));
					advThread.join();//join him, wait for him to leave together
				} catch (InterruptedException e) {//should not come to this step
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (id == 0) {//the last one that leaves give the dragon a signal, let it go home/terminate
			Main.dragon_terminate = true;
		}
		msg("left and terminated!");
	}

	private void randomGetItem() {//get random number(0-3) of each kind of item
		getRandomStone();
		getRandomNecklace();
		getRandomEarring();
		getRandomRing();
	}

	public void addFortune() {//add fortune number
		this.fortune_size++;
	}

	public int getRandomStone() {
		this.precious_stone = randomInt(0, 3);
		return precious_stone;
	}

	public int getRandomNecklace() {
		this.necklace = randomInt(0, 3);
		return necklace;
	}

	public int getRandomEarring() {
		this.earring = randomInt(0, 3);
		return earring;
	}

	public int getRandomRing() {
		this.ring = randomInt(0, 3);
		return ring;
	}

	public void setNeedAssistance(boolean value) {
		this.need_assistance = value;
	}

	public int randomInt(int min, int max) {//random integer formula
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}

	private void spentRandomTime(int max) {//sleep random time
		try {
			Thread.yield();
			Thread.sleep(randomInt(0, max * 10));
		} catch (InterruptedException e) {//should not come  to this step 
			msg("Error: spentRandomTime was interrupted by unknown reason.");
			e.printStackTrace();
		}
	}
//Basic getter and setter method for items and battle booleans below
	public int getPrecious_stone() {
		return precious_stone;
	}

	public void setPrecious_stone(int precious_stone) {
		this.precious_stone = precious_stone;
	}

	public int getNecklace() {
		return necklace;
	}

	public void setNecklace(int neckless) {
		this.necklace = neckless;
	}

	public int getEarring() {
		return earring;
	}

	public void setEarring(int earring) {
		this.earring = earring;
	}

	public int getRing() {
		return ring;
	}

	public void setRing(int ring) {
		this.ring = ring;
	}

	public void setFinish_battle(boolean finish_battle) {
		this.finish_battle = finish_battle;
	}

	public void setWin_dragon(boolean win_dragon) {
		this.win_dragon = win_dragon;
	}

	public int getId() {//to get name
		return this.id;
	}

	public String getName() {//to get name
		return "AdventurerThread_" + this.id;
	}

	public void msg(String m) {//to print message
		System.out.println("[" + (System.currentTimeMillis() - Main.time) + "]" + getName() + ":" + m);
	}

}
