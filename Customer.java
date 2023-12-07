import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

//Customer is a thread that buys food and toys, adopts pets, or does both
//Last modified time: 11/21/23 12:08
//Author: Alexis Hampton
public class Customer implements Runnable {

    //Atomic so the value is never incorrect
    public AtomicBoolean isWaitingAtCashier = new AtomicBoolean();
    public AtomicBoolean isWaitingForPetAdoption = new AtomicBoolean();
    public AtomicBoolean noMorePets = new AtomicBoolean();

    private String name;
    private Thread thread;
    private int randomNum;
    private final Random random = new Random();
    public static long time = System.currentTimeMillis();

    //creates a customer with a given number and begins the thread
    public Customer(int num) {
        name = "Customer " + num;
        this.thread = new Thread(this, name);
        msg("Initializing Customer");
        thread.start();
    }

    //prints a message with a given string and time since the beginning of runtime
    public void msg(String m) {
        System.out.println("[" + (System.currentTimeMillis() - time) + "] " + getName() + ": " + m);
    }

    //returns the name of the customer
    public String getName() {
        return name;
    }

    //the run method used to run the thread
    //lets the customer buy food and toys, adopt a pet or do both, and then leave once its done
    @Override
    public void run() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            msg(" has been interrupted"); // never called so not very detailed
        }
        randomNum = random.nextInt(9) + 1;

        msg("received num " + randomNum);
        //the name is changed to reflect what each customer is doing for better understanding and debugging
        if (randomNum < 4) {
            name += " b";
            BuyFoodAndToys();
        } else if (randomNum % 2 == 0) {
            name += " a";
            AdoptPet();
        } else {
            name += " ba";
            BuyFoodAndToys();
            AdoptPet();
        }

        Leave();
    }

    //the customer will browse, join the cashier line and leave the cashier line
    void BuyFoodAndToys() {
        msg("Going to buy toys and food");
        //set to the highest so the thread will rush more
        thread.setPriority(10);
        try {
            Thread.sleep(4000);

        } catch (InterruptedException e) {
            msg(" has been interrupted");
        }
        thread.setPriority(5); //set it back to normal

        msg("Browsing the aisles");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            msg(" has been interrupted");
        }

        msg("Finished browsing and on the cashier line");

        AdoptionClerk.cashierLine.add(this);
        isWaitingAtCashier.set(true);
        msg("Waiting for cashier");

        //busy wait while on the cashier line
        while (isWaitingAtCashier.get()) {
        }
        msg("Checking out ");
        msg("Leaving the line");
    }

    //the customer will wait to be let into the pet adoption area
    //then will go in, look at the pets
    //if they want a pet, they will adopt one, sign forms and leave the adopted customer line
    //if not, they just leave normally
    void AdoptPet() {
        try {
            msg("Waiting to be let into the pet adoption area");
            AdoptionClerk.petAdoptionLine.add(this);
            isWaitingForPetAdoption.set(true);
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            msg("------------------------has been interrupted by AdoptionClerk"); //dashes to make it more obvious
        }

        while (isWaitingForPetAdoption.get()) {
        }

        if(noMorePets.get()) return;

        msg("Checking all the pets in the room");

        AdoptionClerk.petAdoptionLine.remove(this);

        int adopt = random.nextInt(9) + 1;
        if (adopt < 6) {
            msg("Adopting a pet");
            AdoptionClerk.AdoptPet(this);
            if(noMorePets.get()) return;
            msg("Taking a coffee break");
            Thread.yield();
            Thread.yield();
            msg("Filling out forms");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                msg("interrupted");
            }
            AdoptionClerk.AdoptedCustomersLeave();
        }
    }

    //in the event that there are no more pets to adopt, the customer will just leave if waiting to adopt a pet
    public void NoMorePets() {
        msg("No more pets to adopt");
    }

    //the customer leaves, and alerts the adoptionClerk
    void Leave() {
        msg("Leaving the shop");
        AdoptionClerk.CustomerLeaves();
    }


    public void Interrupt() {
        thread.interrupt();
    }


}
