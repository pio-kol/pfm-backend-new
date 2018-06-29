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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import pl.pfm.model.account.Account;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    private Account account1 = Account.builder().name("BZWBK").value(new BigDecimal("10.12")).build();
    private Account account2 = Account.builder().name("mBank").value(new BigDecimal("11130.89")).build();
    private Account invalidAccount = Account.builder().id(14L).build();

    @Test
    public void shouldReturnEmptyListWhenNoAccountsWereAdded() throws Exception {
        // given

        // when
        List<Account> accounts = getAccounts();

        assertNotNull(accounts);
        assertEquals(0, accounts.size());
    }


    @Test
    public void shouldGetSingleAccountWhenOnlyOneAccountWasLoaded() throws Exception {
        // given
        saveAccount(account1);

        // when
        List<Account> accounts = getAccounts();

        assertNotNull(accounts);
        assertEquals(1, accounts.size());
        assertAccount(1L, account1, accounts.get(0));
    }

    @Test
    public void shouldGetMultipleAccountsWhenMultipleAccountsWereLoaded() throws Exception {
        // given
        loadAccounts(Arrays.asList(account1, account2));

        // when
        List<Account> accounts = getAccounts();

        assertNotNull(accounts);
        assertEquals(2, accounts.size());

        assertAccount(1L, account1, accounts.get(0));
        assertAccount(2L, account2, accounts.get(1));
    }


    @Test
    public void shouldReturnSingleAccountByItsId() throws Exception {
        saveAccount(account1);

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
    public void shouldReturnNotFoundStatusIfIdDoesNotExistsForGetOperation() throws Exception {
        saveAccount(account1);

        // when
        mockMvc.perform(
                get("/v1/accounts/15")
                        .contentType(MediaType.APPLICATION_JSON_UTF8))

                // then
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldUpdateAccount() throws Exception {
        // given
        saveAccount(account1);

        // when
        MvcResult result = callMethodAndReturnMvcResult(
                put("/v1/accounts/1")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(asJson(account2)));

        // then
        assertThat(result.getResponse().getContentLength(), equalTo(0));
        assertThat(result.getResponse().getContentType(), equalTo(null));

        // get account and verify it's really updated
        result = callMethodAndReturnMvcResult(
                get("/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON_UTF8));
        List<Account> accounts = asListOfAccounts(result);

        assertThat(accounts.size(), equalTo(1));
        assertAccount(1L, account2, accounts.get(0));
    }

    @Test
    public void shouldReturnNotFoundStatusIfIdDoesNotExistsForPutOperation() throws Exception {
        // when
        mockMvc.perform(
                put("/v1/accounts/16")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(asJson(account2)))

                // then
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnValidationErrorsForInvalidAccountForPutOperation() throws Exception {
        // when
        mockMvc.perform(
                put("/v1/accounts/16")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(asJson(invalidAccount)))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]", is("Account id must not be provided, it's generated by the application")))
                .andExpect(jsonPath("$[1]", is("Account name must not be empty")))
                .andExpect(jsonPath("$[2]", is("Account value must be specified")));
    }

    @Test
    public void shouldReturnIdOfNewlyCreatedAccount() throws Exception {
        // when
        mockMvc.perform(
                post("/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(asJson(account1)))

                // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name").doesNotExist())
                .andExpect(jsonPath("$.value").doesNotExist());
    }

    @Test
    public void shouldCreateSequentialIdsForNewlyCreatedAccount() throws Exception {
        // when
        for (int i = 1; i <= 10; ++i) {
            mockMvc.perform(
                    post("/v1/accounts")
                            .contentType(MediaType.APPLICATION_JSON_UTF8)
                            .content(asJson(account1)))

                    // then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(i)))
                    .andExpect(jsonPath("$.name").doesNotExist())
                    .andExpect(jsonPath("$.value").doesNotExist());
        }
    }

    @Test
    public void shouldReturnValidationErrorsForInvalidAccountForPostOperation() throws Exception {
        saveAccount(account1);

        // when
        mockMvc.perform(
                post("/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(asJson(invalidAccount)))

                // then
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]", is("Account id must not be provided, it's generated by the application")))
                .andExpect(jsonPath("$[1]", is("Account name must not be empty")))
                .andExpect(jsonPath("$[2]", is("Account value must be specified")));
    }

    @Test
    public void shouldReturnNotFoundStatusIfIdDoesNotExistsForDeleteOperation() throws Exception {
        // when
        mockMvc.perform(
                delete("/v1/accounts/87"))

                // then
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldDeleteAccount() throws Exception {
        // given
        loadAccounts(Arrays.asList(account1, account2));

        // when
        mockMvc.perform(
                delete("/v1/accounts/2"))

                // then
                .andExpect(status().isOk());

        // then
        List<Account> accounts = getAccounts();

        assertNotNull(accounts);
        assertEquals(1, accounts.size());

        assertAccount(1L, account1, accounts.get(0));
    }

    private List<Account> getAccounts() throws Exception {
        MvcResult result = callMethodAndReturnMvcResult(
                get("/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON_UTF8));

        return asListOfAccounts(result);
    }

    private void loadAccounts(List<Account> accounts) throws Exception {
        for (Account account : accounts) {
            saveAccount(account);
        }

    }

    private void saveAccount(Account account) throws Exception {
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
        assertThat(result.getResponse().getContentType(), equalTo(MediaType.APPLICATION_JSON_UTF8.toString()));

        return objectMapper.readValue(
                result.getResponse().getContentAsByteArray(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, Account.class)
        );
    }

    private void assertAccount(Long id, Account expected, Account given) {
        assertEquals(id, given.getId()); // generated by DB
        assertEquals(expected.getName(), given.getName());
        assertEquals(expected.getValue(), given.getValue());
    }

    private MvcResult callMethodAndReturnMvcResult(MockHttpServletRequestBuilder mockHttpServletRequestBuilder) throws Exception {
        return mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isOk())
                .andReturn();
    }

}