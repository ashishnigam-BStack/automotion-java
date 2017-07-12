package net.itarray.automotion.tests.geometry;

import net.itarray.automotion.internal.geometry.Scalar;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ScalarTest {

    private int value;
    private Scalar scalar;

    @Before
    public void setUp() {
        value = 13;
        scalar = new Scalar(value);
    }

    @Test
    public void isEqualToScalarsWithEqualValue() {
        assertThat(scalar).isEqualTo(new Scalar(value));
        assertThat(scalar.hashCode()).isEqualTo(new Scalar(value).hashCode());
    }

    @Test
    public void isNotEqualToScalarsWithDifferentValue() {
        assertThat(scalar).isNotEqualTo(new Scalar(value+1));
    }

    @Test
    public void isNotEqualToObjects() {
        assertThat(scalar).isNotEqualTo(new Object());
    }

    @Test
    public void toStringWithUnitsAppendsTheUnitsToToString() {
        assertThat(scalar.toStringWithUnits("px")).isEqualTo("13px");
    }

    @Test
    public void getValueReturnsTheConstructorParameter() {
        assertThat(scalar.getValue()).isEqualTo(value);
    }

    @Test
    public void plusIntReturnsAScalarWithValueEqualToTheSumOfValueAndTheAddend() {
        int addend = 2;
        assertThat(scalar.plus(addend)).isEqualTo(new Scalar(value + addend));
    }

    @Test
    public void plusScalarReturnsAScalarWithValueEqualToTheSumOfValueAndTheAddend() {
        Scalar addend = new Scalar(2);
        assertThat(scalar.plus(addend)).isEqualTo(new Scalar(value + addend.getValue()));
    }

    @Test
    public void minusIntReturnsAScalarWithValueEqualToTheDifferenceOfValueAndTheSubtrahend() {
        int addend = 2;
        assertThat(scalar.minus(addend)).isEqualTo(new Scalar(value - addend));
    }

    @Test
    public void minusScalarReturnsAScalarWithValueEqualToTheDifferenceOfValueAndTheSubtrahend() {
        Scalar addend = new Scalar(2);
        assertThat(scalar.minus(addend)).isEqualTo(new Scalar(value - addend.getValue()));
    }

    @Test
    public void negatedReturnsAScalarWithValueEqualToNegatedValue() {
        assertThat(scalar.negated()).isEqualTo(new Scalar(-value));
    }

    @Test
    public void absReturnsAScalarWithValueEqualToAbosulteValue() {
        assertThat(scalar.abs()).isEqualTo(new Scalar(value));
        assertThat(scalar.negated().abs()).isEqualTo(new Scalar(value));
        assertThat(new Scalar(0).abs()).isEqualTo(new Scalar(0));
    }

    @Test
    public void isCompareToWorks() {
        assertThat(scalar.compareTo(new Scalar(value-1))).isGreaterThan(0);
        assertThat(scalar.compareTo(new Scalar(value))).isEqualTo(0);
        assertThat(scalar.compareTo(new Scalar(value+1))).isLessThan(0);
    }

    @Test
    public void isLessOrEqualThanWorks() {
        assertThat(scalar.isLessOrEqualThan(new Scalar(value-1))).isFalse();
        assertThat(scalar.isLessOrEqualThan(new Scalar(value))).isTrue();
        assertThat(scalar.isLessOrEqualThan(new Scalar(value+1))).isTrue();
    }

    @Test
    public void isGreaterOrEqualThanWorks() {
        assertThat(scalar.isGreaterOrEqualThan(new Scalar(value-1))).isTrue();
        assertThat(scalar.isGreaterOrEqualThan(new Scalar(value))).isTrue();
        assertThat(scalar.isGreaterOrEqualThan(new Scalar(value+1))).isFalse();
    }

    @Test
    public void isLessThanWorks() {
        assertThat(scalar.isLessThan(new Scalar(value-1))).isFalse();
        assertThat(scalar.isLessThan(new Scalar(value))).isFalse();
        assertThat(scalar.isLessThan(new Scalar(value+1))).isTrue();
    }

    @Test
    public void isGreaterThanWorks() {
        assertThat(scalar.isGreaterThan(new Scalar(value-1))).isTrue();
        assertThat(scalar.isGreaterThan(new Scalar(value))).isFalse();
        assertThat(scalar.isGreaterThan(new Scalar(value+1))).isFalse();
    }
}
