import java.util.ArrayList;

//Initializes all of the threads
//Last modified time: 11/21/23 12:08
//Author: Alexis Hampton
public class Main {

    static int numCustomers = 20;
    static int numCashiers = 3;
     static int numVisitors = 3;
     static int numPets = 12;

    private static ArrayList<Customer> customers = new ArrayList<>();
    private static  ArrayList<Cashier> cashiers = new ArrayList<>();

    //Initializes the adoptionClerk, the customers and the cashiers
    public static void main(String[] args) {
        AdoptionClerk adoptionClerk = new AdoptionClerk(numCustomers, numCashiers, numPets, numVisitors);

        for(int i = 0; i < numCustomers; i++)
            customers.add(new Customer(i));
        for(int i = 0; i < numCashiers; i++)
            cashiers.add(new Cashier(i));

    }

}
