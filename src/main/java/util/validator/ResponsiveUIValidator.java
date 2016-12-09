package util.validator;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.*;
import org.openqa.selenium.Point;
import util.driver.PageValidator;
import util.general.HtmlReportBuilder;
import util.general.SystemHelper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static environment.EnvironmentFactory.isChrome;
import static util.general.SystemHelper.isAutomotionFolderExists;
import static util.general.SystemHelper.isRetinaDisplay;
import static util.validator.Constants.*;
import static util.validator.ResponsiveUIValidator.Units.PX;

public class ResponsiveUIValidator {

    static final int MIN_OFFSET = -10000;
    private final static Logger LOG = Logger.getLogger(ResponsiveUIValidator.class);
    protected static WebDriver driver;
    static WebElement rootElement;
    static long startTime;
    private static boolean withReport = false;
    private static String scenarioName = "Default";
    private static Color rootColor = new Color(255, 0, 0, 255);
    private static Color highlightedElementsColor = new Color(255, 0, 255, 255);
    private static Color linesColor = Color.ORANGE;
    private static File screenshot;
    private static BufferedImage img;
    private static Graphics2D g;
    String rootElementReadableName = "Root Element";
    List<WebElement> rootElements;
    boolean drawLeftOffsetLine = false;
    boolean drawRightOffsetLine = false;
    boolean drawTopOffsetLine = false;
    boolean drawBottomOffsetLine = false;
    ResponsiveUIValidator.Units units = PX;
    int xRoot;
    int yRoot;
    int widthRoot;
    int heightRoot;
    int pageWidth;
    int pageHeight;
    int rootElementRightOffset;
    int rootElementBottomOffset;
    private JSONArray errorMessage;

    public ResponsiveUIValidator(WebDriver driver) {
        ResponsiveUIValidator.driver = driver;
        errorMessage = new JSONArray();
    }

    public void setColorForRootElement(Color color) {
        rootColor = color;
    }

    public void setColorForHighlightedElements(Color color) {
        highlightedElementsColor = color;
    }

    public void setLinesColor(Color color) {
        linesColor = color;
    }

    public ResponsiveUIValidator init() {
        return new ResponsiveUIValidator(driver);
    }

    public ResponsiveUIValidator init(String scenarioName) {
        ResponsiveUIValidator.scenarioName = scenarioName;
        return new ResponsiveUIValidator(driver);
    }

    public UIValidator findElement(WebElement element, String readableNameOfElement) {
        return new UIValidator(driver, element, readableNameOfElement);
    }

    public ResponsiveUIChunkValidator findElements(java.util.List<WebElement> elements) {
        return new ResponsiveUIChunkValidator(driver, elements);
    }

    public ResponsiveUIValidator insideOf(WebElement element, String readableContainerName) {
        validateInsideOfContainer(element, readableContainerName);
        return this;
    }

    public ResponsiveUIValidator drawMap() {
        withReport = true;
        return this;
    }

    public boolean validate() {
        JSONObject jsonResults = new JSONObject();
        jsonResults.put(ERROR_KEY, false);

        if (rootElement != null) {
            if (!errorMessage.isEmpty()) {
                jsonResults.put(ERROR_KEY, true);
                jsonResults.put(DETAILS, errorMessage);
            }

            if (withReport && !errorMessage.isEmpty()) {
                try {
                    screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                    img = ImageIO.read(screenshot);
                } catch (Exception e) {
                    LOG.error("Failed to create screenshot file: " + e.getMessage());
                }

                if (!errorMessage.isEmpty()) {
                    JSONObject rootDetails = new JSONObject();
                    rootDetails.put(X, xRoot);
                    rootDetails.put(Y, yRoot);
                    rootDetails.put(WIDTH, widthRoot);
                    rootDetails.put(HEIGHT, heightRoot);

                    jsonResults.put(SCENARIO, scenarioName);
                    jsonResults.put(ROOT_ELEMENT, rootDetails);
                    jsonResults.put(TIME_EXECUTION, String.valueOf(System.currentTimeMillis() - startTime) + " milliseconds");
                    jsonResults.put(ELEMENT_NAME, rootElementReadableName);
                    jsonResults.put(SCREENSHOT, rootElementReadableName.replace(" ", "") + "-" + screenshot.getName());
                }

                long ms = System.currentTimeMillis();
                try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(TARGET_AUTOMOTION_JSON + rootElementReadableName.replace(" ", "") + "-automotion" + ms + ".json"), StandardCharsets.UTF_8))) {
                    writer.write(jsonResults.toJSONString());
                } catch (IOException ex) {
                    LOG.error("Cannot create json report: " + ex.getMessage());
                }
                try {
                    File file = new File(TARGET_AUTOMOTION_JSON + rootElementReadableName.replace(" ", "") + "-automotion" + ms + ".json");
                    if (file.getParentFile().mkdirs()) {
                        if (file.createNewFile()) {
                            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                            writer.write(jsonResults.toJSONString());
                            writer.close();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if ((boolean) jsonResults.get(ERROR_KEY)) {
                    drawScreenshot();
                }
            }
        } else {
            jsonResults.put(ERROR_KEY, true);
            jsonResults.put(DETAILS, "Set root web element");
        }

        return !((boolean) jsonResults.get(ERROR_KEY));
    }

