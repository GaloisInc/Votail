package ie.votail.uilioch;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ChannelQueue<T> implements Channel<T> {
  
  BlockingQueue<T> queue;  
  
  public ChannelQueue (int capacity) {
    queue = new ArrayBlockingQueue<T> (capacity);
  }
  
  public void put(T x) throws InterruptedException {
   
    queue.put(x); 
  }
  
  public T take() throws InterruptedException {
    return queue.take();
  }
  
}