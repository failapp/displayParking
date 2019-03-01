package cl.architeq.displayParking.repository;


import cl.architeq.displayParking.entity.Display;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisplayRepository extends CrudRepository<Display, Integer> {

   //Display findByName(String displayName);

   Display findByIpAddr(String ipAddr);


}
