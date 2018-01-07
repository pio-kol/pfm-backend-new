package pl.pfm.logic.account;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.pfm.model.account.Account;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/accounts") // TODO [PK] - how to handle versions in more flexible way?
public class AccountController {

    private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @CrossOrigin
    @GetMapping
    public List<Account> getAccounts() {
        return accountService.getAccounts();
    }

    @CrossOrigin
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getOneAccount(@PathVariable long id) {
        Account account = accountService.getOneAccount(id);

        if (account == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(account);
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
