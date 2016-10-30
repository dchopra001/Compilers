package ece351.vhdl.test;

import org.junit.Test;

import ece351.util.TestInputs351;
import ece351.vhdl.DefBeforeUseChecker;

import java.io.File;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TestDefBeforeUseCheckerAccept {

	private final File f;

	public TestDefBeforeUseCheckerAccept(final File f) {
		this.f = f;
	}

	@Parameterized.Parameters
	public static Collection<Object[]> files() {
		return TestInputs351.vhdlFiles();
	}

	@Test
	public void accept() {
		final String inputSpec = f.getAbsolutePath();
		DefBeforeUseChecker.main(inputSpec);
		System.out.println("accepted, as expected:  " + inputSpec);
	}


}
