public class Diner extends Thread{
    private Thread dinerThread;
    private long dinerArrived; 
    private int burgerRequested,friesRequested,cokeRequested;
    private int dinerId;
    private int tableId;

    //#region Getters and Setters
    public long getDinerArrived() {
        return dinerArrived;
    }

    public void setDinerArrived(long dinerArrived) {
        this.dinerArrived = dinerArrived;
    }

    public int getBurgerRequested() {
        return burgerRequested;
    }

    public void setBurgerRequested(int burgerRequested) {
        this.burgerRequested = burgerRequested;
    }
    
    public int getFriesRequested() {
        return friesRequested;
    }

    public void setFriesRequested(int friesRequested) {
        this.friesRequested = friesRequested;
    }

    public int getCokeRequested() {
        return cokeRequested;
    }

    public void setCokeRequested(int cokeRequested) {
        this.cokeRequested = cokeRequested;
    }
    
    public int getDinerId() {
        return dinerId;
    }

    public void setDinerId(int dinerId) {
        this.dinerId = dinerId;
    }
    //#endregion

    public Diner(long dinerArrived,int burgerRequested,int friesRequested, int cokeRequested,int dinerId){
        this.dinerArrived = dinerArrived;
        this.burgerRequested=burgerRequested;
        this.friesRequested=friesRequested;
        this.cokeRequested=cokeRequested;  
        this.dinerId=dinerId;
    }

    public Diner(){}

    public void AssignTable(){
        try {  
            synchronized(Restaurant.tableLock){
                while(Restaurant.tableCount<=0){
                    Restaurant.tableLock.wait();             
                } 
                Restaurant.tableCount --;                
                tableId = Restaurant.allTables.remove(0);               
                long timeStamp=(System.currentTimeMillis()-Restaurant.restaurantOpenTime)/1000;
                System.out.println(timeStamp +" Diner " + dinerId +" is seated at table "+ tableId);                        
            }                                         
        } catch (InterruptedException e) {         
        }
    }

    public void AssignCook(){
        try {     
            synchronized(Restaurant.waitingForCooks)  {
                Restaurant.waitingForCooks.add(this);  
                Restaurant.waitingForCooks.notify();  
            }             
        }                                                                                         
        catch (Exception ex) {      
        }
    }

    public void WaitForFood(){
        try {    
            synchronized(this){
              this.wait(); 
            }                 
        }                                                                                         
        catch (InterruptedException e) {         
        }
    }

    public void Eat(){
        try {  
            long timeStamp=(System.currentTimeMillis()-Restaurant.restaurantOpenTime)/1000;
            System.out.println(timeStamp +" Diner " + dinerId +"'s food is ready. Diner "+ dinerId+" starts to eat.");           
            Thread.sleep(30000);                 
        }                                                                                         
        catch (InterruptedException e) {         
        }
    }

    public void LeaveTable(){
        synchronized(Restaurant.tableLock){
            Restaurant.tableCount ++;
            long timeStamp=(System.currentTimeMillis()-Restaurant.restaurantOpenTime)/1000;
            System.out.println(timeStamp +" Diner " + dinerId +" leaves ");
            Restaurant.allTables.add(tableId);
            Restaurant.tableLock.notify();
        }

        synchronized(Restaurant.dinerLock){
            Restaurant.dinerCount--;
        }
    }

    public void LastDinerLeave(){
        synchronized(Restaurant.dinerLock){
            if(Restaurant.dinerCount==0){
                long timeStamp=(System.currentTimeMillis()-Restaurant.restaurantOpenTime)/1000;
                System.out.println(timeStamp+" The last diner has left and the restaurant is to be closed.");
                System.exit(0);
            }
            
        }
    }

    public void run() {
        long timeStamp=(System.currentTimeMillis()-Restaurant.restaurantOpenTime)/1000;
        System.out.println(timeStamp +" Diner " + dinerId +" arrives.");
        try {                   
                AssignTable();
                AssignCook();
                WaitForFood();  
                Eat(); 
                LeaveTable();
                LastDinerLeave();                         
        } 
        catch (Exception ex) {     
        }        
    }

    public void start () {
        dinerThread = new Thread (this);
        dinerThread.start ();      
     }
}
