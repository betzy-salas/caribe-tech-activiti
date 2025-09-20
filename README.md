# Activiti Runtime Setup

A continuación, se detallan los pasos para configurar y ejecutar el proyecto en tu entorno local.

## Pasos para configurar el entorno

### 1. Crear base de datos en tu ambiente local

Asegurate de seguir el paso a paso especificado en el archivo README.md del repositorio activiti-database

``` bash
git clone https://github.com/betzy-salas/activiti-database.git 
```

### 2. Clonar repositorio

Clonar el repositorio utilizando el comando 

``` bash
git clone https://github.com/betzy-salas/activiti.git 
```

### 3. Modificar los atributos de Base de Datos

En el archivo application.properties modificar las configuraciones de base de datos que sean necesarias

``` properties
spring.datasource.url=jdbc:postgresql://localhost:5432/<DATABASE_NAME>
spring.datasource.username=<DATABASE_USER>
spring.datasource.password=<DATABASE_PASSWORD>
```

### 4. Ejecutar el proyecto

Ejecute el proyecto en su ambiente local a fin de que se inicie el engine de activiti

### 5. Desplegar el proceso hello-process, move-rates y process-transfer-funds

En el repositorio encontrará un archivo llamado hello-process.bpmn20.xml. <br>
Este archivo corresponde con la descripción en formato XML del proceso.
Este proceso debe ser desplegado dentro del engine de activiti para que pueda ser posteriormente ejecutado.
Para  desplegar el proceso ejecute el siguiente curl (reemplace el puerto por el puerto de inicialización de su ambiente local)

``` bash
curl --location --request POST 'http://localhost:8080/api/deploy/hello-process'
```

### 6. Ejecutar el proceso hello-process

Una vez desplegado el proceso, ya puede iniciar la ejecución del proceso (reemplace el puerto por el puerto de inicialización de su ambiente local).

``` bash
curl --location 'http://localhost:8087/api/process/start/hello-process' \
--header 'Content-Type: application/json' \
--data '{"Hola":"hola"}'
```

``` bash
curl --location 'http://localhost:8087/api/process/start/process-transfer-funds' \
--header 'Content-Type: application/json' \
--data '{
    "sourceData": {
        "documentSourceType": "CC",
        "documentSourceNumber": "22632918",
        "accountSourceType": "CTE",
        "accountSourceNumber": "22631918"
    },
    "targetData": {
        "documentTargetType": "CC",
        "documentTargetNumber": "72274044",
        "accountTargetType": "AHO",
        "accountTargetNumber": "72274044",
        "amount": 15.00
    }
}'
```

