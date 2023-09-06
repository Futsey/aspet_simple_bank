package aspet_simple_bank.service;

import aspet_simple_bank.model.client.domain_model.Account;
import aspet_simple_bank.model.client.dto.AccountDTO;

import java.util.List;
import java.util.Optional;

public interface AccountService {

    AccountDTO transfer(String accountFrom, String accountTo, String incomePinCode, double sumToTransfer);

    AccountDTO makeDeposit(String name, String incomePinCode, double deposit);

    AccountDTO withDrawDeposit(String name, String incomePinCode, double withDraw);

    List<AccountDTO> getAccounts();

    Optional<Account> createAccount(String name, String pinCode);
}
