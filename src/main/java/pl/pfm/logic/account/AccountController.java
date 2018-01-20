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

    private AccountValidator accountValidator;

    public AccountController(AccountService accountService, AccountValidator accountValidator) {
        this.accountService = accountService;
        this.accountValidator = accountValidator;
    }

    @CrossOrigin
    @GetMapping
    public List<Account> getAccounts() {
        return accountService.getAccounts();
    }

    @CrossOrigin
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getOneAccount(@PathVariable long id) {
        Account account = accountService.getAccount(id);

        if (account == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(account);
    }

    @CrossOrigin
    @PostMapping
    public ResponseEntity<?> postAccount(@RequestBody Account account) throws IOException {
        List<String> validationResult = accountValidator.validate(account);
        if (!validationResult.isEmpty()) {
            return ResponseEntity.badRequest().body(validationResult);
        }

        Account createdAccount = accountService.postAccount(account);

        return ResponseEntity.ok(Account.builder().id(createdAccount.getId()).build());
    }

    @CrossOrigin
    @PutMapping(value = "/{id}")
    public ResponseEntity<?> putAccount(@PathVariable long id, @RequestBody Account account) {
        List<String> validationResult = accountValidator.validate(account);
        if (!validationResult.isEmpty()) {
            return ResponseEntity.badRequest().body(validationResult);
        }

        if (accountService.getAccount(id) == null) {
            return ResponseEntity.notFound().build();
        }

        accountService.putAccount(id, account);

        return ResponseEntity.ok().build();
    }

    @CrossOrigin
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable long id) {
        if (accountService.getAccount(id) == null) {
            return ResponseEntity.notFound().build();
        }

        accountService.deleteAccount(id);

        return ResponseEntity.ok().build();
    }


}
