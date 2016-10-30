package ece351.vhdl.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import ece351.f.ast.FProgram;
import ece351.f.parboiled.FParboiledParser;
import ece351.util.CommandLine;
import ece351.util.ExaminableProperties;
import ece351.util.TestInputs351;
import ece351.util.CommandLine.FSimplifierOptions;
import ece351.vhdl.Synthesizer;


@RunWith(Parameterized.class)
public final class TestSynthesizer {

	private final File f;

	public TestSynthesizer(final File f) {
		this.f = f;
	}

	@Parameterized.Parameters
	public static Collection<Object[]> files() {
		return TestInputs351.vhdlFiles();
	}

	@Test
	public void parse() {
		final String inputSpec = f.getAbsolutePath();
		final CommandLine c = new CommandLine("-h", "-o4", inputSpec);
		System.out.println("processing " + inputSpec);
		// parse the VHDL source and synthesize all assignment statements
		final String fp = Synthesizer.synthesize(c);
		System.out.println("generated F program: ");
		System.out.println(fp);
		// F parser will through an exception if it encounters a parse error
		FProgram fp1 = FParboiledParser.parse(fp);
		fp1 = fp1.standardize();
		fp1 = fp1.simplify(c.simplifierOpts);
		System.out.println("synthesizer generated a valid F program\n");
		
		// find the appropriate solution file for comparison
		String solnSpec = "";
		for (final Object[] obj : TestInputs351.synthesizedFFiles()) {
			if (obj[0] instanceof File) {
				final File soln = (File)obj[0];
				// strip file extensions for comparison
				final String fname1 = f.getName().substring(0, f.getName().lastIndexOf("."));
			    final String fname2 = soln.getName().substring(0, soln.getName().lastIndexOf("."));
			    if (fname1.equals(fname2)) {
					solnSpec = soln.getAbsolutePath();
					break;
				}
			}
		}

		assertTrue("no matching file found to compare the input file: " + inputSpec, solnSpec.length() > 0);
		System.out.println("checking f program output against: " + solnSpec);
		final CommandLine sc = new CommandLine("-h", "-o4", solnSpec);
		FProgram fp2 = FParboiledParser.parse(sc.readInputSpec());
		fp2 = fp2.standardize();
		fp2 = fp2.simplify(c.simplifierOpts);
		System.out.println("solution: ");
		System.out.println(fp2.toString());
		// check that the two ASTs are isomorphic (syntactically the same)
		assertTrue("ASTs differ for " + inputSpec, fp1.isomorphic(fp2));
		// check examinable sanity
		ExaminableProperties.checkAllUnary(fp1);
		ExaminableProperties.checkAllUnary(fp2);
		ExaminableProperties.checkAllBinary(fp1, fp2);
		// success!
		System.out.println("accepted, as expected:  " + inputSpec);
	}

}
