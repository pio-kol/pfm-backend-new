package pl.pfm.logic.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import pl.pfm.model.account.Account;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // clears DB
public class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    private Account account1 = Account.builder().name("BZWBK").value(new BigDecimal("10.12")).build();
    private Account account2 = Account.builder().name("mBank").value(new BigDecimal("11130.89")).build();

    @Test
    public void shouldReturnEmptyListWhenNoAccountsWereAdded() throws Exception {
        // given

        // when
        MvcResult result = callGetAndReturnMvcResult();

        // then
        List<Account> accounts = asListOfAccounts(result);

        assertNotNull(accounts);
        assertEquals(0, accounts.size());
    }


    @Test
    public void shouldGetSingleAccountWhenOnlyOneAccountWasLoaded() throws Exception {
        // given
        loadAccount(account1);

        // when
        MvcResult result = callGetAndReturnMvcResult();

        // then
        List<Account> accounts = asListOfAccounts(result);

        assertNotNull(accounts);
        assertEquals(1, accounts.size());
        assertAccount(1, account1, accounts.get(0));
    }

    @Test
    public void shouldGetMultipleAccountsWhenMultipleAccountsWereLoaded() throws Exception {
        // given
        loadAccounts(Arrays.asList(account1, account2));

        // when
        MvcResult result = callGetAndReturnMvcResult();

        // then
        List<Account> accounts = asListOfAccounts(result);

        assertNotNull(accounts);
        assertEquals(2, accounts.size());

        assertAccount(1, account1, accounts.get(0));
        assertAccount(2, account2, accounts.get(1));

    }


    @Test
    public void shouldReturnSingleAccountByItsId() throws Exception {
        loadAccount(account1);

        // when
        mockMvc.perform(
                get("/v1/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON_UTF8))

                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(account1.getName())))
                .andExpect(jsonPath("$.value", is(account1.getValue().doubleValue()))); // jsonPath returns double instead of BigDecimal
    }

    @Test
    public void shouldReturnNotFoundStatusIfIdDoesNotExists() throws Exception {
        loadAccount(account1);

        // when
        mockMvc.perform(
                get("/v1/accounts/15")
                        .contentType(MediaType.APPLICATION_JSON_UTF8))

                // then
                .andExpect(status().isNotFound());
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

    private void assertAccount(int id, Account expected, Account given) {
        assertEquals(id, given.getId()); // generated by DB
        assertEquals(expected.getName(), given.getName());
        assertEquals(expected.getValue(), given.getValue());
    }

    private MvcResult callGetAndReturnMvcResult() throws Exception {
        return mockMvc.perform(
                get("/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andReturn();
    }

}