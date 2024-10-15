import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.uwm.cs.junit.EfficiencyTestCase;
import edu.uwm.cs351.Process;

public class TestEfficiency extends EfficiencyTestCase{

	private Process.Queue li;
	private Random gen = new Random();
	
	@Override
	public void setUp(){
		li = new Process.Queue();
		try {
			assert 1/li.size() == 42 : "OK";
			assertTrue(true);
		} catch (ArithmeticException ex) {
			assertFalse("Assertions must NOT be enabled while running efficiency tests.",true);
		}
		super.setUp();
	}
	
	private static final int POWER = 20;
	private static final int MAX = 1 << POWER;
	
	public void test0() {
		for (int i=0; i < MAX; ++i) {
			li.add(new Process("", i));
			if ((i % 2) == 1) {
				Process p = li.poll();
				assertEquals(i/2, p.getTotal());
			} 
		}
		assertEquals(MAX/2, li.size());
	}
	
	public void test1() {
		for (int i=0; i < MAX; ++i) {
			li.add(new Process("", i));
			if ((i % 2) == 1) {
				Process p = li.poll();
				assertEquals(i/2, p.getTotal());
			} else {
				assertEquals(i/2 + 1, li.size());
			}
		}
	}
	
	public void test2() {
		for (int i=0; i < MAX; ++i) {
			li.add(new Process("", i));
			assertEquals(0, li.peek().getTotal());
		}
		assertEquals(MAX, li.size());
		for (int i=0; i < MAX; ++i) {
			Process p = li.poll();
			assertEquals(i, p.getTotal());
		}
		assertTrue(li.isEmpty());
	}
	
	public void test3() {
		for (int i=0; i < MAX; ++i) {
			li.add(new Process("", i));
			assertEquals(0, li.peek().getTotal());
		}
		assertEquals(MAX, li.size());
		for (int i=0; i < MAX; ++i) {
			li.clear();
			assertEquals(0, li.size());
		}
	}
	
	public void test4() {
		List<Process> l = new ArrayList<Process>();
		for (int i=0; i < MAX; ++i) {
			Process p = new Process("", i);
			l.add(p);
			li.add(p);
		}
		li.clear();
		for (Process p : l) {
			li.add(p);
		}
		assertEquals(MAX, li.size());
	}
	
	public void test5() {
		for (int i=0; i < MAX; ++i) {
			Process p = new Process("", i);
			li.add(p);
		}
		Process.Queue alt = new Process.Queue();
		for (int i=0; i < MAX; ++i) {
			alt.takeAll(li);
			assertEquals(MAX, alt.size());
			li.takeAll(alt);
			assertEquals(0, alt.size());
		}
	}
	
	public void test6() {
		Process.Queue alt = new Process.Queue();
		for (int i=0; i < MAX; ++i) {
			assertEquals(i, li.size());
			Process p = new Process("", i);
			alt.takeAll(li);
			li.add(p);
			li.takeAll(alt);
		}
		assertEquals(MAX, li.size());
	}
	
	public void test7(){
		int i;
		int[] init = new int[2];
		li.offer(new Process("testdata1", init[0] = gen.nextInt(MAX)));
		li.offer(new Process("testdata2", init[1] = gen.nextInt(MAX)));
		for(i=1; i<POWER; i++) {
			li.takeAll(li.clone());
		}
		assertEquals(MAX, li.size());
		
		i=0;
		while (!li.isEmpty()){
			assertEquals(init[i++%2], li.poll().getTotal());
			assertEquals(MAX-i, li.size());
		}
		assertEquals(MAX, i);
	}
}
