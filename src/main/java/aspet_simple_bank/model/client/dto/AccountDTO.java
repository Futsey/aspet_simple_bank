package aspet_simple_bank.model.client.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDTO {

    private String name;
    private double balance;
}
