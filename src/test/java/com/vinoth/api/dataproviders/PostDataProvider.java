package com.vinoth.api.dataproviders;

import com.vinoth.api.models.request.PostRequest;
import com.vinoth.api.utils.TestDataLoader;
import org.testng.annotations.DataProvider;

import java.util.List;

/**
 * TestNG {@link DataProvider} for Post API test data.
 */
public class PostDataProvider {

    @DataProvider(name = "validPosts", parallel = true)
    public static Object[][] validPosts() {
        List<PostRequest> posts = TestDataLoader.loadList(
                "testdata/posts/valid-posts.json", PostRequest.class);
        return posts.stream()
                .map(p -> new Object[]{p})
                .toArray(Object[][]::new);
    }
}