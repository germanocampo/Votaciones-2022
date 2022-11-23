package tutorial.misionTIC.ModuloSeguridad.Controladores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tutorial.misionTIC.ModuloSeguridad.Modelos.Permiso;
import tutorial.misionTIC.ModuloSeguridad.Modelos.PermisosRoles;
import tutorial.misionTIC.ModuloSeguridad.Modelos.Rol;
import tutorial.misionTIC.ModuloSeguridad.Repositorios.RepositorioPermiso;
import tutorial.misionTIC.ModuloSeguridad.Repositorios.RepositorioPermisosRoles;
import tutorial.misionTIC.ModuloSeguridad.Repositorios.RepositorioRol;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/permisos-roles")
public class ControladorPermisosRoles {
    @Autowired //asistentes que se requiren
    private RepositorioPermisosRoles miRepositorioPermisoRoles;

    @Autowired
    private RepositorioPermiso miRepositorioPermiso;

    @Autowired
    private RepositorioRol miRepositorioRol;


    @GetMapping("")
    public List<PermisosRoles> index(){ //devuelve todos los permisos roles
        return this.miRepositorioPermisoRoles.findAll();
    }

    /**
     * Asignación rol y permiso
     * @param id_rol
     * @param id_permiso
     * @return
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("rol/{id_rol}/permiso/{id_permiso}") //url que toma dos parametros
    public PermisosRoles create(@PathVariable String id_rol,@PathVariable String id_permiso){ //crea una instancia con los parametros a relacionar
        PermisosRoles nuevo=new PermisosRoles(); //crea una nueva instacia de permisoRoles
        Rol elRol=this.miRepositorioRol.findById(id_rol).get(); //busda el rol .get obtiene el rol
        Permiso elPermiso=this.miRepositorioPermiso.findById(id_permiso).get(); //busca permiso y utiliza el .get para obteren el permiso
        if (elRol!=null && elPermiso!=null){ //se vailad que el rol y el permiso existan
            nuevo.setPermiso(elPermiso); //Aplica el Set para la permiso
            nuevo.setRol(elRol);
            return this.miRepositorioPermisoRoles.save(nuevo); //guarda las relaciones
        }else{
            return null;
        }
    }
    @GetMapping("{id}")
    public PermisosRoles show(@PathVariable String id){
        PermisosRoles permisosRolesActual=this.miRepositorioPermisoRoles
                .findById(id)
                .orElse(null);
        return permisosRolesActual;
    }

    /**
     * Modificación Rol y Permiso
     * @param id
     * @param id_rol
     * @param id_permiso
     * @return
     */
    @PutMapping("{id}/rol/{id_rol}/permiso/{id_permiso}")
    public PermisosRoles update(@PathVariable String id,@PathVariable String id_rol,@PathVariable String id_permiso){
        PermisosRoles permisosRolesActual=this.miRepositorioPermisoRoles
                .findById(id)
                .orElse(null);
        Rol elRol=this.miRepositorioRol.findById(id_rol).get();
        Permiso elPermiso=this.miRepositorioPermiso.findById(id_permiso).get();
        if(permisosRolesActual!=null && elPermiso!=null && elRol!=null){
            permisosRolesActual.setPermiso(elPermiso);
            permisosRolesActual.setRol(elRol);
            return this.miRepositorioPermisoRoles.save(permisosRolesActual);
        }else{
            return null;
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{id}")
    public void delete(@PathVariable String id){
        PermisosRoles permisosRolesActual=this.miRepositorioPermisoRoles
                .findById(id)
                .orElse(null);
        if (permisosRolesActual!=null){
            this.miRepositorioPermisoRoles.delete(permisosRolesActual);
        }
    }

    @GetMapping("validar-permiso/rol/{id_rol}") //ruta del endpoint(contacto de atencion
    public PermisosRoles getPermiso(@PathVariable String id_rol,@RequestBody Permiso infoPermiso){ //define el metodo
        Permiso elPermiso=this.miRepositorioPermiso.
                getPermiso(infoPermiso.getUrl(),
                        infoPermiso.getMetodo()); //obtiene el permiso si la url esta asociada a este metodo
        Rol elRol=this.miRepositorioRol.findById(id_rol).get(); //busca el rol poe el id
        if (elPermiso!=null && elRol!=null){ //validacion que el rol y el permiso existen
            return
                    this.miRepositorioPermisoRoles.getPermisoRol(elRol.get_id(),elPermiso.get_id()); //si existen los dos se solicita embiar el permisoRol
        }else{
            return null;
        }
    }
}