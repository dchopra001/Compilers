package ece351.f;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import ece351.f.ast.FProgram;

@RunWith(Parameterized.class)
public class TestInlineIntermediateVars {

	private final FProgram original, expected;
	
	public TestInlineIntermediateVars(final String o, final String e) {
		original = parse(e).standardize();
		expected = parse(e).standardize();
	}
	
	@Parameterized.Parameters
	public static Collection<Object[]> tests() {
		final Object[] a = new Object[] {
			new String[]{
					// original
					"x <= b; b <= c;",
					// expected
					"x <= c;"
			},
			new String[]{
					// original
					"x <= a or b; b <= c or d;",
					// expected
					"x <= a or c or d;"
			},
			new String[]{
					// original
					"x <= a or b; b <= c or d; c <= e and f;",
					// expected
					"x <= a or (e and f) or d;"
			},
		};
		final ArrayList<Object[]> r = new ArrayList<Object[]>(a.length);
		for (int i = 0; i < a.length; i++) {
			r.add((String[]) a[i]);
		}
		return r;
	}


	@Test
	public void test() {
		final FProgram actual = InlineIntermediateVariables.inline(original).standardize();
		assertEquals(expected, actual);
	}

	/** Helper. */
	private static FProgram parse(final String s) {
		return FParser.parse(new String[]{s, "-h"});
	}
}
