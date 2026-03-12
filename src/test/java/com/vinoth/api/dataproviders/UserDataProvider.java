package com.vinoth.api.dataproviders;

import com.vinoth.api.models.request.UserRequest;
import com.vinoth.api.utils.TestDataLoader;
import org.testng.annotations.DataProvider;

import java.util.List;

/**
 * TestNG {@link DataProvider} for User API test data.
 *
 * <p>Data is loaded from {@code src/test/resources/testdata/users/}.
 * Adding new test cases is a JSON edit — no code change required.
 *
 * <p>The {@code parallel = true} flag on {@code validUsers} allows TestNG
 * to run each data row in a separate thread (subject to the suite thread pool).
 */
public class UserDataProvider {

    @DataProvider(name = "validUsers", parallel = true)
    public static Object[][] validUsers() {
        List<UserRequest> users = TestDataLoader.loadList(
                "testdata/users/valid-users.json", UserRequest.class);
        return users.stream()
                .map(u -> new Object[]{u})
                .toArray(Object[][]::new);
    }

    @DataProvider(name = "invalidUsers")
    public static Object[][] invalidUsers() {
        List<UserRequest> users = TestDataLoader.loadList(
                "testdata/users/invalid-users.json", UserRequest.class);
        return users.stream()
                .map(u -> new Object[]{u})
                .toArray(Object[][]::new);
    }
}

