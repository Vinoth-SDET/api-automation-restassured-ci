package com.vinoth.automation.dataproviders;

import com.vinoth.automation.models.request.UserRequest;
import com.vinoth.automation.utils.TestDataFactory;
import com.vinoth.automation.utils.TestDataLoader;
import org.testng.annotations.DataProvider;

/**
 * TestNG DataProvider for User API tests.
 *
 * NOTE: parallel = false on all providers — TestNG 7.9 requires the calling
 * test method to be in a parallel suite for parallelism; setting parallel=true
 * on the DataProvider itself in an external class causes "DataProvider not found"
 * errors in some TestNG versions.
 */
public class UserDataProvider {

    @DataProvider(name = "validUsers")
    public static Object[][] validUsers() {
        return TestDataLoader
                .loadList("testdata/users/valid-users.json", UserRequest.class)
                .stream()
                .map(u -> new Object[]{u})
                .toArray(Object[][]::new);
    }

    @DataProvider(name = "invalidUsers")
    public static Object[][] invalidUsers() {
        return TestDataLoader
                .loadList("testdata/users/invalid-users.json", UserRequest.class)
                .stream()
                .map(u -> new Object[]{u})
                .toArray(Object[][]::new);
    }

    @DataProvider(name = "randomUsers")
    public static Object[][] randomUsers() {
        return new Object[][]{
                {TestDataFactory.randomUser()},
                {TestDataFactory.randomUser()},
                {TestDataFactory.minimalUser()},
        };
    }

    @DataProvider(name = "boundaryUserIds")
    public static Object[][] boundaryUserIds() {
        return new Object[][]{
                {0,                 "zero id"},
                {-1,                "negative id"},
                {Integer.MAX_VALUE, "max integer id"},
                {99999,             "non-existent id"},
        };
    }
}