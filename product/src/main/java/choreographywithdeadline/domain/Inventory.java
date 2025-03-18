package choreographywithdeadline.domain;

import choreographywithdeadline.ProductApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Inventory_table")
@Data
//<<< DDD / Aggregate Root
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String productName;

    private String productImage;

    private Integer stock;

    public static InventoryRepository repository() {
        InventoryRepository inventoryRepository = ProductApplication.applicationContext.getBean(
            InventoryRepository.class
        );
        return inventoryRepository;
    }

    //<<< Clean Arch / Port Method
    public static void stockDecrease(DeliveryStarted deliveryStarted) {
       

       

        repository().findById(Long.valueOf(deliveryStarted.getProductId())
        ).ifPresent(inventory->{
            
            inventory.setStock(inventory.getStock() - deliveryStarted.getQty());

            repository().save(inventory);

            StockDecreased stockDecreased =new StockDecreased(inventory);
            stockDecreased.setOrderId(deliveryStarted.getOrderId());
            stockDecreased.publishAfterCommit();


         });
        

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
