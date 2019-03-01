package base.dao;

import base.pojo.Behavior;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IBehaviorDao {
    List<Behavior> getBehavior(@Param("id") String id);
}
