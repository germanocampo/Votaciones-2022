package tutorial.misionTIC.ModuloSeguridad.Repositorios;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import tutorial.misionTIC.ModuloSeguridad.Modelos.PermisosRoles;

public interface RepositorioPermisosRoles extends MongoRepository<PermisosRoles, String> {

    @Query("{'rol.$id': ObjectId(?0),'permiso.$id': ObjectId(?1)}") //Neceita dos condiciones, consultas SQL
    PermisosRoles getPermisoRol(String id_rol,String id_permiso);
}
