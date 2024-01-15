import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
  
public class Restaurant {

   public static int dinerCount, cookCount, tableCount;
   public static boolean isBurgerMachineBusy,isFriesMachineBusy,isCokeMachineBusy = false;
   public static Object burgerMachineLock= new Object();
   public static Object friesMachineLock= new Object();
   public static Object cokeMachineLock = new Object();
   public static Object cookLock= new Object();
   public static Object tableLock= new Object();
   public static Object dinerLock = new Object();  
   public static ArrayList<Diner> allDiners= new ArrayList<Diner>();
   public static ArrayList<Cook> allCooks = new ArrayList<Cook>();
   public static ArrayList<Integer> allTables= new ArrayList<Integer>();
   public static ArrayList<Diner> waitingForCooks = new ArrayList<Diner>();
   public static long restaurantOpenTime;

   public static void main(String args[]) {

      //Read Input 
      String filePath=args[0];
      try {

         BufferedReader reader = new BufferedReader(new FileReader(filePath));

         dinerCount = Integer.parseInt(reader.readLine().trim());
         if(dinerCount<=0){
            System.out.println("Error: Diner count cannot be zero");
            System.exit(0);
         }
         tableCount = Integer.parseInt(reader.readLine().trim());
         if(tableCount<=0){
            System.out.println("Error: Table count cannot be zero");
            System.exit(0);
         }
         cookCount = Integer.parseInt(reader.readLine().trim());
         if(cookCount<=0){
            System.out.println("Error: Cook count cannot be zero");
            System.exit(0);
         }

         for(int i=0; i<dinerCount;i++){         
               String dinerValues = reader.readLine().trim();
               String values[] = dinerValues.split(",") ;
               long dinerArrived = Integer.parseInt(values[0]);
               int burgerRequested = Integer.parseInt(values[1]);
               int friesRequested = Integer.parseInt(values[2]);
               int cokeRequested = Integer.parseInt(values[3]);
               int dinerId = i+1;

               if(dinerArrived>120){
                  System.out.println("Error: Diner can nor arrive at restaurant after 120 minutes");
                  System.exit(0);
               }
               if(burgerRequested==0){
                  System.out.println("Error: Diner should order at least one burger");
                  System.exit(0);
               }

               Diner diner = new Diner(dinerArrived,burgerRequested,friesRequested,cokeRequested,dinerId);  
               allDiners.add(diner);      
         }
         reader.close();
      } catch (IOException e) {
         e.printStackTrace();
      }      

      System.out.println("In Restaurant 6431, there are "+ tableCount +" tables, "+ cookCount +" cooks, and "+ dinerCount +" dinner will be coming.");

      for(int i=1;i<=tableCount;i++){
         allTables.add(i);
      }

      for(int i=1;i<=cookCount;i++){
         Cook cook = new Cook(i);   
         allCooks.add(cook);       
         cook.start();
      }

      restaurantOpenTime = System.currentTimeMillis();
      long dinerStartTime = 0;
      long previousTime =0;              

      for (Diner i : allDiners) {
         try {
            dinerStartTime=i.getDinerArrived() - previousTime;
            Thread.sleep(dinerStartTime*1000);
            i.start();
            previousTime=i.getDinerArrived();           
         } catch (InterruptedException e) {
         }
      }
   } 
}
