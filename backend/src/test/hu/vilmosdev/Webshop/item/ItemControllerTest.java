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
public class ItemControllerTest {
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
  public void testGetRandomPagable() throws Exception {

    mockMvc.perform(MockMvcRequestBuilders.get("/api/items/random?page=1&size=3"))
      .andExpect(status().isOk())
      .andDo(document("Items/getting_random_items_pagable"));
  }

  @Test
  public void testGetItemsByCategoriesPagable() throws Exception {

    mockMvc.perform(MockMvcRequestBuilders.get("/api/items?page=1&size=8&category=1&category=2"))
      .andExpect(status().isOk())
      .andDo(document("Items/gettingItemsByCategoriesPagable"));
  }

  @Test
  public void testGetItemsByVendorsPagable() throws Exception {

    mockMvc.perform(MockMvcRequestBuilders.get("/api/items?page=1&size=8&vendor=1&vendor=2"))
      .andExpect(status().isOk())
      .andDo(document("Items/gettingItemsByVendorPagable"));
  }

  @Test
  public void testGetItemsByVendorAndCategoriesPagable() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/items?page=1&size=8&vendor=1&vendor=2&category=1&category=2"))
      .andExpect(status().isOk())
      .andDo(document("Items/gettingItemsByVendorAndCategoryPagable"));
  }

  @Test
  public void testGettingItemById() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/items/1"))
      .andExpect(status().isOk())
      .andDo(document("Items/gettingItemById"));
  }

  @Test
  public void testGettingItemFail() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/items/1111111"))
      .andExpect(status().isNotFound())
      .andDo(document("Items/gettingItemByIdFail"));
  }

  @Test
  public void testItemsByIdBatch() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/items/batch?1,2,3"))
      .andExpect(status().isOk())
      .andDo(document("Items/gettingItemsByIdsBatch"));
  }

  @Test
  public void testItemsByIdPartialContent() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/items/batch?1,2,4000000"))
      .andExpect(status().isPartialContent())
      .andDo(document("Items/gettingItemsByIdsBatch"));
  }

  @Test
  public void testGettingImageServed() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/items/uploads/something.png"))
      .andExpect(status().isOk())
      .andDo(document("Items/gettingImageServed"));
  }

  @Test
  public void testImageNotFound() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/items/uploads/something_else.png"))
      .andExpect(status().isNotFound())
      .andDo(document("Items/gettingImageNotFound"));
  }

}
