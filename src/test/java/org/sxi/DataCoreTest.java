package org.sxi;

import org.junit.jupiter.api.Test;
import org.sxi.dao.ArticleDataCore;

import java.util.Map;

public class DataCoreTest {

    @Test
    public void testInitArtcleData() {
        Map<String, Object> data = ArticleDataCore.getArticleDataCore();
        data.forEach((k,v)->{
            System.out.println(v);
        });
    }
}
