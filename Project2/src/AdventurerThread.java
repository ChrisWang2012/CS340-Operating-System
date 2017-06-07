import java.util.Stack;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

public class AdventurerThread implements Runnable {

    private static Semaphore mutex = new Semaphore(1);// for ME
    private static Stack<AdventurerThread> waitingGohomeList = new Stack<AdventurerThread>();//create stack for leving home
    public Semaphore canContinue = new Semaphore(0);//create semaphore for synchronization at the jewelry shop
	private int id;
    private int precious_stone;
	private int necklace;
	private int earring;
	private int ring;
	private int fortune_size;
	
	public int roundOfFight = 0;
	public int winTimes = 0;

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

	private void goToBattle() {
		msg("Arrives cave and waits for fighting with Dragon.");
		try {
			Main.greenDragon.seats.acquire();//try to sit at the table and wait for dragon's signal
			Main.greenDragon.addToWaitlist(this);//if the adventurer has a seat,add this adventurer thread to wait list (for round robin purpose)
			Main.greenDragon.waitingAdv.release();//tells the dragon there is adventurer wait
			canContinue.acquire();// wait to do other things until dragon says:"you can go"
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void goToJewelShop() {
		spentRandomTime(500);//wait at the front door of the shop
		msg("Arrives JewelerShop and waits for assistance. Current fortune size is " + fortune_size
				+ ", (precious stone: " + precious_stone + ", Ring: " + ring + ", Necklace: " + necklace + ", Earring: "
				+ earring + ")");//check current fortune and item status
		JewelryShopThread.addToShoppinglist(this);//add himself to the list waiting for clerk to choose
		JewelryShopThread.waitingAdv.release();//let the clerk know there is adventurer in line
		try {
			canContinue.acquire();//wait to "checkout" until the clerk says:" you can go"
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		msg("has new fortunes. Fortune size is " + fortune_size);//check the updated fortune size
	}


	private void leaveForHome() {
		spentRandomTime(1000);
		msg(" has enough fortune and ready to leave.");
		try {
			mutex.acquire();//ensures only one adventurer can do things on stack
			if (waitingGohomeList.size() == Main.num_adv - 1) {//if this adventurer is the last one
				mutex.release();//unlock semaphore
				AdventurerThread adv = waitingGohomeList.pop();//choose another adventurer from stack
				adv.canContinue.release();//signal him, let him continue execution
			} else {
				waitingGohomeList.push(this);//add this adventurer to the stack
				mutex.release();//unlock semaphore
				canContinue.acquire();//wait until other adventurer to signal
				if (!waitingGohomeList.empty()) {//if there still adventurers on stack
					AdventurerThread adv = waitingGohomeList.pop();//choose another adventurer from stack
					adv.canContinue.release();//signal him, let him continue execution
				} else {// nobody on stack, last person
					Main.dragon_terminate = true;//signal dragon
					Main.greenDragon.waitingAdv.release();//ensures that dragon will check while statement and go home!
				}
			}
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
