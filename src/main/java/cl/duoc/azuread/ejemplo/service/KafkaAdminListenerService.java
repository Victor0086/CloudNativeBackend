package cl.duoc.azuread.ejemplo.service;

public interface KafkaAdminListenerService {

    
    void pausarListener(String id);

    void reanudarListener(String id);

    void stopListener(String id);

    void statusListener(String id);

  

  

    
}
