package pl.pfm.logic.account;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pfm.model.account.Account;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/accounts")
public class AccountController {

    private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @CrossOrigin
    @GetMapping(value = "/")
    public List<Account> getAccounts() {
        return accountService.getAccounts();
    }

    @CrossOrigin
    @GetMapping(value = "/{id}")
    public Account getOneAccount(@PathVariable long id) {
        return accountService.getOneAccount(id);
    }

    @CrossOrigin
    @PostMapping
    public long postAccount(@RequestBody Account account) throws IOException {
        return accountService.postAccount(account);
    }

    @CrossOrigin
    @PutMapping(value = "/{id}")
    public void putAccount(@PathVariable long id, @RequestBody Account account) {
        accountService.putAccount(id, account);
    }

    @CrossOrigin
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable long id) {
        return (accountService.deleteAccount(id) ?
                ResponseEntity.ok() : ResponseEntity.notFound()).build();
    }


}
