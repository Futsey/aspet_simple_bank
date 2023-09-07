package aspet_simple_bank.service;

import aspet_simple_bank.model.client.domain_model.Account;
import aspet_simple_bank.model.client.dto.AccountDTO;
import aspet_simple_bank.model.client.factory.AccountDTOFactory;
import aspet_simple_bank.store.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    AccountRepository accountRepository;

    @Mock
    AccountDTOFactory dtoFactory;

    @InjectMocks
    AccountServiceImpl accountService;

    Account bob = Account.builder()
            .id(1)
            .name("Bob Marley")
            .pinCode("1234")
            .balance(100)
            .build();

    Account dart = Account.builder()
            .id(1)
            .name("Dart Vader")
            .pinCode("1234")
            .balance(200)
            .build();

    List<AccountDTO> dtoList = new ArrayList<>();
    List<Account> accountList = new ArrayList<>();

    @BeforeEach
    public void initAccounts() {
        dtoList.add(dtoFactory.createAccountDTO(bob));
        dtoList.add(dtoFactory.createAccountDTO(dart));
        accountList.add(bob);
        accountList.add(dart);
    }

    @Test
    void whenGetAccounts_Successful() {
        doReturn(accountList).when(this.accountRepository).findAll();

        var responseEntity = this.accountService.getAccounts();

        assertNotNull(responseEntity);
        assertEquals(dtoList, responseEntity.stream().toList());
    }

    @Test
    void whenGetAccounts_EmptyList() {
        List<AccountDTO> emptyList = new ArrayList<>();
        doReturn(emptyList).when(this.accountRepository).findAll();

        var responseEntity = this.accountService.getAccounts();

        assertNotNull(responseEntity);
        assertEquals(emptyList, responseEntity.stream().toList());
    }

    @Test
    void whenTransfer_SuccessfulAndSenderResponseEntityCorrect() {
        double sumToTransfer = 30.0D;
        double sumOnDepositAfterTransfer = bob.getBalance() - sumToTransfer;
        doReturn(Optional.of(bob))
                .when(this.accountRepository)
                .findAccountByName(bob.getName());
        doReturn(Optional.of(dart))
                .when(this.accountRepository)
                .findAccountByName(dart.getName());
        doReturn(AccountDTO.builder()
                    .name(bob.getName())
                    .balance(sumOnDepositAfterTransfer)
                    .build())
                .when(this.dtoFactory)
                .createAccountDTO(bob);

        var responseEntity = this.accountService.transfer(
                bob.getName(),
                dart.getName(),
                bob.getPinCode(),
                sumToTransfer
        );

        assertNotNull(responseEntity);
        assertEquals(bob.getBalance(), responseEntity.getBalance());
        assertEquals(sumOnDepositAfterTransfer, responseEntity.getBalance());
    }

    @Test
    void whenTransfer_SuccessfulAndRecipientResponseEntityCorrect() {
        double sumToTransfer = 30.0D;
        double sumOnDepositAfterTransfer = dart.getBalance() + sumToTransfer;
        doReturn(Optional.of(bob))
                .when(this.accountRepository)
                .findAccountByName(bob.getName());
        doReturn(Optional.of(dart))
                .when(this.accountRepository)
                .findAccountByName(dart.getName());
        doReturn(AccountDTO.builder()
                    .name(bob.getName())
                    .balance(sumOnDepositAfterTransfer)
                    .build())
                .when(this.dtoFactory)
                .createAccountDTO(bob);

        var responseEntity = this.accountService.transfer(
                bob.getName(),
                dart.getName(),
                bob.getPinCode(),
                sumToTransfer
        );

        assertNotNull(responseEntity);
        assertEquals(dart.getBalance(), responseEntity.getBalance());
        assertEquals(sumOnDepositAfterTransfer, responseEntity.getBalance());
    }

    @Test
    void whenMakeDeposit_ThenSuccessfulAndResponseEntityCorrect() {
        double sumToDeposit = 30.0D;
        double sumOnDepositAfterDeposit = bob.getBalance() + sumToDeposit;
        doReturn(Optional.of(bob))
                .when(this.accountRepository)
                .findAccountByName(bob.getName());
        doReturn(AccountDTO.builder()
                .name(bob.getName())
                .balance(sumOnDepositAfterDeposit)
                .build())
                .when(this.dtoFactory)
                .createAccountDTO(bob);

        var responseEntity = this.accountService.makeDeposit(
                bob.getName(),
                bob.getPinCode(),
                sumToDeposit
        );

        assertNotNull(responseEntity);
        assertEquals(bob.getBalance(), responseEntity.getBalance());
        assertEquals(sumOnDepositAfterDeposit, responseEntity.getBalance());
    }

    @Test
    void whenWithdrawDeposit_ThenSuccessfulAndResponseEntityCorrect() {
        double sumToWithdrawDeposit = 30.0D;
        double sumOnDepositAfterDeposit = bob.getBalance() - sumToWithdrawDeposit;
        doReturn(Optional.of(bob))
                .when(this.accountRepository)
                .findAccountByName(bob.getName());
        doReturn(AccountDTO.builder()
                .name(bob.getName())
                .balance(sumOnDepositAfterDeposit)
                .build())
                .when(this.dtoFactory)
                .createAccountDTO(bob);

        var responseEntity = this.accountService.withDrawDeposit(
                bob.getName(),
                bob.getPinCode(),
                sumToWithdrawDeposit
        );

        assertNotNull(responseEntity);
        assertEquals(bob.getBalance(), responseEntity.getBalance());
        assertEquals(sumOnDepositAfterDeposit, responseEntity.getBalance());
    }

    @Test
    void whenCreateAccount_ThenSuccessfulAndResponseEntityCorrect() {
        doReturn(bob)
                .when(this.accountRepository)
                .save(ArgumentMatchers.any(Account.class));

        var responseEntity = this.accountService.createAccount(
                bob.getName(),
                bob.getPinCode()
        );

        assertNotNull(responseEntity.get());
        assertEquals(bob.getName(), responseEntity.get().getName());
        assertEquals(bob.getBalance(), responseEntity.get().getBalance());
    }
}