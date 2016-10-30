package ece351.vhdl.test;

import static org.junit.Assert.fail;

import java.io.File;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import ece351.util.TestInputs351;
import ece351.vhdl.VRecognizer;


@RunWith(Parameterized.class)
public class TestVRecognizerReject {

	private final File f;

	public TestVRecognizerReject(final File f) {
		this.f = f;
	}

	@Parameterized.Parameters
	public static Collection<Object[]> files() {
		return TestInputs351.badVhdlFiles();
	}

	@Test
	public void accept() {
		final String inputSpec = f.getAbsolutePath();
		try {
			VRecognizer.main(inputSpec);
			fail("should have rejected but didn't:  " + inputSpec);
		} catch (final Exception e) {
			System.out.println("rejected, as expected:  " + inputSpec);
		}
	}


}
