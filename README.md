# Pet-Adoption
This project synchronizes threads using AtomicIntegers, AtomicBooleans, and busy waits. This is a project I did for my Operating Systems class. 

It doesn't work perfectly. Sometimes a Customer will not leave the cashier line and will busy wait forever. Or sometimes the Cashiers will try to reference a customer that doesn't exist anymore because the other cashiers helped them first. 

But it runs correctly most of the time.