    public void generateReport() {
        if (withReport && isAutomotionFolderExists()) {
            try {
                new HtmlReportBuilder().buildReport();
            } catch (IOException | ParseException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void generateReport(String name) {
        if (withReport && isAutomotionFolderExists()) {
            try {
                new HtmlReportBuilder().buildReport(name);
            } catch (IOException | ParseException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    void drawScreenshot() {
        g = img.createGraphics();

        drawRoot(rootColor);

        for (Object obj : errorMessage) {
            JSONObject det = (JSONObject) obj;
            JSONObject details = (JSONObject) det.get(REASON);
            JSONObject numE = (JSONObject) details.get(ELEMENT);

            if (numE != null) {
                float x = (float) numE.get(X);
                float y = (float) numE.get(Y);
                float width = (float) numE.get(WIDTH);
                float height = (float) numE.get(HEIGHT);

                g.setColor(highlightedElementsColor);
                g.setStroke(new BasicStroke(2));
                if (isRetinaDisplay() && isChrome()) {
                    g.drawRect(2 * (int) x, 2 * (int) y, 2 * (int) width, 2 * (int) height);
                    //g.fillRect(2 * (int) x, 2 * (int) y, 2 * (int) width, 2 * (int) height);
                } else {
                    g.drawRect((int) x, (int) y, (int) width, (int) height);
                    //g.fillRect(2 * (int) x, 2 * (int) y, 2 * (int) width, 2 * (int) height);
                }
            }
        }

        try {
            ImageIO.write(img, "png", screenshot);
            File file = new File(TARGET_AUTOMOTION_IMG + rootElementReadableName.replace(" ", "") + "-" + screenshot.getName());
            FileUtils.copyFile(screenshot, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void validateElementsAreNotOverlapped(List<WebElement> rootElements) {
        for (WebElement el1 : rootElements) {
            for (WebElement el2 : rootElements) {
                if (!el1.equals(el2)) {
                    if (elementsAreOverlapped(el1, el2)) {
                        putJsonDetailsWithElement("Elements are overlapped", el1);
                        break;
                    }
                }
            }
        }
    }

    void validateGridAlignment(int columns, int rows) {
        if (rootElements != null) {
            ConcurrentHashMap<Integer, AtomicLong> map = new ConcurrentHashMap<>();
            for (WebElement el : rootElements) {
                Integer y = el.getLocation().y;

                map.putIfAbsent(y, new AtomicLong(0));
                map.get(y).incrementAndGet();
            }

            int mapSize = map.size();
            if (rows > 0) {
                if (mapSize != rows) {
                    putJsonDetailsWithoutElement("Elements in a grid are not aligned properly. Looks like grid has wrong amount of rows. Expected is " + rows + ". Actual is " + mapSize + "");
                }
            }

            if (columns > 0) {
                int rowCount = 1;
                for (Map.Entry<Integer, AtomicLong> entry : map.entrySet()) {
                    if (rowCount <= mapSize) {
                        int actualInARow = entry.getValue().intValue();
                        if (actualInARow != columns) {
                            putJsonDetailsWithoutElement("Elements in a grid are not aligned properly in row #" + rowCount + ". Expected " + columns + " elements in a row. Actually it's " + actualInARow + "");
                        }
                        rowCount++;
                    }
                }
            }
        }
    }

    void validateRightOffsetForChunk(List<WebElement> elements) {
        for (int i = 0; i < elements.size() - 1; i++) {
            if (!elementsHaveEqualLeftRightOffset(false, elements.get(i), elements.get(i + 1))) {
                putJsonDetailsWithElement("Element #" + (i + 1) + " has not the same right offset as element #" + (i + 2) + "", elements.get(i + 1));
            }
        }
    }

    void validateLeftOffsetForChunk(List<WebElement> elements) {
        for (int i = 0; i < elements.size() - 1; i++) {
            if (!elementsHaveEqualLeftRightOffset(true, elements.get(i), elements.get(i + 1))) {
                putJsonDetailsWithElement("Element #" + (i + 1) + " has not the same left offset as element #" + (i + 2) + "", elements.get(i + 1));
            }
        }
    }

    void validateTopOffsetForChunk(List<WebElement> elements) {
        for (int i = 0; i < elements.size() - 1; i++) {
            if (!elementsHaveEqualTopBottomOffset(true, elements.get(i), elements.get(i + 1))) {
                putJsonDetailsWithElement("Element #" + (i + 1) + " has not the same top offset as element #" + (i + 2) + "", elements.get(i + 1));
            }
        }
    }

    void validateBottomOffsetForChunk(List<WebElement> elements) {
        for (int i = 0; i < elements.size() - 1; i++) {
            if (!elementsHaveEqualTopBottomOffset(false, elements.get(i), elements.get(i + 1))) {
                putJsonDetailsWithElement("Element #" + (i + 1) + " has not the same bottom offset as element #" + (i + 2) + "", elements.get(i + 1));
            }
        }
    }

    void validateRightOffsetForElements(WebElement element, String readableName) {
        if (!element.equals(rootElement)) {
            if (!elementsHaveEqualLeftRightOffset(false, element)) {
                putJsonDetailsWithElement(String.format("Element '%s' has not the same right offset as element '%s'", rootElementReadableName, readableName), element);
            }
        }
    }

    void validateLeftOffsetForElements(WebElement element, String readableName) {
        if (!element.equals(rootElement)) {
            if (!elementsHaveEqualLeftRightOffset(true, element)) {
                putJsonDetailsWithElement(String.format("Element '%s' has not the same left offset as element '%s'", rootElementReadableName, readableName), element);
            }
        }
    }

    void validateTopOffsetForElements(WebElement element, String readableName) {
        if (!element.equals(rootElement)) {
            if (!elementsHaveEqualTopBottomOffset(true, element)) {
                putJsonDetailsWithElement(String.format("Element '%s' has not the same top offset as element '%s'", rootElementReadableName, readableName), element);
            }
        }
    }

    void validateBottomOffsetForElements(WebElement element, String readableName) {
        if (!element.equals(rootElement)) {
            if (!elementsHaveEqualTopBottomOffset(false, element)) {
                putJsonDetailsWithElement(String.format("Element '%s' has not the same bottom offset as element '%s'", rootElementReadableName, readableName), element);
            }
        }
    }

    void validateNotOverlappingWithElements(WebElement element, String readableName) {
        if (!element.equals(rootElement)) {
            if (elementsAreOverlapped(element)) {
                putJsonDetailsWithElement(String.format("Element '%s' is overlapped with element '%s' but should not", rootElementReadableName, readableName), element);
            }
        }
    }

    void validateOverlappingWithElements(WebElement element, String readableName) {
        if (!element.equals(rootElement)) {
            if (!elementsAreOverlapped(element)) {
                putJsonDetailsWithElement(String.format("Element '%s' is not overlapped with element '%s' but should be", rootElementReadableName, readableName), element);
            }
        }
    }

    void validateMaxOffset(int top, int right, int bottom, int left) {
        if (xRoot > left) {
            putJsonDetailsWithoutElement(String.format("Expected max left offset of element  '%s' is: %spx. Actual left offset is: %spx", rootElementReadableName, left, xRoot));
        }
        if (yRoot > top) {
            putJsonDetailsWithoutElement(String.format("Expected max top offset of element '%s' is: %spx. Actual top offset is: %spx", rootElementReadableName, top, yRoot));
        }
        if (rootElementRightOffset > right) {
            putJsonDetailsWithoutElement(String.format("Expected max right offset of element  '%s' is: %spx. Actual right offset is: %spx", rootElementReadableName, right, rootElementRightOffset));
        }
        if (rootElementBottomOffset > bottom) {
            putJsonDetailsWithoutElement(String.format("Expected max bottom offset of element  '%s' is: %spx. Actual bottom offset is: %spx", rootElementReadableName, bottom, rootElementBottomOffset));
        }
    }

    void validateMinOffset(int top, int right, int bottom, int left) {
        if (xRoot < left) {
            putJsonDetailsWithoutElement(String.format("Expected min left offset of element  '%s' is: %spx. Actual left offset is: %spx", rootElementReadableName, left, xRoot));
        }
        if (yRoot < top) {
            putJsonDetailsWithoutElement(String.format("Expected min top offset of element  '%s' is: %spx. Actual top offset is: %spx", rootElementReadableName, top, yRoot));
        }
        if (rootElementRightOffset < right) {
            putJsonDetailsWithoutElement(String.format("Expected min top offset of element  '%s' is: %spx. Actual right offset is: %spx", rootElementReadableName, right, rootElementRightOffset));
        }
        if (rootElementBottomOffset < bottom) {
            putJsonDetailsWithoutElement(String.format("Expected min bottom offset of element  '%s' is: %spx. Actual bottom offset is: %spx", rootElementReadableName, bottom, rootElementBottomOffset));
        }
    }

    void validateMaxHeight(int height) {
        if (heightRoot > height) {
            putJsonDetailsWithoutElement(String.format("Expected max height of element  '%s' is: %spx. Actual height is: %spx", rootElementReadableName, height, heightRoot));
        }
    }

    void validateMinHeight(int height) {
        if (heightRoot < height) {
            putJsonDetailsWithoutElement(String.format("Expected min height of element '%s' is: %spx. Actual height is: %spx", rootElementReadableName, height, heightRoot));
        }
    }

    void validateSameHeight(WebElement element, String readableName) {
        if (!element.equals(rootElement)) {
            int h = element.getSize().getHeight();
            if (h != heightRoot) {
                putJsonDetailsWithElement(String.format("Element '%s' has not the same height as %s. Height of '%s' is %spx. Height of element is %spx", rootElementReadableName, readableName, rootElementReadableName, heightRoot, h), element);
            }
        }
    }

    void validateMaxWidth(int width) {
        if (widthRoot > width) {
            putJsonDetailsWithoutElement(String.format("Expected max width of element '%s' is: %spx. Actual width is: %spx", rootElementReadableName, width, widthRoot));
        }
    }

    void validateMinWidth(int width) {
        if (widthRoot < width) {
            putJsonDetailsWithoutElement(String.format("Expected min width of element '%s' is: %spx. Actual width is: %spx", rootElementReadableName, width, widthRoot));
        }
    }

    void validateSameWidth(WebElement element, String readableName) {
        if (!element.equals(rootElement)) {
            int w = element.getSize().getWidth();
            if (w != widthRoot) {
                putJsonDetailsWithElement(String.format("Element '%s' has not the same width as %s. Width of '%s' is %spx. Width of element is %spx", rootElementReadableName, readableName, rootElementReadableName, widthRoot, w), element);
            }
        }
    }

    void validateSameSize(WebElement element, String readableName) {
        if (!element.equals(rootElement)) {
            int h = element.getSize().getHeight();
            int w = element.getSize().getWidth();
            if (h != heightRoot || w != widthRoot) {
                putJsonDetailsWithElement(String.format("Element '%s' has not the same size as %s. Size of '%s' is %spx x %spx. Size of element is %spx x %spx", rootElementReadableName, readableName, rootElementReadableName, widthRoot, heightRoot, w, h), element);
            }
        }
    }

    void validateSameSize(List<WebElement> elements, int type) {
        for (int i = 0; i < elements.size() - 1; i++) {
            int h1 = elements.get(i).getSize().getHeight();
            int w1 = elements.get(i).getSize().getWidth();
            int h2 = elements.get(i + 1).getSize().getHeight();
            int w2 = elements.get(i + 1).getSize().getWidth();
            switch (type) {
                case 0:
                    if (h1 != h2 || w1 != w2) {
                        putJsonDetailsWithElement(String.format("Element #%d has different size. Element size is: [%d, %d]", (i + 1), elements.get(i).getSize().width, elements.get(i).getSize().height), elements.get(i));
                        putJsonDetailsWithElement(String.format("Element #%d has different size. Element size is: [%d, %d]", (i + 2), elements.get(i + 1).getSize().width, elements.get(i + 1).getSize().height), elements.get(i + 1));
                    }
                    break;
                case 1:
                    if (w1 != w2) {
                        putJsonDetailsWithElement(String.format("Element #%d has different width. Element width is: [%d, %d]", (i + 1), elements.get(i).getSize().width, elements.get(i).getSize().height), elements.get(i));
                        putJsonDetailsWithElement(String.format("Element #%d has different width. Element width is: [%d, %d]", (i + 2), elements.get(i + 1).getSize().width, elements.get(i + 1).getSize().height), elements.get(i + 1));
                    }
                    break;
                case 2:
                    if (h1 != h2) {
                        putJsonDetailsWithElement(String.format("Element #%d has different height. Element height is: [%d, %d]", (i + 1), elements.get(i).getSize().width, elements.get(i).getSize().height), elements.get(i));
                        putJsonDetailsWithElement(String.format("Element #%d has different height. Element height is: [%d, %d]", (i + 2), elements.get(i + 1).getSize().width, elements.get(i + 1).getSize().height), elements.get(i + 1));
                    }
            }
        }
    }

    void validateInsideOfContainer(WebElement element, String readableContainerName) {
        float xContainer = element.getLocation().x;
        float yContainer = element.getLocation().y;
        float widthContainer = element.getSize().width;
        float heightContainer = element.getSize().height;
        if (rootElements == null || rootElements.isEmpty()) {
            if (xRoot < xContainer || yRoot < yContainer || (xRoot + widthRoot) > (xContainer + widthContainer) || (yRoot + heightRoot) > (yContainer + heightContainer)) {
                putJsonDetailsWithElement(String.format("Element '%s' is not inside of '%s'", rootElementReadableName, readableContainerName), element);
            }
        } else {
            for (WebElement el : rootElements) {
                float xRoot = el.getLocation().x;
                float yRoot = el.getLocation().y;
                float widthRoot = el.getSize().width;
                float heightRoot = el.getSize().height;
                if (xRoot < xContainer || yRoot < yContainer || (xRoot + widthRoot) > (xContainer + widthContainer) || (yRoot + heightRoot) > (yContainer + heightContainer)) {
                    putJsonDetailsWithElement(String.format("Element is not inside of '%s'", readableContainerName), element);
                }
            }
        }
    }

    void validateBelowElement(WebElement element, int minMargin, int maxMargin) {
        int yBelowElement = element.getLocation().getY();
        int marginBetweenRoot = yBelowElement - yRoot + heightRoot;
        if (marginBetweenRoot < minMargin || marginBetweenRoot > maxMargin) {
            putJsonDetailsWithElement(String.format("Below element aligned not properly. Expected margin should be between %spx and %spx. Actual margin is %spx", minMargin, maxMargin, marginBetweenRoot), element);
        }
    }

    void validateBelowElement(WebElement element) {
        List<WebElement> elements = new ArrayList<>();
        elements.add(rootElement);
        elements.add(element);

        if (!PageValidator.elementsAreAlignedVertically(elements)) {
            putJsonDetailsWithoutElement("Below element aligned not properly");
        }
    }

    void validateAboveElement(WebElement element, int minMargin, int maxMargin) {
        int yAboveElement = element.getLocation().getY();
        int heightAboveElement = element.getSize().getHeight();
        int marginBetweenRoot = yRoot - yAboveElement + heightAboveElement;
        if (marginBetweenRoot < minMargin || marginBetweenRoot > maxMargin) {
            putJsonDetailsWithElement(String.format("Above element aligned not properly. Expected margin should be between %spx and %spx. Actual margin is %spx", minMargin, maxMargin, marginBetweenRoot), element);
        }
    }

    void validateAboveElement(WebElement element) {
        List<WebElement> elements = new ArrayList<>();
        elements.add(element);
        elements.add(rootElement);

        if (!PageValidator.elementsAreAlignedVertically(elements)) {
            putJsonDetailsWithoutElement("Above element aligned not properly");
        }
    }

    void validateRightElement(WebElement element, int minMargin, int maxMargin) {
        int xRightElement = element.getLocation().getX();
        int marginBetweenRoot = xRightElement - xRoot + widthRoot;
        if (marginBetweenRoot < minMargin || marginBetweenRoot > maxMargin) {
            putJsonDetailsWithElement(String.format("Right element aligned not properly. Expected margin should be between %spx and %spx. Actual margin is %spx", minMargin, maxMargin, marginBetweenRoot), element);
        }
    }

    void validateRightElement(WebElement element) {
        List<WebElement> elements = new ArrayList<>();
        elements.add(rootElement);
        elements.add(element);

        if (!PageValidator.elementsAreAlignedHorizontally(elements)) {
            putJsonDetailsWithoutElement("Right element aligned not properly");
        }
    }

    void validateLeftElement(WebElement leftElement, int minMargin, int maxMargin) {
        int xLeftElement = leftElement.getLocation().getX();
        int widthLeftElement = leftElement.getSize().getWidth();
        int marginBetweenRoot = xRoot - xLeftElement + widthLeftElement;
        if (marginBetweenRoot < minMargin || marginBetweenRoot > maxMargin) {
            putJsonDetailsWithElement(String.format("Left element aligned not properly. Expected margin should be between %spx and %spx. Actual margin is %spx", minMargin, maxMargin, marginBetweenRoot), leftElement);
        }
    }

    void validateLeftElement(WebElement leftElement) {
        List<WebElement> elements = new ArrayList<>();
        elements.add(leftElement);
        elements.add(rootElement);

        if (!PageValidator.elementsAreAlignedHorizontally(elements)) {
            putJsonDetailsWithoutElement("Left element aligned not properly");
        }
    }

    boolean elementsAreOverlappedOnBorder(WebElement rootElement, WebElement elementOverlapWith) {
        Point elLoc = elementOverlapWith.getLocation();
        Dimension elSize = elementOverlapWith.getSize();
        int xRoot = rootElement.getLocation().x;
        int yRoot = rootElement.getLocation().y;
        int widthRoot = rootElement.getSize().width;
        int heightRoot = rootElement.getSize().height;

        int sqRootElement = (xRoot + widthRoot) * (yRoot + heightRoot);
        int sqElement = (elLoc.x + elSize.width) * (elLoc.y + elSize.height);

        int sqCommon = 0;
        if ((xRoot < elLoc.x && yRoot == elLoc.y) || (yRoot < elLoc.y && xRoot == elLoc.x)) {
            sqCommon = (xRoot + widthRoot + elSize.width) + (yRoot + heightRoot + elSize.height);
        } else if ((elLoc.x < xRoot && yRoot == elLoc.y) || (elLoc.y < yRoot && xRoot == elLoc.x)) {
            sqCommon = (elLoc.x + elSize.width + widthRoot) * (elLoc.y + elSize.height + heightRoot);
        }

        return sqCommon - sqElement >= sqRootElement;
    }

    boolean elementsAreOverlapped(WebElement elementOverlapWith) {
        Point elLoc = elementOverlapWith.getLocation();
        Dimension elSize = elementOverlapWith.getSize();
        return ((xRoot >= elLoc.x && yRoot > elLoc.y && xRoot < elLoc.x + elSize.width && yRoot < elLoc.y + elSize.height)
                || (xRoot + widthRoot > elLoc.x && yRoot > elLoc.y && xRoot + widthRoot < elLoc.x + elSize.width && yRoot < elLoc.y + elSize.height)
                || (xRoot > elLoc.x && yRoot + heightRoot > elLoc.y && xRoot < elLoc.x + elSize.width && yRoot + heightRoot < elLoc.y + elSize.height)
                || (xRoot + widthRoot > elLoc.x && yRoot + heightRoot > elLoc.y && xRoot + widthRoot < elLoc.x + elSize.width && yRoot + widthRoot < elLoc.y + elSize.height))

                || ((elLoc.x > xRoot && elLoc.y > yRoot && elLoc.x + elSize.width < xRoot && elLoc.y + elSize.height < yRoot)
                || (elLoc.x > xRoot + widthRoot && elLoc.y > yRoot && elLoc.x + elSize.width < xRoot + widthRoot && elLoc.y + elSize.height < yRoot)
                || (elLoc.x > xRoot && elLoc.y > yRoot + heightRoot && elLoc.x + elSize.width < xRoot && elLoc.y + elSize.height < yRoot + heightRoot)
                || (elLoc.x > xRoot + widthRoot && elLoc.y > yRoot + heightRoot && elLoc.x + elSize.width < xRoot + widthRoot && elLoc.y + elSize.height < yRoot + widthRoot))

                || elementsAreOverlappedOnBorder(rootElement, elementOverlapWith);
    }

    boolean elementsAreOverlapped(WebElement rootElement, WebElement elementOverlapWith) {
        Point elLoc = elementOverlapWith.getLocation();
        Dimension elSize = elementOverlapWith.getSize();
        int xRoot = rootElement.getLocation().x;
        int yRoot = rootElement.getLocation().y;
        int widthRoot = rootElement.getSize().width;
        int heightRoot = rootElement.getSize().height;

        return ((xRoot > elLoc.x && yRoot > elLoc.y && xRoot < elLoc.x + elSize.width && yRoot < elLoc.y + elSize.height)
                || (xRoot + widthRoot > elLoc.x && yRoot > elLoc.y && xRoot + widthRoot < elLoc.x + elSize.width && yRoot < elLoc.y + elSize.height)
                || (xRoot > elLoc.x && yRoot + heightRoot > elLoc.y && xRoot < elLoc.x + elSize.width && yRoot + heightRoot < elLoc.y + elSize.height)
                || (xRoot + widthRoot > elLoc.x && yRoot + heightRoot > elLoc.y && xRoot + widthRoot < elLoc.x + elSize.width && yRoot + widthRoot < elLoc.y + elSize.height))

                || ((elLoc.x > xRoot && elLoc.y > yRoot && elLoc.x + elSize.width < xRoot && elLoc.y + elSize.height < yRoot)
                || (elLoc.x > xRoot + widthRoot && elLoc.y > yRoot && elLoc.x + elSize.width < xRoot + widthRoot && elLoc.y + elSize.height < yRoot)
                || (elLoc.x > xRoot && elLoc.y > yRoot + heightRoot && elLoc.x + elSize.width < xRoot && elLoc.y + elSize.height < yRoot + heightRoot)
                || (elLoc.x > xRoot + widthRoot && elLoc.y > yRoot + heightRoot && elLoc.x + elSize.width < xRoot + widthRoot && elLoc.y + elSize.height < yRoot + widthRoot))

                || elementsAreOverlappedOnBorder(rootElement, elementOverlapWith);
    }

    boolean elementsHaveEqualLeftRightOffset(boolean isLeft, WebElement elementToCompare) {
        Point elLoc = elementToCompare.getLocation();
        Dimension elSize = elementToCompare.getSize();

        if (isLeft) {
            return xRoot == elLoc.getX();
        } else {
            return (pageWidth - xRoot + widthRoot) == (pageWidth - elLoc.getX() + elSize.getWidth());
        }
    }

    boolean elementsHaveEqualLeftRightOffset(boolean isLeft, WebElement element, WebElement elementToCompare) {
        Point elLoc = elementToCompare.getLocation();
        Dimension elSize = elementToCompare.getSize();
        int xRoot = element.getLocation().x;
        int widthRoot = element.getSize().width;

        if (isLeft) {
            return xRoot == elLoc.getX();
        } else {
            return (pageWidth - xRoot + widthRoot) == (pageWidth - elLoc.getX() + elSize.getWidth());
        }
    }


    boolean elementsHaveEqualTopBottomOffset(boolean isTop, WebElement elementToCompare) {
        Point elLoc = elementToCompare.getLocation();
        Dimension elSize = elementToCompare.getSize();

        if (isTop) {
            return yRoot == elLoc.getY();
        } else {
            return (pageHeight - yRoot + heightRoot) == (pageHeight - elLoc.getY() + elSize.getHeight());
        }
    }

    boolean elementsHaveEqualTopBottomOffset(boolean isTop, WebElement element, WebElement elementToCompare) {
        Point elLoc = elementToCompare.getLocation();
        Dimension elSize = elementToCompare.getSize();
        int yRoot = element.getLocation().y;
        int heightRoot = element.getSize().height;

        if (isTop) {
            return yRoot == elLoc.getY();
        } else {
            return (pageHeight - yRoot + heightRoot) == (pageHeight - elLoc.getY() + elSize.getHeight());
        }
    }

    void validateEqualLeftRightOffset(WebElement element, String rootElementReadableName){
        if (!elementHasEqualLeftRightOffset(element)){
            putJsonDetailsWithElement(String.format("Element '%s' has not equal left and right offset. Left offset is %dpx, right is %dpx", rootElementReadableName, getLeftOffset(element), getRightOffset(element)), element);
        }
    }

    void validateEqualTopBottomOffset(WebElement element, String rootElementReadableName){
        if (!elementHasEqualTopBottomOffset(element)){
            putJsonDetailsWithElement(String.format("Element '%s' has not equal top and bottom offset. Top offset is %dpx, bottom is %dpx", rootElementReadableName, getTopOffset(element), getBottomOffset(element)), element);
        }
    }

    void validateEqualLeftRightOffset(List<WebElement> elements){
        for (WebElement element: elements) {
            if (!elementHasEqualLeftRightOffset(element)) {
                putJsonDetailsWithElement(String.format("Element '%s' has not equal left and right offset. Left offset is %dpx, right is %dpx", getFormattedMessage(element), getLeftOffset(element), getRightOffset(element)), element);
            }
        }
    }

    void validateEqualTopBottomOffset(List<WebElement> elements){
        for (WebElement element: elements) {
            if (!elementHasEqualTopBottomOffset(element)) {
                putJsonDetailsWithElement(String.format("Element '%s' has not equal top and bottom offset. Top offset is %dpx, bottom is %dpx", getFormattedMessage(element), getTopOffset(element), getBottomOffset(element)), element);
            }
        }
    }

    boolean elementHasEqualLeftRightOffset(WebElement element) {
        return getLeftOffset(element) == getRightOffset(element);
    }

    boolean elementHasEqualTopBottomOffset(WebElement element) {
        return getTopOffset(element) == getBottomOffset(element);
    }

    void drawRoot(Color color) {
        g.setColor(color);
        g.setStroke(new BasicStroke(2));
        if (isRetinaDisplay() && isChrome()) {
            g.drawRect(2 * xRoot, 2 * yRoot, 2 * widthRoot, 2 * heightRoot);
            //g.fillRect(2 * xRoot, 2 * yRoot, 2 * widthRoot, 2 * heightRoot);
        } else {
            g.drawRect(xRoot, yRoot, widthRoot, heightRoot);
            //g.fillRect(xRoot, yRoot, widthRoot, heightRoot);
        }

        Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
        g.setStroke(dashed);
        g.setColor(linesColor);
        if (drawLeftOffsetLine) {
            if (isRetinaDisplay() && isChrome()) {
                g.drawLine(2 * xRoot, 0, 2 * xRoot, 2 * img.getHeight());
            } else {
                g.drawLine(xRoot, 0, xRoot, img.getHeight());
            }
        }
        if (drawRightOffsetLine) {
            if (isRetinaDisplay() && isChrome()) {
                g.drawLine(2 * (xRoot + widthRoot), 0, 2 * (xRoot + widthRoot), 2 * img.getHeight());
            } else {
                g.drawLine(xRoot + widthRoot, 0, xRoot + widthRoot, img.getHeight());
            }
        }
        if (drawTopOffsetLine) {
            if (isRetinaDisplay() && isChrome()) {
                g.drawLine(0, 2 * yRoot, 2 * img.getWidth(), 2 * yRoot);
            } else {
                g.drawLine(0, yRoot, img.getWidth(), yRoot);
            }
        }
        if (drawBottomOffsetLine) {
            if (isRetinaDisplay() && isChrome()) {
                g.drawLine(0, 2 * (yRoot + heightRoot), 2 * img.getWidth(), 2 * (yRoot + heightRoot));
            } else {
                g.drawLine(0, yRoot + heightRoot, img.getWidth(), yRoot + heightRoot);
            }
        }
    }

    void putJsonDetailsWithoutElement(String message) {
        JSONObject details = new JSONObject();
        JSONObject mes = new JSONObject();
        mes.put(MESSAGE, message);
        details.put(REASON, mes);
        errorMessage.add(details);
    }

    void putJsonDetailsWithElement(String message, WebElement element) {
        float xContainer = element.getLocation().getX();
        float yContainer = element.getLocation().getY();
        float widthContainer = element.getSize().getWidth();
        float heightContainer = element.getSize().getHeight();

        JSONObject details = new JSONObject();
        JSONObject elDetails = new JSONObject();
        elDetails.put(X, xContainer);
        elDetails.put(Y, yContainer);
        elDetails.put(WIDTH, widthContainer);
        elDetails.put(HEIGHT, heightContainer);
        JSONObject mes = new JSONObject();
        mes.put(MESSAGE, message);
        mes.put(ELEMENT, elDetails);
        details.put(REASON, mes);
        errorMessage.add(details);
    }

    int getInt(int i, boolean horizontal) {
        if (units.equals(PX)) {
            return i;
        } else {
            if (horizontal) {
                return (i * pageWidth) / 100;
            } else {
                return (i * pageHeight) / 100;
            }
        }
    }

    String getFormattedMessage(WebElement element) {
        return String.format("with properties: tag='%s', id='%s', class='%s', text='%s', coord=[%s,%s], size=[%s,%s]",
                element.getTagName(),
                element.getAttribute("id"),
                element.getAttribute("class"),
                element.getText().length() < 10 ? element.getText() : element.getText().substring(0, 10) + "...",
                String.valueOf(element.getLocation().x),
                String.valueOf(element.getLocation().y),
                String.valueOf(element.getSize().width),
                String.valueOf(element.getSize().height));
    }

    int getLeftOffset(WebElement element) {
        return element.getLocation().x;
    }

    int getRightOffset(WebElement element) {
        return pageWidth - (element.getLocation().x + element.getSize().width);
    }

    int getTopOffset(WebElement element) {
        return element.getLocation().y;
    }

    int getBottomOffset(WebElement element) {
        return pageHeight - (element.getLocation().y + element.getSize().height);
    }

    public enum Units {
        PX,
        PERCENT
    }

}
