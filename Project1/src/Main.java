
public class Main {
	public static long time = System.currentTimeMillis();
	public static int num_clerk = 2;
	public static int num_adv = 6;
	public static int fortune_size = 5;

	public static Thread[] adv_threads = new Thread[num_adv];//create adventurer thread list to use

	public static Thread[] jewelryShop_threads = new Thread[num_clerk];//create clerk thread list to use

	public static GreenDragonThread greenDragon;//only one dragon run, use the default class
	public static Thread greenDragonThread;//create dragon thread to use
	
	public static boolean jewel_terminate = false;//signal clerk to terminate
	
	public static boolean dragon_terminate = false;//signal dragon to terminate

	public static void main(String[] args) {
		Main main = new Main();
		main.msg("Starting GreenDragon thread ...");
		greenDragon = new GreenDragonThread();//create a new object of GreenDragonThread class
		greenDragonThread = new Thread(greenDragon);//create a new dragon thread use the object created
		greenDragonThread.start();//run the dragon thread

		main.msg("Starting JewelryShop threads ...");
		for (int i = 0; i < num_clerk; i++) {
			jewelryShop_threads[i] = new Thread(new JewelryShopThread(i));//there are 2 clerks, create two clerk thread to run
			jewelryShop_threads[i].start();//run
		}

		main.msg("Starting Adventurer threads ...");
		for (int i = 0; i < num_adv; i++) {
			adv_threads[i] = new Thread(new AdventurerThread(i));//there are 6 adventurers, create 6 adventurer threads to run
			adv_threads[i].start();//run
		}

	}

	public void msg(String m) {//print method
		System.out.println("[" + (System.currentTimeMillis() - time) + ")" + getName() + ": " + m);

	}

	public String getName() {//get name
		return "Main";
	}

}
