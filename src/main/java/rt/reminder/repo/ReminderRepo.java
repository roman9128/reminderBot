package rt.reminder.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rt.reminder.model.Reminder;

import java.util.List;

@Repository
public interface ReminderRepo extends JpaRepository<Reminder, Long> {

    public List<Reminder> findAllByUserID(Long userID);

    public Reminder findFirstByOrderByNextRemindTimeAsc();

    public Reminder findFirstByUserIDOrderByNextRemindTimeAsc(Long userID);
}
