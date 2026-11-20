package dev.returntojava.katas;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

/** Proves the toolchain works: JDK 25 language level, JUnit Jupiter, AssertJ, Mockito agent. */
class ToolchainSmokeTest {

    sealed interface Shape permits Circle, Square {}
    record Circle(double r) implements Shape {}
    record Square(double side) implements Shape {}

    @Test
    void runsOnJdk25() {
        assertThat(Runtime.version().feature()).isGreaterThanOrEqualTo(25);
    }

    @Test
    void patternSwitchOverSealedHierarchyIsExhaustive() {
        List<Shape> shapes = List.of(new Circle(1), new Square(2));
        var names = shapes.stream()
                .map(s -> switch (s) {
                    case Circle(var r) -> "circle:" + r;
                    case Square(var side) -> "square:" + side;
                })
                .toList();
        assertThat(names).containsExactly("circle:1.0", "square:2.0");
    }

    @Test
    void mockitoInlineMockMakerWorks() {
        @SuppressWarnings("unchecked")
        Supplier<String> supplier = mock(Supplier.class);
        when(supplier.get()).thenReturn("ok");
        assertThat(supplier.get()).isEqualTo("ok");
    }
}
