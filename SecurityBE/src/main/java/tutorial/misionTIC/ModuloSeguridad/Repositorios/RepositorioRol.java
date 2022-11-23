package tutorial.misionTIC.ModuloSeguridad.Repositorios;

import org.springframework.data.mongodb.repository.MongoRepository;
import tutorial.misionTIC.ModuloSeguridad.Modelos.Rol;

public interface RepositorioRol extends MongoRepository<Rol, String>{
}
