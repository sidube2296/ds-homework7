package edu.uwm.cs351;
import edu.uwm.cs351.Process.Queue;

/**
 * The Class Scheduler.
 */
public class Scheduler{
	
	private CPU cpu;
	private boolean roundRobin;
	private Queue ready, done;
	
	/** Instantiates a new scheduler with a boolean denoting type.
	 * @param rr denotes whether Round Robin of Strict-FIFO scheduler */
	public Scheduler(boolean rr){
		cpu = new CPU();
		ready = new Queue();
		done = new Queue();
		roundRobin = rr;}
	
	/** Schedules a new process in the ready queue.
	 * @param p the process to schedule */
	public void schedule(Process p){ready.offer(p);}
	
	/** Schedules all processes from the parameter queue into the ready queue.
	 * @param pq the process queue from which to take everything */
	public void scheduleAll(Queue pq){ready.takeAll(pq);}
	
	/** Gets the ready queue.
	 * @return the ready queue */
	public Queue getReadyQueue(){return ready;}
	
	/** Gets the done queue.
	 * @return the done queue */
	public Queue getDoneQueue(){return done;}
	
	/** Gets the CPU.
	 * @return the cpu */
	public CPU getCPU(){return cpu;}
	
	/** Checks if there are any processes left in the ready queue.
	 * @return true, if all processes are done */
	public boolean isDone(){return ready.isEmpty() && !cpu.hasProcess();}
	
	/** Executed at every pulse of the driver's clock.
	 * <ol>
	 *  <li> If the CPU has no process and there are some left in this
	 *  scheduler's ready queue, load the CPU with a process.
	 *  <li>
	 *  Call step on the CPU.
	 *  <li>
	 *  If the process in the CPU is done, then offer it to the
	 *  done queue. Otherwise, if this is a Round Robin scheduler,
	 *  unload it from the CPU and offer it back to ready queue.
	 * </ol>
	 *  @pre !isDone()
	 *  @throws NullPointerException if there are not processes left.
	 */
	public void step(){
		// TODO
		if (isDone()) throw new NullPointerException("There are no processes left to schedule.");
	    if (!cpu.hasProcess() && !ready.isEmpty()) 
	        cpu.load(ready.poll());
	    if (cpu.hasProcess()) {
	        cpu.step(); 
	        if (cpu.getProcess().isDone())
	            done.offer(cpu.unload());
	        else if (roundRobin)
	            ready.offer(cpu.unload());
	    }
	}
}
