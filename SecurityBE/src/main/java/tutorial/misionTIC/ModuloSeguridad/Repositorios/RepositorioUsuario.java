package tutorial.misionTIC.ModuloSeguridad.Repositorios;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import tutorial.misionTIC.ModuloSeguridad.Modelos.Usuario;

public interface RepositorioUsuario extends MongoRepository<Usuario, String> {
    @Query("{'correo': ?0}") //mongo query for el atributo correo ?0 es un parametro dinamico
    public Usuario getUserByEmail(String correo);
}
