package org.highj.data;

import org.highj.data.compare.Ordering;
import org.highj.data.tuple.T2;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.awt.*;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class TreeMapTest {

    @Rule
    public ExpectedException shouldThrow = ExpectedException.none();

    private static String[] STRING_DATA =
            ("it is so shocking to find out how many people do not believe that they can learn "
                    + "and how many more believe learning to be difficult").split("\\s");
    private static final Comparator<Point> POINT_COMPARATOR = (p1, p2) ->
            Ordering.compare(p1.getX(), p2.getX()).andThen(p1.getY(), p2.getY()).cmpResult();

    @Test
    public void empty() {
        TreeMap<String, Integer> treeMap = TreeMap.empty();
        assertThat(treeMap.isEmpty()).isTrue();
        assertThat(treeMap.size()).isEqualTo(0);
        assertThat(treeMap.apply("x").isNothing()).isTrue();
    }

    @Test
    public void empty_withComparator() {
        TreeMap<Point, Integer> treeMap = TreeMap.empty(POINT_COMPARATOR);
        assertThat(treeMap.isEmpty()).isTrue();
        assertThat(treeMap.size()).isEqualTo(0);
        assertThat(treeMap.apply(new Point(2, 3)).isNothing()).isTrue();
    }

    @Test
    public void of() {
        TreeMap<String, Integer> treeMap = TreeMap.of("test", 42);
        assertThat(treeMap.isEmpty()).isFalse();
        assertThat(treeMap.size()).isEqualTo(1);
        assertThat(treeMap.apply("x").isNothing()).isTrue();
        assertThat(treeMap.apply("test")).containsExactly(42);
    }

    @Test
    public void of_withComparator() {
        TreeMap<Point, Integer> treeMap = TreeMap.of(POINT_COMPARATOR, new Point(2, 3), 42);
        assertThat(treeMap.isEmpty()).isFalse();
        assertThat(treeMap.size()).isEqualTo(1);
        assertThat(treeMap.apply(new Point(2, 4)).isNothing()).isTrue();
        assertThat(treeMap.apply(new Point(2, 3))).containsExactly(42);
    }

    @Test
    public void of_Iterable() {
        List<T2<String, Integer>> list = List.of(STRING_DATA).map(s -> T2.of(s, s.length()));
        TreeMap<String, Integer> treeMap = TreeMap.of(list);
        assertThat(treeMap.isEmpty()).isFalse();
        assertThat(treeMap.size()).isEqualTo(22);
        assertThat(treeMap.apply("x").isNothing()).isTrue();
        assertThat(treeMap.apply("to")).containsExactly(2);
    }

    @Test
    public void of_Iterable_withComparator() {
        List<T2<Point, Integer>> list = List.of(new Point(2, 4), new Point(6, 3), new Point(5, 2), new Point(6, 3))
                .map(p -> T2.of(p, p.x + p.y));
        TreeMap<Point, Integer> treeMap = TreeMap.of(POINT_COMPARATOR, list);
        assertThat(treeMap.isEmpty()).isFalse();
        assertThat(treeMap.size()).isEqualTo(3);
        assertThat(treeMap.apply(new Point(3, 7)).isNothing()).isTrue();
        assertThat(treeMap.apply(new Point(2, 4))).containsExactly(6);
    }

    @Test
    public void size() {
        TreeMap<Integer, Integer> treeMap = TreeMap.of(List.range(0, 1, 100).map(i -> T2.of(i, i * i)));
        treeMap = treeMap.insert(10, 100);
        treeMap = treeMap.insert(101, 10201);
        assertThat(treeMap.size()).isEqualTo(102);
    }

    @Test
    public void insert() {
        TreeMap<String, Integer> treeMap = TreeMap.<String, Integer>empty()
                .insert("foo", 10).insert("bar", 15).insert("bar", 20).insert("baz", 30);
        assertThat(treeMap.size()).isEqualTo(3);
        assertThat(treeMap.get("foo")).isEqualTo(10);
        assertThat(treeMap.get("bar")).isEqualTo(20);
        assertThat(treeMap.get("baz")).isEqualTo(30);
        assertThat(treeMap.containsKey("boo")).isFalse();
    }

    @Test
    public void insertAll() {
        TreeMap<String, Integer> treeMap = TreeMap.<String, Integer>empty()
                .insertAll(List.of("one", "two", "two", "three").map(s -> T2.of(s, s.length())));
        assertThat(treeMap.size()).isEqualTo(3);
        assertThat(treeMap.get("one")).isEqualTo(3);
        assertThat(treeMap.get("two")).isEqualTo(3);
        assertThat(treeMap.get("three")).isEqualTo(5);
        assertThat(treeMap.containsKey("four")).isFalse();
    }

    @Test
    public void get() {
        TreeMap<String, Integer> treeMap = TreeMap.of("foo", 10).insert("bar", 20).insert("baz", 30);
        assertThat(treeMap.get("foo")).isEqualTo(10);
        assertThat(treeMap.get("bar")).isEqualTo(20);
        assertThat(treeMap.get("baz")).isEqualTo(30);
        shouldThrow.expect(NoSuchElementException.class);
        treeMap.get("boo");
    }

    @Test
    public void apply() {
        TreeMap<String, Integer> treeMap = TreeMap.of("foo", 10).insert("bar", 20).insert("baz", 30);
        assertThat(treeMap.apply("foo").get()).isEqualTo(10);
        assertThat(treeMap.apply("bar").get()).isEqualTo(20);
        assertThat(treeMap.apply("baz").get()).isEqualTo(30);
        assertThat(treeMap.apply("boo").isNothing()).isTrue();
    }


    @Test
    public void containsKey() {
        TreeMap<String, Integer> treeMap = TreeMap.of("foo", 10).insert("bar", 20).insert("baz", 30);
        assertThat(treeMap.containsKey("foo")).isTrue();
        assertThat(treeMap.containsKey("bar")).isTrue();
        assertThat(treeMap.containsKey("baz")).isTrue();
        assertThat(treeMap.containsKey("boo")).isFalse();
    }

    @Test
    public void minimum() {
        TreeMap<String, Integer> treeMap = TreeMap.of(
                List.of("one", "two", "three", "four").map(s -> T2.of(s, s.length())));
        assertThat(treeMap.minimum()).isEqualTo(T2.of("four", 4));
        shouldThrow.expect(NoSuchElementException.class);
        TreeMap.<String, Integer>empty().minimum();
    }

    @Test
    public void maximum() {
        TreeMap<String, Integer> treeMap = TreeMap.of(
                List.of("one", "two", "three", "four").map(s -> T2.of(s, s.length())));
        assertThat(treeMap.maximum()).isEqualTo(T2.of("two", 3));
        shouldThrow.expect(NoSuchElementException.class);
        TreeMap.<String, Integer>empty().maximum();
    }

    @Test
    public void minimumKey() {
        TreeMap<String, Integer> treeMap = TreeMap.of(
                List.of("one", "two", "three", "four").map(s -> T2.of(s, s.length())));
        assertThat(treeMap.minimumKey()).isEqualTo("four");
        shouldThrow.expect(NoSuchElementException.class);
        TreeMap.<String, Integer>empty().minimumKey();
    }

    @Test
    public void maximumKey() {
        TreeMap<String, Integer> treeMap = TreeMap.of(
                List.of("one", "two", "three", "four").map(s -> T2.of(s, s.length())));
        assertThat(treeMap.maximumKey()).isEqualTo("two");
        shouldThrow.expect(NoSuchElementException.class);
        TreeMap.<String, Integer>empty().maximumKey();
    }

    @Test
    public void toKeys() {
        TreeMap<String, Integer> treeMap = TreeMap.of(
                List.of("one", "two", "three", "four").map(s -> T2.of(s, s.length())));
        assertThat(treeMap.toKeys()).containsExactly("four", "one", "three", "two");
    }

    @Test
    public void toList() {
        TreeMap<String, Integer> treeMap = TreeMap.of(
                List.of("one", "two", "three", "four").map(s -> T2.of(s, s.length())));
        assertThat(treeMap.toList()).containsExactly(
                T2.of("four", 4), T2.of("one", 3), T2.of("three", 5), T2.of("two", 3));
    }


    @Test
    public void toValues() {
        TreeMap<String, Integer> treeMap = TreeMap.of(
                List.of("one", "two", "three", "four").map(s -> T2.of(s, s.length())));
        assertThat(treeMap.toValues()).containsExactly(4, 3, 5, 3);
    }

    @Test
    public void deleteMin() {
        TreeMap<String, Integer> treeMap = TreeMap.of(
                List.of("one", "two", "three", "four").map(s -> T2.of(s, s.length())));
        assertThat(treeMap.deleteMin().toKeys()).containsExactly("one", "three", "two");
    }

    @Test
    public void deleteMax() {
        TreeMap<String, Integer> treeMap = TreeMap.of(
                List.of("one", "two", "three", "four").map(s -> T2.of(s, s.length())));
        assertThat(treeMap.deleteMax().toKeys()).containsExactly("four", "one", "three");
    }

    @Test
    public void delete() {
        TreeMap<String, Integer> treeMap = TreeMap.of(
                List.of("one", "two", "three", "four").map(s -> T2.of(s, s.length())));
        assertThat(treeMap.delete("one").toKeys()).containsExactly("four", "three", "two");
        assertThat(treeMap.delete("two").toKeys()).containsExactly("four", "one", "three");
        assertThat(treeMap.delete("three").toKeys()).containsExactly("four", "one", "two");
        assertThat(treeMap.delete("four").toKeys()).containsExactly("one", "three", "two");
        assertThat(treeMap.delete("five").toKeys()).containsExactly("four", "one", "three", "two");
    }

    @Test
    public void backToBack() {
        Random random = new Random(1);
        TreeMap<Integer, Double> treeMap = TreeMap.empty();
        java.util.Map<Integer, Double> jMap = new java.util.TreeMap<>();
        for (int i = 0; i < 10000; i++) {
            int k = random.nextInt(1000);
            if (random.nextBoolean()) {
                double v = random.nextDouble();
                treeMap = treeMap.insert(k,v);
                jMap.put(k,v);
            } else {
                treeMap = treeMap.delete(k);
                jMap.remove(k);
            }
        }
        assertThat(treeMap.toKeys()).containsExactly(jMap.keySet().toArray(new Integer[]{}));
        assertThat(treeMap.toValues()).containsExactly(jMap.values().toArray(new Double[]{}));
    }

}