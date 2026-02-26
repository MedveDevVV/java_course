package autoservice.repository.impl;

import autoservice.model.CarServiceMaster;
import autoservice.repository.MasterRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public class JpaMasterRepository extends AbstractJpaRepository implements MasterRepository {
    @Override
    public void addMaster(CarServiceMaster master) {
        execute("Добавление мастера",
                em -> em.persist(master),
                master.getFullName());
    }

    @Override
    public void removeMaster(CarServiceMaster master) {
        execute("Удаление мастера",
                em -> {
                    em.createQuery("DELETE CarServiceMaster csm where csm.id = :id")
                            .setParameter("id", master.getId())
                            .executeUpdate();
                },
                master.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarServiceMaster> getAllMasters() {
        return executeWithResult("Получение всех мастеров",
                em ->
                em.createQuery("select csm from CarServiceMaster csm order by csm.fullName", CarServiceMaster.class)
                        .getResultList()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CarServiceMaster> findMasterByFullName(String fullName) {
        return executeWithResult("Поиск мастера по полному имени",
                em ->
                em.createQuery("select csm from CarServiceMaster csm where lower(csm.fullName) = lower(:fullName)", CarServiceMaster.class)
                        .setParameter("fullName", fullName)
                        .getResultStream()
                        .findFirst(),
                fullName
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CarServiceMaster> findMasterById(UUID id) {
        return executeWithResult("Поиск мастера по ID",
                em ->
                em.createQuery("select csm from CarServiceMaster csm where csm.id = :id", CarServiceMaster.class)
                        .setParameter("id", id)
                        .getResultStream()
                        .findFirst(),
                id
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarServiceMaster> findMastersByFullNameContaining(String fullName) {
        return executeWithResult("Поиск мастеров по части имени",
                em ->
                em.createQuery("select csm from CarServiceMaster csm where csm.fullName ilike :fullName", CarServiceMaster.class)
                        .setParameter("fullName", "%" + fullName + "%")
                        .getResultList(),
                fullName
        );
    }
}
