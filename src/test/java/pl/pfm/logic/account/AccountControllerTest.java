package pl.pfm.logic.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import pl.pfm.model.account.Account;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    private Account account1 = Account.builder().id(1).name("BZWBK").value(BigDecimal.TEN).build();

    @Before
    public void beforeTest() throws Exception {
        accountRepository.deleteAllInBatch();
    }

    @Test
    public void shouldGetSingleAccountWhenOnlyOneAccountWasLoaded() throws Exception {
        // given
        loadAccount(account1);

        // when
        mockMvc.perform(
                get("/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON_UTF8))

                // then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is((int) account1.getId()))) // TODO [PK] jsonPath interprets number as int
                .andExpect(jsonPath("$[0].name", is(account1.getName())))
                .andExpect(jsonPath("$[0].value", is(account1.getValue().doubleValue()))); // TODO [PK] jsonPath interprets number as double
    }

    @Test
    public void getOneAccount() {
    }

    @Test
    public void postAccount() {
    }

    @Test
    public void putAccount() {
    }

    @Test
    public void deleteAccount() {
    }

    private void loadAccounts(List<Account> accounts) throws Exception {
        for (Account account : accounts) {
            loadAccount(account);
        }

    }

    private void loadAccount(Account account) throws Exception {
        mockMvc.perform(
                post("/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(asJson(account))
        )
                .andExpect(status().isOk());
    }

    private String asJson(Account account) throws JsonProcessingException {
        return objectMapper.writeValueAsString(account);
    }
}