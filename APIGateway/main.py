from flask import Flask
from flask import jsonify
from flask import request
from flask_cors import CORS
import json
from waitress import serve
import datetime
import requests
import re
from flask_jwt_extended import create_access_token, verify_jwt_in_request
from flask_jwt_extended import get_jwt_identity
from flask_jwt_extended import jwt_required
from flask_jwt_extended import JWTManager

app=Flask(__name__)
cors = CORS(app)

app.config["JWT_SECRET_KEY"]="super-secret" #Cambiar por el que se conveniente
jwt = JWTManager(app) #crea un manager y le pasa la aplicacion para operar los tokens

@app.route("/login", methods=["POST"]) #punto de acceso al login
def create_token():
    data = request.get_json() #recupera el json guardado como un diccionario llamado data
    headers = {"Content-Type": "application/json; charset=utf-8"} #body del mensaje con diccionario llave vallor
    url=dataConfig["url-backend-security"]+'/usuarios/validate' #url de la funcionalidad
    response = requests.post(url, json=data, headers=headers) #peticion con url y el jason y headers
    if response.status_code == 200: #Si el orquestador da respuesta positiva
        user = response.json() #desempaco del. jaso tomo el body de la respuesta a un diccionario llamado user
        expires = datetime.timedelta(seconds=60 * 60*24) #tiempo de vida del token de un dia
        access_token = create_access_token(identity=user, expires_delta=expires) #crea un token de accesso
        return jsonify({"token": access_token, "user_id": user["_id"]}) #retorna el diccionario token y el user id
    else:
        return jsonify({"msg": "Bad username or password"}), 401


@app.before_request
def before_request_callback():
    endPoint=limpiarURL(request.path)
    excludedRoutes=["/login"]
    if excludedRoutes.__contains__(request.path):
        pass
    elif verify_jwt_in_request():
        usuario = get_jwt_identity()
        if usuario["rol"]is not None:
            tienePersmiso=validarPermiso(endPoint,request.method,usuario["rol"]["_id"])
            if not tienePersmiso:
                return jsonify({"message": "Permission denied"}), 401
        else:
            return jsonify({"message": "Permission denied"}), 401

def limpiarURL(url):
    partes = url.split("/")
    for laParte in partes:
        if re.search('\\d', laParte):
            url = url.replace(laParte, "?")
    return url

def validarPermiso(endPoint,metodo,idRol):
    url=dataConfig["url-backend-security"]+"/permisos-roles/validar-permiso/rol/"+str(idRol)
    tienePermiso=False
    headers = {"Content-Type": "application/json; charset=utf-8"}
    body={
        "url":endPoint,
        "metodo":metodo
    }
    response = requests.get(url,json=body, headers=headers)
    try:
        data=response.json()
        if("_id" in data):
            tienePermiso=True
    except:
        pass
    return tienePermiso

##################################################################################
#### Servico Votacion
# Rutas Ciudadanos

@app.route("/ciudadanos",methods=['GET'])
def getCiudadanos():
    headers = {"Content-Type": "application/json; charset=utf-8"}
    url = dataConfig["url-backend-votacion"] + '/ciudadanos'
    response = requests.get(url, headers=headers)
    json = response.json()
    return jsonify(json)

@app.route("/ciudadanos",methods=['POST'])
def crearCiudadano():
    data = request.get_json()
    headers = {"Content-Type": "application/json; charset=utf-8"}
    url = dataConfig["url-backend-votacion"] + '/ciudadanos'
    response = requests.post(url, headers=headers,json=data)
    json = response.json()
    return jsonify(json)

@app.route("/ciudadanos/<string:id>",methods=['GET'])
def getCiudadano(id):
    headers = {"Content-Type": "application/json; charset=utf-8"}
    url = dataConfig["url-backend-votacion"] + '/ciudadanos/'+id
    response = requests.get(url, headers=headers)
    json = response.json()
    return jsonify(json)

