package net.bioclipse.sdk.templating.test;

import java.util.ArrayList;
import java.util.List;

import net.bioclipse.sdk.templating.Templater;

class TemplaterTest {
    static private abstract class TestCase {
        abstract boolean run();
    }

    static List<TestCase> tests = new ArrayList<TestCase>() {{

        add(new TestCase() {
            boolean run() {
                Templater t = new Templater("foo");
                return "foo".equals(t.generate());
            }
        });

        add(new TestCase() {
            boolean run() {
                Templater t = new Templater("foo");
                try {
                    t.generate("bar");
                }
                catch (IllegalArgumentException e) {
                    return true;
                }
                return false;
            }
        });

        add(new TestCase() {
            boolean run() {
                Templater t = new Templater("foo");
                try {
                    t.generate("noSuchKey", "value");
                }
                catch (IllegalArgumentException e) {
                    return true;
                }
                return false;
            }
        });

        add(new TestCase() {
            boolean run() {
                Templater t = new Templater("foo${key1}bar");
                try {
                    t.generate("key1", "foo",
                               "key1", "bar");
                }
                catch (IllegalArgumentException e) {
                    return true;
                }
                return false;
            }
        });

        add(new TestCase() {
            boolean run() {
                Templater t = new Templater("foo${key1}bar");
                return "foo!bar".equals(t.generate("key1", "!"));
            }
        });

        add(new TestCase() {
            boolean run() {
                Templater t = new Templater("foo${key1}${key1}bar");
                return "foo!!bar".equals(t.generate("key1", "!"));
            }
        });
    }};

    public static void main(String[] _) {
        System.out.print("1..");
        System.out.println(tests.size());
        for (int i = 0; i < tests.size(); ++i) {
            System.out.print( tests.get(i).run() ? "ok " : "not ok " );
            System.out.println( i+1 );
        }
    }
}
