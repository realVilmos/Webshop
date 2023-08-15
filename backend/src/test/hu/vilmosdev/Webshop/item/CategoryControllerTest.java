package hu.vilmosdev.Webshop.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class CategoryControllerTest {
  @Autowired
  private WebApplicationContext context;
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private RestDocumentationContextProvider restDocumentation;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Before
  public void setUp(){
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
      .apply(documentationConfiguration(this.restDocumentation))
      .build();
  }

  @Test
  public void gettingCategoryById() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/items/category/2"))
      .andExpect(status().isOk())
      .andDo(document("Category/gettingACategory"));
  }

  @Test
  public void gettingCategoryByIdNotFound() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/items/category/100"))
      .andExpect(status().isNotFound())
      .andDo(document("Category/gettingACategoryNotFound"));
  }

  @Test
  public void gettingAllCategories() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/items/category/main-categories"))
      .andExpect(status().isOk())
      .andDo(document("Category/main-categories"));
  }
}
