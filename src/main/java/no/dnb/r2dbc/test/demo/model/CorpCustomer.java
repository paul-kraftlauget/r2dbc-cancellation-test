package no.dnb.r2dbc.test.demo.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Builder(toBuilder = true)
public class CorpCustomer {
    @Id
    private Integer id;
    private String userId;
}
