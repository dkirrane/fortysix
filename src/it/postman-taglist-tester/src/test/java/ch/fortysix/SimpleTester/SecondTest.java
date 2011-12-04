package ch.fortysix.SimpleTester;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

public class SecondTest {

	@Test
	public void testname() throws Exception {
		assertTrue(false);
	}

	@Test
	public void testname2() throws Exception {
		assertTrue(true);
	}

	@Test
	@Ignore
	public void testname3() throws Exception {
		assertTrue(false);
	}
}
