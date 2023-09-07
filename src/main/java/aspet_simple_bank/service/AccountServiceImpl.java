package aspet_simple_bank.service;

import aspet_simple_bank.exceptions.BadRequestException;
import aspet_simple_bank.model.client.domain_model.Account;
import aspet_simple_bank.model.client.dto.AccountDTO;
import aspet_simple_bank.model.client.factory.AccountDTOFactory;
import aspet_simple_bank.store.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountDTOFactory accountDTOFactory;

    @Transactional
    @Override
    public List<AccountDTO> getAccounts() {
        return accountRepository.findAll().stream()
                .map(accountDTOFactory::createAccountDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public AccountDTO transfer(String accountFrom, String accountTo, String incomePinCode, double sumToTransfer) {
        Optional<Account> nonNullAccountFrom = accountRepository.findAccountByName(accountFrom);
        Optional<Account> nonNullAccountTo = accountRepository.findAccountByName(accountTo);
        if(nonNullAccountFrom.isPresent()
                && nonNullAccountTo.isPresent()
                && nonNullAccountFrom.get().getPinCode().equals(incomePinCode)
                && nonNullAccountFrom.get().getBalance() >= sumToTransfer) {
            nonNullAccountFrom.get().setBalance(nonNullAccountFrom.get().getBalance() - sumToTransfer);
            nonNullAccountTo.get().setBalance(nonNullAccountTo.get().getBalance() + sumToTransfer);
            accountRepository.save(nonNullAccountFrom.get());
            accountRepository.save(nonNullAccountTo.get());
        } else {
            log.error("AccountServiceImpl{} transfer(): "
                    + accountFrom + " tried to transfer on account: " + accountTo
                    + "with pin code: " + incomePinCode + ".");
            throw new BadRequestException(
                    "Check accounts names or pin code "
                    + "or sum of transfer is higher then balance on account " + accountFrom + " is");
        }
        var dto = accountDTOFactory.createAccountDTO(nonNullAccountFrom.get());
        return dto;
    }

    @Transactional
    @Override
    public AccountDTO makeDeposit(String name, String incomePinCode, double deposit) {
        Optional<Account> account = accountRepository.findAccountByName(name);
        if(account.isPresent() && account.get().getPinCode().equals(incomePinCode)) {
            account.get().setBalance(account.get().getBalance() + deposit);
            accountRepository.save(account.get());
        } else {
            log.error("AccountServiceImpl{} deposit(): "
                    + "User tried to deposit on name: " + name + "with pin code: " + incomePinCode);
            throw new BadRequestException("Name is invalid or pin code is invalid");
        }
        return accountDTOFactory.createAccountDTO(account.get());
    }

    @Transactional
    @Override
    public AccountDTO withDrawDeposit(String name, String incomePinCode, double withDraw) {
        Optional<Account> account = accountRepository.findAccountByName(name);
        if(account.isPresent()
                && account.get().getPinCode().equals(incomePinCode)
                && account.get().getBalance() >= withDraw) {
            account.get().setBalance(account.get().getBalance() - withDraw);
            accountRepository.save(account.get());
        } else {
            log.error("AccountServiceImpl{} withDraw(): "
                    + "User tried to deposit on name: " + name + "with pin code: " + incomePinCode + "."
                    + "Balance is: " + account.get().getBalance() + "."
                    + "Withdrow is: " + withDraw + ".");
            throw new BadRequestException(
                    "Name is invalid or pin code is invalid or sum of withdraw is higher then balance is");
        }
        return accountDTOFactory.createAccountDTO(account.get());
    }

    @Transactional
    @Override
    public Optional<Account> createAccount(String name, String pinCode) {
        return Optional.of(accountRepository.save(Account.builder()
                .name(name)
                .pinCode(pinCode)
                .balance(0.0)
                .build()));
    }
}
