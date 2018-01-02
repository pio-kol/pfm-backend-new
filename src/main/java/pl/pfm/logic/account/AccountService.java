package pl.pfm.logic.account;

import org.springframework.stereotype.Service;
import pl.pfm.model.account.Account;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AccountService {

    @Resource
    private AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    public Account getOneAccount(long id) {
        return accountRepository.findOne(id);
    }

    public long postAccount(Account account) {
        return accountRepository.save(account).getId();
    }

    public boolean deleteAccount(long id) {
        accountRepository.delete(id);
        return true;
    }

    public void putAccount(long id, Account account) {
        Account accountToUpdate = accountRepository.findOne(id);
        accountToUpdate.setName(account.getName());
        accountToUpdate.setValue(account.getValue());

        accountRepository.save(account);
    }
}
