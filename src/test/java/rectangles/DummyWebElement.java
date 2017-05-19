package rectangles;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.util.List;

public class DummyWebElement implements WebElement {

    private final Point location;
    private final Dimension size;

    public DummyWebElement(Point location, Dimension size) {
        this.location = location;
        this.size = size;
    }

    @Override
    public void click() {

    }

    @Override
    public void submit() {

    }

    @Override
    public void sendKeys(CharSequence... charSequences) {

    }

    @Override
    public void clear() {

    }

    @Override
    public String getTagName() {
        return null;
    }

    @Override
    public String getAttribute(String s) {
        return null;
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public List<WebElement> findElements(By by) {
        return null;
    }

    @Override
    public WebElement findElement(By by) {
        return null;
    }

    @Override
    public boolean isDisplayed() {
        return false;
    }

    @Override
    public Point getLocation() {
        return location;
    }

    @Override
    public Dimension getSize() {
        return size;
    }

    @Override
    public Rectangle getRect() {
        return new Rectangle(location, size);
    }

    @Override
    public String getCssValue(String s) {
        return null;
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> outputType) throws WebDriverException {
        return null;
    }

    public static WebElement createElement(int originX, int originY, int cornerX, int cornerY) {
        return new DummyWebElement(
                new Point(originX, originY),
                new Dimension(cornerX-originX, cornerY-originY));
    }

    public static WebElement createRootElement() {
        return new DummyWebElement(
                new Point(RectangleFixture.originX, RectangleFixture.originY),
                new Dimension(RectangleFixture.cornerX-RectangleFixture.originX, RectangleFixture.cornerY-RectangleFixture.originY));
    }

}
