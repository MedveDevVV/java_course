package autoservice.repository.impl;

import autoservice.model.CarServiceMaster;
import autoservice.repository.MasterRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Scope("singleton")
public class JpaMasterRepository extends AbstractJpaRepository implements MasterRepository {
    @Override
    public void addMaster(CarServiceMaster master) {
        executeInTransaction(em -> em.persist(master));
        logger.info("Добавлен мастер: {}", master.getFullName());
    }

    @Override
    public void removeMaster(CarServiceMaster master) {
        executeInTransaction(em -> {
            em.createQuery("DELETE CarServiceMaster csm where csm.id = :id")
                    .setParameter("id", master.getId())
                    .executeUpdate();
        });
        logger.info("Удалено мастер: {}", master.getId());
    }

    @Override
    public List<CarServiceMaster> getAllMasters() {
        return executeWithResult(em ->
            em.createQuery("select csm from CarServiceMaster csm order by csm.fullName", CarServiceMaster.class)
                    .getResultList()
        );
    }

    @Override
    public Optional<CarServiceMaster> findMasterByFullName(String fullName) {
        return executeWithResult(em ->
                em.createQuery("select csm from CarServiceMaster csm where csm.fullName = :fullName", CarServiceMaster.class)
                .setParameter("fullName", fullName)
                        .getResultStream()
                        .findFirst()
        );
    }
}