@app.route("/ciudadanos/<string:id>",methods=['PUT'])
def modificarCiudadano(id):
    data = request.get_json()
    headers = {"Content-Type": "application/json; charset=utf-8"}
    url = dataConfig["url-backend-votacion"] + '/ciudadanos/'+id
    response = requests.put(url, headers=headers, json=data)
    json = response.json()
    return jsonify(json)

@app.route("/ciudadanos/<string:id>",methods=['DELETE'])
def eliminarCiudadano(id):
    headers = {"Content-Type": "application/json; charset=utf-8"}
    url = dataConfig["url-backend-votacion"] + '/ciudadanos/' + id
    response = requests.delete(url, headers=headers)
    json = response.json()
    return jsonify(json)

##################################################################################
# Rutas Candidatos

@app.route("/candidatos",methods=['GET'])
def getCandidatos():
    headers = {"Content-Type": "application/json; charset=utf-8"}
    url = dataConfig["url-backend-votacion"] + '/candidatos'
    response = requests.get(url, headers=headers)
    json = response.json()
    return jsonify(json)

@app.route("/candidatos",methods=['POST'])
def crearCandidato():
    data = request.get_json()
    headers = {"Content-Type": "application/json; charset=utf-8"}
    url = dataConfig["url-backend-votacion"] + '/candidatos'
    response = requests.post(url, headers=headers,json=data)
    json = response.json()
    return jsonify(json)

@app.route("/candidatos/<string:id>",methods=['GET'])
def getCandidato(id):
    headers = {"Content-Type": "application/json; charset=utf-8"}
    url = dataConfig["url-backend-votacion"] + '/candidatos/'+id
    response = requests.get(url, headers=headers)
    json = response.json()
    return jsonify(json)

@app.route("/candidatos/<string:id>",methods=['PUT'])
def modificarCandidato(id):
    data = request.get_json()
    headers = {"Content-Type": "application/json; charset=utf-8"}
    url = dataConfig["url-backend-votacion"] + '/candidatos/'+id
    response = requests.put(url, headers=headers, json=data)
    json = response.json()
    return jsonify(json)

@app.route("/candidatos/<string:id>",methods=['DELETE'])
def eliminarCandidato(id):
    headers = {"Content-Type": "application/json; charset=utf-8"}
    url = dataConfig["url-backend-votacion"] + '/candidatos/' + id
    response = requests.delete(url, headers=headers)
    json = response.json()
    return jsonify(json)

##################################################################################
# Rutas Partidos

@app.route("/partidos",methods=['GET'])
def getPartidos():
    headers = {"Content-Type": "application/json; charset=utf-8"}
    url = dataConfig["url-backend-votacion"] + '/partidos'
    response = requests.get(url, headers=headers)
    json = response.json()
    return jsonify(json)

@app.route("/partidos",methods=['POST'])
def crearPartido():
    data = request.get_json()
    headers = {"Content-Type": "application/json; charset=utf-8"}
    url = dataConfig["url-backend-votacion"] + '/partidos'
    response = requests.post(url, headers=headers,json=data)
    json = response.json()
    return jsonify(json)

@app.route("/partidos/<string:id>",methods=['GET'])
def getPartido(id):
    headers = {"Content-Type": "application/json; charset=utf-8"}
    url = dataConfig["url-backend-votacion"] + '/partidos/'+id
    response = requests.get(url, headers=headers)
    json = response.json()
    return jsonify(json)

@app.route("/partidos/<string:id>",methods=['PUT'])
def modificarPartido(id):
    data = request.get_json()
    headers = {"Content-Type": "application/json; charset=utf-8"}
    url = dataConfig["url-backend-votacion"] + '/partidos/'+id
    response = requests.put(url, headers=headers, json=data)
    json = response.json()
    return jsonify(json)

@app.route("/partidos/<string:id>",methods=['DELETE'])
def eliminarPartido(id):
    headers = {"Content-Type": "application/json; charset=utf-8"}
    url = dataConfig["url-backend-votacion"] + '/partidos/' + id
    response = requests.delete(url, headers=headers)
    json = response.json()
    return jsonify(json)

