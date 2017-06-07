
public class Main {
	public static long time = System.currentTimeMillis();
	public static int num_clerk = 2;
	public static int num_adv = 8;
	public static int fortune_size = 3;
	public static int num_table = 3;

	public static Thread[] adv_threads = null;//create adventurer thread list to use

	public static Thread[] jewelryShop_threads = null;//create clerk thread list to use

	public static GreenDragonThread greenDragon;//only one dragon run, use the default class
	public static Thread greenDragonThread;//create dragon thread to use
	
	public static boolean jewel_terminate = false;//signal clerk to terminate
	
	public static boolean dragon_terminate = false;//signal dragon to terminate

	public static void main(String[] args) {
		if (args.length == 4) {//command line arguments
			num_adv = Integer.parseInt(args[0]);
			num_clerk = Integer.parseInt(args[1]);
			fortune_size = Integer.parseInt(args[2]);
			num_table = Integer.parseInt(args[3]);
		} else {//use default
			msg("Usage: java Main [num_adv] [num_clerk] [fortune_size] [num_table]");
		}
		msg("num_adv=" + num_adv + ", num_clerk=" + num_clerk + ", fortune_size=" + fortune_size + ", num_table=" + num_table);
		adv_threads = new Thread[num_adv];
		jewelryShop_threads = new Thread[num_clerk];
		msg("Starting GreenDragon thread ...");
		greenDragon = new GreenDragonThread();//create a new object of GreenDragonThread class
		greenDragonThread = new Thread(greenDragon);//create a new dragon thread use the object created
		greenDragonThread.start();//run the dragon thread

		msg("Starting JewelryShop threads ...");
		for (int i = 0; i < num_clerk; i++) {
			jewelryShop_threads[i] = new Thread(new JewelryShopThread(i));//there are ? clerks, create ? clerk thread to run
			jewelryShop_threads[i].start();//run
		}

		msg("Starting Adventurer threads ...");
		for (int i = 0; i < num_adv; i++) {
			adv_threads[i] = new Thread(new AdventurerThread(i));//there are ? adventurers, create ? adventurer threads to run
			adv_threads[i].start();//run
		}

	}

	public static void msg(String m) {//print method
		System.out.println("[" + (System.currentTimeMillis() - time) + "]" + getName() + ": " + m);

	}

	public static String getName() {//get name
		return "Main";
	}

}
