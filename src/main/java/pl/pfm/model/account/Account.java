package pl.pfm.model.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String name;

  private BigDecimal value;
}
