package net.itarray.automotion.validation.properties;

import net.itarray.automotion.internal.geometry.Direction;
import net.itarray.automotion.internal.geometry.ExtendGiving;
import net.itarray.automotion.internal.geometry.MetricSpace;
import net.itarray.automotion.internal.geometry.Scalar;
import net.itarray.automotion.internal.properties.AndExpression;
import net.itarray.automotion.internal.properties.BinaryExpression;
import net.itarray.automotion.internal.properties.ConstantExpression;
import net.itarray.automotion.internal.properties.Context;
import net.itarray.automotion.internal.properties.OrExpression;
import net.itarray.automotion.internal.properties.PagePercentage;
import net.itarray.automotion.internal.properties.PagePercentageOrPixels;
import net.itarray.automotion.internal.properties.PercentReference;
import net.itarray.automotion.internal.properties.PixelConstant;

import static net.itarray.automotion.internal.geometry.Scalar.scalar;
import static net.itarray.automotion.internal.properties.PercentReference.PAGE;

public interface Expression<T> {

    static PagePercentageOrPixels percentOrPixels(Scalar constant) {
        return new PagePercentageOrPixels(constant);
    }

    static PagePercentageOrPixels percentOrPixels(int constant) {
        return percentOrPixels(scalar(constant));
    }

    static Expression<Scalar> percent(int percentage, PercentReference reference) {
        return percent(scalar(percentage), reference);
    }

    static Expression<Scalar> percent(Scalar percentage, PercentReference reference) {
        if (PAGE.equals(reference)) {
            return new PagePercentage(percentage);
        } else {
            throw new RuntimeException("unsupported percentage reference " + reference);
        }
    }

    static <V extends MetricSpace<V>> Expression<Boolean> equalTo(Expression<V> left, Expression<V> right) {
        return new BinaryExpression<>(
                left,
                right,
                (vector, other, context) -> vector.minus(other).norm().isLessOrEqualTo(context.getTolerance()),
                "Expected %1$s to be equal to %2$s.");
    }

    static <V extends MetricSpace<V>> Expression<V> signedDistance(Expression<V> left, Expression<V> right, ExtendGiving<V> extendGiving) {
        return new BinaryExpression<>(
                left,
                right,
                (vector, other, context) -> extendGiving.signedDistance(vector, other),
                "offset of %1$s from %2$s");
    }

    <V extends MetricSpace<V>> T evaluateIn(Context context, ExtendGiving<V> direction);

    <V extends MetricSpace<V>> String getDescription(Context context, ExtendGiving<V> direction);

    default <V extends MetricSpace<V>> String getRepeatedDescription(Context context, ExtendGiving<V> direction) {
        return getDescription(context, direction);
    }

    static Expression<Boolean> and(Expression<Boolean> left, Expression<Boolean> right) {
        return new AndExpression(left, right);
    }

    static Expression<Boolean> or(Expression<Boolean> left, Expression<Boolean> right) {
        return new OrExpression(left, right);
    }
}
