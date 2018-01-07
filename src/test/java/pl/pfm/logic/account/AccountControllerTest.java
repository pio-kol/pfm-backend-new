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
import org.springframework.test.web.servlet.MvcResult;
import pl.pfm.model.account.Account;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    private Account account1 = Account.builder().id(1).name("BZWBK").value(new BigDecimal("10.12")).build();

    @Before
    public void beforeTest() throws Exception {
        accountRepository.deleteAllInBatch();
    }

    @Test
    public void shouldGetSingleAccountWhenOnlyOneAccountWasLoaded() throws Exception {
        // given
        loadAccount(account1);

        // when
        MvcResult result = mockMvc.perform(
                get("/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();

        // then
        List<Account> accounts = asListOfAccounts(result);

        assertNotNull(accounts);
        assertEquals(1, accounts.size());
        assertEquals(account1, accounts.get(0));
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

    private List<Account> asListOfAccounts(MvcResult result) throws IOException {
        return objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, Account.class)
        );
    }
}