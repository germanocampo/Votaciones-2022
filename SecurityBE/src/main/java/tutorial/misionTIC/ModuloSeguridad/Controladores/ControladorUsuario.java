package tutorial.misionTIC.ModuloSeguridad.Controladores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tutorial.misionTIC.ModuloSeguridad.Modelos.Rol;
import tutorial.misionTIC.ModuloSeguridad.Repositorios.RepositorioUsuario;
import tutorial.misionTIC.ModuloSeguridad.Modelos.Usuario;
import tutorial.misionTIC.ModuloSeguridad.Repositorios.RepositorioRol;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@CrossOrigin //CORS para que sepa la seguridad https - sello de calidad
@RestController //Vuelve a esta clase un controlador vista.  Cumple la funcion de vista y controlador
@RequestMapping("/usuarios") //Es como el router.  un conjunto de servicios comparte una misma direccion
public class ControladorUsuario {
    @Autowired //Crea una instancia de este metodo sin necesitar crear
    private RepositorioUsuario miRepositorioUsuario;

    @Autowired
    private RepositorioRol miRepositorioRol;

    @GetMapping("") //el metodo acontinuacion recive operacion de tipo GET
    public List<Usuario> index(){
        return this.miRepositorioUsuario.findAll(); //utiliza el metodo a ir a la bodega
    }

    @ResponseStatus(HttpStatus.CREATED) //devuelva un HttpStatus cuando se aga esta operacion
    @PostMapping
    public Usuario create(@RequestBody Usuario infoUsuario){ //crea un usuario de tipo jaon llamado infoUsuario en el body
        infoUsuario.setContrasena(convertirSHA256(infoUsuario.getContrasena())); //Encripta la contraseña
        return this.miRepositorioUsuario.save(infoUsuario); //Guarda el usuario den la base de datos
    }

    @GetMapping("{id}")
    public Usuario show(@PathVariable String id){ //parametro de tipo string usa la anotacion variable. busca id en la url
        Usuario usuarioActual=this.miRepositorioUsuario //llama al que se encarga de operar en la base de datos
                .findById(id) //utiliza el metodo de la interface padre
                .orElse(null); //si no existe devuelve el null
        return usuarioActual; //devuelve usuario o null
    }

    @PutMapping("{id}") //id del usuario a buscar
    public Usuario update(@PathVariable String id,@RequestBody Usuario infoUsuario){ //usuario en el body
        Usuario usuarioActual=this.miRepositorioUsuario //llama al que se encarga de operar la base de datos
                .findById(id) //encontrar el usuario
                .orElse(null); //devuelve null si no lo encuentra
        if (usuarioActual!=null){ //
            usuarioActual.setSeudonimo(infoUsuario.getSeudonimo()); //se le puede añadire el seudonimo, correo o contraseña
            usuarioActual.setCorreo(infoUsuario.getCorreo());
            usuarioActual.setContrasena(convertirSHA256(infoUsuario.getContrasena()));//encripta la contraseña
            return this.miRepositorioUsuario.save(usuarioActual);
        }else{
            return null;
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT) // No devuelve nada
    @DeleteMapping("{id}") //recive el id
    public void delete(@PathVariable String id){ //lo busca por la URL
        Usuario usuarioActual=this.miRepositorioUsuario //primero busca el objecto a eliminar
                .findById(id)
                .orElse(null);
        if (usuarioActual!=null){
            this.miRepositorioUsuario.delete(usuarioActual); //se borra
        }
    }
    public String convertirSHA256(String password) { //toma el password y lo encripta
        MessageDigest md = null; //esta clase tinen un metodo estatico
        try {
            md = MessageDigest.getInstance("SHA-256"); //Medotdo estaico(Se puede utilizar sin necesidad de instacia)
        } //SHA-256 algorithmo de incriptacion
        catch (NoSuchAlgorithmException e) { //
            e.printStackTrace();
            return null;
        }
        byte[] hash = md.digest(password.getBytes()); //digest se le pasa al passowrord en la serializacion haciendolo en bytes digest lo encripta y devuelve bites
        StringBuffer sb = new StringBuffer(); //itera el arregle de bytes
        for(byte b : hash) {
            sb.append(String.format("%02x", b));//aplica un formato
        }
        return sb.toString(); //sale encriptado
    }
    /** JavaDoc
     * Relación (1 a n) entre rol y usuario
     * @param id
     * @param id_rol
     * @return
     */
    @PutMapping("{id}/rol/{id_rol}") //indica la notacion utilizando el metodo REST Put con su URL
    public Usuario asignarRolAUsuario(@PathVariable String id,@PathVariable String id_rol){ //Metodo que asigna el rol a un usuario
        Usuario usuarioActual = this.miRepositorioUsuario.findById(id).orElseThrow(RuntimeException::new); //busca al usuario por el id, y si no existe genera excepcion
        Rol rolActual = this.miRepositorioRol.findById(id_rol).orElseThrow(RuntimeException::new);//busca al rol por el id_roll, y si no existe genera excepcion
        usuarioActual.setRol(rolActual); //mezcla los ingredientes y los asocia setting the rol al usuario
        return this.miRepositorioUsuario.save(usuarioActual); //guarda este canbio
    }

    @PostMapping("/validate")
    public Usuario validate(@RequestBody Usuario infoUsuario, final HttpServletResponse response) throws IOException {
        Usuario usuarioActual=this.miRepositorioUsuario.getUserByEmail(infoUsuario.getCorreo());
        if (usuarioActual!=null && usuarioActual.getContrasena().equals(convertirSHA256(infoUsuario.getContrasena()))) {
            usuarioActual.setContrasena(""); //borrando la contraseña
            return usuarioActual;
        }else{
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return null;
        }
    }
}

