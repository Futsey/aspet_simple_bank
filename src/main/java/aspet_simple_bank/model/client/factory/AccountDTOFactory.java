package aspet_simple_bank.model.client.factory;

import aspet_simple_bank.model.client.domain_model.Account;
import aspet_simple_bank.model.client.dto.AccountDTO;
import org.springframework.stereotype.Component;

@Component
public class AccountDTOFactory {

    /**
     * Фабричный метод для создания DTO-сущности аккаунта
     * @param account - исходный экземпляр аккаунта
     * @return DTO-сущность аккаунта
     */
    public AccountDTO createAccountDTO(Account account) {
        return AccountDTO.builder()
                .name(account.getName())
                .balance(account.getBalance())
                .build();
    }
}
