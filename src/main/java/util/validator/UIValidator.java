package util.validator;

import net.itarray.automotion.internal.AbstractValidator;
import net.itarray.automotion.internal.DriverFacade;
import net.itarray.automotion.internal.UIValidatorBase;
import net.itarray.automotion.internal.geometry.Scalar;
import net.itarray.automotion.validation.UIElementValidator;
import net.itarray.automotion.validation.UISnapshot;
import net.itarray.automotion.validation.properties.Condition;
import org.openqa.selenium.WebElement;
import net.itarray.automotion.validation.properties.Padding;

import java.util.List;

/**
 */
@Deprecated
public class UIValidator extends AbstractValidator implements Validator {

    public UIValidator(UISnapshot snapshot, DriverFacade driver, WebElement webElement, String readableNameOfElement) {
        super(driver, new UIValidatorBase(snapshot, webElement, readableNameOfElement));
    }

    protected UIValidatorBase getBase() {
        return (UIValidatorBase) super.getBase();
    }

    @Override
    public UIValidator drawMap() {
        super.drawMap();
        return this;
    }

    @Override
    public UIValidator dontDrawMap() {
        super.dontDrawMap();
        return this;
    }

    /**
     * Change units to Pixels or % (Units.PX, Units.PERCENT)
     *
     * @param units
     * @return UIValidator
     */
    @Override
    public UIValidator changeMetricsUnitsTo(Units units) {
        getBase().changeMetricsUnitsTo(units);
        return this;
    }

    @Override
    public UIValidator changeMetricsUnitsTo(net.itarray.automotion.validation.Units units) {
        getBase().changeMetricsUnitsTo(units);
        return this;
    }

    /**
     * Verify that element which located left to is correct
     *
     * @param element
     * @return UIValidator
     */
    @Override
    public UIValidator isRightOf(WebElement element) {
        getBase().isRightOf(element);
        return this;
    }

    /**
     * Verify that element which located left to is correct with specified margins
     *
     * @param element
     * @param minMargin
     * @param maxMargin
     * @return UIValidator
     */
    @Override
    public UIValidator isRightOf(WebElement element, int minMargin, int maxMargin) {
        getBase().isRightOf(element, minMargin, maxMargin);
        return this;
    }

    /**
     * Verify that element which located right to is correct
     *
     * @param element
     * @return UIValidator
     */
    @Override
    public UIValidator isLeftOf(WebElement element) {
        getBase().isLeftOf(element);
        return this;
    }

    /**
     * Verify that element which located right to is correct with specified margins
     *
     * @param element
     * @param minMargin
     * @param maxMargin
     * @return UIValidator
     */
    @Override
    public UIValidator isLeftOf(WebElement element, int minMargin, int maxMargin) {
        getBase().isLeftOf(element, minMargin, maxMargin);
        return this;
    }

    /**
     * Verify that element which located top to is correct
     *
     * @param element
     * @return UIValidator
     */
    @Override
    public UIValidator isBelow(WebElement element) {
        getBase().isBelow(element);
        return this;
    }

    /**
     * Verify that element which located top to is correct with specified margins
     *
     * @param element
     * @param minMargin
     * @param maxMargin
     * @return UIValidator
     */
    @Override
    public UIValidator isBelow(WebElement element, int minMargin, int maxMargin) {
        getBase().isBelow(element, minMargin, maxMargin);
        return this;
    }

    /**
     * Verify that element which located bottom to is correct
     *
     * @param element
     * @return UIValidator
     */
    @Override
    public UIValidator isAbove(WebElement element) {
        getBase().isAbove(element);
        return this;
    }

    /**
     * Verify that element which located bottom to is correct with specified margins
     *
     * @param element
     * @param minMargin
     * @param maxMargin
     * @return UIValidator
     */
    @Override
    public UIValidator isAbove(WebElement element, int minMargin, int maxMargin) {
        getBase().isAbove(element, minMargin, maxMargin);
        return this;
    }

    /**
     * Verify that element is NOT overlapped with specified element
     *
     * @param element
     * @param readableName
     * @return UIValidator
     */
    @Override
    public UIValidator notOverlapWith(WebElement element, String readableName) {
        getBase().notOverlapWith(element, readableName);
        return this;
    }

