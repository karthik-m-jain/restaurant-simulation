
public class Cook extends Thread{
    private int cookId;  
    private Thread cookThread;
    Diner currentDiner = new Diner();

    public Cook(int cookId)
    {
        this.cookId=cookId;
    }

    public void ReceiveOrder(){
        try {   
            synchronized(Restaurant.waitingForCooks){
                while(Restaurant.waitingForCooks.size()<=0){
                    Restaurant.waitingForCooks.wait();             
                }   
            }                  
                      
            synchronized(Restaurant.cookLock){
                while(Restaurant.cookCount<=0){
                    Restaurant.cookLock.wait();             
                } 
                currentDiner = Restaurant.waitingForCooks.get(0);
                Restaurant.waitingForCooks.remove(0); 
                Restaurant.cookCount --; 
                long timeStamp=(System.currentTimeMillis()-Restaurant.restaurantOpenTime)/1000;
                System.out.println(timeStamp+" Diner "+currentDiner.getDinerId() +"'s order will be processed by Cook "+cookId);                                      
            }  
                      
        } catch (InterruptedException e) {         
        }         
    }

    public void PrepareFood(){
        try {
            int totalItems = currentDiner.getBurgerRequested()+currentDiner.getCokeRequested()+currentDiner.getFriesRequested();                                                 
            while(totalItems>0){

                if(currentDiner.getBurgerRequested()>0){
                    cookBurger(currentDiner);                 
                }
                if(currentDiner.getFriesRequested()>0){
                    cookFries(currentDiner);  
                }
                if(currentDiner.getCokeRequested()>0){
                    cookCoke(currentDiner);  
                }
                totalItems = currentDiner.getBurgerRequested()+currentDiner.getCokeRequested()+currentDiner.getFriesRequested();
                if(totalItems==0){
                    synchronized(currentDiner){
                        currentDiner.notify();
                    }               
                }
            }
        } catch (Exception ex) {        
        }       
    }

    public void cookBurger(Diner currentDiner){
        try {  
            synchronized(Restaurant.burgerMachineLock){
                while(Restaurant.isBurgerMachineBusy==true){
                    Restaurant.burgerMachineLock.wait();             
                } 
                Restaurant.isBurgerMachineBusy=true;
                long timeStamp=(System.currentTimeMillis()-Restaurant.restaurantOpenTime)/1000;
                System.out.println(timeStamp+" Cook "+this.cookId+" takes the machine for Buckeye Burger ");
                Thread.sleep(5000); 
                currentDiner.setBurgerRequested(currentDiner.getBurgerRequested()-1);
                Restaurant.isBurgerMachineBusy=false; 
                Restaurant.burgerMachineLock.notify();               
            }                                         
        } catch (InterruptedException e) {         
        }
    }

    public void cookCoke(Diner currentDiner){
        try {  
            synchronized(Restaurant.cokeMachineLock){
                while(Restaurant.isCokeMachineBusy==true){
                    Restaurant.cokeMachineLock.wait();             
                } 
                Restaurant.isCokeMachineBusy=true;
                long timeStamp=(System.currentTimeMillis()-Restaurant.restaurantOpenTime)/1000;
                System.out.println(timeStamp+" Cook "+ this.cookId+" takes the machine for Coke. ");
                Thread.sleep(1000);  
                currentDiner.setCokeRequested(currentDiner.getCokeRequested()-1);
                Restaurant.isCokeMachineBusy=false; 
                Restaurant.cokeMachineLock.notify();               
            }                                         
        } catch (InterruptedException e) {         
        }
    }

    public void cookFries(Diner currentDiner){
        try {  
            synchronized(Restaurant.friesMachineLock){
                while(Restaurant.isFriesMachineBusy==true){
                    Restaurant.friesMachineLock.wait();             
                } 
                Restaurant.isFriesMachineBusy=true;
                long timeStamp=(System.currentTimeMillis()-Restaurant.restaurantOpenTime)/1000;
                System.out.println(timeStamp+ " Cook "+ this.cookId+" takes the machine for Fries. ");
                Thread.sleep(3000); 
                currentDiner.setFriesRequested(currentDiner.getFriesRequested()-1);
                Restaurant.isFriesMachineBusy=false; 
                Restaurant.friesMachineLock.notify();               
            }                                         
        } catch (InterruptedException e) {         
        }
    }

    public void FinishOrder(){
        synchronized(Restaurant.cookLock){
            Restaurant.cookCount ++;    
            Restaurant.cookLock.notifyAll();         
        }
    }
    
    public void run() {
        try { 
            while(true){
                ReceiveOrder();   
                PrepareFood();  
                FinishOrder(); 
            }                                 
        } catch (Exception ex) {        
        }        
    }

    public void start () {
        cookThread = new Thread (this);
        cookThread.start ();      
     }
}
