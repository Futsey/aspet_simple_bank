package aspet_simple_bank.controller;

import aspet_simple_bank.exceptions.BadRequestException;
import aspet_simple_bank.model.client.domain_model.Account;
import aspet_simple_bank.model.client.dto.AccountDTO;
import aspet_simple_bank.model.client.factory.AccountDTOFactory;
import aspet_simple_bank.service.AccountService;
import aspet_simple_bank.store.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    AccountService accountService;

    @Mock
    AccountRepository accountRepository;

    @Mock
    AccountDTOFactory dtoFactory;

    @InjectMocks
    AccountController accountController;

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
            .balance(100)
            .build();

    List<AccountDTO> dtoList = new ArrayList<>();

    @BeforeEach
    public void initAccounts() {
        dtoList.add(dtoFactory.createAccountDTO(bob));
        dtoList.add(dtoFactory.createAccountDTO(dart));
    }

    @Test
    void whenGetAllAccountsDTO_ThenReturnsValidResponseEntity() {
        doReturn(dtoList).when(this.accountService).getAccounts();

        var responseEntity = this.accountController.getAllAccounts();

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertEquals(dtoList, responseEntity.getBody());
    }

    @Test
    void whenGetAllAccountsDTO_ThenReturnsInvalidResponseEntity() {
        doReturn(new ArrayList<>()).when(this.accountService).getAccounts();

        var responseEntity = this.accountController.getAllAccounts();

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertNotEquals(dtoList, responseEntity.getBody());
    }

    @Test
    void whenCreateAccount_ThenReturnsValidResponseEntity() {
        doReturn(Optional.of(bob))
                .when(this.accountService)
                .createAccount(bob.getName(), bob.getPinCode());

        var responseEntity = this.accountController.createAccount(
                bob.getName(),
                bob.getPinCode()
        );

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    void whenCreateAccountWithEmptyName_ThenReturnsBadRequestException() {

        try {
            accountController.createAccount("", "1111");
        } catch (BadRequestException e) {
            assertEquals("Field name can`t be empty", e.getMessage());
        }

        verify(accountService, never()).createAccount(any(), any());
    }

    @Test
    void whenCreateAccountWithEmptyPinCode_ThenReturnsBadRequestException() {

        try {
            accountController.createAccount("Oleg", "");
        } catch (BadRequestException e) {
            assertEquals("Pin code must contain four digits", e.getMessage());
        }

        verify(accountService, never()).createAccount(any(), any());
    }

    @Test
    void whenCreateAccount_ThenReturnsConflictResponse() {
        doReturn(Optional.empty())
                .when(this.accountService)
                .createAccount(bob.getName(), bob.getPinCode());

        var responseEntity = this.accountController.createAccount(
                bob.getName(),
                bob.getPinCode()
        );

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
    }

    @Test
    void whenMakeDeposit_ThenReturnsValidResponseEntity() {
        double sumOfDeposit = 100.0D;
        doReturn(dtoList.get(0))
                .when(this.accountService)
                .makeDeposit(bob.getName(), bob.getPinCode(), bob.getBalance());

        var responseEntity = this.accountController.makeDeposit(
                bob.getName(),
                bob.getPinCode(),
                sumOfDeposit
        );

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void whenMakeDepositWithEmptyName_ThenReturnsBadRequestException() {
        try {
            accountController.makeDeposit("", bob.getPinCode(), bob.getBalance());
        } catch (BadRequestException e) {
            assertEquals("Field name can`t be empty", e.getMessage());
        }

        verify(accountService, never()).makeDeposit(any(), any(), anyDouble());
    }

    @Test
    void whenMakeDepositWithEmptyPinCode_ThenReturnsBadRequestException() {
        try {
            accountController.makeDeposit(bob.getName(), "", bob.getBalance());
        } catch (BadRequestException e) {
            assertEquals("Pin code must contain four digits", e.getMessage());
        }

        verify(accountService, never()).makeDeposit(any(), any(), anyDouble());
    }

    @Test
    void whenMakeDepositWithEmptyDeposit_ThenReturnsBadRequestException() {
        double sumOfDeposit = 0.0D;
        try {
            accountController.makeDeposit(bob.getName(), bob.getPinCode(), sumOfDeposit);
        } catch (BadRequestException e) {
            assertEquals("The deposit must have a positive balance", e.getMessage());
        }

        verify(accountService, never()).makeDeposit(any(), any(), anyDouble());
    }

    @Test
    void whenWithdrawDeposit_ThenReturnsValidResponseEntity() {
        double sumAfterWithdraw = 50.0D;
        double sumOfWithdraw = 50.0D;
        doReturn(dtoList.get(0))
                .when(this.accountService)
                .withDrawDeposit(bob.getName(), bob.getPinCode(), sumAfterWithdraw);

        var responseEntity = this.accountController.withdrawDeposit(
                bob.getName(),
                bob.getPinCode(),
                sumOfWithdraw
        );

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void whenWithdrawDepositWithEmptyName_ThenReturnsBadRequestException() {
        try {
            accountController.withdrawDeposit("", bob.getPinCode(), bob.getBalance());
        } catch (BadRequestException e) {
            assertEquals("Field name can`t be empty", e.getMessage());
        }

        verify(accountService, never()).withDrawDeposit(any(), any(), anyDouble());
    }

    @Test
    void whenWithdrawDepositWithEmptyPinCode_ThenReturnsBadRequestException() {
        try {
            accountController.withdrawDeposit(bob.getName(), "", bob.getBalance());
        } catch (BadRequestException e) {
            assertEquals("Pin code must contain four digits", e.getMessage());
        }

        verify(accountService, never()).withDrawDeposit(any(), any(), anyDouble());
    }

    @Test
    void whenWithdrawDepositWithEmptyDeposit_ThenReturnsBadRequestException() {
        try {
            accountController.withdrawDeposit(bob.getName(), bob.getPinCode(), 0.0D);
        } catch (BadRequestException e) {
            assertEquals("The deposit must have a positive balance", e.getMessage());
        }

        verify(accountService, never()).withDrawDeposit(any(), any(), anyDouble());
    }

    @Test
    void whenTransfer_ThenReturnsValidResponseEntity() {
        double sumToTransfer = 30.0D;
        double sumOnDepositAfterTransfer = bob.getBalance() - sumToTransfer;
        bob.setBalance(sumOnDepositAfterTransfer);
        doReturn(dtoFactory.createAccountDTO(bob))
                .when(this.accountService)
                .transfer(bob.getName(), dart.getName(), bob.getPinCode(), sumToTransfer);

        var responseEntity = this.accountController.transfer(
                bob.getName(),
                dart.getName(),
                bob.getPinCode(),
                sumToTransfer
        );

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}