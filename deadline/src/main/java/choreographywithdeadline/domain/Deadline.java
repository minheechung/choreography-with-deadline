package choreographywithdeadline.domain;

import choreographywithdeadline.DeadlineApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Deadline_table")
@Data
//<<< DDD / Aggregate Root
public class Deadline {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Date deadline;

    private Long orderId;

    private Date startedTime;

    public static DeadlineRepository repository() {
        DeadlineRepository deadlineRepository = DeadlineApplication.applicationContext.getBean(
            DeadlineRepository.class
        );
        return deadlineRepository;
    }

    //<<< Clean Arch / Port Method
    public static void schedule(OrderCreated orderCreated) {
        Deadline deadline = new Deadline();
        deadline.setOrderId(orderCreated.getId());
        deadline.setStartedTime(new Date(orderCreated.getTimestamp()));

        Date deadlineDate = new Date(deadline.getStartedTime().getTime() + deadlineDurationInMS);
        deadline.setDeadline(deadlineDate);
        
        repository().save(deadline);
}
    public static void sendDeadlineEvents(){
        repository().findAll().forEach(deadline ->{
            Date now = new Date();
            
            if(now.after(deadline.getDeadline())){
                new DeadlineReached(deadline).publishAfterCommit();
                repository().delete(deadline);
            }
        });
    }

}
//>>> DDD / Aggregate Root
