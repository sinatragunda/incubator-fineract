/**
 * Added 12/10/2022 at 1414
 */ 


import java.math.BigDecimal;
import java.util.*;


import javax.persistence.Transient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


@Entity
@Table(name ="m_charge_tier")
public class ChargeTier extends AbstractCustomPersistence<Long>{


	@Column(name="amount")
	private BigDecimal amount ;

	@Column(name="upperLimit")
	private BigDecimal upperLimit ;

 	@ManyToOne
    @JoinColumn(name = "charge_id")
	private Charge charge ;


	protected ChargeTier(){}


	public ChargeTier(Charge charge ,BigDecimal amount ,BigDecimal upperLimit){
		this.charge = charge ;
		this.amount = amount ;
		this.upperLimit = upperLimit;
	}


}

