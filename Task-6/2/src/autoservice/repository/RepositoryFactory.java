package autoservice.repository;

import autoservice.repository.impl.RepairOrderRepository;

public interface RepositoryFactory {
    MasterRepository createMasterRepository();
    WorkshopPlaceRepository createWorkshopPlaceRepository();
    RepairOrderRepository createOrderRepository();
}