    /**
     * Verify that element is overlapped with specified element
     *
     * @param element
     * @param readableName
     * @return UIValidator
     */
    @Override
    public UIValidator overlapWith(WebElement element, String readableName) {
        getBase().overlapWith(element, readableName);
        return this;
    }

    /**
     * Verify that element is NOT overlapped with every element is the list
     *
     * @param elements
     * @return UIValidator
     */
    @Override
    public UIValidator notOverlapWith(List<WebElement> elements) {
        getBase().notOverlapWith(elements);
        return this;
    }

    /**
     * Verify that element has the same left offset as specified element
     *
     * @param element
     * @param readableName
     * @return UIValidator
     */
    @Override
    public UIValidator isLeftAlignedWith(WebElement element, String readableName) {
        getBase().isLeftAlignedWith(element, readableName);
        return this;
    }

    /**
     * Verify that element has the same left offset as every element is the list
     *
     * @param webElements
     * @return UIValidator
     */
    @Override
    public UIValidator isLeftAlignedWith(List<WebElement> webElements) {
        getBase().isLeftAlignedWith(webElements);
        return this;
    }

    /**
     * Verify that element has the same right offset as specified element
     *
     * @param element
     * @param readableName
     * @return UIValidator
     */
    @Override
    public UIValidator isRightAlignedWith(WebElement element, String readableName) {
        getBase().isRightAlignedWith(element, readableName);
        return this;
    }

    /**
     * Verify that element has the same right offset as every element is the list
     *
     * @param elements
     * @return UIValidator
     */
    @Override
    public UIValidator isRightAlignedWith(List<WebElement> elements) {
        getBase().isRightAlignedWith(elements);
        return this;
    }

    /**
     * Verify that element has the same top offset as specified element
     *
     * @param element
     * @param readableName
     * @return UIValidator
     */
    @Override
    public UIValidator isTopAlignedWith(WebElement element, String readableName) {
        getBase().isTopAlignedWith(element, readableName);
        return this;
    }

    /**
     * Verify that element has the same top offset as every element is the list
     *
     * @param elements
     * @return UIValidator
     */
    @Override
    public UIValidator isTopAlignedWith(List<WebElement> elements) {
        getBase().isTopAlignedWith(elements);
        return this;
    }

    /**
     * Verify that element has the same bottom offset as specified element
     *
     * @param element
     * @param readableName
     * @return UIValidator
     */
    @Override
    public UIValidator isBottomAlignedWith(WebElement element, String readableName) {
        getBase().isBottomAlignedWith(element, readableName);
        return this;
    }

    /**
     * Verify that element has the same bottom offset as every element is the list
     *
     * @param elements
     * @return UIValidator
     */
    @Override
    public UIValidator isBottomAlignedWith(List<WebElement> elements) {
        getBase().isBottomAlignedWith(elements);
        return this;
    }

    /**
     * Verify that element has the same width as specified element
     *
     * @param element
     * @param readableName
     * @return UIValidator
     */
    @Override
    public UIValidator sameWidthAs(WebElement element, String readableName) {
        getBase().sameWidthAs(element, readableName);
        return this;
    }

    /**
     * Verify that element has the same width as every element in the list
     *
     * @param elements
     * @return UIValidator
     */
    @Override
    public UIValidator sameWidthAs(List<WebElement> elements) {
        getBase().sameWidthAs(elements);
        return this;
    }

    /**
     * Verify that width of element is in range
     *
     * @param min
     * @param max
     * @return UIValidator
     */
    @Override
    public UIValidator hasWidthBetween(int min, int max) {
        getBase().hasWidthBetween(min, max);
        return this;
    }

    @Override
    public UIElementValidator hasWidth(Condition<Scalar> condition) {
        getBase().hasWidth(condition);
        return this;
    }

    /**
     * Verify that element has the same height as specified element
     *
     * @param element
     * @param readableName
     * @return UIValidator
     */
    @Override
    public UIValidator sameHeightAs(WebElement element, String readableName) {
        getBase().sameHeightAs(element, readableName);
        return this;
    }

    /**
     * Verify that element has the same height as every element in the list
     *
     * @param elements
     * @return UIValidator
     */
    @Override
    public UIValidator sameHeightAs(List<WebElement> elements) {
        getBase().sameHeightAs(elements);
        return this;
    }

    @Override
    public UIValidator hasHeight(Condition<Scalar> condition) {
        getBase().hasHeight(condition);
        return this;
    }

