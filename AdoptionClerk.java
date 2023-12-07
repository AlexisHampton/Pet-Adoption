import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

//AdoptionClerk is a thread that manages the store, tells the cashiers to leave and lets customers adopt pets if there are any
//Last modified time: 11/21/23 12:08
//Author: Alexis Hampton
public class AdoptionClerk implements Runnable {

    private String name = "Adoption Clerk";
    private static Thread thread;

    static AtomicInteger numCustomers = new AtomicInteger();
    static AtomicInteger numCashiers = new AtomicInteger();
    static AtomicInteger numVisitors = new AtomicInteger();
    static AtomicInteger numPets = new AtomicInteger();

    //since there's only one AdoptionClerk and these bools are only set here, they don't need to be synchronized
    public static boolean cashiersCanLeave = false;
    private static boolean adoptionClerkCanLeave = false;

    //the lines for operation
    public static Vector<Customer> cashierLine = new Vector<>();
    public static Vector<Customer> petAdoptionLine = new Vector<>();
    public static Vector<Customer> adoptedCustomers = new Vector<>();

    public static long time = System.currentTimeMillis();

    //creates an AdoptionClerk with a given number of customers, cashiers, pets and vistors and starts the thread
    public AdoptionClerk(int nCustomers, int nCashiers, int nPets, int nVisitors) {
        numCustomers.set(nCustomers);
        numCashiers.set(nCashiers);
        numPets.set(nPets);
        numVisitors.set(nVisitors);
        thread = new Thread(this, name);
        thread.start();
    }

    //prints a string that includes the time since instantiated and the message given
    public static void msg(String m) {
        System.out.println("[" + (System.currentTimeMillis() - time) + "] " + thread.getName() + ": " + m);
    }

    //checks if the number of visitors is less than or equal to the max amount, and if there are pets still to adopt
    public static boolean CanCustomersComeIn() {
        return numVisitors.get() > 0 && numPets.get() > 0;
    }

    //the method that makes the AdoptionClerk go to work
    @Override
    public void run() {
        //an endless loop because it needs to be alive until all customers leave
        while (true) {
            //if there are no pets, leave
            while (numPets.get() <= 0) {
                NoMorePets();
                if (adoptionClerkCanLeave)
                    return;
            }
            //checks if customers are on the line and if they can come in
            while (petAdoptionLine.size() == 0 && !CanCustomersComeIn()) {
                if (numPets.get() <= 0)
                    NoMorePets();
                if (adoptionClerkCanLeave)
                    return;
            }

            numVisitors.decrementAndGet();

            for (int i = 0; i < petAdoptionLine.size(); i++) {
                petAdoptionLine.get(i).Interrupt();
                Customer customer = petAdoptionLine.remove(0);
                msg(customer.getName() + " can go in the pet room");
                customer.isWaitingForPetAdoption.set(false);
            }
        }
    }


    //in the event that there are no more pets and customers are waiting on the line to adopt them,
    //they will be taken off the line and will leave the store
    private static void NoMorePets() {
        for (int i = 0; i < petAdoptionLine.size(); i++) {
            Customer customer = petAdoptionLine.remove(i);
            customer.isWaitingForPetAdoption.set(false);
            customer.noMorePets.set(true);
            customer.NoMorePets();
        }
    }

    //in the event that there are no more pets and customers have finished waiting on the line but were not fast enough,
    //they will leave the store empty-handed
    private static void NoMorePets(Customer customer) {
        customer.noMorePets.set(true);
        customer.NoMorePets();
    }

    //A customer will adopt a pet and exit the pet area if there are still pets
    public static void AdoptPet(Customer customer) {
        if (numPets.get() <= 0) {
            NoMorePets(customer);
            return;
        }
        numPets.decrementAndGet();
        numVisitors.incrementAndGet();
        adoptedCustomers.add(customer);
    }

    //Adopted Customers will leave the adopted customers line if they are done adopting pets
    public static void AdoptedCustomersLeave() {
        for (int i = adoptedCustomers.size() - 1; i < -1; i--) {
            Customer cust = adoptedCustomers.remove(i);
        }
    }

    //Customers let the AdoptionClerk know when they leave, and if all gone, then the Cashiers will begin to leave
    public static void CustomerLeaves() {
        msg("Remaining Customers: " + numCustomers.decrementAndGet());
        if (numCustomers.get() <= 0)
            cashiersCanLeave = true;
    }

    //Cashiers let the AdoptionClerk know when they leave, and if all gone, then the adoption clerk can leave
    public static void CashierLeaves() {
        if (numCashiers.decrementAndGet() <= 0) {
            Leave();
        }
    }


    //Adoption clerk will leave when the while loop runs again
    public static void Leave() {
        adoptionClerkCanLeave = true;
        msg("Leaving");
    }


}