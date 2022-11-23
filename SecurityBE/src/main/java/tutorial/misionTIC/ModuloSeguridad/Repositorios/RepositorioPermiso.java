package tutorial.misionTIC.ModuloSeguridad.Repositorios;

import org.springframework.data.mongodb.repository.Query;
import tutorial.misionTIC.ModuloSeguridad.Modelos.Permiso;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface RepositorioPermiso extends MongoRepository<Permiso,String> {

    @Query("{'url':?0,'metodo':?1}") //busca por la url y el metodo
    Permiso getPermiso(String url, String metodo);
}