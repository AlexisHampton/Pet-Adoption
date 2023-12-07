import java.util.concurrent.atomic.AtomicInteger;

//The Cashier is a thread that checks Customers out of the store and leaves when there are no more customers
//Last modified time: 11/21/23 12:08
//Author: Alexis Hampton
public class Cashier implements Runnable {

    private String name = "";
    private Thread thread;

    //the amount of Cashiers currently working
    //This stops 3 cashiers from working on 2 customers
    private static AtomicInteger numCashiersWorking = new AtomicInteger();

    public static long time = System.currentTimeMillis();

    //Creates  customer with a given index and starts the thread
    public Cashier(int num) {
        name = "Cashier " + num;
        this.thread = new Thread(this, name);
        msg("Initializing Cashier");
        thread.start();
    }

    //prints a message with a given string and the time since the beginning of the program
    public void msg(String m) {
        System.out.println("[" + (System.currentTimeMillis() - time) + "] " + thread.getName() + ": " + m);
    }

    //The run method that runs when the thread begins and services each customer on the line until it can leave

    @Override
    public void run() {
        //don't know when customers will be here
        while (true) {
            //checks if there are enough cashiers for the amount of customers, or if the line is empty and the cashiers can't leave
            while (numCashiersWorking.get() >= AdoptionClerk.cashierLine.size() || (AdoptionClerk.cashierLine.isEmpty() && !AdoptionClerk.cashiersCanLeave)) {
                if (AdoptionClerk.cashiersCanLeave) {
                    Leave();
                    return;
                }
            }
            numCashiersWorking.incrementAndGet();
            if (!AdoptionClerk.cashierLine.isEmpty()) {
                //no for loop because it didn't work, everyone would have the same i value
                //so if only the first customer is removed, then there won't be any issues accessing a customer,
                //except for the times it doesn't work, of course
                Customer cust = AdoptionClerk.cashierLine.remove(0);
                msg("Assisting Customer " + cust.getName());
                cust.isWaitingAtCashier.set(false);
                msg("No longer assisting " + cust.getName());
            }
            numCashiersWorking.decrementAndGet();

        }

    }

    //Leaves the shop and reports to the AdoptionClerk
    public void Leave() {
            msg("Leaving the shop");
            AdoptionClerk.CashierLeaves();



    }

}
