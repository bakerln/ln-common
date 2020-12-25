//package swagger;
//
//import io.github.swagger2markup.GroupBy;
//import io.github.swagger2markup.Language;
//import io.github.swagger2markup.Swagger2MarkupConfig;
//import io.github.swagger2markup.Swagger2MarkupConverter;
//import io.github.swagger2markup.builder.Swagger2MarkupConfigBuilder;
//import io.github.swagger2markup.markup.builder.MarkupLanguage;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.mock.web.MockHttpServletResponse;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//
//import java.nio.file.Paths;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
///**
// * <p>Description: create swagger interface document</p>
// *
// * @author linan
// * @date 2020-10-27
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest
//@AutoConfigureMockMvc
//public class SwaggerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Test
//    public void createSwaggerPdfAndHtml() throws Exception {
//
//        /**
//         * 模拟请求获取api-docs接口数据
//         */
//        MvcResult mvcResult = this.mockMvc.perform(get("/v2/api-docs")
//                .accept("application/json;charset=UTF-8"))
//                .andExpect(status().isOk())
//                .andReturn();
//        MockHttpServletResponse response = mvcResult.getResponse();
//        String swaggerJson = response.getContentAsString();
//        /**
//         *  输出Ascii到单文件
//          */
//        String outputDir = System.getProperty("swagger.output.file");
//        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
//                .withMarkupLanguage(MarkupLanguage.ASCIIDOC)
//                .withOutputLanguage(Language.ZH)
//                .withPathsGroupedBy(GroupBy.TAGS)
//                .withGeneratedExamples()
//                .withoutInlineSchema()
//                .build();
//        Swagger2MarkupConverter.from(swaggerJson)
//                .withConfig(config)
//                .build()
//                .toFile(Paths.get(outputDir));
//    }
//
//
////    /**
////     * 生成AsciiDocs格式文档
////     * @throws Exception
////     */
////    @Test
////    public void generateAsciiDocs() throws Exception {
////        //    输出Ascii格式
////        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
////                .withMarkupLanguage(MarkupLanguage.ASCIIDOC)
////                .withOutputLanguage(Language.ZH)
////                .withPathsGroupedBy(GroupBy.TAGS)
////                .withGeneratedExamples()
////                .withoutInlineSchema()
////                .build();
////
////        Swagger2MarkupConverter.from(new URL("http://localhost:9080/v2/api-docs"))
////                .withConfig(config)
////                .build()
////                .toFolder(Paths.get("D:\\data\\swagger"));
////    }
////    /**
////     * 生成Markdown格式文档
////     * @throws Exception
////     */
////    @Test
////    public void generateMarkdownDocs() throws Exception {
////        //    输出Markdown格式
////        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
////                .withMarkupLanguage(MarkupLanguage.MARKDOWN)
////                .withOutputLanguage(Language.ZH)
////                .withPathsGroupedBy(GroupBy.TAGS)
////                .withGeneratedExamples()
////                .withoutInlineSchema()
////                .build();
////
////        Swagger2MarkupConverter.from(new URL("http://localhost:9080/v2/api-docs"))
////                .withConfig(config)
////                .build()
////                .toFolder(Paths.get("D:\\data\\swagger"));
////    }
////    /**
////     * 生成Confluence格式文档
////     * @throws Exception
////     */
////    @Test
////    public void generateConfluenceDocs() throws Exception {
////        //    输出Confluence使用的格式
////        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
////                .withMarkupLanguage(MarkupLanguage.CONFLUENCE_MARKUP)
////                .withOutputLanguage(Language.ZH)
////                .withPathsGroupedBy(GroupBy.TAGS)
////                .withGeneratedExamples()
////                .withoutInlineSchema()
////                .build();
////
////        Swagger2MarkupConverter.from(new URL("http://localhost:9080/v2/api-docs"))
////                .withConfig(config)
////                .build()
////                .toFolder(Paths.get("D:\\data\\swagger"));
////    }
////    /**
////     * 生成Markdown格式文档,并汇总成一个文件
////     * @throws Exception
////     */
////    @Test
////    public void generateMarkdownDocsToFile() throws Exception {
////        //    输出Markdown到单文件
////        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
////                .withMarkupLanguage(MarkupLanguage.MARKDOWN)
////                .withOutputLanguage(Language.ZH)
////                .withPathsGroupedBy(GroupBy.TAGS)
////                .withGeneratedExamples()
////                .withoutInlineSchema()
////                .build();
////
////        Swagger2MarkupConverter.from(new URL("http://localhost:9080/v2/api-docs"))
////                .withConfig(config)
////                .build()
////                .toFile(Paths.get("./docs/markdown/generated/all"));
////    }
//}