    /**
     * Verify that element has the same size as specified element
     *
     * @param element
     * @param readableName
     * @return UIValidator
     */
    @Override
    public UIValidator sameSizeAs(WebElement element, String readableName) {
        getBase().sameSizeAs(element, readableName);
        return this;
    }

    /**
     * Verify that element has the same size as every element in the list
     *
     * @param elements
     * @return UIValidator
     */
    @Override
    public UIValidator sameSizeAs(List<WebElement> elements) {
        getBase().sameSizeAs(elements);
        return this;
    }

    /**
     * Verify that element has not the same size as specified element
     *
     * @param element
     * @param readableName
     * @return UIValidator
     */
    @Override
    public UIValidator notSameSizeAs(WebElement element, String readableName) {
        getBase().notSameSizeAs(element, readableName);
        return this;
    }

    /**
     * Verify that element has not the same size as every element in the list
     *
     * @param elements
     * @return UIValidator
     */
    @Override
    public UIValidator notSameSizeAs(List<WebElement> elements) {
        getBase().notSameSizeAs(elements);
        return this;
    }

    /**
     * Verify that height of element is in range
     *
     * @param min
     * @param max
     * @return UIValidator
     */
    @Override
    public UIValidator hasHeightBetween(int min, int max) {
        getBase().hasHeightBetween(min, max);
        return this;
    }

    /**
     * Verify that min offset of element is not less than (min value is -10000)
     *
     * @param top
     * @param right
     * @param bottom
     * @param left
     * @return UIValidator
     */
    @Override
    public UIValidator minOffset(int top, int right, int bottom, int left) {
        getBase().minOffset(top, right, bottom, left);
        return this;
    }

    @Override
    public UIElementValidator hasLeftOffsetToPage(Condition<Scalar> condition) {
        getBase().hasLeftOffsetToPage(condition);
        return this;
    }

    @Override
    public UIElementValidator hasRightOffsetToPage(Condition<Scalar> condition) {
        getBase().hasRightOffsetToPage(condition);
        return this;
    }

    @Override
    public UIElementValidator hasTopOffsetToPage(Condition<Scalar> condition) {
        getBase().hasTopOffsetToPage(condition);
        return this;
    }

    @Override
    public UIElementValidator hasBottomOffsetToPage(Condition<Scalar> condition) {
        getBase().hasBottomOffsetToPage(condition);
        return this;
    }

    /**
     * Verify that max offset of element is not bigger than (min value is -10000)
     *
     * @param top
     * @param right
     * @param bottom
     * @param left
     * @return UIValidator
     */
    @Override
    public UIValidator maxOffset(int top, int right, int bottom, int left) {
        getBase().maxOffset(top, right, bottom, left);
        return this;
    }

    /**
     * Verify that element has correct CSS values
     *
     * @param cssProperty
     * @param args
     * @return UIValidator
     */
    @Override
    public UIValidator hasCssValue(String cssProperty, String... args) {
        getBase().hasCssValue(cssProperty, args);
        return this;
    }

    /**
     * Verify that concrete CSS values are absent for specified element
     *
     * @param cssProperty
     * @param args
     * @return UIValidator
     */
    @Override
    public UIValidator doesNotHaveCssValue(String cssProperty, String... args) {
        getBase().doesNotHaveCssValue(cssProperty, args);
        return this;
    }

    /**
     * Verify that element has equal left and right offsets (e.g. Bootstrap container)
     *
     * @return UIValidator
     */
    @Override
    public UIValidator equalLeftRightOffset() {
        getBase().equalLeftRightOffset();
        return this;
    }

    /**
     * Verify that element has equal top and bottom offset (aligned vertically in center)
     *
     * @return UIValidator
     */
    @Override
    public UIValidator equalTopBottomOffset() {
        getBase().equalTopBottomOffset();
        return this;
    }

    /**
     * Verify that element(s) is(are) located inside of specified element
     *
     * @param containerElement
     * @param readableContainerName
     * @return ResponsiveUIValidator
     */
    @Override
    public UIValidator isInsideOf(WebElement containerElement, String readableContainerName) {
        getBase().isInsideOf(containerElement, readableContainerName);
        return this;
    }

    @Override
    public UIValidator isInsideOf(WebElement containerElement, String readableContainerName, Padding padding) {
        getBase().isInsideOf(containerElement, readableContainerName, padding);
        return this;
    }
}