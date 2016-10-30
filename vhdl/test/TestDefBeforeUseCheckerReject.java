package ece351.vhdl.test;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import ece351.util.TestInputs351;
import ece351.vhdl.DefBeforeUseChecker;


@RunWith(Parameterized.class)
public class TestDefBeforeUseCheckerReject {

	private final File f;

	public TestDefBeforeUseCheckerReject(final File f) {
		this.f = f;
	}

	@Parameterized.Parameters
	public static Collection<Object[]> files() {
		return TestInputs351.badDefVhdlFiles();
	}

	@Test
	public void accept() {
		final String inputSpec = f.getAbsolutePath();
		try {
			DefBeforeUseChecker.main(inputSpec);
			fail("should have rejected but didn't:  " + inputSpec);
		} catch (final Exception e) {
			System.out.println("rejected, as expected:  " + inputSpec);
		}
	}


}
