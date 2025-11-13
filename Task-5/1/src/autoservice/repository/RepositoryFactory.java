package autoservice.repository;

public interface RepositoryFactory {
    MasterRepository createMasterRepository();
    WorkshopPlaceRepository createWorkshopPlaceRepository();
    OrderRepository createOrderRepository();
}