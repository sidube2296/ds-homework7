import edu.uwm.cs351.Process;
import edu.uwm.cs351.Scheduler;
import junit.framework.TestCase;

public class TestScheduler extends TestCase{
	private Scheduler s;
	private Process.Queue pq;
	private Process[] procs;
	
	public void setUp(){
		pq = new Process.Queue();
		procs = new Process[] {
				new Process("p1",10),
				new Process("p2",10),
				new Process("p3",10),
				new Process("p4",10),
				new Process("p5",10)};
		try {
			assert 1/pq.size() == 42 : "OK";
			System.err.println("Assertions must be enabled to use this test suite.");
			System.err.println("In Eclipse: add -ea in the VM Arguments box under Run>Run Configurations>Arguments");
			assertFalse("Assertions must be -ea enabled in the Run Configuration>Arguments>VM Arguments",true);
		} catch (ArithmeticException ex) {
			return;
		}
	}

	public void testRoundRobin(){
		s = new Scheduler(true);
		for (Process p: procs)
			s.schedule(p);
	
		for (int i=0;i<50;i++){
			assertEquals("is CPU empty?", null, s.getCPU().getProcess());
			assertEquals("what's first process in ready queue?", s.getReadyQueue().peek(), procs[i%5]);
			s.step();}
		
		assertTrue(s.isDone());
		for (Process p: procs)
			assertEquals("were processes moved to done queue?", p, s.getDoneQueue().poll());
	}
	
	public void testStrictFIFO(){
		s = new Scheduler(false);
		for (Process p: procs)
			s.schedule(p);
	
		Process cur;
		for (int i=0;i<5;i++){
			assertEquals("what's first process in ready queue?",s.getReadyQueue().peek(), cur = procs[i%5]);
			
			for (int j=0; j<10;j++)
				s.step();

			assertEquals("was process moved to done queue?",cur, s.getDoneQueue().poll());
			assertEquals("is CPU empty?",null, s.getCPU().getProcess());
		}
		
		assertTrue(s.isDone());
	}
}
