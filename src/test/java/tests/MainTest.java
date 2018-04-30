package tests;
import com.galenframework.api.Galen;
import com.galenframework.reports.GalenTestInfo;
import com.galenframework.reports.HtmlReportBuilder;
import com.galenframework.reports.model.LayoutReport;

import org.easetech.easytest.annotation.DataLoader;
import org.easetech.easytest.annotation.Param;
import org.easetech.easytest.loader.LoaderType;
import org.easetech.easytest.runner.DataDrivenTestRunner;
import org.junit.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.IOException;
//import java.text.SimpleDateFormat;
import java.util.Arrays;
//import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(DataDrivenTestRunner.class)
@DataLoader(filePaths="src\\test\\java\\resources\\GalenTest.csv",loaderType=LoaderType.CSV)
public class MainTest{
    private static WebDriver driver;

    //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
    public static void initBrowser(String browserType) {
        if(browserType.toLowerCase().contains("chrome"))
            {
                System.setProperty("webdriver.chrome.driver", "src\\test\\java\\resources\\chromedriver.exe");
                driver = new ChromeDriver();
            }
            else if (browserType.toLowerCase().contains("firefox"))
                {
                    System.setProperty("webdriver.gecko.driver", "src\\test\\java\\resources\\geckodriver.exe");
                driver = new FirefoxDriver();
                }
            else if (browserType.toLowerCase().contains("edge"))
                {
                    System.setProperty("webdriver.edge.driver", "src\\test\\java\\resources\\MicrosoftWebDriver.exe");
                driver = new EdgeDriver();
                }
            else{
                System.setProperty("webdriver.chrome.driver", "src\\test\\java\\resources\\chromedriver.exe");
                driver = new ChromeDriver();
            }       
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
    }

    @Test 
    public void testLayout(@Param(name="PageName")String page,@Param(name="URL")String url,@Param(name="Dimensions_Width")Integer dWidth
    ,@Param(name="Dimension_Height")Integer dHeight,@Param(name="ResolutionCategory")String resolutionCategory,@Param(name="Browser")String Browser
    ,@Param(name="TestCaseId")String TestCaseId) throws IOException
    {
        //Initalize browser
        initBrowser(Browser);
        //Set the browser size for desktop
        driver.manage().window().setSize(new Dimension(dWidth, dHeight));
        //Go to aamlive.com
        driver.get("https://qaint3.aamlive.com"+url);

        //Create a layoutReport object
        //checkLayout function checks the layout and returns a LayoutReport object
        LayoutReport layoutReport = Galen.checkLayout(driver, "src\\test\\java\\resources\\specs\\"+page+".gspec", Arrays.asList(resolutionCategory));

        //Create a tests list
        List<GalenTestInfo> tests = new LinkedList<GalenTestInfo>();

        //Create a GalenTestInfo object
        GalenTestInfo test = GalenTestInfo.fromString(page+" layout");

        //Get layoutReport and assign to test object
        test.getReport().layout(layoutReport, "check "+page+" layout");

        //Add test object to the tests list
        tests.add(test);

        //Create a htmlReportBuilder object
        HtmlReportBuilder htmlReportBuilder = new HtmlReportBuilder();

        //Create a report under /tests folder based on tests list
        htmlReportBuilder.build(tests, "src\\test\\java\\TestResults"+"\\"+TestCaseId+"_TestResults");

        //If layoutReport has errors Assert Fail
        if (layoutReport.errors() > 0)
        {
            Assert.fail("Layout test failed");
        }
    }

    @After
    public void tearDown()
    {
        driver.quit();
    }

}
