# esqueleto-servicio

Base para arrancar un microservicio desde cero

## Instrucciones

Clonar este repositorio y entrar en su directorio

```sh
git clone git@github.com:Senescyt/esqueleto-servicio.git
cd esqueleto-servicio
```

Copiar en la raíz el archivo `sniese.keystore`

```sh
cp ../servicio-usuario/sniese.keystore .
```

Ejecutar la siguiente tarea de `gradle` para crear un nuevo microservicio

```sh
./gradlew -b crear.gradle -Pnombre=<nombre microservicio> -Ppuerto=<puerto inicial> -Pkpass=<contraseña del keystore>
```

Por ejemplo

```sh
./gradlew -b crear.gradle -Pnombre=mascotas -Ppuerto=8080 -Pkpass=superSecreto
```

Donde:  
`<nombre microservicio>` es el nombre del microservicio sin el prefijo `servicio-`  
`<puerto inicial>` es el número de puerto HTTP inicial ([ver tabla en mingle][1])  
`<contraseña del keystore>` es la contraseña del archivo `sniese.keystore`

El ejemplo anterior creará un nuevo directorio en `../servicio-mascotas`, con
todo lo necesario para poder empezar a usarlo y desarrollarlo

```sh
cd ../servicio-mascotas
./gradlew idea
./gradlew run
```

[1]: https://sniese.mingle.thoughtworks.com/projects/sniese/wiki/Configuraci%C3%B3n_de_Puertos_para_Microservicios
