package com.vinoth.automation.dataproviders;

import com.vinoth.automation.models.request.PostRequest;
import com.vinoth.automation.utils.TestDataFactory;
import com.vinoth.automation.utils.TestDataLoader;
import org.testng.annotations.DataProvider;

/**
 * TestNG DataProvider for Post API tests.
 *
 * NOTE: parallel = false — see UserDataProvider for explanation.
 */
public class PostDataProvider {

    @DataProvider(name = "validPosts")
    public static Object[][] validPosts() {
        return TestDataLoader
                .loadList("testdata/posts/valid-posts.json", PostRequest.class)
                .stream()
                .map(p -> new Object[]{p})
                .toArray(Object[][]::new);
    }

    @DataProvider(name = "randomPosts")
    public static Object[][] randomPosts() {
        return new Object[][]{
                {TestDataFactory.randomPost(1)},
                {TestDataFactory.randomPost(2)},
                {TestDataFactory.randomPost(3)},
        };
    }

    @DataProvider(name = "boundaryPostIds")
    public static Object[][] boundaryPostIds() {
        return new Object[][]{
                {0,                 "zero id"},
                {-1,                "negative id"},
                {Integer.MAX_VALUE, "max integer id"},
                {99999,             "non-existent id"},
        };
    }
}