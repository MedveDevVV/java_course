package autoservice.repository.impl;

import autoservice.model.CarServiceMaster;
import autoservice.repository.MasterRepository;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaMasterRepository extends AbstractJpaRepository implements MasterRepository {
    public JpaMasterRepository(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public void addMaster(CarServiceMaster master) {
        execute("Добавление мастера",
                session -> session.persist(master),
                master.getFullName());
    }

    @Override
    public void removeMaster(CarServiceMaster master) {
        execute("Удаление мастера",
                session -> {
                    session.createQuery("DELETE CarServiceMaster csm where csm.id = :id")
                            .setParameter("id", master.getId())
                            .executeUpdate();
                },
                master.getId());
    }

    @Override
    public List<CarServiceMaster> getAllMasters() {
        return executeWithResult("Получение всех мастеров",
                session ->
                session.createQuery("select csm from CarServiceMaster csm order by csm.fullName", CarServiceMaster.class)
                        .getResultList()
        );
    }

    @Override
    public Optional<CarServiceMaster> findMasterByFullName(String fullName) {
        return executeWithResult("Поиск мастера по полному имени",
                session ->
                session.createQuery("select csm from CarServiceMaster csm where lower(csm.fullName) = lower(:fullName)", CarServiceMaster.class)
                        .setParameter("fullName", fullName)
                        .getResultStream()
                        .findFirst(),
                fullName
        );
    }

    @Override
    public Optional<CarServiceMaster> findMasterById(UUID id) {
        return executeWithResult("Поиск мастера по ID",
                session ->
                session.createQuery("select csm from CarServiceMaster csm where csm.id = :id", CarServiceMaster.class)
                        .setParameter("id", id)
                        .getResultStream()
                        .findFirst(),
                id
        );
    }

    @Override
    public List<CarServiceMaster> findMastersByFullNameContaining(String fullName) {
        return executeWithResult("Поиск мастеров по части имени",
                session ->
                session.createQuery("select csm from CarServiceMaster csm where csm.fullName ilike :fullName", CarServiceMaster.class)
                        .setParameter("fullName", "%" + fullName + "%")
                        .getResultList(),
                fullName
        );
    }
}
