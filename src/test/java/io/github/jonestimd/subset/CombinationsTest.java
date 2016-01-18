package io.github.jonestimd.subset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
import org.junit.Test;

import static org.fest.assertions.Assertions.*;

public class CombinationsTest {
    private int removedCount = 0;

    @Test
    public void visitAllCombinations() throws Exception {
        final List<List<String>> combinations = new ArrayList<>();
        CombinationVisitor<String> visitor = new CombinationVisitor<String>() {
            public boolean itemAdded(List<String> subset, String item) {
                combinations.add(subset);
                return true;
            }

            public void itemRemoved(String item) {
                removedCount++;
            }
        };

        Combinations.visitCombinations(Arrays.asList("A", "B", "C", "D"), visitor);

        assertThat(combinations).hasSize(15);
        assertThat(removedCount).isEqualTo(15);
        assertThat(combinations).contains(Lists.newArrayList("A"));
        assertThat(combinations).contains(Lists.newArrayList("A", "B"));
        assertThat(combinations).contains(Lists.newArrayList("A", "B", "C"));
        assertThat(combinations).contains(Lists.newArrayList("A", "B", "C", "D"));
        assertThat(combinations).contains(Lists.newArrayList("A", "B", "D"));
        assertThat(combinations).contains(Lists.newArrayList("A", "C"));
        assertThat(combinations).contains(Lists.newArrayList("A", "C", "D"));
        assertThat(combinations).contains(Lists.newArrayList("A", "D"));
        assertThat(combinations).contains(Lists.newArrayList("B"));
        assertThat(combinations).contains(Lists.newArrayList("B", "C"));
        assertThat(combinations).contains(Lists.newArrayList("B", "C", "D"));
        assertThat(combinations).contains(Lists.newArrayList("B", "D"));
        assertThat(combinations).contains(Lists.newArrayList("C"));
        assertThat(combinations).contains(Lists.newArrayList("C", "D"));
        assertThat(combinations).contains(Lists.newArrayList("D"));
    }

    @Test
    public void skipCombinations() throws Exception {
        final List<List<String>> combinations = new ArrayList<>();
        CombinationVisitor<String> visitor = new CombinationVisitor<String>() {
            public boolean itemAdded(List<String> subset, String item) {
                combinations.add(subset);
                return ! subset.contains("C");
            }

            public void itemRemoved(String item) {
                removedCount++;
            }
        };

        Combinations.visitCombinations(Lists.newArrayList("A", "B", "C", "D"), visitor);

        assertThat(combinations).hasSize(11);
        assertThat(removedCount).isEqualTo(11);
        assertThat(combinations).contains(Lists.newArrayList("A"));
        assertThat(combinations).contains(Lists.newArrayList("A", "B"));
        assertThat(combinations).contains(Lists.newArrayList("A", "B", "C"));
        assertThat(combinations).contains(Lists.newArrayList("A", "B", "D"));
        assertThat(combinations).contains(Lists.newArrayList("A", "C"));
        assertThat(combinations).contains(Lists.newArrayList("A", "D"));
        assertThat(combinations).contains(Lists.newArrayList("B"));
        assertThat(combinations).contains(Lists.newArrayList("B", "C"));
        assertThat(combinations).contains(Lists.newArrayList("B", "D"));
        assertThat(combinations).contains(Lists.newArrayList("C"));
        assertThat(combinations).contains(Lists.newArrayList("D"));
    }
}
