package ece351;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import ece351.util.TestInputs351;
import ece351.util.Utils351;

@RunWith(Parameterized.class)
public class TestImports {

	private final File f;

	public TestImports(final File f) {
		this.f = f;
	}

	@Parameterized.Parameters
	public static Collection<Object[]> files() {
		return TestInputs351.javaFiles();
	}

	private final static String[] strings = new String[]{
		"org.junit", 
		"java", 
		"org.parboiled", 
		"ece351", 
		"kodkod",
		"edu.mit.csail.sdg",
		"org.w3c.dom",
		"javax.xml.parsers",
		"org.apache.batik",
		"javax.tools",
	};
	
	private final static Pattern[] patterns;
	static {
		patterns = new Pattern[strings.length];
		for (int i = 0; i < strings.length; i++) {
			patterns[i] = Pattern.compile("import.*" + strings[i] + "\\..*;");
		}
	}
	
	@Test
	public void test() {
		final List<String> lines = Utils351.readFileLines(f.getAbsolutePath());
		for (final String line : lines) {
			if (line.startsWith("import ")) {
				boolean legal = false;
				for (final Pattern p : patterns) {
					legal |= p.matcher(line).matches();
				}
				final String msg = "illegal import in \n" + f.getName() + "\n" + line + "\n";
				assertTrue(msg, legal);
			}
		}
	}

}