##################################################################################
# Rutas Mesas

@app.route("/mesas",methods=['GET'])
def getMesas():
    headers = {"Content-Type": "application/json; charset=utf-8"}
    url = dataConfig["url-backend-votacion"] + '/mesas'
    response = requests.get(url, headers=headers)
    json = response.json()
    return jsonify(json)

@app.route("/mesas",methods=['POST'])
def crearMesas():
    data = request.get_json()
    headers = {"Content-Type": "application/json; charset=utf-8"}
    url = dataConfig["url-backend-votacion"] + '/mesas'
    response = requests.post(url, headers=headers,json=data)
    json = response.json()
    return jsonify(json)

@app.route("/mesas/<string:id>",methods=['GET'])
def getMesa(id):
    headers = {"Content-Type": "application/json; charset=utf-8"}
    url = dataConfig["url-backend-votacion"] + '/mesas/'+id
    response = requests.get(url, headers=headers)
    json = response.json()
    return jsonify(json)

@app.route("/mesas/<string:id>",methods=['PUT'])
def modificarMesa(id):
    data = request.get_json()
    headers = {"Content-Type": "application/json; charset=utf-8"}
    url = dataConfig["url-backend-votacion"] + '/mesas/'+id
    response = requests.put(url, headers=headers, json=data)
    json = response.json()
    return jsonify(json)

@app.route("/mesas/<string:id>",methods=['DELETE'])
def eliminarMesa(id):
    headers = {"Content-Type": "application/json; charset=utf-8"}
    url = dataConfig["url-backend-votacion"] + '/mesas/' + id
    response = requests.delete(url, headers=headers)
    json = response.json()
    return jsonify(json)

##################################################################################
### Servicio Seguridad
# Rutas Permisos Usuarios

@app.route("/usuarios",methods=['GET'])
def getUsuarios():
    headers = {"Content-Type": "application/json; charset=utf-8"}
    url = dataConfig["url-backend-security"] + '/usuarios'
    response = requests.get(url, headers=headers)
    json = response.json()
    return jsonify(json)

@app.route("/usuarios",methods=['POST'])
def crearUsuarios():
    data = request.get_json()
    headers = {"Content-Type": "application/json; charset=utf-8"}
    url = dataConfig["url-backend-security"] + '/usuarios'
    response = requests.post(url, headers=headers,json=data)
    json = response.json()
    return jsonify(json)

@app.route("/usuarios/<string:id>",methods=['GET'])
def getUsuario(id):
    headers = {"Content-Type": "application/json; charset=utf-8"}
    url = dataConfig["url-backend-security"] + '/usuarios/'+id
    response = requests.get(url, headers=headers)
    json = response.json()
    return jsonify(json)

@app.route("/usuarios/<string:id>",methods=['PUT'])
def modificarUsuario(id):
    data = request.get_json()
    headers = {"Content-Type": "application/json; charset=utf-8"}
    url = dataConfig["url-backend-security"] + '/usuarios/'+id
    response = requests.put(url, headers=headers, json=data)
    json = response.json()
    return jsonify(json)

@app.route("/usuarios/<string:id>",methods=['DELETE'])
def eliminarUsuario(id):
    headers = {"Content-Type": "application/json; charset=utf-8"}
    url = dataConfig["url-backend-security"] + '/usuarios/' + id
    response = requests.delete(url, headers=headers)
    json = response.json()
    return jsonify(json)

##################################################################################
# Otras rutas

@app.route("/",methods=['GET'])
def test():
    json = {}
    json["message"]="Server running ..."
    return jsonify(json)

def loadFileConfig():
    with open('config.json') as f:
        data = json.load(f)
    return data

if __name__=='__main__':
    dataConfig = loadFileConfig()
    print("Server running : "+"http://"+dataConfig["url-backend"]+":" + str(dataConfig["port"]))
    serve(app,host=dataConfig["url-backend"],port=dataConfig["port"])