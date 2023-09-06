package aspet_simple_bank.controller;

import aspet_simple_bank.exceptions.BadRequestException;
import aspet_simple_bank.model.client.domain_model.Account;
import aspet_simple_bank.model.client.dto.AccountDTO;
import aspet_simple_bank.model.client.factory.AccountDTOFactory;
import aspet_simple_bank.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Tag(name = "AccountController", description = "Some operations with deposit")
public class AccountController {

    private final AccountService accountService;
    private final AccountDTOFactory accountDTOFactory;

    /**
     * Метод для получения списка аккаунтов
     * @return List с перечнем аккаунтов
     */
    @Operation(summary = "Get all accounts with name and balance")
    @GetMapping("/api/accounts")
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.accountService.getAccounts());
    }

    /**
     * Метод для создания аккаунта
     * @param incomeName - имя аккаунта
     * @param incomePinCode - пинкод аккаунта
     * @see Optional использован для обработки null
     * @return статус 201 - если аккаунт успешно создан, 409 - если что-то пошло не так
     */
    @Operation(summary = "Create new account")
    @PostMapping("/api/create")
    public ResponseEntity<AccountDTO> createAccount(
            @RequestParam(value = "name", required = true) String incomeName,
            @RequestParam(value = "pin_code", required = true) String incomePinCode) {

        if(incomeName.trim().isEmpty()) {
            throw new BadRequestException("Field name can`t be empty");
        }

        if(incomePinCode.length() != 4) {
            throw new BadRequestException("Pin code must contain four digits");
        }

        Optional<Account> newAcc = accountService.createAccount(incomeName, incomePinCode);

        return newAcc.isPresent() ?
                new ResponseEntity<>(HttpStatus.CREATED)
                : new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    /**
     * Метод для пополнения счета аккаунта
     * @param incomeName - имя аккаунта
     * @param incomePinCode - пинкод аккаунта
     * @param incomeAddSumDeposit - сумма пополнения
     * @see Optional использован для обработки null
     * @return статус 200 - если пополнение успешно
     */
    @Operation(summary = "Make deposit on account")
    @PatchMapping("/api/makeDeposit")
    public ResponseEntity<AccountDTO> makeDeposit(
            @RequestParam(value = "name", required = true) String incomeName,
            @RequestParam(value = "pin_code", required = true) String incomePinCode,
            @RequestParam(value = "deposit", required = true) double incomeAddSumDeposit) {

        if(incomeName.trim().isEmpty()) {
            throw new BadRequestException("Field name can`t be empty");
        }
        if(incomePinCode.length() != 4) {
            throw new BadRequestException("Pin code must contain four digits");
        }
        if(incomeAddSumDeposit <= 0) {
            throw new BadRequestException("The deposit must have a positive balance");
        }
        return new ResponseEntity<>(
                accountService.makeDeposit(incomeName, incomePinCode, incomeAddSumDeposit),
                HttpStatus.OK);
    }

    /**
     * Метод для снятия со счета аккаунта
     * @param incomeName - имя аккаунта
     * @param incomePinCode - пинкод аккаунта
     * @param incomeWithdrawDeposit - сумма снятия
     * @see Optional использован для обработки null
     * @return статус 200 - если снятие успешно
     */
    @Operation(summary = "Make withdraw from deposit on account")
    @PatchMapping("/api/withdrawDeposit")
    public ResponseEntity<AccountDTO> withdrawDeposit(
            @RequestParam(value = "name", required = true) String incomeName,
            @RequestParam(value = "pin_code", required = true) String incomePinCode,
            @RequestParam(value = "deposit", required = true) double incomeWithdrawDeposit) {

        if(incomeName.trim().isEmpty()) {
            throw new BadRequestException("Field name can`t be empty");
        }
        if(incomePinCode.length() != 4) {
            throw new BadRequestException("Pin code must contain four digits");
        }
        if(incomeWithdrawDeposit <= 0) {
            throw new BadRequestException("The deposit must have a positive balance");
        }
        return new ResponseEntity<>(
                accountService.withDrawDeposit(incomeName, incomePinCode, incomeWithdrawDeposit),
                HttpStatus.OK);
    }

    /**
     * Метод для перевода со счета одного аккаунта на счет другого аккаунта
     * @param incomeNameFrom - имя аккаунта - отправителя перевода
     * @param incomeNameTo - имя аккаунта - получателя перевода
     * @param incomePinCode - пинкод аккаунта - отправителя перевода
     * @param remittance - сумма перевода
     * @see Optional использован для обработки null
     * @return статус 200 - если перевод успешен
     */
    @Operation(summary = "Make transfer from one deposit to another")
    @PatchMapping("/api/transfer")
    public ResponseEntity<AccountDTO> transfer(
            @RequestParam(value = "nameFrom", required = true) String incomeNameFrom,
            @RequestParam(value = "nameTo", required = true) String incomeNameTo,
            @RequestParam(value = "pin_code", required = true) String incomePinCode,
            @RequestParam(value = "remittance", required = true) double remittance) {

        if(incomeNameFrom.trim().isEmpty() && incomeNameTo.trim().isEmpty()) {
            throw new BadRequestException("Name fields can`t be empty");
        }
        if(incomePinCode.length() != 4) {
            throw new BadRequestException("Pin code must contain four digits");
        }
        if(remittance <= 0) {
            throw new BadRequestException("The remittance must have a positive balance");
        }
        return new ResponseEntity<>(
                accountService.transfer(incomeNameFrom, incomeNameTo, incomePinCode, remittance),
                HttpStatus.OK);
    }
}
