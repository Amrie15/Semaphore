import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class BusStop{
    static int riders=0;
    
    static Semaphore mutex= new Semaphore(1);
    static Semaphore multiplex= new Semaphore(50);
    static Semaphore bus= new Semaphore(0);
    static Semaphore allBoard= new Semaphore(0);

    // Bus class
    static class Bus extends Thread{
        
        private String busName;
    
        public Bus(String busName){
            this.busName=busName;
        }
            
        public void run(){   
            try{
                System.out.println(busName+" arrived to the bus stop");
                try {
                    mutex.acquire();
                    System.out.println(busName+" picking up riders "+"\n///////////////////////////////////////////////////////////////");
                    if (riders > 0){
                        bus.release();
                        allBoard.acquire();
                    }
                }
                finally{
                    mutex.release();
                    System.out.println("///////////////////////////////////////////////////////////////\n"+busName+" leaving");
                }
            }catch(InterruptedException e) {

				e.printStackTrace();

			}
          
        }

    }

    // Rider class 
    static class Rider extends Thread{
        private String riderName;
    
        public Rider(String riderName){
            this.riderName = riderName;
        }
        public void run(){
            System.out.println(riderName+ " wait for the bus.");
    
            try{
                try{
                    multiplex.acquire();
                    try{
                    mutex.acquire();
                    riders += 1;
                    }finally{
                        mutex.release();
                    }
                    
                    bus.acquire();
                     
                }finally{
                    
                    multiplex.release();
                }   

                System.out.println(riderName+" getting into the bus");
                
                    
                
                riders -= 1;
                
                if( riders == 0){
                    allBoard.release();
                }else {
                    bus.release();
                }
            }catch(InterruptedException e) {

				e.printStackTrace();

			}
        }
    
    }


    public static void main(String args[]){
        System.out.println("Initiating a Bus stop.");

        
    
        for (int i=0 ; i<60;i++){
            Rider temp= new Rider("Rider "+(i+1));
            temp.start();
        }

        // Ensures 60 passengers came early than bus 
        try {
            TimeUnit.SECONDS.sleep(60);    
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Bus b1= new Bus("Bus 1");
        b1.start();

        // Next bus will come after 50 seconds, remaining passengers (10) will board on bus 2
        try {
            TimeUnit.SECONDS.sleep(50);    
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Bus b2= new Bus("Bus 2");
        b2.start();

    }

}